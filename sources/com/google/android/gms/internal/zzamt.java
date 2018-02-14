package com.google.android.gms.internal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

final class zzamt extends SQLiteOpenHelper {
    private /* synthetic */ zzams zzagu;

    zzamt(zzams zzams, Context context, String str) {
        this.zzagu = zzams;
        super(context, str, null, 1);
    }

    private static void zza(SQLiteDatabase sQLiteDatabase) {
        int i = 0;
        Set zzb = zzb(sQLiteDatabase, "properties");
        String[] strArr = new String[]{"app_uid", "cid", "tid", "params", "adid", "hits_count"};
        while (i < 6) {
            Object obj = strArr[i];
            if (zzb.remove(obj)) {
                i++;
            } else {
                String str = "Database properties is missing required column: ";
                String valueOf = String.valueOf(obj);
                throw new SQLiteException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            }
        }
        if (!zzb.isEmpty()) {
            throw new SQLiteException("Database properties table has extra columns");
        }
    }

    private final boolean zza(SQLiteDatabase sQLiteDatabase, String str) {
        Object e;
        Throwable th;
        Cursor cursor = null;
        Cursor query;
        try {
            SQLiteDatabase sQLiteDatabase2 = sQLiteDatabase;
            query = sQLiteDatabase2.query("SQLITE_MASTER", new String[]{"name"}, "name=?", new String[]{str}, null, null, null);
            try {
                boolean moveToFirst = query.moveToFirst();
                if (query == null) {
                    return moveToFirst;
                }
                query.close();
                return moveToFirst;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    this.zzagu.zzc("Error querying for table", str, e);
                    if (query != null) {
                        query.close();
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    cursor = query;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            this.zzagu.zzc("Error querying for table", str, e);
            if (query != null) {
                query.close();
            }
            return false;
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    private static Set<String> zzb(SQLiteDatabase sQLiteDatabase, String str) {
        Set<String> hashSet = new HashSet();
        Cursor rawQuery = sQLiteDatabase.rawQuery(new StringBuilder(String.valueOf(str).length() + 22).append("SELECT * FROM ").append(str).append(" LIMIT 0").toString(), null);
        try {
            String[] columnNames = rawQuery.getColumnNames();
            for (Object add : columnNames) {
                hashSet.add(add);
            }
            return hashSet;
        } finally {
            rawQuery.close();
        }
    }

    public final SQLiteDatabase getWritableDatabase() {
        if (this.zzagu.zzagt.zzu(3600000)) {
            SQLiteDatabase writableDatabase;
            try {
                writableDatabase = super.getWritableDatabase();
            } catch (SQLiteException e) {
                this.zzagu.zzagt.start();
                this.zzagu.zzbs("Opening the database failed, dropping the table and recreating it");
                this.zzagu.getContext().getDatabasePath("google_analytics_v4.db").delete();
                try {
                    writableDatabase = super.getWritableDatabase();
                    this.zzagu.zzagt.clear();
                } catch (SQLiteException e2) {
                    this.zzagu.zze("Failed to open freshly created database", e2);
                    throw e2;
                }
            }
            return writableDatabase;
        }
        throw new SQLiteException("Database open failed");
    }

    public final void onCreate(SQLiteDatabase sQLiteDatabase) {
        String path = sQLiteDatabase.getPath();
        if (zzanr.version() >= 9) {
            File file = new File(path);
            file.setReadable(false, false);
            file.setWritable(false, false);
            file.setReadable(true, true);
            file.setWritable(true, true);
        }
    }

    public final void onOpen(SQLiteDatabase sQLiteDatabase) {
        int i = 1;
        if (VERSION.SDK_INT < 15) {
            Cursor rawQuery = sQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
            try {
                rawQuery.moveToFirst();
            } finally {
                rawQuery.close();
            }
        }
        if (zza(sQLiteDatabase, "hits2")) {
            Set zzb = zzb(sQLiteDatabase, "hits2");
            String[] strArr = new String[]{"hit_id", "hit_string", "hit_time", "hit_url"};
            int i2 = 0;
            while (i2 < 4) {
                Object obj = strArr[i2];
                if (zzb.remove(obj)) {
                    i2++;
                } else {
                    String str = "Database hits2 is missing required column: ";
                    String valueOf = String.valueOf(obj);
                    throw new SQLiteException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                }
            }
            if (zzb.remove("hit_app_id")) {
                i = 0;
            }
            if (!zzb.isEmpty()) {
                throw new SQLiteException("Database hits2 has extra columns");
            } else if (i != 0) {
                sQLiteDatabase.execSQL("ALTER TABLE hits2 ADD COLUMN hit_app_id INTEGER");
            }
        } else {
            sQLiteDatabase.execSQL(zzams.zzagp);
        }
        if (zza(sQLiteDatabase, "properties")) {
            zza(sQLiteDatabase);
        } else {
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS properties ( app_uid INTEGER NOT NULL, cid TEXT NOT NULL, tid TEXT NOT NULL, params TEXT NOT NULL, adid INTEGER NOT NULL, hits_count INTEGER NOT NULL, PRIMARY KEY (app_uid, cid, tid)) ;");
        }
    }

    public final void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }
}
