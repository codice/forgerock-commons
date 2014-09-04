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

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.*;
import static java.lang.String.valueOf;
import static org.forgerock.contactmanager.Constants.*;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class defines the context of the current application.
 */
public class AppContext {

    /**
     * The shared preferences used in this application.
     */
    private static SharedPreferences pref;

    /**
     * The application context.
     */
    private static Context context;

    /**
     * The cache directory.
     */
    private static File cacheDir;

    /**
     * The selected server which is going to be used to perform request.
     */
    private static ServerConfiguration serverConfiguration;

    /**
     * The constructor.
     *
     * @param ctx
     *            The context to set.
     */
    AppContext(final Context ctx) {
        context = ctx;
        pref = context.getSharedPreferences(PREF_NAME_APPLICATION, MODE_PRIVATE);
        serverConfiguration = Utils.loadActiveServer();
        if (MEDIA_MOUNTED.equals(getExternalStorageState())) {
            cacheDir = new File(getExternalStorageDirectory(), PREF_STORAGE_APPLICATION);
        } else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    /**
     * Returns a file from the selected URL.
     */
    static File get(final String url) {
        return new File(cacheDir, valueOf(url.hashCode()));
    }

    /**
     * Clears the cache.
     */
    static void clearCache() {
        final File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (final File f : files) {
            f.delete();
        }
    }

    /**
     * Returns the shared preferences for this application.
     *
     * @return The shared preferences for this application.
     */
    public static SharedPreferences getPref() {
        if (pref == null) {
            pref = context.getSharedPreferences(PREF_NAME_APPLICATION, MODE_PRIVATE);
        }
        return pref;
    }

    /**
     * Sets the shared preferences for this application.
     *
     * @param preferences
     *            The shared preferences for this application.
     */
    public void setPref(final SharedPreferences preferences) {
        pref = preferences;
    }

    /**
     * Returns the server configuration which is used in this application.
     *
     * @return The selected server which is used in this application.
     */
    public static ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    /**
     * Sets the server configuration which is used in this application.
     *
     * @param srvConfiguration
     *            The server configuration which is used in this application.
     */
    public static void setServerConfiguration(final ServerConfiguration srvConfiguration) {
        serverConfiguration = srvConfiguration;
    }

    /**
     * Returns the context of this application.
     *
     * @return The context of this application.
     */
    public static Context getContext() {
        return context;
    }
}
