package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri.Builder;
import android.text.TextUtils;
import com.google.android.gms.analytics.zzl;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zzm;
import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class zzams extends zzamh implements Closeable {
    private static final String zzagp = String.format("CREATE TABLE IF NOT EXISTS %s ( '%s' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, '%s' INTEGER NOT NULL, '%s' TEXT NOT NULL, '%s' TEXT NOT NULL, '%s' INTEGER);", new Object[]{"hits2", "hit_id", "hit_time", "hit_url", "hit_string", "hit_app_id"});
    private static final String zzagq = String.format("SELECT MAX(%s) FROM %s WHERE 1;", new Object[]{"hit_time", "hits2"});
    private final zzamt zzagr;
    private final zzaoo zzags = new zzaoo(zzkq());
    private final zzaoo zzagt = new zzaoo(zzkq());

    zzams(zzamj zzamj) {
        super(zzamj);
        this.zzagr = new zzamt(this, zzamj.getContext(), "google_analytics_v4.db");
    }

    private final long zza$6e791f8(String str, String[] strArr) {
        Object e;
        Throwable th;
        Cursor cursor = null;
        try {
            Cursor rawQuery = getWritableDatabase().rawQuery(str, strArr);
            try {
                if (rawQuery.moveToFirst()) {
                    long j = rawQuery.getLong(0);
                    if (rawQuery == null) {
                        return j;
                    }
                    rawQuery.close();
                    return j;
                }
                if (rawQuery != null) {
                    rawQuery.close();
                }
                return 0;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = rawQuery;
                try {
                    zzd("Database error", str, e);
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                cursor = rawQuery;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (SQLiteException e3) {
            e = e3;
            zzd("Database error", str, e);
            throw e;
        }
    }

    private final long zzb$41d7834c(String str) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(str, null);
            if (cursor.moveToFirst()) {
                long j = cursor.getLong(0);
                if (cursor != null) {
                    cursor.close();
                }
                return j;
            }
            throw new SQLiteException("Database returned empty set");
        } catch (SQLiteException e) {
            zzd("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private final Map<String, String> zzbt(String str) {
        if (TextUtils.isEmpty(str)) {
            return new HashMap(0);
        }
        try {
            if (!str.startsWith("?")) {
                String str2 = "?";
                String valueOf = String.valueOf(str);
                str = valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2);
            }
            return zzm.zza(new URI(str), "UTF-8");
        } catch (URISyntaxException e) {
            zze("Error parsing hit parameters", e);
            return new HashMap(0);
        }
    }

    private final Map<String, String> zzbu(String str) {
        if (TextUtils.isEmpty(str)) {
            return new HashMap(0);
        }
        try {
            String str2 = "?";
            String valueOf = String.valueOf(str);
            return zzm.zza(new URI(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2)), "UTF-8");
        } catch (URISyntaxException e) {
            zze("Error parsing property parameters", e);
            return new HashMap(0);
        }
    }

    private final long zzkN() {
        zzl.zzjC();
        zzkD();
        return zzb$41d7834c("SELECT COUNT(*) FROM hits2");
    }

    private final List<Long> zzn(long j) {
        Object e;
        Throwable th;
        Cursor cursor = null;
        zzl.zzjC();
        zzkD();
        if (j <= 0) {
            return Collections.emptyList();
        }
        SQLiteDatabase writableDatabase = getWritableDatabase();
        List<Long> arrayList = new ArrayList();
        Cursor query;
        try {
            query = writableDatabase.query("hits2", new String[]{"hit_id"}, null, null, null, null, String.format("%s ASC", new Object[]{"hit_id"}), Long.toString(j));
            try {
                if (query.moveToFirst()) {
                    do {
                        arrayList.add(Long.valueOf(query.getLong(0)));
                    } while (query.moveToNext());
                }
                if (query != null) {
                    query.close();
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzd("Error selecting hit ids", e);
                    if (query != null) {
                        query.close();
                    }
                    return arrayList;
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
            zzd("Error selecting hit ids", e);
            if (query != null) {
                query.close();
            }
            return arrayList;
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
        return arrayList;
    }

    public final void beginTransaction() {
        zzkD();
        getWritableDatabase().beginTransaction();
    }

    public final void close() {
        try {
            this.zzagr.close();
        } catch (SQLiteException e) {
            zze("Sql error closing database", e);
        } catch (IllegalStateException e2) {
            zze("Error closing database", e2);
        }
    }

    public final void endTransaction() {
        zzkD();
        getWritableDatabase().endTransaction();
    }

    final SQLiteDatabase getWritableDatabase() {
        try {
            return this.zzagr.getWritableDatabase();
        } catch (SQLiteException e) {
            zzd("Error opening database", e);
            throw e;
        }
    }

    final boolean isEmpty() {
        return zzkN() == 0;
    }

    public final void setTransactionSuccessful() {
        zzkD();
        getWritableDatabase().setTransactionSuccessful();
    }

    public final long zza(long j, String str, String str2) {
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzkD();
        zzl.zzjC();
        return zza$6e791f8("SELECT hits_count FROM properties WHERE app_uid=? AND cid=? AND tid=?", new String[]{String.valueOf(j), str, str2});
    }

    public final void zzc(zzanx zzanx) {
        String str;
        zzbo.zzu(zzanx);
        zzl.zzjC();
        zzkD();
        zzbo.zzu(zzanx);
        Builder builder = new Builder();
        for (Entry entry : zzanx.zzdV().entrySet()) {
            str = (String) entry.getKey();
            if (!("ht".equals(str) || "qt".equals(str) || "AppUID".equals(str))) {
                builder.appendQueryParameter(str, (String) entry.getValue());
            }
        }
        String encodedQuery = builder.build().getEncodedQuery();
        str = encodedQuery == null ? "" : encodedQuery;
        if (str.length() > 8192) {
            zzkr().zza(zzanx, "Hit length exceeds the maximum allowed size");
            return;
        }
        int intValue = ((Integer) zzans.zzahj.get()).intValue();
        long zzkN = zzkN();
        if (zzkN > ((long) (intValue - 1))) {
            List zzn = zzn((zzkN - ((long) intValue)) + 1);
            zzd("Store full, deleting hits to make room, count", Integer.valueOf(zzn.size()));
            zzs(zzn);
        }
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hit_string", str);
        contentValues.put("hit_time", Long.valueOf(zzanx.zzlG()));
        contentValues.put("hit_app_id", Integer.valueOf(zzanx.zzlE()));
        contentValues.put("hit_url", zzanx.zzlI() ? zzank.zzlu() : zzank.zzlv());
        try {
            long insert = writableDatabase.insert("hits2", null, contentValues);
            if (insert == -1) {
                zzbs("Failed to insert a hit (got -1)");
            } else {
                zzb("Hit saved to database. db-id, hit", Long.valueOf(insert), zzanx);
            }
        } catch (SQLiteException e) {
            zze("Error storing a hit", e);
        }
    }

    protected final void zzjD() {
    }

    public final int zzkS() {
        zzl.zzjC();
        zzkD();
        if (!this.zzags.zzu(86400000)) {
            return 0;
        }
        this.zzags.start();
        zzbo("Deleting stale hits (if any)");
        int delete = getWritableDatabase().delete("hits2", "hit_time < ?", new String[]{Long.toString(zzkq().currentTimeMillis() - 2592000000L)});
        zza("Deleted stale hits, count", Integer.valueOf(delete));
        return delete;
    }

    public final long zzkT() {
        zzl.zzjC();
        zzkD();
        return zza$6e791f8(zzagq, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.util.List<com.google.android.gms.internal.zzanx> zzo(long r14) {
        /*
        r13 = this;
        r0 = 1;
        r1 = 0;
        r9 = 0;
        r2 = 0;
        r2 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x008f;
    L_0x0009:
        com.google.android.gms.common.internal.zzbo.zzaf(r0);
        com.google.android.gms.analytics.zzl.zzjC();
        r13.zzkD();
        r0 = r13.getWritableDatabase();
        r1 = "hits2";
        r2 = 5;
        r2 = new java.lang.String[r2];	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r3 = 0;
        r4 = "hit_id";
        r2[r3] = r4;	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r3 = 1;
        r4 = "hit_time";
        r2[r3] = r4;	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r3 = 2;
        r4 = "hit_string";
        r2[r3] = r4;	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r3 = 3;
        r4 = "hit_url";
        r2[r3] = r4;	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r3 = 4;
        r4 = "hit_app_id";
        r2[r3] = r4;	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r7 = "%s ASC";
        r8 = 1;
        r8 = new java.lang.Object[r8];	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r10 = 0;
        r11 = "hit_id";
        r8[r10] = r11;	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r7 = java.lang.String.format(r7, r8);	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r8 = java.lang.Long.toString(r14);	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r9 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ SQLiteException -> 0x0092, all -> 0x00a2 }
        r10 = new java.util.ArrayList;	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r10.<init>();	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r0 = r9.moveToFirst();	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        if (r0 == 0) goto L_0x0089;
    L_0x0059:
        r0 = 0;
        r6 = r9.getLong(r0);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r0 = 1;
        r3 = r9.getLong(r0);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r0 = 2;
        r0 = r9.getString(r0);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r1 = 3;
        r1 = r9.getString(r1);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r2 = 4;
        r8 = r9.getInt(r2);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r2 = r13.zzbt(r0);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r5 = com.google.android.gms.internal.zzaos.zzbF(r1);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r0 = new com.google.android.gms.internal.zzanx;	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r1 = r13;
        r0.<init>(r1, r2, r3, r5, r6, r8);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r10.add(r0);	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        r0 = r9.moveToNext();	 Catch:{ SQLiteException -> 0x00a4, all -> 0x00a2 }
        if (r0 != 0) goto L_0x0059;
    L_0x0089:
        if (r9 == 0) goto L_0x008e;
    L_0x008b:
        r9.close();
    L_0x008e:
        return r10;
    L_0x008f:
        r0 = r1;
        goto L_0x0009;
    L_0x0092:
        r0 = move-exception;
        r1 = r9;
    L_0x0094:
        r2 = "Error loading hits from the database";
        r13.zze(r2, r0);	 Catch:{ all -> 0x009a }
        throw r0;	 Catch:{ all -> 0x009a }
    L_0x009a:
        r0 = move-exception;
        r9 = r1;
    L_0x009c:
        if (r9 == 0) goto L_0x00a1;
    L_0x009e:
        r9.close();
    L_0x00a1:
        throw r0;
    L_0x00a2:
        r0 = move-exception;
        goto L_0x009c;
    L_0x00a4:
        r0 = move-exception;
        r1 = r9;
        goto L_0x0094;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzams.zzo(long):java.util.List<com.google.android.gms.internal.zzanx>");
    }

    public final void zzp(long j) {
        zzl.zzjC();
        zzkD();
        List arrayList = new ArrayList(1);
        arrayList.add(Long.valueOf(j));
        zza("Deleting hit, id", Long.valueOf(j));
        zzs(arrayList);
    }

    public final List<zzamm> zzq$187a7388() {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzkD();
        zzl.zzjC();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            String[] strArr = new String[]{"cid", "tid", "adid", "hits_count", "params"};
            int intValue = ((Integer) zzans.zzahl.get()).intValue();
            String str = "properties";
            Cursor query = writableDatabase.query(str, strArr, "app_uid=?", new String[]{"0"}, null, null, null, String.valueOf(intValue));
            try {
                List<zzamm> arrayList = new ArrayList();
                if (query.moveToFirst()) {
                    do {
                        Object string = query.getString(0);
                        Object string2 = query.getString(1);
                        boolean z = query.getInt(2) != 0;
                        long j = (long) query.getInt(3);
                        Map zzbu = zzbu(query.getString(4));
                        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
                            zzc("Read property with empty client id or tracker id", string, string2);
                        } else {
                            arrayList.add(new zzamm(string, string2, z, j, zzbu));
                        }
                    } while (query.moveToNext());
                }
                if (arrayList.size() >= intValue) {
                    zzbr("Sending hits to too many properties. Campaign report might be incorrect");
                }
                if (query != null) {
                    query.close();
                }
                return arrayList;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
            } catch (Throwable th2) {
                th = th2;
                cursor2 = query;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            try {
                zze("Error loading hits from the database", e);
                throw e;
            } catch (Throwable th3) {
                th = th3;
                cursor2 = cursor;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    public final void zzs(List<Long> list) {
        zzbo.zzu(list);
        zzl.zzjC();
        zzkD();
        if (!list.isEmpty()) {
            int i;
            StringBuilder stringBuilder = new StringBuilder("hit_id");
            stringBuilder.append(" in (");
            for (i = 0; i < list.size(); i++) {
                Long l = (Long) list.get(i);
                if (l == null || l.longValue() == 0) {
                    throw new SQLiteException("Invalid hit id");
                }
                if (i > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(l);
            }
            stringBuilder.append(")");
            String stringBuilder2 = stringBuilder.toString();
            try {
                SQLiteDatabase writableDatabase = getWritableDatabase();
                zza("Deleting dispatched hits. count", Integer.valueOf(list.size()));
                i = writableDatabase.delete("hits2", stringBuilder2, null);
                if (i != list.size()) {
                    zzb("Deleted fewer hits then expected", Integer.valueOf(list.size()), Integer.valueOf(i), stringBuilder2);
                }
            } catch (SQLiteException e) {
                zze("Error deleting hits", e);
                throw e;
            }
        }
    }
}
