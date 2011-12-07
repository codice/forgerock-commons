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
import java.util.HashMap;
import java.util.Map;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.fluent.JsonValueException;

/**
 * Routes requests to resources based on matching {@code id} prefix.
 *
 * @author Paul C. Bryan
 */
public class JsonResourceIdRouter implements JsonResource {

    /**
     * Maps {@code id} prefixes to the resources to route to. An {@code id} matches if it
     * is equal to the prefix, or if it begins with the prefix separated by a '/' character.
     * This map permits a {@code null} key.
     */
    protected final Map<String, JsonResource> routes = new HashMap<String, JsonResource>();

    /**
     * Dispatches the JSON resource request to the matching resource. A resource matches if
     * its {@code id} is equal to or is prefixed with the the request {@code id} followed
     * by the {@code '/'} character. On a successful match, the {@code id} member in the JSON
     * resource request is modified to remove the matching resource prefix.
     *
     * @throws JsonResourceException if there is no resource to route to.
     * @throws JsonValueException if the request is malformed.
     */
    @Override
    public JsonValue handle(JsonValue request) throws JsonResourceException, JsonValueException {
        String id = request.get("id").asString();
        JsonResource resource = routes.get(id); // try exact match first
        String child = null;
        if (resource == null && id != null) {
            int idLength = id.length();
            for (String key : routes.keySet()) {
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
            throw new JsonResourceException(JsonResourceException.NOT_FOUND, "No route for " + id);
        }
        request.put("id", child); // modify id in request; can be null
        return resource.handle(request);
    }
}
