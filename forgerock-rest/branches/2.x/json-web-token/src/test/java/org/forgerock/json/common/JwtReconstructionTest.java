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
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */

package org.forgerock.json.common;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.jose.builders.JwsHeaderBuilder;
import org.forgerock.json.jose.builders.JwtBuilderFactory;
import org.forgerock.json.jose.common.JwtReconstruction;
import org.forgerock.json.jose.jwk.KeyUse;
import org.forgerock.json.jose.jwk.RsaJWK;
import org.forgerock.json.jose.jws.JwsAlgorithm;
import org.forgerock.json.jose.jws.SignedJwt;
import org.forgerock.json.jose.jws.SigningManager;
import org.forgerock.json.jose.jws.handlers.SigningHandler;
import org.forgerock.json.jose.jwt.JwtClaimsSet;
import org.forgerock.json.jose.utils.KeystoreManager;
import org.forgerock.util.encode.Base64;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtReconstructionTest {
    private enum PublicKeyReferenceType { none, jku, jwk, x5u, x5c, x5t }
    private static final String JKU_URL_STRING = "http://host.com/op/keys";
    private static final String X5U_URL_STRING = "http://host.com/op/keys";
    private static final String ISSUER = "http://host.com/op";
    private static final String AUDIENCE = "some_client";
    private static final String SUBJECT = "some_subject";
    private static final String FAUX_SHA_CERT_HASH = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
    private static final String CLIENT_SECRET = "bobo";

    private final KeystoreManager keystoreManager;

    public JwtReconstructionTest() throws UnsupportedEncodingException {
        keystoreManager = new KeystoreManager("JKS",
                URLDecoder.decode(ClassLoader.getSystemResource("keystore.jks").getFile(), "UTF-8"), "password");
    }

    @Test
    public void testAsymmetricReconstruction() throws UnsupportedEncodingException, MalformedURLException, CertificateEncodingException {
        final JwtReconstruction jwtReconstruction = new JwtReconstruction();
        final KeyPair keyPair = getKeyPair();
        jwtReconstruction.reconstructJwt(asymmetricSign(PublicKeyReferenceType.none, keyPair).build(), SignedJwt.class);
        jwtReconstruction.reconstructJwt(asymmetricSign(PublicKeyReferenceType.jwk, keyPair).build(), SignedJwt.class);
        jwtReconstruction.reconstructJwt(asymmetricSign(PublicKeyReferenceType.jku, keyPair).build(), SignedJwt.class);
        jwtReconstruction.reconstructJwt(asymmetricSign(PublicKeyReferenceType.x5u, keyPair).build(), SignedJwt.class);
        jwtReconstruction.reconstructJwt(asymmetricSign(PublicKeyReferenceType.x5c, keyPair).build(), SignedJwt.class);
        jwtReconstruction.reconstructJwt(asymmetricSign(PublicKeyReferenceType.x5t, keyPair).build(), SignedJwt.class);
    }

    @Test
    public void testSymmetricReconstruction() {
        new JwtReconstruction().reconstructJwt(symmetricSign().build(), SignedJwt.class);
    }

    private SignedJwt symmetricSign() {
        final SigningHandler signingHandler = new SigningManager().newHmacSigningHandler(CLIENT_SECRET.getBytes());
        JwsHeaderBuilder builder = new JwtBuilderFactory().jws(signingHandler).headers().alg(JwsAlgorithm.HS256);
        JwtClaimsSet claimsSet = new JwtBuilderFactory().claims().claims(getJwt()).build();
        return builder.done().claims(claimsSet).asJwt();

    }
    private SignedJwt asymmetricSign(PublicKeyReferenceType publicKeyReferenceType, KeyPair keyPair) throws UnsupportedEncodingException,
            ClassCastException, MalformedURLException, CertificateEncodingException {

        JwsHeaderBuilder jwsHeaderBuilder = new JwtBuilderFactory().jws(
                new SigningManager().newRsaSigningHandler(keyPair.getPrivate())).headers().alg(JwsAlgorithm.RS256);
        JwtClaimsSet claimsSet = new JwtBuilderFactory().claims().claims(getJwt()).build();
        handleKeyIdentification(jwsHeaderBuilder, publicKeyReferenceType, (RSAPublicKey)keyPair.getPublic(), JwsAlgorithm.RS256);
        return jwsHeaderBuilder.done().claims(claimsSet).asJwt();
    }

    private Map<String, Object> getJwt() {
        JsonValue jsonValue = new JsonValue(new HashMap<String, Object>());
        jsonValue.put("iss", ISSUER);
        jsonValue.put("sub", SUBJECT);
        jsonValue.put("aud", AUDIENCE);
        jsonValue.put("exp", (System.currentTimeMillis() / 1000) + 600);
        return jsonValue.asMap();
    }

    private void handleKeyIdentification(JwsHeaderBuilder jwsHeaderBuilder, PublicKeyReferenceType publicKeyReferenceType,
                                         RSAPublicKey rsaPublicKey, JwsAlgorithm jwsAlgorithm) throws MalformedURLException,
            CertificateEncodingException {
        switch (publicKeyReferenceType) {
            case none:
                return;
            case jwk:
                jwsHeaderBuilder.jwk(buildRSAJWKForPublicKey(rsaPublicKey, jwsAlgorithm));
                return;
            case jku:
                jwsHeaderBuilder.jku(new URL(JKU_URL_STRING));
                return;
            case x5u:
                jwsHeaderBuilder.x5u(new URL(X5U_URL_STRING));
                return;
            case x5c:
                final List<String> list = new ArrayList<String>();
                list.add(Base64.encode(getCertificate().getEncoded()));
                jwsHeaderBuilder.x5c(list);
                return;
            case x5t:
                jwsHeaderBuilder.x5t(FAUX_SHA_CERT_HASH);
                return;
            default:
                throw new IllegalArgumentException("Unsupported public key identification test");
        }
    }

    private RsaJWK buildRSAJWKForPublicKey(RSAPublicKey rsaPublicKey, JwsAlgorithm jwsAlgorithm) {
        final String kid = null, x5u = null, x5t = null;
        final List<Base64> x5c = null;
        return new RsaJWK(rsaPublicKey, KeyUse.SIG, jwsAlgorithm.name(), kid, x5u, x5t, x5c);
    }

    private KeyPair getKeyPair() throws UnsupportedEncodingException {
        return new KeyPair(keystoreManager.getPublicKey("jwt-test-ks"), keystoreManager.getPrivateKey("jwt-test-ks", "password"));
    }

    private X509Certificate getCertificate() {
        return keystoreManager.getX509Certificate("jwt-test-ks");
    }
}