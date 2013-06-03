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

package org.forgerock.jaspi.modules.session.jwt;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.forgerock.json.jwt.keystore.KeystoreManager;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtSessionModule implements ServerAuthModule {

    private CallbackHandler handler;
    private Map options;
    private String keyAlias;
    private String privateKeyPassword;
    private String keystoreType;
    private String keystoreFile;
    private String keystorePassword;

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
                           Map options) throws AuthException {
        this.handler = handler;
        this.options = options;
        this.keyAlias = (String) options.get("key-alias");
        this.privateKeyPassword = (String) options.get("private-key-password");
        this.keystoreType = (String) options.get("keystore-type");
        this.keystoreFile = (String) options.get("keystore-file");
        this.keystorePassword = (String) options.get("keystore-password");
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
            throws AuthException {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();

//        String sessionJwt = request.getHeader("session-jwt");
        String sessionJwt = null;
        for (Cookie cookie : request.getCookies()) {
            if ("session-jwt".equals(cookie.getName())) {
                sessionJwt = cookie.getValue();
                break;
            }
        }

        if (sessionJwt != null && !"".equals(sessionJwt)) {

            if (!verifySessionJwt(sessionJwt)) {
                return AuthStatus.SEND_FAILURE;
            } else {
                //if all goes well!
                HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();
//                createSessionJwt(response, sessionJwt);
                Map<String, Object> jwtParameters = new HashMap<String, Object>();//TODO not right
                createSessionJwt(response, jwtParameters);
                return AuthStatus.SUCCESS;
            }
        }

        return AuthStatus.SEND_FAILURE;
    }

    private boolean verifySessionJwt(String sessionJwt) throws AuthException {

        KeystoreManager keystoreManager = new KeystoreManager(privateKeyPassword, keystoreType,
                keystoreFile, keystorePassword);

        RSAPrivateKey privateKey = (RSAPrivateKey) keystoreManager.getPrivateKey(keyAlias);
//        X509Certificate certificate = keystoreManager.getX509Certificate("jwt-test-ks");

//        SignedJwt jwt = (SignedJwt) new JwtBuilder().recontructJwt(sessionJwt);
//        boolean verified = jwt.verify(privateKey, certificate);

        try {
            EncryptedJWT jwt = EncryptedJWT.parse(sessionJwt);

            RSADecrypter decrypter = new RSADecrypter(privateKey);

            jwt.decrypt(decrypter);

            Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();

            if (System.currentTimeMillis() < expirationTime.getTime()) {
                return true;
            }

        } catch (JOSEException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {

        Map<String, Object> jwtParameters = new HashMap<String, Object>();

        if (serviceSubject != null) {
            for (Principal principal : serviceSubject.getPrincipals()) {
    //            principal.
                //add stuff to jwtParameters
            }
        }

        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();
        createSessionJwt(response, jwtParameters);

        return AuthStatus.SEND_SUCCESS;
    }

//    private void createSessionJwt(HttpServletResponse response, String currentSessionJwt) {
//
//        SignedJwt jwt = ((SignedJwt) new JwtBuilder().recontructJwt(currentSessionJwt));
//
//        String sessionJwt = jwt.getJwt()
//                .content("issueTimestamp", System.currentTimeMillis())
//                .build();
//
//        response.addHeader("session-jwt", sessionJwt);
//    }

    private void createSessionJwt(HttpServletResponse response, Map<String, Object> jwtParameters) throws AuthException {

        KeystoreManager keystoreManager = new KeystoreManager(privateKeyPassword, keystoreType,
                keystoreFile, keystorePassword);

//        PrivateKey privateKey = keystoreManager.getPrivateKey("jwt-test-ks");

        RSAPublicKey publicKey = (RSAPublicKey) keystoreManager.getPublicKey(keyAlias);

//        String sessionJwt = new JwtBuilder().jwt()
//                .content("issueTimestamp", System.currentTimeMillis())
//                .content("validity", 300000)
//                .content(jwtParameters)
//                .sign(JwsAlgorithm.HS256, privateKey)
//                .build();

        JWTClaimsSet jwtClaims = new JWTClaimsSet();

//        String iss = "https://openid.net";
//        jwtClaims.setIssuer(iss);

//        String sub = "alice";
//        jwtClaims.setSubject(sub);

//        List<String> aud = new ArrayList<String>();
//        aud.add("https://app-one.com");
//        aud.add("https://app-two.com");
//        jwtClaims.setAudience(aud);

        // Set expiration in 10 minutes
        final Date NOW =  new Date(new Date().getTime() / 1000 * 1000);
        Date exp = new Date(NOW.getTime() + 1000 * 60 * 10);
        jwtClaims.setExpirationTime(exp);

        Date nbf = NOW;
        jwtClaims.setNotBeforeTime(nbf);

        Date iat = NOW;
        jwtClaims.setIssueTime(iat);

        String jti = UUID.randomUUID().toString();
        jwtClaims.setJWTID(jti);

        JWEHeader header = new JWEHeader(JWEAlgorithm.RSA1_5, EncryptionMethod.A128CBC_HS256);

        EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

        try {
            RSAEncrypter encrypter = new RSAEncrypter(publicKey);

            jwt.encrypt(encrypter);

            String jwtString = jwt.serialize();

//            response.addHeader("session-jwt", jwtString);
            response.addCookie(new Cookie("session-jwt", jwtString));

        } catch (JOSEException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new AuthException(e.getMessage());
        }
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
    }
}
