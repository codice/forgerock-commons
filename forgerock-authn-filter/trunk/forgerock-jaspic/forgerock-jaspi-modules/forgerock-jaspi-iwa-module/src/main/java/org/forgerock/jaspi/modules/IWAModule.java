package org.forgerock.jaspi.modules;

//import com.sun.identity.shared.encode.Base64;
//import org.forgerock.dev.filter.wdsso.WDSSO;
//import org.forgerock.jaspi.container.modules.openidm.wdsso.WDSSO;
//
//import javax.security.auth.Subject;
//import javax.security.auth.callback.CallbackHandler;
//import javax.security.auth.message.AuthException;
//import javax.security.auth.message.AuthStatus;
//import javax.security.auth.message.MessageInfo;
//import javax.security.auth.message.MessagePolicy;
//import javax.security.auth.message.module.ServerAuthModule;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Map;

public class IWAModule /*implements ServerAuthModule*/ {
//
//    private static final String IWA_FAILED = "iwa-failed";
//
//    private CallbackHandler handler;
//
//    @Override
//    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
//            Map options) throws AuthException {
//        this.handler = handler;
//    }
//
//    @Override
//    public Class[] getSupportedMessageTypes() {
//        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
//    }
//
//    @Override
//    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
//            throws AuthException {
//
//
//        HttpServletRequest request = (HttpServletRequest)messageInfo.getRequestMessage();
//        HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();
//
//        String httpAuthorization = request.getHeader("Authorization");
//
//        if (httpAuthorization == null || "".equals(httpAuthorization)) {
////            DEBUG.message("Authorization Header not set in request.");
//
////            JsonValue jsonValue = JsonValueBuilder.jsonValue()
////                    .put("failure", true)
////                    .put("reason", IWA_FAILED)
////                    .build();
//            response.addHeader("WWW-Authenticate", "Negotiate");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            try {
//                response.getWriter().write("{\"failure\":true,\"reason\":\"" + IWA_FAILED + "\", \"egtoken\":\"" + Base64.encode(new byte[]{0x60}/*"THISISATOKEN".getBytes("UTF-8")*/) + "\"}");
//                response.getWriter().flush();
//                response.getWriter().close();
//            } catch (IOException e) {
//                //TODO
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//
//            return AuthStatus.SEND_CONTINUE;
////            throw new RestAuthCallbackHandlerResponseException(Response.Status.UNAUTHORIZED,
////                    responseHeaders, jsonValue);
//        } else {
//            //TODO forward onto WDSSO logic to validate credentials
//            try {
//                new WDSSO().doIt(request);
//            } catch (IOException e) {
//                return AuthStatus.FAILURE;
//            }
//            return AuthStatus.SUCCESS;
//        }
//    }
//
//    @Override
//    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
//        return AuthStatus.SEND_SUCCESS;
//    }
//
//    @Override
//    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
}
