package org.forgerock.jaspi.modules;

import org.forgerock.jaspi.modules.wdsso.WDSSO;

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
import java.security.Principal;
import java.util.Map;

public class IWAModule implements ServerAuthModule {

    private static final String IWA_FAILED = "iwa-failed";

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
        HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();

        String httpAuthorization = request.getHeader("Authorization");

        if (httpAuthorization == null || "".equals(httpAuthorization)) {
//            DEBUG.message("Authorization Header not set in request."); //TODO logging

            response.addHeader("WWW-Authenticate", "Negotiate");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                response.getWriter().write("{\"failure\":true,\"reason\":\"" + IWA_FAILED + "\"}");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                //TODO logging
                throw new AuthException("Error writing to Response");
            }

            return AuthStatus.SEND_CONTINUE;
        } else {
            //TODO forward onto WDSSO logic to validate credentials
            try {
                final String username = new WDSSO().process(options, request);

                clientSubject.getPrincipals().add(new Principal() {
                    public String getName() {
                        return username;
                    }
                });
            } catch (RuntimeException e) {
                // TODO logging
                throw new AuthException("IWA has failed");
            }


            return AuthStatus.SUCCESS;
        }
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
