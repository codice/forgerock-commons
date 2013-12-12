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

package org.forgerock.openam.mobile.example.authentication.activities.authenticate.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creates {@link Authenticate} objects from JSON strings.
 */
public class AuthenticateFactory {

    public static Authenticate createFromString(String objectString) throws JSONException {
        return createFromJSON(new JSONObject(objectString));
    }

    public static Authenticate createFromJSON(JSONObject authJson) throws JSONException {
        final Authenticate auth = new Authenticate();

        auth.setAuthId(authJson.getString("authId"));
        auth.setStage(authJson.getString("stage"));
        auth.setTemplate(authJson.getString("template"));

        final JSONArray callbackJson = authJson.getJSONArray("callbacks");
        final Callback[] callbacks = new Callback[callbackJson.length()];

        for (int i = 0; i < callbackJson.length(); i++) {
            JSONObject callback = callbackJson.getJSONObject(i);

            Callback c = new Callback();
            c.setType(callback.getString("type"));

            JSONArray outputCallbacks = callback.getJSONArray("output");
            JSONArray inputCallbacks = callback.getJSONArray("input");

            c.setOutput(gatherCallbackElements(outputCallbacks));
            c.setInput(gatherCallbackElements(inputCallbacks));

            callbacks[i] = c;
        }

        auth.setCallbacks(callbacks);

        return auth;
    }

    private static CallbackElement[] gatherCallbackElements(JSONArray callbacks) throws JSONException {

        final CallbackElement[] elements = new CallbackElement[callbacks.length()];

        for(int i = 0; i < callbacks.length(); i++) {
            JSONObject callbackElement = callbacks.getJSONObject(i);
            CallbackElement ce = new CallbackElement();
            ce.setName(callbackElement.getString("name"));
            ce.setValue(callbackElement.getString("value"));

            elements[i] = ce;
        }

        return elements;
    }

}
