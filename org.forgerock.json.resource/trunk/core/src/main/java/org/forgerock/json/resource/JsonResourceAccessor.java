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
 * it.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceAccessor {

    /** The resource to access through the accessor. */
    private JsonResource resource;

    /** The context to pass as the parent context for all requests. */
    private JsonValue context;

    /**
     * Contructs a new JSON resource accessor for the specified resource and request context.
     *
     * @param resource the resource to access through the accessor.
     * @param context the context to pass as the parent context for all requests.
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
     * Creates a new request context, populating it with the "type", "uuid", "parent",
     * "method" and "id" members.
     *
     * @param method the value of the "method" member to populate.
     * @param id the value of the "id" member to populate.
     * @return the newly created request context.
     */
    private JsonValue newRequest(String method, String id) {
        JsonValue request = JsonResourceContext.newContext("resource", this.context);
        request.put("method", method);
        request.put("id", id);
        return request;
    }

    /**
     * Creates a resource.
     *
     * @param id the requested identifier for the newly created resource.
     * @param value the value of the resource to create.
     * @throws JsonResourceException if there is an exception handling the request.
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
     * Reads a resource.
     *
     * @param id the identifier of the resource to read.
     * @throws JsonResourceException if there is an exception handling the request.
     */
    public JsonValue read(String id) throws JsonResourceException {
        JsonValue request = newRequest("read", id);
        return resource.handle(request);
    }

    /**
     * Updates a resource.
     *
     * @param id the identifier of the resource to update.
     * @param rev the current version of the resource, or {@code null} if not provided.
     * @param value the new value to updated the resource to.
     * @throws JsonResourceException if there is an exception handling the request.
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
     * Deletes a resource.
     *
     * @param id the identifier of the resource to delete.
     * @param rev the current version of the resource, or {@code null} if not provided.
     * @throws JsonResourceException if there is an exception handling the request.
     */
    public JsonValue delete(String id, String rev) throws JsonResourceException {
        JsonValue request = newRequest("delete", id);
        if (rev != null) {
            request.put("rev", rev);
        }
        return resource.handle(request);
    }

    /**
     * Applies a set of patches to a resource.
     *
     * @param id the identifier of the resource to patch.
     * @param rev the current version of the resource, or {@code null} if not provided.
     * @param value the patch document to apply to the resource.
     * @throws JsonResourceException if there is an exception handling the request.
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
     * Performs a query on a resource.
     *
     * @param id the identifier of the resource to query.
     * @param params the parameters to supply to the query.
     * @throws JsonResourceException if there is an exception handling the request.
     */
    public JsonValue query(String id, JsonValue params) throws JsonResourceException {
        JsonValue request = newRequest("query", id);
        request.put("params", params.getObject());
        return resource.handle(request);
    }
    
    /**
     * Performs an action on a resource.
     *
     * @param id the identifier of the resource to perform the action on.
     * @param params the parameters to supply to the action.
     * @param value the value to supply to the action; or {@code null} if no value.
     * @throws JsonResourceException if there is an exception handling the request.
     */
    public JsonValue action(String id, JsonValue params, JsonValue value) throws JsonResourceException {
        JsonValue request = newRequest("action", id);
        request.put("params", params.getObject());
        if (value != null) {
            request.put("value", value.getObject());
        }
        return resource.handle(request);
    }
}
