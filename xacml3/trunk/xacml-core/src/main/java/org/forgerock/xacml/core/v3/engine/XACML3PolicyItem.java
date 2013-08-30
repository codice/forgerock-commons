package org.forgerock.xacml.core.v3.engine;

import com.sun.identity.entitlement.xacml3.core.PolicySet;

import java.util.List;

public interface XACML3PolicyItem {
    public List<XACML3Decision> evaluate(XACMLEvalContext pip);
}
