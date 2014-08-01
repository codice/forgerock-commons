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
import org.forgerock.util.Reject;

/**
 * Provides default implementations of version translation methods that simply return the original values without any
 * translation. Sub-classes can override particular parts of the translation process.
 *
 * @since 3.0.0
 */
public abstract class AbstractResourceAPIVersionTranslator implements ResourceAPIVersionTranslator {
    private final Version supportedClientVersion;
    private final Version supportedServerVersion;

    /**
     * Constructs the version translator with the given supported client and server resource API versions.
     *
     * @param supportedClientVersion the supported client resource API version to translate from. Not null.
     * @param supportedServerVersion the supported server resource API version to translate to. Not null.
     */
    protected AbstractResourceAPIVersionTranslator(final Version supportedClientVersion,
                                                   final Version supportedServerVersion) {
        Reject.ifNull(supportedClientVersion, supportedServerVersion);
        this.supportedClientVersion = supportedClientVersion;
        this.supportedServerVersion = supportedServerVersion;
    }

    @Override
    public Version getSupportedClientVersion() {
        return supportedClientVersion;
    }

    @Override
    public Version getSupportedServerVersion() {
        return supportedServerVersion;
    }

    /**
     * {@inheritDoc}
     *
     * Default implementation returns the original request, without translation.
     */
    @Override
    public <R extends Request> R translateRequest(final ServerContext context, final R request) {
        return request;
    }

    /**
     * {@inheritDoc}
     *
     * Default implementation returns the original response, without translation.
     */
    @Override
    public JsonValue translateActionResponse(final ServerContext context, final ActionRequest request,
                                             final JsonValue value) {
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * Default implementation returns the original response, without translation.
     */
    @Override
    public Resource translateResourceResponse(final ServerContext context, final Request request,
                                              final Resource resource) {
        return resource;
    }

    /**
     * {@inheritDoc}
     *
     * Default implementation returns the original result, without translation.
     */
    @Override
    public QueryResult translateQueryResult(final ServerContext context, final QueryRequest request,
                                            final QueryResult result) {
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * Default implementation returns the original error, without translation.
     */
    @Override
    public ResourceException translateException(final ServerContext context, final Request request,
                                                final ResourceException error) {
        return error;
    }

    /**
     * {@inheritDoc}
     *
     * Default implementation returns a clone of the original context, with any resource version in the
     * {@link AcceptAPIVersionContext} updated to match the translated version.
     */
    @Override
    public ServerContext translateContext(final ServerContext serverContext) {
        ServerContext result = serverContext;

        if (serverContext.containsContext(AcceptAPIVersionContext.class)) {
            final AcceptAPIVersionContext versionContext = serverContext.asContext(AcceptAPIVersionContext.class);

            if (!getSupportedServerVersion().equals(versionContext.getResourceVersion())) {

                // Need to update the resource version in the version context. As context is immutable, we create a new
                // context, wrapping the old one. The new version will take precedence in the context chain.

                final AcceptAPIVersion newVersion =
                        AcceptAPIVersion.newBuilder()
                                        .withDefaultProtocolVersion(versionContext.getProtocolVersion())
                                        .withDefaultResourceVersion(getSupportedServerVersion())
                                        .build();
                final AcceptAPIVersionContext newContext =
                        new AcceptAPIVersionContext(serverContext, versionContext.getProtocolName(), newVersion);

                result = new ServerContext(serverContext.getId(), newContext);
            }
        }

        return result;
    }
}
