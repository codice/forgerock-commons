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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.RootContext;
import org.forgerock.script.Script;
import org.forgerock.script.ScriptEntry;
import org.forgerock.script.ScriptName;
import org.forgerock.script.ScriptTest;
import org.forgerock.script.scope.Function;
import org.forgerock.script.scope.FunctionFactory;
import org.forgerock.script.scope.OperationParameter;
import org.forgerock.script.source.EmbeddedScriptSource;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.script.Bindings;
import javax.script.SimpleBindings;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
@Test
public class RhinoScriptTest extends ScriptTest {

    protected Map<String, Object> getConfiguration() {
        Map<String, Object> configuration = new HashMap<String, Object>(1);
        // configuration.put(RhinoScriptEngine.CONFIG_DEBUG_PROPERTY,
        // "transport=socket,suspend=y,address=9888,trace=true");
        return configuration;
    }

    protected String getLanguageName() {
        return RhinoScriptEngineFactory.LANGUAGE_NAME;
    }

    protected URL getScriptContainer(String name) {
        return RhinoScriptTest.class.getResource(name);
    }

    protected EmbeddedScriptSource getScriptSourceWithException() {
        ScriptName scriptName = new ScriptName("exception", "javascript");
        return new EmbeddedScriptSource("throw \"Access denied\";", scriptName);
    }

    //@Test
    /*public void compileTest() throws Exception {
        Bindings b= new SimpleBindings();
        b.put("router", FunctionFactory.getResource());

        b = getScriptRegistry().getEngineByName(RhinoScriptEngineFactory.LANGUAGE_NAME).compileBindings(new RootContext(), b);

        Object o = ((Map)b.get("router")).get("read");
        o.getClass();
    }*/

}
