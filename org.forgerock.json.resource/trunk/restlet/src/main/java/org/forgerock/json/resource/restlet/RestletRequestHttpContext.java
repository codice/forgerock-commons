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
import org.restlet.data.Form;

// Utilities
import org.forgerock.util.Factory;
import org.forgerock.util.LazyMap;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

/**
 * TODO: Description.
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
        Map<String, Object> result = null;
        Principal user = request.getClientInfo().getUser();
        if (user != null) {
            String name = user.getName();
            if (name != null) {
                result = new LinkedHashMap<String, Object>();
                result.put("user", name);
            }
        }
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
                Map<String, Object> security = securityProperties(request);
                if (security != null) {
                    result.put("security", security);
                }
                result.put("method", request.getMethod().getName());
                result.put("path", request.getResourceRef().getRelativePart());
                result.put("query", lazyQuery(request));
                result.put("headers", lazyHeaders(request));
                return result;
            }
        }));
    }

    /**
     * TODO: Description.
     */
    private static Map<String, List<String>> lazyForm(final Form form) {
        return new LazyMap<String, List<String>>(new Factory<Map<String, List<String>>>() {
            @Override public Map<String, List<String>> newInstance() {
                LinkedHashMap<String, List<String>> result = new LinkedHashMap<String, List<String>>();
                for (String name : form.getNames()) {
                    result.put(name, Arrays.asList(form.getValuesArray(name)));
                }
                return result;
            }
        });
    }

    /**
     * TODO: Description.
     */
    private static Map<String, List<String>> lazyQuery(final Request request) {
        return lazyForm(request.getResourceRef().getQueryAsForm());
    }

    /**
     * TODO: Description.
     */
    private static Map<String, List<String>> lazyHeaders(final Request request) {
        return lazyForm((Form)(request.getAttributes().get("org.restlet.http.headers")));
    }
}
