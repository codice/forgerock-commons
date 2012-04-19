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

package org.forgerock.resource.framework;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.resource.exception.ResourceException;

/**
 * Provider API for resources. 
 * Can be used for cross cutting functionality that is not method specific; 
 * use the Resource interface for method specific implementations instead.
 * 
 * A resource that has a JSON representation. Handles JSON resource requests.
 *
 * @author Paul C. Bryan
 */
public interface JsonResourceProvider {

    /**
     * Handles a JSON resource request and returns a JSON resource response. If a request
     * results in an error, a {@code JsonResourceException} is thrown.
     * <p>
     * <strong>Note:</strong> This method <strong>should not</strong> modify the request.
     *
     * @param request the JSON resource request.
     * @return the JSON resource response.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue handle(JsonValue request) throws ResourceException;
}
