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
package org.forgerock.xacml.reference.storage;


/*
 *  This implements a way to load and dump policies from an in memory store.
 */

import com.sun.identity.entitlement.xacml3.core.PolicySet;
import org.forgerock.xacml.core.v3.engine.XACML3Policy;
import org.forgerock.xacml.core.v3.engine.XACML3PolicyItem;
import org.forgerock.xacml.core.v3.engine.XACML3PolicySet;
import org.forgerock.xacml.core.v3.interfaces.PolicyStore;

import java.util.*;

public class PolicyStoreImpl implements PolicyStore {


    Map<String,XACML3PolicyItem> policies;


    public PolicyStoreImpl() {
        policies = new HashMap<String,XACML3PolicyItem>();
    }

    public String loadPolicySet(PolicySet ps) {
        return XACML3PolicySet.parsePolicySet(ps, this);
    }

    public PolicySet exportPolicySet(String name) {
        XACML3PolicyItem pset = policies.get(name);
        PolicySet result = null;

        if (pset instanceof XACML3Policy) {
            String parent = ((XACML3Policy)pset).getParent(0);
            result =  ((XACML3PolicySet)policies.get(name)).getPolicySet();
        }
        if (pset instanceof XACML3PolicySet) {
             result =  ((XACML3PolicySet)pset).getPolicySet();
        }
        return result;
    }

    public Set<String> listPolicySets() {
        return policies.keySet();
    }

    public void savePolicySet(XACML3PolicySet pset, String id) {
        policies.put(id,pset);

    }
    public void savePolicy(XACML3Policy pol, String id) {
        policies.put(id,pol);
    }

    public XACML3PolicyItem getPolicyForEval(String name) {
        XACML3PolicyItem it = policies.get(name);
        return it;
    }
}
