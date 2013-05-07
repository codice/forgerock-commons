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

package org.forgerock.jaspi.modules;

import org.forgerock.openidm.filter.AuthFilter;
import org.osgi.framework.FrameworkUtil;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ADPassthroughModule implements ServerAuthModule {

    /** Attribute in session containing authenticated username. */
    public static final String USERNAME_ATTRIBUTE = "openidm.username";

    /** Attribute in session containing authenticated userid. */
    public static final String USERID_ATTRIBUTE = "openidm.userid";

    /** Attribute in session and request containing assigned roles. */
    public static final String ROLES_ATTRIBUTE = "openidm.roles";

    /** Attribute in session containing user's resource (managed_user or internal_user) */
    public static final String RESOURCE_ATTRIBUTE = "openidm.resource";

    /** Attribute in request to indicate to openidm down stream that an authentication filter has secured the request */
    public static final String OPENIDM_AUTHINVOKED = "openidm.authinvoked";

    /** Authentication username header */
    public static final String HEADER_USERNAME = "X-OpenIDM-Username";

    /** Authentication password header */
    public static final String HEADER_PASSWORD = "X-OpenIDM-Password";

    /** Re-authentication password header */
    public static final String HEADER_REAUTH_PASSWORD = "X-OpenIDM-Reauth-Password";

    private CallbackHandler handler;

    public ADPassthroughModule() {

    }

    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        this.handler = handler;
    }

    /**
     * TODO
     *
     * this array must contain HttpServletRequest and HttpServletResponse
     *
     *
     * @return
     */

    public Class[] getSupportedMessageTypes() {
        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
    }

    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {

        HttpServletRequest request = (HttpServletRequest)messageInfo.getRequestMessage();
        HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();

        FrameworkUtil.getBundle(AuthFilter.class);
        AuthFilter authFilter = new AuthFilter();

        try {
            AuthFilter.AuthData authData = authFilter.authenticate(request, response);

            if (authData.getStatus()) {
                return AuthStatus.SEND_FAILURE;
            } else {
                return AuthStatus.SUCCESS;
            }

        } catch (IOException e) {
            return AuthStatus.SEND_FAILURE;
        } catch (org.forgerock.openidm.filter.AuthException e) {
            return AuthStatus.SEND_FAILURE;
        }


//        try {
//            if ("user1".equals(username)
//                //                && arePasswordsEqual("password123".toCharArray(), password)
//                    ) {
//                return AuthStatus.SUCCESS;
//            } else if ("user3".equals(username)) {
//                ((HttpServletResponse) messageInfo.getResponseMessage()).getWriter().write("validateResponse was " +
//                        "successful but has changed the response.");
//                return AuthStatus.SEND_SUCCESS;
//            } else if ("user4".equals(username)) {
//                ((HttpServletResponse) messageInfo.getResponseMessage()).getWriter().write("validateResponse has changed " +
//                        "the response as it needs more information from the client to be able to authenticate.");
//                return AuthStatus.SEND_CONTINUE;
//            }
//        } catch (IOException e) {
//
//        }
//
//        //user2
//        return AuthStatus.SEND_FAILURE;
    }

    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}