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

package org.forgerock.json.jwt;

import org.forgerock.json.fluent.JsonException;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.util.encode.Base64;

import java.security.PrivateKey;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a Plaintext JWT, with no signing or encryption.
 */
public class PlaintextJwt implements Jwt {

    private final Map<String, String> headers = new TreeMap<String, String>();
    private final Map<String, Object> content = new TreeMap<String, Object>();

    /**
     * Constructs a PlaintextJwt with no headers or content.
     */
    PlaintextJwt() {
    }

    /**
     * Constructs a PlaintextJwt with the given headers and content.
     *
     * @param headers The Jwt headers.
     * @param content The Jwt content.
     */
    PlaintextJwt(Map<String, String> headers, Map<String, Object> content) {
        header(headers);
        content(content);
    }

    /**
     * Returns the key set for the headers.
     *
     * @return The headers key set.
     */
    public Set<String> headerKeySet() {
        return headers.keySet();
    }

    /**
     * Returns the header value for the given key.
     *
     * @param key The header key.
     * @return The header value.
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * Returns the key set for the content.
     *
     * @return The content key set.
     */
    public Set<String> contentKeySet() {
        return content.keySet();
    }

    /**
     * Returns the content value for the given key.
     *
     * @param key The content key.
     * @return The content value.
     */
    public Object getContent(String key) {
        Object value = content.get(key);
        if (value != null && String.class.isAssignableFrom(value.getClass())) {
            String s = (String) value;
            if (s.contains("\\\\\"")) {
                s = s.replaceAll("\\\\\"", "\"");
            }
            return s;
        }
        return value;
    }

    /**
     * Returns the content value for the given key.
     *
     * @param key The content key.
     * @param clazz The class of the Object stored with the given key.
     * @param <T> The type of Object stored with the given key.
     * @return The content value.
     */
    public <T> T getContent(String key, Class<? extends T> clazz) {
        return (T) getContent(key);
    }

    /**
     * Adds an entry to the JWT's header.
     *
     * @param key The header key.
     * @param value The header value.
     * @return This PlaintextJwt.
     */
    public PlaintextJwt header(String key, String value) {
        headers.put(key, processString(value));
        return this;
    }

    private PlaintextJwt header(Map<String, String> values) {
        for (String key : values.keySet()) {
            header(key, values.get(key));
        }
        return this;
    }

    /**
     * Adds an entry to the JWT's content.
     *
     * @param key The content key.
     * @param value The content value.
     * @return This PlaintextJwt.
     */
    public PlaintextJwt content(String key, Object value) {
        content.put(key, processJwtValue(value));
        return this;
    }

    /**
     * Adds a set of entries to the JWT's content.
     *
     * @param values A Map of content keys and values.
     * @return This PlaintextJwt.
     */
    public PlaintextJwt content(Map<String, Object> values) {
        for (String key : values.keySet()) {
            content(key, values.get(key));
        }
        return this;
    }

    private Object processJwtValue(Object value) {
        if (String.class.isAssignableFrom(value.getClass())) {
            return processString((String) value);
        } else {
            return value;
        }
    }

    private String processString(String s) {
        if (s.contains("\"")) {
            s = s.replaceAll("\"", "\\\\\"");
        }
        return s;
    }

    /**
     * Signs the Plaintext Jwt, resulting in a SignedJwt.
     *
     * @param algorithm The Jwt Algorithm used to perform the signing.
     * @param privateKey The private key to use to sign with.
     * @return A SignedJwt.
     * @throws JWTBuilderException If there is a problem creating the SignedJwt.
     */
    public SignedJwt sign(JwsAlgorithm algorithm, PrivateKey privateKey) throws JWTBuilderException {
        return new SignedJwt(this, algorithm, privateKey);
    }

    /**
     * Encrypts the Plaintext Jwt, resulting in an EncryptedJwt.
     *
     * Currently not supported. Will thrown an UnsupportedOperationException.
     *
     * @return An EncryptedJwt.
     */
    public EncryptedJwt encrypt() {
        throw new UnsupportedOperationException("Encrypting JWTs is not currently supported");
//        return new EncryptedJwt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build() throws JWTBuilderException {

        try {
            JsonValue header = buildHeader();
            header.put(JWT_HEADER_TYPE_KEY, JwtType.JWT.toString());

            String encodedHeader = Base64.encode(header.toString().getBytes());

            JsonValue claimsSet = buildContent();

            byte[] message = claimsSet.toString().getBytes();

            String secondPart = Base64.encode(message);

            String thirdPart = "";

            return encodedHeader + JWT_PART_SEPARATOR + secondPart + JWT_PART_SEPARATOR + thirdPart;

        } catch (JsonException e) {
            throw new JWTBuilderException("Failed to build JWT", e);
        }
    }

    /**
     * Creates a JsonValue of the JWT's headers.
     *
     * @return JWT's headers as a JsonValue.
     * @throws JsonException If there is a problem creating the JsonValue.
     */
    JsonValue buildHeader() throws JsonException {
        return createJsonObject(headers);
    }

    /**
     * Creates a JsonValue of the JWT's content.
     *
     * @return JWT's content as a JsonValue.
     * @throws JsonException If there is a problem creating the JsonValue.
     */
    JsonValue buildContent() throws JsonException {
        return createJsonObject(content);
    }

    /**
     * Creates a JsonValue from a given Map of key value pairs.
     *
     * @param map The Map of key value pairs.
     * @return The JsonValue.
     * @throws JsonException If there is a problem creating the JsonValue.
     */
    private JsonValue createJsonObject(Map<String, ? extends Object> map) throws JsonException {
        return new JsonValue(map);
    }
}
