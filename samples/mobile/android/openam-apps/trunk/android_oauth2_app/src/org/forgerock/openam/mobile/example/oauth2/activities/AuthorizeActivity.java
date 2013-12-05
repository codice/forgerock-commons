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

package org.forgerock.openam.mobile.example.oauth2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.example.oauth2.R;
import org.forgerock.openam.mobile.example.oauth2.activities.webview.AuthorizeWebClient;
import org.forgerock.openam.mobile.oauth.OAuthAction;

/**
 * Activity to authorize via a web client.
 */
public class AuthorizeActivity extends OAuth2AppActivity implements Listener<String> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        final WebView webView = (WebView) findViewById(R.id.webview);
        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);

        AuthorizeWebClient awc = new AuthorizeWebClient(getPresenter(),
                getPresenter().getSSOToken(), getAuthNClient(), getAuthZClient());

        webView.setWebViewClient(awc);

        final String base = getAuthNClient().getOpenAmServerResource().getOpenAmBase();

        webView.loadUrl(getAuthZClient().getOAuth2ServerResource().getAuthorizeUrl(base));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().registerListener(this);
    }

    @Override
    public void onEvent(ActionType action, String response) {
        if (action == OAuthAction.GET_CODE) {
            getCodeSuccess();
        } else if (action == OAuthAction.GET_CODE_FAIL) {
            getCodeFail();
        }
    }

    private void getCodeSuccess() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish(); //then close the window
    }

    private void getCodeFail() {
        AndroidUtils.showToast("Getting the authorization grant code failed", this);
    }
}
