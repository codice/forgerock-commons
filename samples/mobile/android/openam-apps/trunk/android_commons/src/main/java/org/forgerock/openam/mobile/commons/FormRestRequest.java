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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

/**
 * The concrete implementation of an asynchronous HTTP POST request.
 * The params passed to it are url-encoded and attached to the
 * request uri.
 */
public class FormRestRequest extends ASyncRestRequest<HttpPost> {

    private static final String TAG = "FormRestRequest";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * Creates an instance of a FormRestRequest
     *
     * @param listener The listening class
     * @param successAction The success action indicator
     * @param failAction The fail action indicator
     * @param request The HTTP GET request to execute
     * @param params Any parameters to included as POST data
     */
    public FormRestRequest(Listener<UnwrappedResponse> listener, ActionType successAction,
                              ActionType failAction, HttpPost request, HashMap<String, String> params,
                              HashMap<String, String> headers, HashMap<String, String> formParameters) {
        super(listener, successAction, failAction, request, params, headers);
        insertData(formParameters);
    }

    /**
     * Alters the request to insert additional HTTP POST x-www-form-urlencoded data
     */
    public void insertData(HashMap<String, String> formParameters) {

        if (formParameters == null) {
            return;
        }

        HttpPost request = getRequest();

        try {
            List<NameValuePair> data = new ArrayList<NameValuePair>();

            for(String s : formParameters.keySet()) {
                NameValuePair nvp = new BasicNameValuePair(s, formParameters.get(s));
                data.add(nvp);
            }

            UrlEncodedFormEntity formData = new UrlEncodedFormEntity(data);

            formData.setContentType(CONTENT_TYPE);
            request.setEntity(formData);
        } catch (UnsupportedEncodingException e) {
            fail(TAG, "Unable to set entity.");
        }
    }
}