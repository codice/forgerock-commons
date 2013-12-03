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

package org.forgerock.openam.mobile.example.content;

import android.net.Uri;

public class SampleContract {

    //database
    public static final int VERSION = 1;
    public static final String DB_NAME = "forgerock";

    //db specifics
    public static final String SSO_TABLE_NAME = "ssotoken";
    public static final String OAUTH_TABLE_NAME = "oauthtoken";

    public static final String ID_COLUMN = "_id";
    public static final String TOKEN_COLUMN = "token";
    public static final String CONFIG_COLUMN = "config";

    //result type
    public static final String CONTENT_TYPE = "text/plain";

    //provider/authority
    public static final String SSO_AUTH = "org.forgerock.openam.mobile.example.content.SSOContentProvider";
    public static final String OAUTH_AUTH = "org.forgerock.openam.mobile.example.content.OAuthContentProvider";

    //reference
    public static final Uri SSO_URI = Uri.parse("content://" + SSO_AUTH + "/" + SSO_TABLE_NAME);
    public static final Uri TOKEN_URI = Uri.parse("content://" + OAUTH_AUTH + "/" + OAUTH_TABLE_NAME);

}
