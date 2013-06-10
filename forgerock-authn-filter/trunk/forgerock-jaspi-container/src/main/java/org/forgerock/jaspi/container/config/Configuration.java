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
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock Inc.
 */

package org.forgerock.jaspi.container.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Configuration {

    private Map<String, Map<String, Object>> authContexts = new HashMap<String, Map<String, Object>>();

    public AuthContextConfiguration addAuthContext(String authContextId) {
        return new AuthContextConfiguration(this, authContextId);
    }

    void addAuthContext(String authContextId, Map<String, Object> sessionModule, List<Map<String, Object>> authenticationModules) {

        Map<String, Object> authContext = new HashMap<String, Object>();

        if (sessionModule != null) {
            authContext.put("session-module", sessionModule);
        }

        authContext.put("auth-modules", authenticationModules);

        authContexts.put(authContextId, authContext);
    }

    public Set<String> keySet() {
        return authContexts.keySet();
    }

    public Map<String, Object> get(String authContextId) {
        return authContexts.get(authContextId);
    }
}
