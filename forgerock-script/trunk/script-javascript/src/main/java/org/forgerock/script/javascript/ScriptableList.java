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

// Java Standard Edition

import java.util.List;

import org.forgerock.script.scope.AbstractFactory;
import org.forgerock.script.scope.Parameter;
import org.forgerock.util.LazyList;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

// Mozilla Rhino
// OpenIDM

/**
 * Provides a {@code Scriptable} wrapper for a {@code List} object.
 * 
 * Rhino 1.7R3 supports native List
 * 
 * @author Paul C. Bryan
 */
class ScriptableList extends IdScriptableObject implements Wrapper {

    /** The request being wrapped. */
    private final Parameter parameter;

    /** The list being wrapped. */
    private final List<Object> list;

    public ScriptableList(final AbstractFactory.ListFactory factory) {
        if (null == factory) {
            throw new NullPointerException();
        }
        this.list = new LazyList<Object>(factory);
        this.parameter = factory.getParameter();
    }

    /**
     * Constructs a new scriptable wrapper around the specified list.
     * 
     * @param list
     *            the list to be wrapped.
     * @throws NullPointerException
     *             if the specified list is {@code null}.
     */
    public ScriptableList(final Parameter operationParameter, final List<Object> list) {
        if (null == operationParameter) {
            throw new NullPointerException();
        }
        if (null == list) {
            throw new NullPointerException();
        }
        this.list = list;
        this.parameter = operationParameter;
    }

    /**
     * Attempts to resize the list to the specified size. If growing the list,
     * "sparse" elements with {@code null} value are added.
     * 
     * @param size
     *            the required size of the list.
     * @throws org.mozilla.javascript.EvaluatorException
     *             if the list could not be resized.
     */
    private void resize(int size) {
        while (list.size() < size) {
            try {
                list.add(null);
            } catch (Exception e) {
                throw Context.reportRuntimeError("list prohibits addition of null elements");
            }
        }
        while (list.size() > size) {
            try {
                list.remove(size);
            } catch (Exception e) {
                throw Context.reportRuntimeError("list prohibits element removal");
            }
        }
    }

    @Override
    public String getClassName() {
        return "ScriptableList";
    }

    @Override
    public Object get(String name, Scriptable start) {
        if ("length".equals(name)) {
            return Integer.valueOf(list.size());
        } else {
            return NOT_FOUND;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(int index, final Scriptable start) {
        if (this == start) {
            if (index >= 0 && index < list.size()) {
                return Converter.wrap(parameter, list.get(index), start, list instanceof LazyList);
            } else {
                return NOT_FOUND;
            }
        } else {
            return start.get(index, start);
        }
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return "length".equals(name); // length is only supported property
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return (index >= 0 && index < list.size());
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if ("length".equals(name)) {
            if (!(value instanceof Number)) {
                throw Context.reportRuntimeError("invalid array length");
            }
            int length = ((Number) value).intValue();
            if (length < 0) {
                throw Context.reportRuntimeError("invalid array length");
            }
            resize(length);
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        if (index <= 0) {
            throw Context.reportRuntimeError("index out of bounds");
        }
        if (list.size() < index + 1) {
            resize(index + 1); // "sparsely" allocate null elements if index
                               // exceeds size
        }
        value = Converter.convert(value);
        try {
            if (index < list.size()) {
                list.set(index, value);
            } else {
                list.add(value);
            }
        } catch (Exception e) {
            throw Context.reportRuntimeError("list prohibits modification");
        }
    }

    @Override
    public void delete(String name) {
        // attempt to delete any property is silently ignored
    }

    @Override
    public void delete(int index) {
        if (index >= 0 && index < list.size()) {
            try {
                list.set(index, null); // "sparse" allocation; does not remove
                                       // elements
            } catch (Exception e) {
                throw Context.reportRuntimeError("list prohibits modification");
            }
        }
    }

    @Override
    public Object[] getIds() {
        Object[] result = new Object[list.size()];
        for (int n = 0; n < result.length; n++) {
            result[n] = Integer.valueOf(n);
        }
        return result;
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        if (hint == null || hint == String.class) {
            return "[object ScriptableList]";
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
        return list;
    }

    public String toString() {
        return list == null ? "null" : list.toString();
    }
}
