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
import java.util.HashMap;

// JSON Fluent library
import org.forgerock.json.fluent.JsonValueException;
import org.forgerock.json.fluent.JsonValue;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class JsonCrypto {

    /** TODO: Description. */
    private String type;

    /** TODO: Description. */
    private JsonValue value;

    /**
     * TODO: Description.
     */
    public JsonCrypto() {
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public JsonCrypto(JsonValue value) throws JsonValueException {
        fromJsonValue(value);
    }

    /**
     * TODO: Description.
     *
     * @param type TODO.
     * @param value TODO.
     */
    public JsonCrypto(String type, JsonValue value) {
        setType(type);
        setValue(value);
    }

    /**
     * Returns {@code true} if the specified JSON value contains a valid JSON crypto structure.
     */
    public static boolean isJsonCrypto(JsonValue value) {
        JsonValue crypto = (value == null ? new JsonValue(null) : value.get("$crypto"));
        return (crypto.get("type").isString() && crypto.isDefined("value"));
    }

    /**
     * TODO: Description.
     */
    public String getType() {
        return type;
    }

    /**
     * TODO: Description.
     *
     * @param type TODO.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * TODO: Description.
     */
    public JsonValue getValue() {
        return value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     */
    public void setValue(JsonValue value) {
        this.value = value;
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     * @throws JsonValueException TODO.
     */
    public void fromJsonValue(JsonValue value) throws JsonValueException {
        JsonValue crypto = value.get("$crypto").required();
        this.type = crypto.get("type").required().asString();
        this.value = crypto.get("value").required();
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    public JsonValue toJsonValue() {
        HashMap<String, Object> crypto = new HashMap<String, Object>();
        crypto.put("type", type);
        crypto.put("value", value == null ? null : value.getValue());
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("$crypto", crypto);
        return new JsonValue(result);
    }
}
