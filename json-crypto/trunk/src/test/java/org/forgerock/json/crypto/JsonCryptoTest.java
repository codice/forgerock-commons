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
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.crypto;

// Java Standard Edition
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

// FEST-Assert
import static org.fest.assertions.Assertions.assertThat;

// TestNG
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

// JSON Fluent library
import org.forgerock.json.fluent.JsonNode;
import org.forgerock.json.fluent.JsonTransformer;

// JSON Crypto library
import org.forgerock.json.crypto.simple.SimpleDecryptor;
import org.forgerock.json.crypto.simple.SimpleEncryptor;
import org.forgerock.json.crypto.simple.SimpleKeySelector;
import org.forgerock.json.fluent.JsonPointer;

/**
 * @author Paul C. Bryan
*/
public class JsonCryptoTest {

    private static final String SYMMETRIC_CIPHER = "AES/CBC/PKCS5Padding";

    private static final String ASYMMETRIC_CIPHER = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";

    private static final String PASSWORD = "P@55W0RD";

    private static final String PLAINTEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

    private SecretKey secretKey;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    private SimpleKeySelector selector = new SimpleKeySelector() {
        @Override public Key select(String key) {
            if (key.equals("secretKey")) {
                return secretKey;
            } else if (key.equals("privateKey")) {
                return privateKey;
            } else {
                return null;
            }
        }
    };

    // ----- initialization ----------

    @BeforeClass
    public void beforeClass() throws GeneralSecurityException, IOException {

        // generate AES 128-bit secret key
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128); // the Sun JRE out of the box restricts to 128-bit key length
        secretKey = kg.generateKey();

        // generate RSA 1024-bit key pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    // ----- happy path ----------

    @Test
    public void testSymmetricEncryption() throws JsonCryptoException {
        JsonNode node = new JsonNode(PLAINTEXT);
        node = new SimpleEncryptor(SYMMETRIC_CIPHER, secretKey, "secretKey").encrypt(node);
        assertThat(node.getValue()).isNotEqualTo(PLAINTEXT);
        node = new SimpleDecryptor(selector).decrypt(node);
        assertThat(node.getValue()).isEqualTo(PLAINTEXT);
    }

    @Test
    public void testAsymmetricEncryption() throws JsonCryptoException {
        JsonNode node = new JsonNode(PLAINTEXT);
        node = new SimpleEncryptor(ASYMMETRIC_CIPHER, publicKey, "privateKey").encrypt(node);
        assertThat(node.getValue()).isNotEqualTo(PLAINTEXT);
        node = new SimpleDecryptor(selector).decrypt(node);
        assertThat(node.getValue()).isEqualTo(PLAINTEXT);
    }

    @Test
    public void testJsonCryptoTransformer() throws JsonCryptoException {
        JsonNode node = new JsonNode(PLAINTEXT);
        JsonEncryptor encryptor = new SimpleEncryptor(SYMMETRIC_CIPHER, secretKey, "secretKey");
        JsonNode crypto = new JsonCrypto(encryptor.getType(), encryptor.encrypt(node)).toJsonNode();
        ArrayList<JsonTransformer> transformers = new ArrayList<JsonTransformer>();
        transformers.add(new JsonCryptoTransformer(new SimpleDecryptor(selector)));
        node = new JsonNode(crypto.getValue(), null, transformers);
        assertThat(node.getValue()).isEqualTo(PLAINTEXT);
    }

    @Test
    public void testDeepObjectEncryption() throws JsonCryptoException {
        JsonTransformer encryptionTransformer = new JsonCryptoTransformer(new SimpleEncryptor(SYMMETRIC_CIPHER, secretKey, "secretKey"));
        ArrayList<JsonTransformer> transformers = new ArrayList<JsonTransformer>();
        transformers.add(new JsonCryptoTransformer(new SimpleDecryptor(selector)));

        //Encrypt a simple node
        JsonNode node = new JsonNode(PASSWORD);
        encryptionTransformer.transform(node);
        assertThat(node.getValue()).isNotEqualTo(PASSWORD);

        Map<String, Object> inner = new HashMap<String, Object>();
        inner.put("password",node.getValue());
        node = new JsonNode(new HashMap<String, Object>());
        node.put("user", inner);
        node.put("description", PLAINTEXT);
        

        //Decrypt the DeepObject        
        node.getTransformers().addAll(transformers);
        node = node.copy();         
        assertThat(node.get(new JsonPointer("/user/password")).getValue()).isEqualTo(PASSWORD);

        //Encrypt a complex object
        node = new JsonNode(node.getValue());
        encryptionTransformer.transform(node);
        Assert.assertTrue(JsonCrypto.isJsonCrypto(node));

        //Decrypt the DeepObject
        //TODO Expected way of decryption
        /*
        node.getTransformers().addAll(transformers);
        node = node.copy();
         */
        node = new JsonNode(node.getValue(), null, transformers);
        assertThat(node.get(new JsonPointer("/user/password")).getValue()).isEqualTo(PASSWORD);
        assertThat(node.get("description").getValue()).isEqualTo(PLAINTEXT);
    }

    // ----- exceptions ----------

    @Test(expectedExceptions=JsonCryptoException.class)
    public void testDroppedIV() throws JsonCryptoException {
        JsonNode node = new JsonNode(PLAINTEXT);
        node = new SimpleEncryptor(SYMMETRIC_CIPHER, secretKey, "secretKey").encrypt(node);
        node.remove("iv");
        new SimpleDecryptor(selector).decrypt(node);
    }

    @Test(expectedExceptions=JsonCryptoException.class)
    public void testUnknownKey() throws JsonCryptoException {
        JsonNode node = new JsonNode(PLAINTEXT);
        node = new SimpleEncryptor(SYMMETRIC_CIPHER, secretKey, "secretKey").encrypt(node);
        node.put("key", "somethingCompletelyDifferent");
        new SimpleDecryptor(selector).decrypt(node);
    }
}
