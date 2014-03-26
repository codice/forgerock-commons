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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XACML3Response {
    private List<XACML3Decision> decisions;

    public XACML3Response() {
        decisions = new ArrayList<XACML3Decision>();
    }

    public void add(XACML3Decision desc) {
        decisions.add(desc);
    }
    public void addAll(Collection<? extends XACML3Decision> descs ) {
        decisions.addAll(descs);
    }

    public Response asResponse(XACMLEvalContext pip) {
        Response theResponse = new Response();

        for (XACML3Decision xd : decisions) {
            Result r = new Result();

            Status stat = new Status();
            StatusCode sc = new StatusCode();
            sc.setValue(XACML3Decision.OK);
            stat.setStatusCode(sc);
            r.setStatus(stat);

            r.setDecision(xd.getDecision());

            PolicyIdentifierList pd = new PolicyIdentifierList();
            ObjectFactory objectFactory = new ObjectFactory();
            IdReference id = new IdReference();
            id.setValue(xd.getRuleID());
            pd.getPolicyIdReferenceOrPolicySetIdReference().add(objectFactory.createPolicyIdReference(id));

            r.setPolicyIdentifierList(pd);

            r.setAssociatedAdvice(getReturnAdvice(xd.getAdvices(),pip));
            r.setObligations(getReturnObligations(xd.getObligations(),pip));

            theResponse.getResult().add(r);

        }
        return theResponse;
    }


    public AssociatedAdvice getReturnAdvice(List<XACML3Advice> adviceList, XACMLEvalContext pip) {
        AssociatedAdvice result = new AssociatedAdvice();

        for (XACML3Advice ad : adviceList) {
            result.getAdvice().add(ad.getAdvice(pip));
        }
        return result;
    }

    public Obligations getReturnObligations(List<XACML3Obligation> obligations,XACMLEvalContext pip) {
        Obligations result = new Obligations();

        for (XACML3Obligation ob : obligations) {
            result.getObligation().add(ob.getObligation(pip));
        }
        return result;
    }
}
