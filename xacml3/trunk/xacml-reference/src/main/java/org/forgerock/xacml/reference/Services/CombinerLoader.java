package org.forgerock.xacml.reference.Services;

import org.forgerock.xacml.core.v3.interfaces.EntitlementCombiner;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created with IntelliJ IDEA.
 * User: allan
 * Date: 9/7/13
 * Time: 2:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class CombinerLoader {
    static private CombinerLoader instance;

private Map<String,EntitlementCombiner> handlers;

    private  CombinerLoader() {
        handlers = new HashMap<String,EntitlementCombiner>();
        ServiceLoader<EntitlementCombiner> combiners = ServiceLoader.load(
                EntitlementCombiner.class);
        for (EntitlementCombiner p : combiners) {
            p.register();

        }
        }
    public static void startup() {
        if (instance == null) {
            instance = new CombinerLoader();

        }

    }
    }
