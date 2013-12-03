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

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Class that breaks down an HTTP response to its simplest components for
 * use throughout the rest of the system
 */
public class ResponseUnwrapper implements IResponseUnwrapper {

    private HttpResponse response;
    private ActionType successAction;
    private ActionType failAction;

    public ResponseUnwrapper(HttpResponse response, ActionType successAction, ActionType failAction) {
        this.response = response;
        this.successAction = successAction;
        this.failAction = failAction;
    }

    /**
     * Returns the entity contents of an {@link org.apache.http.HttpResponse} as a String alongside its
     * status code. Ensures that we cannot pass back an HTTP Response directory to the UI thread, which
     * would break the Android app as there would be network activity on it.
     *
     * @return The String contents of the response's entity
     * @throws java.io.IOException If anything goes wrong reading the entity
     */
    public UnwrappedResponse unwrapHttpResponse() throws IOException {

        String contents = EntityUtils.toString(response.getEntity());

        return new UnwrappedResponse(response.getStatusLine().getStatusCode(), contents, successAction, failAction);
    }
}
