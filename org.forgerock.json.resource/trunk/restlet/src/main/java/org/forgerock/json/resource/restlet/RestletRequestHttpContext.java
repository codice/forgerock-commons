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

// Restlet API
import org.restlet.Request;
import org.restlet.data.Form;

// Utilities
import org.forgerock.util.Factory;
import org.forgerock.util.LazyMap;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
class RestletRequestHttpContext extends LazyMap<String, Object> {

    /** TODO: Description. */
    private Request request;

    /**
     * TODO: Description.
     */
    public RestletRequestHttpContext(Request request, final Object parent) {
        this.request = request;
        this.factory = new Factory<Map<String, Object>>() {
            @Override public Map<String, Object> newInstance() {
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
                result.put("type", "http");
                result.put("parent", parent);
                result.put("method", RestletRequestHttpContext.this.request.getMethod().getName());
                result.put("path", RestletRequestHttpContext.this.request.getResourceRef().getRelativePart());
                result.put("query", lazyQuery());
                result.put("headers", lazyHeaders());
                return result;
            }
        };
    }

    /**
     * TODO: Description.
     */
    private Map<String, List<String>> lazyForm(final Form form) {
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
    private Map<String, List<String>> lazyQuery() {
        return lazyForm(request.getResourceRef().getQueryAsForm());
    }

    /**
     * TODO: Description.
     */
    private Map<String, List<String>> lazyHeaders() {
        return lazyForm((Form)(request.getAttributes().get("org.restlet.http.headers")));
    }
}
