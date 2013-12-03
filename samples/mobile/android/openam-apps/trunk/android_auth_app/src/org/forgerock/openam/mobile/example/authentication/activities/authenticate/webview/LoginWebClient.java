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

package org.forgerock.openam.mobile.example.authentication.activities.authenticate.webview;

import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.StringTokenizer;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.auth.AuthenticationClient;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.commons.RestConstants;
import org.forgerock.openam.mobile.commons.UnwrappedResponse;
import org.forgerock.openam.mobile.example.authentication.AppConstants;

/**
 * Login client for the authentication process
 */
public class LoginWebClient extends WebViewClient {

    private final Listener<UnwrappedResponse> activity;
    private final AuthenticationClient authNClient;

    /**
     * Creates the web client, with the appropriate listener.
     * Also checks and clears the cookie manager so we don't
     * inadvertantly use an old session.
     * @param activity
     */
    public LoginWebClient(Listener<UnwrappedResponse> activity, AuthenticationClient authNClient) {
        this.activity = activity;
        this.authNClient = authNClient;

        final CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    /**
     * Implementing this could prevent the user from leaving the domain or page(s)
     * we explicitly want them to stay on in our WebView. Skipped for brevity, so
     * this WebView will handle any page we end up on.
     *
     * @param view this view
     * @param url the url to decide whether to use native or not
     * @return whether to use native browser instead of this view
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    /**
     * Each time the page is finished we check whether or not we are on the
     * page to which we intend to be redirected. Once we've ascertained we are
     * on the right place, we take the code parameter from the end of the URL and
     * return that to the user in a new UnwrappedResponse object.
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(WebView view, String url) {

        final CookieManager cookieManager = CookieManager.getInstance();

        if (cookieManager.getCookie(url) != null) {

            final StringTokenizer tokenizer = new StringTokenizer(cookieManager.getCookie(url), ";");
            final String startField = authNClient.getCookieNameWithDefault(AppConstants.DEFAULT_COOKIE_NAME);

            while (tokenizer.hasMoreTokens()) {
                String cookie = tokenizer.nextToken().trim();

                if (cookie.startsWith(startField)) {

                    UnwrappedResponse res = new UnwrappedResponse(RestConstants.HTTP_SUCCESS,
                            cookie.substring(cookie.indexOf("=") + 1),
                            AuthNAction.WEB_AUTH, AuthNAction.WEB_AUTH_FAIL);

                    activity.onEvent(AuthNAction.WEB_AUTH, res);
                }

            }

        }
    }

}