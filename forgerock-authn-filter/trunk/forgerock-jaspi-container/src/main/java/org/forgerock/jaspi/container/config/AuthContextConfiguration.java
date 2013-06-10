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

import javax.security.auth.message.module.ServerAuthModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthContextConfiguration {

    private final Configuration configuration;
    private final String authContextId;

    private Map<String, Object> sessionModule;
    private final List<Map<String, Object>> authenticationModules = new ArrayList<Map<String, Object>>();

    public AuthContextConfiguration(Configuration configuration, String authContextId) {
        this.configuration = configuration;
        this.authContextId = authContextId;
    }


    public <T extends ServerAuthModule> AuthContextConfiguration setSessionModule(Class<T> sessionModuleClass,
            Map<String, Object> moduleProperties) {

        Map<String, Object> moduleProps = new HashMap<String, Object>(moduleProperties);
        moduleProps.put("class-name", sessionModuleClass.getCanonicalName());
        sessionModule = moduleProps;

        return this;
    }

    public <T extends ServerAuthModule> AuthContextConfiguration addAuthenticationModule(
            Class<T> authenticationModuleClass, Map<String, Object> moduleProperties) {

        Map<String, Object> moduleProps = new HashMap<String, Object>(moduleProperties);
        moduleProps.put("class-name", authenticationModuleClass.getCanonicalName());
        authenticationModules.add(moduleProps);

        return this;
    }

    public Configuration done() {

        configuration.addAuthContext(authContextId, sessionModule, authenticationModules);

        return configuration;
    }
}
