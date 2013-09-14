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
import org.forgerock.xacml.core.v3.model.XACMLFunction;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class XACML3PolicySet implements XACML3PolicyItem{

    private static Debug debug = Debug.getInstance("Xacml3");

    private PolicySet policySet;
    private String policySetName;

    private Version version;
    private PolicyIssuer policyIssuer;

    private FunctionArgument target;

    private List<String> items;

    private List<XACML3Obligation> obligations;
    private List<XACML3Advice> advices;

    private String combiner;

    private XACML3PolicySet(PolicySet policySet) {

        this.policySet = policySet;
        policySetName = policySet.getPolicySetId();
        combiner = policySet.getPolicyCombiningAlgId();
        version = policySet.getVersion();
        policyIssuer = policySet.getPolicyIssuer();

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

    public String getPolicySetName() {
        return  policySetName;
    }
    public PolicySet getPolicySet() {
        return policySet;
    }

    public void resolveChildren(PolicySet policySet, PolicyStore pstore) {
        // first save this policySet
        pstore.savePolicySet(this,policySetName);

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

    public static String parsePolicySet(PolicySet policySet, PolicyStore pstore) {

        XACML3PolicySet pset = new XACML3PolicySet(policySet);

        pset.resolveChildren(policySet, pstore);
        return pset.getPolicySetName();
    }

    public EntitlementCombiner evaluate(XACMLEvalContext pip) {

        boolean indeterminate = true;
        XACMLFunction.indent = 0;

        FunctionArgument evalResult;
        EntitlementCombiner  results = CombinerManager.getInstance(combiner);

            try {
                evalResult = target.evaluate(pip);
            } catch (XACML3EntitlementException ex) {
                XACML3Decision result = new XACML3Decision(policySetName,pip.getRequest().getContextID(),"Indeterminate");
                results.add(result);
                return results;
            }

        if (evalResult.isTrue())        {    // we  match,  so evaluate
            for (String s : items) {
                XACML3PolicyItem pSet = pip.getPolicyForEval(s);
                results.addAll(pSet.evaluate(pip));
            }
        } else {
            XACML3Decision result = new XACML3Decision(policySetName,pip.getRequest().getContextID(),"NotApplicable");
            results.add(result);
        }
        return results;
    }


}
