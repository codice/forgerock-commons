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

// Java SE
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Restlet API
import org.restlet.Request;
import org.restlet.data.ClientInfo;
import org.restlet.data.Form;

// Utilities
import org.forgerock.util.Factory;
import org.forgerock.util.LazyMap;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

/**
 * Represents a restlet request object as a HTTP request context.
 *
 * @author Paul C. Bryan
 */
class RestletRequestHttpContext {

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     */
    private static Map<String, Object> securityProperties(Request request) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        ClientInfo rci = request.getClientInfo();
        String name = null;
        {
            Principal user = rci.getUser();
            if (user != null) {
                name = user.getName();
            }
        }
        if (name == null) {
            List<Principal> principals = request.getClientInfo().getPrincipals();
            if (principals != null && principals.size() != 0) {
                Principal principal = principals.get(0);
                if (principal != null) {
                    name = principal.getName();
                }
            }
        }
        result.put("user", name);
        return result;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @param parent TODO.
     */
    public static JsonValue newContext(final Request request, final JsonValue parent) {
        return new JsonValue(new LazyMap<String, Object>(new Factory<Map<String, Object>>() {
            @Override public Map<String, Object> newInstance() {
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("type", "http");
                result.put("uuid", UUID.randomUUID().toString());
                result.put("parent", parent.getWrappedObject());
                result.put("security", securityProperties(request));
                result.put("method", request.getMethod().getName());
                result.put("path", request.getOriginalRef().getPath());
                result.put("query", lazyQuery(request));
                result.put("headers", lazyHeaders(request));
                return result;
            }
        }));
    }

    /**
     * TODO: Description.
     */
    private static Map<String, Object> lazyForm(final Form form) {
        return new LazyMap<String, Object>(new Factory<Map<String, Object>>() {
            @Override public Map<String, Object> newInstance() {
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                for (String name : form.getNames()) {
                    String[] values = form.getValuesArray(name);
                    if (values.length == 0) {
                        result.put(name, null);
                    } else if (values.length == 1) {
                        result.put(name, values[0]);
                    } else {
                        result.put(name, Arrays.asList(values));
                    }
                }
                return result;
            }
        });
    }

    /**
     * TODO: Description.
     */
    private static Map<String, Object> lazyQuery(final Request request) {
        Map<String, Object> result = null;
        if (request.getOriginalRef().getQuery() != null) {
            result = lazyForm(request.getResourceRef().getQueryAsForm());
        }
        return result;
    }

    /**
     * TODO: Description.
     */
    private static Map<String, Object> lazyHeaders(final Request request) {
        return lazyForm((Form)(request.getAttributes().get("org.restlet.http.headers")));
    }
}
