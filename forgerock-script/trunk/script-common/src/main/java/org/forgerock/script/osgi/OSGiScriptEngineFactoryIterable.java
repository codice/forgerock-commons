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

package org.forgerock.script.osgi;

import java.util.List;

import org.forgerock.script.engine.ScriptEngineFactory;
import org.forgerock.script.engine.ScriptEngineFactoryObserver;
import org.forgerock.script.source.SourceUnitObserver;
import org.ops4j.pax.swissbox.extender.BundleObserver;
import org.ops4j.pax.swissbox.extender.ManifestEntry;
import org.osgi.framework.Bundle;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class OSGiScriptEngineFactoryIterable implements BundleObserver<ManifestEntry> {

    private ScriptEngineFactoryObserver factoryObserver = null;
    private SourceUnitObserver unitObserver = null;

    public void addingEntries(Bundle bundle, List<ManifestEntry> entries) {
        ScriptEngineFactory factory = null;

        if (null != factoryObserver && null != factory) {
            factoryObserver.addingEntries(factory);
        }
    }

    public void removingEntries(Bundle bundle, List<ManifestEntry> entries) {
        ScriptEngineFactory factory = null;

        if (null != factoryObserver && null != factory) {
            factoryObserver.removingEntries(factory);
        }
    }

    public void setScriptEngineFactoryObserver(ScriptEngineFactoryObserver observer) {
        this.factoryObserver = observer;
    }

    public void setSourceUnitObserver(SourceUnitObserver observer) {
        this.unitObserver = observer;
    }
}
