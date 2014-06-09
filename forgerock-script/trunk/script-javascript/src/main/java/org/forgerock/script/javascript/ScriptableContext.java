/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014 Forgerock AS. All rights reserved.
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

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ClientContext;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.servlet.HttpContext;
import org.forgerock.script.scope.Parameter;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.annotations.JSFunction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides a {@code Scriptable} wrapper for an abstract {@code Context} object.
 */
class ScriptableContext extends NativeObject implements Wrapper {

    private static final long serialVersionUID = 1L;

    private static final String CALLER = "caller";
    private static final String CURRENT = "current";
    private static final String PARENT = "parent";

    final transient Parameter parameter;

    /** map of contexts exposed by this wrapper by context name */
    private Map<String,Context> contexts;

    /** information about the calling client */
    private JsonValue caller;

    /** set of keys that this context wrapper "responds to */
    private Set<String> ids;

    /**
     * Constructs a new scriptable wrapper around the specified context.
     *
     * @param context
     *            the context to be wrapped.
     * @throws NullPointerException
     *             if the specified context is {@code null}.
     */
    public ScriptableContext(final Parameter parameter, final Context context) {
        if (null == context) {
            throw new NullPointerException();
        } this.parameter = parameter;
        contexts = new HashMap<String, Context>();
        contexts.put(CURRENT, context);
        if (context.getParent() != null) {
            contexts.put(PARENT, context.getParent());
        }
        for (Context aContext = context; aContext != null; aContext = aContext.getParent()) {
            if (!contexts.containsKey(aContext.getContextName())) {
                contexts.put(aContext.getContextName(), aContext);
            }
        }

        ids = new HashSet<String>(contexts.keySet());

        if (context.containsContext(ClientContext.class)) {
            // add "caller" to the scope if there is client context information
            ids.add(CALLER);

            final ClientContext client = context.asContext(ClientContext.class);
            caller = new JsonValue(new HashMap<String,Object>());
            caller.put("name", client.getContextName());
            caller.put("external", client.isExternal());
        }
    }

    Context getWrappedContext() {
        return contexts.get(CURRENT);
    }

    @JSFunction
    public boolean containsContext(Class clazz, Scriptable start) {
        return getWrappedContext().containsContext(clazz);
    }

    @JSFunction
    public Object asContext(Class clazz, Scriptable start) {
        return Converter.wrap(parameter, getWrappedContext().asContext(clazz).toJsonValue(), start, false);
    }

    @JSFunction
    public boolean containsContext(String contextName, Scriptable start) {
        return getWrappedContext().containsContext(contextName);
    }

    @JSFunction
    public Object getContext(String contextName, Scriptable start) {
        return Converter.wrap(parameter, getWrappedContext().getContext(contextName).toJsonValue(), start, false);
    }

    @JSFunction
    public Object getParent(Scriptable start) {
        return Converter.wrap(parameter, getWrappedContext().getParent(), start, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String name, Scriptable start) {
        if (CALLER.equals(name)) {
            return Converter.wrap(parameter, caller, start, false);
        } else if (contexts.containsKey(name)) {
            if (HttpContext.CONTEXT_NAME.equals(name)) {
                final JsonValue value = contexts.get(name).toJsonValue();
                // Join all header/parameter values for the same header/parameter into comma-separated-value String
                value.put(HttpContext.ATTR_HEADERS, listValuesAsStrings(value.get(HttpContext.ATTR_HEADERS)));
                value.put(HttpContext.ATTR_PARAMETERS, listValuesAsStrings(value.get(HttpContext.ATTR_PARAMETERS)));
                return Converter.wrap(parameter, value, start, false);
            } else {
                return Converter.wrap(parameter, contexts.get(name).toJsonValue(), start, false);
            }
        } else {
            return NOT_FOUND;
        }
    }

    private Map<String, Object> listValuesAsStrings(JsonValue values) {
        final Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (final String key : values.keys()) {
            final StringBuilder sb = new StringBuilder();
            for (final Object value : values.get(key).asList()) {
                sb.append(value.toString());
                sb.append(",");
            }
            map.put(key, sb.substring(0, sb.length() - 1));
        }
        return map;
    }

    @Override
    public Object get(int index, Scriptable start) {
        return NOT_FOUND;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return ids.contains(name);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return false;
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
    }

    @Override
    public void delete(String name) {
    }

    @Override
    public void delete(int index) {
    }

    @Override
    public Object[] getIds() {
        return ids.toArray();
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return false; // no support for javascript instanceof
    }

    @Override
    public Object unwrap() {
        return getWrappedContext();
    }

    public String toString() {
        final JsonValue allContexts = new JsonValue(new HashMap<String,Object>(contexts.size()));
        for (final Map.Entry<String,Context> entry : contexts.entrySet()) {
            allContexts.put(entry.getKey(), entry.getValue().toJsonValue().getObject());
        }
        return allContexts.toString();
    }
}
