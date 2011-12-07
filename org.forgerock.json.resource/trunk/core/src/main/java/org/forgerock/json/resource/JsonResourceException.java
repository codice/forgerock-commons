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

package org.forgerock.json.resource;

// Java SE
import java.util.LinkedHashMap;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

/**
 * An exception that is thrown during the processing of a JSON resource request. Contains
 * an integer exception code and short reason phrase. A longer description of the exception
 * is provided in the exception's detail message.
 * <p>
 * Positive 3-digit integer exception codes are explicitly reserved for exceptions that
 * correspond with HTTP status codes. For the sake of interoperability with HTTP, if an
 * exception corresponds with an HTTP error status, use the matching HTTP status code. 
 *
 * @author Paul C. Bryan
 */
public class JsonResourceException extends Exception {

    /** Serializable class a version number. */
    private static final long serialVersionUID = 1L;

    /**
     * Indicates that the request could not be understood by the resource due to malformed
     * syntax. Equivalent to HTTP status: 400 Bad Request.
     */
    public static final JsonResourceException BAD_REQUEST = new JsonResourceException(400);

    /**
     * Indicates the request could not be completed due to a conflict with the current state
     * of the resource. Equivalent to HTTP status: 409 Conflict.
     */
    public static final JsonResourceException CONFLICT = new JsonResourceException(409);

    /**
     * Indicates that the resource understood the request, but is refusing to fulfill it.
     * Equivalent to HTTP status: 403 Forbidden.
     */
    public static final JsonResourceException FORBIDDEN = new JsonResourceException(403);

    /**
     * Indicates that a resource encountered an unexpected condition which prevented it from
     * fulfilling the request. Equivalent to HTTP status: 500 Internal Server Error.
     */
    public static final JsonResourceException INTERNAL_ERROR = new JsonResourceException(500);

    /**
     * Indicates that the resource could not be found. Equivalent to HTTP status:
     * 404 Not Found.
     */
    public static final JsonResourceException NOT_FOUND = new JsonResourceException(404);

    /**
     * Indicates that the resource's current version does not match the version provided.
     * Equivalent to HTTP status: 412 Precondition Failed.
     */
    public static final JsonResourceException VERSION_MISMATCH = new JsonResourceException(412);

    /**
     * Indicates that the resource requires a version, but no version was supplied in the
     * request. Equivalent to draft-nottingham-http-new-status-03 HTTP status:
     * 428 Precondition Required.
     */ 
    public static final JsonResourceException VERSION_REQUIRED = new JsonResourceException(428);

    /**
     * Indicates that the resource is temporarily unable to handle the request. Equivalent to
     * HTTP status: 503 Service Unavailable.
     */
    public static final JsonResourceException UNAVAILABLE = new JsonResourceException(503);

    /** The numeric code of the exception. */
    private final int code;

    /** The short reason phrase of the exception. */
    private final String reason;

    /**
     * Returns the reason phrase for an HTTP error status code, per RFC 2616 and
     * draft-nottingham-http-new-status-03. If no match is found, then a generic reason
     * {@code "Resource Exception"} is returned.
     */
    private static String reason(int code) {
        String result = "Resource Exception"; // default
        switch (code) {
        case 400: result = "Bad Request"; break;
        case 401: result = "Unauthorized"; break;
        case 402: result = "Payment Required"; break;
        case 403: result = "Forbidden"; break;
        case 404: result = "Not Found"; break;
        case 405: result = "Method Not Allowed"; break;
        case 406: result = "Not Acceptable"; break;
        case 407: result = "Proxy Authentication Required"; break;
        case 408: result = "Request Time-out"; break;
        case 409: result = "Conflict"; break;
        case 410: result = "Gone"; break;
        case 411: result = "Length Required"; break;
        case 412: result = "Precondition Failed"; break;
        case 413: result = "Request Entity Too Large"; break;
        case 414: result = "Request-URI Too Large"; break;
        case 415: result = "Unsupported Media Type"; break;
        case 416: result = "Requested range not satisfiable"; break;
        case 417: result = "Expectation Failed"; break;
        case 428: result = "Precondition Required";  break; // draft-nottingham-http-new-status-03
        case 500: result = "Internal Server Error"; break;
        case 501: result = "Not Implemented"; break;
        case 502: result = "Bad Gateway"; break;
        case 503: result = "Service Unavailable"; break;
        case 504: result = "Gateway Time-out"; break;
        case 505: result = "HTTP Version not supported"; break;
        }
        return result;
    }
    
