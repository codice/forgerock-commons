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

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads AuthConfigProvider registrations from a persistent xml file on the classpath and registers them in the
 * AuthConfigFactory.
 */
public class AuthConfigProviderFileLoader extends AbstractXmlFileParser<Collection<AuthConfigProviderXmlRegistration>>
        implements AuthConfigProviderLoader {

    private static final String AUTH_CONFIG_PROVIDER_CONFIG_FILE_NAME = "providerConfiguration.xml";

    /**
     * {@inheritDoc}
     */
    public void loadAuthConfigProviders(AuthConfigFactory authConfigFactory) throws IOException {
        loadAuthConfigProviders(authConfigFactory, AUTH_CONFIG_PROVIDER_CONFIG_FILE_NAME);
    }

    /**
     * Parses the provider registration xml file and constructs instances of each AuthConfigProvider and registers
     * each one in the AuthConfigFactory.
     *
     * @param authConfigFactory The AuthConfigFactory instance.
     * @param configFileName The name of the provider registration xml file.
     * @throws IOException If there is a problem reading or parsing the xml file.
     */
    private void loadAuthConfigProviders(AuthConfigFactory authConfigFactory, String configFileName)
            throws IOException {

        try {
            Collection<AuthConfigProviderXmlRegistration> providerRegistrations = parseXmlFile(configFileName);

            for (AuthConfigProviderXmlRegistration providerReg : providerRegistrations) {
                authConfigFactory.registerConfigProvider(providerReg.getClassName(), providerReg.getProperties(),
                        providerReg.getLayer(), providerReg.getAppContext(), providerReg.getDescription());
            }
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<AuthConfigProviderXmlRegistration> walk(Node node) {

        List<AuthConfigProviderXmlRegistration> providerRegistrations = new
                ArrayList<AuthConfigProviderXmlRegistration>();

        AuthConfigProviderXmlRegistration providerRegistration = null;

        int type = node.getNodeType();
        String nodeName = node.getNodeName();
        switch(type) {
        case Node.ELEMENT_NODE: {

            if ("AuthConfigProviders".equals(nodeName)) {
                //nothing to do here
            } else if ("AuthConfigProvider".equals(nodeName)) {
                String className = getAttribute(node, "className");
                String propertiesString = getAttribute(node, "properties");
                String layer = getAttribute(node, "layer");
                String appContext = getAttribute(node, "appContext");
                String description = getAttribute(node, "description");
                Map<String, String> properties = new HashMap<String, String>();

                if (propertiesString.contains(",")) {
                    String[] propertyStrings = propertiesString.split(",");
                    for (String propertyString : propertyStrings) {
                        String[] propertyPair = propertyString.split("=");
                        properties.put(propertyPair[0], propertyPair[1]);
                    }
                }

                providerRegistration = new AuthConfigProviderXmlRegistration(className, properties, layer,
                        appContext, description);
            }
            break;
        }
        default: {
            break;
        }
        }

        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            providerRegistrations.addAll(walk(child));
        }

        //without this the ending tags will miss
        if (type == Node.ELEMENT_NODE) {
            if (nodeName.equals("AuthConfigProvider")) {
                providerRegistrations.add(providerRegistration);
            }
        }

        return providerRegistrations;
    }
}
