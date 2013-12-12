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

package org.forgerock.openam.mobile.example.authentication.activities.authenticate.callbacks;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A drawable (on Android) version of a NameCallback.
 */
public class NameCallback implements DrawableCallback {

    private final LinearLayout myLayout;
    private final String id;
    private final EditText myField;

    public static final String TYPE = "NameCallback";

    public NameCallback(String label, String id, Activity active) {
        this.id = id;

        myLayout = new LinearLayout(active);
        myLayout.setOrientation(LinearLayout.VERTICAL);
        myLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView myLabel = new TextView(active);
        myLabel.setText(label);

        myLayout.addView(myLabel);

        myField = new EditText(active);

        myLayout.addView(myField);
    }

    @Override
    public View getDisplayElement() {
        return myLayout;
    }

    @Override
    public String getValue() {
        return myField.getText().toString();
    }

    @Override
    public String getId() {
        return id;
    }
}