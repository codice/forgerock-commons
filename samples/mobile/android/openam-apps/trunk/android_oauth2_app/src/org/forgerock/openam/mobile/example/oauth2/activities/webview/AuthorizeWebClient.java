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

package org.forgerock.openam.mobile.example.oauth2.activities.webview;

import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.forgerock.openam.mobile.auth.AuthenticationClient;
import org.forgerock.openam.mobile.auth.resources.OpenAMServerResource;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.commons.RestConstants;
import org.forgerock.openam.mobile.commons.UnwrappedResponse;
import org.forgerock.openam.mobile.example.oauth2.AppConstants;
import org.forgerock.openam.mobile.oauth.AuthZConstants;
import org.forgerock.openam.mobile.oauth.AuthorizationClient;
import org.forgerock.openam.mobile.oauth.OAuthAction;

/**
 * This class demonstrates authorizing with an OpenAM OAuth2.0 system
 * using a previously-gathered and valid SSO Token (passed in here as tokenId in the constructor).
 *
 * On every page load, we check to see if the user has been given a cookie with the
 * appropriate cookie name by the site. Upon discovering this cookie, we return the
 * authorization to the listening activity, which can propogate or act on it as desired.
 */
public class AuthorizeWebClient extends WebViewClient {

    private final String TAG = "AuthorizeWebClient";

    private final Listener<UnwrappedResponse> activity;
    private final AuthorizationClient authZClient;
    private final AuthenticationClient authNClient;
    private final String tokenId;

    /**
     * Creates the web client, with the appropriate listener and tokenId to attempt to authorize.
     * Also checks and clears the cookie manager so we don't
     * inadvertantly use an old session.
     * @param presenter
     */
    public AuthorizeWebClient(Listener<UnwrappedResponse> presenter, String tokenId,
                              AuthenticationClient authNClient, AuthorizationClient authZClient) {
        this.activity = presenter;
        this.authZClient = authZClient;
        this.authNClient = authNClient;
        this.tokenId = tokenId;

        configureClient(authNClient.getOpenAmServerResource());
    }

    /**
     * Configures the client with the token needed to authenticate to the server
     *
     * @param resource The openam server information
     */
    private void configureClient(OpenAMServerResource resource) {

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        if (tokenId != null) {
            String cookieName = authNClient.getCookieNameWithDefault(AppConstants.DEFAULT_COOKIE_NAME);
            String domain = authNClient.getCookieDomainWithDefault(Uri.parse(resource.getOpenAmBase()).getHost());
            String url = authNClient.getOpenAmServerResource().getWebLoginUrl();

            insertCookie(cookieName, tokenId, domain, url);
        }
    }

    /**
     * Used to append our authentication cookie to the authorization request
     *
     * @param name name of the cookie
     * @param value value of the cookie
     * @param domain domain in which the cookie applies
     * @param url url to register the cookie against in the cookie manager
     */
    public void insertCookie(String name, String value, String domain, String url) {
        StringBuilder cookie = new StringBuilder(name);
        cookie.append("=").append(value);
        cookie.append("; domain=").append(domain);
        cookie.append("; path=/");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookie.toString());
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
        String prefix = authZClient.getOAuth2ServerResource().getRedirectUri();

        if (url.startsWith(prefix)) {

            URI tempUri = null;
            try {
                tempUri = new URI(url);
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to validate URI correctly.", e);
            }

            List<NameValuePair> params = URLEncodedUtils.parse(tempUri, RestConstants.UTF8);

            for(NameValuePair nvp : params) {
                if (nvp.getName().equals(AuthZConstants.CODE)) {

                    UnwrappedResponse res = new UnwrappedResponse(RestConstants.HTTP_SUCCESS, nvp.getValue(),
                            OAuthAction.GET_CODE, OAuthAction.GET_CODE_FAIL);

                    activity.onEvent(OAuthAction.GET_CODE, res);
                }
            }
        }

    }

}
