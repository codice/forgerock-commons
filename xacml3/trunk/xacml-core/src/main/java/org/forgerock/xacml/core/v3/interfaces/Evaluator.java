package org.forgerock.xacml.core.v3.interfaces;

import org.forgerock.xacml.core.v3.engine.XACML3Response;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;


public interface Evaluator {
    public XACML3Response evaluate(XACMLEvalContext context, String policyName);


}
