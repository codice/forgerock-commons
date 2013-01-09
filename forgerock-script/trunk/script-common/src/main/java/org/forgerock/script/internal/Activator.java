/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */

package org.forgerock.script.internal;

import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import org.forgerock.script.ScriptRegistry;
import org.forgerock.script.osgi.OSGiScriptEngineFactoryIterable;
import org.forgerock.script.osgi.ScriptEngineManifestScanner;
import org.ops4j.pax.swissbox.extender.BundleObserver;
import org.ops4j.pax.swissbox.extender.BundleURLScanner;
import org.ops4j.pax.swissbox.extender.BundleWatcher;
import org.ops4j.pax.swissbox.extender.ManifestEntry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * A Activator is an example. SCR has to do it.
 * 
 * @author Laszlo Hordos
 */
@Deprecated
public class Activator implements BundleActivator {

    /**
     * Bundle watcher of ConnectorBundle-.
     */
    private BundleWatcher<ManifestEntry> manifestWatcher;
    private BundleWatcher<URL> metainfWatcher;

    private ServiceRegistration scriptLibrary = null;

    public void start(BundleContext context) throws Exception {
        // OSGi alternatives to the ServiceLoader
//        OSGiScriptEngineFactoryIterable iterable = new OSGiScriptEngineFactoryIterable();
//        ScriptEngineManager manager = null;//new ScriptEngineManager(null, null, iterable);
//
//        iterable.setScriptEngineFactoryObserver(manager.getScriptEngineFactoryObserver());
//        iterable.setSourceUnitObserver(manager.getSourceUnitObserver());
//
//        manifestWatcher =
//                new BundleWatcher<ManifestEntry>(context, new ScriptEngineManifestScanner(),
//                        iterable);
//        manifestWatcher.start();
//
//        metainfWatcher =
//                new BundleWatcher<URL>(context, new BundleURLScanner("META-INF/services/",
//                        "org.forgerock.script.engine.ScriptEngineFactory", false // do
//                        // recursive
//                        ), new BundleObserver<URL>() {
//                            public void addingEntries(Bundle bundle, List<URL> entries) {
//                            }
//
//                            public void removingEntries(Bundle bundle, List<URL> entries) {
//                            }
//                        });
//        metainfWatcher.start();
//
//        Hashtable<String, String> properties = new Hashtable<String, String>();
//        scriptLibrary =
//                context.registerService(ScriptRegistry.class.getName(), manager.getScriptLibrary(),
//                        properties);

    }

    public void stop(BundleContext context) throws Exception {
        if (null != scriptLibrary) {
            scriptLibrary.unregister();
            scriptLibrary = null;
        }

        // Stop the bundle watcher.
        // This will result in un-publish of each web application that was
        // registered during the lifetime of
        // bundle watcher.
        if (manifestWatcher != null) {
            manifestWatcher.stop();
            manifestWatcher = null;
        }
    }
}
