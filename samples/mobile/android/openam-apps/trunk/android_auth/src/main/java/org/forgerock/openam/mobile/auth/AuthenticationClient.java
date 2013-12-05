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

package org.forgerock.openam.mobile.auth;

import java.util.HashMap;
import java.util.Hashtable;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.forgerock.openam.mobile.auth.resources.OpenAMServerResource;
import org.forgerock.openam.mobile.commons.ASyncRestRequest;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.IRestRequestBuilder;
import org.forgerock.openam.mobile.commons.Relay;
import org.forgerock.openam.mobile.commons.RestActions;
import org.forgerock.openam.mobile.commons.RestConstants;
import org.forgerock.openam.mobile.commons.RestRequestBuilder;
import org.forgerock.openam.mobile.commons.UnwrappedResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RestClient for talking with the OpenAM Authentication server.
 *
 * Extends Relay<UnwrapedResponse, UnwrappedResponse> so that this client can
 * propagate messages returned to one or more more knowledgable objects
 * (controllers / presenters, etc.)
 *
 * Supports basic querying of an openAM authentication service through
 * asynchronous calls.
 *
 * todo: make the client smarter, with knowledge of the expected return-type values
 * setting the failure action if we know that a request has failed - eg move some
 * functionality from the current Auth App Presenter to here to make the client
 * more helpful
 *
 */
public class AuthenticationClient extends Relay<UnwrappedResponse, UnwrappedResponse> {

    private OpenAMServerResource server;

    public void setOpenAMServerResource(OpenAMServerResource server) {
        this.server = server;
    }

    public OpenAMServerResource getOpenAmServerResource() {
        return server;
    }

    /**
     * A method for getting the cookie domain from the server, or returning the
     * string supplied if there's no cookie domain set on the server's config
     *
     * @param defaultStr The string to use if no domain saved
     * @return the domain of the cookie to send to OpenAM
     */
    public String getCookieDomainWithDefault(String defaultStr) {
        checkServer();

        return server.getCookieDomain() == null || server.getCookieDomain().equals("") ?
                defaultStr : server.getCookieDomain();
    }

    /**
     * Simplified accessor to getCookieDomainWithDefault
     */
    public String getCookieDomain() {
        checkServer();

        return getCookieNameWithDefault(server.getOpenAmBase());
    }

    /**
     * A method for getting the cookie name from the server, or returning the
     * string supplied if there's no cookie name set on the server's config
     *
     * @param defaultStr The string to use if no name saved
     * @return the name of the cookie to send to openAM as your SSO token
     */
    public String getCookieNameWithDefault(String defaultStr) {
        checkServer();

        return server.getCookieName() == null || server.getCookieName().equals("") ?
                defaultStr : server.getCookieName();
    }

    /**
     * Simplified accessor to getCookieNameWithDefault
     */
    public String getCookieName() {
        checkServer();

        return getCookieNameWithDefault(RestConstants.IPLANETDIRECTORYPRO);
    }

    /**
     * Fires a request off to the openAM server asking for the list of
     * appropriate domains for cookies for this server
     *
     * @param baseUrl Takes the baseURL of the openAM instance to query, as this
     *                will likely be used prior to setup of the server object itself
     */

    public void cookieDomain(String baseUrl) {
        final StringBuilder url = new StringBuilder(baseUrl);
        url.append(AuthNConstants.DOMAIN_OPTIONS_ENDPOINT);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(AuthNAction.GET_COOKIE_DOMAINS)
                .setFailureAction(AuthNAction.GET_COOKIE_DOMAINS_FAIL)
                .setUrl(url.toString());

        final ASyncRestRequest<HttpGet> request = rrb.createGetRestRequest();

        request.execute();
    }

    /**
     * For querying the server for its cookie domain using the pre-configured
     * URL as its base.
     */
    public void cookieDomain() {
        checkServer();

        cookieName(server.getOpenAmBase());
    }

    /**
     * Fires a request off to the openAM server asking for the name of the cookie
     * which it must return to openAM
     *
     * @param baseUrl Takes the baseURL of the openAM instance to query, as this
     *                will likely be used prior to setup of the server object itself
     */
    public void cookieName(String baseUrl) {
        final StringBuilder url = new StringBuilder(baseUrl);
        url.append(AuthNConstants.COOKIE_NAME_ENDPOINT);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(AuthNAction.GET_COOKIE_NAME)
                .setFailureAction(AuthNAction.GET_COOKIE_NAME_FAIL)
                .setUrl(url.toString());

        final ASyncRestRequest<HttpGet> request = rrb.createGetRestRequest();

        request.execute();
    }

    /**
     * For querying the server for its cookie name
     */
    public void cookieName() {
        checkServer();

        cookieName(server.getOpenAmBase());
    }

    /**
     * Used to continue along an authentication process.
     *
     * @param data The JSON data to return to the server as a payload
     */
    public void authenticate(JSONObject data) {
        final String url = server.getAuthenticateUrl(true);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(AuthNAction.AUTH)
                .setFailureAction(AuthNAction.AUTH_FAIL)
                .setUrl(url)
                .setJson(data);

        final ASyncRestRequest<HttpPost> request = rrb.createPostJSONRestRequest();

        request.execute();
    }

    /**
     * Used to begin the authentication process.
     */
    public void authenticate() {
        authenticate(null);
    }

