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

package org.forgerock.openam.mobile.example.authentication.activities.authenticate;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.example.authentication.R;
import org.forgerock.openam.mobile.example.authentication.activities.AuthAppActivity;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.webview.LoginWebClient;

/**
 * Login activity that wraps the LoginWebClient
 */
public class LoginWebActivity extends AuthAppActivity implements Listener<String> {

    private static final String TAG = "LoginWebActivity";

    /**
     * Sets up the webview, and displays it to the user
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        final WebView webView = (WebView) findViewById(R.id.webview);
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);

        WebViewClient loginClient = new LoginWebClient(getPresenter(), getAuthNClient());

        webView.setWebViewClient(loginClient);

        webView.loadUrl(getAuthNClient().getOpenAmServerResource().getWebLoginUrl());
    }

    /**
     * stop listening when we leave the activity
     */
    @Override
    public void onPause() {
        super.onPause();
        getPresenter().unregisterListener(this);
    }

    /**
     * start listening any time we're in the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        getPresenter().registerListener(this);
    }

    /**
     * handles the resulting values from the webviewclient
     *
     * @param action the actiontype to check
     * @param response
     */
    @Override
    public void onEvent(ActionType action, String response) {
        if (action == AuthNAction.WEB_AUTH) {
            webAuthSuccess();
        } else if (action == AuthNAction.WEB_AUTH_FAIL) {
            webAuthFail();
        }
    }

    /**
     * to perform post-gathering the cookie
     */
    private void webAuthSuccess() {
        finish(); //then close the window
    }

    /**
     * to be performed when we client reports
     * it has failed
     */
    private void webAuthFail() {
        //nyi
    }

}
