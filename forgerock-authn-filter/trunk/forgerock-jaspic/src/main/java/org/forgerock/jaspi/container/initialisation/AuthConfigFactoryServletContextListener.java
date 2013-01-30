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

import org.forgerock.jaspi.container.AuthConfigFactoryImpl;

import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * On web container deployment creates an instance of the JASPI AuthConfigFactory and sets in the static singleton
 * factory so that it can be retrieved as required.
 */
public class AuthConfigFactoryServletContextListener implements ServletContextListener {

    /**
     * On context initialisation will set an implementation of the AuthConfigFactory.
     *
     * @param servletContextEvent The ServletContextEvent object.
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            AuthConfigFactory factory = new AuthConfigFactoryImpl();
            AuthConfigFactory.setFactory(factory);
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Not required to be implemented for this listener.
     *
     * @param servletContextEvent The ServletContextEvent object.
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
