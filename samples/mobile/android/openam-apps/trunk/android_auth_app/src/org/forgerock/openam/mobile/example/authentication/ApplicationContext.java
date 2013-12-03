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

package org.forgerock.openam.mobile.example.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.forgerock.openam.mobile.auth.AuthenticationClient;
import org.forgerock.openam.mobile.auth.resources.OpenAMServerResource;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.json.JSONException;

/**
 * The ApplicationContext gets configured as soon as we enter the HomeActivity
 * (our launch location) and acts as a place for each of our Actvities to
 * be able to get a handle onto the presenter. The presenter contains the
 * client and tells the client to perform all asynchronous requests.
 *
 * The responses are brought back to the client, which forwards them to their
 * respective presenter. The presenter is the responsible for determining
 * more information about the response, before finally propagating the message
 * down to the Activities to act upon within the application's context if necessary.
 *
 * By this standard we are using a Model-View-Presenter model.
 */
public final class ApplicationContext {

    private static ApplicationContext applicationContext;

    private SharedPreferences pref;
    private Presenter presenter;
    private Context context;

    private ApplicationContext() { }

    private SharedPreferences getPref() {
        return pref;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public Presenter getPresenter() {
        return presenter;
    }

    private void setSharedPreferences(SharedPreferences sharedPrefs) {
        this.pref = sharedPrefs;
    }

    /**
     * Attempts to load an openAM server object from a JSON string, stored
     * within our application preferences
     */
    public void reloadServerConfig() {

        OpenAMServerResource openam = new OpenAMServerResource();

        final String openamConfigStr = getPref().getString(AppConstants.OPENAM_SERVER_CONFIGURATION, null);

        if (openamConfigStr != null) {
            AndroidUtils.loadConfig(openam, openamConfigStr);
            getPresenter().getAuthNClient().setOpenAMServerResource(openam);
        }

    }

    /**
     * Saves our current server configuration out to our local preferences store
     *
     * @param openamConfig JSON representation of a server
     * @return boolean success or fail
     */
    public boolean saveServerConfiguration(OpenAMServerResource openamConfig) {

        try {
            final SharedPreferences.Editor edit = getPref().edit();
            edit.putString(AppConstants.OPENAM_SERVER_CONFIGURATION, openamConfig.toJSON());
            edit.commit();

            getPresenter().getAuthNClient().setOpenAMServerResource(openamConfig);

            return true;
        } catch (JSONException e) {
            Log.e("JSON server configuration", e.toString());
        } catch (Exception e) {
            Log.e("Registering server configuration", e.toString());
        }
        return false;
    }

    /**
     * Retrieves the singleton instance of ApplicationContext, for use throughout the
     * application.
     *
     * @param context The initial Android application context
     * @return the only instance of this class
     */
    public static ApplicationContext getInstance(Context context) {

        if (context == null) {
            throw new NullPointerException("Context cannot be null on creation of ApplicationContext!");
        }

        if (applicationContext == null) {
            applicationContext = new ApplicationContext();
        } else {
            return applicationContext;
        }

        applicationContext.setContext(context);
        applicationContext.setSharedPreferences(context.getSharedPreferences(AppConstants.APPLICATION_PREFS, Context.MODE_PRIVATE));

        Presenter appPresenter = new Presenter();
        appPresenter.setAuthNClient(new AuthenticationClient());

        applicationContext.setPresenter(appPresenter);
        applicationContext.reloadServerConfig();

        return applicationContext;
    }

    /**
     * Retrieves the singleton instance of this instance
     *
     * @return the only instance of this class
     */
    public static ApplicationContext getInstance() {
        if (applicationContext == null) {
            throw new IllegalStateException("Must init ApplicationContext via getInstance(Context context)");
        }

        return applicationContext;
    }
}
