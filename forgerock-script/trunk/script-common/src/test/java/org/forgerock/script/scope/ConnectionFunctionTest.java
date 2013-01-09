/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock AS. All Rights Reserved
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

package org.forgerock.script.scope;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.Bindings;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.Resources;
import org.forgerock.json.resource.ResultHandler;
import org.forgerock.json.resource.RequestHandler;
import org.forgerock.json.resource.ServerContext;
import org.forgerock.script.engine.ScriptEngine;
import org.forgerock.script.engine.ScriptEngineFactory;
import org.forgerock.script.registry.ScriptRegistryImpl;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class ConnectionFunctionTest {

    public void jsr223Test() throws Exception {
        // Get the OSGi Router
        RequestHandler router = null;

        // Setup the map with functions
        Map<String, Object> functions = new HashMap<String, Object>(7);
        for (ConnectionFunction operation : ConnectionFunction.values()) {
            functions.put(operation.name().toLowerCase(), operation);
        }
        // Example to use the stateless functions
        // ConnectionFunction.READ.call({ConnectionFactory,Context}, argument)

        Bindings globalOpenIDMBinding = new SimpleBindings(new ConcurrentHashMap<String, Object>());

        globalOpenIDMBinding.put("openidm", functions);

        // Use Spring or SCR to create this bean
        ScriptRegistryImpl registry =
                new ScriptRegistryImpl(new HashMap<String, Object>(), ServiceLoader
                        .load(ScriptEngineFactory.class), globalOpenIDMBinding);

        //Setup the ConnectionFactory local or remote
        registry.setConnectionFactory(Resources.newInternalConnectionFactory(router));

        //Find the Engine for the Script name
        ScriptEngine engine = registry.getEngineByName("JavaScript");

        // Merge, deep copy and compile
        Bindings runtime = engine.compileBindings(null, null, registry.getBindings());

        // JSR 223 - 1
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        scriptEngineManager.getEngineByName("JavaScript").eval(
                "openidm.read('managed/user','DDOE')", runtime);

        // JSR 223 - 2
        Function custom = new Function() {
            public Object call(OperationParameter parameter, Object[] arguments) throws Exception {
                return "Works!!";
            }

            public <R, P> R accept(ScriptableVisitor<R, P> v, P p) {
                return v.visitFunction(p, this);
            }
        };

        runtime = new SimpleBindings();

        // Compile single object
        runtime.put("custom", engine.compileObject(null, custom));

        scriptEngineManager.getEngineByName("JavaScript").eval("custom()", runtime);
    }

    @Test
    public void actionTest() throws Exception {
        // action(String endPoint[, String id], String type, Map params, Map
        // content[, List fieldFilter][,Map context])
        final JsonValue result = new JsonValue(true);

        RequestHandler resource = mock(RequestHandler.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // ActionRequest request = (ActionRequest)
                // invocation.getArguments()[1];
                ResultHandler<JsonValue> handler =
                        (ResultHandler<JsonValue>) invocation.getArguments()[2];
                handler.handleResult(result);
                return null;
            }
        }).when(resource).handleAction(any(ServerContext.class), any(ActionRequest.class),
                any(ResultHandler.class));
        OperationParameter parameter =
                new OperationParameter(null, Resources.newInternalConnectionFactory(resource));

        Object[] arguments =
                new Object[] { "endPoint", "type", new HashMap<String, Object>(),
                    new HashMap<String, Object>(), new ArrayList<String>(),
                    new HashMap<String, Object>() };
        Assert.assertTrue(((JsonValue) ConnectionFunction.ACTION.call(parameter, arguments))
                .asBoolean());

        arguments =
                new Object[] { "endPoint", "type", new HashMap<String, Object>(),
                    new HashMap<String, Object>(), new ArrayList<String>(),
                    new HashMap<String, Object>() };
        Assert.assertTrue(((JsonValue) ConnectionFunction.ACTION.call(parameter, arguments))
                .asBoolean());

        try {
            arguments =
                    new Object[] { "endPoint", null, "type", new HashMap<String, Object>(),
                        new HashMap<String, Object>(), new ArrayList<String>(),
                        new HashMap<String, Object>() };
            ConnectionFunction.ACTION.call(parameter, arguments);
            Assert.fail("No such method");
        } catch (NoSuchMethodException e) {
            /* expected */
        }

        arguments =
                new Object[] { "endPoint", "type", new HashMap<String, Object>(),
                    new ArrayList<String>(), new HashMap<String, Object>() };
        try {
            ConnectionFunction.ACTION.call(parameter, arguments);
            Assert.fail("No such method");
        } catch (NoSuchMethodException e) {
            /* Expected */
        }
    }

}
