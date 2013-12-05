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

/**
 * Constants used throughout the Authorzation system
 */
public class AuthZConstants {

    //endpoints
    public static final String AUTHORIZE_ENDPOINT = "/oauth2/authorize";
    public static final String ACCESS_TOKEN_ENDPOINT = "/oauth2/access_token";
    public static final String TOKEN_INFO_ENDPOINT = "/oauth2/tokeninfo";

    //query params
    public static final String RESPONSE_TYPE_PARAM = "?response_type=";
    public static final String CLIENT_ID_PARAM = "&client_id=";
    public static final String REDIRECT_URI_PARAM = "&redirect_uri=";
    public static final String SCOPE_PARAM = "&scope=";

    //keys and additionals
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String CODE = "code";
    public static final String GRANT_TYPE = "grant_type";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_ID = "client_id";

    public static final String VALIDATE_FAIL_STR = "boolean=false";
    public static final String ERROR_DESCRIPTION = "error_description";

}
