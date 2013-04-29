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

package org.forgerock.jaspi.container.initialisation;

import javax.security.auth.message.config.AuthConfigFactory;
import java.io.IOException;

/**
 * This interface is implemented by objects that can be used to load AuthConfigProviders from a persistent declarative
 * source and register them in an AuthConfigFactory.
 */
public interface AuthConfigProviderLoader {

    /**
     * Performs the loading and parsing of the AuthConfigProviders, to register, from a persistent declarative source
     * and registers them in the given AuthConfigFactory.
     *
     * @param authConfigFactory The AuthConfigFactory instance to register the AuthConfigProviders in.
     * @throws IOException If there is a problem loading the AuthConfigProvider registrations.
     */
    void loadAuthConfigProviders(AuthConfigFactory authConfigFactory) throws IOException;
}
