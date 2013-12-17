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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.Hashtable;
import org.forgerock.openam.mobile.auth.AuthNAction;
import org.forgerock.openam.mobile.commons.ActionType;
import org.forgerock.openam.mobile.commons.Listener;
import org.forgerock.openam.mobile.example.authentication.AppConstants;
import org.forgerock.openam.mobile.example.authentication.activities.AuthAppActivity;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.callbacks.DrawableCallback;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.callbacks.NameCallback;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.callbacks.PasswordCallback;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.model.Authenticate;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.model.AuthenticateFactory;
import org.forgerock.openam.mobile.example.authentication.activities.authenticate.model.Callback;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adaptive authenticate activity. Draws elements from the callback it just
 * received (so long as they're PasswordCallback or NameCallback types).
 */
public class AuthenticateActivity extends AuthAppActivity implements Listener<String> {

    private static final String TAG = "AuthenticateActivity";

    /**
     * Interrogates the previous Intent. If we were passed here with an authentication callback
     * to display to the screen, draw it here.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent previous = getIntent();
        final String authCallbackString = previous.getStringExtra(AppConstants.AUTH_CALLBACK);

        Authenticate auth = null;
        boolean errored = false;
        try {
            auth = AuthenticateFactory.createFromString(authCallbackString);
        } catch (JSONException e) {
            Log.e(TAG, "Unable to generate JSON representation of authentication stage.", e);
            errored = true;
        }

        if (errored || auth == null || auth.getCallbacks() == null) {
            finish(); //if we error here, we cannot continue with the activity
        }

        final LinearLayout newLayout = new LinearLayout(this);
        newLayout.setOrientation(LinearLayout.VERTICAL);
        newLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //for each callback returned to us, create a drawable callback from it, auth not null here
        final DrawableCallback[] dcs = new DrawableCallback[auth.getCallbacks().length];

        //todo: clean this up, take it away from here.
        for(int i = 0; i < auth.getCallbacks().length; i++)
        {
            Callback cb = auth.getCallbacks()[i];

            if (cb.getType().equals(PasswordCallback.TYPE)) {
                dcs[i] = new PasswordCallback(cb.getOutput()[0].getValue(), cb.getInput()[0].getName(), this);
            } else if (cb.getType().equals(NameCallback.TYPE)) {
                dcs[i] = new NameCallback(cb.getOutput()[0].getValue(), cb.getInput()[0].getName(), this);
            }
        }

        //put our new drawables on to the screen
        for(DrawableCallback dc : dcs) {
            newLayout.addView(dc.getDisplayElement());
        }

        //add a submit button for the user to press
        Button submitButton = createSubmitButton(dcs, authCallbackString);
        newLayout.addView(submitButton);

        setContentView(newLayout);
    }

    /**
     * Draws the submit button for this authenticate stage.
     *
     * Upon clicking the button, we call the authentication client's authenticate function
     * passing back the now-filled-in json
     *
     * @param dcs The drawable callbacks
     * @param authCallbackString JSON data returned describing this auth step
     * @return the button object to display
     */
    private Button createSubmitButton(final DrawableCallback[] dcs, final String authCallbackString) {

        Button submitButton = new Button(this);
        submitButton.setText("Submit");
        submitButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        //for each callback,
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Hashtable<String, String> myVals = new Hashtable<String, String>();

                for (DrawableCallback dc : dcs) {
                    myVals.put(dc.getId(), dc.getValue());
                }

                JSONObject jsonToAlter = null;

                try {
                    jsonToAlter = new JSONObject(authCallbackString);
                    getAuthNClient().replaceAuthenticateInputVals(jsonToAlter, myVals);

                } catch (JSONException e) {
                    Log.e(TAG, "Unable to modify JSON representation of authentication stage.", e);
                }

                getAuthNClient().authenticate(jsonToAlter);
            }
        });

        return submitButton;
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
     * if the next activity is another authentication module
     * then generate the new intent and launch it.
     *
     * @param action Indicates the current stage of the process
     * @param response Response returned by the previous stage
     */
    @Override
    public void onEvent(ActionType action, String response) {

        if (action == AuthNAction.AUTH_CONT) {
            final Intent intent = new Intent();
            intent.setClass(this, AuthenticateActivity.class);
            intent.putExtra(AppConstants.AUTH_CALLBACK, response);
            this.startActivity(intent);
        }

        finish();
    }

}
