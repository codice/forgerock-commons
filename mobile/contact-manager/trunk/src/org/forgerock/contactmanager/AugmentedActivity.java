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

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * This class allows the use of a custom title bar in all activities in the application.
 */
public abstract class AugmentedActivity extends Activity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.activity_search);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.forgerock_title_bar);
    }
}
