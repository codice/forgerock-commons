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

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.servlet.HttpContext;
import org.forgerock.script.scope.Parameter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a {@code Scriptable} wrapper for an {@code HttpContext} object.
 * This is done specifically so we can join multiple values for headers or
 * parameters into a single, comma-separated String.
 */
class ScriptableHttpContext extends ScriptableContext implements Wrapper {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new scriptable wrapper around the specified context.
     *
     * @param context
     *            the context to be wrapped.
     * @throws NullPointerException
     *             if the specified context is {@code null}.
     */
    public ScriptableHttpContext(final Parameter parameter, final Context context) {
        super(parameter, context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String name, Scriptable start) {
        if (HttpContext.ATTR_HEADERS.equals(name) || HttpContext.ATTR_PARAMETERS.equals(name)) {
            // Join all header/parameter values for the same header/parameter into comma-separated-value String
            final JsonValue values = getWrappedContext().toJsonValue().get(name);
            final Map<String, String> map = new LinkedHashMap<String, String>();
            for (final String key : values.keys()) {
                final StringBuilder sb = new StringBuilder();
                for (final Object value : values.get(key).asList()) {
                    sb.append(value.toString());
                    sb.append(",");
                }
                map.put(key, sb.substring(0, sb.length() - 1));
            }
            return Converter.wrap(parameter, map, start, false);
        }
        else {
            return super.get(name, start);
        }
    }
}
