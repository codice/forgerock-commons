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
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2012-2014 ForgeRock AS.
 */

package org.forgerock.json.resource;

import org.forgerock.json.fluent.JsonValue;

/**
 * Represents an extension of the {@link ResultHandler} contract to support
 * batching requests and handling the results as a single transaction.
 */
public interface BatchRequestHandler {

    /**
     * Handles performing one or more operations on a resource in any combination
     * and optionally returns an associated result.
     * <p>
     * The operations in a batch may contain any mix of requestTypes having only
     * the handler in common.  A handler of this type is expected to process the
     * batch in the most efficient manner as a specialization of the iterative
     * approach found in the {@link Router}.  Resources that do not support a form
     * of batch processing more efficient that interation need not implement this
     * interface.
     *
     * @param context
     *            The request server context, such as associated principal.
     * @param request
     *            The batch request.
     * @param handler
     *            The result handler to be notified on completion.
     */
    void handleBatch(ServerContext context, ActionRequest request, ResultHandler<JsonValue> handler);
}
