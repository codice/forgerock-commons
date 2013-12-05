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
import android.util.Log;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.auth.AuthenticationClient;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.forgerock.openam.mobile.commons.Relay;
import org.forgerock.openam.mobile.commons.RestActions;
import org.forgerock.openam.mobile.commons.UnwrappedResponse;
import org.forgerock.openam.mobile.example.content.TokenDataSource;
import org.forgerock.openam.mobile.oauth.AuthZConstants;
import org.forgerock.openam.mobile.oauth.AuthorizationClient;
import org.forgerock.openam.mobile.oauth.OAuthAction;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * This class determines if the response is valid (not in network terms,
 * the Rest client marks those) for the action that has been set.
 *
 * It then triggers actions as appropriate -
 *  - firing off further async requests, as per VALIDATE
 *  - detecting failure and altering the action type
 *  - propagating the messages to any further listeners
 *
 * The "further listeners" are likely to be Activities, which
 * can perform actions based on the returned values from within
 * their Context if necessary.
 *
 */
public class Presenter extends Relay<UnwrappedResponse, String> {

    private static final String TAG = "Presenter";

    private AuthenticationClient authNClient;
    private AuthorizationClient authZClient;

    private Context context = ApplicationContext.getInstance().getContext();
    private final TokenDataSource dataSource = new TokenDataSource();

    //get datasource
    public TokenDataSource getDataSource() {
        return dataSource;
    }

    //set clients
    public void setAuthNClient (AuthenticationClient authNClient) {
        this.authNClient = authNClient;
        authNClient.registerListener(this);
    }

    public AuthenticationClient getAuthNClient() {
        return authNClient;
    }

    public void setAuthZClient(AuthorizationClient authZClient) {
        this.authZClient = authZClient;
        authZClient.registerListener(this);
    }

    public AuthorizationClient getAuthZClient() {
        return authZClient;
    }

    //access methods for the view
    public boolean deleteAccessToken() {
        return getDataSource().deleteAccessToken(context);
    }

    public String getSSOToken() {
        return getDataSource().getSSOToken(context);
    }

    public String getAccessToken() {
        return getDataSource().getAccessToken(context);
    }

    /**
     * The main bulk of the Presenter class. Here we are responsible for
     * making sure that the asynchronous requests we've sent off to the server
     * are received.
     *
     * The Presenter's duty here is threefold:
     *
     *  - Determine if there have been errors in the response from the client, and
     *    if so, log this and mark the ActionType as failed, using the appropriate
     *    failure response contained within the passed in UnwrappedResponse
     *
     *  - Alter the response-type such that the classes registered as Listeners
     *    to this Presenter receive the correct type - in this case we alter the response
     *    to be of type {@link String}, via the responseValue variable.
     *
     *  - Propagate the response and (possibly altered) action on to further listeners,
     *    such as Activities.
     *
     * @param action The action of whose response we are handling
     * @param response The response returned to us from the client
     */
    @Override
    public void onEvent(ActionType action, UnwrappedResponse response) {

        if (action == RestActions.TRANSPORT_FAIL) {
            AndroidUtils.showToast("Transport failed. Check internet?", context);
            Log.e(TAG, "Transport failed. Aborting Presenter flow.");
            return;
        }

        if (response == null || action == null) {
            throw new NullPointerException("Response and action to the Presenter cannot be null.");
        }

        //anything that doesn't have an entry in the following (e.g. GET_COOKIE_NAME_FAIL)
        //should be handled by one of the listening classes to notify the user
        String responseValue = response.getEntityContent();

        if (action == OAuthAction.GET_PROFILE) {
            getAuthZClient().setProfile(responseValue);
        } else if (action == AuthNAction.VALIDATE) {
            triggerOAuthRequest();
        } else if (action == OAuthAction.GET_CODE) {
            convertCodeToToken(responseValue);
        } else if (action == OAuthAction.GET_TOKEN) {
            action = doStoreToken(response);
        }

        notify(action, responseValue);

    }

    /**
     * perform the storing of a token
     * @param response
     * @return
     */
    private ActionType doStoreToken(UnwrappedResponse response) {

        try {
            if (!storeToken(response.getEntityContent())) {
                return fail("Errored attempting to store access token", response);
            }
        } catch (JSONException e) {
            return fail("Errored attempting to store the access token.", response);
        }

        return response.getSuccessActionType();

    }

    /**
     * for storing an access token
     *
     * @param responseValue
     * @return
     * @throws JSONException
     */
    private boolean storeToken(String responseValue) throws JSONException {

        String serverSetup = getAuthZClient().getOAuth2ServerResource().toJSON();
        JSONObject data = new JSONObject(responseValue);
        String tokenId = data.getString(AuthZConstants.ACCESS_TOKEN);

        return getDataSource().storeAccessToken(tokenId, context, serverSetup);
    }


    /**
     * Validates the OAuth and returned the appropriate success/fail action
     */
    private void triggerOAuthRequest() {

        final String ssoToken = getDataSource().getSSOToken(context);
        final String accessToken = getDataSource().getAccessToken(context);

        if (accessToken != null && ssoToken != null) {
            String base = getAuthNClient().getOpenAmServerResource().getOpenAmBase();
            String tokenName = getAuthNClient().getCookieNameWithDefault(AppConstants.DEFAULT_COOKIE_NAME);

            getAuthZClient().isAccessTokenValid(base, accessToken, tokenName, ssoToken);
        }
    }

    /**
     * Triggers querying the server for an access token
     * @param code
     */
    private void convertCodeToToken(String code) {

        final String base = getAuthNClient().getOpenAmServerResource().getOpenAmBase();
        final String token = getDataSource().getSSOToken(context);
        final String tokenName = getAuthNClient().getCookieNameWithDefault(AppConstants.DEFAULT_COOKIE_NAME);

        getAuthZClient().convertCodeToAccessToken(code, base, token, tokenName);

    }

    /**
     * Writes out a log message, returns the failure-state {@link ActionType} from
     * the {@link UnwrappedResponse} as the new action to be propagated
     *
     * @param msg A message to log
     * @param response The response from which to read the failure ActionType
     * @return The failure ActionType to set to further-propagated messages
     */
    private ActionType fail(String msg, UnwrappedResponse response) {
        Log.d(TAG, msg);
        AndroidUtils.showToast(msg, ApplicationContext.getInstance().getContext());
        return response.getFailActionType();
    }

}
