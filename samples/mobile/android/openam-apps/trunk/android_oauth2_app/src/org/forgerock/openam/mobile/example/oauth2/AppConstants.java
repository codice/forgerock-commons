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

public class AppConstants {

    //where our prefs are stored
    public static final String APPLICATION_PREFS = "openam_oauth2_demo_prefs";

    //where the oauth server configuration should save itself in our prefs
    public static final String OAUTH_SERVER_CONFIGURATION = "oauth2_server_config_prefs";

    //default cookie name, supplied by the application
    public static final String DEFAULT_COOKIE_NAME = "iPlanetDirectoryPro";

    //for the About page
    public static final String FORGEROCK_URL = "http://www.forgerock.com";
    public static final String OAUTH2_URL = "http://oauth.net/2/";

}
