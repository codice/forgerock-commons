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
 * Dispatches requests to a {@link RequestHandler} using a visitor implementation. Useful for tests that need to
 * verify behaviour of a given RequestHandler for a range of different types of request, as the requests can be
 * provided by a TestNG data provider and then dispatched to the correct method using this visitor.
 */
@SuppressWarnings("unchecked")
final class RequestDispatcher implements RequestVisitor<Void, ResultHandler<?>> {
    private final RequestHandler requestHandler;
    private final ServerContext context;

    public RequestDispatcher(final RequestHandler requestHandler, final ServerContext context) {
        this.requestHandler = requestHandler;
        this.context = context;
    }

    @Override
    public Void visitActionRequest(final ResultHandler<?> resultHandler, final ActionRequest request) {
        requestHandler.handleAction(context, request, (ResultHandler<JsonValue>) resultHandler);
        return null;
    }

    @Override
    public Void visitCreateRequest(final ResultHandler<?> resultHandler, final CreateRequest request) {
        requestHandler.handleCreate(context, request, (ResultHandler<Resource>) resultHandler);
        return null;
    }

    @Override
    public Void visitDeleteRequest(final ResultHandler<?> resultHandler, final DeleteRequest request) {
        requestHandler.handleDelete(context, request, (ResultHandler<Resource>) resultHandler);
        return null;
    }

    @Override
    public Void visitPatchRequest(final ResultHandler<?> resultHandler, final PatchRequest request) {
        requestHandler.handlePatch(context, request, (ResultHandler<Resource>) resultHandler);
        return null;
    }

    @Override
    public Void visitQueryRequest(final ResultHandler<?> resultHandler, final QueryRequest request) {
        requestHandler.handleQuery(context, request, (QueryResultHandler) resultHandler);
        return null;
    }

    @Override
    public Void visitReadRequest(final ResultHandler<?> resultHandler, final ReadRequest request) {
        requestHandler.handleRead(context, request, (ResultHandler<Resource>) resultHandler);
        return null;
    }

    @Override
    public Void visitUpdateRequest(final ResultHandler<?> resultHandler, final UpdateRequest request) {
        requestHandler.handleUpdate(context, request, (ResultHandler<Resource>) resultHandler);
        return null;
    }
}
