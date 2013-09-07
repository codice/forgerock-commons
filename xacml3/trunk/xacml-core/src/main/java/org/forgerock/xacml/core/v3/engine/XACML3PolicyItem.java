package org.forgerock.xacml.core.v3.engine;

import com.sun.identity.entitlement.xacml3.core.PolicySet;
import org.forgerock.xacml.core.v3.interfaces.EntitlementCombiner;

import java.util.List;

public interface XACML3PolicyItem {
    public EntitlementCombiner evaluate(XACMLEvalContext pip);
}
