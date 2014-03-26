package org.forgerock.xacml.reference.combiner;

import org.forgerock.xacml.core.v3.ImplementationManagers.CombinerManager;
import org.forgerock.xacml.core.v3.engine.XACML3Decision;
import org.forgerock.xacml.core.v3.interfaces.EntitlementCombiner;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: allan
 * Date: 9/7/13
 * Time: 1:34 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CombinerBase implements EntitlementCombiner {
    protected boolean done = false;

    public void addAll(Collection<XACML3Decision> c) {
        for (XACML3Decision dec : c) {
            this.add(dec);
            if (isDone()) {
                return;
            }
        }
    }
    public void addAll(EntitlementCombiner c) {
        this.addAll(c.getResult());
    }

    public boolean isDone() {
        return done;
    }
    public void register(String type, String clazzName) {
        CombinerManager.registerHandler(type,clazzName);
    }

    }
