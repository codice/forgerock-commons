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

package org.forgerock.jaspi.container.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.CertStoreCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.PasswordValidationCallback;
import javax.security.auth.message.callback.PrivateKeyCallback;
import javax.security.auth.message.callback.SecretKeyCallback;
import javax.security.auth.message.callback.TrustStoreCallback;

/**
 * Default CallbackHandler for Authentication modules to use to request more information about the request and
 * response messages from the JASPI Container.
 */
public class CallbackHandlerImpl implements CallbackHandler {

    private final static Logger logger = LoggerFactory.getLogger(CallbackHandlerImpl.class);

    /**
     * Called by Authentication modules to request more information about the request and response message.
     *
     * @param callbacks An array of Callback objects provided by the Authentication modules.
     * @throws UnsupportedCallbackException If a callback is passed which is not supported by this CallbackHandler.
     */
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {

        for (Callback callback : callbacks) {

            if (CallerPrincipalCallback.class.isAssignableFrom(callback.getClass())) {
                //TODO MUST implement - apart of another story, AME-520
                throw new UnsupportedCallbackException(callback);
            } else if (GroupPrincipalCallback.class.isAssignableFrom(callback.getClass())) {
                //TODO MUST implement - apart of another story, AME-520
                throw new UnsupportedCallbackException(callback);
            } else if (PasswordValidationCallback.class.isAssignableFrom(callback.getClass())) {
                //TODO MUST implement - apart of another story, AME-520
                throw new UnsupportedCallbackException(callback);
            } else if (CertStoreCallback.class.isAssignableFrom(callback.getClass())) {
                //SHOULD implement
                throw new UnsupportedCallbackException(callback);
            } else if (PrivateKeyCallback.class.isAssignableFrom(callback.getClass())) {
                //SHOULD implement
                throw new UnsupportedCallbackException(callback);
            } else if (SecretKeyCallback.class.isAssignableFrom(callback.getClass())) {
                //SHOULD implement
                throw new UnsupportedCallbackException(callback);
            } else if (TrustStoreCallback.class.isAssignableFrom(callback.getClass())) {
                //SHOULD implement
                throw new UnsupportedCallbackException(callback);
            } else if (HttpCallback.class.isAssignableFrom(callback.getClass())) {
                //SHOULD implement
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}
