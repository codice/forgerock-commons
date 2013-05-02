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

package org.forgerock.jaspi.container.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.message.AuthException;
import javax.security.auth.message.module.ServerAuthModule;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the adding, removing and retrieving of registered JASPI Authentication Modules.
 */
public class AuthModuleManagerImpl implements AuthModuleManager {

    private final static Logger logger = LoggerFactory.getLogger(AuthModuleManagerImpl.class);

    private Map<String, List<String>> moduleChains = new ConcurrentHashMap<String, List<String>>();
    private Map<String, String> modules = new ConcurrentHashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    public List<ServerAuthModule> getModuleChain(String moduleChainName) throws AuthException {
        if (moduleChains.containsKey(moduleChainName)) {

            List<ServerAuthModule> serverAuthModules = new ArrayList<ServerAuthModule>();

            List<String> moduleChainModules = moduleChains.get(moduleChainName);

            for (String module : moduleChainModules) {
                serverAuthModules.add(constructServerAuthModule(modules.get(module)));
            }

            return serverAuthModules;
        } else {
            logger.warn("No module chains configured for {}", moduleChainName);
            throw new AuthException("No module chains configured for " + moduleChainName);
        }
    }

    /**
     * Constructs as instance of the ServerAuthModule.
     *
     * @param moduleClassName The modules fully qualified class name.
     * @return An instance of the Authentication module.
     * @throws AuthException If there is a problem constructing the Authentication Module.
     */
    private ServerAuthModule constructServerAuthModule(String moduleClassName) throws AuthException {

        try {
            Class<? extends ServerAuthModule> moduleClass = Class.forName(moduleClassName).asSubclass(
                    ServerAuthModule.class);

            Constructor<? extends ServerAuthModule> constructor = moduleClass.getConstructor();

            return constructor.newInstance();

        } catch (ClassNotFoundException e) {
            throw new AuthException("Could not instantiated the ServerAuthModule, " + moduleClassName);
        } catch (NoSuchMethodException e) {
            throw new AuthException("Could not instantiated the ServerAuthModule, " + moduleClassName);
        } catch (InvocationTargetException e) {
            throw new AuthException("Could not instantiated the ServerAuthModule, " + moduleClassName);
        } catch (InstantiationException e) {
            throw new AuthException("Could not instantiated the ServerAuthModule, " + moduleClassName);
        } catch (IllegalAccessException e) {
            throw new AuthException("Could not instantiated the ServerAuthModule, " + moduleClassName);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addModule(String moduleName, String moduleClassName) {
        modules.put(moduleName, moduleClassName);
    }

    /**
     * {@inheritDoc}
     */
    public void removeModule(String moduleName) throws ModuleManagerException {
        for (List<String> chainModules : moduleChains.values()) {
            for (String module : chainModules) {
                if (module.equals(moduleName)) {
                    throw new ModuleManagerException(MessageFormat.format("Cannot remove Module , {0}, "
                            + "as it currently referenced from existing Module Chains.", moduleName));
                }
            }
        }
        modules.remove(moduleName);
    }

    /**
     * {@inheritDoc}
     */
    public void addModuleChain(String moduleChainName, List<String> moduleNames)
            throws ModuleManagerException {
        for (String moduleName : moduleNames) {
            if (!modules.containsKey(moduleName)) {
                throw new ModuleManagerException(MessageFormat.format("Cannot add Module Chain, {0}, as it contains "
                        + "an invalid module name, {1}.", moduleChainName, moduleName));
            }
        }
        moduleChains.put(moduleChainName, moduleNames);
    }

    /**
     * {@inheritDoc}
     */
    public void removeModuleChain(String moduleChainName) {
        moduleChains.remove(moduleChainName);
    }
}
