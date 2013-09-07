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
import org.forgerock.xacml.core.v3.ImplementationManagers.AttributeResolverManager;
import org.forgerock.xacml.core.v3.ImplementationManagers.EvaluatorManager;
import org.forgerock.xacml.core.v3.ImplementationManagers.PolicyStoreManager;
import org.forgerock.xacml.core.v3.interfaces.Evaluator;
import org.forgerock.xacml.core.v3.interfaces.PolicyStore;
import org.forgerock.xacml.core.v3.model.DataDesignator;
import org.forgerock.xacml.core.v3.model.FunctionArgument;

public class XACMLEvalContext  {

    private PolicyStore store =null;
    private XACML3Request requestContext;
    private Response response;
    private XACML3Policy policyRef;

    public XACMLEvalContext() {
    }

    public void setStore(PolicyStore store) {
        this.store = store;
    }

    public FunctionArgument getDefinedVariable(String variableID){
         return policyRef.getDefinedVariable(variableID);
    }
    public void setReponse(Response response) {
        this.response = response;
    }
    public Response getResponse() {
        return response;
    }
    public void setRequest(XACML3Request request) {
        this.requestContext = request;
    }
    public XACML3Request getRequest() {
        return requestContext;
    }
    public void setPolicyRef(XACML3Policy polRef) {
        this.policyRef = polRef;
    }

    public FunctionArgument resolve(DataDesignator designator) throws XACML3EntitlementException {

        return AttributeResolverManager.getInstance().resolve(designator,requestContext);
    }
    public XACML3PolicyItem getPolicyForEval(String name) {
        return store.getPolicyForEval(name);
    }

    public void setResult(XACML3Decision decision)  {
        Result r = new Result();
        r.setStatus(decision.getStatus());
        r.setDecision(decision.getDecision());
        response.getResult().add(r);
        /*
        Obligations obs = new Obligations();
        List<Obligation> ob = obs.getObligation();
        Obligation newOb = new Obligation();
        newOb.
        ob.addAll(decision.getObligations());

        r.setObligations(decision.getObligations());

          */

    }
    public static Response XACMLEvaluate(Request request, String appname) {
        XACML3Request xReq = new  XACML3Request(request);
        XACMLEvalContext eContext =  new XACMLEvalContext();
        eContext.setRequest(xReq);
        eContext.setStore(PolicyStoreManager.getInstance());

        XACML3Response response = null;

        try {
            Evaluator eval = EvaluatorManager.newInstance();
            eval.setScope(appname);

            response = eval.evaluate(eContext);

        } catch (Exception ex) {

        }
        return response.asResponse(eContext);
    }

}
