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

package org.forgerock.json.resource.restlet;

// Restlet
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

// JSON Resource
import org.forgerock.resource.framework.JsonResourceProvider;
import org.forgerock.resource.framework.impl.JsonResourceContext;

/**
 * A Restlet that dispatches requests to a JSON resource.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceRestlet extends Finder {

    /** Request attribute containing members to add to the HTTP context. */
    public static final String ATTRIBUTE_HTTP_CONTEXT = "org.forgerock.json.resource.context.http";

    /** The JSON resource to dispatch Restlet requests to. */
    private JsonResourceProvider resource;

    /**
     * Constructs a new Restlet that dispatching requests to a JSON resource.
     *
     * @param resource the JSON resource to dispatch Restlet requests to.
     */
    public JsonResourceRestlet(JsonResourceProvider resource) {
        this.resource = resource;
        setTargetClass(JsonServerResource.class);
    }

    /**
     * Handles a Restlet request.
     *
     * @param request the request to handle.
     * @param response the response to update.
     */
    @Override
    public void handle(Request request, Response response) {
        request.getAttributes().put(JsonResourceRestlet.class.getName(), this);
        super.handle(request, response);
    }

    /**
     * Returns the JSON resource that this Restlet dispatches requests to.
     */ 
    public JsonResourceProvider getResource() {
        return this.resource;
    }

    /**
     * Creates a new HTTP context object associated with an incoming Restlet request. Can be
     * overriden by an application to customize the context.
     *
     * @param request the Restlet request for which to create the new HTTP context object.
     * @return a new HTTP context object for the Restlet request.
     */
    public JsonValue newContext(Request request) {
        return RestletRequestHttpContext.newContext(request, JsonResourceContext.newRootContext());
    }
}
