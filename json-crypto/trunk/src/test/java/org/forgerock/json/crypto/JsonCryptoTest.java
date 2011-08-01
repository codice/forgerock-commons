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
import java.security.KeyStore;
import java.util.ArrayList;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

// FEST-Assert
import static org.fest.assertions.Assertions.assertThat;

// TestNG
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

// JSON Fluent library
import org.forgerock.json.fluent.JsonException;
import org.forgerock.json.fluent.JsonNode;
import org.forgerock.json.fluent.JsonTransformer;

// JSON Crypto library
import org.forgerock.json.crypto.simple.SimpleDecryptor;
import org.forgerock.json.crypto.simple.SimpleEncryptor;
import org.forgerock.json.crypto.simple.SimpleKeySelector;
import org.forgerock.json.crypto.simple.SimpleKeyStoreSelector;

/**
 * @author Paul C. Bryan
 */
public class JsonCryptoTest {

    private static final String CIPHER = "AES/CBC/PKCS5Padding";

    private static final String PASSWORD = "P@55W0RD";

    private static final String PLAINTEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

    private static final String ALIAS = "secret";

    private SecretKey key;

    private SimpleKeySelector selector;

    // ----- initialization ----------

    @BeforeClass
    public void beforeClass() throws GeneralSecurityException, IOException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        key = kg.generateKey();
        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(null, null);
        ks.setKeyEntry(ALIAS, key, PASSWORD.toCharArray(), null);
        selector = new SimpleKeyStoreSelector(ks, PASSWORD);
    }

    // ----- happy path ----------

    @Test
    public void testSimpleEncryption() throws JsonException {
        JsonNode node = new JsonNode(PLAINTEXT);
        node = new SimpleEncryptor(CIPHER, key, ALIAS).encrypt(node);
        assertThat(node.getValue()).isNotEqualTo(PLAINTEXT);
        node = new SimpleDecryptor(selector).decrypt(node);
        assertThat(node.getValue()).isEqualTo(PLAINTEXT);
    }
    
    @Test
    public void testJsonCryptoTransformer() throws JsonException {
        JsonNode node = new JsonNode(PLAINTEXT);
        JsonEncryptor encryptor = new SimpleEncryptor(CIPHER, key, ALIAS);
        JsonNode crypto = new JsonCrypto(encryptor.getType(), encryptor.encrypt(node)).toJsonNode();
        ArrayList<JsonTransformer> transformers = new ArrayList<JsonTransformer>();
        transformers.add(new JsonCryptoTransformer(new SimpleDecryptor(selector)));
        node = new JsonNode(crypto.getValue(), null, transformers);
        assertThat(node.getValue()).isEqualTo(PLAINTEXT);
    }

    // ----- exceptions ----------

    @Test(expectedExceptions=JsonCryptoException.class)
    public void testDroppedIV() throws JsonException {
        JsonNode node = new JsonNode(PLAINTEXT);
        node = new SimpleEncryptor(CIPHER, key, ALIAS).encrypt(node);
        node.remove("iv");
        node = new SimpleDecryptor(selector).decrypt(node);
    }
}
