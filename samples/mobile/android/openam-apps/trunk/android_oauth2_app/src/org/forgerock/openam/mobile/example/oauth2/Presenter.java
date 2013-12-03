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
import android.content.Intent;
import android.util.Log;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.auth.AuthenticationClient;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.forgerock.openam.mobile.commons.Relay;
import org.forgerock.openam.mobile.commons.RestConstants;
import org.forgerock.openam.mobile.commons.UnwrappedResponse;
import org.forgerock.openam.mobile.example.content.TokenDataSource;
import org.forgerock.openam.mobile.example.oauth2.activities.HomeActivity;
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
     *    todo: this will get cleaner as the AuthorizationClient gets smarter
     *
     * @param action The action of whose response we are handling
     * @param response The response returned to us from the client
     */
    @Override
    public void onEvent(ActionType action, UnwrappedResponse response) {

        String responseValue = null;

        if (response != null) {

             responseValue = response.getEntityContent();

            if (response.getStatusCode() != RestConstants.HTTP_SUCCESS) {
                action = response.getFailActionType();
            }

            if (action == OAuthAction.GET_PROFILE) {
                getAuthZClient().setProfile(responseValue);
            }

            if (action == AuthNAction.VALIDATE) {

                if (responseValue.contains("boolean=false")) {
                    action = AuthNAction.VALIDATE_FAIL;
                } else {

                    final String ssoToken = getDataSource().getSSOToken(context);
                    final String accessToken = getDataSource().getAccessToken(context);

                    if (accessToken != null && ssoToken != null) {
                        String base = getAuthNClient().getOpenAmServerResource().getOpenAmBase();
                        String tokenName = getAuthNClient().getCookieNameWithDefault(AppConstants.DEFAULT_COOKIE_NAME);

                        getAuthZClient().isAccessTokenValid(base, accessToken, tokenName, ssoToken);
                    }

                }
            } else if (action == OAuthAction.GET_CODE) {

                if (validateJsonResponse(responseValue, response)) {

                    final String base = getAuthNClient().getOpenAmServerResource().getOpenAmBase();
                    final String token = getDataSource().getSSOToken(context);
                    final String tokenName = getAuthNClient().getCookieNameWithDefault(AppConstants.DEFAULT_COOKIE_NAME);

                    getAuthZClient().convertCodeToAccessToken(responseValue, base, token, tokenName);
                }

            } else if (action == OAuthAction.GET_TOKEN) {

                if (validateJsonResponse(responseValue, response)) {

                    JSONObject data = null;

                    try {
                        data = new JSONObject(responseValue);
                    } catch (JSONException e) {
                       fail("Unable to unmarshall the token response", response);
                    }

                    if (data != null && data.has(AuthZConstants.ACCESS_TOKEN)) {

                        String tokenId = null;

                        try {
                            tokenId = data.getString(AuthZConstants.ACCESS_TOKEN);
                        } catch (JSONException e) {
                            fail("Unable to marshall the access token response", response);
                        }

                        String serverSetup = null;

                        try {
                            serverSetup = getAuthZClient().getOAuth2ServerResource().toJSON();
                        } catch (JSONException e) {
                            fail("Unable to read the OAuth2.0 server configuration", response);
                        }

                        if (getDataSource().storeAccessToken(tokenId, context, serverSetup)) {

                            Intent intent = new Intent();
                            intent.setClass(context, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            return;

                        } else {
                            fail("Errored attempting to store the access token.", response);
                        }
                    }
                }

            }

        }

        notify(action, responseValue);

    }

    /**
     * Performs some validation of the response to ensure that no error has been returned.
     * todo: move this functionality into the client
     *
     * @param jsonResponse string representation of the server's response
     * @param response
     * @return
     */
    private boolean validateJsonResponse(String jsonResponse, UnwrappedResponse response) {

        boolean result = true;

        if (jsonResponse.contains("error_description")) {
            try {
                JSONObject errorResponse = new JSONObject(jsonResponse);
                fail(errorResponse.getString("error_description"), response);
                result = false;
            } catch (JSONException e) {
                result = false;
                fail("Getting code failed.", response);
            }
        }

        return result;
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
