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

//import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.shared.debug.Debug;
import org.forgerock.xacml.core.v3.interfaces.Entitlement;
import com.sun.identity.entitlement.xacml3.core.*;
import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class XACML3Policy implements XACML3PolicyItem {
    private static Debug debug = Debug.getInstance("Xacml3");

    private Policy policy;

    private List<String> policySets;
    private String policyName;

    private FunctionArgument target;
    private List<XACML3PolicyRule> rules;

    private java.util.Map<String,FunctionArgument> definedVars;
    private String ruleCombiner;
    private Set<String> resourceSelectors;


    public XACML3Policy(Policy policy) {
        this.policy = policy;
        policySets = new ArrayList<String>();

        policyName = policy.getPolicyId();
        ruleCombiner = policy.getRuleCombiningAlgId();
        resourceSelectors = new HashSet<String>();

        target = XACML3PrivilegeUtils.getTargetFunction(policy.getTarget(),resourceSelectors);

        definedVars =  XACML3PrivilegeUtils.getVariableDefinitions(policy);

        rules =  XACML3PrivilegeUtils.getRules(policy,this);

    }

    public XACML3Policy() {
    }

    public FunctionArgument getDefinedVariable(String variableID){
        FunctionArgument retVal = definedVars.get(variableID);
        return retVal;
    }

    public String getPolicyName() {
        return policyName;
    }
    public void addParent(String parent) {
        policySets.add(parent);
    }
    public Policy getPolicy() {
        return policy;
    }
    public String getParent(int index) {
        return policySets.get(index);
    }
    public int getParentCount() {
        return policySets.size();
    }

    public Set<String> getResourceSelectors() {
         return resourceSelectors;
    }
    public void setResourceSelectors(Set<String> rSel) {
        resourceSelectors = rSel;
    }

    public List<XACML3Decision> evaluate(XACMLEvalContext pip) {

        boolean indeterminate = true;
        FunctionArgument evalResult;
        List<XACML3Decision> results = new ArrayList<XACML3Decision>();
        pip.setPolicyRef(this);
        try {
            evalResult = target.evaluate(pip);
        } catch (XACML3EntitlementException ex) {
            XACML3Decision result = new XACML3Decision(policyName,pip.getRequest().getContextID(),"Indeterminate");
            results.add(result);
            return results;
        }

        if (evalResult.isTrue())        {    // we  match,  so evaluate
            for (XACML3PolicyRule r : rules) {
                XACML3Decision decision = r.evaluate(pip);
                results.add(decision);
            }
        } else {
            XACML3Decision result = new XACML3Decision(policyName,pip.getRequest().getContextID(),"NotApplicable");
            results.add(result);
        }
        return results;
    }


    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("classname",this.getClass().getName());

        jo.put("policyName", policyName);
        jo.put("ruleCombiner", ruleCombiner);
        jo.put("target", target.toJSONObject() );


        Set<String> keys = definedVars.keySet();
        JSONObject dv = new JSONObject();

        for (String k: keys) {
            dv.put(k, definedVars.get(k).toJSONObject());
        }
        jo.put("definedVars",dv) ;

        for (XACML3PolicyRule r: rules) {
            jo.append("rules", r.toJSONObject());
        }

        return jo;
    }

    public void init(JSONObject jo) throws JSONException {
        policyName = jo.optString("policyName");
        ruleCombiner = jo.optString("ruleCombiner");

        target = FunctionArgument.getInstance(jo.getJSONObject("target"));
        definedVars = new HashMap<String, FunctionArgument>();

        JSONObject dv = jo.getJSONObject("definedVars");
        Iterator iter =  dv.keys();
        while (iter.hasNext()) {
            String s = (String)iter.next();
            definedVars.put(s,FunctionArgument.getInstance(dv.getJSONObject(s)));
        }

        rules = new ArrayList<XACML3PolicyRule>() ;

        JSONArray array = jo.getJSONArray("rules");
        for (int i = 0; i < array.length(); i++) {
            JSONObject json = (JSONObject)array.get(i);
            rules.add(XACML3PolicyRule.getInstance(json));
        }
    }

    static public XACML3Policy getInstance(JSONObject jo)  {
        String className = jo.optString("classname");
        try {
            Class clazz = Class.forName(className);
            XACML3Policy farg = (XACML3Policy)clazz.newInstance();
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
