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
package org.forgerock.xacml.reference.evaluator;

import org.forgerock.xacml.core.v3.engine.XACML3Decision;
import org.forgerock.xacml.core.v3.engine.XACML3PolicyItem;
import org.forgerock.xacml.core.v3.engine.XACML3Response;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.interfaces.Evaluator;

import java.util.List;

public class EvaluatorImpl implements Evaluator {
    private String scope;

    public XACML3Response evaluate(XACMLEvalContext context,String policyName) {

        XACML3PolicyItem pSet = context.getPolicyForEval(scope);

        List<XACML3Decision> results = pSet.evaluate(context).getResult();

        XACML3Response resp = new XACML3Response();
        resp.addAll(results);

        //
        // Here is where we should do Combiner
        //
        //

        return resp;

    }
    public void setScope(String appName) {
        scope = appName;
    }


}
