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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple android utils, used across both applications.
 */
public class AndroidUtils {

    public static void showToast(CharSequence msg, Context context) {
        showToast(msg, Toast.LENGTH_SHORT, context);
    }

    public static void showToast(CharSequence msg, int length, Context context) {
        Toast.makeText(context, msg, length).show();
    }


    /**
     * Informs the supplied resource to attempt to load from the supplied config string
     *
     * @param toLoad A resource
     * @param configStr A JSON config string
     */
    public static void loadConfig(Resource toLoad, String configStr) {
        if (configStr != null) {
            try {
                toLoad.fromJSON(new JSONObject(configStr));
            } catch (JSONException e) {
                Log.e("Loading selected server configuration fails", e.toString());
            }
        }
    }
}
