/**
 *
 ~ DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 ~
 ~ Copyright (c) 2011-2013 ForgeRock AS. All Rights Reserved
 ~
 ~ The contents of this file are subject to the terms
 ~ of the Common Development and Distribution License
 ~ (the License). You may not use this file except in
 ~ compliance with the License.
 ~
 ~ You can obtain a copy of the License at
 ~ http://forgerock.org/license/CDDLv1.0.html
 ~ See the License for the specific language governing
 ~ permission and limitations under the License.
 ~
 ~ When distributing Covered Code, include this CDDL
 ~ Header Notice in each file and include the License file
 ~ at http://forgerock.org/license/CDDLv1.0.html
 ~ If applicable, add the following below the CDDL Header,
 ~ with the fields enclosed by brackets [] replaced by
 ~ your own identifying information:
 ~ "Portions Copyrighted [year] [name of copyright owner]"
 *
 */
package org.forgerock.xacml.reference.storage;


/*
 *  This implements a way to load and dump policies from an in memory store.
 */

import com.mongodb.*;
import com.sun.identity.entitlement.xacml3.core.Policy;
import com.sun.identity.entitlement.xacml3.core.PolicySet;
import org.forgerock.xacml.core.v3.engine.XACML3Policy;
import org.forgerock.xacml.core.v3.engine.XACML3PolicyItem;
import org.forgerock.xacml.core.v3.engine.XACML3PolicySet;
import org.forgerock.xacml.core.v3.interfaces.PolicyStore;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class PolicyStoreImpl implements PolicyStore {


    DB policyDB;

    public PolicyStoreImpl() {

        try {
        Mongo mongo = new Mongo( "localhost" , 27017 );

        policyDB = mongo.getDB("policydatabase");
        }    catch (Exception ex ) {
            System.out.println("Unable to open MongoDB");

        }
    }

    protected   BasicDBObject findPolicy(String coll, String value, String name) {
        DBCollection table = policyDB.getCollection(coll);
        BasicDBObject retVal = null;

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put(value, name);

        DBCursor cursor = table.find(searchQuery);

        if (cursor.hasNext())   {
            retVal = (BasicDBObject)cursor.next();
        }
        return retVal;
    }
    protected   DBCursor findPolicies(String coll, String value, String name) {
        DBCollection table = policyDB.getCollection(coll);

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put(value, name);

        DBCursor cursor = table.find(searchQuery);

        return cursor;
    }

    public String loadPolicySet(PolicySet ps) {
        return XACML3PolicySet.parsePolicySet(ps, this);
    }

    public PolicySet exportPolicySet(String name) {

        BasicDBObject obj = findPolicy( "policySets", "policySetID", name);
        XACML3PolicyItem item ;
        PolicySet retVal = null;

        try {
            String pol = (String)obj.get("policySetJSON");
            JSONObject polJ = new JSONObject(pol) ;
            item =  XACML3PolicySet.getInstance(polJ);
            retVal = (PolicySet)item.getXACMLRoot();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to export PolicySet");
        }
        return retVal;

    }
    public Policy exportPolicy(String name) {

        BasicDBObject obj = findPolicy( "policies", "policyID", name);
        XACML3PolicyItem item = null;
        Policy retVal = null;

        try {
            String pol = (String)obj.get("policyJSON");
            JSONObject polJ = new JSONObject(pol) ;

            item =  XACML3Policy.getInstance(polJ);
            retVal =  (Policy)item.getXACMLRoot();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Unable to export Policy");
        }
        return retVal;

    }

    public Set<String> listPolicySets() {
        DBCollection table = policyDB.getCollection("policySets");
        Set<String> retVal = new HashSet<String>();

        DBCursor cursor = table.find();

        while (cursor.hasNext())   {
            BasicDBObject obj = (BasicDBObject)cursor.next();
            retVal.add((String)obj.get("policySetID"));
        }
        return retVal;
    }

    public void savePolicySet(XACML3PolicySet pset, String id) {
        DBCollection table = policyDB.getCollection("policySets");
        BasicDBObject document = findPolicy( "policySets", "policySetID", id);

        if (document == null) {
             document = new BasicDBObject();
        }
        try {
            document.put("policySetID",id);
            document.put("policySetJSON", pset.toJSONObject().toString());
            table.save(document);
        } catch (Exception ex) {
            System.out.print(ex);
            ex.printStackTrace();
        }

    }

    public Set<String> listPolicies() {
        DBCollection table = policyDB.getCollection("policies");
        Set<String> retVal = new HashSet<String>();

        DBCursor cursor = table.find();

        while (cursor.hasNext())   {
            BasicDBObject obj = (BasicDBObject)cursor.next();
            retVal.add((String)obj.get("policyID"));
        }
        return retVal;
    }

    public void savePolicy(XACML3Policy pol, String id) {

        DBCollection table = policyDB.getCollection("policies");
        BasicDBObject document = findPolicy( "policies", "policyID", id);

        if (document == null) {
             document = new BasicDBObject();
        }

        try {
            document.put("policyID",id);
            document.put("policyJSON", pol.toJSONObject().toString());
            table.save(document);
        } catch (Exception ex )  {

        }
    }

    public XACML3Policy getPolicyForEval(String name) {
        BasicDBObject obj = findPolicy("policies", "policyID", name);
        XACML3Policy retVal = null;

        try {
        if (obj != null)   {

            String pol = (String)obj.get("policyJSON");
            JSONObject polJ = new JSONObject(pol) ;

            retVal = XACML3Policy.getInstance(polJ);
        }
        } catch (Exception ex) {

        }
        return retVal;
    }
}
