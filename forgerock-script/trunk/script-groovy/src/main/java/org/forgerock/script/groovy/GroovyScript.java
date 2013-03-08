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

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.MethodClosure;
import org.forgerock.json.resource.Context;
import org.forgerock.script.engine.CompiledScript;
import org.forgerock.script.exception.ScriptThrownException;
import org.forgerock.script.scope.AbstractFactory;
import org.forgerock.script.scope.Function;
import org.forgerock.script.scope.OperationParameter;
import org.forgerock.script.scope.Parameter;
import org.forgerock.util.Factory;
import org.forgerock.util.LazyMap;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.Script;
import groovy.lang.Tuple;
import groovy.util.ResourceException;

/**
 * A JavaScript script.
 * <p>
 * This implementation pre-compiles the provided script. Any syntax errors in
 * the source code will throw an exception during construction of the object.
 * <p>
 * 
 * @author Paul C. Bryan
 * @author aegloff
 */
public class GroovyScript implements CompiledScript {

    private final String scriptName;

    private final GroovyScriptEngineImpl engine;

    public GroovyScript(String scriptName, final GroovyScriptEngineImpl groovyEngine)
            throws IllegalAccessException, InstantiationException, ResourceException,
            groovy.util.ScriptException {
        this.scriptName = scriptName;
        engine = groovyEngine;
        engine.createScript(scriptName, new Binding());
    }

    public Bindings prepareBindings(final Context context, final Bindings request,
            final Bindings... scopes) {
        final Map<String, Object> b = mergeBindings(context, request, scopes);
        return b instanceof Bindings ? (Bindings) b : new SimpleBindings(b);
    }

    public Object eval(final Context context, final Bindings request,final Bindings... scopes)
            throws ScriptException {

        final Map<String, Object> bindings = mergeBindings(context, request, scopes);

        // Bindings so script has access to this environment.
        // Only initialize once.
        if (null == bindings.get("context")) {
            // add context to bindings
            // ctx.setAttribute("context", ctx);

            // direct output to ctx.getWriter
            // If we're wrapping with a PrintWriter here,
            // enable autoFlush because otherwise it might not get done!
            final Writer writer = engine.getWriter();
            bindings.put("out", (writer instanceof PrintWriter) ? writer : new PrintWriter(writer,
                    true));

            // Not going to do this after all (at least for now).
            // Scripts can use context.{reader, writer, errorWriter}.
            // That is a modern version of System.{in, out, err} or
            // Console.{reader, writer}().
            //
            // // New I/O names consistent with ScriptContext and
            // java.io.Console.
            //
            // ctx.setAttribute("writer", writer, ScriptContext.ENGINE_SCOPE);
            //
            // // Direct errors to ctx.getErrorWriter
            // final Writer errorWriter = ctx.getErrorWriter();
            // ctx.setAttribute("errorWriter", (errorWriter instanceof
            // PrintWriter) ?
            // errorWriter :
            // new PrintWriter(errorWriter),
            // ScriptContext.ENGINE_SCOPE);
            //
            // // Get input from ctx.getReader
            // // We don't wrap with BufferedReader here because we expect that
            // if
            // // the host wants that they do it. Either way Groovy scripts will
            // // always have readLine because the GDK supplies it for Reader.
            // ctx.setAttribute("reader", ctx.getReader(),
            // ScriptContext.ENGINE_SCOPE);
        }

        // Fix for GROOVY-3669: Can't use several times the same JSR-223
        // ScriptContext for differents groovy script
        // if (ctx.getWriter() != null) {
        // ctx.setAttribute("out", new PrintWriter(ctx.getWriter(), true),
        // DefaultScriptContext.REQUEST_SCOPE);
        // }

        try {
            Script scriptObject = engine.createScript(scriptName, new Binding(bindings));

            // create a Map of MethodClosures from this new script object
            Method[] methods = scriptObject.getClass().getMethods();
            final Map<String, Closure> closures = new HashMap<String, Closure>();
            for (Method m : methods) {
                String name = m.getName();
                closures.put(name, new MethodClosure(scriptObject, name));
            }

            MetaClass oldMetaClass = scriptObject.getMetaClass();

            /*
             * We override the MetaClass of this script object so that we can
             * forward calls to global closures (of previous or future "eval"
             * calls) This gives the illusion of working on the same "global"
             * scope.
             */
            scriptObject.setMetaClass(new DelegatingMetaClass(oldMetaClass) {
                @Override
                public Object invokeMethod(Object object, String name, Object args) {
                    if (args == null) {
                        return invokeMethod(object, name, MetaClassHelper.EMPTY_ARRAY);
                    }
                    if (args instanceof Tuple) {
                        return invokeMethod(object, name, ((Tuple) args).toArray());
                    }
                    if (args instanceof Object[]) {
                        return invokeMethod(object, name, (Object[]) args);
                    } else {
                        return invokeMethod(object, name, new Object[] { args });
                    }
                }

                @Override
                public Object invokeMethod(Object object, String name, Object[] args) {
                    try {
                        return super.invokeMethod(object, name, args);
                    } catch (MissingMethodException mme) {
                        return callGlobal(name, args, bindings);
                    }
                }

                @Override
                public Object invokeStaticMethod(Object object, String name, Object[] args) {
                    try {
                        return super.invokeStaticMethod(object, name, args);
                    } catch (MissingMethodException mme) {
                        return callGlobal(name, args, bindings);
                    }
                }

                private Object callGlobal(String name, Object[] args, Map<String, Object> ctx) {
                    Closure closure = closures.get(name);
                    if (closure != null) {
                        return closure.call(args);
                    } else {
                        // Look for closure valued variable in the
                        // given ScriptContext. If available, call it.
                        Object value = ctx.get(name);
                        if (value instanceof Closure) {
                            return ((Closure) value).call(args);
                        } // else fall thru..
                    }
                    throw new MissingMethodException(name, getClass(), args);
                }
            });

            try {
                return scriptObject.run();
            } catch (Exception e) {
                throw new ScriptThrownException("", e);
            }
        } catch (ScriptException e) {
            throw e;
        } catch (Exception e) {
            throw new ScriptException(e);
        } finally {
            // Fix for GROOVY-3669: Can't use several times the same JSR-223
            // ScriptContext for different groovy script
            // Groovy's scripting engine implementation adds those two variables
            // in the binding
            // but should clean up afterwards
            // ctx.removeAttribute("context",
            // DefaultScriptContext.REQUEST_SCOPE);
            // ctx.removeAttribute("out", DefaultScriptContext.REQUEST_SCOPE);
        }
    }

