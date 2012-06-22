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
 * Copyright © 2012 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.resource;

/**
 * The context associated with a request currently being processed by a JSON
 * resource provider. A request context can be used to query state information
 * about the request. Implementations may provide additional information,
 * time-stamp information, HTTP headers, etc.
 */
public interface RequestContext {
    // TODO: methods for determining whether or not the request has been
    // cancelled?

    /**
     * Returns the specified resource name field, if present.
     * <p>
     * Resource providers are registered against a resource name template, such
     * as:
     *
     * <pre>
     * /users/{userId}/devices/{deviceId}
     * </pre>
     *
     * This method provides access to the parsed path elements, e.g.
     * {@code userId} and {@code deviceId} above.
     *
     * @param fieldName
     *            The name of the path element to be retrieved.
     * @return The named path element.
     */
    String getNamedPathElement(String fieldName);
}
