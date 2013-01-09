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

package org.forgerock.script;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.Resources;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.Resource;
import org.forgerock.json.resource.ResultHandler;
import org.forgerock.json.resource.RequestHandler;
import org.forgerock.json.resource.ServerContext;
import org.forgerock.script.engine.ScriptEngineFactory;
import org.forgerock.script.exception.ScriptThrownException;
import org.forgerock.script.registry.ScriptRegistryImpl;
import org.forgerock.script.scope.ConnectionFunction;
import org.forgerock.script.source.DirectoryContainer;
import org.forgerock.script.source.EmbeddedScriptSource;
import org.forgerock.script.source.ScriptSource;
import org.forgerock.script.source.SourceContainer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public abstract class ScriptTest {

    private ScriptRegistryImpl scriptRegistry = null;

    protected abstract Map<String, Object> getConfiguration();

    protected abstract String getLanguageName();

    protected abstract URL getScriptContainer(String name);

    @BeforeClass
    public void initScriptRegistry() throws Exception {
        Map<String, Object> configuration = new HashMap<String, Object>(1);
        configuration.put(getLanguageName(),getConfiguration());

        scriptRegistry = new ScriptRegistryImpl(configuration, ServiceLoader.load(ScriptEngineFactory.class),null);

        RequestHandler resource = mock(RequestHandler.class);

        scriptRegistry.setConnectionFactory(Resources.newInternalConnectionFactory(resource));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ReadRequest request = (ReadRequest) invocation.getArguments()[1];
                ResultHandler<Resource> handler = (ResultHandler<Resource>) invocation.getArguments()[2];
                handler.handleResult(
                        new Resource(request.getResourceName(), "1", new JsonValue(new HashMap<String, Object>())));
                return null;
            }
        }).when(resource).handleRead(any(ServerContext.class),any(ReadRequest.class),any(ResultHandler.class));

        Map<String,Object> router = new HashMap<String, Object>(7);

        for (ConnectionFunction operation : ConnectionFunction.values()) {
            router.put(operation.name().toLowerCase(),operation);
        }
        scriptRegistry.put("router", router);

        URL container = getScriptContainer("/container/");
        Assert.assertNotNull(container);

        scriptRegistry.addSourceUnit(new DirectoryContainer("name", container));
        scriptRegistry.addSourceUnit(new EmbeddedScriptSource(ScriptEntry.Visibility.PUBLIC,
                "egy = egy + 2;egy", new ScriptName("test1",getLanguageName())));

    }

    public ScriptRegistry getScriptRegistry() {
        return scriptRegistry;
    }

    @Test
    public void testEval() throws Exception {
        ScriptName scriptName = new ScriptName("test1",getLanguageName());
        ScriptEntry scriptEntry = getScriptRegistry().takeScript(scriptName);
        Assert.assertNotNull(scriptEntry);
        scriptEntry.put("egy", 1);
        assertThat(scriptEntry.getScript(null).eval()).isEqualTo(3);

        // Load Script from Directory
        scriptEntry = getScriptRegistry().takeScript(new ScriptName("sample", getLanguageName()));
        Assert.assertNotNull(scriptEntry);

        // Set ServiceLevel Scope
        scriptEntry.put("egy", 1);
        Script script = scriptEntry.getScript(null);

        // Set RequestLevel Scope
        script.put("ketto", 2);
        assertThat(script.eval()).isEqualTo(3);
    }


    protected abstract EmbeddedScriptSource getScriptSourceWithException();

    @Test(expectedExceptions = ScriptThrownException.class)
    public void testException() throws Exception {
        ScriptSource scriptSource = getScriptSourceWithException();

        getScriptRegistry().addSourceUnit(scriptSource);
        Assert.assertNotNull(getScriptRegistry().takeScript(scriptSource.getName()));
        getScriptRegistry().takeScript(scriptSource.getName()).getScript(null).eval(new SimpleBindings());
    }

    @Test
    public void testResource() throws Exception {
        ScriptName scriptName = new ScriptName("resource",getLanguageName());
        ScriptEntry scriptEntry = getScriptRegistry().takeScript(scriptName);
        Assert.assertNotNull(scriptEntry);

        Script script = scriptEntry.getScript(null);
        // Set RequestLevel Scope
        script.put("ketto", 2);
        assertThat(script.eval()).isEqualTo("DDOE");
    }

    @DataProvider(name = "Data-Provider-Function")
    public Object[][] scriptProvider() {
        ScriptEntry scriptEntry =
                getScriptRegistry().takeScript(new ScriptName("sample",getLanguageName()));
        scriptEntry.put("egy", 1);
        return new Object[][] { { scriptEntry } };
    }

    @Test(threadPoolSize = 4, invocationCount = 10, timeOut = 1000,
            dataProvider = "Data-Provider-Function", dependsOnMethods = { "testEval" })
    public void parameterIntTest(ScriptEntry scriptEntry) throws Exception {
        Script script = scriptEntry.getScript(null);
        script.put("ketto", 2);
        assertThat(script.eval()).isEqualTo(3);
    }

    @Test
    public void testListener() throws Exception {
        ScriptName scriptName = new ScriptName("listener",getLanguageName());
        final SourceContainer parentContainer =
                new DirectoryContainer("root", getScriptContainer("/"));
        ScriptSource scriptSource = new EmbeddedScriptSource("2 * 2", scriptName) {
            public ScriptName[] getDependencies() {
                return new ScriptName[] { parentContainer.getName() };
            }
        };
        final Object[] status = new Object[2];
        ScriptListener listener = new ScriptListener() {
            public void scriptChanged(ScriptEvent event) {
                status[0] = Integer.valueOf(event.getType());
                status[1] = event.getScriptLibraryEntry();
            }
        };

        getScriptRegistry().addScriptListener(scriptName, listener);
        // verifyZeroInteractions(listener);

        getScriptRegistry().addSourceUnit(scriptSource);
        Assert.assertEquals(status[0], ScriptEvent.REGISTERED);
        assertThat(((ScriptEntry) status[1]).getScript(null).eval()).isEqualTo(4);
        getScriptRegistry().addSourceUnit(scriptSource);
        Assert.assertEquals(status[0], ScriptEvent.MODIFIED);
        assertThat(((ScriptEntry) status[1]).getScript(null).eval()).isEqualTo(4);
        getScriptRegistry().removeSourceUnit(scriptSource);
        Assert.assertEquals(status[0], ScriptEvent.UNREGISTERING);
        try {
            ((ScriptEntry) status[1]).getScript(null).eval();
            Assert.fail("Script MUST fail!");
        } catch (ScriptException e) {
            /* Expected */
        } catch (Exception e) {
            e.printStackTrace();
        }

        getScriptRegistry().addSourceUnit(scriptSource);
        Assert.assertEquals(status[0], ScriptEvent.REGISTERED);
        assertThat(((ScriptEntry) status[1]).getScript(null).eval()).isEqualTo(4);
        getScriptRegistry().removeSourceUnit(parentContainer);
        Assert.assertEquals(status[0], ScriptEvent.UNREGISTERING);

        getScriptRegistry().addSourceUnit(scriptSource);
        Assert.assertEquals(status[0], ScriptEvent.REGISTERED);
        getScriptRegistry().deleteScriptListener(scriptName, listener);

        getScriptRegistry().addSourceUnit(scriptSource);
        Assert.assertEquals(status[0], ScriptEvent.REGISTERED);

    }

    @Test
    public void testCompiler() throws Exception {
        ScriptName scriptName = new ScriptName("invalid",getLanguageName());
        ScriptSource scriptSource =
                new EmbeddedScriptSource("must-fail(\"syntax error')", scriptName);

        Assert.assertNull(getScriptRegistry().takeScript(scriptName));

        // TODO Should we throw exception here? Not maybe to resolve circular
        // references
        getScriptRegistry().addSourceUnit(scriptSource);
        Assert.assertNotNull(getScriptRegistry().takeScript(scriptName));
        Assert.assertFalse(getScriptRegistry().takeScript(scriptName).isActive());
    }
}
