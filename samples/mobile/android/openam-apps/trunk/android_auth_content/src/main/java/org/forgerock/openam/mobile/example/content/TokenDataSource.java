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

package org.forgerock.openam.mobile.example.content;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class TokenDataSource {

    public boolean deleteAccessToken(Context context) {
        return deleteToken(context, SampleContract.TOKEN_URI);
    }

    public boolean deleteSSOToken(Context context) {
        return deleteToken(context, SampleContract.SSO_URI);
    }

    public boolean storeSSOToken(String token, Context context, String config) {
        return storeToken(token, context, config, SampleContract.SSO_URI);
    }

    public boolean storeAccessToken(String token, Context context, String config) {
        return storeToken(token, context, config, SampleContract.TOKEN_URI);
    }

    public String getOpenamConfig(Context context) {
        return doQuery(context, SampleContract.CONFIG_COLUMN, SampleContract.SSO_URI);
    }

    public String getOAuthConfig(Context context) {
        return doQuery(context, SampleContract.CONFIG_COLUMN, SampleContract.TOKEN_URI);
    }

    public String getSSOToken(Context context) {
        return getToken(context, SampleContract.SSO_URI);
    }

    public String getAccessToken(Context context) {
        return getToken(context, SampleContract.TOKEN_URI);
    }

    private String getToken(Context context, Uri loc) {
        return doQuery(context, SampleContract.TOKEN_COLUMN, loc);
    }


    private boolean storeToken(String token, Context context, String config, Uri loc) {
        ContentValues cval = new ContentValues();
        cval.put(SampleContract.ID_COLUMN, 1);
        cval.put(SampleContract.TOKEN_COLUMN, token);
        cval.put(SampleContract.CONFIG_COLUMN, config);

        return doUpsert(context, cval, loc);
    }

    /**
     * Insert / Update as appropriate
     *
     * @param context To get our content resolver from
     * @param cval The values to insert
     * @param loc The URI to insert the values to
     * @return success / faiolure indicator
     */
    private boolean doUpsert(Context context, ContentValues cval, Uri loc) {

        boolean retVal = false;

        synchronized (this) {

            if  (getToken(context, loc) == null) {
                Uri uri = context.getContentResolver().insert(loc, cval);
                if (uri != null) {
                    retVal = true;
                }
            } else {
                context.getContentResolver().update(loc, cval, null, null);
                retVal = true;
            }
        }

        return retVal;
    }

    private String doQuery(Context context, String projection, Uri uri) {

        String[] proj = { projection };

        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(projection));
        }

        return null;
    }

    private boolean deleteToken(Context context, Uri loc) {
        int num = context.getContentResolver().delete(loc, null, null);
        if (num > 0) {
            return true;
        }

        return false;
    }

}
