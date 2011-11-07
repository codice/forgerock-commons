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

// JSON Fluent
import org.forgerock.json.fluent.JsonException;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.fluent.JsonTransformer;

/**
 * Transforms JSON values by applying an encryptor or decryptor.
 *
 * @author Paul C. Bryan
 */
public class JsonCryptoTransformer implements JsonTransformer {

    /** Decryptor to apply to JSON values. */
    private JsonDecryptor decryptor;

    /** Encryptor to apply to JSON values. */
    private JsonEncryptor encryptor;

    /**
     * Constructs a transformer to apply a decryptor.
     *
     * @param decryptor the decryptor to apply to JSON values.
     * @throws NullPointerException if {@code decryptor} is {@code null}.
     */
    public JsonCryptoTransformer(JsonDecryptor decryptor) {
        if (decryptor == null) {
            throw new NullPointerException();
        }
        this.decryptor = decryptor;
    }

    /**
     * Constructs a transformer to apply an encryptor.
     *
     * @param encryptor the encryptor to apply to JSON values.
     * @throws NullPointerException if {@code encryptor} is {@code null}.
     */
    public JsonCryptoTransformer(JsonEncryptor encryptor) {
        if (encryptor == null) {
            throw new NullPointerException();
        }
        this.encryptor = encryptor;
    }

    @Override
    public void transform(JsonValue value) throws JsonException {
        try {
            if (encryptor != null) { // transformer performs encryption
                value.setValue(new JsonCrypto(encryptor.getType(), encryptor.encrypt(value)).toJsonValue().getValue());
            } else if (JsonCrypto.isJsonCrypto(value)) { // transformer performs decryption and value properties match
                JsonCrypto crypto = new JsonCrypto(value);
                if (crypto.getType().equals(decryptor.getType())) { // only attempt decryption if type matches
                    value.setValue(decryptor.decrypt(crypto.getValue()).getValue());
                }
            }
        } catch (JsonCryptoException jce) {
            throw new JsonException(jce);
        }
    }
}
