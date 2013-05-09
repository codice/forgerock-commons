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

import javax.security.auth.message.AuthException;
import javax.security.auth.message.module.ServerAuthModule;
import java.util.List;

/**
 * Implementations of this interface will manage the adding, removing and retrieving of registered JASPI
 * Authentication Modules.
 */
public interface AuthModuleManager {

    /**
     * Gets the list of Authentication Modules configured for the module chain with the given name.
     *
     * @param moduleChainName The name of the module chain.
     * @return The list of modules for the chains.
     * @throws AuthException If there is a problem getting and constructing the modules.
     */
    List<ServerAuthModule> getModuleChain(String moduleChainName) throws AuthException;

    /**
     * Adds an authentication module to the internal store.
     *
     * @param moduleName The name of the Authentication Module.
     * @param moduleClassName The fully qualified class name of the Authentication Module.
     */
    void addModule(String moduleName, String moduleClassName);

    /**
     * Removes an authentication module from the internal store.
     *
     * @param moduleName The name of the Authentication Module to remove.
     * @throws ModuleManagerException If the Authentication module is being referenced by Module Chains.
     */
    void removeModule(String moduleName) throws ModuleManagerException;

    /**
     * Adds a chain of authentication modules to the internal store.
     *
     * @param moduleChainName The name of the Authentication Module chain.
     * @param moduleNames A List of Authentication module names.
     * @throws ModuleManagerException If a Authentication Module name is given which is not in the internal store.
     */
    void addModuleChain(String moduleChainName, List<String> moduleNames) throws ModuleManagerException;

    /**
     * Removes a chain of authentication modules from the internal store.
     *
     * @param moduleChainName The name of the Authentication Module chain.
     */
    void removeModuleChain(String moduleChainName);
}
