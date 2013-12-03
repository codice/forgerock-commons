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

import java.util.HashMap;
import org.apache.http.client.methods.HttpGet;

/**
 * The concrete implementation of an asynchronous HTTP GET request.
 * The params passed to it are url-encoded and attached to the
 * request uri.
 */
public class GetRestRequest extends ASyncRestRequest<HttpGet> {

    private static final String TAG = "GetRestRequest";

    /**
     * Creates an instance of a GetRestRequest
     *
     * @param listener The listening class
     * @param successAction The success action indicator
     * @param failAction The fail action indicator
     * @param url The HTTP GET request to execute
     * @param params Any parameters to appended to the query string
     */
    public GetRestRequest(Listener<UnwrappedResponse> listener, ActionType successAction,
                             ActionType failAction, HttpGet url, HashMap<String, String> params,
                             HashMap<String, String> headers) {
        super(listener, successAction, failAction, url, params, headers);
    }

}
