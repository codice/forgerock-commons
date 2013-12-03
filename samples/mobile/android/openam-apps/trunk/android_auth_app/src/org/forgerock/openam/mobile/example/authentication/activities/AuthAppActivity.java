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

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import org.forgerock.openam.mobile.example.authentication.ApplicationContext;
import org.forgerock.openam.mobile.example.authentication.Presenter;
import org.forgerock.openam.mobile.example.authentication.R;
import org.forgerock.openam.mobile.auth.AuthenticationClient;

public class AuthAppActivity extends Activity {

    private static final String TAG = "AuthAppActvity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.home_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.forgerock_title_bar);
    }

    protected AuthenticationClient getAuthNClient() {
        return getPresenter().getAuthNClient();
    }

    protected Presenter getPresenter() {
        return ApplicationContext.getInstance().getPresenter();
    }

}
