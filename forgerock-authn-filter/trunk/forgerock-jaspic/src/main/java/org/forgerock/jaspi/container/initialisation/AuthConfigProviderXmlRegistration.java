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

import java.util.Map;

/**
 * A simple POJO to model the required information to create and register an AuthConfigProvider.
 */
public class AuthConfigProviderXmlRegistration {

    private final String className;
    private final Map<String, String> properties;
    private final String layer;
    private final String appContext;
    private final String description;

    /**
     * Constructs an instance of AuthConfigProviderXmlRegistration.
     *
     * @param className The fully qualified class name of the AuthConfigProvider.
     * @param properties A map of key pair properties to be used to create the AuthConfigProvider.
     * @param layer A String identifying the message layer for which the provider will be registered at the factory.
     *              A null value may be passed as an argument for this parameter, in which case the provider is
     *              registered at all layers.
     * @param appContext A String value that may be used by a runtime to request a configuration object from this
     *                   provider. A null value may be passed as an argument for this parameter, in which case the
     *                   provider is registered for all configuration ids (at the indicated layers).
     * @param description A text String describing the provider. This value may be null.
     */
    public AuthConfigProviderXmlRegistration(String className, Map<String, String> properties, String layer,
            String appContext, String description) {
        this.className = className;
        this.properties = properties;
        this.layer = layer;
        this.appContext = appContext;
        this.description = description;
    }

    /**
     * Gets the AuthConfigProvider's fully qualified class name.
     *
     * @return The AuthConfigProvider class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Gets the properties map to construct the AuthConfigProvider with.
     *
     * @return The key pair map.
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Gets the message layer to register the AuthConfigProvider at.
     *
     * @return The message layer.
     */
    public String getLayer() {
        return layer;
    }

    /**
     * Gets the application context to register the AuthConfigProvider at.
     *
     * @return Teh application context.
     */
    public String getAppContext() {
        return appContext;
    }

    /**
     * Gets the description of the AuthConfigProvider.
     *
     * @return The AuthConfigProvider's description.
     */
    public String getDescription() {
        return description;
    }
}
