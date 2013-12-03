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

package org.forgerock.openam.mobile.oauth;

import java.util.HashMap;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.forgerock.openam.mobile.commons.ASyncRestRequest;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.IRestRequestBuilder;
import org.forgerock.openam.mobile.commons.Relay;
import org.forgerock.openam.mobile.commons.RestRequestBuilder;
import org.forgerock.openam.mobile.commons.UnwrappedResponse;
import org.forgerock.openam.mobile.oauth.resources.OAuth2ServerResource;

/**
 * RestClient for talking with the OpenAM OAuth2.0 Authorization server.
 *
 * Extends Relay<UnwrapedResponse, UnwrappedResponse> so that this client can
 * propagate messages returned to one or more more knowledgable objects
 * (controllers / presenters, etc.)
 *
 * Supports basic querying of an openAM authorization service through
 * asynchronous calls.
 *
 * The client's REST-calling functions take the necessary parameters for
 * each REST method to be called easily, bundles any necessary query string parameters,
 * headers together, and creates the object supplying its entity data if
 * appropriate.
 *
 * Responses are returned as-is through the {@link Relay} interface.
 *
 * todo: make the client smarter, with knowledge of the expected return-type values
 * setting the failure action if we know that a request has failed - eg move some
 * functionality from the current OAuth2.0 App Presenter to here to make the client
 * more helpful
 *
 */
public class AuthorizationClient extends Relay<UnwrappedResponse, UnwrappedResponse> {

    private OAuth2ServerResource server;

    private String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setOAuth2ServerResource(OAuth2ServerResource oauth2) {
        this.server = oauth2;
    }

    public OAuth2ServerResource getOAuth2ServerResource() {
        return server;
    }

    /**
     * Converting an authorization grant flow code into an access token
     *
     * @param code The code to turn into an access_code
     * @param base The base url of the openam instance
     * @param ssoToken the sso token giving authentication access to this endpoint
     * @param cookieName the name of the cookie to apply to the sso token
     */
    public void convertCodeToAccessToken(String code, String base, String ssoToken, String cookieName) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AuthZConstants.GRANT_TYPE, AuthZConstants.AUTHORIZATION_CODE);
        params.put(AuthZConstants.CODE, code);
        params.put(AuthZConstants.REDIRECT_URI, server.getRedirectUri());
        params.put(AuthZConstants.CLIENT_ID, server.getClientId());
        params.put(AuthZConstants.CLIENT_SECRET, server.getClientSecret());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(cookieName, ssoToken);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(OAuthAction.GET_TOKEN)
                .setFailureAction(OAuthAction.GET_TOKEN_FAIL)
                .setUrl(server.getAccessTokenUrl(base))
                .setFormParams(params)
                .setHeaders(headers);

        final ASyncRestRequest<HttpPost> request = rrb.createPostFormRestRequest();

        request.execute();
    }

    /**
     * Fires off a request to the TokenInfo endpoint URL, and if it receives a response is aware
     * that the token is valid.
     *
     * @param base The base openam URL
     * @param accessToken the access token to check
     * @param cookieName the name of the cookie in which to send the sso token
     * @param ssoToken the sso token from authentication
     */
    public void isAccessTokenValid(String base, String accessToken, String cookieName, String ssoToken) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(AuthZConstants.ACCESS_TOKEN, accessToken);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(cookieName, ssoToken);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(OAuthAction.VALIDATE)
                .setFailureAction(OAuthAction.VALIDATE_FAIL)
                .setUrl(server.getTokenInfoUrl(base))
                .setQueryParams(params)
                .setHeaders(headers);

        final ASyncRestRequest<HttpGet> request = rrb.createGetRestRequest();
        request.execute();
    }

    /**
     * Retrieves the profile belonging to the user whose access token we have. This will
     * include any specific information requested by the scope
     *
     * @param base OpenAM base url
     * @param accessToken The access token which allows us access
     * @param cookieName the name of the sso token's cookie
     * @param ssoToken the SSO token which authenticates us
     */
    public void getProfile(String base, String accessToken, String cookieName, String ssoToken) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put(AuthZConstants.ACCESS_TOKEN, accessToken);

        final HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(cookieName, ssoToken);

        IRestRequestBuilder rrb = new RestRequestBuilder()
                .setListener(this)
                .setSuccessAction(OAuthAction.GET_PROFILE)
                .setFailureAction(OAuthAction.GET_PROFILE_FAIL)
                .setUrl(server.getTokenInfoUrl(base))
                .setQueryParams(params)
                .setHeaders(headers);

        final ASyncRestRequest<HttpGet> request = rrb.createGetRestRequest();

        request.execute();
    }

    /**
     * {@link Relay} contract. Notifies its listeners.
     *
     * todo: see top
     *
     * @param action The action just performed
     * @param response The response to the action just performed
     */
    @Override
    public void onEvent(ActionType action, UnwrappedResponse response) {
        notify(action, response);
    }
}
