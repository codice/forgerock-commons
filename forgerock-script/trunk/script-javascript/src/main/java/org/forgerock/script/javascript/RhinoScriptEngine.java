/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 ForgeRock Inc. All rights reserved.
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

import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.Bindings;

import org.forgerock.json.resource.PersistenceConfig;
import org.forgerock.script.engine.AbstractScriptEngine;
import org.forgerock.script.engine.CompilationHandler;
import org.forgerock.script.engine.ScriptEngineFactory;
import org.forgerock.script.scope.OperationParameter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class RhinoScriptEngine extends AbstractScriptEngine {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RhinoScriptEngine.class);
    private ScriptEngineFactory factory;
    private AtomicReference<PersistenceConfig> persistenceConfigReference;

    RhinoScriptEngine(Map<String, Object> configuration, final ScriptEngineFactory factory) {
        this.factory = factory;

        Object debugProperty = configuration.get(CONFIG_DEBUG_PROPERTY);
        if (debugProperty instanceof String) {
            initDebugListener((String) debugProperty);
        }
    }

    public void compileScript(CompilationHandler handler) {

        // TODO Cache the source for debugger

        try {
            boolean sharedScope = true;// config.get("sharedScope").defaultTo(true).asBoolean();
            handler.setClassLoader(RhinoScriptEngine.class.getClassLoader());
            String name = handler.getScriptSource().getName().getName();
            if (null != handler.getScriptSource().getSource()
                    && "file".equals(handler.getScriptSource().getSource().getProtocol())) {
                name = URLDecoder.decode(handler.getScriptSource().getSource().getFile(), "utf-8");
            }
            handler.setCompiledScript(new RhinoScript(name, handler.getScriptSource().getReader(),
                    this, sharedScope));
        } catch (Throwable e) {
            handler.handleException(e);
        }
    }

    public ScriptEngineFactory getFactory() {
        return factory;
    }

    public OperationParameter getOperationParameter(
            final org.forgerock.json.resource.Context context) {
        final PersistenceConfig persistenceConfig = persistenceConfigReference.get();
        if (null == persistenceConfig) {
            throw new NullPointerException();
        }
        return new OperationParameter(context, "DEFAULT", persistenceConfig);
    }

    private ContextFactory.Listener debugListener = null;

    private volatile Boolean debugInitialised = null;

    public static final String CONFIG_DEBUG_PROPERTY = "openidm.script.javascript.debug";

    private synchronized void initDebugListener(String configString) {
        if (null == debugInitialised) {
            // Get here only once when the first factory initialised.
            if (null != configString) {
                try {
                    if (null == debugListener) {
                        debugListener =
                                new org.eclipse.wst.jsdt.debug.rhino.debugger.RhinoDebugger(
                                        configString);
                        Context.enter().getFactory().addListener(debugListener);
                        Context.exit();
                    }
                    ((org.eclipse.wst.jsdt.debug.rhino.debugger.RhinoDebugger) debugListener)
                            .start();
                    debugInitialised = Boolean.TRUE;
                } catch (Throwable ex) {
                    // Catch NoClassDefFoundError exception
                    if (!(ex instanceof NoClassDefFoundError)) {
                        // TODO What to do if there is an exception?
                        // throw new
                        // ScriptException("Failed to stop RhinoDebugger", ex);
                        logger.error("RhinoDebugger can not be started", ex);
                    } else {
                        // TODO add logging to WARN about the missing
                        // RhinoDebugger class
                        logger.warn("RhinoDebugger can not be started because the JSDT RhinoDebugger and Transport bundles must be deployed.");
                    }
                }
                debugInitialised = null == debugInitialised ? Boolean.FALSE : debugInitialised;
            } else if (false /* TODO How to stop */) {
                try {
                    ((org.eclipse.wst.jsdt.debug.rhino.debugger.RhinoDebugger) debugListener)
                            .stop();
                } catch (Throwable ex) {
                    // We do not care about the NoClassDefFoundError when we
                    // "Stop"
                    if (!(ex instanceof NoClassDefFoundError)) {
                        // TODO What to do if there is an exception?
                        // throw new
                        // ScriptException("Failed to stop RhinoDebugger", ex);
                    }
                } finally {
                    debugInitialised = Boolean.FALSE;
                }
            } else {
                debugInitialised = Boolean.FALSE;
            }
        }
    }

    public void setPersistenceConfig(final AtomicReference<PersistenceConfig> persistenceConfig) {
        this.persistenceConfigReference = persistenceConfig;
    }

    @Override
    public Bindings compileBindings(org.forgerock.json.resource.Context context, Bindings request,
            Bindings... value) {
        return null;
    }

    // private Script initializeScript(String name, File source, boolean
    // sharedScope)
    // throws ScriptException {
    // initDebugListener();
    // if (debugInitialised) {
    // try {
    // FileChannel inChannel = new FileInputStream(source).getChannel();
    // FileChannel outChannel = new
    // FileOutputStream(getTargetFile(name)).getChannel();
    // FileLock outLock = outChannel.lock();
    // FileLock inLock = inChannel.lock(0, inChannel.size(), true);
    // inChannel.transferTo(0, inChannel.size(), outChannel);
    //
    // outLock.release();
    // inLock.release();
    //
    // inChannel.close();
    // outChannel.close();
    // } catch (IOException e) {
    // logger.warn("JavaScript source was not updated for {}", name, e);
    // }
    // }
    // return new RhinoScript(name, source, sharedScope);
    // }
    //
    // private Script initializeScript(String name, String source, boolean
    // sharedScope)
    // throws ScriptException {
    // initDebugListener();
    // if (debugInitialised) {
    // try {
    // FileChannel outChannel = new
    // FileOutputStream(getTargetFile(name)).getChannel();
    // FileLock outLock = outChannel.lock();
    // ByteBuffer buf = ByteBuffer.allocate(source.length());
    // buf.put(source.getBytes("UTF-8"));
    // buf.flip();
    // outChannel.write(buf);
    // outLock.release();
    // outChannel.close();
    // } catch (IOException e) {
    // logger.warn("JavaScript source was not updated for {}", name, e);
    // }
    // }
    // return new RhinoScript(name, source, sharedScope);
    // }

}
