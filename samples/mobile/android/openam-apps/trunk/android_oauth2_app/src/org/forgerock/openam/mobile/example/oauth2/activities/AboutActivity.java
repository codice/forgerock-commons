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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import org.forgerock.openam.mobile.example.oauth2.AppConstants;
import org.forgerock.openam.mobile.example.oauth2.R;

/**
 * Shows the about page for this application
 */
public class AboutActivity extends OAuth2AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);

        final ImageView forgerockClickable = (ImageView) findViewById(R.id.about_forgerock_img);
        forgerockClickable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(AppConstants.FORGEROCK_URL));
                startActivity(intent);
            }
        });

        final ImageView oauth2Clickable = (ImageView) findViewById(R.id.about_oauth2_img);
        oauth2Clickable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(AppConstants.OAUTH2_URL));
                startActivity(intent);
            }
        });
    }
}
