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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.auth.resources.OpenAMServerResource;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.AndroidUtils;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.commons.ValidationUtils;
import org.forgerock.openam.mobile.example.authentication.ApplicationContext;
import org.forgerock.openam.mobile.example.authentication.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Configuring the authentication server for the AuthenticationClient
 * is done in this Activity.
 */
public class SettingsActivity extends AuthAppActivity implements Listener<String> {

    private static final String TAG = "SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        final OpenAMServerResource originalOpenAM = getAuthNClient().getOpenAmServerResource();
        final OpenAMServerResource configOpenAM = new OpenAMServerResource();

        final Button loadDomainsButton = (Button) findViewById(R.id.settings_domains_button);
        final Button saveButton = (Button) findViewById(R.id.settings_save_button);

        final EditText openamBaseEdit = (EditText) findViewById(R.id.settings_openambase_text);
        final EditText realmEdit = (EditText) findViewById(R.id.settings_realm_text);
        final EditText cookieDomainEdit = (EditText) findViewById(R.id.settings_cookie_domain_text);
        final EditText cookieNameEdit = (EditText) findViewById(R.id.settings_cookie_name_text);

        if (originalOpenAM != null) {
            openamBaseEdit.setText(originalOpenAM.getOpenAmBase());
            realmEdit.setText(originalOpenAM.getRealm());
            cookieDomainEdit.setText(originalOpenAM.getCookieDomain());
            cookieNameEdit.setText(originalOpenAM.getCookieName());
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationUtils.validateAllUrl(SettingsActivity.this, openamBaseEdit) &&
                        ValidationUtils.validateSubdomain(SettingsActivity.this, cookieDomainEdit, openamBaseEdit)) {

                    configOpenAM.setRealm(realmEdit.getText().toString());
                    configOpenAM.setOpenAmBase(openamBaseEdit.getText().toString());
                    configOpenAM.setCookieDomain(cookieDomainEdit.getText().toString());
                    configOpenAM.setCookieName(cookieNameEdit.getText().toString());

                    ApplicationContext.getInstance().saveServerConfiguration(configOpenAM);

                    finish();
                }

            }
        });

        openamBaseEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {
                    if (ValidationUtils.validateAllUrl(SettingsActivity.this, openamBaseEdit)) {
                        getAuthNClient().cookieName(openamBaseEdit.getText().toString());
                    }
                }
            }
        });

        loadDomainsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationUtils.validateAllUrl(SettingsActivity.this, openamBaseEdit)) {
                    getAuthNClient().cookieDomain(openamBaseEdit.getText().toString());
                }
            }
        });

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

    /**
     * when we get the cookie name returned
     */
    private void cookieNameSuccess(String name) {
        final EditText cookieNameEdit = (EditText) findViewById(R.id.settings_cookie_name_text);
        cookieNameEdit.setText(name);
    }

    /**
     * if we fail to get the cookie name from the server
     */
    private void cookieNameFail() {
        AndroidUtils.showToast("Unable to load cookie domains.",this);
    }

    /**
     * when we get the list of cookies returned from the server
     */
    private void cookieDomainSuccess(String domains) {

        final JSONObject json;
        final JSONArray domainArray;
        final List<String> options = new ArrayList<String>();

        try {
            json = new JSONObject(domains);
            domainArray = json.getJSONArray("domains");

            for(int i = 0; i < domainArray.length(); i++) {
                options.add(domainArray.getString(i));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Unable to objectify the returned JSON.", e);
        }

        final String[] optArray = options.toArray(new String[options.size()]);
        final EditText cookieDomainEdit = (EditText) findViewById(R.id.settings_cookie_domain_text);

        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Select Domain");
        builder.setItems(optArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cookieDomainEdit.setText(optArray[which]);
            }
        });

        builder.create().show();
    }

    private void cookieDomainFail() {
        AndroidUtils.showToast("Unable to load cookie domains.", this);
    }

    @Override
    public void onEvent(ActionType action, String response) {
        if (action == AuthNAction.GET_COOKIE_NAME) {
            cookieNameSuccess(response);
        } else if (action == AuthNAction.GET_COOKIE_NAME_FAIL) {
            cookieNameFail();
        } else if (action == AuthNAction.GET_COOKIE_DOMAINS) {
            cookieDomainSuccess(response);
        } else if (action == AuthNAction.GET_COOKIE_DOMAINS_FAIL) {
            cookieDomainFail();
        }
    }
}
