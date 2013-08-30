package org.forgerock.xacml.core.v3.interfaces;

import java.util.HashMap;
import java.util.Map;

public class Entitlement  {
    public Map<String,Boolean> getActionValues() {
        return new HashMap<String, Boolean>();
    }

    public void setApplicationName(String applicationName) {
        return;
    }
}
