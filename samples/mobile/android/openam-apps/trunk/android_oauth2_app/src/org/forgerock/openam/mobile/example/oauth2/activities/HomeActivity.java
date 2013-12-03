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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.commons.RestConstants;
import org.forgerock.openam.mobile.example.oauth2.AppConstants;
import org.forgerock.openam.mobile.example.oauth2.ApplicationContext;
import org.forgerock.openam.mobile.example.oauth2.R;
import org.forgerock.openam.mobile.oauth.OAuthAction;

/**
 * Our initial activity for the OAuth2.0 application.
 */
public class HomeActivity extends OAuth2AppActivity implements Listener<String> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        //ensures our global application context exists in a sane state
        ApplicationContext.getInstance(getApplicationContext());

        final Button homeAuthorizeButton = (Button) findViewById(R.id.home_authorize_button);
        final Button homeProfileButton = (Button) findViewById(R.id.home_profile_button);
        final Button homeDiscardButton = (Button) findViewById(R.id.home_discard_button);

        //to authorize, move to the appropriate activity
        homeAuthorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent = new Intent(HomeActivity.this, AuthorizeActivity.class);
                startActivity(intent);

            }
        });

        //
        homeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String base = getAuthNClient().getOpenAmServerResource().getOpenAmBase();
                final String cookieName = getAuthNClient().getCookieNameWithDefault(AppConstants.DEFAULT_COOKIE_NAME);
                final String ssoToken = getPresenter().getSSOToken();
                final String accessToken = getPresenter().getAccessToken();

                getAuthZClient().getProfile(base, accessToken, cookieName, ssoToken);
          }
        });

        homeDiscardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( getPresenter().deleteAccessToken() ) {
                    clearTextEdit(R.id.home_oauth2_text);
                    clearTextEdit(R.id.home_profile_text);
                    enableButtons(true, false, false);
                }

            }
        });

        //create in off mode, allow onResume to set them appropriately
        enableButtons(false, false, false);

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

    /**
     * for clearing the displayed strings
     * @param idToHide id of the textView element to clear
     */
    private void clearTextEdit(int idToHide) {
        TextView toHide = (TextView) findViewById(idToHide);
        toHide.setText("");
    }

    /**
     * convenience to let us alter the pressable buttons
     *
     * @param authorizeButton
     * @param profileButton
     * @param discardButton
     */
    private void enableButtons(boolean authorizeButton, boolean profileButton, boolean discardButton) {
        final Button homeAuthorizeButton = (Button) findViewById(R.id.home_authorize_button);
        final Button homeProfileButton = (Button) findViewById(R.id.home_profile_button);
        final Button homeDiscardButton = (Button) findViewById(R.id.home_discard_button);

        homeAuthorizeButton.setEnabled(authorizeButton);
        homeProfileButton.setEnabled(profileButton);
        homeDiscardButton.setEnabled(discardButton);
    }

    /**
     * used to check that we can execute the intent we want
     * to pass to the other application
     * @param intent
     * @return
     */
    private boolean canExecuteIntent(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }

    /**
     * Passes us over to the Auth app
     */
    private void passToAuthApp() {
        clearTextEdit(R.id.home_sso_text);
        enableButtons(false, false, false);

        Uri location = Uri.parse(RestConstants.AUTH_SHARE_SCHEME);
        Intent intent = new Intent(Intent.ACTION_SENDTO, location);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (canExecuteIntent(intent)) {
            showAuthenticateAlert(intent);
        } else {
            showAbortAlert();
        }

    }

    /**
     * displays a dialog that blocks input to the application and
     * moves the user off to the handling app
     */
    private void showAuthenticateAlert(final Intent intent) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Authenticate!");

        alertDialogBuilder
                .setMessage("Click 'okay' to authenticate with the ForgeRock Authentication app before using the OAuth2.0 app.")
                .setCancelable(false)
                .setPositiveButton("Okay!",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        startActivity(intent);
                        dialog.dismiss();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    /**
     * displays to the user a message then kicks them out having
     * blocked on user input
     */
    private void showAbortAlert() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Authenticate!");

        alertDialogBuilder
                .setMessage("You must have the ForgeRock Authentication app installed to use this app.")
                .setCancelable(false)
                .setPositiveButton("Quit!",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        dialog.dismiss();
                        HomeActivity.this.finish();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().unregisterListener(this);
    }

    /**
     * On resuming the activity we perform a number of actions
     * before we're ready to present to the user:
     *
     *  - register this activity as a listener to the presenter
     *  - reload the server configuration, to ensure we're up to date
     *  - if the user has no SSO token from authentication, move them to the authentication app
     *  - if there is no authorization server config, move to the settings
     *  - finally, if everything looks right attempt to validate the SSO token
     */
    @Override
    public void onResume() {
        super.onResume();

        getPresenter().registerListener(this);

        //could be coming in from anywhere, so make sure to reload here
        ApplicationContext.getInstance().reloadServerConfig();

        if (getAuthNClient().getOpenAmServerResource() == null) {
            passToAuthApp();
        }

        if (getAuthZClient().getOAuth2ServerResource() == null) {
            final Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            enableButtons(false, false, false);
            return;
        }

        final String ssoToken = getPresenter().getSSOToken();

        if (ssoToken != null) {
            getAuthNClient().isTokenValid(ssoToken);
        } else {
            passToAuthApp();
        }
    }

    /**
     * {@link Listener} implementation that controls the
     * view response to a listened-to action.
     *
     * @param action the action performed
     * @param response response information to display, if any
     */
    public void onEvent(ActionType action, String response) {

        if (action == OAuthAction.GET_PROFILE) {
            getProfileSuccess();
        } else if (action == OAuthAction.GET_PROFILE_FAIL) {
            getProfileFail();
        } else if (action == OAuthAction.VALIDATE) {
            validateOauthSuccess();
        } else if (action == OAuthAction.VALIDATE_FAIL) {
            validateOauthFail();
        } else if ( action == AuthNAction.VALIDATE) {
            validateAuthNSuccess();
        } else if ( action == AuthNAction.VALIDATE_FAIL) {
            validateAuthNFail();
        }
    }

    private void getProfileSuccess() {
        final String profile = getAuthZClient().getProfile();
        final TextView showProfile = (TextView) findViewById(R.id.home_profile_text);

        showProfile.setText(profile);
    }

    private void getProfileFail() {
        //nyi
    }

    /**
     * If we're validated, display the token
     */
    private void validateOauthSuccess() {
        final String accessToken = getPresenter().getAccessToken();
        final TextView showSSOToken = (TextView) findViewById(R.id.home_oauth2_text);

        showSSOToken.setText(accessToken);
        enableButtons(false, true, true);
    }

    /**
     * If we aren't validated,
     */
    private void validateOauthFail() {
        clearTextEdit(R.id.home_oauth2_text);
        clearTextEdit(R.id.home_profile_text);
        enableButtons(true, false, false);
    }

    /**
     * If the authentication token is valid, display it
     */
    private void validateAuthNSuccess() {
        final String ssoToken = getPresenter().getSSOToken();
        final TextView showSSOToken = (TextView) findViewById(R.id.home_sso_text);

        showSSOToken.setText(ssoToken);
        enableButtons(true, false, false);
    }

    /**
     * If the authentication token is not valid, move the user to the authentication app
     */
    private void validateAuthNFail() {
        AndroidUtils.showToast("Unable to validate token.", this);
        passToAuthApp();
    }

}