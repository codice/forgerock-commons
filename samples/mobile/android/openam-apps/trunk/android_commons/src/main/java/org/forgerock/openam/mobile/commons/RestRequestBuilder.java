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
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

/**
 * Implementation of the {@link IRestRequestBuilder} used to generate different
 * requests for our clients.
 */
public class RestRequestBuilder implements IRestRequestBuilder {

    private Listener<UnwrappedResponse> listener;
    private ActionType successAction, failAction;
    private HashMap<String, String> headers;
    private String url;
    private HashMap<String, String> params;

    //optional, only for createPostJSONRestRequest, can be null
    private JSONObject json;
    //optional, only for createFormRestRequest, can be null
    private HashMap<String, String> formParameters;

    /**
     * Generates a new post request whose payload content-type is application/x-www-form-encoded
     * @return
     */
    public ASyncRestRequest<HttpPost> createPostFormRestRequest() {
        return new FormRestRequest(listener, successAction, failAction, new HttpPost(url), params, headers, formParameters);
    }

    /**
     * Generates a new GET request
     * @return
     */
    public ASyncRestRequest<HttpGet> createGetRestRequest() {
        return new GetRestRequest(listener, successAction, failAction, new HttpGet(url), params, headers);
    }

    /**
     * Generates a new POST request whose payload content-type is application/json
     * @return
     */
    public ASyncRestRequest<HttpPost> createPostJSONRestRequest() {
        return new JSONRestRequest(listener, successAction, failAction, new HttpPost(url), params, headers, json);
    }

    public RestRequestBuilder setListener(Listener<UnwrappedResponse> listener) {
        this.listener = listener;
        return this;
    }

    public RestRequestBuilder setSuccessAction(ActionType successAction) {
        this.successAction = successAction;
        return this;
    }

    public RestRequestBuilder setFailureAction(ActionType failAction) {
        this.failAction = failAction;
        return this;
    }

    public RestRequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public RestRequestBuilder setQueryParams(HashMap<String, String> params) {
        this.params = params;
        return this;
    }

    public RestRequestBuilder setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RestRequestBuilder setFormParams(HashMap<String, String> formParameters) {
        this.formParameters = formParameters;
        return this;
    }

    public RestRequestBuilder setJson(JSONObject json) {
        this.json = json;
        return this;
    }
}
