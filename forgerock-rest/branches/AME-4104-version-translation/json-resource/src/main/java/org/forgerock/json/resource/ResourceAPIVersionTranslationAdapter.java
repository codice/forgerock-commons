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
 * Adapts requests and responses from one <em>resource API version</em> to another. This allows clean separation of
 * version changes from the actual endpoint implementation, ensuring that only a single endpoint needs to be maintained
 * without conditional logic based on the version requested by a client. Use of this class should be preferred when a
 * version change can be handled entirely by request/response translation (e.g., JSON format changes, renamed
 * actions/parameters etc).
 * <p/>
 * All requests will be translated from the configured client version to the given server version and then forwarded
 * to the delegate request handler. All responses will be translated from the server version back to the client version.
 * The context will be updated with the correct target version for the underlying request handler.
 * <p/>
 * Note: the version translator should be prepared to handle differences in the <em>minor</em> version of requests.
 * That is, all client requests to be translated will have the same <em>major</em> version, but may have earlier
 * <em>minor</em> version numbers than the supported client version (i.e., it is assumed that a version translator for
 * client version 2.1 can also translate requests from a 2.0 client).
 *
 * @since 2.4.0
 */
final class ResourceAPIVersionTranslationAdapter implements RequestHandler {

    private final RequestHandler delegate;
    private final ResourceAPIVersionTranslator translator;

    /**
     * Constructs the version translation adapter to convert requests and responses to/from the given delegate
     * request handler using the given client and server translation versions.
     *
     * @param delegate the underlying request provider to adapt to the client version.
     * @param translator the translation implementation.
     */
    protected ResourceAPIVersionTranslationAdapter(final RequestHandler delegate,
                                                   final ResourceAPIVersionTranslator translator) {
        Reject.ifNull(delegate, translator);
        this.delegate = delegate;
        this.translator = translator;
    }

    /**
     * Handles the given action request, translating between client and server versions.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The action request.
     * @param handler
     *            The result handler to pass result/errors to.
     */
    @Override
    public void handleAction(final ServerContext context, final ActionRequest request,
                                   final ResultHandler<JsonValue> handler) {
        if (!checkClientVersion(context, handler)) {
            return;
        }

        delegate.handleAction(translator.translateContext(context), translator.translateRequest(context, request),
                new TranslatingActionResultHandler(context, request, handler));
    }

    /**
     * Handles the given create request, translating between client and server versions.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The create request.
     * @param handler
     *            The result handler to pass result/errors to.
     */
    @Override
    public void handleCreate(final ServerContext context, final CreateRequest request,
                                   final ResultHandler<Resource> handler) {
        if (!checkClientVersion(context, handler)) {
            return;
        }

        delegate.handleCreate(translator.translateContext(context), translator.translateRequest(context, request),
                new TranslatingResourceResultHandler(context, request, handler));
    }

    /**
     * Handles the given delete request, translating between client and server versions.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The delete request.
     * @param handler
     *            The result handler to pass result/errors to.
     */
    @Override
    public void handleDelete(final ServerContext context, final DeleteRequest request,
                                   final ResultHandler<Resource> handler) {
        if (!checkClientVersion(context, handler)) {
            return;
        }

        delegate.handleDelete(translator.translateContext(context), translator.translateRequest(context, request),
                new TranslatingResourceResultHandler(context, request, handler));
    }

    /**
     * Handles the given patch request, translating between client and server versions.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The patch request.
     * @param handler
     *            The result handler to pass result/errors to.
     */
    @Override
    public void handlePatch(final ServerContext context, final PatchRequest request,
                                  final ResultHandler<Resource> handler) {
        if (!checkClientVersion(context, handler)) {
            return;
        }

        delegate.handlePatch(translator.translateContext(context), translator.translateRequest(context, request),
                new TranslatingResourceResultHandler(context, request, handler));
    }


    /**
     * Handles the given query request, translating between client and server versions.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The query request.
     * @param handler
     *            The result handler to pass query results to.
     */
    @Override
    public void handleQuery(final ServerContext context, final QueryRequest request,
                                  final QueryResultHandler handler) {
        if (!checkClientVersion(context, handler)) {
            return;
        }

        delegate.handleQuery(translator.translateContext(context), translator.translateRequest(context, request),
                new TranslatingQueryResultHandler(context, request, handler));
    }

