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

import org.forgerock.jaspi.container.AuthConfigFactoryImpl;
import org.forgerock.jaspi.container.AuthConfigProviderImpl;
import org.forgerock.jaspi.container.ServerAuthConfigImpl;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;

/**
 * Responsible for configuring the Auth Contexts for the Authentication Filter.
 */
public final class ConfigurationManager {

    private static boolean configured = false;

    /**
     * Private default constructor.
     */
    private ConfigurationManager() {
    }

    /**
     * Configures the Authentication Filter with the given Auth Context configuration.
     *
     * Can only be called once, unless the unconfigure method is called.
     *
     * @param authContextConfiguration The Configuration object containing the Auth Context configuration.
     * @throws AuthException If there is a problem configuring the Authentication Filter.
     */
    public static synchronized void configure(Configuration authContextConfiguration) throws AuthException {

        if (!configured) {
            CallbackHandler callbackHandler = null;
            ServerAuthConfigImpl serverAuthConfig = new ServerAuthConfigImpl(null, null, callbackHandler);

            for (String authContextId : authContextConfiguration.keySet()) {
                serverAuthConfig.registerAuthContext(authContextId, authContextConfiguration.get(authContextId));
            }

            // Now assemble the factory-provider-config-context-module structure
            AuthConfigFactory authConfigFactory = AuthConfigFactoryImpl.getInstance();
            AuthConfigProviderImpl authConfigProvider = new AuthConfigProviderImpl(null, null);
            authConfigProvider.setServerAuthConfig(serverAuthConfig);
            authConfigFactory.registerConfigProvider(authConfigProvider, null, null, null);

            configured = true;
        } else {
            throw new AuthException("JASPI Authn Filter already configured.");
        }
    }

    /**
     * Sets the configured flag to false.
     */
    public static synchronized void unconfigure() {
        configured = false;
    }
}
