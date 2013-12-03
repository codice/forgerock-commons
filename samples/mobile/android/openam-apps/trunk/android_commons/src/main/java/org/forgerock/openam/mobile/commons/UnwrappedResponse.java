/*
 * Copyright 2013 ForgeRock AS.
 *
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
 */

package org.forgerock.openam.mobile.commons;

/**
 * Convenience class for storing information about the response
 * coming back from one of our REST clients
 */
public class UnwrappedResponse {

    final int statusCode;
    final String entityContent;
    final ActionType success;
    final ActionType fail;

    public UnwrappedResponse(int statusCode, String entityContent, ActionType success, ActionType fail) {
        this.statusCode = statusCode;
        this.entityContent = entityContent;
        this.success = success;
        this.fail = fail;
    }

    /**
     * The HTTP response status code. Ideally, 200.
     *
     * @return
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Contents of the HTTP Response, as a String
     *
     * @return
     */
    public String getEntityContent() {
        return entityContent;
    }

    /**
     * What the type of future propgations should be if this
     * response is determined to be valid
     *
     * @return The success-state actiontype
     */
    public ActionType getSuccessActionType() {
        return success;
    }

    /**
     * What the type of future propgations should be if this
     * response is determined to be invalid.
     *
     * @return The failure-state actiontype
     */
    public ActionType getFailActionType() {
        return fail;
    }
}
