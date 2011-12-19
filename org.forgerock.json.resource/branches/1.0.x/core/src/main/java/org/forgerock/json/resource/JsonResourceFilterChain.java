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
import java.util.ArrayList;
import java.util.List;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

/**
 * Passes JSON resource requests through a list of filters and then to a target resource.
 * <p>
 * <strong>Note:</strong> This implementation is not synchronized. Modifying an active filter
 * chain can result in undefined behavior. If a filter chain needs to be modified, it is
 * recommended to create a copy, modify the, and then place it into service.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceFilterChain implements JsonResource {

    /** The list of filters to pass the request through. */
    protected final List<JsonResourceFilter> filters = new ArrayList<JsonResourceFilter>();

    /** The target resource to process the request once passed through the filters. */
    protected JsonResource resource;

    /**
     * Handles a JSON resource request by passing it through the filters and then to the
     * target resource.
     *
     * @param request the JSON resource request.
     * @return the JSON resource response.
     * @throws JsonResourceException if there is an exception handling the request.
     */
    @Override
    public JsonValue handle(JsonValue request) throws JsonResourceException {
        return new JsonResource() {
            private int cursor = 0;
            @Override public JsonValue handle(JsonValue request) throws JsonResourceException {
                int saved = cursor; // save position to restore after the call
                JsonValue response;
                try {
                    response = (cursor < filters.size() ?
                     filters.get(cursor++).filter(request, this) : resource.handle(request));
                } finally {
                    cursor = saved;
                }
                return response;
            }
        }.handle(request);
    }
}
