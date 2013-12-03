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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.forgerock.openam.mobile.auth.resources.OpenAMServerResource;
import org.forgerock.openam.mobile.commons.ValidationUtils;
import org.forgerock.openam.mobile.example.oauth2.ApplicationContext;
import org.forgerock.openam.mobile.example.oauth2.R;
import org.forgerock.openam.mobile.oauth.resources.OAuth2ServerResource;

/**
 * Interface for displaying config settings to the user
 */
public class SettingsActivity extends OAuth2AppActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        final OAuth2ServerResource originalOAuth = getAuthZClient().getOAuth2ServerResource();
        final OAuth2ServerResource configOAuth = new OAuth2ServerResource();

        final OpenAMServerResource originalOpenAM = getAuthNClient().getOpenAmServerResource();

        final Button saveButton = (Button) findViewById(R.id.settings_button);

        final TextView openamBase = (TextView) findViewById(R.id.settings_openambase_text);
        final TextView realm = (TextView) findViewById(R.id.settings_realm_text);
        final TextView cookieDomain = (TextView) findViewById(R.id.settings_cookie_domain_text);
        final TextView cookieName = (TextView) findViewById(R.id.settings_cookie_name_text);

        final EditText clientIdEdit = (EditText) findViewById(R.id.settings_client_id_text);
        final EditText clientSecretEdit = (EditText) findViewById(R.id.settings_client_secret_text);
        final EditText scopeEdit = (EditText) findViewById(R.id.settings_scope_text);
        final EditText redirectEdit = (EditText) findViewById(R.id.settings_redirect_text);

        if (originalOpenAM != null) {
            openamBase.setText(originalOpenAM.getOpenAmBase());
            realm.setText(originalOpenAM.getRealm());
            cookieDomain.setText(originalOpenAM.getCookieDomain());
            cookieName.setText(originalOpenAM.getCookieName());
        }

        if (originalOAuth != null) {
            clientIdEdit.setText(originalOAuth.getClientId());
            clientSecretEdit.setText(originalOAuth.getClientSecret());
            scopeEdit.setText(originalOAuth.getScope());
            redirectEdit.setText(originalOAuth.getRedirectUri());
        }

        //upon attempting to save, validate and then save the config if valid
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationUtils.validateAllText(SettingsActivity.this, clientIdEdit, clientSecretEdit, scopeEdit)
                        && ValidationUtils.validateAllUrl(SettingsActivity.this, redirectEdit)) {

                    configOAuth.setClientId(clientIdEdit.getText().toString());
                    configOAuth.setClientSecret(clientSecretEdit.getText().toString());
                    configOAuth.setScope(scopeEdit.getText().toString());
                    configOAuth.setRedirectUri(redirectEdit.getText().toString());

                    ApplicationContext.getInstance().saveServerConfiguration(configOAuth);

                    finish();
                }

            }
        });

    }

}
