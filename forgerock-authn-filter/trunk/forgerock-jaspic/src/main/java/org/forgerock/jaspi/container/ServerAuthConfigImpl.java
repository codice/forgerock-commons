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

package org.forgerock.jaspi.container;

import org.forgerock.jaspi.container.modules.AuthModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import java.util.List;
import java.util.Map;

/**
 * Defines the configuration of ServerAuthModules at a given message layer, and for a particular application context.
 */
public class ServerAuthConfigImpl implements ServerAuthConfig {

    private final static Logger logger = LoggerFactory.getLogger(ServerAuthConfigImpl.class);

    private final String layer;
    private final String appContext;
    private final CallbackHandler handler;

    private AuthModuleManager authModuleManager;

    /**
     * Constructs an instance of ServerAuthConfigImpl.
     *
     * @param layer A String identifying the message layer for the returned ServerAuthConfig object. This argument must
     *              not be null.
     * @param appContext A String that identifies the messaging context for the returned ServerAuthConfig object. This
     *                   argument must not be null.
     * @param handler A CallbackHandler to be passed to the ServerAuthModules encapsulated by ServerAuthContext objects
     *                derived from the returned ServerAuthConfig. The CallbackHandler assigned to the configuration
     *                must support the Callback objects required to be supported by the Servlet Container profile of
     *                this specification being followed by the messaging runtime. The CallbackHandler instance must
     *                be initialized with any application context needed to process the required callbacks on behalf
     *                of the corresponding application.
     */
    public ServerAuthConfigImpl(String layer, String appContext, CallbackHandler handler) {
        this.layer = layer;
        this.appContext = appContext;
        this.handler = handler;
    }

    /**
     * Sets the AuthModuleManager to be used to get the Authentication Modules.
     *
     * @param authModuleManager An instance of the AuthModuleManager.
     */
    public void setAuthModuleManager(AuthModuleManager authModuleManager) {
        this.authModuleManager = authModuleManager;
    }

    /**
     * Gets a ServerAuthContextImpl instance, initialised with the Authentication modules defined by the
     * authContextID and a map of property key pairs.
     *
     * @param authContextID An identifier used to index the provided config, or null. This value must be identical to
     *                      the value returned by the getAuthContextID method for all MessageInfo objects passed to the
     *                      validateRequest method of the returned ServerAuthContext.
     * @param serviceSubject A Subject that represents the source of the service response to be secured by the acquired
     *                       authentication context. A null value may be passed for this parameter.
     * @param properties A Map object that may be used by the caller to augment the properties that will be passed to
     *                   the encapsulated modules at module initialization. The null value may be passed for this
     *                   parameter.
     * @return A ServerAuthContext instance that encapsulates the ServerAuthModules used to secure and validate
     *          requests/responses associated with the given authContextID, or null (indicating that no modules are
     *          configured).
     * @throws AuthException If this method fails.
     */
    public ServerAuthContext getAuthContext(String authContextID, Subject serviceSubject, Map properties)
            throws AuthException {

        List<ServerAuthModule> serverAuthModules = authModuleManager.getModuleChain(authContextID);

        if (serverAuthModules == null || serverAuthModules.size() == 0) {
            return null;
        }

        return new ServerAuthContextImpl(serverAuthModules, createRequestMessagePolicy(), null, properties, handler);
    }

    /**
     * Creates a MessagePolicy instance for the Request Policy, using the AUTHENTICATE_SENDER ProtectionPolicy and
     * ensuring the message policy is mandatory.
     *
     * @return A MessagePolicy.
     */
    private MessagePolicy createRequestMessagePolicy() {
        MessagePolicy.Target[] targets = new MessagePolicy.Target[]{};
        MessagePolicy.ProtectionPolicy protectionPolicy = new ProtectionPolicyImpl();
        MessagePolicy.TargetPolicy targetPolicy = new MessagePolicy.TargetPolicy(targets, protectionPolicy);
        MessagePolicy.TargetPolicy[] targetPolicies = new MessagePolicy.TargetPolicy[]{targetPolicy};
        return new MessagePolicy(targetPolicies, true);
    }

    /**
     * Get the message layer name of this authentication context configuration object.
     *
     * @return The message layer name of this configuration object, or null if the configuration object pertains to an
     *          unspecified message layer.
     */
    public String getMessageLayer() {
        return layer;
    }

    /**
     * Get the application context identifier of this authentication context configuration object.
     *
     * @return The String identifying the application context of this configuration object, or null if the
     *          configuration object pertains to an unspecified application context.
     */
    public String getAppContext() {
        return appContext;
    }

    /**
     * Get the authentication context identifier corresponding to the request and response objects encapsulated in
     * messageInfo.
     *
     * @param messageInfo A contextual Object that encapsulates the client request and server response objects.
     * @return The authentication context identifier corresponding to the encapsulated request and response objects,
     *          or null.
     */
    public String getAuthContextID(MessageInfo messageInfo) {
        return (String) messageInfo.getMap().get("moduleChain");
    }

    /**
     * No state to refresh so this method does nothing.
     */
    public void refresh() {
        // No state to refresh
    }

    /**
     * Used to determine whether the authentication context configuration object encapsulates any protected
     * authentication contexts.
     *
     * @return false.
     */
    public boolean isProtected() {
        return false;
    }

    /**
     * Represents a ProtectionPolicy for a message authentication policy.
     */
    static class ProtectionPolicyImpl implements MessagePolicy.ProtectionPolicy {

        /**
         * Gets the ProtectionPolicy identifier.
         *
         * @return AUTHENTICATE_SENDER ProtectionPolicy.
         */
        public String getID() {
            return MessagePolicy.ProtectionPolicy.AUTHENTICATE_SENDER;
        }
    }
}
