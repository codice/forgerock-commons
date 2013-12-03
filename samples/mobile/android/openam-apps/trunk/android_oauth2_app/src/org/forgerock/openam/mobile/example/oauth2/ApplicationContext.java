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

package org.forgerock.openam.mobile.example.oauth2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.forgerock.openam.mobile.auth.AuthenticationClient;
import org.forgerock.openam.mobile.auth.resources.OpenAMServerResource;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.forgerock.openam.mobile.oauth.AuthorizationClient;
import org.forgerock.openam.mobile.oauth.resources.OAuth2ServerResource;
import org.json.JSONException;

/**
 * The ApplicationContext gets configured as soon as we enter the HomeActivity
 * (our launch location) and acts as a place for each of our Actvities to
 * be able to get a handle onto the presenter. The presenter contains the
 * clients and tells the client to perform all asynchronous requests.
 *
 * The responses are brought back to the clients, which forwards them to their
 * respective presenter. The presenter is the responsible for determining
 * more information about the response, before finally propagating the message
 * down to the Activities to act upon within the application's context if necessary.
 *
 * By this standard we are using a Model-View-Presenter model.
 */
public class ApplicationContext {

    private static final String TAG = "ApplicationContext";

    private static ApplicationContext applicationContext;

    private SharedPreferences pref;
    private Context context;
    private Presenter presenter;

    public ApplicationContext() { }

    public SharedPreferences getPref() {
        return pref;
    }

    private void setSharedPreferences(SharedPreferences prefs) {
        this.pref = prefs;
    }

    private void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public Presenter getPresenter() {
        return presenter;
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Attempts to load an openAM server object from the supplied Content Provider.
     *
     * If not configured, will not load.
     *
     * If it is configured, it will attempt to load the OAuth server configuration
     * from the local preferences.
     *
     */
    public void reloadServerConfig() {

        OpenAMServerResource openam = new OpenAMServerResource();
        OAuth2ServerResource oauth2 = new OAuth2ServerResource();

        final String openamConfigStr = getPresenter().getDataSource().getOpenamConfig(getContext());
        final String oauthConfigStr = getPref().getString(AppConstants.OAUTH_SERVER_CONFIGURATION, null);

        if (openamConfigStr != null) {
            AndroidUtils.loadConfig(openam, openamConfigStr);
            getPresenter().getAuthNClient().setOpenAMServerResource(openam);
        }

        if (oauthConfigStr != null) {
            AndroidUtils.loadConfig(oauth2, oauthConfigStr);
            getPresenter().getAuthZClient().setOAuth2ServerResource(oauth2);
        }

    }

    /**
     * Stores a copy of this application's passed in configuration and
     * writes it to the local preferences.
     *
     * @param oauthConfig the config to store
     * @return true if successfully stored, false if something went wrong
     */
    public boolean saveServerConfiguration(OAuth2ServerResource oauthConfig) {

        try {
            final SharedPreferences.Editor edit = getPref().edit();
            edit.putString(AppConstants.OAUTH_SERVER_CONFIGURATION, oauthConfig.toJSON());
            edit.commit();

            getPresenter().getAuthZClient().setOAuth2ServerResource(oauthConfig);

            return true;
        } catch (JSONException e) {
            Log.e(TAG, "OAuth2.0 config save failed.");
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
        appPresenter.setAuthZClient(new AuthorizationClient());

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