    private Map<String, Object> mergeBindings(final Context context, final Bindings request,
                                              final Bindings... scopes) {
        Set<String> safeAttributes = null != request ? request.keySet() : Collections.EMPTY_SET;
        Map<String, Object> scope = new HashMap<String, Object>();
        for (Map<String, Object> next : scopes) {
            if (null == next)
                continue;
            for (Map.Entry<String, Object> entry : next.entrySet()) {
                if (scope.containsKey(entry.getKey()) || safeAttributes.contains(entry.getKey())) {
                    continue;
                }
                scope.put(entry.getKey(), entry.getValue());
            }
        }
        // Make lazy deep copy        
        if (!scope.isEmpty()) {
            scope =
                    new LazyMap<String, Object>(new InnerMapFactory(scope, new OperationParameter(
                            context, "DEFAULT", engine.getPersistenceConfig())));
        }

        if (null == request || request.isEmpty()) {
            return scope;
        } else if (scope.isEmpty()) {
            return request;
        } else {
            request.putAll(scope);
            return request;
        }
    }

    public static class InnerMapFactory extends AbstractFactory.MapFactory {

        private final Parameter parameter;

        public InnerMapFactory(final Map<String, Object> source, final Parameter parameter) {
            super(source);
            this.parameter = parameter;
        }

        protected Factory<List<Object>> newListFactory(final List<Object> source) {
            return new InnerListFactory(source, parameter);
        }

        protected Factory<Map<String, Object>> newMapFactory(final Map<String, Object> source) {
            return new InnerMapFactory(source, parameter);
        }

        protected Object convertFunction(final Function<?> source) {
            return new FunctionClosure(null, parameter, source);
        }

        public Parameter getParameter() {
            return parameter;
        }
    }

    public static class InnerListFactory extends AbstractFactory.ListFactory {

        private final Parameter parameter;

        public InnerListFactory(final List<Object> source, final Parameter parameter) {
            super(source);
            this.parameter = parameter;
        }

        protected Factory<List<Object>> newListFactory(final List<Object> source) {
            return new InnerListFactory(source, parameter);
        }

        protected Factory<Map<String, Object>> newMapFactory(final Map<String, Object> source) {
            return new InnerMapFactory(source, parameter);
        }

        protected Object convertFunction(final Function<?> source) {
            return new FunctionClosure(null, parameter, source);
        }

        public Parameter getParameter() {
            return parameter;
        }
    }
}
