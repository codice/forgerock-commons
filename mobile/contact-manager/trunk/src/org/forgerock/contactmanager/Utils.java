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
 *
 *       Copyright 2013-2014 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import static android.text.TextUtils.isEmpty;
import static org.forgerock.contactmanager.AppContext.*;
import static org.forgerock.contactmanager.Constants.ALL_SERVER_CONFIGURATIONS;
import static org.forgerock.contactmanager.Constants.SELECTED_SERVER_CONFIGURATION;
import static org.forgerock.contactmanager.MapperConstants.RESULT;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class contains utilities function for the android application.
 */
public final class Utils {

    /** Prevent instantiation. */
    private Utils() {
        throw new AssertionError();
    }

    /**
     * Deletes a selected server from shared preferences.
     *
     * @param pref
     *            The user shared preferences.
     * @param target
     *            The server to delete.
     * @return {@code true} if the server has been successfully being deleted.
     */
    static final boolean deleteServerConfigurationFromPreferences(final String target) {
        final List<ServerConfiguration> registered = loadRegisteredServerList();
        final Iterator<ServerConfiguration> it = registered.iterator();
        final JSONArray jsonArray = new JSONArray();
        while (it.hasNext()) {
            final ServerConfiguration current = it.next();
            if (!current.getServerName().equals(target)) {
                jsonArray.put(current.toJSON());
            }
        }

        final Editor edit = getPref().edit();
        edit.remove("srvconf");
        edit.commit();
        edit.putString("srvconf", jsonArray.toString());
        edit.commit();

        setServerConfiguration(null);

        return true;
    }

    /**
     * Retrieves a list of server configurations from user preferences.
     *
     * @param pref
     *            User shared preferences for this application.
     * @return A linked list of server configurations.
     */
    static final List<ServerConfiguration> loadRegisteredServerList() {
        List<ServerConfiguration> registered = null;
        final String registeredListPref = getPref().getString(ALL_SERVER_CONFIGURATIONS, null);
        if (registeredListPref != null) {
            registered = new LinkedList<ServerConfiguration>();
            try {
                final JSONArray jsonArray = new JSONArray(registeredListPref);
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject jsonObj = new JSONObject(jsonArray.get(i).toString());
                    final ServerConfiguration current = ServerConfiguration.fromJSON(jsonObj);
                    registered.add(current);
                }
            } catch (final JSONException e) {
                Log.e("Loading server configuration fails", e.toString());
            }
        }
        return registered;
    }

    /**
     * Saves a server configuration in a provided user shared preferences.
     *
     * @param pref
     *            User shared preferences.
     * @param configuration
     *            The server configuration to register.
     * @return {@code true} if the server configuration has successfully being added.
     */
    static final boolean saveCurrentServer(final ServerConfiguration configuration) {

        final SharedPreferences pref = getPref();
        final String jsonArrayString = pref.getString(ALL_SERVER_CONFIGURATIONS, null);
        JSONArray jsonArray = null;
        try {
            if (jsonArrayString != null) {
                jsonArray = new JSONArray(jsonArrayString);
            } else {
                jsonArray = new JSONArray();
            }
            jsonArray.put(configuration.toJSON());

            final Editor edit = pref.edit();
            edit.putString(ALL_SERVER_CONFIGURATIONS, jsonArray.toString());
            edit.commit();
            return true;
        } catch (final JSONException e) {
            Log.e("JSON server configuration", e.toString());
        } catch (final Exception e) {
            Log.e("Registering server configuration", e.toString());
        }
        return false;
    }

    /**
     * Saves the active server configuration in shared preferences.
     *
     * @param configuration
     *            The configuration to save.
     * @return Returns {@code true} if the server configuration is saved.
     */
    static final boolean saveActiveServer(final ServerConfiguration configuration) {
        try {
            final Editor edit = getPref().edit();
            edit.putString(SELECTED_SERVER_CONFIGURATION, configuration.toJSON());
            edit.commit();

            return true;
        } catch (final Exception ex) {
            Log.e("Registering server configuration error", ex.toString());
        }
        return false;
    }

    /**
     * Loads the active server configuration from shared preferences.
     *
     * @return The active server configuration.
     */
    static final ServerConfiguration loadActiveServer() {
        ServerConfiguration serverconfiguration = null;
        final String active = getPref().getString(SELECTED_SERVER_CONFIGURATION, null);
        if (active != null) {
            serverconfiguration = new ServerConfiguration();
            try {
                serverconfiguration = ServerConfiguration.fromJSON(new JSONObject(active));

            } catch (final JSONException e) {
                Log.e("Loading selected server configuration fails", e.toString());
            }
        }
        return serverconfiguration;
    }

    /**
     * Reads a string representing JSonObjects and returns a list of JSONObjects.
     *
     * @param json
     *            The JSON string representation.
     * @return A list of JSON objects.
     */
    static final List<JSONObject> read(final String json) {
        if (json != null) {
            try {
                return read(new JSONObject(json));
            } catch (final JSONException e) {
                Log.e("Error reading json string", e.toString());
            }
        }
        return null;
    }

    /**
     * Reads a JSON object and parse it into a list of JSON Objects.
     *
     * @param json
     *            The main SON object to parse.
     * @return A list of JSON objects.
     */
    private static final List<JSONObject> read(final JSONObject json) {
        if (json != null) {
            final List<JSONObject> results = new LinkedList<JSONObject>();

            try {
                if (json.getString(RESULT) != null) {
                    final JSONArray msg = (JSONArray) json.get(RESULT);
                    for (int i = 0; i < msg.length(); i++) {
                        results.add(msg.getJSONObject(i));
                    }
                } else {
                    results.add(json);
                }

            } catch (final JSONException e) {
                Log.e("Error reading json object", e.toString());
            }
            return results;
        }
        return null;
    }

    /**
     * Copies an input stream to an output one.
     *
     * @param is
     *            The input stream to copy.
     * @param os
     *            The output stream.
     * @throws IOException
     *             If an error occurs during the copy process.
     */
    static final void copyStream(final InputStream is, final OutputStream os) throws IOException {
        try {
            final byte[] buffer = new byte[16 * 1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (final Exception ex) {
            Log.e("Unable to copy the stream", ex.toString());
        } finally {
            is.close();
            os.close();
        }
    }

    /**
     * URL encodes the selected string.
     *
     * @param stringToEncode
     *            The string to encode.
     * @return A URL encoded string or {@code empty} if an error occurs.
     */
    static final String getURLEncoded(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode.replace("\"", ""), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            Log.e("An exception occured during encoding string", e.toString());
        }
        return "";
    }

    /**
     * Closes a selected connection.
     *
     * @param c
     *            The connection to close.
     */
    static final void closeConnection(final URLConnection c) {
        try {
            // Close the connection.
            if (c instanceof HttpURLConnection) {
                ((HttpURLConnection) c).disconnect();
            } else {
                ((HttpsURLConnection) c).disconnect();
            }
        } catch (final Exception ex) {
            Log.w("Closing connection", "Unable to close the connection");
        }
    }

    /**
     * Checks if an edit text is not empty. (Must contains a value).
     *
     * @param editText
     *            The UI element to check.
     * @return {@code true} if the text is not empty.
     */
    static final boolean checkEditValue(final EditText editText) {
        if (isEmpty(editText.getText())) {
            Toast.makeText(getContext(), String.format("Invalid input : '%s' ", editText.getHint()),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
