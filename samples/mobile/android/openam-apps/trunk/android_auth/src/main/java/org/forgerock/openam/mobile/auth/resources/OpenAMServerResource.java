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

package org.forgerock.openam.mobile.auth.resources;

import org.forgerock.openam.mobile.auth.AuthNConstants;
import org.forgerock.openam.mobile.commons.Resource;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to store the configuration information necessary to use the
 * {@link org.forgerock.openam.mobile.auth.AuthenticationClient} to connect
 * to an OpenAM server.
 *
 * Also provides methods for getting the full endpoint URLs using this information,
 * and implements the {@link Resource} interface for loading and storing of contents.
 */
public class OpenAMServerResource implements Resource<OpenAMServerResource> {

    private String openAmBase;
    private String realm;
    private String cookieDomain;
    private String cookieName;

    public String getRealm() {  return realm; }
    public String getOpenAmBase() { return openAmBase; }
    public String getCookieDomain() { return cookieDomain; }
    public String getCookieName() { return cookieName; }

    public void setRealm(String realm) { this.realm = realm; }
    public void setOpenAmBase(String openamBase) { this.openAmBase = openamBase; }
    public void setCookieDomain(String cookieDomain) { this.cookieDomain = cookieDomain; }
    public void setCookieName(String cookieName) { this.cookieName = cookieName; }

    /**
     * Converts this object into a JSON representation of itself
     * to be stored, ready for loading at a later date.
     *
     * @return a {@link JSONObject} representing this instance
     * @throws JSONException if the json object was not able to be formed
     */
    public String toJSON() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(AuthNConstants.REALM, getRealm());
        jsonObject.put(AuthNConstants.OPENAM_BASE, getOpenAmBase());
        jsonObject.put(AuthNConstants.COOKIE_DOMAIN, getCookieDomain());
        jsonObject.put(AuthNConstants.COOKIE_NAME, getCookieName());

        return jsonObject.toString();
    }

    /**
     * To load this resource from a JSON representation of it
     *
     * @param json The JSON representation
     * @return the OpenAM server resource with completed information
     * @throws JSONException if the json object is not formed as expected
     */
    public OpenAMServerResource fromJSON(JSONObject json) throws JSONException {

        setOpenAmBase(json.getString(AuthNConstants.OPENAM_BASE));
        setRealm(json.getString(AuthNConstants.REALM));
        setCookieDomain(json.getString(AuthNConstants.COOKIE_DOMAIN));
        setCookieName(json.getString(AuthNConstants.COOKIE_NAME));

        return this;
    }

    /**
     * Returns the authentication url endpoint for this server.
     *
     * @param includeRealm Whether or not to include the realm parameter
     * @return the authentication url endpoint.
     */
    public String getAuthenticateUrl(boolean includeRealm) {
        final StringBuilder sb = new StringBuilder(getOpenAmBase());
        sb.append(AuthNConstants.AUTHENTICATE_ENDPOINT);

        if (includeRealm && getRealm() != null && !getRealm().equals("")) {
            sb.append(AuthNConstants.REALM_PARAM);
            sb.append(getRealm());
        }

        return sb.toString();
    }

    /**
     * Returns the validation url endpoint for this server.
     *
     * @return the validation url endpoint.
     */
    public String getValidateUrl() {
        final StringBuilder sb = new StringBuilder(getOpenAmBase());
        sb.append(AuthNConstants.VALIDATE_ENDPOINT);

        return sb.toString();
    }

    /**
     * Returns the logout url endpoint for this server.
     *
     * @return the logout url endpoint.
     */
    public String getLogoutUrl() {
        final StringBuilder sb = new StringBuilder(getOpenAmBase());
        sb.append(AuthNConstants.LOGOUT_ENDPOINT);

        return sb.toString();
    }

    /**
     * Returns the web login url for this server.
     *
     * @return The web login url, including realm if set.
     */
    public String getWebLoginUrl() {
        final StringBuilder sb = new StringBuilder(getOpenAmBase());
        sb.append(AuthNConstants.WEB_LOGIN);

        if (getRealm() != null && !getRealm().equals("")) {
            sb.append(AuthNConstants.REALM_PARAM);
            sb.append(getRealm());
        }

        return sb.toString();
    }

    /**
     * Returns the cookie domain endpoint url for this server.
     *
     * @return the cookie domain url endpoint
     */
    public String getCookieDomainUrl() {
        final StringBuilder sb = new StringBuilder(getOpenAmBase());
        sb.append(AuthNConstants.DOMAIN_OPTIONS_ENDPOINT);

        return sb.toString();
    }

    /**
     * Returns the cookie name endpoint url for this server.
     *
     * @return the cookie name url endpoint
     */
    public String getCookieNameUrl() {
        final StringBuilder sb = new StringBuilder(getOpenAmBase());
        sb.append(AuthNConstants.COOKIE_NAME_ENDPOINT);

        return sb.toString();
    }

}
