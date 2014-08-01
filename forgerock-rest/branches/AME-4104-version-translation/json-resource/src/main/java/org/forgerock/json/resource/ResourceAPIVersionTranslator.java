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
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.json.resource;

import org.forgerock.json.fluent.JsonValue;

/**
 * Interface for translations between different resource API versions of requests and responses.
 *
 * @since 3.0.0
 */
public interface ResourceAPIVersionTranslator {

    /**
     * The API version of the client that this translator can translate requests/responses for.
     *
     * @return the supported client API version. May not be null.
     */
    Version getSupportedClientVersion();

    /**
     * The API version of the server endpoint that this translator can translate requests/responses for.
     *
     * @return the supported server API version. May not be null.
     */
    Version getSupportedServerVersion();

    /**
     * Translates requests from the client version to the server version. Implementations should be prepared to handle
     * differences in the minor version of the request compared to the supported client version. For example, a
     * translator that supports client version {@code 2.1} may be asked to translate a request for version {@code 2.0},
     * but will not be asked to translate version {@code 2.2} or {@code 3.0}.
     *
     * @param context the (untranslated) context of the request.
     * @param request the untranslated request.
     * @param <R> the type of request.
     * @return a translated request matching the expected server resource API version.
     */
    <R extends Request> R translateRequest(ServerContext context, R request);

    /**
     * Translates action responses from the server version back to the client version.
     *
     * @param context the (untranslated) context of the request.
     * @param request the untranslated request.
     * @param value the server response to the action request to be translated (server version).
     * @return the translated action response suitable for the configured client resource API version.
     */
    JsonValue translateActionResponse(ServerContext context, ActionRequest request, JsonValue value);

    /**
     * Sub-classes should implement this method to translate resource responses from the server version back to the
     * client version. This will be called for all methods that return a Resource response (including query results).
     *
     * @param context the (untranslated) context of the request.
     * @param request the untranslated request.
     * @param resource the server response to the request to be translated (server version).
     * @return the translated resource suitable for the configured client resource API version.
     */
    Resource translateResourceResponse(ServerContext context, Request request, Resource resource);

    /**
     * Sub-classes should implement this method to translate query results from the server API version back to the
     * client API version.
     *
     * @param context the (untranslated) context of the request.
     * @param request the untranslated request.
     * @param result the query result to be translated (server version).
     * @return the translated query result suitable for the configured client resource API version.
     */
    QueryResult translateQueryResult(ServerContext context, QueryRequest request, QueryResult result);

    /**
     * Translates any resource exceptions from the server version to a suitable exception for the given client version.
     * Sub-classes should override this if exceptions have changed between API versions.
     *
     * @param context the (untranslated) context of the request.
     * @param request the request that resulted in an error (client version).
     * @param error the exception that was thrown, to be translated.
     * @return a suitable equivalent exception for the configured client API version.
     */
    ResourceException translateException(ServerContext context, Request request, ResourceException error);

    /**
     * Translates the server context from the client version to the server version.
     *
     * @param context the context to translate.
     * @return an equivalent context adapted to the expected server API version.
     */
    ServerContext translateContext(final ServerContext context);
}
