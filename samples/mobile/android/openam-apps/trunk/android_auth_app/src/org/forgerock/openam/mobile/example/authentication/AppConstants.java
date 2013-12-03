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

package org.forgerock.openam.mobile.example.authentication;

/**
 * Authentication-application specific Strings
 */
public class AppConstants {

    //constants referenced throughout the application
    public static final String FORGEROCK_URL = "http://www.forgerock.com";
    public static final String APPLICATION_PREFS = "openam_auth_demo_prefs";
    public static final String OPENAM_SERVER_CONFIGURATION = "openam_server_config_prefs";
    public static final String DEFAULT_COOKIE_NAME = "iPlanetDirectoryPro";

    //used to pass information about the current authentication stage between Activities via Intents
    public static final String AUTH_CALLBACK = "auth_callback";

}
