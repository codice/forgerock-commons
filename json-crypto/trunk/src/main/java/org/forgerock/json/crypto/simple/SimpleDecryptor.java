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
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

// Apache Commons Codec
import org.apache.commons.codec.binary.Base64;

// Jackson
import org.codehaus.jackson.map.ObjectMapper;

// JSON Fluent
import org.forgerock.json.fluent.JsonNode;
import org.forgerock.json.fluent.JsonNodeException;

// JSON Crypto
import org.forgerock.json.crypto.JsonCryptoException;
import org.forgerock.json.crypto.JsonDecryptor;
/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class SimpleDecryptor implements JsonDecryptor {

    /** TODO: Description. */
    private final ObjectMapper mapper = new ObjectMapper();

    private final SimpleKeySelector selector;

    /**
     * TODO: Description.
     *
     * @param selector TODO.
     */
    public SimpleDecryptor(SimpleKeySelector selector) {
        this.selector = selector;
    }

    @Override
    public String getType() {
        return "x-simple-encryption";
    }

    @Override
    public JsonNode decrypt(JsonNode node) throws JsonCryptoException {
        try {
            String alias = node.get("key").required().asString();
            Key key = selector.select(alias);
            if (key == null) {
                throw new JsonCryptoException("key not found: " + alias);
            }
            Cipher cipher = Cipher.getInstance(node.get("cipher").required().asString());
            String iv = node.get("iv").asString();
            IvParameterSpec ivps = (iv == null ? null : new IvParameterSpec(Base64.decodeBase64(iv)));
            cipher.init(Cipher.DECRYPT_MODE, key, ivps);
            byte[] plaintext = cipher.doFinal(Base64.decodeBase64(node.get("data").required().asString()));
            return new JsonNode(mapper.readValue(plaintext, Object.class));
        } catch (GeneralSecurityException gse) { // Java Cryptography Extension
            throw new JsonCryptoException(gse);
        } catch (IOException ioe) { // Jackson
            throw new JsonCryptoException(ioe);
        } catch (JsonNodeException jne) { // JSON Fluent
            throw new JsonCryptoException(jne);
        }
    }

}
