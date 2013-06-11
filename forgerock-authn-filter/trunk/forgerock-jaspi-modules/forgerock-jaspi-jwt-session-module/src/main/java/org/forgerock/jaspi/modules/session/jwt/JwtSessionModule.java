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

import org.forgerock.json.jose.builders.JwtBuilder;
import org.forgerock.json.jose.jwe.EncryptedJwt;
import org.forgerock.json.jose.jwe.EncryptionMethod;
import org.forgerock.json.jose.jwe.JweAlgorithm;
import org.forgerock.json.jose.jwt.Jwt;
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
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
            throws AuthException {

        HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();

        String sessionJwt = null;
        for (Cookie cookie : request.getCookies()) {
            if ("session-jwt".equals(cookie.getName())) {
                sessionJwt = cookie.getValue();
                break;
            }
        }

        if (sessionJwt != null && !"".equals(sessionJwt)) {

            Jwt jwt;
            if ((jwt = verifySessionJwt(sessionJwt)) == null) {

                return AuthStatus.SEND_FAILURE;
            } else {
                //if all goes well!
                for (String key : jwt.getClaimsSet().keys()) {
                    request.setAttribute(key, jwt.getClaimsSet().get(key));
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
    private Jwt verifySessionJwt(String sessionJwt) {

        KeystoreManager keystoreManager = new KeystoreManager(privateKeyPassword, keystoreType,
                keystoreFile, keystorePassword);

        RSAPrivateKey privateKey = (RSAPrivateKey) keystoreManager.getPrivateKey(keyAlias);

        JwtBuilder jwtBuilder = new JwtBuilder();
        EncryptedJwt jwt = jwtBuilder.reconstruct(sessionJwt, EncryptedJwt.class);
        jwt.decrypt(privateKey);

        Date expirationTime = jwt.getClaimsSet().getExpirationTime();

        if (System.currentTimeMillis() < expirationTime.getTime()) {
            return jwt;
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

        JwtBuilder jwtBuilder = new JwtBuilder();


        final Date NOW =  new Date(new Date().getTime() / 1000 * 1000);
        Date exp = new Date(NOW.getTime() + 1000 * 60 * tokenLife);
        Date nbf = NOW;
        Date iat = NOW;
        String jti = UUID.randomUUID().toString();

        String jwtString = jwtBuilder
                .jwe(publicKey)
                    .headers()
                        .alg(JweAlgorithm.RSAES_PKCS1_V1_5)
                        .enc(EncryptionMethod.A128CBC_HS256)
                        .done()
                    .claims()
                        .jti(jti)
                        .exp(exp)
                        .nbf(nbf)
                        .iat(iat)
                        .claims(jwtParameters)
                        .done()
                    .build();


        Cookie cookie = new Cookie("session-jwt", jwtString);
        cookie.setPath("/");
        response.addCookie(cookie);
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
