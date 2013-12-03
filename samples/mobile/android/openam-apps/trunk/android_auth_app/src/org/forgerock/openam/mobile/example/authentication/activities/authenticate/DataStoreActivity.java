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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Hashtable;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.commons.ValidationUtils;
import org.forgerock.openam.mobile.example.authentication.AppConstants;
import org.forgerock.openam.mobile.example.authentication.R;
import org.forgerock.openam.mobile.example.authentication.activities.AuthAppActivity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity specific to the DataStore type. Demonstrating how individual authentication
 * modules can have different specific activities if required.
 */
public class DataStoreActivity extends AuthAppActivity implements Listener<String> {

    private static final String TAG = "DataStoreActivity";

    /**
     * When this activity is first loaded
     *
     * @param savedInstanceState A previous state held by the system to load
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datastore_layout);

        final Button submitButton = (Button) findViewById(R.id.datastore_button);

        final EditText usernameEdit = (EditText) findViewById(R.id.datastore_username_text);
        final EditText passwordEdit = (EditText) findViewById(R.id.datastore_password_text);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ValidationUtils.validateAllText(DataStoreActivity.this, usernameEdit, passwordEdit)) {

                    //use intent here to pass the json data between the pages
                    Intent previous = getIntent();
                    String jsonToFill = previous.getStringExtra(AppConstants.AUTH_CALLBACK);

                    JSONObject json = null;

                    try {
                        json = new JSONObject(jsonToFill);
                    } catch (JSONException e) {
                        Log.e(TAG, "Unable to unmarshall JSON sent to this authentication stage.");
                    }

                    try {
                        Hashtable<String, String> myVals = new Hashtable<String, String>();
                        myVals.put("IDToken1", usernameEdit.getText().toString());
                        myVals.put("IDToken2", passwordEdit.getText().toString());

                        getAuthNClient().replaceAuthenticateInputVals(json, myVals);

                    } catch (JSONException e) {
                        Log.e(TAG, "Unable to modify JSON representation of authentication stage.");
                    }

                    getAuthNClient().authenticate(json);

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
     * See {@link AuthenticateActivity} on how this could continue to chain if
     * it received AUTH_CONT. However, as this is not adaptive it would require to
     * determine the type of the next module it's passing.
     *
     * @param action Indicates the current stage of the process
     * @param response Response returned by the previous stage
     */
    @Override
    public void onEvent(ActionType action, String response) {
        finish();
    }

}