    /**
     * Log out an SSO token from OpenAM.
     *
     * @param ssoToken The SSO Token to log out
     */
    public void logout(String ssoToken) {

        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(server.getCookieName(), ssoToken);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(AuthNAction.LOGOUT)
                .setFailureAction(AuthNAction.LOGOUT_FAIL)
                .setUrl(server.getLogoutUrl())
                .setHeaders(headers);

        final ASyncRestRequest<HttpPost> request = rrb.createPostJSONRestRequest();

        request.execute();
    }

    /**
     * Check if a token is valid or not.
     *
     * Response ActionType will be of
     * {@link AuthNAction#VALIDATE} or {@link AuthNAction#VALIDATE_FAIL}.
     *
     * The returned object will be an {@link UnwrappedResponse}, with status code
     * 200 and its string contents the server's response entity.
     *
     * @param token The SSO token to check
     */
    public void isTokenValid(String token) {

        final HashMap<String, String> data = new HashMap<String, String>();
        data.put(AuthNConstants.TOKENID, token);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(AuthNAction.VALIDATE)
                .setFailureAction(AuthNAction.VALIDATE_FAIL)
                .setUrl(server.getValidateUrl())
                .setFormParams(data);

        final ASyncRestRequest<HttpPost> request = rrb.createPostFormRestRequest();

        request.execute();
    }

    /**
     * Checks if our server has been set with a value. Errors if not.
     */
    private void checkServer() {
        if (server == null) {
            throw new NullPointerException("OpenAM server must be configured; currently null.");
        }
    }

    /**
     * Interrogates a JSONObject representing an authentication module stage, and puts the appropriate
     * data into the appropriate location. As per the rest of this file, only works for NameCallback
     * and PasswordCallback types.
     *
     * @param authObj The JSON object representing the current stage of authentication
     * @param values The id:value pairs to insert into the JSON
     *
     * @throws org.json.JSONException If we cannot alter the JSON correctly
     */
    public void replaceAuthenticateInputVals(JSONObject authObj, Hashtable<String, String> values) throws JSONException {

        JSONArray callbacks = authObj.getJSONArray(AuthNConstants.CALLBACKS);

        for (int i = 0; i < callbacks.length(); i++) {
            JSONObject callback = callbacks.getJSONObject(i);
            JSONArray inputs = callback.getJSONArray(AuthNConstants.INPUT);

            for (int k = 0; k < inputs.length(); k++) {
                JSONObject input = inputs.getJSONObject(k);
                if (values.containsKey(input.getString(AuthNConstants.NAME))) {
                    input.put(AuthNConstants.VALUE, values.get(input.getString(AuthNConstants.NAME)));
                }
            }
        }
    }

    /**
     * {@link Relay} contract. Notifies its listeners.
     *
     * @param action The action just performed
     * @param response The response to the action just performed
     */
    public void onEvent(ActionType action, UnwrappedResponse response) {
        action = detectFailures(action, response);
        notify(action, response);
    }


    /**
     * Detects whether known failures have been hit, or otherwise there have been
     * issues with gathering the response.
     *
     * The response is not altered in any way here, just interrogated.
     *
     * @param action the action we are returning from
     * @param response the response associated with the actiowe just performed
     * @return the action to continue with
     */
    private ActionType detectFailures(ActionType action, UnwrappedResponse response) {

        if (action == null) {
            throw new NullPointerException("Responses to the client must have an action type associated.");
        }

        if (response == null || response.getStatusCode() != RestConstants.HTTP_SUCCESS ||
                response.getEntityContent() == null) {
            return RestActions.TRANSPORT_FAIL;
        }

        //check that we don't have the failure string returned
        if (action == AuthNAction.VALIDATE) {
            if (response.getEntityContent().contains(AuthNConstants.VALIDATE_FAIL_STR)) {
                return AuthNAction.VALIDATE_FAIL;
            }
        }

        // check there's JSON information, and it has a 'domains' key
        if (action == AuthNAction.GET_COOKIE_DOMAINS) {
            try {
                JSONObject data = new JSONObject(response.getEntityContent());

                if (!data.has(AuthNConstants.DOMAINS)) {
                    return AuthNAction.GET_COOKIE_DOMAINS_FAIL;
                }
            } catch (JSONException e) {
                return AuthNAction.GET_COOKIE_DOMAINS_FAIL;
            }
        }

        //check there's a response string, and it starts with 'string='
        if (action == AuthNAction.GET_COOKIE_NAME) {
            if (!response.getEntityContent().startsWith(AuthNConstants.COOKIE_NAME_STR)) {
                action = AuthNAction.GET_COOKIE_NAME_FAIL;
            }
        }

        //check we have the necessary string indicating logout
        if (action == AuthNAction.LOGOUT) {
            if (!response.getEntityContent().contains(AuthNConstants.LOGOUT_STR)) {
                action = AuthNAction.LOGOUT_FAIL;
            }
        }

        // validates that the returned auth action has a json body content
        // and a token ID, if it doesn't ahve the former, error, if it
        // doesn't have the latter, set to continue
        if (action == AuthNAction.AUTH) {

            try {
                JSONObject data = new JSONObject(response.getEntityContent());

                if (!data.has(AuthNConstants.TOKEN_ID)) {
                    return AuthNAction.AUTH_CONT;
                }
            } catch (JSONException e) {
                return AuthNAction.AUTH_FAIL;
            }

        }

        return action;
    }
}
