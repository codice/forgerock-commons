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

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.fluent.JsonValueException;

/**
 * Interface to filter JSON resource requests and/or JSON resource responses.
 *
 * @author Paul C. Bryan
 */
public interface JsonResourceFilter {

    /**
     * Filters the JSON resource request and/or JSON resource response. A filter is typically
     * included in a list within {@link JsonResourceFilterChain}. 
     * <p>
     * To pass the request to the next filter/handler in the chain, the filter calls
     * {@code next.handle(request)}. The response returned from the call can be filtered
     * before being returned by the filter.
     * <p>
     * A filter may elect not to pass the request to the next handler, instead handling the
     * request itself. The filter can also replace a response with response of its own after
     * the call to {@code next.handle(request)}.
     * <p>
     * @param request the JSON resource request.
     * @param next the next filter or resource in chain.
     * @return the JSON resource response.
     * @throws JsonResourceException if there is an exception handling the request.
     * @throws JsonValueException if the request is malformed.
     */
    JsonValue filter(JsonValue request, JsonResource next) throws JsonResourceException, JsonValueException;
}
