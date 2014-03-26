package org.forgerock.xacml.core.v3.interfaces;

import com.sun.identity.entitlement.xacml3.core.Policy;
import com.sun.identity.entitlement.xacml3.core.PolicySet;
import org.forgerock.xacml.core.v3.engine.XACML3Policy;
import org.forgerock.xacml.core.v3.engine.XACML3PolicyItem;
import org.forgerock.xacml.core.v3.engine.XACML3PolicySet;

import java.util.Set;

public interface PolicyStore {


    public String loadPolicySet(PolicySet ps);

    public PolicySet exportPolicySet(String name);
    public Policy exportPolicy(String name);


    public Set<String> listPolicySets();
    public Set<String> listPolicies();

    public XACML3PolicyItem getPolicyForEval(String name);


    public void savePolicySet(XACML3PolicySet pset, String id);
    public void savePolicy(XACML3Policy pol, String id);

    }
