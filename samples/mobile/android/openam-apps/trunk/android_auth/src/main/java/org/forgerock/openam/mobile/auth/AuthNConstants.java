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

/**
 * Constants used throughout the Authentication system.
 */
public class AuthNConstants {

    //endpoints
    public static final String AUTHENTICATE_ENDPOINT = "/json/authenticate";
    public static final String LOGOUT_ENDPOINT = "/json/sessions/?_action=logout";
    public static final String DOMAIN_OPTIONS_ENDPOINT = "/json/serverinfo/cookieDomains";
    public static final String VALIDATE_ENDPOINT = "/identity/isTokenValid";
    public static final String COOKIE_NAME_ENDPOINT = "/identity/getCookieNameForToken";

    //query string
    public static final String REALM_PARAM = "?realm=";
    public static final String WEB_LOGIN = "/XUI/#login/";

    //two versions of tokenId....
    public final static String TOKEN_ID = "tokenId";
    public final static String TOKENID = "tokenid";

    //some key references for maps
    public final static String OPENAM_BASE = "openamBase";
    public final static String REALM = "realm";
    public final static String COOKIE_DOMAIN = "cookie_realm";
    public final static String COOKIE_NAME = "cookie_name";

}
