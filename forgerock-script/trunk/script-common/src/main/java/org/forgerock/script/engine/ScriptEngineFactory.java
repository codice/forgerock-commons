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

package org.forgerock.script.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.forgerock.json.resource.PersistenceConfig;

/**
 * {@code ScriptEngineFactory} is used to describe and instantiate
 * {@code ScriptEngines}. <br>
 * <br>
 * Each class implementing {@code ScriptEngine} has a corresponding factory that
 * exposes metadata describing the engine class. <br>
 * <br>
 * The {@code ScriptEngineManager} uses the service provider mechanism described
 * in the <i>Jar File Specification</i> to obtain instances of all
 * {@code ScriptEngineFactories} available in the current ClassLoader.
 * 
 */
public interface ScriptEngineFactory {
    /**
     * Returns the full name of the {@code ScriptEngine}. For instance an
     * implementation based on the Mozilla Rhino Javascript engine might return
     * <i>Rhino Mozilla Javascript Engine</i>.
     * 
     * @return The name of the engine implementation.
     */
    public String getEngineName();

    /**
     * Returns the version of the {@code ScriptEngine}.
     * 
     * @return The {@code ScriptEngine} implementation version.
     */
    public String getEngineVersion();

    /**
     * Returns an immutable list of filename extensions, which generally
     * identify scripts written in the language supported by this
     * {@code ScriptEngine}. The array is used by the
     * {@code ScriptEngineManager} to implement its {@code getEngineByExtension}
     * method.
     * 
     * @return The list of extensions.
     */
    public List<String> getExtensions();

    /**
     * Returns an immutable list of mimetypes, associated with scripts that can
     * be executed by the engine. The list is used by the
     * {@code ScriptEngineManager} class to implement its
     * {@code getEngineByMimetype} method.
     * 
     * @return The list of mime types.
     */
    public List<String> getMimeTypes();

    /**
     * Returns an immutable list of short names for the {@code ScriptEngine},
     * which may be used to identify the {@code ScriptEngine} by the
     * {@code ScriptEngineManager}. For instance, an implementation based on the
     * Mozilla Rhino Javascript engine might return list containing
     * {&quot;javascript&quot;, &quot;rhino&quot;}.
     */
    public List<String> getNames();

    /**
     * Returns the name of the scripting langauge supported by this
     * {@code ScriptEngine}.
     * 
     * @return The name of the supported language.
     */
    public String getLanguageName();

    /**
     * Returns the version of the scripting language supported by this
     * {@code ScriptEngine}.
     * 
     * @return The version of the supported language.
     */
    public String getLanguageVersion();

    /**
     * Returns an instance of the {@code ScriptEngine} associated with this
     * {@code ScriptEngineFactory}. A new ScriptEngine is generally returned,
     * but implementations may pool, share or reuse engines.
     * 
     * @return A new {@code ScriptEngine} instance.
     */
    public ScriptEngine getScriptEngine(AtomicReference<PersistenceConfig> connectionFactory,
            Map<String, Object> configuration);

}
