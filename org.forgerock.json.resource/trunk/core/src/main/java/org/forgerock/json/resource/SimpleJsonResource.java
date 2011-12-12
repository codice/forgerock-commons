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

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.fluent.JsonValueException;

/**
 * A convenience class that breaks-out requests into specific methods.
 *
 * @author Paul C. Bryan
 */
public class SimpleJsonResource implements JsonResource {

    /** TODO: Description. */
    private enum Method {
        create, read, update, delete, patch, query, action
    }

    /**
     * TODO: Description.
     * <p>
     * This method catches any thrown {@code JsonValueException}, and rethrows it as a
     * {@link JsonResourceException#BAD_REQUEST}.
     */ 
    @Override
    public JsonValue handle(JsonValue request) throws JsonResourceException {
        try {
            try {
                switch (request.get("method").required().asEnum(Method.class)) {
                case create: return create(request);
                case read: return read(request);
                case update: return update(request);
                case delete: return delete(request);
                case patch: return patch(request);
                case query: return query(request);
                case action: return action(request);
                default: throw JsonResourceException.BAD_REQUEST;
                }
            } catch (JsonValueException jve) {
                throw new JsonResourceException(JsonResourceException.BAD_REQUEST, jve);
            }
        }
        catch (Exception e1) {
            try {
                onException(e1); // give handler opportunity to throw its own exception
                throw e1;
            } catch (Exception e2) {
                if (e2 instanceof JsonResourceException) { // no rethrowing necessary
                    throw (JsonResourceException)e2;
                } else { // need to rethrow as resource exception
                    throw new JsonResourceException(JsonResourceException.INTERNAL_ERROR, e2);
                }
            }
        }
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     * @throws JsonResourceException TODO.
     */
    protected JsonValue create(JsonValue request) throws JsonResourceException {
        throw JsonResourceException.FORBIDDEN;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     * @throws JsonResourceException TODO.
     */
    protected JsonValue read(JsonValue request) throws JsonResourceException {
        throw JsonResourceException.FORBIDDEN;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     * @throws JsonResourceException TODO.
     */
    protected JsonValue update(JsonValue request) throws JsonResourceException {
        throw JsonResourceException.FORBIDDEN;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     * @throws JsonResourceException TODO.
     */
    protected JsonValue delete(JsonValue request) throws JsonResourceException {
        throw JsonResourceException.FORBIDDEN;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     * @throws JsonResourceException TODO.
     */
    protected JsonValue patch(JsonValue request) throws JsonResourceException {
        throw JsonResourceException.FORBIDDEN;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     * @throws JsonResourceException TODO.
     */
    protected JsonValue query(JsonValue request) throws JsonResourceException {
        throw JsonResourceException.FORBIDDEN;
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     * @throws JsonResourceException TODO.
     */
    protected JsonValue action(JsonValue request) throws JsonResourceException {
        throw JsonResourceException.FORBIDDEN;
    }

    /**
     * Provides the ability to handle an exception by taking additional steps such as
     * logging, and optionally to override by throwing its own {@link JsonResourceException}.
     * This implementation does nothing; it is intended to be overridden by a subclass.
     *
     * @param exception the exception that was thrown.
     * @throws JsonResourceException an optional exception to be thrown instead.
     */
    protected void onException(Exception exception) throws JsonResourceException {
        // default implementation does nothing
    }
}
