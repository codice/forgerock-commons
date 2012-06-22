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

package org.forgerock.resource.client.impl;

// Java SE
import java.util.LinkedHashMap;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.resource.client.ResourceAccessor;
import org.forgerock.resource.exception.BadRequestException;
import org.forgerock.resource.exception.ResourceException;
import org.forgerock.resource.framework.JsonResourceProvider;
import org.forgerock.resource.framework.impl.JsonResourceContext;

/**
 * Wraps a JsonResource object and provides a set of convenience methods to use for accessing
 * it.
 *
 * @author Paul C. Bryan
 */
public class ResourceAccessorImpl implements ResourceAccessor {

    /** The resource to access through the accessor. */
    private JsonResourceProvider resource;

    /** The context to pass as the parent context for all requests. */
    private JsonValue context;

    /**
     * Contructs a new JSON resource accessor for the specified resource and request context.
     *
     * @param resource the resource to access through the accessor.
     * @param context the context to pass as the parent context for all requests.
     * @throws NullPointerException if {@code resource} is {@code null}.
     */
    public ResourceAccessorImpl(JsonResourceProvider resource, JsonValue context) {
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

    /* (non-Javadoc)
     * @see org.forgerock.resource.client.impl.ResourceAccessor#create(java.lang.String, org.forgerock.json.fluent.JsonValue)
     */
    @Override
    public JsonValue create(String id, JsonValue value) throws ResourceException {
        if (value == null) {
            throw new BadRequestException("Value passed in is null");
        }
        JsonValue request = newRequest("create", id);
        request.put("value", value.getObject());
        return resource.handle(request);
    }

    /* (non-Javadoc)
     * @see org.forgerock.resource.client.impl.ResourceAccessor#read(java.lang.String)
     */
    @Override
    public JsonValue read(String id) throws ResourceException {
        JsonValue request = newRequest("read", id);
        return resource.handle(request);
    }

    /* (non-Javadoc)
     * @see org.forgerock.resource.client.impl.ResourceAccessor#update(java.lang.String, java.lang.String, org.forgerock.json.fluent.JsonValue)
     */
    @Override
    public JsonValue update(String id, String rev, JsonValue value) throws ResourceException {
        if (value == null) {
            throw new BadRequestException("Value passed in is null");
        }
        JsonValue request = newRequest("update", id);
        if (rev != null) {
            request.put("rev", rev);
        }
        request.put("value", value.getObject());
        return resource.handle(request);
    }

    /* (non-Javadoc)
     * @see org.forgerock.resource.client.impl.ResourceAccessor#delete(java.lang.String, java.lang.String)
     */
    @Override
    public JsonValue delete(String id, String rev) throws ResourceException {
        JsonValue request = newRequest("delete", id);
        if (rev != null) {
            request.put("rev", rev);
        }
        return resource.handle(request);
    }

    /* (non-Javadoc)
     * @see org.forgerock.resource.client.impl.ResourceAccessor#patch(java.lang.String, java.lang.String, org.forgerock.json.fluent.JsonValue)
     */
    @Override
    public JsonValue patch(String id, String rev, JsonValue value) throws ResourceException {
        if (value == null) {
            throw new BadRequestException("Value passed in is null");
        }
        JsonValue request = newRequest("patch", id);
        if (rev != null) {
            request.put("rev", rev);
        }
        request.put("value", value.getObject());
        return resource.handle(request);
    }

    /* (non-Javadoc)
     * @see org.forgerock.resource.client.impl.ResourceAccessor#query(java.lang.String, org.forgerock.json.fluent.JsonValue)
     */
    @Override
    public JsonValue query(String id, JsonValue params) throws ResourceException {
        JsonValue request = newRequest("query", id);
        request.put("params", params.getObject());
        return resource.handle(request);
    }
    
    /* (non-Javadoc)
     * @see org.forgerock.resource.client.impl.ResourceAccessor#action(java.lang.String, org.forgerock.json.fluent.JsonValue, org.forgerock.json.fluent.JsonValue)
     */
    @Override
    public JsonValue action(String id, JsonValue params, JsonValue value) throws ResourceException {
        JsonValue request = newRequest("action", id);
        request.put("params", params.getObject());
        if (value != null) {
            request.put("value", value.getObject());
        }
        return resource.handle(request);
    }
}
