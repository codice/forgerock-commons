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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SampleDatabase extends SQLiteOpenHelper {

    private static final String CREATE_SSO =
            "create table " + SampleContract.SSO_TABLE_NAME + " ( "
            + SampleContract.ID_COLUMN + " integer primary key, "
            + SampleContract.TOKEN_COLUMN + " text not null, "
            + SampleContract.CONFIG_COLUMN + " text not null); ";

    private static final String CREATE_TOKEN =
            "create table " + SampleContract.OAUTH_TABLE_NAME + " ( "
            + SampleContract.ID_COLUMN + " integer primary key, "
            + SampleContract.TOKEN_COLUMN + " text not null, "
            + SampleContract.CONFIG_COLUMN + " text not null); ";

    private static final String UPGRADE_TABLE =
            "drop table if exists " + SampleContract.SSO_TABLE_NAME + "; " +
            "drop table if exists " + SampleContract.OAUTH_TABLE_NAME + ";";

    public SampleDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_SSO);
        sqLiteDatabase.execSQL(CREATE_TOKEN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL(UPGRADE_TABLE);
        onCreate(sqLiteDatabase);
    }

}
