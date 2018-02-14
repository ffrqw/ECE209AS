package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import com.google.android.gms.common.util.zze;

public final class zzcfh extends zzchj {
    private final zzcfi zzbqF = new zzcfi(this, super.getContext(), zzcem.zzxD());
    private boolean zzbqG;

    zzcfh(zzcgl zzcgl) {
        super(zzcgl);
    }

    private final SQLiteDatabase getWritableDatabase() {
        if (this.zzbqG) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.zzbqF.getWritableDatabase();
        if (writableDatabase != null) {
            return writableDatabase;
        }
        this.zzbqG = true;
        return null;
    }

    @TargetApi(11)
    private final boolean zza(int i, byte[] bArr) {
        super.zzwp();
        super.zzjC();
        if (this.zzbqG) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(i));
        contentValues.put("entry", bArr);
        zzcem.zzxN();
        int i2 = 0;
        int i3 = 5;
        while (i2 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            Cursor cursor = null;
            try {
                sQLiteDatabase = getWritableDatabase();
                if (sQLiteDatabase == null) {
                    this.zzbqG = true;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.close();
                    }
                    return false;
                }
                sQLiteDatabase.beginTransaction();
                long j = 0;
                cursor = sQLiteDatabase.rawQuery("select count(1) from messages", null);
                if (cursor != null && cursor.moveToFirst()) {
                    j = cursor.getLong(0);
                }
                if (j >= 100000) {
                    super.zzwF().zzyx().log("Data loss, local db full");
                    j = (100000 - j) + 1;
                    long delete = (long) sQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[]{Long.toString(j)});
                    if (delete != j) {
                        super.zzwF().zzyx().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(j), Long.valueOf(delete), Long.valueOf(j - delete));
                    }
                }
                sQLiteDatabase.insertOrThrow("messages", null, contentValues);
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                if (cursor != null) {
                    cursor.close();
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                return true;
            } catch (SQLiteFullException e) {
                super.zzwF().zzyx().zzj("Error writing entry to local database", e);
                this.zzbqG = true;
                if (cursor != null) {
                    cursor.close();
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i2++;
            } catch (SQLiteException e2) {
                if (VERSION.SDK_INT < 11 || !(e2 instanceof SQLiteDatabaseLockedException)) {
                    if (sQLiteDatabase != null) {
                        if (sQLiteDatabase.inTransaction()) {
                            sQLiteDatabase.endTransaction();
                        }
                    }
                    super.zzwF().zzyx().zzj("Error writing entry to local database", e2);
                    this.zzbqG = true;
                } else {
                    SystemClock.sleep((long) i3);
                    i3 += 20;
                }
                if (cursor != null) {
                    cursor.close();
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i2++;
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
            }
        }
        super.zzwF().zzyz().log("Failed to write entry to local database");
        return false;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final boolean zza(zzcez zzcez) {
        Parcel obtain = Parcel.obtain();
        zzcez.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(0, marshall);
        }
        super.zzwF().zzyz().log("Event is too long for local database. Sending event directly to service");
        return false;
    }

    public final boolean zza(zzcji zzcji) {
        Parcel obtain = Parcel.obtain();
        zzcji.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(1, marshall);
        }
        super.zzwF().zzyz().log("User property too long for local database. Sending directly to service");
        return false;
    }

    @android.annotation.TargetApi(11)
    public final java.util.List<com.google.android.gms.common.internal.safeparcel.zza> zzbp$22f3aa59() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:151:0x00f7
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.modifyBlocksTree(BlockProcessor.java:248)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r14 = this;
        super.zzjC();
        super.zzwp();
        r0 = r14.zzbqG;
        if (r0 == 0) goto L_0x000c;
    L_0x000a:
        r0 = 0;
    L_0x000b:
        return r0;
    L_0x000c:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r0 = super.getContext();
        r1 = com.google.android.gms.internal.zzcem.zzxD();
        r0 = r0.getDatabasePath(r1);
        r0 = r0.exists();
        if (r0 != 0) goto L_0x0025;
    L_0x0023:
        r0 = r10;
        goto L_0x000b;
    L_0x0025:
        r9 = 5;
        r0 = 0;
        r12 = r0;
    L_0x0028:
        r0 = 5;
        if (r12 >= r0) goto L_0x01e1;
    L_0x002b:
        r1 = 0;
        r11 = 0;
        r0 = r14.getWritableDatabase();	 Catch:{ SQLiteFullException -> 0x0210, SQLiteException -> 0x0205, all -> 0x01f1 }
        if (r0 != 0) goto L_0x003d;
    L_0x0033:
        r1 = 1;
        r14.zzbqG = r1;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        if (r0 == 0) goto L_0x003b;
    L_0x0038:
        r0.close();
    L_0x003b:
        r0 = 0;
        goto L_0x000b;
    L_0x003d:
        r0.beginTransaction();	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r1 = "messages";	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r2 = 3;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r2 = new java.lang.String[r2];	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r3 = 0;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r4 = "rowid";	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r3 = 1;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r4 = "type";	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r4 = "entry";	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r3 = 0;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r4 = 0;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r5 = 0;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r6 = 0;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r7 = "rowid asc";	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r8 = 100;	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r8 = java.lang.Integer.toString(r8);	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r2 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ SQLiteFullException -> 0x0215, SQLiteException -> 0x0209, all -> 0x01f5 }
        r4 = -1;
    L_0x0066:
        r1 = r2.moveToNext();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        if (r1 == 0) goto L_0x018b;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x006c:
        r1 = 0;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r4 = r2.getLong(r1);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1 = 1;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1 = r2.getInt(r1);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r6 = r2.getBlob(r3);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        if (r1 != 0) goto L_0x0106;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x007d:
        r3 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1 = 0;
        r7 = r6.length;	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r3.unmarshall(r6, r1, r7);	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r1 = 0;	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r3.setDataPosition(r1);	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r1 = com.google.android.gms.internal.zzcez.CREATOR;	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r1 = r1.createFromParcel(r3);	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r1 = (com.google.android.gms.internal.zzcez) r1;	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r3.recycle();
        if (r1 == 0) goto L_0x0066;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x0097:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        goto L_0x0066;
    L_0x009b:
        r1 = move-exception;
        r13 = r1;
        r1 = r2;
        r2 = r0;
        r0 = r13;
    L_0x00a0:
        r3 = super.zzwF();	 Catch:{ all -> 0x01fc }
        r3 = r3.zzyx();	 Catch:{ all -> 0x01fc }
        r4 = "Error reading entries from local database";	 Catch:{ all -> 0x01fc }
        r3.zzj(r4, r0);	 Catch:{ all -> 0x01fc }
        r0 = 1;	 Catch:{ all -> 0x01fc }
        r14.zzbqG = r0;	 Catch:{ all -> 0x01fc }
        if (r1 == 0) goto L_0x00b5;
    L_0x00b2:
        r1.close();
    L_0x00b5:
        if (r2 == 0) goto L_0x021b;
    L_0x00b7:
        r2.close();
        r0 = r9;
    L_0x00bb:
        r1 = r12 + 1;
        r12 = r1;
        r9 = r0;
        goto L_0x0028;
    L_0x00c1:
        r1 = move-exception;
        r1 = super.zzwF();	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r1 = r1.zzyx();	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r6 = "Failed to load event from local database";	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r1.log(r6);	 Catch:{ zzc -> 0x00c1, all -> 0x00f2 }
        r3.recycle();
        goto L_0x0066;
    L_0x00d3:
        r1 = move-exception;
        r13 = r1;
        r1 = r0;
        r0 = r13;
    L_0x00d7:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0202 }
        r4 = 11;	 Catch:{ all -> 0x0202 }
        if (r3 < r4) goto L_0x01c3;	 Catch:{ all -> 0x0202 }
    L_0x00dd:
        r3 = r0 instanceof android.database.sqlite.SQLiteDatabaseLockedException;	 Catch:{ all -> 0x0202 }
        if (r3 == 0) goto L_0x01c3;	 Catch:{ all -> 0x0202 }
    L_0x00e1:
        r4 = (long) r9;	 Catch:{ all -> 0x0202 }
        android.os.SystemClock.sleep(r4);	 Catch:{ all -> 0x0202 }
        r0 = r9 + 20;
    L_0x00e7:
        if (r2 == 0) goto L_0x00ec;
    L_0x00e9:
        r2.close();
    L_0x00ec:
        if (r1 == 0) goto L_0x00bb;
    L_0x00ee:
        r1.close();
        goto L_0x00bb;
    L_0x00f2:
        r1 = move-exception;
        r3.recycle();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        throw r1;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x00f7:
        r1 = move-exception;
        r13 = r1;
        r1 = r0;
        r0 = r13;
    L_0x00fb:
        if (r2 == 0) goto L_0x0100;
    L_0x00fd:
        r2.close();
    L_0x0100:
        if (r1 == 0) goto L_0x0105;
    L_0x0102:
        r1.close();
    L_0x0105:
        throw r0;
    L_0x0106:
        r3 = 1;
        if (r1 != r3) goto L_0x0141;
    L_0x0109:
        r7 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r3 = 0;
        r1 = 0;
        r8 = r6.length;	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r7.unmarshall(r6, r1, r8);	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r1 = 0;	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r7.setDataPosition(r1);	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r1 = com.google.android.gms.internal.zzcji.CREATOR;	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r1 = r1.createFromParcel(r7);	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r1 = (com.google.android.gms.internal.zzcji) r1;	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r7.recycle();
    L_0x0122:
        if (r1 == 0) goto L_0x0066;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x0124:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        goto L_0x0066;
    L_0x0129:
        r1 = move-exception;
        r1 = super.zzwF();	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r1 = r1.zzyx();	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r6 = "Failed to load user property from local database";	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r1.log(r6);	 Catch:{ zzc -> 0x0129, all -> 0x013c }
        r7.recycle();
        r1 = r3;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        goto L_0x0122;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x013c:
        r1 = move-exception;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        throw r1;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x0141:
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        if (r1 != r3) goto L_0x017c;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x0144:
        r7 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r3 = 0;
        r1 = 0;
        r8 = r6.length;	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r7.unmarshall(r6, r1, r8);	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r1 = 0;	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r7.setDataPosition(r1);	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r1 = com.google.android.gms.internal.zzcek.CREATOR;	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r1 = r1.createFromParcel(r7);	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r1 = (com.google.android.gms.internal.zzcek) r1;	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r7.recycle();
    L_0x015d:
        if (r1 == 0) goto L_0x0066;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x015f:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        goto L_0x0066;
    L_0x0164:
        r1 = move-exception;
        r1 = super.zzwF();	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r1 = r1.zzyx();	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r6 = "Failed to load user property from local database";	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r1.log(r6);	 Catch:{ zzc -> 0x0164, all -> 0x0177 }
        r7.recycle();
        r1 = r3;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        goto L_0x015d;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x0177:
        r1 = move-exception;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        throw r1;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x017c:
        r1 = super.zzwF();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1 = r1.zzyx();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r3 = "Unknown record type in local database";	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1.log(r3);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        goto L_0x0066;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x018b:
        r1 = "messages";	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r3 = "rowid <= ?";	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r6 = 1;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r6 = new java.lang.String[r6];	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r7 = 0;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r4 = java.lang.Long.toString(r4);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r6[r7] = r4;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1 = r0.delete(r1, r3, r6);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r3 = r10.size();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        if (r1 >= r3) goto L_0x01b0;	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x01a3:
        r1 = super.zzwF();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1 = r1.zzyx();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r3 = "Fewer entries removed from local database than expected";	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r1.log(r3);	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
    L_0x01b0:
        r0.setTransactionSuccessful();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        r0.endTransaction();	 Catch:{ SQLiteFullException -> 0x009b, SQLiteException -> 0x00d3, all -> 0x00f7 }
        if (r2 == 0) goto L_0x01bb;
    L_0x01b8:
        r2.close();
    L_0x01bb:
        if (r0 == 0) goto L_0x01c0;
    L_0x01bd:
        r0.close();
    L_0x01c0:
        r0 = r10;
        goto L_0x000b;
    L_0x01c3:
        if (r1 == 0) goto L_0x01ce;
    L_0x01c5:
        r3 = r1.inTransaction();	 Catch:{ all -> 0x0202 }
        if (r3 == 0) goto L_0x01ce;	 Catch:{ all -> 0x0202 }
    L_0x01cb:
        r1.endTransaction();	 Catch:{ all -> 0x0202 }
    L_0x01ce:
        r3 = super.zzwF();	 Catch:{ all -> 0x0202 }
        r3 = r3.zzyx();	 Catch:{ all -> 0x0202 }
        r4 = "Error reading entries from local database";	 Catch:{ all -> 0x0202 }
        r3.zzj(r4, r0);	 Catch:{ all -> 0x0202 }
        r0 = 1;	 Catch:{ all -> 0x0202 }
        r14.zzbqG = r0;	 Catch:{ all -> 0x0202 }
        r0 = r9;
        goto L_0x00e7;
    L_0x01e1:
        r0 = super.zzwF();
        r0 = r0.zzyz();
        r1 = "Failed to read events from database in reasonable time";
        r0.log(r1);
        r0 = 0;
        goto L_0x000b;
    L_0x01f1:
        r0 = move-exception;
        r2 = r11;
        goto L_0x00fb;
    L_0x01f5:
        r1 = move-exception;
        r2 = r11;
        r13 = r1;
        r1 = r0;
        r0 = r13;
        goto L_0x00fb;
    L_0x01fc:
        r0 = move-exception;
        r13 = r1;
        r1 = r2;
        r2 = r13;
        goto L_0x00fb;
    L_0x0202:
        r0 = move-exception;
        goto L_0x00fb;
    L_0x0205:
        r0 = move-exception;
        r2 = r11;
        goto L_0x00d7;
    L_0x0209:
        r1 = move-exception;
        r2 = r11;
        r13 = r1;
        r1 = r0;
        r0 = r13;
        goto L_0x00d7;
    L_0x0210:
        r0 = move-exception;
        r2 = r1;
        r1 = r11;
        goto L_0x00a0;
    L_0x0215:
        r1 = move-exception;
        r2 = r0;
        r0 = r1;
        r1 = r11;
        goto L_0x00a0;
    L_0x021b:
        r0 = r9;
        goto L_0x00bb;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcfh.zzbp$22f3aa59():java.util.List<com.google.android.gms.common.internal.safeparcel.zza>");
    }

    public final boolean zzc(zzcek zzcek) {
        super.zzwB();
        byte[] zza = zzcjl.zza((Parcelable) zzcek);
        if (zza.length <= 131072) {
            return zza(2, zza);
        }
        super.zzwF().zzyz().log("Conditional user property too long for local database. Sending directly to service");
        return false;
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }
}
