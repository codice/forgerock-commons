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
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
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
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A JASPI Session Module which creates a JWT when securing the response from a successful authentication and sets it
 * as a Cookie on the response. Then on subsequent requests checks for the presents of the JWT as a Cookie on the
 * request and validates the signature and decrypts it and checks the expiration time of the JWT.
 */
public class JwtSessionModule implements ServerAuthModule {

    private CallbackHandler handler;
    private Map options;
    private String keyAlias;
    private String privateKeyPassword;
    private String keystoreType;
    private String keystoreFile;
    private String keystorePassword;
    private int tokenLife;

    /**
     * Initialises the module by getting the Keystore and Key alias properties out of the module configuration.
     *
     * @param requestPolicy {@inheritDoc}
     * @param responsePolicy {@inheritDoc}
     * @param handler {@inheritDoc}
     * @param options {@inheritDoc}
     * @throws AuthException {@inheritDoc}
     */
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
        this.tokenLife = Integer.parseInt((String) options.get("token-life"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class[] getSupportedMessageTypes() {
        return new Class[]{HttpServletRequest.class, HttpServletResponse.class};
    }

    /**
     * Checks for the presents of the JWT as a Cookie on the request and validates the signature and decrypts it and
     * checks the expiration time of the JWT. If all these checks pass then the method return AuthStatus.SUCCESS,
     * otherwise returns AuthStatus.SEND_FAILURE.
     *
     * @param messageInfo {@inheritDoc}
     * @param clientSubject {@inheritDoc}
     * @param serviceSubject {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();

        String sessionJwt = null;
        for (Cookie cookie : request.getCookies()) {
            if ("session-jwt".equals(cookie.getName())) {
                sessionJwt = cookie.getValue();
                break;
            }
        }

        if (sessionJwt != null && !"".equals(sessionJwt)) {

            JWT jwt;
            if ((jwt = verifySessionJwt(sessionJwt)) == null) {
                return AuthStatus.SEND_FAILURE;
            } else {
                //if all goes well!
                try {
                    for (String key : jwt.getJWTClaimsSet().getCustomClaims().keySet()) {
                        request.setAttribute(key, jwt.getJWTClaimsSet().getCustomClaim(key));
                    }
                } catch (ParseException e) {
                    return AuthStatus.SEND_FAILURE;
                }

                return AuthStatus.SUCCESS;
            }
        }

        return AuthStatus.SEND_FAILURE;
    }

    /**
     * Verifies that the JWT has a valid signature and can be decrypted and that the JWT expiration time has not
     * passed.
     *
     * @param sessionJwt The JWT string.
     * @return The validated decrypted JWT.
     */
    private JWT verifySessionJwt(String sessionJwt) {

        KeystoreManager keystoreManager = new KeystoreManager(privateKeyPassword, keystoreType,
                keystoreFile, keystorePassword);

        RSAPrivateKey privateKey = (RSAPrivateKey) keystoreManager.getPrivateKey(keyAlias);

        try {
            EncryptedJWT jwt = EncryptedJWT.parse(sessionJwt);

            RSADecrypter decrypter = new RSADecrypter(privateKey);

            jwt.decrypt(decrypter);

            Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();

            if (System.currentTimeMillis() < expirationTime.getTime()) {
                return jwt;
            }

        } catch (JOSEException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParseException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    /**
     * Creates a JWT after a successful authentication and sets it as a Cookie on the response. An expiration time
     * is included in the JWT to limit the life of the JWT.
     *
     * @param messageInfo {@inheritDoc}
     * @param serviceSubject {@inheritDoc}
     * @return {@inheritDoc}
     * @throws AuthException {@inheritDoc}
     */
    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {

        Map<String, Object> jwtParameters = new HashMap<String, Object>(messageInfo.getMap());

        if (jwtParameters.containsKey("skipSession") && ((Boolean) jwtParameters.get("skipSession"))) {
            // TODO log skipping session
            return AuthStatus.SEND_SUCCESS;
        }

        HttpServletResponse response = (HttpServletResponse) messageInfo.getResponseMessage();
        createSessionJwt(response, jwtParameters);

        return AuthStatus.SEND_SUCCESS;
    }

    /**
     * Creates the session JWT, including the custom parameters in the payload and adding the expiration time and then
     * sets the JWT onto the response as a Cookie.
     *
     * @param response The HttpServletResponse.
     * @param jwtParameters The parameters that should be added to the JWT payload.
     * @throws AuthException If there is a problem creating and encrypting the JWT.
     */
    private void createSessionJwt(HttpServletResponse response, Map<String, Object> jwtParameters) throws AuthException {

        KeystoreManager keystoreManager = new KeystoreManager(privateKeyPassword, keystoreType,
                keystoreFile, keystorePassword);

        RSAPublicKey publicKey = (RSAPublicKey) keystoreManager.getPublicKey(keyAlias);

        JWTClaimsSet jwtClaims = new JWTClaimsSet();

        final Date NOW =  new Date(new Date().getTime() / 1000 * 1000);
        Date exp = new Date(NOW.getTime() + 1000 * 60 * tokenLife);
        jwtClaims.setExpirationTime(exp);

        Date nbf = NOW;
        jwtClaims.setNotBeforeTime(nbf);

        Date iat = NOW;
        jwtClaims.setIssueTime(iat);

        String jti = UUID.randomUUID().toString();
        jwtClaims.setJWTID(jti);

        jwtClaims.setAllClaims(jwtParameters);

        JWEHeader header = new JWEHeader(JWEAlgorithm.RSA1_5, EncryptionMethod.A128CBC_HS256);

        EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

        try {
            RSAEncrypter encrypter = new RSAEncrypter(publicKey);

            jwt.encrypt(encrypter);

            String jwtString = jwt.serialize();

            //TODO need to sign the JWT once encrypted, but the nimbus library we are using doesn't support this.
            //TODO so will need to be updated once we have finished writing our own JWT Signing and Enryption library.

            Cookie cookie = new Cookie("session-jwt", jwtString);
            cookie.setPath("/");
            response.addCookie(cookie);

        } catch (JOSEException e) {
            throw new AuthException(e.getMessage());
        }
    }

    /**
     * No cleaning for the Subject is required for this module.
     *
     * @param messageInfo {@inheritDoc}
     * @param subject {@inheritDoc}
     * @throws AuthException {@inheritDoc}
     */
    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
    }
}
