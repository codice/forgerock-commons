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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Example Content Provider for storing basic information in a SQLite database.
 */
public abstract class SampleContentProvider extends ContentProvider {

    private SampleDatabase dbHelper;

    @Override
    public boolean onCreate() {

        dbHelper = new SampleDatabase(getContext(), SampleContract.DB_NAME, null, SampleContract.VERSION);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        queryBuilder.setTables(getTable());
        queryBuilder.appendWhere(SampleContract.ID_COLUMN + "=" + 1);

        final Cursor cursor = queryBuilder.query(db, projection, null, null, null, null, null);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return SampleContract.CONTENT_TYPE;
    }

    private Uri getUri(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = db.insert(getTable(), null, contentValues);

        return getUri(id, uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numTouched = db.delete(getTable(), SampleContract.ID_COLUMN + "=" + 1, null);
        getContext().getContentResolver().notifyChange(uri, null);

        return numTouched;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numTouched = db.update(getTable(), contentValues, SampleContract.ID_COLUMN + "=" + 1, null);
        getContext().getContentResolver().notifyChange(uri, null);

        return numTouched;
    }

    protected abstract String getTable();
}
