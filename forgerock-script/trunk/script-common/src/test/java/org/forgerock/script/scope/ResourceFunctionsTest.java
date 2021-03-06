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

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.Connection;
import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.ConnectionProvider;
import org.forgerock.json.resource.InternalServerErrorException;
import org.forgerock.json.resource.MemoryBackend;
import org.forgerock.json.resource.PersistenceConfig;
import org.forgerock.json.resource.RequestHandler;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.Resources;
import org.forgerock.json.resource.ResultHandler;
import org.forgerock.json.resource.RootContext;
import org.forgerock.json.resource.Router;
import org.forgerock.json.resource.ServerContext;
import org.forgerock.script.engine.ScriptEngine;
import org.forgerock.script.engine.ScriptEngineFactory;
import org.forgerock.script.registry.ScriptRegistryImpl;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import javax.script.Bindings;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * A NAME does ...
 *
 * @author Laszlo Hordos
 */
public class ResourceFunctionsTest {

    private ConnectionFactory getConnectionFactory() {
        // Get the OSGi Router
        final Router router = new Router();
        router.addRoute("Users", new MemoryBackend());
        router.addRoute("Groups", new MemoryBackend());
        return Resources.newInternalConnectionFactory(router);
    }

    protected PersistenceConfig getPersistenceConfig(final ConnectionFactory connectionFactory) {
        return PersistenceConfig.builder().connectionProvider(new ConnectionProvider() {
            // no longer used, but still required by PersistenceConfig
            @Override
            public Connection getConnection(String connectionId) throws ResourceException {
                return connectionFactory.getConnection();
            }

            @Override
            public String getConnectionId(Connection connection) throws ResourceException {
                return "TEST";
            }
        }).build();
    }

    // @Test
    public void jsr223Test() throws Exception {

        // Example to use the stateless functions
        // ConnectionFunction.READ.call({ConnectionFactory,Context}, argument)

        ConnectionFactory connectionFactory = getConnectionFactory();

        Bindings globalBinding = new SimpleBindings(new ConcurrentHashMap<String, Object>());

        globalBinding.put("global", FunctionFactory.getResource(connectionFactory));

        // Use Spring or SCR to create this bean
        ScriptRegistryImpl registry = new ScriptRegistryImpl(
                new HashMap<String, Object>(), ServiceLoader.load(ScriptEngineFactory.class), globalBinding);

        // Setup the PersistenceConfig
        registry.setPersistenceConfig(getPersistenceConfig(connectionFactory));

        // Find the Engine for the Script name
        ScriptEngine engine = registry.getEngineByName("JavaScript");

        // Merge, deep copy and compile
        Bindings runtime = null;// engine.compileBindings(null, null,
                                // registry.getBindings());

        // JSR 223 - 1
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

        // JSR 223 - 2
        Function custom = new Function<JsonValue>() {

            private static final long serialVersionUID = 1L;

            @Override
            public JsonValue call(Parameter scope, Function<?> callback, Object... arguments)
                    throws ResourceException {
                return new JsonValue("Works!!");
            }
        };

        runtime = new SimpleBindings();

        // Compile single object
        runtime.put("custom", engine.compileObject(null, custom));

        scriptEngineManager.getEngineByName("JavaScript").eval("custom()", runtime);
    }

    @Test
    public void actionTest(final ITestContext testContext) throws Exception {
        // action(String endPoint[, String id], String type, Map params, Map content[, List fieldFilter][,Map context])
        final JsonValue result = new JsonValue(true);

        final RequestHandler resource = mock(RequestHandler.class);

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                // ActionRequest request = (ActionRequest)
                // invocation.getArguments()[1];
                ResultHandler<JsonValue> handler = (ResultHandler<JsonValue>) invocation.getArguments()[2];
                handler.handleResult(result);
                return null;
            }
        }).when(resource).handleAction(any(ServerContext.class), any(ActionRequest.class), any(ResultHandler.class));

        final ConnectionFactory connectionFactory = Resources.newInternalConnectionFactory(resource);

        ServerContext serverContext = new ServerContext(new RootContext());
        final PersistenceConfig persistenceConfig =
                PersistenceConfig.builder().connectionProvider(new ConnectionProvider() {
                    @Override
                    public Connection getConnection(String connectionId) throws ResourceException {
                        return connectionFactory.getConnection();
                    }

                    @Override
                    public String getConnectionId(Connection connection) throws ResourceException {
                        return "DEFAULT";
                    }
                }).build();

        OperationParameter parameter =
                new OperationParameter(serverContext, "DEFAULT", persistenceConfig);
        /*
         * new OperationParameter(serverContext) {
         *
         * @Override protected Object convertObject(Object source) { return
         * source; }
         *
         * @Override protected Object convertFunction(Function source) { return
         * source; }
         *
         * @Override protected Object convertLazyResource(LazyResource source) {
         * return source; }
         *
         * @Override protected String getConnectionId() { return "DEFAULT"; }
         *
         * @Override public PersistenceConfig getPersistenceConfig() { return
         * persistenceConfig; } };
         */

        // action(String resourceName, [String actionId,] Map params, Map
        // content[, List fieldFilter][,Map context])

        Function<JsonValue> action = ResourceFunctions.newActionFunction(connectionFactory);

        Object[] arguments =
                new Object[] { "resourceName", "actionId", new HashMap<String, Object>(),
                    new HashMap<String, Object>(), new ArrayList<String>(), serverContext.toJsonValue() };
        Assert.assertTrue(action.call(parameter, null, arguments).asBoolean());

        arguments =
                new Object[] { "resourceName", "actionId", new HashMap<String, Object>(),
                    new HashMap<String, Object>(), new ArrayList<String>() };
        Assert.assertTrue(action.call(parameter, null, arguments).asBoolean());

        try {
            arguments =
                    new Object[] { "resourceName", null, "actionId", new HashMap<String, Object>(),
                        new HashMap<String, Object>(), new ArrayList<String>(),
                        new HashMap<String, Object>() };
            action.call(parameter, null, arguments);
            Assert.fail("No such method");
        } catch (NoSuchMethodException e) {
            /* expected */
        }

        arguments =
                new Object[] { "resourceName", "actionId", new HashMap<String, Object>(),
                    new ArrayList<String>(), new HashMap<String, Object>() };
        try {
            action.call(parameter, null, arguments);
            Assert.fail("No such method");
        } catch (NoSuchMethodException e) {
            /* Expected */
        }
    }

}
