package com.squareup.picasso;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public final class PicassoProvider extends ContentProvider {
    static Context context;

    public final boolean onCreate() {
        context = getContext();
        return true;
    }

    public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    public final String getType(Uri uri) {
        return null;
    }

    public final Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public final int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public final int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
