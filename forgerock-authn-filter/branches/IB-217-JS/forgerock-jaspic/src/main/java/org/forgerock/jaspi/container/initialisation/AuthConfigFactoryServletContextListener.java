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

import org.forgerock.jaspi.filter.AuthNFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * On web container deployment creates an instance of the JASPI AuthConfigFactory and sets in the static singleton
 * factory so that it can be retrieved as required. The class name of the provider to use can be specified in the
 * servlet context using a context-param with name "auth-config-provider-factory-class".
 *
 * The factory creation will cascade, causing the provider to read it's config by whatever means have been implemented.
 * In practice this means that the module configurations are read at this time.
 *
 */
public class AuthConfigFactoryServletContextListener implements ServletContextListener {

    public static final String FACTORY_CLASS = "auth-config-provider-factory-class";
    private Logger logger = LoggerFactory.getLogger(AuthNFilter.class);

    /**
     * On context initialisation will set an implementation of the AuthConfigFactory.
     *
     * @param servletContextEvent The ServletContextEvent object.
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        AuthConfigFactory factory = null;

        // See if the provider class has been set
        String providerClass = servletContextEvent.getServletContext().getInitParameter(FACTORY_CLASS);
        if (providerClass != null) {
            try {
                logger.debug("Trying to load AuthConfigFactory: " + providerClass);
                factory = (AuthConfigFactory) Class.forName(providerClass).newInstance();
            } catch (ClassNotFoundException e) {
                logger.error("Could not find class: " + providerClass, e);
            } catch (InstantiationException e) {
                logger.error("Could not instantiate object of class: " + providerClass, e);
            } catch (IllegalAccessException e) {
                logger.error("Class has no public noargs constructor: " + providerClass, e);
            }
        }

/*
        if (factory == null) {
            try {
                logger.debug("Loading default factory");
                factory = new AuthConfigFactoryImpl();
            } catch (AuthException e) {
                // TODO Jonathan: improve this
                throw new RuntimeException(e);
            }
        }
*/

        // Set the global factory
        AuthConfigFactory.setFactory(factory);
    }

    /**
     * Not required to be implemented for this listener.
     *
     * @param servletContextEvent The ServletContextEvent object.
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
