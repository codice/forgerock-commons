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

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * This abstract class acts as a parent to each of the types needed to talk to ForgeRock
 * REST services via asynchronous tasks. We use Apache HTTP Client as the backing for
 * our HTTP comms.
 * @param <T> The HTTPRequest type that the concrete class uses, e.g. HttpPost
 */
public abstract class ASyncRestRequest<T extends HttpRequestBase> extends AsyncTask<Void, Integer, UnwrappedResponse> {

    private final String TAG = "ASyncRestRequest";

    private final T request;
    private final Listener<UnwrappedResponse> listener;
    private final ActionType successAction;
    private final ActionType failAction;

    private ActionType action;

    //settable
    private HttpClient httpClient;
    private IResponseUnwrapperFactory unwrapperFactory;


    /**
     * Constructs a new ASync Request
     *
     * @param listener The listening class
     * @param successAction Indicates the HTTP request succeeded with 200
     * @param failAction Indicates a failure during the asynchronous request
     * @param request The request itself, an extension of {@link org.apache.http.client.methods.HttpRequestBase}
     */
    protected ASyncRestRequest(Listener<UnwrappedResponse> listener, ActionType successAction,
                            ActionType failAction, T request, HashMap<String, String> params,
                            HashMap<String, String> headers) {
        this.listener = listener;
        this.successAction = successAction;
        this.failAction = failAction;
        this.request = request;
        this.action = failAction; //assume failure

        if (headers != null) {
            for (String key : headers.keySet()) {
                request.addHeader(key, headers.get(key));
            }
        }

        if (params != null) {
            appendParams(params);
        }
    }

    /**
     * The main bulk of this class. This is performed before onPostExecute.
     *
     * The response from this method is the unwrapped HTTP response. This includes
     * two fields - the status code (an int) and the response entity (if appropriate)
     * as a String.
     *
     * The {@link Listener} that was set in the constructor receives the
     * message. If the {@link ActionType} returned at this point is the failAction something
     * has gone wrong during the HTTP communication stage.
     *
     * Past this point, failures detected in the response itself should change the
     * {@link ActionType} to the failure type if appropriate, as the response is
     * propagated to any further listeners from the original client.
     *
     * @param voids
     * @return Returns to the {@link Listener#onEvent(ActionType, Object)} method,
     *  via {@link #onPostExecute(UnwrappedResponse)}
     */
    @Override
    protected UnwrappedResponse doInBackground(Void... voids) {

        HttpResponse res = null;
        UnwrappedResponse unwrappedResponse = null;
        HttpClient client = getHttpClient();
        boolean success = false;

        try {
            res = client.execute(request);
            success = true;
        } catch (IOException e) {
            fail(TAG, "HTTP Client unable to execute request.");
        }

        if (success) {
            try {
                IResponseUnwrapper unwrapper = getUnwrapperFactory().createUnwrapper(res, successAction, failAction);
                unwrappedResponse = unwrapper.unwrapHttpResponse();
                action = successAction;
            } catch (IOException e) {
                fail(TAG, "Unable to unwrap HTTP response.");
            }

        }

        return unwrappedResponse;
    }

    /**
     * For adjusting the HttpClient if necessary
     */
    public void setHttpClient(HttpClient client) {
        this.httpClient = client;
    }

    /**
     * Gets the http client impl. or gives default
     * @return
     */
    private HttpClient getHttpClient() {
        if (httpClient == null) {
            return new DefaultHttpClient();
        } else {
            return httpClient;
        }
    }

    /**
     * For adjusting the ResponseUnwrapperFactory if necessary
     */
    public void setResponseUnwrapperFactory(IResponseUnwrapperFactory factory) {
        this.unwrapperFactory = factory;
    }

    /**
     * Gets the response unwrapper impl. or gives default
     */
    private IResponseUnwrapperFactory getUnwrapperFactory() {
        if (unwrapperFactory == null) {
            return new ResponseUnwrapperFactory();
        } else {
            return unwrapperFactory;
        }
    }

    /**
     * Allows for error reporting and setting of failure action-type.
     *
     * @param tag Class identifier
     * @param msg Message to be logged
     */
    protected void fail(String tag, String msg) {
        action = failAction;
        Log.e(tag, msg);
    }

    /**
     * Allows for querying of the current ActionType
     *
     * @return
     */
    public ActionType getCurrentActionState() {
        return action;
    }

    /**
     * This function passes back to the calling activity via the {@link Listener<T>} interface, through the
     * {@link org.forgerock.openam.mobile.commons.Listener<T>#onEvent(org.forgerock.openam.mobile.commons.ActionType, T)}.
     *
     * @param data An {@link UnwrappedResponse} including the status code and HTTP Response entity as String
     */
    @Override
    protected void onPostExecute(final UnwrappedResponse data) {
        listener.onEvent(action, data);
    }

    /**
     * Accessor for the HTTP request underpinning this task
     *
     * @return
     */
    public T getRequest() {
        return request;
    }

    /**
     * Appends parameter information to the query string of this underlying request
     */
    private void appendParams(HashMap<String, String> params) {

        // No URIBuilder included in the HTTPClient inside Android; Uri.Builder not so lovely.
        // So lets do it by hand
        if (params == null || params.size() == 0) {
            return;
        }

        HttpRequestBase request = getRequest();

        boolean first = false;

        StringBuilder sb = new StringBuilder(request.getURI().toString());

        if (!sb.toString().contains("?")) {
            first = true;
        }

        for(String key : params.keySet()) {
            if (first) {
                sb.append("?");
            } else {
                sb.append("&");
            }

            try {
                sb.append(key).append("=").append(URLEncoder.encode(params.get(key), RestConstants.UTF8));
            } catch (UnsupportedEncodingException e) {
                fail(TAG, "Unable to URL encode params to add to request.");
            }
        }

        URI validUri = null;
        try {
            validUri = new URI(sb.toString());
        } catch (URISyntaxException e) {
            fail(TAG, "Unable to validate URL after adding HTTP parameters.");
        }

        request.setURI(validUri);
    }
}