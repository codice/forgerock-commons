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

package org.forgerock.json.crypto.simple;

// Java Standard Edition
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

// Apache Commons Codec
import org.apache.commons.codec.binary.Base64;

// Jackson
import org.codehaus.jackson.map.ObjectMapper;

// JSON Fluent library
import org.forgerock.json.fluent.JsonValue;

// JSON Cryptographic library
import org.forgerock.json.crypto.JsonCryptoException;
import org.forgerock.json.crypto.JsonEncryptor;

/**
 * Encrypts a JSON value into an {@code x-simple-encryption} type {@code $crypto} JSON object.
 *
 * @author Paul C. Bryan
 */
public class SimpleEncryptor implements JsonEncryptor {

    /** The type of cryptographic representation that this encryptor supports. */
    public static final String TYPE = "x-simple-encryption";

    /** Converts between Java objects and JSON constructs. */
    private final ObjectMapper mapper = new ObjectMapper();

    /** The cipher to encrypt with. */
    private String cipher;

    /** The key to encrypt with. */
    private Key key;

    /** The key alias to list in the encrypted object. */
    private String alias;

    /**
     * Constructs a new simple encryptor for the specified cipher, key and alias.
     *
     * @param cipher the cipher to encrypt with.
     * @param key the key to encrypt with.
     * @param alias the key alias to list in the encrypted object.
     */
    public SimpleEncryptor(String cipher, Key key, String alias) {
        this.cipher = cipher;
        this.key = key;
        this.alias = alias;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Encrypts with a symmetric cipher.
     *
     * @param value the value to be encrypted.
     * @return the encrypted value.
     * @throws GeneralSecurityException if a cryptographic operation failed.
     * @throws IOException if an I/O exception occurred.
     */
    private JsonValue symmetric(JsonValue value) throws GeneralSecurityException, IOException {
        Cipher symmetric = Cipher.getInstance(cipher);
        symmetric.init(Cipher.ENCRYPT_MODE, key);
        String data = Base64.encodeBase64String(symmetric.doFinal(mapper.writeValueAsBytes(value.getValue())));
        byte[] iv = symmetric.getIV();
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("cipher", this.cipher);
        result.put("key", this.alias);
        result.put("data", data);
        if (iv != null) {            
            result.put("iv", Base64.encodeBase64String(iv));
        }
        return new JsonValue(result);
    }

    /**
     * Encrypts using an asymmetric cipher.
     *
     * @param value the value to be encrypted.
     * @return the encrypted value.
     * @throws GeneralSecurityException if a cryptographic operation failed.
     * @throws IOException if an I/O exception occurred.
     */
    private JsonValue asymmetric(JsonValue value) throws GeneralSecurityException, IOException {
        String symmetricCipher = "AES/ECB/PKCS5Padding"; // no IV required for randomly-generated session key
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128);
        SecretKey sessionKey = generator.generateKey();
        Cipher symmetric = Cipher.getInstance(symmetricCipher);
        symmetric.init(Cipher.ENCRYPT_MODE, sessionKey);
        String data = Base64.encodeBase64String(symmetric.doFinal(mapper.writeValueAsBytes(value.getValue())));
        Cipher asymmetric = Cipher.getInstance(cipher);
        asymmetric.init(Cipher.ENCRYPT_MODE, key);
        HashMap<String, Object> keyObject = new HashMap<String, Object>();
        keyObject.put("cipher", this.cipher);
        keyObject.put("key", this.alias);
        keyObject.put("data", Base64.encodeBase64String(asymmetric.doFinal(sessionKey.getEncoded())));
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("cipher", symmetricCipher);
        result.put("key", keyObject);
        result.put("data", data);
        return new JsonValue(result);
    }

    @Override
    public JsonValue encrypt(JsonValue value) throws JsonCryptoException {
        try {
            return (key instanceof SecretKey ? symmetric(value) : asymmetric(value));
        } catch (GeneralSecurityException gse) { // Java Cryptography Extension
            throw new JsonCryptoException(gse);
        } catch (IOException ioe) { // Jackson
            throw new JsonCryptoException(ioe);
        }
    }
}
