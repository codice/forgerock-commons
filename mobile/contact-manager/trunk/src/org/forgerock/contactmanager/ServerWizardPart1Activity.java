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

import static org.forgerock.contactmanager.Utils.checkEditValue;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * This class is the first step of the 'wizard' server configuration.
 */
public class ServerWizardPart1Activity extends AugmentedActivity {

    private boolean isEdit = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_server_wizard_part1);

        final Button btnNext = (Button) findViewById(R.id.sw1NextBtn);
        final EditText etServerName = (EditText) findViewById(R.id.sw1EditServerName);
        final EditText etServerAddress = (EditText) findViewById(R.id.sw1EditServerAddress);
        etServerName.requestFocus();

        // Edit mode
        final Intent previousIntent = getIntent();
        isEdit = previousIntent.getExtras() != null ? previousIntent.getExtras().getBoolean("isEdit") : false;
        if (isEdit) {
            final ServerConfiguration serverToEdit = AppContext.getServerConfiguration();
            etServerName.setText(serverToEdit.getServerName());
            etServerAddress.setText(serverToEdit.getAddress());
        }

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (checkEditValue(etServerAddress)) {
                    final Intent intent2 = new Intent(ServerWizardPart1Activity.this, ServerWizardPart2Activity.class);
                    intent2.putExtra("serverName", etServerName.getText().toString().trim());
                    final String address = etServerAddress.getText().toString()
                            .replace("https://", "")
                            .replace("http://", "")
                            .trim();
                    intent2.putExtra("address", address);
                    if (isEdit) {
                        intent2.putExtra("isEdit", true);
                    }
                    startActivityForResult(intent2, 10);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10) {
            final String msg = data.getStringExtra("returnedData");
            final Intent intent = new Intent();
            intent.putExtra("returnedData", msg);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
