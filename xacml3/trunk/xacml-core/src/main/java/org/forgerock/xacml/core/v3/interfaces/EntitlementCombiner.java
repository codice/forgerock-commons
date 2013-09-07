package org.forgerock.xacml.core.v3.interfaces;

import org.forgerock.xacml.core.v3.engine.XACML3Decision;

import java.util.Collection;
import java.util.List;

public interface EntitlementCombiner {
    public void add(XACML3Decision dec);
    public void addAll(Collection<XACML3Decision> c);
    public void addAll(EntitlementCombiner c);
    public List<XACML3Decision> getResult();

    public boolean isDone();
    public void register();

}

