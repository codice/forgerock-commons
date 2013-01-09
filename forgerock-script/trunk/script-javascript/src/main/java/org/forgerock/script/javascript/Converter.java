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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

// Mozilla Rhino

// TODO: get rid of this; provide a replacement that just wraps the scriptable and exposes list/map interfaces

/**
 * Converts scriptable types provided by Rhino into standard Java objects.
 * 
 * @author Paul C. Bryan
 */
class Converter {

    private final long LONG_HIGH_BITS = 0xFFFFFFFF80000000L;

    private int longToInteger(Long number, Integer defaultTo) {
        if ((number & LONG_HIGH_BITS) == 0 || (number & LONG_HIGH_BITS) == LONG_HIGH_BITS) {
            return number.intValue();
        } else {
            return defaultTo;
        }
    }

    /**
     * Returns {@code true} if the specified number can be converted to an
     * integer without rounding.
     * 
     * @param number
     *            the number to be tested.
     * @return {@code true} if the number is an integer value.
     */
    private static boolean isInteger(Number number) {
        if (number instanceof Integer || number instanceof Long || number instanceof Byte) {
            return true;
        } else if (number instanceof Double || number instanceof Float) {
            double d = number.doubleValue();
            if ((d >= 0.0 && d == Math.floor(d)) || (d < 0 && d == Math.ceil(d))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns {@code true} if the specified scriptable object is a JavaScript
     * array.
     * 
     * @param scriptable
     *            the scriptable to be tested.
     * @return {@code true} if the scriptable is a JavaScript array.
     */
    private static boolean isArray(Scriptable scriptable) {
        while (scriptable != null) {
            if (scriptable.getClassName().equals("Array")) {
                return true;
            }
            scriptable = scriptable.getPrototype();
        }
        return false;
    }

    /**
     * Converts a value provided from JavaScript into a standard Java object.
     * Used when the script is attempting to assign a value to a supplied scope
     * or any properties/elements within. If the value is already suitable for
     * such assignment, it is returned unmolested. If the value is not suitable,
     * {@code null} is returned. This method performs recursive conversion for
     * any converted array elements or object properties.
     * 
     * @param value
     *            the value to be converted.
     * @return the value converted into standard Java object.
     */
    public static Object convert(Object value) {
        Object result = null;
        if (value == null || value == Context.getUndefinedValue() || value == Scriptable.NOT_FOUND) {
            result = null; // null is how undefined values are manifested
        } else if (value instanceof Double || value instanceof Float) { // coerce
                                                                        // to
                                                                        // integer
                                                                        // without
                                                                        // rounding
            Number number = (Number) value;
            result = (isInteger(number) ? Integer.valueOf(number.intValue()) : number);
        } else if (value instanceof Scriptable) { // javascript array or object
            Scriptable scriptable = (Scriptable) value;
            if (value instanceof Wrapper) {
                result = convert(((Wrapper) value).unwrap()); // recursive
            } else if (isArray(scriptable)) {
                Object o = scriptable.get("length", scriptable);
                if (o != null && o instanceof Number) {
                    int size = ((Number) o).intValue();
                    ArrayList<Object> list = new ArrayList<Object>(size);
                    for (int n = 0; n < size; n++) {
                        list.add(convert(scriptable.get(n, scriptable))); // recursive
                    }
                    result = list;
                }
            } else {
                HashMap<String, Object> map = new HashMap<String, Object>();
                for (Object id : scriptable.getIds()) {
                    String sid = id.toString();
                    Object object;
                    if (id instanceof Number && isInteger((Number) id)) {
                        object = scriptable.get(((Number) id).intValue(), scriptable);
                    } else {
                        object = scriptable.get(sid, scriptable);
                    }
                    map.put(sid, convert(object)); // recursive
                }
                result = map;
            }
        } else if (value instanceof Number || value instanceof String || value instanceof Boolean
                || value instanceof Map || value instanceof List) {
            result = value; // already valid JSON element
        }
        return result;
    }
}
