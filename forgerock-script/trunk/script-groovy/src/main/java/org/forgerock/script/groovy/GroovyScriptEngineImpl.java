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

package org.forgerock.script.groovy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.ScriptException;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.Context;
import org.forgerock.script.engine.AbstractScriptEngine;
import org.forgerock.script.engine.CompilationHandler;
import org.forgerock.script.engine.ScriptEngineFactory;
import org.forgerock.script.scope.ObjectConverter;
import org.forgerock.script.scope.OperationParameter;
import org.forgerock.script.scope.ScriptableVisitor;
import org.forgerock.script.source.SourceContainer;
import org.forgerock.script.source.SourceUnit;
import org.forgerock.script.source.SourceUnitObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceConnector;
import groovy.util.ResourceException;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class GroovyScriptEngineImpl extends AbstractScriptEngine implements ResourceConnector,
        SourceUnitObserver {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(GroovyScriptEngineImpl.class);

    private ScriptEngineFactory factory;

    private GroovyScriptEngine groovyScriptEngine;

    private AtomicReference<ConnectionFactory> connectionFactory;

    GroovyScriptEngineImpl(Map<String, Object> configuration, final ScriptEngineFactory factory) {
        this.factory = factory;

        // TODO get them from configuration
        Properties properties = new Properties();

        CompilerConfiguration config = new CompilerConfiguration(properties);

        GroovyClassLoader loader =
                new GroovyClassLoader(getParentLoader(), config, false /* true */);

        groovyScriptEngine = new GroovyScriptEngine(this, loader);
        groovyScriptEngine.setConfig(config);

    }

    public void compileScript(CompilationHandler handler) {
        try {
            handler.setClassLoader(groovyScriptEngine.getGroovyClassLoader());

            GroovyCodeSource codeSource =
                    new GroovyCodeSource(handler.getScriptSource().getReader(), handler
                            .getScriptSource().getName().getName(), "/groovy/script");
            codeSource.setCachable(false);

            Class scriptClass = groovyScriptEngine.getGroovyClassLoader().parseClass(codeSource);

            if (groovy.lang.Script.class.isAssignableFrom(scriptClass)) {
                handler.setCompiledScript(new GroovyScript(scriptClass,this));
            } else {
                handler.handleException(new ScriptException("Source is not a Groovy Script"));
            }
        } catch (Throwable e) {
            e.printStackTrace();
            handler.handleException(e);
        }
    }

    public ScriptEngineFactory getFactory() {
        return factory;
    }

    // determine appropriate class loader to serve as parent loader
    // for GroovyClassLoader instance
    private ClassLoader getParentLoader() {
        // check whether thread context loader can "see" Groovy Script class
        ClassLoader ctxtLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class c = ctxtLoader.loadClass(Script.class.getName());
            if (c == Script.class) {
                return ctxtLoader;
            }
        } catch (ClassNotFoundException cnfe) {
            /* ignore */
        }
        // exception was thrown or we get wrong class
        return Script.class.getClassLoader();
    }

    private Set<URL> roots = new HashSet<URL>();

    /**
     * Get a resource connection as a {@code URLConnection} to retrieve a script
     * from the {@code ResourceConnector}.
     * 
     * @param resourceName
     *            name of the resource to be retrieved
     * @return a URLConnection to the resource
     * @throws ResourceException
     */
    public URLConnection getResourceConnection(String resourceName) throws ResourceException {
        // Get the URLConnection
        URLConnection groovyScriptConn = null;

        ResourceException se = null;
        for (URL root : roots) {
            URL scriptURL = null;
            try {
                scriptURL = new URL(root, resourceName);
                groovyScriptConn = scriptURL.openConnection();

                // Make sure we can open it, if we can't it doesn't exist.
                // Could be very slow if there are any non-file:// URLs in there
                groovyScriptConn.getInputStream();

                break; // Now this is a bit unusual

            } catch (MalformedURLException e) {
                String message = "Malformed URL: " + root + ", " + resourceName;
                if (se == null) {
                    se = new ResourceException(message);
                } else {
                    se = new ResourceException(message, se);
                }
            } catch (IOException e1) {
                groovyScriptConn = null;
                String message = "Cannot open URL: " + scriptURL;
                groovyScriptConn = null;
                if (se == null) {
                    se = new ResourceException(message);
                } else {
                    se = new ResourceException(message, se);
                }
            }
        }

        if (se == null)
            se = new ResourceException("No resource for " + resourceName + " was found");

        // If we didn't find anything, report on all the exceptions that
        // occurred.
        if (groovyScriptConn == null)
            throw se;
        return groovyScriptConn;
    }

    public void addSourceUnit(SourceUnit unit) {
        if (unit instanceof SourceContainer) {
            URL root = unit.getSource();
            if (null != root) {
                roots.add(root);
            }
        }
    }

    public void removeSourceUnit(SourceUnit unit) {
        if (unit instanceof SourceContainer) {
            URL root = unit.getSource();
            if (null != root) {
                roots.remove(root);
            }
        }
    }

    protected ObjectConverter getObjectConverter(Context context) {
        return new ObjectConverter(new OperationParameter(context, getConnectionFactory())) {
            protected void init() {

            }

            public Object handle(Object value) {
                return value;
            }
        };
    }

    protected ScriptableVisitor<Object, ObjectConverter> getVisitor() {
        return new GroovyScriptableVisitor();
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory.get();
    }

    public void setConnectionFactory(AtomicReference<ConnectionFactory> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
