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

// Apache Commons Codec
import org.apache.commons.codec.binary.Base64;

// Jackson
import org.codehaus.jackson.map.ObjectMapper;

// JSON Fluent
import org.forgerock.json.fluent.JsonNode;

// JSON Crypto
import org.forgerock.json.crypto.JsonCryptoException;
import org.forgerock.json.crypto.JsonEncryptor;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class SimpleEncryptor implements JsonEncryptor {

    /** TODO: Description. */
    private final ObjectMapper mapper = new ObjectMapper();

    /** TODO: Description. */
    private String cipher;

    /** TODO: Description. */
    private Key key;

    /** TODO: Description. */
    private String alias;

    /** TODO: Description. */
    private String password;

    /**
     * TODO: Description.
     *
     * @param cipher TODO.
     * @param key TODO.
     * @param alias TODO.
     */
    public SimpleEncryptor(String cipher, Key key, String alias) {
        this.cipher = cipher;
        this.key = key;
        this.alias = alias;
    }

    @Override
    public String getType() {
        return "x-simple-encryption";
    }

    @Override
    public JsonNode encrypt(JsonNode node) throws JsonCryptoException {
        try {
            Cipher cipher = Cipher.getInstance(this.cipher);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plaintext = mapper.writeValueAsBytes(node.getValue());
            String data = Base64.encodeBase64String(cipher.doFinal(plaintext));
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("cipher", this.cipher);
            result.put("key", alias);
            result.put("data", data);
            result.put("iv", Base64.encodeBase64String(cipher.getIV()));
            return new JsonNode(result);
        } catch (GeneralSecurityException gse) { // Java Cryptography Extension
            throw new JsonCryptoException(gse);
        } catch (IOException ioe) { // Jackson
            throw new JsonCryptoException(ioe);
        }
    }
}
