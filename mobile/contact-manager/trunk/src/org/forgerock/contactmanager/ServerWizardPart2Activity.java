/*
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
 *
 *       Copyright 2013-2014 ForgeRock AS.
 */

package org.forgerock.contactmanager;

import static org.forgerock.contactmanager.Utils.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

/**
 * This class is the second step of the 'wizard' server configuration.
 */
public class ServerWizardPart2Activity extends AugmentedActivity {

    private boolean isEdit;
    ServerConfiguration original;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_server_wizard_part2);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /*
         * Declare used objects.
         */
        final Button btnFinish = (Button) findViewById(R.id.sw2AccountFinishBtn);
        final EditText etUsername = (EditText) findViewById(R.id.sw2EditAccountUserName);
        final EditText etPassword = (EditText) findViewById(R.id.sw2EditAccountServerPassword);
        final ToggleButton toogleSSL = (ToggleButton) findViewById(R.id.sw2_toggle_ssl);
        etUsername.requestFocus();

        /*
         * Retrieves previous server configuration.
         */
        final Intent previous = getIntent();
        // Edit mode
        isEdit = previous.getExtras() != null ? previous.getExtras().getBoolean("isEdit") : false;
        if (isEdit) {
            original = AppContext.getServerConfiguration();
            etUsername.setText(original.getUsername());
            etPassword.setText(original.getPassword());
            toogleSSL.setChecked(original.isSSL());
        }

        final ServerConfiguration newServerConfiguration = new ServerConfiguration(
                previous.getStringExtra("serverName"), previous.getStringExtra("address"));

        /*
         * Finish. Sends data to main menu.
         */
        btnFinish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (checkEditValue(etUsername) && checkEditValue(etPassword)) {

                    newServerConfiguration.setUsername(etUsername.getText().toString().trim());
                    newServerConfiguration.setPassword(etPassword.getText().toString().trim());
                    newServerConfiguration.setSSL(toogleSSL.isChecked());

                    if (isEdit) {
                        deleteServerConfigurationFromPreferences(original.getServerName());
                    }
                    saveCurrentServer(newServerConfiguration);
                    saveActiveServer(newServerConfiguration);

                    final Intent intent = getIntent();
                    intent.putExtra("returnedData", "new server added");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
