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
 */

package org.forgerock.openam.mobile.commons;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

/**
 * The concrete implementation of an asynchronous HTTP POST request.
 * The params passed to it are included as JSON data in the request.
 */
public class JSONRestRequest extends ASyncRestRequest<HttpPost> {

    private static final String TAG = "JSONRestRequest";

    private static final String CONTENT_TYPE = "application/json";

    /**
     * Creates an instance of a JSONRestRequest
     *
     * @param listener The listening class
     * @param successAction The success action indicator
     * @param failAction The fail action indicator
     * @param url The HTTP GET request to execute
     * @param json JSON content to be added as an entity in the request
     */
    public JSONRestRequest(Listener<UnwrappedResponse> listener, ActionType successAction,
                              ActionType failAction, HttpPost url, HashMap<String, String> params,
                              HashMap<String, String> headers, JSONObject json) {
        super(listener, successAction, failAction, url, params, headers);
        insertData(json);
    }

    /**
     * Inserts the supplied JSON data into the request
     */
    public void insertData(JSONObject json) {

        if (json == null) {
            json = new JSONObject();
        }

        try {
            HttpPost request = getRequest();
            StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType(CONTENT_TYPE);
            request.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            fail(TAG, "Unable to set entity");
        }
    }

}