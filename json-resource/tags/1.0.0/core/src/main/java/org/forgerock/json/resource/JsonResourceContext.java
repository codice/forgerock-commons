/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.resource;

// Java SE
import java.util.LinkedHashMap;
import java.util.UUID;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceContext {

    /**
     * Creates a new context.
     *
     * @param type the type of context to create.
     * @param parent the parent context to associate the new context with.
     * @return the newly created context.
     */
    public static JsonValue newContext(String type, JsonValue parent) {
        JsonValue context = new JsonValue(new LinkedHashMap<String, Object>());
        context.put("type", type);
        context.put("uuid", UUID.randomUUID().toString());
        context.put("parent", (parent == null ? null : parent.getWrappedObject()));
        return context;
    }

    /**
     * Creates and returns a new root context. By definition, a root context does not have
     * a parent.
     */
    public static JsonValue newRootContext() {
// TODO: timestamp (once date support in JSON Fluent is completed)
        return newContext("root", null);
    }

    /**
     * Returns the context of the specified type, above the current context. If no
     * such context exists, returns {@code null}.
     *
     * @param context TODO.
     * @param type TODO.
     */
    public static JsonValue getContext(JsonValue context, String type) {
        JsonValue result = null;
        while (context != null && context.isDefined("parent")) {
            context = context.get("parent");
            if (type.equals(context.get("type").getObject())) {
                result = context;
                break;
            }
        }
        return (result == null ? new JsonValue(null) : result);
    }

    /**
     * Returns the context at the root. This implementation traverses parent contexts
     * until there are no more parents, and returns the last parent.
     *
     * @param context the context in which to locate the root.
     * @return the root context.
     */
    public static JsonValue getRootContext(JsonValue context) {
        while (context != null) {
            JsonValue parent = context.get("parent");
            if (parent.isNull()) {
                break;
            }
            context = parent;
        }
        return (context == null ? new JsonValue(null) : context);
    }

    /**
     * Returns the parent context of the specified context, or {@code null} if the specified
     * context has no parent.
     *
     * @param context the context for which to return the parent.
     * @return the parent context, or {@code null} if no parent.
     */
    public static JsonValue getParentContext(JsonValue context) {
        JsonValue result = null;
        if (context != null && context.isDefined("parent")) {
            result = context.get("parent");
        }
        return (result == null ? new JsonValue(null) : result);
    }
}