    /**
     * Constructs a new exception with the specified exception code, reason phrase, detail
     * message and cause.
     *
     * @param code the numeric code of the exception.
     * @param reason the short reason phrase of the exception.
     * @param message the detail message of the exception.
     * @param cause the cause of the exception.
     */
    public JsonResourceException(int code, String reason, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.reason = reason;
    }

    /**
     * Constructs a new exception with the specified exception code, reason phrase and detail
     * message.
     *
     * @param code the numeric code of the exception.
     * @param reason the short reason phrase of the exception.
     * @param message the detail message of the exception.
     */
    public JsonResourceException(int code, String reason, String message) {
        super(message);
        this.code = code;
        this.reason = reason;
    }

    /**
     * Constructs a new exception with the specified exception code, reason phrase and cause.
     * The detail message is initialized to the detail message from the specified cause. 
     *
     * @param code the numeric code of the exception.
     * @param reason the short reason phrase of the exception.
     * @param cause the cause of the exception.
     */
    public JsonResourceException(int code, String reason, Throwable cause) {
        this(code, reason, cause.getMessage(), cause);
    }

    /**
     * Constructs a new exception with the specified exception code and reason phrase, and
     * {@code null} as its detail message.
     *
     * @param code the numeric code of the exception.
     * @param reason the short reason phrase of the exception.
     */
    public JsonResourceException(int code, String reason) {
        super();
        this.code = code;
        this.reason = reason;
    }

    /**
     * Constructs a new exception with the specified exception code, and {@code null} as its
     * detail message. If the error code corresponds with a known HTTP error status code,
     * then the reason phrase is set to a corresponding reason phrase, otherwise is
     * set to a generic value {@code "Resource Exception"}.
     *
     * @param code the numeric code of the exception.
     */
    public JsonResourceException(int code) {
        this(code, reason(code));
    }

    /**
     * Constructs a new exception, with the exception code and reason phrase from the
     * specified prototype, and the specified detail message.
     *
     * @param prototype the exception prototype from which to derive error code and reason phrase. 
     * @param message the detail message of the exception.
     */
    public JsonResourceException(JsonResourceException prototype, String message) {
        this(prototype.code, prototype.reason, message);
    }

    /**
     * Constructs a new exception, with the exception code and reason phrase from the
     * specified prototype, and the specified cause. The detail message is initialized to the
     * detail message from the specified cause. 
     *
     * @param prototype the exception prototype from which to derive error code and reason phrase. 
     * @param cause the cause of the exception.
     */
    public JsonResourceException(JsonResourceException prototype, Throwable cause) {
        this(prototype.code, prototype.reason, cause);
    }

    /**
     * Constructs a new exception, with the exception code and reason phrase from the
     * specified prototype, and the specified detail message and cause.
     *
     * @param prototype the exception prototype from which to derive error code and reason phrase. 
     * @param message the detail message of the exception.
     * @param cause the cause of the exception.
     */
    public JsonResourceException(JsonResourceException prototype, String message, Throwable cause) {
        this(prototype.code, prototype.reason, message, cause);
    }

    /**
     * Returns the numeric code of the exception.
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the short reason phrase of the exception.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the exception in a JSON object structure, suitable for inclusion in the
     * entity of an HTTP error response.
     */
    public JsonValue toJsonValue() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("error", code); // required
        if (reason != null) { // optional
            result.put("reason", reason);
        }
        String detail = getMessage();
        if (detail != null) { // optional
            result.put("detail", detail);
        }
        return new JsonValue(result);
    }
}