    /**
     * Handles the given read request, translating between client and server versions.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The read request.
     * @param handler
     *            The result handler to pass result/errors to.
     */
    @Override
    public void handleRead(final ServerContext context, final ReadRequest request,
                                 final ResultHandler<Resource> handler) {
        if (!checkClientVersion(context, handler)) {
            return;
        }

        delegate.handleRead(translator.translateContext(context), translator.translateRequest(context, request),
                new TranslatingResourceResultHandler(context, request, handler));
    }

    /**
     * Handles the given update request, translating between client and server versions.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The update request.
     * @param handler
     *            The result handler to pass result/errors to.
     */
    @Override
    public void handleUpdate(final ServerContext context, final UpdateRequest request,
                                   final ResultHandler<Resource> handler) {
        if (!checkClientVersion(context, handler)) {
            return;
        }

        delegate.handleUpdate(translator.translateContext(context), translator.translateRequest(context, request),
                new TranslatingResourceResultHandler(context, request, handler));
    }

    /**
     * Checks that a request can be translated according to the configured client API versions.
     *
     * @param context the context to check for version translation compatibility.
     * @param handler the result handler to report version mismatch errors to.
     */
    private boolean checkClientVersion(final ServerContext context, final ResultHandler<?> handler) {
        final AcceptAPIVersionContext versionContext = context.asContext(AcceptAPIVersionContext.class);
        final Version supportedClientVersion = translator.getSupportedClientVersion();
        if (!matches(supportedClientVersion, versionContext.getResourceVersion())) {

            handler.handleError(ResourceException.getException(ResourceException.BAD_REQUEST,
                    "Unsupported resource API version: " + versionContext.getResourceVersion()));
            return false;
        }

        return true;
    }

    /**
     * TODO: remove when Phill's Version.matches method is checked in.
     */
    private boolean matches(Version supported, Version supplied) {
        return supported.getMajor() == supplied.getMajor() && supported.getMinor() >= supplied.getMinor();
    }

    /**
     * Abstract result handler adapter that ensures any errors are appropriately translated before being passed to
     * the underlying concrete result handler.
     *
     * @param <R> the type of requests that this handler will process.
     * @param <T> the type of results that this handler expects.
     */
    private abstract class AbstractTranslatingResultHandler<R extends Request, T> implements ResultHandler<T> {
        protected final ServerContext context;
        protected final R request;
        protected final ResultHandler<T> delegate;

        protected AbstractTranslatingResultHandler(final ServerContext context, final R request,
                                                   final ResultHandler<T> delegate) {
            this.context = context;
            this.request = request;
            this.delegate = delegate;
        }

        @Override
        public void handleError(final ResourceException error) {
            delegate.handleError(translator.translateException(context, request, error));
        }
    }

    /**
     * Translates action responses from the server version to the client version.
     */
    private final class TranslatingActionResultHandler
            extends AbstractTranslatingResultHandler<ActionRequest, JsonValue> {

        private TranslatingActionResultHandler(final ServerContext context, final ActionRequest request,
                                               final ResultHandler<JsonValue> delegate) {
            super(context, request, delegate);
        }

        @Override
        public void handleResult(final JsonValue result) {
            delegate.handleResult(translator.translateActionResponse(context, request, result));
        }
    }

    /**
     * Translates resource responses from the server version to the client version.
     */
    private final class TranslatingResourceResultHandler extends AbstractTranslatingResultHandler<Request, Resource> {

        private TranslatingResourceResultHandler(final ServerContext context, final Request request,
                                                 final ResultHandler<Resource> delegate) {
            super(context, request, delegate);
        }

        @Override
        public void handleResult(final Resource result) {
            delegate.handleResult(translator.translateResourceResponse(context, request, result));
        }
    }

    /**
     * Translates query results from the server version to the client version.
     */
    private final class TranslatingQueryResultHandler
            extends AbstractTranslatingResultHandler<QueryRequest, QueryResult> implements QueryResultHandler {
        private TranslatingQueryResultHandler(final ServerContext context, final QueryRequest request,
                                              final QueryResultHandler delegate) {
            super(context, request, delegate);
        }

        private QueryResultHandler getDelegate() {
            return (QueryResultHandler) delegate;
        }

        @Override
        public boolean handleResource(final Resource resource) {
            return getDelegate().handleResource(translator.translateResourceResponse(context, request, resource));
        }

        @Override
        public void handleResult(final QueryResult result) {
            delegate.handleResult(translator.translateQueryResult(context, request, result));
        }
    }
}
