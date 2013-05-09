//package org.forgerock.jaspi.container.modules.openidm.wdsso;
//
//import com.sun.identity.authentication.util.DerValue;
//import com.sun.identity.shared.encode.Base64;
//import org.ietf.jgss.GSSContext;
//import org.ietf.jgss.GSSCredential;
//import org.ietf.jgss.GSSException;
//import org.ietf.jgss.GSSManager;
//import org.ietf.jgss.GSSName;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.security.PrivilegedActionException;
//import java.util.Arrays;
//
//public class WDSSO {
//
//    private static final String amAuthWindowsDesktopSSO =
//            "amAuthWindowsDesktopSSO";
//
////    private static final String[] configAttributes = {
////            "iplanet-am-auth-windowsdesktopsso-principal-name",
////            "iplanet-am-auth-windowsdesktopsso-keytab-file",
////            "iplanet-am-auth-windowsdesktopsso-kerberos-realm",
////            "iplanet-am-auth-windowsdesktopsso-kdc",
////            "iplanet-am-auth-windowsdesktopsso-returnRealm",
////            "iplanet-am-auth-windowsdesktopsso-lookupUserInRealm",
////            "iplanet-am-auth-windowsdesktopsso-auth-level",
////            "serviceSubject" };
//
////    private static final int PRINCIPAL = 0;
////    private static final int KEYTAB    = 1;
////    private static final int REALM     = 2;
////    private static final int KDC       = 3;
////    private static final int RETURNREALM = 4;
////    private static final int LOOKUPUSER = 5;
////    private static final int AUTHLEVEL = 6;
////    private static final int SUBJECT   = 7;
//
////    private static Hashtable configTable = new Hashtable();
////    private Principal userPrincipal = null;
////    private Subject serviceSubject = null;
////    private String servicePrincipalName = null;
////    private String keyTabFile = null;
////    private String kdcRealm   = null;
////    private String kdcServer  = null;
////    private boolean returnRealm = false;
////    private String authLevel  = null;
////    private Map options    = null;
////    private String confIndex  = null;
////    private boolean lookupUserInRealm = false;
//
////    private Debug debug = Debug.getInstance(amAuthWindowsDesktopSSO);
//
//    public void doIt(HttpServletRequest request) throws IOException {
//
//        // retrieve the spnego token
//        byte[] spnegoToken = getSPNEGOTokenFromHTTPRequest(request);
//
//        if (spnegoToken == null) {
////            debug.error("spnego token is not valid.");
////            throw new AuthLoginException(amAuthWindowsDesktopSSO, "token",null);//TODO
//            throw new RuntimeException();
//        }
//
////        if (debug.messageEnabled()) {
////            debug.message("SPNEGO token: \n" +
////                    DerValue.printByteArray(spnegoToken, 0, spnegoToken.length));
////        }
//        // parse the spnego token and extract the kerberos mech token from it
//        final byte[] kerberosToken = parseToken(spnegoToken);
//        if (kerberosToken == null) {
////            debug.error("kerberos token is not valid.");
////            throw new AuthLoginException(amAuthWindowsDesktopSSO, "token",null);//TODO
//            throw new RuntimeException();
//        }
////        if (debug.messageEnabled()) {
////            debug.message("Kerberos token retrieved from SPNEGO token: \n" +
////                    DerValue.printByteArray(kerberosToken,0,kerberosToken.length));
////        }
//
//        // authenticate the user with the kerberos token
//        try {
//            authenticateToken(kerberosToken);
////            if (debug.messageEnabled()){
////                debug.message("WindowsDesktopSSO kerberos authentication passed succesfully.");
////            }
////            result = ISAuthConstants.LOGIN_SUCCEED;//TODO All Good here!!
//        } catch (PrivilegedActionException pe) {
//            Exception e = extractException(pe);
//            if( e instanceof GSSException) {
//                int major = ((GSSException)e).getMajor();
//                if (major == GSSException.CREDENTIALS_EXPIRED) {
////                    debug.message("Credential expired. Re-establish credential...");
//                    try {
//                        authenticateToken(kerberosToken);
////                        if (debug.messageEnabled()){
////                            debug.message("Authentication succeeded with new cred.");
////                            result = ISAuthConstants.LOGIN_SUCCEED;//TODO All Good here!!
////                        }
//                    } catch (Exception ee) {
////                        debug.error("Authentication failed with new cred.");
////                        throw new AuthLoginException(amAuthWindowsDesktopSSO, "auth", null, ee);//TODO
//                        throw new RuntimeException();
//                    }
//                } else {
////                    debug.error("Authentication failed with GSSException.");
////                    throw new AuthLoginException(amAuthWindowsDesktopSSO, "auth", null, e);//TODO
//                    throw new RuntimeException();
//                }
//            }
//        } catch (GSSException e ){
//            int major = e.getMajor();
//            if (major == GSSException.CREDENTIALS_EXPIRED) {
////                debug.message("Credential expired. Re-establish credential...");
//                try {
//                    authenticateToken(kerberosToken);
////                    if (debug.messageEnabled()){
////                        debug.message("Authentication succeeded with new cred.");
////                        result = ISAuthConstants.LOGIN_SUCCEED;//TODO All Good here!!
////                    }
//                } catch (Exception ee) {
////                    debug.error("Authentication failed with new cred.");
////                    throw new AuthLoginException(amAuthWindowsDesktopSSO, "auth", null, ee);//TODO
//                    throw new RuntimeException();
//                }
//            } else {
////                debug.error("Authentication failed with GSSException.");
////                throw new AuthLoginException(amAuthWindowsDesktopSSO, "auth", null, e);//TODO
//                throw new RuntimeException();
//            }
////        } catch (AuthLoginException e) {
////            throw e;
//        } catch (Exception e) {
////            debug.error("Authentication failed with generic exception.");
////            throw new AuthLoginException(amAuthWindowsDesktopSSO, "auth", null, e);//TODO
//            throw new RuntimeException();
//        }
////        return result;
//    }
//
//    private void authenticateToken(final byte[] kerberosToken)
//            throws GSSException, Exception {
//
////        debug.message("In authenticationToken ...");
////        Subject.doAs(serviceSubject, new PrivilegedExceptionAction(){
////            public Object run() throws Exception {
//                GSSContext context =
//                        GSSManager.getInstance().createContext(
//                                (GSSCredential)null);
////                if (debug.messageEnabled()){
////                    debug.message("Context created.");
////                }
//                byte[] outToken = context.acceptSecContext(
//                        kerberosToken, 0,kerberosToken.length);
////                if (outToken != null) {
////                    if (debug.messageEnabled()) {
////                        debug.message(
////                                "Token returned from acceptSecContext: \n"
////                                        + DerValue.printByteArray(
////                                        outToken, 0, outToken.length));
////                    }
////                }
//                if (!context.isEstablished()) {
////                    debug.error("Cannot establish context !");
////                    throw new AuthLoginException(amAuthWindowsDesktopSSO, "context", null);//TODO
//                } else {
////                    if (debug.messageEnabled()) {
////                        debug.message("Context established !");
////                    }
//                    GSSName user = context.getSrcName();
//
//                    // Check if the user account from the Kerberos ticket exists
//                    // in the realm. The "Alias Search Attribute Names" will be used to
//                    // perform the search.
////                    if (lookupUserInRealm) {
////                        String org = getRequestOrg();
////                        String userValue = getUserName(user.toString());
////                        String userName = searchUserAccount(userValue, org);
////                        if (userName != null && !userName.isEmpty()) {
////                            storeUsernamePasswd(userValue, null);
////                        } else {
////                            String data[] = {userValue, org};
////                            debug.error("WindowsDesktopSSO.authenticateToken: "
////                                    + ": Unable to find the user " + userValue);
////                            throw new AuthLoginException(amAuthWindowsDesktopSSO, "notfound", data);//TODO
////                        }
////                    }
//
////                    if (debug.messageEnabled()){
////                        debug.message("WindowsDesktopSSO.authenticateToken:"
////                                + "User authenticated: " + user.toString());
////                    }
//                    if (user != null) {
////                        setPrincipal(user.toString());
//                    }
//                }
//                context.dispose();
////                return null;
////            }
////        });
//    }
//
//    /**
//     * Iterate until we extract the real exception
//     * from PrivilegedActionException(s).
//     */
//    private static Exception extractException(Exception e) {
//        while (e instanceof PrivilegedActionException) {
//            e = ((PrivilegedActionException)e).getException();
//        }
//        return e;
//    }
//
////    private String getUserName(String user) {
////        String userName = user;
////        if (!returnRealm) {
////            int index = user.indexOf("@");
////            if (index != -1) {
////                userName = user.toString().substring(0, index);
////            }
////        }
////        return userName;
////    }
//
//    private static byte[] spnegoOID = {
//            (byte)0x06, (byte)0x06, (byte)0x2b, (byte)0x06, (byte)0x01,
//            (byte)0x05, (byte)0x05, (byte)0x02 };
//
//    // defined but not used.
//    private static byte[] MS_KERBEROS_OID =  {
//            (byte)0x06, (byte)0x09, (byte)0x2a, (byte)0x86, (byte)0x48,
//            (byte)0x82, (byte)0xf7, (byte)0x12, (byte)0x01, (byte)0x02,
//            (byte)0x02 };
//    private static byte[] KERBEROS_V5_OID = {
//            (byte)0x06, (byte)0x09, (byte)0x2a, (byte)0x86, (byte)0x48,
//            (byte)0x86, (byte)0xf7, (byte)0x12, (byte)0x01, (byte)0x02,
//            (byte)0x02 };
//
//    private byte[] getSPNEGOTokenFromHTTPRequest(HttpServletRequest req) {
//        byte[] spnegoToken = null;
//        String header = req.getHeader("Authorization");
//        if ((header != null) && header.startsWith("Negotiate")) {
//            header = header.substring("Negotiate".length()).trim();
//            try {
//                spnegoToken = Base64.decode(header);
//            } catch (Exception e) {
////                debug.error("Decoding token error.");
////                if (debug.messageEnabled()) {
////                    debug.message("Stack trace: ", e);
////                }
//            }
//        }
//        return spnegoToken;
//    }
//
////    private byte[] getSPNEGOTokenFromCallback(Callback[] callbacks) {
////        byte[] spnegoToken = null;
////        if (callbacks != null && callbacks.length != 0) {
////            String spnegoTokenStr =
////                    ((HttpCallback)callbacks[0]).getAuthorization();
////            try {
////                spnegoToken = Base64.decode(spnegoTokenStr);
////            } catch (Exception e) {
////                debug.error("Decoding token error.");
////                if (debug.messageEnabled()) {
////                    debug.message("Stack trace: ", e);
////                }
////            }
////        }
//
////        return spnegoToken;
////    }
//
//    private byte[] parseToken(byte[] rawToken) throws IOException {
//        byte[] token = rawToken;
//        DerValue tmpToken = new DerValue(rawToken);
////        if (debug.messageEnabled()) {
////            debug.message("token tag:" + DerValue.printByte(tmpToken.getTag()));
////        }
//        if (tmpToken.getTag() != (byte)0x60) {
//            return null;
//        }
//
//        ByteArrayInputStream tmpInput = new ByteArrayInputStream(tmpToken.getData());
//
//        // check for SPNEGO OID
//        byte[] oidArray = new byte[spnegoOID.length];
//        tmpInput.read(oidArray, 0, oidArray.length);
//        if (Arrays.equals(oidArray, spnegoOID)) {
////            if (debug.messageEnabled()) {
////                debug.message("SPNEGO OID found in the Auth Token");
////            }
//            tmpToken = new DerValue(tmpInput);
//
//            // 0xa0 indicates an init token(NegTokenInit); 0xa1 indicates an
//            // response arg token(NegTokenTarg). no arg token is needed for us.
//
//            if (tmpToken.getTag() == (byte)0xa0) {
////                if (debug.messageEnabled()) {
////                    debug.message("DerValue: found init token");
////                }
//                tmpToken = new DerValue(tmpToken.getData());
//                if (tmpToken.getTag() == (byte)0x30) {
////                    if (debug.messageEnabled()) {
////                        debug.message("DerValue: 0x30 constructed token found");
////                    }
//                    tmpInput = new ByteArrayInputStream(tmpToken.getData());
//                    tmpToken = new DerValue(tmpInput);
//
//                    // In an init token, it can contain 4 optional arguments:
//                    // a0: mechTypes
//                    // a1: contextFlags
//                    // a2: octect string(with leading char 0x04) for the token
//                    // a3: message integrity value
//
//                    while (tmpToken.getTag() != (byte)-1 &&
//                            tmpToken.getTag() != (byte)0xa2) {
//                        // look for next mech token DER
//                        tmpToken = new DerValue(tmpInput);
//                    }
//                    if (tmpToken.getTag() != (byte)-1) {
//                        // retrieve octet string
//                        tmpToken = new DerValue(tmpToken.getData());
//                        token = tmpToken.getData();
//                    }
//                }
//            }
//        } else {
////            if (debug.messageEnabled()) {
////                debug.message("SPNEGO OID not found in the Auth Token");
////            }
//            byte[] krb5Oid = new byte[KERBEROS_V5_OID.length];
//            int i = 0;
//            for (; i < oidArray.length; i++) {
//                krb5Oid[i] = oidArray[i];
//            }
//            tmpInput.read(krb5Oid, i, krb5Oid.length - i);
//            if (!Arrays.equals(krb5Oid, KERBEROS_V5_OID)) {
////                if (debug.messageEnabled()) {
////                    debug.message("Kerberos V5 OID not found in the Auth Token");
////                }
//                token = null;
//            } else {
////                if (debug.messageEnabled()) {
////                    debug.message("Kerberos V5 OID found in the Auth Token");
////                }
//            }
//        }
//        return token;
//    }
//}
