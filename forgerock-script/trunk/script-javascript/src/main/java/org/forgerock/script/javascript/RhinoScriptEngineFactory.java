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

package org.forgerock.script.javascript;

import org.forgerock.json.resource.PersistenceConfig;
import org.forgerock.script.engine.ScriptEngine;
import org.forgerock.script.engine.ScriptEngineFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A NAME does ...
 *
 * @author Laszlo Hordos
 */
public class RhinoScriptEngineFactory implements ScriptEngineFactory {

    public static final String VERSION = "1.7 release 4";

    public static final String LANGUAGE_NAME = "ECMAScript";

    private static List<String> names;
    private static List<String> mimeTypes;
    private static List<String> extensions;

    private RhinoScriptEngine engine = null;

    static {
        names = new ArrayList(6);
        names.add("js");
        names.add("rhino");
        names.add("JavaScript");
        names.add("javascript");
        names.add("ECMAScript");
        names.add("ecmascript");
        names = Collections.unmodifiableList(names);

        mimeTypes = new ArrayList(4);
        mimeTypes.add("application/javascript");
        mimeTypes.add("application/ecmascript");
        mimeTypes.add("text/javascript");
        mimeTypes.add("text/ecmascript");
        mimeTypes = Collections.unmodifiableList(mimeTypes);

        extensions = new ArrayList(1);
        extensions.add("js");
        extensions = Collections.unmodifiableList(extensions);
    }

    public String getEngineName() {
        return "javascript";
    }

    public String getEngineVersion() {
        return VERSION;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    public List<String> getNames() {
        return names;
    }

    public String getLanguageName() {
        return LANGUAGE_NAME;
    }

    public String getLanguageVersion() {
        return "1.8";
    }

    public ScriptEngine getScriptEngine(
            final AtomicReference<PersistenceConfig> persistenceConfigReference,
            Map<String, Object> configuration) {
        if (null == engine) {
            synchronized (this) {
                if (null == engine) {
                    engine = new RhinoScriptEngine(configuration, this);
                    engine.setPersistenceConfig(persistenceConfigReference);
                }
            }
        }
        return engine;
    }
}
