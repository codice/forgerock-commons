package org.forgerock.jaspi.modules;

import org.forgerock.jaspi.modules.wdsso.Base64;
import org.forgerock.jaspi.modules.wdsso.WDSSO;
import org.forgerock.openidm.jaspi.module.adpassthrough.ADPassthroughModule;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class IWAModule implements ServerAuthModule {

    private static final String IWA_FAILED = "iwa-failed";

    private CallbackHandler handler;
    private Map options;

    private ADPassthroughModule adPassthroughModule;

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
            Map options) throws AuthException {
        this.handler = handler;
        this.options = options;
        this.adPassthroughModule = new ADPassthroughModule();
        adPassthroughModule.initialize(requestPolicy, responsePolicy, handler, options);
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

        String xOpenIDMUsername = request.getHeader("X-OpenIDM-username");
        String xOpenIdmPassword = request.getHeader("X-OpenIDM-password");
        HttpSession session = request.getSession(false);
        boolean haveOpenIDMUsername = false;
        if (xOpenIDMUsername != null && xOpenIdmPassword != null) {
            haveOpenIDMUsername = xOpenIDMUsername != null;
        }
        if (!haveOpenIDMUsername && session != null) {
            haveOpenIDMUsername = session.getAttribute("openidm.username") != null;
        }
        if (haveOpenIDMUsername) {
            //skip straight to ad passthrough
            return adPassthroughModule.validateRequest(messageInfo, clientSubject, serviceSubject);
        }

        String httpAuthorization = request.getHeader("Authorization");

        if (httpAuthorization == null || "".equals(httpAuthorization)) {
//            DEBUG.message("Authorization Header not set in request.");

//            JsonValue jsonValue = JsonValueBuilder.jsonValue()
//                    .put("failure", true)
//                    .put("reason", IWA_FAILED)
//                    .build();
            response.addHeader("WWW-Authenticate", "Negotiate");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try {
                response.getWriter().write("{\"failure\":true,\"reason\":\"" + IWA_FAILED + "\"}");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                //TODO
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return AuthStatus.SEND_CONTINUE;
//            throw new RestAuthCallbackHandlerResponseException(Response.Status.UNAUTHORIZED,
//                    responseHeaders, jsonValue);
        } else {
            //TODO forward onto WDSSO logic to validate credentials
            String iwaUserName = null;
            try {
                iwaUserName = new WDSSO().process(options, request);
            } catch (RuntimeException e) {
                //TODO fallback to AD passthrough
                return adPassthroughModule.validateRequest(messageInfo, clientSubject, serviceSubject);
            }

            final String USERNAME_ATTRIBUTE = "openidm.username";
            final String USERID_ATTRIBUTE = "openidm.userid";
            final String ROLES_ATTRIBUTE = "openidm.roles";
            final String RESOURCE_ATTRIBUTE = "openidm.resource";
            final String OPENIDM_AUTHINVOKED = "openidm.authinvoked";

            String username = null;
//            String xOpenIDMUsername = request.getHeader("X-OpenIDM-username");
//            if (xOpenIDMUsername != null && "anonymous".equals(xOpenIDMUsername)) {
//                username = xOpenIDMUsername;
//            } else {
                username = iwaUserName.substring(0, iwaUserName.indexOf("@"));
//            }

            request.setAttribute(USERNAME_ATTRIBUTE, username);
//            request.setAttribute(USERID_ATTRIBUTE, options.get("userid"));//"%3CGUID%3D2d9010802060b040a6df6aaba3c9bdc3%3E");
//            request.setAttribute(ROLES_ATTRIBUTE, Arrays.asList(new String[]{"openidm-reg"}));
            request.setAttribute(RESOURCE_ATTRIBUTE, "system/AD/account");
            request.setAttribute(OPENIDM_AUTHINVOKED, "authnfilter");
//            messageInfo.setRequestMessage(new UserWrapper(request, username, null, Arrays.asList(new String[]{"openidm-reg"})));
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
