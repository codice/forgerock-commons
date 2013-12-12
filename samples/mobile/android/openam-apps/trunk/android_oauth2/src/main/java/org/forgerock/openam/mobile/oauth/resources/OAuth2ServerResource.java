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

package org.forgerock.openam.mobile.oauth.resources;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.forgerock.openam.mobile.commons.Resource;
import org.forgerock.openam.mobile.commons.RestConstants;
import org.forgerock.openam.mobile.oauth.AuthZConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to store the configuration information necessary to use the
 * {@link org.forgerock.openam.mobile.oauth.AuthorizationClient} to connect
 * to an OpenAM server.
 *
 * Also provides methods for getting the full endpoint URLs using this information,
 * and implements the {@link Resource} interface for loading and storing of contents.
 */
public class OAuth2ServerResource implements Resource<OAuth2ServerResource> {

    private final static String SCOPE = "scope";
    private final static String CLIENT_ID = "clientId";
    private final static String CLIENT_SECRET = "clientSecret";
    private final static String REDIRECT_URI = "redirectUri";

    private String scope;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public String getScope() {
        return scope;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * Turns this server object into a simple JSON object for
     * storing and reloading, as per the {@link Resource} interface
     *
     * @return JSON string representation of this server resource
     * @throws JSONException
     */
    public String toJSON() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(SCOPE, getScope());
        jsonObject.put(CLIENT_ID, getClientId());
        jsonObject.put(CLIENT_SECRET, getClientSecret());
        jsonObject.put(REDIRECT_URI, getRedirectUri());

        return jsonObject.toString();

    }

    /**
     * Recreates this object from a JSON representation of it.
     *
     * @return {@link OAuth2ServerResource} built from the json sent in
     * @throws JSONException
     */
    public OAuth2ServerResource fromJSON(JSONObject json) {
        try {
            setScope(json.getString(SCOPE));
            setClientId(json.getString(CLIENT_ID));
            setClientSecret(json.getString(CLIENT_SECRET));
            setRedirectUri(json.getString(REDIRECT_URI));

        } catch (JSONException e) {
            Log.w("Server Configuration", e);
        }
        return this;
    }

    public String getAuthorizeUrl(String base) {
        final StringBuilder sb = new StringBuilder(base);
        sb.append(AuthZConstants.AUTHORIZE_ENDPOINT);
        sb.append(AuthZConstants.RESPONSE_TYPE_PARAM).append(AuthZConstants.CODE);
        sb.append(AuthZConstants.CLIENT_ID_PARAM).append(getClientId());
        try {
            sb.append(AuthZConstants.SCOPE_PARAM).append(URLEncoder.encode(getScope(), RestConstants.UTF8));
            sb.append(AuthZConstants.REDIRECT_URI_PARAM).append(URLEncoder.encode(getRedirectUri(), RestConstants.UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String getAccessTokenUrl(String base) {
        final StringBuilder sb = new StringBuilder(base);
        sb.append(AuthZConstants.ACCESS_TOKEN_ENDPOINT);

        return sb.toString();
    }

    public String getTokenInfoUrl(String base) {
        final StringBuilder sb = new StringBuilder(base);
        sb.append(AuthZConstants.TOKEN_INFO_ENDPOINT);

        return sb.toString();
    }

}
