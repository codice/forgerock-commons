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

package org.forgerock.openam.mobile.example.authentication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.example.authentication.AppConstants;
import org.forgerock.openam.mobile.example.authentication.ApplicationContext;
import org.forgerock.openam.mobile.example.authentication.R;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.AuthenticateActivity;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.LoginWebActivity;

public class HomeActivity extends AuthAppActivity implements Listener<String> {

    private static final String TAG = "HomeActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        ApplicationContext.getInstance(getApplicationContext());
        CookieSyncManager.createInstance(getApplicationContext());

        final Button loginNativeButton = (Button) findViewById(R.id.home_login_native_button);
        final Button logoutNativeButton = (Button) findViewById(R.id.home_logout_native_button);
        final Button loginWebButton = (Button) findViewById(R.id.home_login_web_button);
        final Button clearLocalButton = (Button) findViewById(R.id.home_clear_local_button);

        loginNativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAuthNClient().authenticate();
            }
        });

        logoutNativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ssoToken = getPresenter().getSSOToken();
                getAuthNClient().logout(ssoToken);
            }
        });

        loginWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(HomeActivity.this, LoginWebActivity.class);
                startActivity(intent);
            }
        });

        clearLocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPresenter().removeLocalSSOToken()) {
                    hideToken();
                    enableButtons(true, false);
                }
            }
        });

        //create in off mode, allow onResume to set them appropriately
        enableButtons(false, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        switch(itemId) {
            case R.id.login_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.login_menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }

        return true;
    }

    private void hideToken() {
        final TextView showSSOToken = (TextView) findViewById(R.id.login_sso_text);
        showSSOToken.setText("");
    }

    private void enableButtons(boolean loginButtons, boolean logoutButton) {

        final Button loginNativeButton = (Button) findViewById(R.id.home_login_native_button);
        final Button logoutNativeButton = (Button) findViewById(R.id.home_logout_native_button);
        final Button loginWebButton = (Button) findViewById(R.id.home_login_web_button);
        final Button clearLocalButton = (Button) findViewById(R.id.home_clear_local_button);

        loginNativeButton.setEnabled(loginButtons);
        loginWebButton.setEnabled(loginButtons);
        logoutNativeButton.setEnabled(logoutButton);
        clearLocalButton.setEnabled(logoutButton);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    http:
        getPresenter().registerListener(this);

        if (getAuthNClient().getOpenAmServerResource() == null) {
            passToSettings();
        } else {

            final String ssoToken = getPresenter().getSSOToken();

            if (ssoToken != null) {
                getAuthNClient().isTokenValid(ssoToken);
            } else {
                enableButtons(true, false);
            }
        }
    }

    @Override
    public void onEvent(ActionType action, String response) {

        if (action == AuthNAction.VALIDATE) {
            validateSuccess();
        } else if (action == AuthNAction.VALIDATE_FAIL) {
            validateFail();
        } else if (action == AuthNAction.LOGOUT) {
            logoutSuccess();
        } else if (action == AuthNAction.LOGOUT_FAIL) {
            logoutFail();
        } else if (action == AuthNAction.AUTH_FAIL) {
            authFail();
        } else if (action == AuthNAction.AUTH_CONT) {
            transferToAuth(response);
        }

    }

    private void transferToAuth(String response) {
        final Intent intent = new Intent();
        intent.setClass(this, AuthenticateActivity.class);
        intent.putExtra(AppConstants.AUTH_CALLBACK, response);
        this.startActivity(intent);
    }

    private void validateSuccess() {
        final String ssoToken = getPresenter().getSSOToken();
        final TextView showSSOToken = (TextView) findViewById(R.id.login_sso_text);

        showSSOToken.setText(ssoToken);
        enableButtons(false, true);
    }

    private void validateFail() {
        AndroidUtils.showToast("Unable to validate token.", this);
        enableButtons(true, false);
        hideToken();
    }

    private void logoutSuccess() {
        enableButtons(true, false);
        hideToken();
    }

    private void authFail() {
        AndroidUtils.showToast("Unable to process authentication request.", this);
    }

    private void logoutFail() {
        AndroidUtils.showToast("Logout attempt failed.", this);
    }

    private void passToSettings() {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}