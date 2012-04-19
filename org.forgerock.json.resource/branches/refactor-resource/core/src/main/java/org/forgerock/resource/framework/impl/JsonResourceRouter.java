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

package org.forgerock.resource.framework.impl;

// Java SE
import java.util.LinkedHashMap;
import java.util.Map;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.fluent.JsonValueException;
import org.forgerock.resource.exception.BadRequestException;
import org.forgerock.resource.exception.NotFoundException;
import org.forgerock.resource.exception.ResourceException;
import org.forgerock.resource.framework.JsonResourceProvider;

/**
 * Routes requests to resources based on matching {@code id} prefix.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceRouter implements JsonResourceProvider {

    /**
     * Maps {@code id} prefixes to the resources to route to. An {@code id} matches if it
     * is equal to the prefix, or if it begins with the prefix separated by a '/' character.
     * This map permits a {@code null} key.
     */
    protected final Map<String, JsonResourceProvider> routes = new LinkedHashMap<String, JsonResourceProvider>();

    /**
     * Dispatches the JSON resource request to the matching resource. A resource matches if
     * its {@code id} is equal to or is prefixed with the the request {@code id} followed
     * by the {@code '/'} character. On a successful match, the {@code id} member in the JSON
     * resource request is modified to remove the matching resource prefix.
     *
     * @throws ResourceException if there is no resource to route to or the request is malformed.
     */
    @Override
    public JsonValue handle(JsonValue request) throws ResourceException {
        String id;
        try {
            id = request.get("id").asString();
        } catch (JsonValueException jve) {
            throw new BadRequestException("Id in request is invalid", jve);
        }
        JsonResourceProvider resource = routes.get(id); // try exact match first
        String child = null;
        if (resource == null && id != null) {
            int idLength = id.length();
            for (String key : routes.keySet()) { // LinkedHashMap for iteration performance
                if (key != null) {
                    int keyLength = key.length();
                    if (idLength > keyLength && id.charAt(keyLength) == '/' && id.startsWith(key)) {
                        resource = routes.get(key);
                        if (idLength > keyLength + 1) {
                            child = id.substring(keyLength + 1); // skip the prefix and slash
                        }
                        break;
                    }
                }
            }
        }
        if (resource == null) {
            throw new NotFoundException("No route for " + id);
        }
        request = request.clone();
        request.put("id", child); // "relativize" id; can be null
        return resource.handle(request);
    }
}
