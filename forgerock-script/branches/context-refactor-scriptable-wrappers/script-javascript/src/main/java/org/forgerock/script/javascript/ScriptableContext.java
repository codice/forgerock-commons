/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014 ForgeRock Inc. All rights reserved.
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

import org.forgerock.json.resource.Context;
import org.forgerock.script.scope.Parameter;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.annotations.JSFunction;

/**
 * Provides a {@code Scriptable} wrapper for an abstract {@code Context} object.
 */
class ScriptableContext extends NativeObject implements Wrapper {

    private static final long serialVersionUID = 1L;

    /** The context being wrapped. */
    final transient Parameter parameter;

    /** The context being wrapped. */
    private final Context context;

    public ScriptableContext() { parameter = null; context = null; }

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
        }
        this.parameter = parameter;
        this.context = context;
    }

    Context getWrappedContext() {
        //return contexts.get("current");
        return context;
    }

    @JSFunction
    public boolean containsContext(Class clazz, Scriptable start) {
        return getWrappedContext().containsContext(clazz);
    }

    @JSFunction
    public Object asContext(Class clazz, Scriptable start) {
        return Converter.wrap(parameter, getWrappedContext().asContext(clazz), start, false);
    }

    @JSFunction
    public boolean containsContext(String contextName, Scriptable start) {
        return getWrappedContext().containsContext(contextName);
    }

    @JSFunction
    public Object getContext(String contextName, Scriptable start) {
        return Converter.wrap(parameter, getWrappedContext().getContext(contextName), start, false);
    }

    @JSFunction
    public Object getParent(Scriptable start) {
        return Converter.wrap(parameter, getWrappedContext().getParent(), start, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String name, Scriptable start) {
        return getWrappedContext().toJsonValue().isMap()
                ? Converter.wrap(parameter, context.toJsonValue().get(name), start, false)
                : NOT_FOUND;
    }

    @Override
    public Object get(int index, Scriptable start) {
        return getWrappedContext().toJsonValue().isList()
                ? Converter.wrap(parameter, context.toJsonValue().get(index), start, false)
                : NOT_FOUND;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return getWrappedContext().toJsonValue().isMap()
                && !getWrappedContext().toJsonValue().get(name).isNull();
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return getWrappedContext().toJsonValue().isList()
                && index < getWrappedContext().toJsonValue().size()
                && getWrappedContext().toJsonValue().get(index).isNull();
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
        return context.toJsonValue().keys().toArray();
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
        return getWrappedContext() == null ? "null" : getWrappedContext().toJsonValue().toString();
    }
}
