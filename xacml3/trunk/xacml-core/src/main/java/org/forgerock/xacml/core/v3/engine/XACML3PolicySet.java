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
package org.forgerock.xacml.core.v3.engine;

import com.sun.identity.entitlement.xacml3.core.*;
import com.sun.identity.shared.debug.Debug;
import org.forgerock.xacml.core.v3.ImplementationManagers.CombinerManager;
import org.forgerock.xacml.core.v3.interfaces.EntitlementCombiner;
import org.forgerock.xacml.core.v3.interfaces.PolicyStore;
import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class XACML3PolicySet implements XACML3PolicyItem{

    private static Debug debug = Debug.getInstance("Xacml3");

    private String                  policySetName;
    private String                  version;
    private List<Object>            policyIssuer;
    private FunctionArgument        target;
    private List<String>            items;
    private List<XACML3Obligation>  obligations;
    private List<XACML3Advice>      advices;
    private String                  combiner;

    private XACML3PolicySet(PolicySet policySet) {

        policySetName = policySet.getPolicySetId();
        combiner = policySet.getPolicyCombiningAlgId();
        version = policySet.getVersion().getValue();
        if (policySet.getPolicyIssuer() != null )  {
            policyIssuer = policySet.getPolicyIssuer().getContent().getContent();
        } else {
            policyIssuer = new ArrayList<Object>();
        }
        obligations = new ArrayList<XACML3Obligation>();
        advices = new ArrayList<XACML3Advice>();
        items = new ArrayList<String>();


        try {
            for (ObligationExpression o : policySet.getObligationExpressions().getObligationExpression()) {
                obligations.add(new XACML3Obligation(o));
            }
        } catch (Exception ex) {

        }
        try {
            for (AdviceExpression a : policySet.getAdviceExpressions().getAdviceExpression()) {
                advices.add(new XACML3Advice(a));
            }
        } catch (Exception ex) {

        }

        target = XACML3PrivilegeUtils.getTargetFunction(policySet.getTarget(),new HashSet());


    }
    private   XACML3PolicySet() {

    }

    public String getPolicySetName() {
        return  policySetName;
    }

    public void resolveChildren(PolicySet policySet, PolicyStore pstore) {

        List<JAXBElement<?>> obList = policySet.getPolicySetOrPolicyOrPolicySetIdReference();
        for (JAXBElement ob : obList) {
            if (ob.getDeclaredType().equals(Policy.class)) {
                XACML3Policy xpol = new XACML3Policy((Policy)ob.getValue());
                xpol.addParent(policySetName);
                pstore.savePolicy(xpol, xpol.getPolicyName());
                items.add(xpol.getPolicyName());
            }
            if (ob.getDeclaredType().equals( PolicySet.class)) {
                items.add(parsePolicySet((PolicySet) ob.getValue(), pstore));
            }
            if (ob.getDeclaredType().equals( IdReference.class)  ) {
                IdReference idr =  (IdReference)ob.getValue();
                items.add( idr.getValue());
            }

        }
    }

    /*
        Given a Xacml3PolicySet return a newly formed JAXB PolicySet
     */
    public XACMLRootElement getXACMLRoot() {

        PolicySet   policySet = new PolicySet();
        ObjectFactory objectFactory = new ObjectFactory();

        policySet.setPolicySetId(policySetName);
        policySet.setPolicyCombiningAlgId(combiner);
        Version v =  new Version();
        v.setValue(version);
        policySet.setVersion(v);
        PolicyIssuer p = new PolicyIssuer();
        Content c = new Content();
        c.getContent().addAll(policyIssuer);
        p.setContent(c);
        policySet.setPolicyIssuer(p);

        try {
            List<ObligationExpression> obs = policySet.getObligationExpressions().getObligationExpression();
            for (XACML3Obligation o : obligations) {
                obs.add(o.getObligationExpression());
            }
        } catch (Exception ex) {

        }
        try {
            List<AdviceExpression> obs = policySet.getAdviceExpressions().getAdviceExpression();
            for (XACML3Advice o : advices) {
                obs.add(o.getAdviceExpression());
            }
        } catch (Exception ex) {

        }
        PolicyIdentifierList pList = new PolicyIdentifierList();

        for (String item : items) {
            IdReference pID = new IdReference();
            pID.setValue(item);
            pList.getPolicyIdReferenceOrPolicySetIdReference().add(objectFactory.createPolicyIdReference(pID));
        }
        policySet
            .getPolicySetOrPolicyOrPolicySetIdReference()
            .add(objectFactory.createPolicyIdentifierList(pList));

        policySet.setTarget((Target)target.getXACMLRoot());

        return policySet;
    }
    public JAXBElement<?> getXACML() {

        PolicySet policySet =  (PolicySet) getXACMLRoot();

        ObjectFactory objectFactory = new ObjectFactory();

        return objectFactory.createPolicySet(policySet);

    }

        public static String parsePolicySet(PolicySet policySet, PolicyStore pstore) {

        XACML3PolicySet pset = new XACML3PolicySet(policySet);

        pset.resolveChildren(policySet, pstore);
        pstore.savePolicySet(pset,pset.policySetName);
        return pset.getPolicySetName();
    }

    public EntitlementCombiner evaluate(XACMLEvalContext pip) {

        boolean indeterminate = true;
        FunctionArgument.indent = 2;

        FunctionArgument evalResult;
        EntitlementCombiner  results = CombinerManager.getInstance(combiner);
        System.out.println("Evaluating PolicySet "+policySetName);

            try {
                evalResult = target.evaluate(pip);
            } catch (XACML3EntitlementException ex) {
                XACML3Decision result = new XACML3Decision(policySetName,pip.getRequest().getContextID(),"Indeterminate");
                results.add(result);
                return results;
            }

        if (evalResult.isTrue())        {    // we  match,  so evaluate
            System.out.println("Target true, evaluating Policies ");

            for (String s : items) {
                XACML3PolicyItem pSet = pip.getPolicyForEval(s);
                results.addAll(pSet.evaluate(pip));
            }
        } else {
            XACML3Decision result = new XACML3Decision(policySetName,pip.getRequest().getContextID(),"NotApplicable");
            results.add(result);
        }
        System.out.println("Evaluating PolicySet "+policySetName +" Completed");
        return results;
    }

    public String asJSONExpression() {
        String retVal = policySetName + "\n\n";

        retVal = retVal + target.asJSONExpression() + "\n\n";

        return retVal;

    }
    public String asRPNExpression() {
        String retVal = policySetName + "\n\n";

        retVal = retVal + target.asRPNExpression() + "\n\n";

        return retVal;

    }



    public JSONObject toJSONObject() throws JSONException {

        JSONObject jo = new JSONObject();
        jo.put("classname", this.getClass().getName());

        jo.put("policySetName", policySetName);
        jo.put("version", version);
        jo.put("combiner", combiner) ;
        jo.put("policyIssuer", policyIssuer);
        jo.put("target", target.toJSONObject());

        for (String item: items) {
            jo.append("elements", item);
        }
        for (XACML3Obligation obl: obligations) {
            jo.append("obligations", obl.toJSONObject());
        }
        for (XACML3Advice adv: advices) {
            jo.append("advices", adv.toJSONObject());
        }

        return jo;
    }

    public void init(JSONObject jo) throws JSONException {

        policySetName = jo.optString("policySetName");
        version       = jo.optString("version");
        combiner      = jo.optString("combiner");
        JSONArray issuer = jo.optJSONArray("policyIssuer");

        policyIssuer = new ArrayList<Object>();
        for (int i=0; i<issuer.length(); i++) {
            policyIssuer.add( issuer.getString(i) );
        }

        target = FunctionArgument.getInstance(jo.getJSONObject("target"));

        items = new ArrayList<String>();

        JSONArray e_array = jo.optJSONArray("elements");
        if (e_array != null) {
            for (int i = 0; i < e_array.length(); i++) {
                String json = (String)e_array.get(i);
                items.add(json);
            }
        }

        obligations = new ArrayList<XACML3Obligation>() ;

        JSONArray o_array = jo.optJSONArray("obligations");
        if (o_array != null) {
                for (int i = 0; i < o_array.length(); i++) {
                JSONObject json = (JSONObject)o_array.get(i);
                obligations.add(XACML3Obligation.getInstance(json));
            }
        }

        advices = new ArrayList<XACML3Advice>() ;

        JSONArray a_array = jo.optJSONArray("advices");
        if (a_array != null) {
            for (int i = 0; i < a_array.length(); i++) {
                JSONObject json = (JSONObject)a_array.get(i);
                advices.add(XACML3Advice.getInstance(json));
            }
        }
    }

    static public XACML3PolicySet getInstance(JSONObject jo)  {
        String className = jo.optString("classname");
        try {
            Class clazz = Class.forName(className);
            XACML3PolicySet farg = (XACML3PolicySet)clazz.newInstance();
            farg.init(jo);

            return farg;
        } catch (InstantiationException ex) {
            debug.error("FunctionArgument.getInstance", ex);
        } catch (IllegalAccessException ex) {
            debug.error("FunctionArgument.getInstance", ex);
        } catch (ClassNotFoundException ex) {
            debug.error("FunctionArgument.getInstance", ex);
        } catch (JSONException ex) {
            debug.error("FunctionArgument.getInstance", ex);
        }
        return null;
    }
}
