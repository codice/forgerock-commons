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

import org.forgerock.json.jwt.JwtBuilder;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

public class JwtSessionModule implements ServerAuthModule {

    private CallbackHandler handler;
    private Map options;

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
                           Map options) throws AuthException {
        this.handler = handler;
        this.options = options;
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
            throws AuthException {

        HttpServletRequest request = (HttpServletRequest)messageInfo.getRequestMessage();

        String sessionJwt = request.getHeader("session-jwt");

        if (sessionJwt != null && !"".equals(sessionJwt)) {

//            PlaintextJwt jwt = (PlaintextJwt) new JwtBuilder().recontructJwt(sessionJwt);



            //if all goes well!
            return AuthStatus.SUCCESS;
        }

        return AuthStatus.SEND_SUCCESS;  //TODO not sure which yet...
//        return AuthStatus.SEND_FAILURE;    //TODO maybe this one if only session is configured?...
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {

        for (Principal principal : serviceSubject.getPrincipals()) {
//            principal.
        }


        String sessionJwt = new JwtBuilder().jwt()
                .header("HEADER1", "H1")
                .content("CONTENT1", "C1")
                .build();

        HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();

        response.addHeader("session-jwt", sessionJwt);

        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
