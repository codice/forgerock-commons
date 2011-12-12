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

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

/**
 * Wraps a JsonResource object and provides a set of convenience methods to use for accessing
 * it. Some assembly required. Batteries not included.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceAccessor {

    /** TODO: Description. */
    private JsonResource resource;

    /** TODO: Description. */
    private JsonValue context;

    /**
     * TODO: Description.
     *
     * @param resource TODO.
     * @param context TODO.
     * @throws NullPointerException if {@code resource} is {@code null}.
     */
    public JsonResourceAccessor(JsonResource resource, JsonValue context) {
        if (resource == null) {
            throw new NullPointerException();
        }
        this.resource = resource;
        this.context = context;
    }

    /**
     * TODO: Description.
     *
     * @param method TODO.
     * @param id TODO.
     */
    private JsonValue newRequest(String method, String id) {
        JsonValue request = JsonResourceContext.newContext("resource", this.context);
        request.put("method", method);
        request.put("id", id);
        return request;
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param value TODO.
     * @throws JsonResourceException TODO.
     */
    public JsonValue create(String id, JsonValue value) throws JsonResourceException {
        if (value == null) {
            throw JsonResourceException.BAD_REQUEST;
        }
        JsonValue request = newRequest("create", id);
        request.put("value", value.getObject());
        return resource.handle(request);
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @throws JsonResourceException TODO.
     */
    public JsonValue read(String id) throws JsonResourceException {
        JsonValue request = newRequest("read", id);
        return resource.handle(request);
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param rev TODO.
     * @param value TODO.
     * @throws JsonResourceException TODO.
     */
    public JsonValue update(String id, String rev, JsonValue value) throws JsonResourceException {
        if (value == null) {
            throw JsonResourceException.BAD_REQUEST;
        }
        JsonValue request = newRequest("update", id);
        if (rev != null) {
            request.put("rev", rev);
        }
        request.put("value", value.getObject());
        return resource.handle(request);
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param rev TODO.
     * @throws JsonResourceException TODO.
     */
    public JsonValue delete(String id, String rev) throws JsonResourceException {
        JsonValue request = newRequest("delete", id);
        if (rev != null) {
            request.put("rev", rev);
        }
        return resource.handle(request);
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param rev TODO.
     * @param value TODO.
     * @throws JsonResourceException TODO.
     */
    public JsonValue patch(String id, String rev, JsonValue value) throws JsonResourceException {
        if (value == null) {
            throw JsonResourceException.BAD_REQUEST;
        }
        JsonValue request = newRequest("patch", id);
        if (rev != null) {
            request.put("rev", rev);
        }
        request.put("value", value.getObject());
        return resource.handle(request);
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param params TODO.
     * @throws JsonResourceException TODO.
     */
    public JsonValue query(String id, JsonValue params) throws JsonResourceException {
        JsonValue request = newRequest("query", id);
        request.put("params", params.getObject());
        return resource.handle(request);
    }
    
    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param params TODO.
     * @param value TODO.
     * @throws JsonResourceException TODO.
     */
    public JsonValue action(String id, JsonValue params, JsonValue value) throws JsonResourceException {
        JsonValue request = newRequest("action", id);
        request.put("params", params.getObject());
        request.put("value", value.getObject());
        return resource.handle(request);
    }
}
