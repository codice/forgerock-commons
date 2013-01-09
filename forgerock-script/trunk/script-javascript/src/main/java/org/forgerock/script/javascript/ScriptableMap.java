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

import java.lang.ref.WeakReference;
import java.util.Map;

import org.forgerock.script.scope.ObjectConverter;
import org.forgerock.script.scope.OperationParameter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

/**
 * Provides a {@code Scriptable} wrapper for a {@code Map} object.
 * 
 * Rhino 1.7R3 supports native Map
 * 
 * @author Paul C. Bryan
 */
class ScriptableMap implements Scriptable, Wrapper {

    /** The map being wrapped. */
    private final Map<String, Object> map;

    private final WeakReference<OperationParameter> paramReference;

    /** The parent scope of the object. */
    private Scriptable parent;

    /** The prototype of the object. */
    private Scriptable prototype;

    /**
     * Constructs a new scriptable wrapper around the specified list.
     * 
     * @param map
     *            the map to be wrapped.
     * @throws NullPointerException
     *             if the specified map is {@code null}.
     */
    public ScriptableMap(OperationParameter objectConverter, Map<String, Object> map) {
        if (map == null) {
            throw new NullPointerException();
        }
        this.map = map;
        if (null != objectConverter) {
            paramReference = new WeakReference<OperationParameter>(objectConverter);
        } else {
            paramReference = null;
        }
    }

    @Override
    public String getClassName() {
        return "ScriptableMap";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String name, Scriptable start) {
        if (map.containsKey(name)) {
            return ScriptableWrapper.wrap(null != paramReference ? paramReference.get() : null, map.get(name));
        } else {
            return NOT_FOUND;
        }
    }

    @Override
    public Object get(int index, Scriptable start) {
        return get(Integer.toString(index), start);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return (map.containsKey(name));
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return has(Integer.toString(index), start);
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        try {
            map.put(name, Converter.convert(value));
        } catch (Exception e) {
            throw Context.reportRuntimeError("map prohibits modification");
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        put(Integer.toString(index), start, value);
    }

    @Override
    public void delete(String name) {
        try {
            map.remove(name);
        } catch (Exception e) {
            throw Context.reportRuntimeError("map prohibits modification");
        }
    }

    @Override
    public void delete(int index) {
        delete(Integer.toString(index));
    }

    @Override
    public Scriptable getPrototype() {
        /*
         * if (prototype == null) { // default if not explicitly set return
         * ScriptableObject.getClassPrototype(prototype, "Object"); } FIXME
         */
        return prototype;
    }

    @Override
    public void setPrototype(Scriptable prototype) {
        this.prototype = prototype;
    }

    @Override
    public Scriptable getParentScope() {
        return parent;
    }

    @Override
    public void setParentScope(Scriptable parent) {
        this.parent = parent;
    }

    @Override
    public Object[] getIds() {
        return map.keySet().toArray();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        if (hint == null || hint == String.class) {
            return "[object ScriptableMap]";
        } else if (hint == Number.class) {
            return Double.NaN;
        } else if (hint == Boolean.class) {
            return Boolean.TRUE;
        } else {
            return this;
        }
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return false; // no support for javascript instanceof
    }

    @Override
    public Object unwrap() {
        return map;
    }

    public String toString() {
        return map == null ? "null" : map.toString();
    }
}
