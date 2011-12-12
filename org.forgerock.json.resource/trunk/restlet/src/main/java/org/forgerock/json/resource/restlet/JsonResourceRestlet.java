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

// JSON Resource
import org.forgerock.json.resource.JsonResource;

/**
 * Wraps a JSON resource object, exposing it as a Restlet.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceRestlet extends Finder {

    /** TODO: Description. */
    private JsonResource resource;

    /**
     * TODO: Description.
     *
     * @param resource TODO.
     */
    public JsonResourceRestlet(JsonResource resource) {
        this.resource = resource;
        setTargetClass(JsonServerResource.class);
    }

    /**
     * Handles a restlet call.
     *
     * @param request the request to handle.
     * @param response the response to update.
     */
    @Override
    public void handle(Request request, Response response) {
        request.getAttributes().put(JsonResource.class.getName(), resource);
        super.handle(request, response);
    }
}
