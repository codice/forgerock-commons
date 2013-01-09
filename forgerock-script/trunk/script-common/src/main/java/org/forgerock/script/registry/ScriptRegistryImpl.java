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

package org.forgerock.script.registry;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.Context;
import org.forgerock.script.Script;
import org.forgerock.script.ScriptEntry;
import org.forgerock.script.ScriptEvent;
import org.forgerock.script.ScriptListener;
import org.forgerock.script.ScriptName;
import org.forgerock.script.ScriptRegistry;
import org.forgerock.script.engine.CompilationHandler;
import org.forgerock.script.engine.CompiledScript;
import org.forgerock.script.engine.ScriptEngine;
import org.forgerock.script.engine.ScriptEngineFactory;
import org.forgerock.script.engine.ScriptEngineFactoryObserver;
import org.forgerock.script.engine.Utils;
import org.forgerock.script.source.EmbeddedScriptSource;
import org.forgerock.script.source.ScriptEngineFactoryAware;
import org.forgerock.script.source.ScriptSource;
import org.forgerock.script.source.SourceContainer;
import org.forgerock.script.source.SourceUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class ScriptRegistryImpl implements ScriptRegistry, ScriptEngineFactoryObserver {

    private static final Logger logger = LoggerFactory.getLogger(ScriptRegistryImpl.class);
    public static final String SCRIPT_CACHE_DIR = "script.cache.dir";

    private final Set<ScriptEngineFactory> engineFactories;

    private final Map<ScriptEngineFactory, ScriptEngine> engines =
            new HashMap<ScriptEngineFactory, ScriptEngine>();

    private final ConcurrentHashMap<ScriptName, LibraryRecord> cache =
            new ConcurrentHashMap<ScriptName, LibraryRecord>();

    private final ConcurrentHashMap<ScriptName, SourceContainer> sourceCache =
            new ConcurrentHashMap<ScriptName, SourceContainer>();

    private final Map<String, Object> properties;

    private final AtomicReference<ConnectionFactory> connectionFactoryReference = new AtomicReference<ConnectionFactory>();

    /**
     * This is the global scope bindings. By default, a null value (which means
     * no global scope) is used.
     */
    protected final AtomicReference<Bindings> globalScope;

    public ScriptRegistryImpl(Map<String, Object> properties, Iterable<ScriptEngineFactory> engine,
            final Bindings globalScope) {
        this.properties = properties;
        this.engineFactories = new HashSet<ScriptEngineFactory>();
        for (ScriptEngineFactory factory : engine) {
            engineFactories.add(factory);
        }
        this.globalScope =
                globalScope != null ? new AtomicReference<Bindings>(globalScope)
                        : new AtomicReference<Bindings>();

        // properties.get(SCRIPT_CACHE_DIR);
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
       connectionFactoryReference.set(connectionFactory);
    }

    public void put(String key, Object value) {
        if (null == globalScope.get()) {
            globalScope.set(createBindings());
        }
        globalScope.get().put(key, value);
    }

    public Object get(String key) {
        if (getBindings() != null) {
            return getBindings().get(key);
        }
        return null;
    }

    public Bindings getBindings() {
        return globalScope.get();
    }

    public void setBindings(Bindings bindings) {
        this.globalScope.set(bindings);
    }

    public Bindings createBindings() {
        return new SimpleBindings(new ConcurrentHashMap<String, Object>());
    }

    // ScriptRegistry Methods

    public Set<ScriptName> listScripts() {
        return Collections.unmodifiableSet(cache.keySet());
    }

    public ScriptEntry takeScript(String name) {
        return takeScript(new ScriptName(name, SourceUnit.AUTO_DETECT));
    }

    public ScriptEngine getEngineByName(String shortName) {
        return findScriptEngine(shortName);
    }

    public ScriptEntry takeScript(JsonValue script)  throws ScriptException {
        if (null == script || script.expect(Map.class).isNull()) {
            throw new NullPointerException("Null scriptValue");
        }
        JsonValue name = script.get(SourceUnit.NAME);
        JsonValue type = script.get(SourceUnit.TYPE);
        JsonValue source = script.get(SourceUnit.SOURCE);

        if (!source.isNull() && (type.isNull() ||  type.expect(String.class).asString().equals(SourceUnit.AUTO_DETECT))) {
            throw new IllegalArgumentException("Embedded script must have type");
        }

        ScriptName scriptName =
                new ScriptName(name.isNull() || !name.isString() ? UUID.randomUUID().toString()
                                                                 : name.asString(),
                        type.isNull() || !type.isString() ? SourceUnit.AUTO_DETECT : name
                                .asString());

        if (!source.isNull()) {
            JsonValue visibility = script.get(SourceUnit.VISIBILITY);
            if (visibility.isNull()) {
            addSourceUnit(new EmbeddedScriptSource(source.asString(),scriptName));} else {
                addSourceUnit(new EmbeddedScriptSource(visibility.asEnum(ScriptEntry.Visibility.class), source.asString(),scriptName));
            }
        }
        ScriptEntry scriptEntry = takeScript(scriptName);

        for (Map.Entry<String,Object> entry: script.asMap().entrySet()) {
            if (SourceUnit.NAME.equals(entry.getKey()) || SourceUnit.TYPE.equals(entry.getKey()) ||SourceUnit.SOURCE.equals(entry.getKey()) ||SourceUnit.VISIBILITY.equals(entry.getKey()) || entry.getKey().startsWith("_")) {
                continue;
            }
            scriptEntry.put(entry.getKey(),entry.getValue());
        }

        return scriptEntry;
    }

    public synchronized ScriptEntry takeScript(ScriptName name) {
        LibraryRecord rec = cache.get(name);
        if (null != rec) {
            return rec.getScriptEntry();
        }
        ScriptEntry result = null;
        ScriptSource source = findScriptSource(name);
        if (null != source) {
            try {
                addSourceUnit(source);
                result = takeScript(name);
            } catch (ScriptException e) {
                /* ignore */
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked"})
    private ScriptEngine findScriptEngine(String shortName) {
        ScriptEngine engine = null;
        if (null != shortName) {
            ScriptEngineFactory factory = Utils.findScriptEngineFactory(shortName, engineFactories);
            if (null != factory) {
                synchronized (engines) {
                    engine = engines.get(factory);
                    if (null == engine) {
                        for (ScriptEngineFactory f : engines.keySet()) {
                            if (f.getLanguageName().equalsIgnoreCase(factory.getLanguageName())) {
                                // Avoid the duplicated factories for the same
                                // language!
                                return engines.get(f);
                            }
                        }
                        // TODO Make the initialization type safe!!
                        Object o = properties.get(factory.getLanguageName());
                        Map<String, Object> configuration = null;
                        if (o instanceof Map) {
                            configuration = (Map<String, Object>) o;
                        } else {
                            configuration = new HashMap<String, Object>();
                        }
                        configuration.put(Bindings.class.getName(), globalScope);
                        engine = factory.getScriptEngine(connectionFactoryReference, configuration);
                    }
                }
            }
        }
        return engine;
    }

    // private classes

    private class LibraryRecord implements CompilationHandler, InvocationHandler {

        private int status = CompilationHandler.INSTALLED;

        private final Vector<ScriptListener> listeners = new Vector<ScriptListener>();

        private WeakReference<ScriptEngine> scriptEngine = null;

        private String languageName = null;

        private ScriptSource source = null;

        private CompiledScript target = null;

        private ClassLoader scriptClassLoader = null;

        // private boolean compilationFailed = false;

        private final ScriptName scriptName;

        private LibraryRecord(ScriptName scriptName) {
            if (null == scriptName) {
                throw new NullPointerException("ScriptName is null");
            }
            this.scriptName = scriptName;
        }

        private LibraryRecord(ScriptSource scriptSource) {
            if (null == scriptSource) {
                throw new NullPointerException("ScriptSource is null");
            }
            this.scriptName = scriptSource.getName();
            setScriptSource(scriptSource);
        }

        private String getLanguageName() {
            return languageName;
        }

        private boolean isDependOn(ScriptName dependency) {
            if (null != dependency || null != source) {
                ScriptName[] dep = source.getDependencies();
                if (null != dep && dep.length > 0) {
                    for (ScriptName name : dep) {
                        if (dependency.equals(name)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private void setScriptEngine(final ScriptEngine scriptEngine) {
            synchronized (this) {
                if (null != scriptEngine) {
                    this.scriptEngine = new WeakReference<ScriptEngine>(scriptEngine);
                    languageName = scriptEngine.getFactory().getLanguageName();
                } else {
                    this.scriptEngine = null;
                }
                compile();
            }
        }

        private void setScriptSource(ScriptSource lazySource) {
            synchronized (this) {
                source = lazySource;
                compile();
            }
        }

        // This method is not Thread-Safe but it's called from synchronized
        // blocks only
        private void compile() {
            ScriptEngine engine = null != scriptEngine ? scriptEngine.get() : null;
            if (null == source) {
                source = findScriptSource(scriptName);
            }
            if (null != source) {
                if (engine == null) {
                    engine = findScriptEngine(source.guessType());
                    if (null != engine) {
                        scriptEngine = new WeakReference<ScriptEngine>(engine);
                    }
                }
            }
            if (null == source || null == engine) {
                try {
                    if (null != target) {
                        status = STOPPING;
                        notifyListeners(ScriptEvent.UNREGISTERING);
                    }
                } finally {
                    target = null;
                    status = INSTALLED;
                }
            } else {
                status = STARTING;
                engine.compileScript(this);
            }
        }

        private void notifyListeners(int type) {
            /*
             * a temporary array buffer, used as a snapshot of the state of
             * current Observers.
             */
            Object[] arrLocal = listeners.toArray();

            for (int i = arrLocal.length - 1; i >= 0; i--)
                try {
                    ((ScriptListener) arrLocal[i]).scriptChanged(new ScriptEvent(type,
                            ScriptRegistryImpl.this, scriptName));
                } catch (Throwable t) {
                    /* ignore */
                }
        }

        // START CompilationHandler

        public ScriptSource getScriptSource() {
            return source;
        }

        public ClassLoader getParentClassLoader() {
            return null;
        }

        public void setCompiledScript(CompiledScript script) {
            int type = null != target ? ScriptEvent.MODIFIED : ScriptEvent.REGISTERED;
            target = script;
            status = ACTIVE;
            notifyListeners(type);
        }

        public void handleException(Throwable throwable) {
            try {
                if (null != target) {
                    status = STOPPING;
                    notifyListeners(ScriptEvent.UNREGISTERING);
                }
            } finally {
                status = RESOLVED;
                target = null;
            }
            logger.error("Script compilation exception: {}", source.getName().getName(), throwable);
        }

        public void setClassLoader(ClassLoader classLoader) {
            this.scriptClassLoader = classLoader;
        }

        // END CompilationHandler

        private synchronized void addScriptListener(ScriptListener o) {
            if (o == null)
                throw new NullPointerException();
            if (!listeners.contains(o)) {
                listeners.addElement(o);
            }
        }

        private synchronized void deleteScriptListener(ScriptListener o) {
            listeners.removeElement(o);
        }

        private ScriptEntry getScriptEntry() {
            return new ServiceScript(getScriptProxy());
        }

        private CompiledScript getScriptProxy() {
            return (CompiledScript) Proxy.newProxyInstance(CompiledScript.class.getClassLoader(),
                    new Class[] { CompiledScript.class }, this);
        }

        private ClassLoader getRuntimeClassLoader() {
            if (null != scriptClassLoader) {
                return scriptClassLoader;
            }
            if (null != getParentClassLoader()) {
                return getParentClassLoader();
            }
            return Thread.currentThread().getContextClassLoader();
        }

        public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
            // do not proxy equals, hashCode, toString
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, arguments);
            }

            ThreadClassLoaderManager.getInstance().pushClassLoader(getRuntimeClassLoader());
            try {
                if (null != target) {
                    return method.invoke(target, arguments);
                } else {
                    throw new ScriptException("Script status is " + status);
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } finally {
                ThreadClassLoaderManager.getInstance().popClassLoader();
            }
        }

        private class ServiceScript extends ScopeHolder implements ScriptEntry {

            private final CompiledScript targetProxy;

            private ServiceScript(final CompiledScript target) {
                this.targetProxy = target;
            }

            public void addScriptListener(ScriptListener o) {
                LibraryRecord.this.addScriptListener(o);
            }

            public void deleteScriptListener(ScriptListener o) {
                LibraryRecord.this.deleteScriptListener(o);
            }

            public Script getScript(final Context context) {
                // TODO Decorate the target with the script
                // TODO Decorate with DelegatedCompilationHandler to compile a new instance for debug mode
                return new ScriptImpl(context, targetProxy) {
                    protected ScriptEngine getScriptEngine() throws ScriptException {
                        ScriptEngine engine = LibraryRecord.this.scriptEngine.get();
                        if (null == engine) {
                            throw new ScriptException("Engine is not available");
                        }
                        return engine;
                    }

                    protected Bindings getGlobalBindings() {
                        return ScriptRegistryImpl.this.globalScope.get();
                    }

                    protected Bindings getServiceBindings() {
                        return ServiceScript.this.getBindings();
                    }
                };
            }

            public ScriptName getName() {
                return scriptName;
            }

            public Visibility getVisibility() {
                return null != source ? source.getVisibility() : Visibility.DEFAULT;
            }

            public boolean isActive() {
                return target != null;
            }
        }
    }

    public void addScriptListener(ScriptName name, ScriptListener hook) {
        if (null != hook && null != name) {
            LibraryRecord record = cache.get(name);
            if (null == record) {
                LibraryRecord newRecord = new LibraryRecord(name);
                record = cache.putIfAbsent(name, newRecord);
                if (record == null) {
                    record = newRecord;
                }
            }
            record.addScriptListener(hook);
        }
    }

    public void deleteScriptListener(ScriptName name, ScriptListener hook) {
        if (null != hook && null != name) {
            LibraryRecord record = cache.get(name);
            if (null != record) {
                record.deleteScriptListener(hook);
            }
        }
    }

    private ScriptSource findScriptSource(ScriptName name) {
        ScriptSource source = null;
        for (SourceContainer container : sourceCache.values()) {
            if (container instanceof ScriptEngineFactoryAware) {
                ((ScriptEngineFactoryAware) container).setScriptEngineFactory(engineFactories);
            }
            source = container.findScriptSource(name);
            if (null != source) {
                break;
            }
        }
        return source;
    }

    // ScriptEngineFactoryObserver
    public void addingEntries(ScriptEngineFactory factory) {
        // engineFactories.add(factory);
        for (LibraryRecord cacheRecord : cache.values()) {
            if (CompilationHandler.INSTALLED == cacheRecord.status && null != cacheRecord.source) {
                ScriptEngine engine = findScriptEngine(cacheRecord.source.guessType());
                if (null != engine) {
                    cacheRecord.setScriptEngine(engine);
                }
            }
        }
    }

    public void removingEntries(ScriptEngineFactory factory) {
        engineFactories.remove(factory);
        engines.remove(factory);
        if (null != factory) {
            String languageName = factory.getLanguageName();
            for (LibraryRecord cacheRecord : cache.values()) {
                if (languageName.equals(cacheRecord.getLanguageName())) {
                    cacheRecord.setScriptEngine(null);
                }
            }
        }
    }

    // SourceUnitObserver
    public void addSourceUnit(SourceUnit unit) throws ScriptException {
        if (unit instanceof ScriptSource) {
            LibraryRecord record = new LibraryRecord((ScriptSource) unit);
            LibraryRecord cacheRecord = cache.putIfAbsent(unit.getName(), record);
            if (null == cacheRecord) {
                cacheRecord = record;
            }
            cacheRecord.setScriptSource((ScriptSource) unit);
        } else if (unit instanceof SourceContainer) {
            SourceContainer container = (SourceContainer) unit;
            sourceCache.put(unit.getName(), container);
            for (LibraryRecord cacheRecord : cache.values()) {
                if (null == cacheRecord.source) {
                    ScriptSource source = container.findScriptSource(cacheRecord.scriptName);
                    if (null != source) {
                        cacheRecord.setScriptSource(source);
                    }
                }
            }
        }
    }

    public void removeSourceUnit(SourceUnit unit) {
        if (unit instanceof ScriptSource) {
            LibraryRecord cacheRecord = cache.get(unit.getName());
            if (null != cacheRecord) {
                cacheRecord.setScriptSource(null);
            }
        } else if (unit instanceof SourceContainer) {
            sourceCache.remove(unit);
            for (LibraryRecord cacheRecord : cache.values()) {
                if (cacheRecord.isDependOn(unit.getName())) {
                    cacheRecord.setScriptSource(null);
                }
            }
        }
    }
}
