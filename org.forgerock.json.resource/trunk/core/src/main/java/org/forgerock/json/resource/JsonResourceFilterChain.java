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
import org.forgerock.json.fluent.JsonValueException;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceFilterChain implements JsonResource {

    /** The list of filters to pass the request through. */
    protected final List<JsonResourceFilter> filters = new ArrayList<JsonResourceFilter>();

    /** The resource to dispatch the request to once passed through all filters. */
    protected JsonResource resource;

    /**
     * TODO: Description.
     *
     * @throws JsonResourceException if there is an exception handling the request.
     * @throws JsonValueException if the request is malformed.
     */
    @Override
    public JsonValue handle(JsonValue request) throws JsonResourceException, JsonValueException {
        return new JsonResource() {
            int cursor = 0;
            @Override public JsonValue handle(JsonValue request)
             throws JsonResourceException, JsonValueException {
                return (cursor < filters.size() ?
                 filters.get(cursor++).filter(request, this) : resource.handle(request));
            }
        }.handle(request);
    }
}
