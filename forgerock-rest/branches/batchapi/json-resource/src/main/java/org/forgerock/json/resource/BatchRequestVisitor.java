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
 * Copyright © 2012 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.resource;


/**
 * An extension of {@link RequestVisitor} to support batch requests.
 * <p>
 * Classes implementing this interface indicate their support of the batch operation.
 *
 * @param <R>
 *            The return type of this visitor's methods. Use
 *            {@link Void} for visitors that do not need to return
 *            results.
 * @param <P>
 *            The type of the additional parameter to this visitor's methods.
 *            Use {@link Void} for visitors that do not need an
 *            additional parameter.
 */
public interface BatchRequestVisitor<R, P> {

    /**
     * Visits a batch request.
     *
     * @param p
     *            A visitor specified parameter.
     * @param request
     *            The batch request.
     * @return Returns a visitor specified result.
     */
    R visitBatchRequest(P p, BatchRequest request);
}
