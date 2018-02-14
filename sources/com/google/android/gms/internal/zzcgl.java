package com.google.android.gms.internal;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri.Builder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class zzcgl {
    private static volatile zzcgl zzbsm;
    private final Context mContext;
    private final boolean zzafK;
    private final zzchz zzbsA;
    private final zzcid zzbsB;
    private final zzcet zzbsC;
    private final zzchl zzbsD;
    private final zzcfg zzbsE;
    private final zzcfu zzbsF;
    private final zzcjg zzbsG;
    private final zzcej zzbsH;
    private final zzcec zzbsI;
    private boolean zzbsJ;
    private Boolean zzbsK;
    private long zzbsL;
    private FileLock zzbsM;
    private FileChannel zzbsN;
    private List<Long> zzbsO;
    private List<Runnable> zzbsP;
    private int zzbsQ;
    private int zzbsR;
    private long zzbsS = -1;
    private long zzbsT;
    private boolean zzbsU;
    private boolean zzbsV;
    private boolean zzbsW;
    private final long zzbsX = this.zzvw.currentTimeMillis();
    private final zzcem zzbsn = new zzcem(this);
    private final zzcfw zzbso;
    private final zzcfl zzbsp;
    private final zzcgg zzbsq;
    private final zzcja zzbsr;
    private final zzcgf zzbss;
    private final AppMeasurement zzbst;
    private final FirebaseAnalytics zzbsu;
    private final zzcjl zzbsv;
    private final zzcfj zzbsw;
    private final zzcen zzbsx;
    private final zzcfh zzbsy;
    private final zzcfp zzbsz;
    private final zze zzvw = zzi.zzrY();

    class zza implements zzcep {
        private /* synthetic */ zzcgl zzbsY;
        zzcjz zzbsZ;
        List<Long> zzbta;
        private long zzbtb;
        List<zzcjw> zztH;

        private zza(zzcgl zzcgl) {
            this.zzbsY = zzcgl;
        }

        private static long zza(zzcjw zzcjw) {
            return ((zzcjw.zzbvx.longValue() / 1000) / 60) / 60;
        }

        public final boolean zza(long j, zzcjw zzcjw) {
            zzbo.zzu(zzcjw);
            if (this.zztH == null) {
                this.zztH = new ArrayList();
            }
            if (this.zzbta == null) {
                this.zzbta = new ArrayList();
            }
            if (this.zztH.size() > 0 && zza((zzcjw) this.zztH.get(0)) != zza(zzcjw)) {
                return false;
            }
            long zzLW = this.zzbtb + ((long) zzcjw.zzLW());
            if (zzLW >= ((long) zzcem.zzxL())) {
                return false;
            }
            this.zzbtb = zzLW;
            this.zztH.add(zzcjw);
            this.zzbta.add(Long.valueOf(j));
            return this.zztH.size() < zzcem.zzxM();
        }

        public final void zzb(zzcjz zzcjz) {
            zzbo.zzu(zzcjz);
            this.zzbsZ = zzcjz;
        }
    }

    private zzcgl(zzchk zzchk) {
        zzcfn zzyB;
        zzbo.zzu(zzchk);
        this.mContext = zzchk.mContext;
        zzcfw zzcfw = new zzcfw(this);
        zzcfw.initialize();
        this.zzbso = zzcfw;
        zzcfl zzcfl = new zzcfl(this);
        zzcfl.initialize();
        this.zzbsp = zzcfl;
        zzwF().zzyB().zzj("App measurement is starting up, version", Long.valueOf(zzcem.zzwP()));
        zzcem.zzxE();
        zzwF().zzyB().log("To enable debug logging run: adb shell setprop log.tag.FA VERBOSE");
        zzcjl zzcjl = new zzcjl(this);
        zzcjl.initialize();
        this.zzbsv = zzcjl;
        zzcfj zzcfj = new zzcfj(this);
        zzcfj.initialize();
        this.zzbsw = zzcfj;
        zzcet zzcet = new zzcet(this);
        zzcet.initialize();
        this.zzbsC = zzcet;
        zzcfg zzcfg = new zzcfg(this);
        zzcfg.initialize();
        this.zzbsE = zzcfg;
        zzcem.zzxE();
        String zzhl = zzcfg.zzhl();
        if (zzwB().zzey(zzhl)) {
            zzyB = zzwF().zzyB();
            zzhl = "Faster debug mode event logging enabled. To disable, run:\n  adb shell setprop debug.firebase.analytics.app .none.";
        } else {
            zzyB = zzwF().zzyB();
            String str = "To enable faster debug mode event logging run:\n  adb shell setprop debug.firebase.analytics.app ";
            zzhl = String.valueOf(zzhl);
            zzhl = zzhl.length() != 0 ? str.concat(zzhl) : new String(str);
        }
        zzyB.log(zzhl);
        zzwF().zzyC().log("Debug-level message logging enabled");
        zzcen zzcen = new zzcen(this);
        zzcen.initialize();
        this.zzbsx = zzcen;
        zzcfh zzcfh = new zzcfh(this);
        zzcfh.initialize();
        this.zzbsy = zzcfh;
        zzcej zzcej = new zzcej(this);
        zzcej.initialize();
        this.zzbsH = zzcej;
        this.zzbsI = new zzcec(this);
        zzcfp zzcfp = new zzcfp(this);
        zzcfp.initialize();
        this.zzbsz = zzcfp;
        zzchz zzchz = new zzchz(this);
        zzchz.initialize();
        this.zzbsA = zzchz;
        zzcid zzcid = new zzcid(this);
        zzcid.initialize();
        this.zzbsB = zzcid;
        zzchl zzchl = new zzchl(this);
        zzchl.initialize();
        this.zzbsD = zzchl;
        zzcjg zzcjg = new zzcjg(this);
        zzcjg.initialize();
        this.zzbsG = zzcjg;
        this.zzbsF = new zzcfu(this);
        this.zzbst = new AppMeasurement(this);
        this.zzbsu = new FirebaseAnalytics(this);
        zzcja zzcja = new zzcja(this);
        zzcja.initialize();
        this.zzbsr = zzcja;
        zzcgf zzcgf = new zzcgf(this);
        zzcgf.initialize();
        this.zzbss = zzcgf;
        zzcgg zzcgg = new zzcgg(this);
        zzcgg.initialize();
        this.zzbsq = zzcgg;
        if (this.zzbsQ != this.zzbsR) {
            zzwF().zzyx().zze("Not all components initialized", Integer.valueOf(this.zzbsQ), Integer.valueOf(this.zzbsR));
        }
        this.zzafK = true;
        zzcem.zzxE();
        if (this.mContext.getApplicationContext() instanceof Application) {
            zzchl zzwt = zzwt();
            if (zzwt.getContext().getApplicationContext() instanceof Application) {
                Application application = (Application) zzwt.getContext().getApplicationContext();
                if (zzwt.zzbto == null) {
                    zzwt.zzbto = new zzchy(zzwt);
                }
                application.unregisterActivityLifecycleCallbacks(zzwt.zzbto);
                application.registerActivityLifecycleCallbacks(zzwt.zzbto);
                zzwt.zzwF().zzyD().log("Registered activity lifecycle callback");
            }
        } else {
            zzwF().zzyz().log("Application context is not an Application");
        }
        this.zzbsq.zzj(new zzcgm(this));
    }

    private final int zza(FileChannel fileChannel) {
        int i = 0;
        zzwE().zzjC();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzwF().zzyx().log("Bad chanel to read from");
        } else {
            ByteBuffer allocate = ByteBuffer.allocate(4);
            try {
                fileChannel.position(0);
                int read = fileChannel.read(allocate);
                if (read == 4) {
                    allocate.flip();
                    i = allocate.getInt();
                } else if (read != -1) {
                    zzwF().zzyz().zzj("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
            } catch (IOException e) {
                zzwF().zzyx().zzj("Failed to read from channel", e);
            }
        }
        return i;
    }

    private final void zza(zzceu zzceu, zzceh zzceh) {
        zzwE().zzjC();
        zzbo.zzu(zzceu);
        zzbo.zzu(zzceh);
        zzbo.zzcF(zzceu.mAppId);
        zzbo.zzaf(zzceu.mAppId.equals(zzceh.packageName));
        zzcjz zzcjz = new zzcjz();
        zzcjz.zzbvD = Integer.valueOf(1);
        zzcjz.zzbvL = "android";
        zzcjz.zzaH = zzceh.packageName;
        zzcjz.zzboR = zzceh.zzboR;
        zzcjz.zzbgW = zzceh.zzbgW;
        zzcjz.zzbvY = zzceh.zzboX == -2147483648L ? null : Integer.valueOf((int) zzceh.zzboX);
        zzcjz.zzbvP = Long.valueOf(zzceh.zzboS);
        zzcjz.zzboQ = zzceh.zzboQ;
        zzcjz.zzbvU = zzceh.zzboT == 0 ? null : Long.valueOf(zzceh.zzboT);
        Pair zzeb = zzwG().zzeb(zzceh.packageName);
        if (!TextUtils.isEmpty((CharSequence) zzeb.first)) {
            zzcjz.zzbvR = (String) zzeb.first;
            zzcjz.zzbvS = (Boolean) zzeb.second;
        }
        zzwv().zzkD();
        zzcjz.zzbvM = Build.MODEL;
        zzwv().zzkD();
        zzcjz.zzaY = VERSION.RELEASE;
        zzcjz.zzbvO = Integer.valueOf((int) zzwv().zzyq());
        zzcjz.zzbvN = zzwv().zzyr();
        zzcjz.zzbvQ = null;
        zzcjz.zzbvG = null;
        zzcjz.zzbvH = null;
        zzcjz.zzbvI = null;
        zzcjz.zzbwc = Long.valueOf(zzceh.zzboZ);
        if (isEnabled() && zzcem.zzyb()) {
            zzwu();
            zzcjz.zzbwd = null;
        }
        zzceg zzdQ = zzwz().zzdQ(zzceh.packageName);
        if (zzdQ == null) {
            zzdQ = new zzceg(this, zzceh.packageName);
            zzdQ.zzdG(zzwu().zzyu());
            zzdQ.zzdJ(zzceh.zzboY);
            zzdQ.zzdH(zzceh.zzboQ);
            zzdQ.zzdI(zzwG().zzec(zzceh.packageName));
            zzdQ.zzQ(0);
            zzdQ.zzL(0);
            zzdQ.zzM(0);
            zzdQ.setAppVersion(zzceh.zzbgW);
            zzdQ.zzN(zzceh.zzboX);
            zzdQ.zzdK(zzceh.zzboR);
            zzdQ.zzO(zzceh.zzboS);
            zzdQ.zzP(zzceh.zzboT);
            zzdQ.setMeasurementEnabled(zzceh.zzboV);
            zzdQ.zzZ(zzceh.zzboZ);
            zzwz().zza(zzdQ);
        }
        zzcjz.zzbvT = zzdQ.getAppInstanceId();
        zzcjz.zzboY = zzdQ.zzwK();
        List zzdP = zzwz().zzdP(zzceh.packageName);
        zzcjz.zzbvF = new zzckb[zzdP.size()];
        for (int i = 0; i < zzdP.size(); i++) {
            zzckb zzckb = new zzckb();
            zzcjz.zzbvF[i] = zzckb;
            zzckb.name = ((zzcjk) zzdP.get(i)).mName;
            zzckb.zzbwh = Long.valueOf(((zzcjk) zzdP.get(i)).zzbuC);
            zzwB().zza(zzckb, ((zzcjk) zzdP.get(i)).mValue);
        }
        try {
            boolean z;
            long zza = zzwz().zza(zzcjz);
            zzcen zzwz = zzwz();
            if (zzceu.zzbpF != null) {
                Iterator it = zzceu.zzbpF.iterator();
                while (it.hasNext()) {
                    if ("_r".equals((String) it.next())) {
                        z = true;
                        break;
                    }
                }
                z = zzwC().zzO(zzceu.mAppId, zzceu.mName);
                zzceo zza2 = zzwz().zza(zzyZ(), zzceu.mAppId, false, false, false, false, false);
                if (z && zza2.zzbpy < ((long) this.zzbsn.zzdM(zzceu.mAppId))) {
                    z = true;
                    if (zzwz.zza(zzceu, zza, z)) {
                        this.zzbsT = 0;
                    }
                }
            }
            z = false;
            if (zzwz.zza(zzceu, zza, z)) {
                this.zzbsT = 0;
            }
        } catch (IOException e) {
            zzwF().zzyx().zze("Data loss. Failed to insert raw event metadata. appId", zzcfl.zzdZ(zzcjz.zzaH), e);
        }
    }

    private static void zza(zzchi zzchi) {
        if (zzchi == null) {
            throw new IllegalStateException("Component not created");
        }
    }

    private static void zza(zzchj zzchj) {
        if (zzchj == null) {
            throw new IllegalStateException("Component not created");
        } else if (!zzchj.isInitialized()) {
            throw new IllegalStateException("Component not initialized");
        }
    }

    private final boolean zza(int i, FileChannel fileChannel) {
        zzwE().zzjC();
        if (fileChannel == null || !fileChannel.isOpen()) {
            zzwF().zzyx().log("Bad chanel to read from");
            return false;
        }
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt(i);
        allocate.flip();
        try {
            fileChannel.truncate(0);
            fileChannel.write(allocate);
            fileChannel.force(true);
            if (fileChannel.size() == 4) {
                return true;
            }
            zzwF().zzyx().zzj("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            return true;
        } catch (IOException e) {
            zzwF().zzyx().zzj("Failed to write to channel", e);
            return false;
        }
    }

    private final zzcjv[] zza(String str, zzckb[] zzckbArr, zzcjw[] zzcjwArr) {
        zzbo.zzcF(str);
        return zzws().zza(str, zzcjwArr, zzckbArr);
    }

    private final void zzb(zzceg zzceg) {
        Map map = null;
        zzwE().zzjC();
        if (TextUtils.isEmpty(zzceg.getGmpAppId())) {
            zzb(zzceg.zzhl(), 204, null, null, null);
            return;
        }
        String gmpAppId = zzceg.getGmpAppId();
        String appInstanceId = zzceg.getAppInstanceId();
        Builder builder = new Builder();
        Builder encodedAuthority = builder.scheme((String) zzcfb.zzbpZ.get()).encodedAuthority((String) zzcfb.zzbqa.get());
        String str = "config/app/";
        String valueOf = String.valueOf(gmpAppId);
        encodedAuthority.path(valueOf.length() != 0 ? str.concat(valueOf) : new String(str)).appendQueryParameter("app_instance_id", appInstanceId).appendQueryParameter("platform", "android").appendQueryParameter("gmp_version", "11020");
        valueOf = builder.build().toString();
        try {
            URL url = new URL(valueOf);
            zzwF().zzyD().zzj("Fetching remote configuration", zzceg.zzhl());
            zzcjt zzeh = zzwC().zzeh(zzceg.zzhl());
            CharSequence zzei = zzwC().zzei(zzceg.zzhl());
            if (!(zzeh == null || TextUtils.isEmpty(zzei))) {
                map = new ArrayMap();
                map.put("If-Modified-Since", zzei);
            }
            this.zzbsU = true;
            zzyU().zza(zzceg.zzhl(), url, map, new zzcgp(this));
        } catch (MalformedURLException e) {
            zzwF().zzyx().zze("Failed to parse config URL. Not fetching. appId", zzcfl.zzdZ(zzceg.zzhl()), valueOf);
        }
    }

    public static zzcgl zzbj(Context context) {
        zzbo.zzu(context);
        zzbo.zzu(context.getApplicationContext());
        if (zzbsm == null) {
            synchronized (zzcgl.class) {
                if (zzbsm == null) {
                    zzbsm = new zzcgl(new zzchk(context));
                }
            }
        }
        return zzbsm;
    }

    private final void zzc(zzcez zzcez, zzceh zzceh) {
        zzcen zzwz;
        zzbo.zzu(zzceh);
        zzbo.zzcF(zzceh.packageName);
        long nanoTime = System.nanoTime();
        zzwE().zzjC();
        String str = zzceh.packageName;
        zzwB();
        if (!zzcjl.zzd(zzcez, zzceh)) {
            return;
        }
        if (!zzceh.zzboV) {
            zzf(zzceh);
        } else if (zzwC().zzN(str, zzcez.name)) {
            zzwF().zzyz().zze("Dropping blacklisted event. appId", zzcfl.zzdZ(str), zzwA().zzdW(zzcez.name));
            r2 = (zzwB().zzeA(str) || zzwB().zzeB(str)) ? 1 : null;
            if (r2 == null && !"_err".equals(zzcez.name)) {
                zzwB().zza$2c2ba1f5(11, "_ev", zzcez.name, 0);
            }
            if (r2 != null) {
                zzceg zzdQ = zzwz().zzdQ(str);
                if (zzdQ != null) {
                    if (Math.abs(this.zzvw.currentTimeMillis() - Math.max(zzdQ.zzwU(), zzdQ.zzwT())) > zzcem.zzxI()) {
                        zzwF().zzyC().log("Fetching config for blacklisted app");
                        zzb(zzdQ);
                    }
                }
            }
        } else {
            Bundle zzyt;
            if (zzwF().zzz(2)) {
                zzwF().zzyD().zzj("Logging event", zzwA().zzb(zzcez));
            }
            zzwz().beginTransaction();
            try {
                zzyt = zzcez.zzbpM.zzyt();
                zzf(zzceh);
                if ("_iap".equals(zzcez.name) || "ecommerce_purchase".equals(zzcez.name)) {
                    long round;
                    r2 = zzyt.getString("currency");
                    if ("ecommerce_purchase".equals(zzcez.name)) {
                        double d = zzyt.getDouble("value") * 1000000.0d;
                        if (d == 0.0d) {
                            d = ((double) zzyt.getLong("value")) * 1000000.0d;
                        }
                        if (d > 9.223372036854776E18d || d < -9.223372036854776E18d) {
                            zzwF().zzyz().zze("Data lost. Currency value is too big. appId", zzcfl.zzdZ(str), Double.valueOf(d));
                            zzwz().setTransactionSuccessful();
                            zzwz().endTransaction();
                            return;
                        }
                        round = Math.round(d);
                    } else {
                        round = zzyt.getLong("value");
                    }
                    if (!TextUtils.isEmpty(r2)) {
                        String toUpperCase = r2.toUpperCase(Locale.US);
                        if (toUpperCase.matches("[A-Z]{3}")) {
                            String valueOf = String.valueOf("_ltv_");
                            toUpperCase = String.valueOf(toUpperCase);
                            String concat = toUpperCase.length() != 0 ? valueOf.concat(toUpperCase) : new String(valueOf);
                            zzcjk zzG = zzwz().zzG(str, concat);
                            if (zzG == null || !(zzG.mValue instanceof Long)) {
                                zzwz = zzwz();
                                int zzb = this.zzbsn.zzb(str, zzcfb.zzbqz) - 1;
                                zzbo.zzcF(str);
                                zzwz.zzjC();
                                zzwz.zzkD();
                                zzwz.getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(zzb)});
                                zzG = new zzcjk(str, zzcez.zzbpc, concat, this.zzvw.currentTimeMillis(), Long.valueOf(round));
                            } else {
                                zzG = new zzcjk(str, zzcez.zzbpc, concat, this.zzvw.currentTimeMillis(), Long.valueOf(round + ((Long) zzG.mValue).longValue()));
                            }
                            if (!zzwz().zza(zzG)) {
                                zzwF().zzyx().zzd("Too many unique user properties are set. Ignoring user property. appId", zzcfl.zzdZ(str), zzwA().zzdY(zzG.mName), zzG.mValue);
                                zzwB().zza$2c2ba1f5(9, null, null, 0);
                            }
                        }
                    }
                }
            } catch (SQLiteException e) {
                zzwz.zzwF().zzyx().zze("Error pruning currencies. appId", zzcfl.zzdZ(str), e);
            } catch (Throwable th) {
                zzwz().endTransaction();
            }
            boolean zzeo = zzcjl.zzeo(zzcez.name);
            boolean equals = "_err".equals(zzcez.name);
            zzceo zza = zzwz().zza(zzyZ(), str, true, zzeo, false, equals, false);
            long zzxq = zza.zzbpv - zzcem.zzxq();
            if (zzxq > 0) {
                if (zzxq % 1000 == 1) {
                    zzwF().zzyx().zze("Data loss. Too many events logged. appId, count", zzcfl.zzdZ(str), Long.valueOf(zza.zzbpv));
                }
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return;
            }
            zzcev zzcev;
            if (zzeo) {
                zzxq = zza.zzbpu - zzcem.zzxr();
                if (zzxq > 0) {
                    if (zzxq % 1000 == 1) {
                        zzwF().zzyx().zze("Data loss. Too many public events logged. appId, count", zzcfl.zzdZ(str), Long.valueOf(zza.zzbpu));
                    }
                    zzwB().zza$2c2ba1f5(16, "_ev", zzcez.name, 0);
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return;
                }
            }
            if (equals) {
                zzxq = zza.zzbpx - ((long) Math.max(0, Math.min(1000000, this.zzbsn.zzb(zzceh.packageName, zzcfb.zzbqg))));
                if (zzxq > 0) {
                    if (zzxq == 1) {
                        zzwF().zzyx().zze("Too many error events logged. appId, count", zzcfl.zzdZ(str), Long.valueOf(zza.zzbpx));
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    return;
                }
            }
            zzwB().zza(zzyt, "_o", zzcez.zzbpc);
            if (zzwB().zzey(str)) {
                zzwB().zza(zzyt, "_dbg", Long.valueOf(1));
                zzwB().zza(zzyt, "_r", Long.valueOf(1));
            }
            zzxq = zzwz().zzdR(str);
            if (zzxq > 0) {
                zzwF().zzyz().zze("Data lost. Too many events stored on disk, deleted. appId", zzcfl.zzdZ(str), Long.valueOf(zzxq));
            }
            zzceu zzceu = new zzceu(this, zzcez.zzbpc, str, zzcez.name, zzcez.zzbpN, 0, zzyt);
            zzcev zzE = zzwz().zzE(str, zzceu.mName);
            if (zzE == null) {
                long zzdU = zzwz().zzdU(str);
                zzcem.zzxp();
                if (zzdU >= 500) {
                    zzwF().zzyx().zzd("Too many event names used, ignoring event. appId, name, supported count", zzcfl.zzdZ(str), zzwA().zzdW(zzceu.mName), Integer.valueOf(zzcem.zzxp()));
                    zzwB().zza$2c2ba1f5(8, null, null, 0);
                    zzwz().endTransaction();
                    return;
                }
                zzcev = new zzcev(str, zzceu.mName, 0, 0, zzceu.zzayS);
            } else {
                zzceu = zzceu.zza(this, zzE.zzbpI);
                zzcev = zzE.zzab(zzceu.zzayS);
            }
            zzwz().zza(zzcev);
            zza(zzceu, zzceh);
            zzwz().setTransactionSuccessful();
            if (zzwF().zzz(2)) {
                zzwF().zzyD().zzj("Event recorded", zzwA().zza(zzceu));
            }
            zzwz().endTransaction();
            zzzc();
            zzwF().zzyD().zzj("Background event processing time, ms", Long.valueOf(((System.nanoTime() - nanoTime) + 500000) / 1000000));
        }
    }

    private final zzceh zzel(String str) {
        zzceg zzdQ = zzwz().zzdQ(str);
        if (zzdQ == null || TextUtils.isEmpty(zzdQ.zzjH())) {
            zzwF().zzyC().zzj("No app data available; dropping", str);
            return null;
        }
        try {
            String str2 = zzbha.zzaP(this.mContext).getPackageInfo(str, 0).versionName;
            if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(str2))) {
                zzwF().zzyz().zzj("App version does not match; dropping. appId", zzcfl.zzdZ(str));
                return null;
            }
        } catch (NameNotFoundException e) {
        }
        return new zzceh(str, zzdQ.getGmpAppId(), zzdQ.zzjH(), zzdQ.zzwN(), zzdQ.zzwO(), zzdQ.zzwP(), zzdQ.zzwQ(), null, zzdQ.zzwR(), false, zzdQ.zzwK(), zzdQ.zzxe(), 0, 0);
    }

    private final void zzf(zzceh zzceh) {
        Object obj = 1;
        zzwE().zzjC();
        zzbo.zzu(zzceh);
        zzbo.zzcF(zzceh.packageName);
        zzceg zzdQ = zzwz().zzdQ(zzceh.packageName);
        String zzec = zzwG().zzec(zzceh.packageName);
        Object obj2 = null;
        if (zzdQ == null) {
            zzceg zzceg = new zzceg(this, zzceh.packageName);
            zzceg.zzdG(zzwu().zzyu());
            zzceg.zzdI(zzec);
            zzdQ = zzceg;
            obj2 = 1;
        } else if (!zzec.equals(zzdQ.zzwJ())) {
            zzdQ.zzdI(zzec);
            zzdQ.zzdG(zzwu().zzyu());
            int i = 1;
        }
        if (!(TextUtils.isEmpty(zzceh.zzboQ) || zzceh.zzboQ.equals(zzdQ.getGmpAppId()))) {
            zzdQ.zzdH(zzceh.zzboQ);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(zzceh.zzboY) || zzceh.zzboY.equals(zzdQ.zzwK()))) {
            zzdQ.zzdJ(zzceh.zzboY);
            obj2 = 1;
        }
        if (!(zzceh.zzboS == 0 || zzceh.zzboS == zzdQ.zzwP())) {
            zzdQ.zzO(zzceh.zzboS);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(zzceh.zzbgW) || zzceh.zzbgW.equals(zzdQ.zzjH()))) {
            zzdQ.setAppVersion(zzceh.zzbgW);
            obj2 = 1;
        }
        if (zzceh.zzboX != zzdQ.zzwN()) {
            zzdQ.zzN(zzceh.zzboX);
            obj2 = 1;
        }
        if (!(zzceh.zzboR == null || zzceh.zzboR.equals(zzdQ.zzwO()))) {
            zzdQ.zzdK(zzceh.zzboR);
            obj2 = 1;
        }
        if (zzceh.zzboT != zzdQ.zzwQ()) {
            zzdQ.zzP(zzceh.zzboT);
            obj2 = 1;
        }
        if (zzceh.zzboV != zzdQ.zzwR()) {
            zzdQ.setMeasurementEnabled(zzceh.zzboV);
            obj2 = 1;
        }
        if (!(TextUtils.isEmpty(zzceh.zzboU) || zzceh.zzboU.equals(zzdQ.zzxc()))) {
            zzdQ.zzdL(zzceh.zzboU);
            obj2 = 1;
        }
        if (zzceh.zzboZ != zzdQ.zzxe()) {
            zzdQ.zzZ(zzceh.zzboZ);
        } else {
            obj = obj2;
        }
        if (obj != null) {
            zzwz().zza(zzdQ);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean zzg$505cfb57(long r20) {
        /*
        r19 = this;
        r2 = r19.zzwz();
        r2.beginTransaction();
        r15 = new com.google.android.gms.internal.zzcgl$zza;	 Catch:{ all -> 0x0199 }
        r2 = 0;
        r0 = r19;
        r15.<init>();	 Catch:{ all -> 0x0199 }
        r14 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r4 = 0;
        r0 = r19;
        r0 = r0.zzbsS;	 Catch:{ all -> 0x0199 }
        r16 = r0;
        com.google.android.gms.common.internal.zzbo.zzu(r15);	 Catch:{ all -> 0x0199 }
        r14.zzjC();	 Catch:{ all -> 0x0199 }
        r14.zzkD();	 Catch:{ all -> 0x0199 }
        r3 = 0;
        r2 = r14.getWritableDatabase();	 Catch:{ SQLiteException -> 0x0790 }
        r5 = 0;
        r5 = android.text.TextUtils.isEmpty(r5);	 Catch:{ SQLiteException -> 0x0790 }
        if (r5 == 0) goto L_0x01a2;
    L_0x002f:
        r6 = -1;
        r5 = (r16 > r6 ? 1 : (r16 == r6 ? 0 : -1));
        if (r5 == 0) goto L_0x013b;
    L_0x0035:
        r5 = 2;
        r5 = new java.lang.String[r5];	 Catch:{ SQLiteException -> 0x0790 }
        r6 = 0;
        r7 = java.lang.String.valueOf(r16);	 Catch:{ SQLiteException -> 0x0790 }
        r5[r6] = r7;	 Catch:{ SQLiteException -> 0x0790 }
        r6 = 1;
        r7 = java.lang.String.valueOf(r20);	 Catch:{ SQLiteException -> 0x0790 }
        r5[r6] = r7;	 Catch:{ SQLiteException -> 0x0790 }
        r6 = r5;
    L_0x0047:
        r8 = -1;
        r5 = (r16 > r8 ? 1 : (r16 == r8 ? 0 : -1));
        if (r5 == 0) goto L_0x0148;
    L_0x004d:
        r5 = "rowid <= ? and ";
    L_0x004f:
        r7 = java.lang.String.valueOf(r5);	 Catch:{ SQLiteException -> 0x0790 }
        r7 = r7.length();	 Catch:{ SQLiteException -> 0x0790 }
        r7 = r7 + 148;
        r8 = new java.lang.StringBuilder;	 Catch:{ SQLiteException -> 0x0790 }
        r8.<init>(r7);	 Catch:{ SQLiteException -> 0x0790 }
        r7 = "select app_id, metadata_fingerprint from raw_events where ";
        r7 = r8.append(r7);	 Catch:{ SQLiteException -> 0x0790 }
        r5 = r7.append(r5);	 Catch:{ SQLiteException -> 0x0790 }
        r7 = "app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;";
        r5 = r5.append(r7);	 Catch:{ SQLiteException -> 0x0790 }
        r5 = r5.toString();	 Catch:{ SQLiteException -> 0x0790 }
        r3 = r2.rawQuery(r5, r6);	 Catch:{ SQLiteException -> 0x0790 }
        r5 = r3.moveToFirst();	 Catch:{ SQLiteException -> 0x0790 }
        if (r5 != 0) goto L_0x014c;
    L_0x007c:
        if (r3 == 0) goto L_0x0081;
    L_0x007e:
        r3.close();	 Catch:{ all -> 0x0199 }
    L_0x0081:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x008d;
    L_0x0085:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.isEmpty();	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x032f;
    L_0x008d:
        r2 = 1;
    L_0x008e:
        if (r2 != 0) goto L_0x077b;
    L_0x0090:
        r13 = 0;
        r0 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r16 = r0;
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.size();	 Catch:{ all -> 0x0199 }
        r2 = new com.google.android.gms.internal.zzcjw[r2];	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvE = r2;	 Catch:{ all -> 0x0199 }
        r12 = 0;
        r2 = 0;
        r14 = r2;
    L_0x00a4:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.size();	 Catch:{ all -> 0x0199 }
        if (r14 >= r2) goto L_0x05c3;
    L_0x00ac:
        r3 = r19.zzwC();	 Catch:{ all -> 0x0199 }
        r2 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r4 = r2.zzaH;	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r2 = r3.zzN(r4, r2);	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x0335;
    L_0x00c4:
        r2 = r19.zzwF();	 Catch:{ all -> 0x0199 }
        r3 = r2.zzyz();	 Catch:{ all -> 0x0199 }
        r4 = "Dropping blacklisted raw event. appId";
        r2 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzaH;	 Catch:{ all -> 0x0199 }
        r5 = com.google.android.gms.internal.zzcfl.zzdZ(r2);	 Catch:{ all -> 0x0199 }
        r6 = r19.zzwA();	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r2 = r6.zzdW(r2);	 Catch:{ all -> 0x0199 }
        r3.zze(r4, r5, r2);	 Catch:{ all -> 0x0199 }
        r2 = r19.zzwB();	 Catch:{ all -> 0x0199 }
        r3 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r3 = r3.zzaH;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzeA(r3);	 Catch:{ all -> 0x0199 }
        if (r2 != 0) goto L_0x0107;
    L_0x00f9:
        r2 = r19.zzwB();	 Catch:{ all -> 0x0199 }
        r3 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r3 = r3.zzaH;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzeB(r3);	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x0332;
    L_0x0107:
        r2 = 1;
    L_0x0108:
        if (r2 != 0) goto L_0x07a7;
    L_0x010a:
        r3 = "_err";
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r2 = r3.equals(r2);	 Catch:{ all -> 0x0199 }
        if (r2 != 0) goto L_0x07a7;
    L_0x011c:
        r3 = r19.zzwB();	 Catch:{ all -> 0x0199 }
        r4 = 11;
        r5 = "_ev";
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r6 = 0;
        r3.zza$2c2ba1f5(r4, r5, r2, r6);	 Catch:{ all -> 0x0199 }
        r2 = r12;
        r3 = r13;
    L_0x0134:
        r4 = r14 + 1;
        r14 = r4;
        r12 = r2;
        r13 = r3;
        goto L_0x00a4;
    L_0x013b:
        r5 = 1;
        r5 = new java.lang.String[r5];	 Catch:{ SQLiteException -> 0x0790 }
        r6 = 0;
        r7 = java.lang.String.valueOf(r20);	 Catch:{ SQLiteException -> 0x0790 }
        r5[r6] = r7;	 Catch:{ SQLiteException -> 0x0790 }
        r6 = r5;
        goto L_0x0047;
    L_0x0148:
        r5 = "";
        goto L_0x004f;
    L_0x014c:
        r5 = 0;
        r4 = r3.getString(r5);	 Catch:{ SQLiteException -> 0x0790 }
        r5 = 1;
        r5 = r3.getString(r5);	 Catch:{ SQLiteException -> 0x0790 }
        r3.close();	 Catch:{ SQLiteException -> 0x0790 }
        r13 = r5;
        r11 = r3;
        r12 = r4;
    L_0x015c:
        r3 = "raw_events_metadata";
        r4 = 1;
        r4 = new java.lang.String[r4];	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r5 = 0;
        r6 = "metadata";
        r4[r5] = r6;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r5 = "app_id = ? and metadata_fingerprint = ?";
        r6 = 2;
        r6 = new java.lang.String[r6];	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 0;
        r6[r7] = r12;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 1;
        r6[r7] = r13;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 0;
        r8 = 0;
        r9 = "rowid";
        r10 = "2";
        r11 = r2.query(r3, r4, r5, r6, r7, r8, r9, r10);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = r11.moveToFirst();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        if (r3 != 0) goto L_0x020c;
    L_0x0181:
        r2 = r14.zzwF();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r2 = r2.zzyx();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = "Raw event metadata record is missing. appId";
        r4 = com.google.android.gms.internal.zzcfl.zzdZ(r12);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r2.zzj(r3, r4);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        if (r11 == 0) goto L_0x0081;
    L_0x0194:
        r11.close();	 Catch:{ all -> 0x0199 }
        goto L_0x0081;
    L_0x0199:
        r2 = move-exception;
        r3 = r19.zzwz();
        r3.endTransaction();
        throw r2;
    L_0x01a2:
        r6 = -1;
        r5 = (r16 > r6 ? 1 : (r16 == r6 ? 0 : -1));
        if (r5 == 0) goto L_0x01f3;
    L_0x01a8:
        r5 = 2;
        r5 = new java.lang.String[r5];	 Catch:{ SQLiteException -> 0x0790 }
        r6 = 0;
        r7 = 0;
        r5[r6] = r7;	 Catch:{ SQLiteException -> 0x0790 }
        r6 = 1;
        r7 = java.lang.String.valueOf(r16);	 Catch:{ SQLiteException -> 0x0790 }
        r5[r6] = r7;	 Catch:{ SQLiteException -> 0x0790 }
        r6 = r5;
    L_0x01b7:
        r8 = -1;
        r5 = (r16 > r8 ? 1 : (r16 == r8 ? 0 : -1));
        if (r5 == 0) goto L_0x01fc;
    L_0x01bd:
        r5 = " and rowid <= ?";
    L_0x01bf:
        r7 = java.lang.String.valueOf(r5);	 Catch:{ SQLiteException -> 0x0790 }
        r7 = r7.length();	 Catch:{ SQLiteException -> 0x0790 }
        r7 = r7 + 84;
        r8 = new java.lang.StringBuilder;	 Catch:{ SQLiteException -> 0x0790 }
        r8.<init>(r7);	 Catch:{ SQLiteException -> 0x0790 }
        r7 = "select metadata_fingerprint from raw_events where app_id = ?";
        r7 = r8.append(r7);	 Catch:{ SQLiteException -> 0x0790 }
        r5 = r7.append(r5);	 Catch:{ SQLiteException -> 0x0790 }
        r7 = " order by rowid limit 1;";
        r5 = r5.append(r7);	 Catch:{ SQLiteException -> 0x0790 }
        r5 = r5.toString();	 Catch:{ SQLiteException -> 0x0790 }
        r3 = r2.rawQuery(r5, r6);	 Catch:{ SQLiteException -> 0x0790 }
        r5 = r3.moveToFirst();	 Catch:{ SQLiteException -> 0x0790 }
        if (r5 != 0) goto L_0x01ff;
    L_0x01ec:
        if (r3 == 0) goto L_0x0081;
    L_0x01ee:
        r3.close();	 Catch:{ all -> 0x0199 }
        goto L_0x0081;
    L_0x01f3:
        r5 = 1;
        r5 = new java.lang.String[r5];	 Catch:{ SQLiteException -> 0x0790 }
        r6 = 0;
        r7 = 0;
        r5[r6] = r7;	 Catch:{ SQLiteException -> 0x0790 }
        r6 = r5;
        goto L_0x01b7;
    L_0x01fc:
        r5 = "";
        goto L_0x01bf;
    L_0x01ff:
        r5 = 0;
        r5 = r3.getString(r5);	 Catch:{ SQLiteException -> 0x0790 }
        r3.close();	 Catch:{ SQLiteException -> 0x0790 }
        r13 = r5;
        r11 = r3;
        r12 = r4;
        goto L_0x015c;
    L_0x020c:
        r3 = 0;
        r3 = r11.getBlob(r3);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r4 = r3.length;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = com.google.android.gms.internal.adg.zzb$2f392411(r3, r4);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r4 = new com.google.android.gms.internal.zzcjz;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r4.<init>();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r4.zza(r3);	 Catch:{ IOException -> 0x0293 }
        r3 = r11.moveToNext();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        if (r3 == 0) goto L_0x0235;
    L_0x0224:
        r3 = r14.zzwF();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = r3.zzyz();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r5 = "Get multiple raw event metadata records, expected one. appId";
        r6 = com.google.android.gms.internal.zzcfl.zzdZ(r12);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3.zzj(r5, r6);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
    L_0x0235:
        r11.close();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r15.zzb(r4);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r4 = -1;
        r3 = (r16 > r4 ? 1 : (r16 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x02ac;
    L_0x0241:
        r5 = "app_id = ? and metadata_fingerprint = ? and rowid <= ?";
        r3 = 3;
        r6 = new java.lang.String[r3];	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = 0;
        r6[r3] = r12;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = 1;
        r6[r3] = r13;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = 2;
        r4 = java.lang.String.valueOf(r16);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r6[r3] = r4;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
    L_0x0253:
        r3 = "raw_events";
        r4 = 4;
        r4 = new java.lang.String[r4];	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 0;
        r8 = "rowid";
        r4[r7] = r8;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 1;
        r8 = "name";
        r4[r7] = r8;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 2;
        r8 = "timestamp";
        r4[r7] = r8;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 3;
        r8 = "data";
        r4[r7] = r8;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r7 = 0;
        r8 = 0;
        r9 = "rowid";
        r10 = 0;
        r3 = r2.query(r3, r4, r5, r6, r7, r8, r9, r10);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r2 = r3.moveToFirst();	 Catch:{ SQLiteException -> 0x0793 }
        if (r2 != 0) goto L_0x02d3;
    L_0x027b:
        r2 = r14.zzwF();	 Catch:{ SQLiteException -> 0x0793 }
        r2 = r2.zzyz();	 Catch:{ SQLiteException -> 0x0793 }
        r4 = "Raw event data disappeared while in transaction. appId";
        r5 = com.google.android.gms.internal.zzcfl.zzdZ(r12);	 Catch:{ SQLiteException -> 0x0793 }
        r2.zzj(r4, r5);	 Catch:{ SQLiteException -> 0x0793 }
        if (r3 == 0) goto L_0x0081;
    L_0x028e:
        r3.close();	 Catch:{ all -> 0x0199 }
        goto L_0x0081;
    L_0x0293:
        r2 = move-exception;
        r3 = r14.zzwF();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = r3.zzyx();	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r4 = "Data loss. Failed to merge raw event metadata. appId";
        r5 = com.google.android.gms.internal.zzcfl.zzdZ(r12);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3.zze(r4, r5, r2);	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        if (r11 == 0) goto L_0x0081;
    L_0x02a7:
        r11.close();	 Catch:{ all -> 0x0199 }
        goto L_0x0081;
    L_0x02ac:
        r5 = "app_id = ? and metadata_fingerprint = ?";
        r3 = 2;
        r6 = new java.lang.String[r3];	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = 0;
        r6[r3] = r12;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        r3 = 1;
        r6[r3] = r13;	 Catch:{ SQLiteException -> 0x02b8, all -> 0x078c }
        goto L_0x0253;
    L_0x02b8:
        r2 = move-exception;
        r3 = r11;
        r4 = r12;
    L_0x02bb:
        r5 = r14.zzwF();	 Catch:{ all -> 0x0328 }
        r5 = r5.zzyx();	 Catch:{ all -> 0x0328 }
        r6 = "Data loss. Error selecting raw event. appId";
        r4 = com.google.android.gms.internal.zzcfl.zzdZ(r4);	 Catch:{ all -> 0x0328 }
        r5.zze(r6, r4, r2);	 Catch:{ all -> 0x0328 }
        if (r3 == 0) goto L_0x0081;
    L_0x02ce:
        r3.close();	 Catch:{ all -> 0x0199 }
        goto L_0x0081;
    L_0x02d3:
        r2 = 0;
        r4 = r3.getLong(r2);	 Catch:{ SQLiteException -> 0x0793 }
        r2 = 3;
        r2 = r3.getBlob(r2);	 Catch:{ SQLiteException -> 0x0793 }
        r6 = r2.length;	 Catch:{ SQLiteException -> 0x0793 }
        r2 = com.google.android.gms.internal.adg.zzb$2f392411(r2, r6);	 Catch:{ SQLiteException -> 0x0793 }
        r6 = new com.google.android.gms.internal.zzcjw;	 Catch:{ SQLiteException -> 0x0793 }
        r6.<init>();	 Catch:{ SQLiteException -> 0x0793 }
        r6.zza(r2);	 Catch:{ IOException -> 0x0309 }
        r2 = 1;
        r2 = r3.getString(r2);	 Catch:{ SQLiteException -> 0x0793 }
        r6.name = r2;	 Catch:{ SQLiteException -> 0x0793 }
        r2 = 2;
        r8 = r3.getLong(r2);	 Catch:{ SQLiteException -> 0x0793 }
        r2 = java.lang.Long.valueOf(r8);	 Catch:{ SQLiteException -> 0x0793 }
        r6.zzbvx = r2;	 Catch:{ SQLiteException -> 0x0793 }
        r2 = r15.zza(r4, r6);	 Catch:{ SQLiteException -> 0x0793 }
        if (r2 != 0) goto L_0x031b;
    L_0x0302:
        if (r3 == 0) goto L_0x0081;
    L_0x0304:
        r3.close();	 Catch:{ all -> 0x0199 }
        goto L_0x0081;
    L_0x0309:
        r2 = move-exception;
        r4 = r14.zzwF();	 Catch:{ SQLiteException -> 0x0793 }
        r4 = r4.zzyx();	 Catch:{ SQLiteException -> 0x0793 }
        r5 = "Data loss. Failed to merge raw event. appId";
        r6 = com.google.android.gms.internal.zzcfl.zzdZ(r12);	 Catch:{ SQLiteException -> 0x0793 }
        r4.zze(r5, r6, r2);	 Catch:{ SQLiteException -> 0x0793 }
    L_0x031b:
        r2 = r3.moveToNext();	 Catch:{ SQLiteException -> 0x0793 }
        if (r2 != 0) goto L_0x02d3;
    L_0x0321:
        if (r3 == 0) goto L_0x0081;
    L_0x0323:
        r3.close();	 Catch:{ all -> 0x0199 }
        goto L_0x0081;
    L_0x0328:
        r2 = move-exception;
    L_0x0329:
        if (r3 == 0) goto L_0x032e;
    L_0x032b:
        r3.close();	 Catch:{ all -> 0x0199 }
    L_0x032e:
        throw r2;	 Catch:{ all -> 0x0199 }
    L_0x032f:
        r2 = 0;
        goto L_0x008e;
    L_0x0332:
        r2 = 0;
        goto L_0x0108;
    L_0x0335:
        r3 = r19.zzwC();	 Catch:{ all -> 0x0199 }
        r2 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r4 = r2.zzaH;	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r17 = r3.zzO(r4, r2);	 Catch:{ all -> 0x0199 }
        if (r17 != 0) goto L_0x0360;
    L_0x034d:
        r19.zzwB();	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r2 = com.google.android.gms.internal.zzcjl.zzeC(r2);	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x05c1;
    L_0x0360:
        r3 = 0;
        r4 = 0;
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        if (r2 != 0) goto L_0x037b;
    L_0x036e:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r5 = 0;
        r5 = new com.google.android.gms.internal.zzcjx[r5];	 Catch:{ all -> 0x0199 }
        r2.zzbvw = r5;	 Catch:{ all -> 0x0199 }
    L_0x037b:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r6 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r7 = r6.length;	 Catch:{ all -> 0x0199 }
        r2 = 0;
        r5 = r2;
    L_0x0388:
        if (r5 >= r7) goto L_0x03ba;
    L_0x038a:
        r2 = r6[r5];	 Catch:{ all -> 0x0199 }
        r8 = "_c";
        r9 = r2.name;	 Catch:{ all -> 0x0199 }
        r8 = r8.equals(r9);	 Catch:{ all -> 0x0199 }
        if (r8 == 0) goto L_0x03a6;
    L_0x0396:
        r8 = 1;
        r3 = java.lang.Long.valueOf(r8);	 Catch:{ all -> 0x0199 }
        r2.zzbvA = r3;	 Catch:{ all -> 0x0199 }
        r2 = 1;
        r3 = r2;
        r2 = r4;
    L_0x03a1:
        r4 = r5 + 1;
        r5 = r4;
        r4 = r2;
        goto L_0x0388;
    L_0x03a6:
        r8 = "_r";
        r9 = r2.name;	 Catch:{ all -> 0x0199 }
        r8 = r8.equals(r9);	 Catch:{ all -> 0x0199 }
        if (r8 == 0) goto L_0x07a4;
    L_0x03b0:
        r8 = 1;
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ all -> 0x0199 }
        r2.zzbvA = r4;	 Catch:{ all -> 0x0199 }
        r2 = 1;
        goto L_0x03a1;
    L_0x03ba:
        if (r3 != 0) goto L_0x041a;
    L_0x03bc:
        if (r17 == 0) goto L_0x041a;
    L_0x03be:
        r2 = r19.zzwF();	 Catch:{ all -> 0x0199 }
        r3 = r2.zzyD();	 Catch:{ all -> 0x0199 }
        r5 = "Marking event as conversion";
        r6 = r19.zzwA();	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r2 = r6.zzdW(r2);	 Catch:{ all -> 0x0199 }
        r3.zzj(r5, r2);	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r3 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r2 = r2.length;	 Catch:{ all -> 0x0199 }
        r2 = r2 + 1;
        r2 = java.util.Arrays.copyOf(r3, r2);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjx[]) r2;	 Catch:{ all -> 0x0199 }
        r3 = new com.google.android.gms.internal.zzcjx;	 Catch:{ all -> 0x0199 }
        r3.<init>();	 Catch:{ all -> 0x0199 }
        r5 = "_c";
        r3.name = r5;	 Catch:{ all -> 0x0199 }
        r6 = 1;
        r5 = java.lang.Long.valueOf(r6);	 Catch:{ all -> 0x0199 }
        r3.zzbvA = r5;	 Catch:{ all -> 0x0199 }
        r5 = r2.length;	 Catch:{ all -> 0x0199 }
        r5 = r5 + -1;
        r2[r5] = r3;	 Catch:{ all -> 0x0199 }
        r3 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r3 = r3.get(r14);	 Catch:{ all -> 0x0199 }
        r3 = (com.google.android.gms.internal.zzcjw) r3;	 Catch:{ all -> 0x0199 }
        r3.zzbvw = r2;	 Catch:{ all -> 0x0199 }
    L_0x041a:
        if (r4 != 0) goto L_0x0478;
    L_0x041c:
        r2 = r19.zzwF();	 Catch:{ all -> 0x0199 }
        r3 = r2.zzyD();	 Catch:{ all -> 0x0199 }
        r4 = "Marking event as real-time";
        r5 = r19.zzwA();	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r2 = r5.zzdW(r2);	 Catch:{ all -> 0x0199 }
        r3.zzj(r4, r2);	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r3 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r2 = r2.length;	 Catch:{ all -> 0x0199 }
        r2 = r2 + 1;
        r2 = java.util.Arrays.copyOf(r3, r2);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjx[]) r2;	 Catch:{ all -> 0x0199 }
        r3 = new com.google.android.gms.internal.zzcjx;	 Catch:{ all -> 0x0199 }
        r3.<init>();	 Catch:{ all -> 0x0199 }
        r4 = "_r";
        r3.name = r4;	 Catch:{ all -> 0x0199 }
        r4 = 1;
        r4 = java.lang.Long.valueOf(r4);	 Catch:{ all -> 0x0199 }
        r3.zzbvA = r4;	 Catch:{ all -> 0x0199 }
        r4 = r2.length;	 Catch:{ all -> 0x0199 }
        r4 = r4 + -1;
        r2[r4] = r3;	 Catch:{ all -> 0x0199 }
        r3 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r3 = r3.get(r14);	 Catch:{ all -> 0x0199 }
        r3 = (com.google.android.gms.internal.zzcjw) r3;	 Catch:{ all -> 0x0199 }
        r3.zzbvw = r2;	 Catch:{ all -> 0x0199 }
    L_0x0478:
        r2 = 1;
        r3 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r4 = r19.zzyZ();	 Catch:{ all -> 0x0199 }
        r6 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r6 = r6.zzaH;	 Catch:{ all -> 0x0199 }
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r11 = 1;
        r3 = r3.zza(r4, r6, r7, r8, r9, r10, r11);	 Catch:{ all -> 0x0199 }
        r4 = r3.zzbpy;	 Catch:{ all -> 0x0199 }
        r0 = r19;
        r3 = r0.zzbsn;	 Catch:{ all -> 0x0199 }
        r6 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r6 = r6.zzaH;	 Catch:{ all -> 0x0199 }
        r3 = r3.zzdM(r6);	 Catch:{ all -> 0x0199 }
        r6 = (long) r3;	 Catch:{ all -> 0x0199 }
        r3 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r3 <= 0) goto L_0x07a1;
    L_0x04a1:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r3 = 0;
    L_0x04aa:
        r4 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r4 = r4.length;	 Catch:{ all -> 0x0199 }
        if (r3 >= r4) goto L_0x04db;
    L_0x04af:
        r4 = "_r";
        r5 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r5 = r5[r3];	 Catch:{ all -> 0x0199 }
        r5 = r5.name;	 Catch:{ all -> 0x0199 }
        r4 = r4.equals(r5);	 Catch:{ all -> 0x0199 }
        if (r4 == 0) goto L_0x0550;
    L_0x04bd:
        r4 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r4 = r4.length;	 Catch:{ all -> 0x0199 }
        r4 = r4 + -1;
        r4 = new com.google.android.gms.internal.zzcjx[r4];	 Catch:{ all -> 0x0199 }
        if (r3 <= 0) goto L_0x04cd;
    L_0x04c6:
        r5 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r6 = 0;
        r7 = 0;
        java.lang.System.arraycopy(r5, r6, r4, r7, r3);	 Catch:{ all -> 0x0199 }
    L_0x04cd:
        r5 = r4.length;	 Catch:{ all -> 0x0199 }
        if (r3 >= r5) goto L_0x04d9;
    L_0x04d0:
        r5 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r6 = r3 + 1;
        r7 = r4.length;	 Catch:{ all -> 0x0199 }
        r7 = r7 - r3;
        java.lang.System.arraycopy(r5, r6, r4, r3, r7);	 Catch:{ all -> 0x0199 }
    L_0x04d9:
        r2.zzbvw = r4;	 Catch:{ all -> 0x0199 }
    L_0x04db:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r2 = r2.name;	 Catch:{ all -> 0x0199 }
        r2 = com.google.android.gms.internal.zzcjl.zzeo(r2);	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x05c1;
    L_0x04eb:
        if (r17 == 0) goto L_0x05c1;
    L_0x04ed:
        r3 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r4 = r19.zzyZ();	 Catch:{ all -> 0x0199 }
        r2 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r6 = r2.zzaH;	 Catch:{ all -> 0x0199 }
        r7 = 0;
        r8 = 0;
        r9 = 1;
        r10 = 0;
        r11 = 0;
        r2 = r3.zza(r4, r6, r7, r8, r9, r10, r11);	 Catch:{ all -> 0x0199 }
        r2 = r2.zzbpw;	 Catch:{ all -> 0x0199 }
        r0 = r19;
        r4 = r0.zzbsn;	 Catch:{ all -> 0x0199 }
        r5 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r5 = r5.zzaH;	 Catch:{ all -> 0x0199 }
        r6 = com.google.android.gms.internal.zzcfb.zzbqi;	 Catch:{ all -> 0x0199 }
        r4 = r4.zzb(r5, r6);	 Catch:{ all -> 0x0199 }
        r4 = (long) r4;	 Catch:{ all -> 0x0199 }
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 <= 0) goto L_0x05c1;
    L_0x0517:
        r2 = r19.zzwF();	 Catch:{ all -> 0x0199 }
        r2 = r2.zzyz();	 Catch:{ all -> 0x0199 }
        r3 = "Too many conversions. Not logging as conversion. appId";
        r4 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r4 = r4.zzaH;	 Catch:{ all -> 0x0199 }
        r4 = com.google.android.gms.internal.zzcfl.zzdZ(r4);	 Catch:{ all -> 0x0199 }
        r2.zzj(r3, r4);	 Catch:{ all -> 0x0199 }
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r5 = 0;
        r4 = 0;
        r7 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r8 = r7.length;	 Catch:{ all -> 0x0199 }
        r3 = 0;
        r6 = r3;
    L_0x053b:
        if (r6 >= r8) goto L_0x0565;
    L_0x053d:
        r3 = r7[r6];	 Catch:{ all -> 0x0199 }
        r9 = "_c";
        r10 = r3.name;	 Catch:{ all -> 0x0199 }
        r9 = r9.equals(r10);	 Catch:{ all -> 0x0199 }
        if (r9 == 0) goto L_0x0554;
    L_0x0549:
        r4 = r5;
    L_0x054a:
        r5 = r6 + 1;
        r6 = r5;
        r5 = r4;
        r4 = r3;
        goto L_0x053b;
    L_0x0550:
        r3 = r3 + 1;
        goto L_0x04aa;
    L_0x0554:
        r9 = "_err";
        r3 = r3.name;	 Catch:{ all -> 0x0199 }
        r3 = r9.equals(r3);	 Catch:{ all -> 0x0199 }
        if (r3 == 0) goto L_0x079d;
    L_0x055e:
        r3 = 1;
        r18 = r4;
        r4 = r3;
        r3 = r18;
        goto L_0x054a;
    L_0x0565:
        if (r5 == 0) goto L_0x059c;
    L_0x0567:
        if (r4 == 0) goto L_0x059c;
    L_0x0569:
        r3 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r3 = r3.length;	 Catch:{ all -> 0x0199 }
        r3 = r3 + -1;
        r7 = new com.google.android.gms.internal.zzcjx[r3];	 Catch:{ all -> 0x0199 }
        r5 = 0;
        r8 = r2.zzbvw;	 Catch:{ all -> 0x0199 }
        r9 = r8.length;	 Catch:{ all -> 0x0199 }
        r3 = 0;
        r6 = r3;
    L_0x0576:
        if (r6 >= r9) goto L_0x0585;
    L_0x0578:
        r10 = r8[r6];	 Catch:{ all -> 0x0199 }
        if (r10 == r4) goto L_0x079a;
    L_0x057c:
        r3 = r5 + 1;
        r7[r5] = r10;	 Catch:{ all -> 0x0199 }
    L_0x0580:
        r5 = r6 + 1;
        r6 = r5;
        r5 = r3;
        goto L_0x0576;
    L_0x0585:
        r2.zzbvw = r7;	 Catch:{ all -> 0x0199 }
        r4 = r13;
    L_0x0588:
        r0 = r16;
        r5 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r3 = r12 + 1;
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.get(r14);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw) r2;	 Catch:{ all -> 0x0199 }
        r5[r12] = r2;	 Catch:{ all -> 0x0199 }
        r2 = r3;
        r3 = r4;
        goto L_0x0134;
    L_0x059c:
        if (r4 == 0) goto L_0x05ac;
    L_0x059e:
        r2 = "_err";
        r4.name = r2;	 Catch:{ all -> 0x0199 }
        r2 = 10;
        r2 = java.lang.Long.valueOf(r2);	 Catch:{ all -> 0x0199 }
        r4.zzbvA = r2;	 Catch:{ all -> 0x0199 }
        r4 = r13;
        goto L_0x0588;
    L_0x05ac:
        r2 = r19.zzwF();	 Catch:{ all -> 0x0199 }
        r2 = r2.zzyx();	 Catch:{ all -> 0x0199 }
        r3 = "Did not find conversion parameter. appId";
        r4 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r4 = r4.zzaH;	 Catch:{ all -> 0x0199 }
        r4 = com.google.android.gms.internal.zzcfl.zzdZ(r4);	 Catch:{ all -> 0x0199 }
        r2.zzj(r3, r4);	 Catch:{ all -> 0x0199 }
    L_0x05c1:
        r4 = r13;
        goto L_0x0588;
    L_0x05c3:
        r2 = r15.zztH;	 Catch:{ all -> 0x0199 }
        r2 = r2.size();	 Catch:{ all -> 0x0199 }
        if (r12 >= r2) goto L_0x05d9;
    L_0x05cb:
        r0 = r16;
        r2 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r2 = java.util.Arrays.copyOf(r2, r12);	 Catch:{ all -> 0x0199 }
        r2 = (com.google.android.gms.internal.zzcjw[]) r2;	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvE = r2;	 Catch:{ all -> 0x0199 }
    L_0x05d9:
        r2 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzaH;	 Catch:{ all -> 0x0199 }
        r3 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r3 = r3.zzbvF;	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r4 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r0 = r19;
        r2 = r0.zza(r2, r3, r4);	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvX = r2;	 Catch:{ all -> 0x0199 }
        r2 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r2 = java.lang.Long.valueOf(r2);	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvH = r2;	 Catch:{ all -> 0x0199 }
        r2 = -9223372036854775808;
        r2 = java.lang.Long.valueOf(r2);	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvI = r2;	 Catch:{ all -> 0x0199 }
        r2 = 0;
    L_0x0607:
        r0 = r16;
        r3 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r3 = r3.length;	 Catch:{ all -> 0x0199 }
        if (r2 >= r3) goto L_0x0647;
    L_0x060e:
        r0 = r16;
        r3 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r3 = r3[r2];	 Catch:{ all -> 0x0199 }
        r4 = r3.zzbvx;	 Catch:{ all -> 0x0199 }
        r4 = r4.longValue();	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r6 = r0.zzbvH;	 Catch:{ all -> 0x0199 }
        r6 = r6.longValue();	 Catch:{ all -> 0x0199 }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x062c;
    L_0x0626:
        r4 = r3.zzbvx;	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvH = r4;	 Catch:{ all -> 0x0199 }
    L_0x062c:
        r4 = r3.zzbvx;	 Catch:{ all -> 0x0199 }
        r4 = r4.longValue();	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r6 = r0.zzbvI;	 Catch:{ all -> 0x0199 }
        r6 = r6.longValue();	 Catch:{ all -> 0x0199 }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 <= 0) goto L_0x0644;
    L_0x063e:
        r3 = r3.zzbvx;	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvI = r3;	 Catch:{ all -> 0x0199 }
    L_0x0644:
        r2 = r2 + 1;
        goto L_0x0607;
    L_0x0647:
        r2 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r6 = r2.zzaH;	 Catch:{ all -> 0x0199 }
        r2 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r7 = r2.zzdQ(r6);	 Catch:{ all -> 0x0199 }
        if (r7 != 0) goto L_0x06d9;
    L_0x0655:
        r2 = r19.zzwF();	 Catch:{ all -> 0x0199 }
        r2 = r2.zzyx();	 Catch:{ all -> 0x0199 }
        r3 = "Bundling raw events w/o app info. appId";
        r4 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r4 = r4.zzaH;	 Catch:{ all -> 0x0199 }
        r4 = com.google.android.gms.internal.zzcfl.zzdZ(r4);	 Catch:{ all -> 0x0199 }
        r2.zzj(r3, r4);	 Catch:{ all -> 0x0199 }
    L_0x066a:
        r0 = r16;
        r2 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r2 = r2.length;	 Catch:{ all -> 0x0199 }
        if (r2 <= 0) goto L_0x06a3;
    L_0x0671:
        com.google.android.gms.internal.zzcem.zzxE();	 Catch:{ all -> 0x0199 }
        r2 = r19.zzwC();	 Catch:{ all -> 0x0199 }
        r3 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r3 = r3.zzaH;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzeh(r3);	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x0686;
    L_0x0682:
        r3 = r2.zzbvl;	 Catch:{ all -> 0x0199 }
        if (r3 != 0) goto L_0x075c;
    L_0x0686:
        r2 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r2 = r2.zzboQ;	 Catch:{ all -> 0x0199 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ all -> 0x0199 }
        if (r2 == 0) goto L_0x0745;
    L_0x0690:
        r2 = -1;
        r2 = java.lang.Long.valueOf(r2);	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbwb = r2;	 Catch:{ all -> 0x0199 }
    L_0x069a:
        r2 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r2.zza(r0, r13);	 Catch:{ all -> 0x0199 }
    L_0x06a3:
        r2 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r3 = r15.zzbta;	 Catch:{ all -> 0x0199 }
        r2.zzG(r3);	 Catch:{ all -> 0x0199 }
        r3 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r2 = r3.getWritableDatabase();	 Catch:{ all -> 0x0199 }
        r4 = "delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)";
        r5 = 2;
        r5 = new java.lang.String[r5];	 Catch:{ SQLiteException -> 0x0764 }
        r7 = 0;
        r5[r7] = r6;	 Catch:{ SQLiteException -> 0x0764 }
        r7 = 1;
        r5[r7] = r6;	 Catch:{ SQLiteException -> 0x0764 }
        r2.execSQL(r4, r5);	 Catch:{ SQLiteException -> 0x0764 }
    L_0x06c2:
        r2 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r2.setTransactionSuccessful();	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r2 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r2 = r2.length;	 Catch:{ all -> 0x0199 }
        if (r2 <= 0) goto L_0x0778;
    L_0x06d0:
        r2 = 1;
    L_0x06d1:
        r3 = r19.zzwz();
        r3.endTransaction();
    L_0x06d8:
        return r2;
    L_0x06d9:
        r0 = r16;
        r2 = r0.zzbvE;	 Catch:{ all -> 0x0199 }
        r2 = r2.length;	 Catch:{ all -> 0x0199 }
        if (r2 <= 0) goto L_0x066a;
    L_0x06e0:
        r2 = r7.zzwM();	 Catch:{ all -> 0x0199 }
        r4 = 0;
        r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r4 == 0) goto L_0x0741;
    L_0x06ea:
        r4 = java.lang.Long.valueOf(r2);	 Catch:{ all -> 0x0199 }
    L_0x06ee:
        r0 = r16;
        r0.zzbvK = r4;	 Catch:{ all -> 0x0199 }
        r4 = r7.zzwL();	 Catch:{ all -> 0x0199 }
        r8 = 0;
        r8 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
        if (r8 != 0) goto L_0x0797;
    L_0x06fc:
        r4 = 0;
        r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r4 == 0) goto L_0x0743;
    L_0x0702:
        r2 = java.lang.Long.valueOf(r2);	 Catch:{ all -> 0x0199 }
    L_0x0706:
        r0 = r16;
        r0.zzbvJ = r2;	 Catch:{ all -> 0x0199 }
        r7.zzwV();	 Catch:{ all -> 0x0199 }
        r2 = r7.zzwS();	 Catch:{ all -> 0x0199 }
        r2 = (int) r2;	 Catch:{ all -> 0x0199 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbvV = r2;	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r2 = r0.zzbvH;	 Catch:{ all -> 0x0199 }
        r2 = r2.longValue();	 Catch:{ all -> 0x0199 }
        r7.zzL(r2);	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r2 = r0.zzbvI;	 Catch:{ all -> 0x0199 }
        r2 = r2.longValue();	 Catch:{ all -> 0x0199 }
        r7.zzM(r2);	 Catch:{ all -> 0x0199 }
        r2 = r7.zzxd();	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzboU = r2;	 Catch:{ all -> 0x0199 }
        r2 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r2.zza(r7);	 Catch:{ all -> 0x0199 }
        goto L_0x066a;
    L_0x0741:
        r4 = 0;
        goto L_0x06ee;
    L_0x0743:
        r2 = 0;
        goto L_0x0706;
    L_0x0745:
        r2 = r19.zzwF();	 Catch:{ all -> 0x0199 }
        r2 = r2.zzyz();	 Catch:{ all -> 0x0199 }
        r3 = "Did not find measurement config or missing version info. appId";
        r4 = r15.zzbsZ;	 Catch:{ all -> 0x0199 }
        r4 = r4.zzaH;	 Catch:{ all -> 0x0199 }
        r4 = com.google.android.gms.internal.zzcfl.zzdZ(r4);	 Catch:{ all -> 0x0199 }
        r2.zzj(r3, r4);	 Catch:{ all -> 0x0199 }
        goto L_0x069a;
    L_0x075c:
        r2 = r2.zzbvl;	 Catch:{ all -> 0x0199 }
        r0 = r16;
        r0.zzbwb = r2;	 Catch:{ all -> 0x0199 }
        goto L_0x069a;
    L_0x0764:
        r2 = move-exception;
        r3 = r3.zzwF();	 Catch:{ all -> 0x0199 }
        r3 = r3.zzyx();	 Catch:{ all -> 0x0199 }
        r4 = "Failed to remove unused event metadata. appId";
        r5 = com.google.android.gms.internal.zzcfl.zzdZ(r6);	 Catch:{ all -> 0x0199 }
        r3.zze(r4, r5, r2);	 Catch:{ all -> 0x0199 }
        goto L_0x06c2;
    L_0x0778:
        r2 = 0;
        goto L_0x06d1;
    L_0x077b:
        r2 = r19.zzwz();	 Catch:{ all -> 0x0199 }
        r2.setTransactionSuccessful();	 Catch:{ all -> 0x0199 }
        r2 = r19.zzwz();
        r2.endTransaction();
        r2 = 0;
        goto L_0x06d8;
    L_0x078c:
        r2 = move-exception;
        r3 = r11;
        goto L_0x0329;
    L_0x0790:
        r2 = move-exception;
        goto L_0x02bb;
    L_0x0793:
        r2 = move-exception;
        r4 = r12;
        goto L_0x02bb;
    L_0x0797:
        r2 = r4;
        goto L_0x06fc;
    L_0x079a:
        r3 = r5;
        goto L_0x0580;
    L_0x079d:
        r3 = r4;
        r4 = r5;
        goto L_0x054a;
    L_0x07a1:
        r13 = r2;
        goto L_0x04db;
    L_0x07a4:
        r2 = r4;
        goto L_0x03a1;
    L_0x07a7:
        r2 = r12;
        r3 = r13;
        goto L_0x0134;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcgl.zzg$505cfb57(long):boolean");
    }

    static void zzwo() {
        zzcem.zzxE();
        throw new IllegalStateException("Unexpected call on client side");
    }

    private final zzcjg zzyW() {
        zza(this.zzbsG);
        return this.zzbsG;
    }

    private final boolean zzyX() {
        zzwE().zzjC();
        try {
            this.zzbsN = new RandomAccessFile(new File(this.mContext.getFilesDir(), zzcem.zzxC()), "rw").getChannel();
            this.zzbsM = this.zzbsN.tryLock();
            if (this.zzbsM != null) {
                zzwF().zzyD().log("Storage concurrent access okay");
                return true;
            }
            zzwF().zzyx().log("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            zzwF().zzyx().zzj("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            zzwF().zzyx().zzj("Failed to access storage lock file", e2);
        }
    }

    private final long zzyZ() {
        long currentTimeMillis = this.zzvw.currentTimeMillis();
        zzcfw zzwG = zzwG();
        zzwG.zzkD();
        zzwG.zzjC();
        long j = zzwG.zzbro.get();
        if (j == 0) {
            j = (long) (zzwG.zzwB().zzzt().nextInt(86400000) + 1);
            zzwG.zzbro.set(j);
        }
        return ((((j + currentTimeMillis) / 1000) / 60) / 60) / 24;
    }

    private final boolean zzzb() {
        zzwE().zzjC();
        return zzwz().zzyh() || !TextUtils.isEmpty(zzwz().zzyc());
    }

    private final void zzzc() {
        zzwE().zzjC();
        if (zzzf()) {
            long abs;
            if (this.zzbsT > 0) {
                abs = 3600000 - Math.abs(this.zzvw.elapsedRealtime() - this.zzbsT);
                if (abs > 0) {
                    zzwF().zzyD().zzj("Upload has been suspended. Will update scheduling later in approximately ms", Long.valueOf(abs));
                    this.zzbsF.unregister();
                    zzyW().cancel();
                    return;
                }
                this.zzbsT = 0;
            }
            if (zzyP() && zzzb()) {
                Object obj;
                long currentTimeMillis = this.zzvw.currentTimeMillis();
                long zzxX = zzcem.zzxX();
                if (zzwz().zzyi() || zzwz().zzyd()) {
                    obj = 1;
                } else {
                    obj = null;
                }
                if (obj != null) {
                    CharSequence zzya = this.zzbsn.zzya();
                    abs = (TextUtils.isEmpty(zzya) || ".none.".equals(zzya)) ? zzcem.zzxS() : zzcem.zzxT();
                } else {
                    abs = zzcem.zzxR();
                }
                long j = zzwG().zzbrk.get();
                long j2 = zzwG().zzbrl.get();
                long max = Math.max(zzwz().zzyf(), zzwz().zzyg());
                if (max == 0) {
                    abs = 0;
                } else {
                    max = currentTimeMillis - Math.abs(max - currentTimeMillis);
                    j2 = currentTimeMillis - Math.abs(j2 - currentTimeMillis);
                    j = Math.max(currentTimeMillis - Math.abs(j - currentTimeMillis), j2);
                    currentTimeMillis = max + zzxX;
                    if (obj != null && j > 0) {
                        currentTimeMillis = Math.min(max, j) + abs;
                    }
                    if (!zzwB().zzf(j, abs)) {
                        currentTimeMillis = j + abs;
                    }
                    if (j2 == 0 || j2 < max) {
                        abs = currentTimeMillis;
                    } else {
                        for (int i = 0; i < zzcem.zzxZ(); i++) {
                            currentTimeMillis += ((long) (1 << i)) * zzcem.zzxY();
                            if (currentTimeMillis > j2) {
                                abs = currentTimeMillis;
                                break;
                            }
                        }
                        abs = 0;
                    }
                }
                if (abs == 0) {
                    zzwF().zzyD().log("Next upload time is 0");
                    this.zzbsF.unregister();
                    zzyW().cancel();
                    return;
                } else if (zzyU().zzlQ()) {
                    currentTimeMillis = zzwG().zzbrm.get();
                    long zzxQ = zzcem.zzxQ();
                    if (!zzwB().zzf(currentTimeMillis, zzxQ)) {
                        abs = Math.max(abs, currentTimeMillis + zzxQ);
                    }
                    this.zzbsF.unregister();
                    abs -= this.zzvw.currentTimeMillis();
                    if (abs <= 0) {
                        abs = zzcem.zzxU();
                        zzwG().zzbrk.set(this.zzvw.currentTimeMillis());
                    }
                    zzwF().zzyD().zzj("Upload scheduled in approximately ms", Long.valueOf(abs));
                    zzyW().zzs(abs);
                    return;
                } else {
                    zzwF().zzyD().log("No network");
                    this.zzbsF.zzlN();
                    zzyW().cancel();
                    return;
                }
            }
            zzwF().zzyD().log("Nothing to upload or uploading impossible");
            this.zzbsF.unregister();
            zzyW().cancel();
        }
    }

    private final boolean zzzf() {
        zzwE().zzjC();
        return this.zzbsJ;
    }

    private final void zzzg() {
        zzwE().zzjC();
        if (this.zzbsU || this.zzbsV || this.zzbsW) {
            zzwF().zzyD().zzd("Not stopping services. fetch, network, upload", Boolean.valueOf(this.zzbsU), Boolean.valueOf(this.zzbsV), Boolean.valueOf(this.zzbsW));
            return;
        }
        zzwF().zzyD().log("Stopping uploading service(s)");
        if (this.zzbsP != null) {
            for (Runnable run : this.zzbsP) {
                run.run();
            }
            this.zzbsP.clear();
        }
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final boolean isEnabled() {
        boolean z = false;
        zzwE().zzjC();
        if (this.zzbsn.zzxF()) {
            return false;
        }
        Boolean zzdN = this.zzbsn.zzdN("firebase_analytics_collection_enabled");
        if (zzdN != null) {
            z = zzdN.booleanValue();
        } else if (!zzcem.zzqB()) {
            z = true;
        }
        return zzwG().zzal(z);
    }

    protected final void start() {
        zzwE().zzjC();
        zzwz().zzye();
        if (zzwG().zzbrk.get() == 0) {
            zzwG().zzbrk.set(this.zzvw.currentTimeMillis());
        }
        if (Long.valueOf(zzwG().zzbrp.get()).longValue() == 0) {
            zzwF().zzyD().zzj("Persisting first open", Long.valueOf(this.zzbsX));
            zzwG().zzbrp.set(this.zzbsX);
        }
        if (zzyP()) {
            zzcem.zzxE();
            if (!TextUtils.isEmpty(zzwu().getGmpAppId())) {
                String zzyG = zzwG().zzyG();
                if (zzyG == null) {
                    zzwG().zzed(zzwu().getGmpAppId());
                } else if (!zzyG.equals(zzwu().getGmpAppId())) {
                    zzwF().zzyB().log("Rechecking which service to use due to a GMP App Id change");
                    zzwG().zzyJ();
                    this.zzbsB.disconnect();
                    this.zzbsB.zzla();
                    zzwG().zzed(zzwu().getGmpAppId());
                    zzwG().zzbrp.set(this.zzbsX);
                    zzwG().zzbrq.zzef(null);
                }
            }
            zzwt().zzee(zzwG().zzbrq.zzyL());
            zzcem.zzxE();
            if (!TextUtils.isEmpty(zzwu().getGmpAppId())) {
                zzchl zzwt = zzwt();
                zzwt.zzjC();
                zzwt.zzwp();
                zzwt.zzkD();
                if (zzwt.zzboe.zzyP()) {
                    zzwt.zzww().zzzk();
                    String zzyK = zzwt.zzwG().zzyK();
                    if (!TextUtils.isEmpty(zzyK)) {
                        zzwt.zzwv().zzkD();
                        if (!zzyK.equals(VERSION.RELEASE)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("_po", zzyK);
                            zzwt.zzd("auto", "_ou", bundle);
                        }
                    }
                }
                zzww().zza(new AtomicReference());
            }
        } else if (isEnabled()) {
            if (!zzwB().zzbv("android.permission.INTERNET")) {
                zzwF().zzyx().log("App is missing INTERNET permission");
            }
            if (!zzwB().zzbv("android.permission.ACCESS_NETWORK_STATE")) {
                zzwF().zzyx().log("App is missing ACCESS_NETWORK_STATE permission");
            }
            zzcem.zzxE();
            if (!zzbha.zzaP(this.mContext).zzsl()) {
                if (!zzcgc.zzj$1a552345(this.mContext)) {
                    zzwF().zzyx().log("AppMeasurementReceiver not registered/enabled");
                }
                if (!zzciw.zzk$1a552345(this.mContext)) {
                    zzwF().zzyx().log("AppMeasurementService not registered/enabled");
                }
            }
            zzwF().zzyx().log("Uploading is not possible. App measurement disabled");
        }
        zzzc();
    }

    protected final void zza(int i, Throwable th, byte[] bArr) {
        zzcen zzwz;
        zzwE().zzjC();
        if (bArr == null) {
            try {
                bArr = new byte[0];
            } catch (Throwable th2) {
                this.zzbsV = false;
                zzzg();
            }
        }
        List<Long> list = this.zzbsO;
        this.zzbsO = null;
        if ((i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204) && th == null) {
            try {
                zzwG().zzbrk.set(this.zzvw.currentTimeMillis());
                zzwG().zzbrl.set(0);
                zzzc();
                zzwF().zzyD().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                zzwz().beginTransaction();
                try {
                    for (Long l : list) {
                        zzwz = zzwz();
                        long longValue = l.longValue();
                        zzwz.zzjC();
                        zzwz.zzkD();
                        if (zzwz.getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(longValue)}) != 1) {
                            throw new SQLiteException("Deleted fewer rows from queue than expected");
                        }
                    }
                    zzwz().setTransactionSuccessful();
                    zzwz().endTransaction();
                    if (zzyU().zzlQ() && zzzb()) {
                        zzza();
                    } else {
                        this.zzbsS = -1;
                        zzzc();
                    }
                    this.zzbsT = 0;
                } catch (SQLiteException e) {
                    zzwz.zzwF().zzyx().zzj("Failed to delete a bundle in a queue table", e);
                    throw e;
                } catch (Throwable th3) {
                    zzwz().endTransaction();
                }
            } catch (SQLiteException e2) {
                zzwF().zzyx().zzj("Database error while trying to delete uploaded bundles", e2);
                this.zzbsT = this.zzvw.elapsedRealtime();
                zzwF().zzyD().zzj("Disable upload, time", Long.valueOf(this.zzbsT));
            }
        } else {
            zzwF().zzyD().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
            zzwG().zzbrl.set(this.zzvw.currentTimeMillis());
            boolean z = i == 503 || i == 429;
            if (z) {
                zzwG().zzbrm.set(this.zzvw.currentTimeMillis());
            }
            zzzc();
        }
        this.zzbsV = false;
        zzzg();
    }

    public final byte[] zza(zzcez zzcez, String str) {
        zzwE().zzjC();
        zzwo();
        zzbo.zzu(zzcez);
        zzbo.zzcF(str);
        zzcjy zzcjy = new zzcjy();
        zzwz().beginTransaction();
        try {
            zzceg zzdQ = zzwz().zzdQ(str);
            byte[] bArr;
            if (zzdQ == null) {
                zzwF().zzyC().zzj("Log and bundle not available. package_name", str);
                bArr = new byte[0];
                return bArr;
            } else if (zzdQ.zzwR()) {
                long j;
                zzcjz zzcjz = new zzcjz();
                zzcjy.zzbvB = new zzcjz[]{zzcjz};
                zzcjz.zzbvD = Integer.valueOf(1);
                zzcjz.zzbvL = "android";
                zzcjz.zzaH = zzdQ.zzhl();
                zzcjz.zzboR = zzdQ.zzwO();
                zzcjz.zzbgW = zzdQ.zzjH();
                long zzwN = zzdQ.zzwN();
                zzcjz.zzbvY = zzwN == -2147483648L ? null : Integer.valueOf((int) zzwN);
                zzcjz.zzbvP = Long.valueOf(zzdQ.zzwP());
                zzcjz.zzboQ = zzdQ.getGmpAppId();
                zzcjz.zzbvU = Long.valueOf(zzdQ.zzwQ());
                if (isEnabled() && zzcem.zzyb() && this.zzbsn.zzdO(zzcjz.zzaH)) {
                    zzwu();
                    zzcjz.zzbwd = null;
                }
                Pair zzeb = zzwG().zzeb(zzdQ.zzhl());
                if (!TextUtils.isEmpty((CharSequence) zzeb.first)) {
                    zzcjz.zzbvR = (String) zzeb.first;
                    zzcjz.zzbvS = (Boolean) zzeb.second;
                }
                zzwv().zzkD();
                zzcjz.zzbvM = Build.MODEL;
                zzwv().zzkD();
                zzcjz.zzaY = VERSION.RELEASE;
                zzcjz.zzbvO = Integer.valueOf((int) zzwv().zzyq());
                zzcjz.zzbvN = zzwv().zzyr();
                zzcjz.zzbvT = zzdQ.getAppInstanceId();
                zzcjz.zzboY = zzdQ.zzwK();
                List zzdP = zzwz().zzdP(zzdQ.zzhl());
                zzcjz.zzbvF = new zzckb[zzdP.size()];
                for (int i = 0; i < zzdP.size(); i++) {
                    zzckb zzckb = new zzckb();
                    zzcjz.zzbvF[i] = zzckb;
                    zzckb.name = ((zzcjk) zzdP.get(i)).mName;
                    zzckb.zzbwh = Long.valueOf(((zzcjk) zzdP.get(i)).zzbuC);
                    zzwB().zza(zzckb, ((zzcjk) zzdP.get(i)).mValue);
                }
                Bundle zzyt = zzcez.zzbpM.zzyt();
                if ("_iap".equals(zzcez.name)) {
                    zzyt.putLong("_c", 1);
                    zzwF().zzyC().log("Marking in-app purchase as real-time");
                    zzyt.putLong("_r", 1);
                }
                zzyt.putString("_o", zzcez.zzbpc);
                if (zzwB().zzey(zzcjz.zzaH)) {
                    zzwB().zza(zzyt, "_dbg", Long.valueOf(1));
                    zzwB().zza(zzyt, "_r", Long.valueOf(1));
                }
                zzcev zzE = zzwz().zzE(str, zzcez.name);
                if (zzE == null) {
                    zzwz().zza(new zzcev(str, zzcez.name, 1, 0, zzcez.zzbpN));
                    j = 0;
                } else {
                    j = zzE.zzbpI;
                    zzwz().zza(zzE.zzab(zzcez.zzbpN).zzys());
                }
                zzceu zzceu = new zzceu(this, zzcez.zzbpc, str, zzcez.name, zzcez.zzbpN, j, zzyt);
                zzcjw zzcjw = new zzcjw();
                zzcjz.zzbvE = new zzcjw[]{zzcjw};
                zzcjw.zzbvx = Long.valueOf(zzceu.zzayS);
                zzcjw.name = zzceu.mName;
                zzcjw.zzbvy = Long.valueOf(zzceu.zzbpE);
                zzcjw.zzbvw = new zzcjx[zzceu.zzbpF.size()];
                Iterator it = zzceu.zzbpF.iterator();
                int i2 = 0;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    zzcjx zzcjx = new zzcjx();
                    int i3 = i2 + 1;
                    zzcjw.zzbvw[i2] = zzcjx;
                    zzcjx.name = str2;
                    zzwB().zza(zzcjx, zzceu.zzbpF.get(str2));
                    i2 = i3;
                }
                zzcjz.zzbvX = zza(zzdQ.zzhl(), zzcjz.zzbvF, zzcjz.zzbvE);
                zzcjz.zzbvH = zzcjw.zzbvx;
                zzcjz.zzbvI = zzcjw.zzbvx;
                zzwN = zzdQ.zzwM();
                zzcjz.zzbvK = zzwN != 0 ? Long.valueOf(zzwN) : null;
                long zzwL = zzdQ.zzwL();
                if (zzwL != 0) {
                    zzwN = zzwL;
                }
                zzcjz.zzbvJ = zzwN != 0 ? Long.valueOf(zzwN) : null;
                zzdQ.zzwV();
                zzcjz.zzbvV = Integer.valueOf((int) zzdQ.zzwS());
                zzcjz.zzbvQ = Long.valueOf(zzcem.zzwP());
                zzcjz.zzbvG = Long.valueOf(this.zzvw.currentTimeMillis());
                zzcjz.zzbvW = Boolean.TRUE;
                zzdQ.zzL(zzcjz.zzbvH.longValue());
                zzdQ.zzM(zzcjz.zzbvI.longValue());
                zzwz().zza(zzdQ);
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                try {
                    bArr = new byte[zzcjy.zzLW()];
                    adh zzc$2f392430 = adh.zzc$2f392430(bArr, bArr.length);
                    zzcjy.zza(zzc$2f392430);
                    zzc$2f392430.zzLN();
                    return zzwB().zzl(bArr);
                } catch (IOException e) {
                    zzwF().zzyx().zze("Data loss. Failed to bundle and serialize. appId", zzcfl.zzdZ(str), e);
                    return null;
                }
            } else {
                zzwF().zzyC().zzj("Log and bundle disabled. package_name", str);
                bArr = new byte[0];
                zzwz().endTransaction();
                return bArr;
            }
        } finally {
            zzwz().endTransaction();
        }
    }

    public final void zzam$1385ff() {
        zzzc();
    }

    final void zzb(zzcek zzcek, zzceh zzceh) {
        boolean z = true;
        zzbo.zzu(zzcek);
        zzbo.zzcF(zzcek.packageName);
        zzbo.zzu(zzcek.zzbpc);
        zzbo.zzu(zzcek.zzbpd);
        zzbo.zzcF(zzcek.zzbpd.name);
        zzwE().zzjC();
        if (!TextUtils.isEmpty(zzceh.zzboQ)) {
            if (zzceh.zzboV) {
                zzcek zzcek2 = new zzcek(zzcek);
                zzcek2.zzbpf = false;
                zzwz().beginTransaction();
                try {
                    zzcek zzH = zzwz().zzH(zzcek2.packageName, zzcek2.zzbpd.name);
                    if (!(zzH == null || zzH.zzbpc.equals(zzcek2.zzbpc))) {
                        zzwF().zzyz().zzd("Updating a conditional user property with different origin. name, origin, origin (from DB)", zzwA().zzdY(zzcek2.zzbpd.name), zzcek2.zzbpc, zzH.zzbpc);
                    }
                    if (zzH != null && zzH.zzbpf) {
                        zzcek2.zzbpc = zzH.zzbpc;
                        zzcek2.zzbpe = zzH.zzbpe;
                        zzcek2.zzbpi = zzH.zzbpi;
                        zzcek2.zzbpg = zzH.zzbpg;
                        zzcek2.zzbpj = zzH.zzbpj;
                        zzcek2.zzbpf = zzH.zzbpf;
                        zzcek2.zzbpd = new zzcji(zzcek2.zzbpd.name, zzH.zzbpd.zzbuy, zzcek2.zzbpd.getValue(), zzH.zzbpd.zzbpc);
                        z = false;
                    } else if (TextUtils.isEmpty(zzcek2.zzbpg)) {
                        zzcek2.zzbpd = new zzcji(zzcek2.zzbpd.name, zzcek2.zzbpe, zzcek2.zzbpd.getValue(), zzcek2.zzbpd.zzbpc);
                        zzcek2.zzbpf = true;
                    } else {
                        z = false;
                    }
                    if (zzcek2.zzbpf) {
                        zzcji zzcji = zzcek2.zzbpd;
                        zzcjk zzcjk = new zzcjk(zzcek2.packageName, zzcek2.zzbpc, zzcji.name, zzcji.zzbuy, zzcji.getValue());
                        if (zzwz().zza(zzcjk)) {
                            zzwF().zzyC().zzd("User property updated immediately", zzcek2.packageName, zzwA().zzdY(zzcjk.mName), zzcjk.mValue);
                        } else {
                            zzwF().zzyx().zzd("(2)Too many active user properties, ignoring", zzcfl.zzdZ(zzcek2.packageName), zzwA().zzdY(zzcjk.mName), zzcjk.mValue);
                        }
                        if (z && zzcek2.zzbpj != null) {
                            zzc(new zzcez(zzcek2.zzbpj, zzcek2.zzbpe), zzceh);
                        }
                    }
                    if (zzwz().zza(zzcek2)) {
                        zzwF().zzyC().zzd("Conditional property added", zzcek2.packageName, zzwA().zzdY(zzcek2.zzbpd.name), zzcek2.zzbpd.getValue());
                    } else {
                        zzwF().zzyx().zzd("Too many conditional properties, ignoring", zzcfl.zzdZ(zzcek2.packageName), zzwA().zzdY(zzcek2.zzbpd.name), zzcek2.zzbpd.getValue());
                    }
                    zzwz().setTransactionSuccessful();
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(zzceh);
            }
        }
    }

    final void zzb(zzcez zzcez, zzceh zzceh) {
        zzbo.zzu(zzceh);
        zzbo.zzcF(zzceh.packageName);
        zzwE().zzjC();
        String str = zzceh.packageName;
        long j = zzcez.zzbpN;
        zzwB();
        if (!zzcjl.zzd(zzcez, zzceh)) {
            return;
        }
        if (zzceh.zzboV) {
            zzwz().beginTransaction();
            try {
                List emptyList;
                Object obj;
                zzcen zzwz = zzwz();
                zzbo.zzcF(str);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zze("Invalid time querying timed out conditional properties", zzcfl.zzdZ(str), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active=0 and app_id=? and abs(? - creation_timestamp) > trigger_timeout", new String[]{str, String.valueOf(j)});
                }
                for (zzcek zzcek : r2) {
                    if (zzcek != null) {
                        zzwF().zzyC().zzd("User property timed out", zzcek.packageName, zzwA().zzdY(zzcek.zzbpd.name), zzcek.zzbpd.getValue());
                        if (zzcek.zzbph != null) {
                            zzc(new zzcez(zzcek.zzbph, j), zzceh);
                        }
                        zzwz().zzI(str, zzcek.zzbpd.name);
                    }
                }
                zzwz = zzwz();
                zzbo.zzcF(str);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zze("Invalid time querying expired conditional properties", zzcfl.zzdZ(str), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active<>0 and app_id=? and abs(? - triggered_timestamp) > time_to_live", new String[]{str, String.valueOf(j)});
                }
                List arrayList = new ArrayList(r2.size());
                for (zzcek zzcek2 : r2) {
                    if (zzcek2 != null) {
                        zzwF().zzyC().zzd("User property expired", zzcek2.packageName, zzwA().zzdY(zzcek2.zzbpd.name), zzcek2.zzbpd.getValue());
                        zzwz().zzF(str, zzcek2.zzbpd.name);
                        if (zzcek2.zzbpl != null) {
                            arrayList.add(zzcek2.zzbpl);
                        }
                        zzwz().zzI(str, zzcek2.zzbpd.name);
                    }
                }
                ArrayList arrayList2 = (ArrayList) arrayList;
                int size = arrayList2.size();
                int i = 0;
                while (i < size) {
                    obj = arrayList2.get(i);
                    i++;
                    zzc(new zzcez((zzcez) obj, j), zzceh);
                }
                zzwz = zzwz();
                String str2 = zzcez.name;
                zzbo.zzcF(str);
                zzbo.zzcF(str2);
                zzwz.zzjC();
                zzwz.zzkD();
                if (j < 0) {
                    zzwz.zzwF().zzyz().zzd("Invalid time querying triggered conditional properties", zzcfl.zzdZ(str), zzwz.zzwA().zzdW(str2), Long.valueOf(j));
                    emptyList = Collections.emptyList();
                } else {
                    emptyList = zzwz.zzc("active=0 and app_id=? and trigger_event_name=? and abs(? - creation_timestamp) <= trigger_timeout", new String[]{str, str2, String.valueOf(j)});
                }
                List arrayList3 = new ArrayList(r2.size());
                for (zzcek zzcek3 : r2) {
                    if (zzcek3 != null) {
                        zzcji zzcji = zzcek3.zzbpd;
                        zzcjk zzcjk = new zzcjk(zzcek3.packageName, zzcek3.zzbpc, zzcji.name, j, zzcji.getValue());
                        if (zzwz().zza(zzcjk)) {
                            zzwF().zzyC().zzd("User property triggered", zzcek3.packageName, zzwA().zzdY(zzcjk.mName), zzcjk.mValue);
                        } else {
                            zzwF().zzyx().zzd("Too many active user properties, ignoring", zzcfl.zzdZ(zzcek3.packageName), zzwA().zzdY(zzcjk.mName), zzcjk.mValue);
                        }
                        if (zzcek3.zzbpj != null) {
                            arrayList3.add(zzcek3.zzbpj);
                        }
                        zzcek3.zzbpd = new zzcji(zzcjk);
                        zzcek3.zzbpf = true;
                        zzwz().zza(zzcek3);
                    }
                }
                zzc(zzcez, zzceh);
                arrayList2 = (ArrayList) arrayList3;
                int size2 = arrayList2.size();
                i = 0;
                while (i < size2) {
                    obj = arrayList2.get(i);
                    i++;
                    zzc(new zzcez((zzcez) obj, j), zzceh);
                }
                zzwz().setTransactionSuccessful();
            } finally {
                zzwz().endTransaction();
            }
        } else {
            zzf(zzceh);
        }
    }

    final void zzb(zzcez zzcez, String str) {
        zzceg zzdQ = zzwz().zzdQ(str);
        if (zzdQ == null || TextUtils.isEmpty(zzdQ.zzjH())) {
            zzwF().zzyC().zzj("No app data available; dropping event", str);
            return;
        }
        try {
            String str2 = zzbha.zzaP(this.mContext).getPackageInfo(str, 0).versionName;
            if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(str2))) {
                zzwF().zzyz().zzj("App version does not match; dropping event. appId", zzcfl.zzdZ(str));
                return;
            }
        } catch (NameNotFoundException e) {
            if (!"_ui".equals(zzcez.name)) {
                zzwF().zzyz().zzj("Could not find package. appId", zzcfl.zzdZ(str));
            }
        }
        zzcez zzcez2 = zzcez;
        zzb(zzcez2, new zzceh(str, zzdQ.getGmpAppId(), zzdQ.zzjH(), zzdQ.zzwN(), zzdQ.zzwO(), zzdQ.zzwP(), zzdQ.zzwQ(), null, zzdQ.zzwR(), false, zzdQ.zzwK(), zzdQ.zzxe(), 0, 0));
    }

    final void zzb(zzcji zzcji, zzceh zzceh) {
        int i = 0;
        zzwE().zzjC();
        if (!TextUtils.isEmpty(zzceh.zzboQ)) {
            if (zzceh.zzboV) {
                int zzes = zzwB().zzes(zzcji.name);
                String zza;
                zzcjl zzwB;
                String str;
                if (zzes != 0) {
                    zzwB();
                    zza = zzcjl.zza(zzcji.name, zzcem.zzxi(), true);
                    if (zzcji.name != null) {
                        i = zzcji.name.length();
                    }
                    zzwB = zzwB();
                    str = zzceh.packageName;
                    zzwB.zza$2c2ba1f5(zzes, "_ev", zza, i);
                    return;
                }
                zzes = zzwB().zzl(zzcji.name, zzcji.getValue());
                if (zzes != 0) {
                    zzwB();
                    zza = zzcjl.zza(zzcji.name, zzcem.zzxi(), true);
                    Object value = zzcji.getValue();
                    if (value != null && ((value instanceof String) || (value instanceof CharSequence))) {
                        i = String.valueOf(value).length();
                    }
                    zzwB = zzwB();
                    str = zzceh.packageName;
                    zzwB.zza$2c2ba1f5(zzes, "_ev", zza, i);
                    return;
                }
                zzwB();
                Object zzm = zzcjl.zzm(zzcji.name, zzcji.getValue());
                if (zzm != null) {
                    zzcjk zzcjk = new zzcjk(zzceh.packageName, zzcji.zzbpc, zzcji.name, zzcji.zzbuy, zzm);
                    zzwF().zzyC().zze("Setting user property", zzwA().zzdY(zzcjk.mName), zzm);
                    zzwz().beginTransaction();
                    try {
                        zzf(zzceh);
                        boolean zza2 = zzwz().zza(zzcjk);
                        zzwz().setTransactionSuccessful();
                        if (zza2) {
                            zzwF().zzyC().zze("User property set", zzwA().zzdY(zzcjk.mName), zzcjk.mValue);
                        } else {
                            zzwF().zzyx().zze("Too many unique user properties are set. Ignoring user property", zzwA().zzdY(zzcjk.mName), zzcjk.mValue);
                            zzcjl zzwB2 = zzwB();
                            String str2 = zzceh.packageName;
                            zzwB2.zza$2c2ba1f5(9, null, null, 0);
                        }
                        zzwz().endTransaction();
                        return;
                    } catch (Throwable th) {
                        zzwz().endTransaction();
                    }
                } else {
                    return;
                }
            }
            zzf(zzceh);
        }
    }

    final void zzb(String str, int i, Throwable th, byte[] bArr, Map<String, List<String>> map) {
        boolean z = true;
        zzwE().zzjC();
        zzbo.zzcF(str);
        if (bArr == null) {
            try {
                bArr = new byte[0];
            } catch (Throwable th2) {
                this.zzbsU = false;
                zzzg();
            }
        }
        zzwF().zzyD().zzj("onConfigFetched. Response size", Integer.valueOf(bArr.length));
        zzwz().beginTransaction();
        zzceg zzdQ = zzwz().zzdQ(str);
        boolean z2 = (i == Callback.DEFAULT_DRAG_ANIMATION_DURATION || i == 204 || i == 304) && th == null;
        if (zzdQ == null) {
            zzwF().zzyz().zzj("App does not exist in onConfigFetched. appId", zzcfl.zzdZ(str));
        } else if (z2 || i == 404) {
            List list = map != null ? (List) map.get("Last-Modified") : null;
            String str2 = (list == null || list.size() <= 0) ? null : (String) list.get(0);
            if (i == 404 || i == 304) {
                if (zzwC().zzeh(str) == null && !zzwC().zzb(str, null, null)) {
                    zzwz().endTransaction();
                    this.zzbsU = false;
                    zzzg();
                    return;
                }
            } else if (!zzwC().zzb(str, bArr, str2)) {
                zzwz().endTransaction();
                this.zzbsU = false;
                zzzg();
                return;
            }
            zzdQ.zzR(this.zzvw.currentTimeMillis());
            zzwz().zza(zzdQ);
            if (i == 404) {
                zzwF().zzyA().zzj("Config not found. Using empty config. appId", str);
            } else {
                zzwF().zzyD().zze("Successfully fetched config. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
            }
            if (zzyU().zzlQ() && zzzb()) {
                zzza();
            } else {
                zzzc();
            }
        } else {
            zzdQ.zzS(this.zzvw.currentTimeMillis());
            zzwz().zza(zzdQ);
            zzwF().zzyD().zze("Fetching config failed. code, error", Integer.valueOf(i), th);
            zzwC().zzej(str);
            zzwG().zzbrl.set(this.zzvw.currentTimeMillis());
            if (!(i == 503 || i == 429)) {
                z = false;
            }
            if (z) {
                zzwG().zzbrm.set(this.zzvw.currentTimeMillis());
            }
            zzzc();
        }
        zzwz().setTransactionSuccessful();
        zzwz().endTransaction();
        this.zzbsU = false;
        zzzg();
    }

    final void zzb$7115b10f() {
        this.zzbsQ++;
    }

    final void zzc(zzcek zzcek, zzceh zzceh) {
        zzbo.zzu(zzcek);
        zzbo.zzcF(zzcek.packageName);
        zzbo.zzu(zzcek.zzbpd);
        zzbo.zzcF(zzcek.zzbpd.name);
        zzwE().zzjC();
        if (!TextUtils.isEmpty(zzceh.zzboQ)) {
            if (zzceh.zzboV) {
                zzwz().beginTransaction();
                try {
                    zzf(zzceh);
                    zzcek zzH = zzwz().zzH(zzcek.packageName, zzcek.zzbpd.name);
                    if (zzH != null) {
                        zzwF().zzyC().zze("Removing conditional user property", zzcek.packageName, zzwA().zzdY(zzcek.zzbpd.name));
                        zzwz().zzI(zzcek.packageName, zzcek.zzbpd.name);
                        if (zzH.zzbpf) {
                            zzwz().zzF(zzcek.packageName, zzcek.zzbpd.name);
                        }
                        if (zzcek.zzbpl != null) {
                            Bundle bundle = null;
                            if (zzcek.zzbpl.zzbpM != null) {
                                bundle = zzcek.zzbpl.zzbpM.zzyt();
                            }
                            zzc(zzwB().zza$23a98d66(zzcek.zzbpl.name, bundle, zzH.zzbpc, zzcek.zzbpl.zzbpN), zzceh);
                        }
                    } else {
                        zzwF().zzyz().zze("Conditional user property doesn't exist", zzcfl.zzdZ(zzcek.packageName), zzwA().zzdY(zzcek.zzbpd.name));
                    }
                    zzwz().setTransactionSuccessful();
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(zzceh);
            }
        }
    }

    final void zzc(zzcji zzcji, zzceh zzceh) {
        zzwE().zzjC();
        if (!TextUtils.isEmpty(zzceh.zzboQ)) {
            if (zzceh.zzboV) {
                zzwF().zzyC().zzj("Removing user property", zzwA().zzdY(zzcji.name));
                zzwz().beginTransaction();
                try {
                    zzf(zzceh);
                    zzwz().zzF(zzceh.packageName, zzcji.name);
                    zzwz().setTransactionSuccessful();
                    zzwF().zzyC().zzj("User property removed", zzwA().zzdY(zzcji.name));
                } finally {
                    zzwz().endTransaction();
                }
            } else {
                zzf(zzceh);
            }
        }
    }

    final void zzd(zzceh zzceh) {
        zzwE().zzjC();
        zzbo.zzcF(zzceh.packageName);
        zzf(zzceh);
    }

    final void zzd(zzcek zzcek) {
        zzceh zzel = zzel(zzcek.packageName);
        if (zzel != null) {
            zzb(zzcek, zzel);
        }
    }

    public final void zze(zzceh zzceh) {
        zzwE().zzjC();
        zzbo.zzu(zzceh);
        zzbo.zzcF(zzceh.packageName);
        if (!TextUtils.isEmpty(zzceh.zzboQ)) {
            zzceg zzdQ = zzwz().zzdQ(zzceh.packageName);
            if (!(zzdQ == null || !TextUtils.isEmpty(zzdQ.getGmpAppId()) || TextUtils.isEmpty(zzceh.zzboQ))) {
                zzdQ.zzR(0);
                zzwz().zza(zzdQ);
                zzwC().zzek(zzceh.packageName);
            }
            if (zzceh.zzboV) {
                int i;
                Bundle bundle;
                long j = zzceh.zzbpa;
                if (j == 0) {
                    j = this.zzvw.currentTimeMillis();
                }
                int i2 = zzceh.zzbpb;
                if (i2 == 0 || i2 == 1) {
                    i = i2;
                } else {
                    zzwF().zzyz().zze("Incorrect app type, assuming installed app. appId, appType", zzcfl.zzdZ(zzceh.packageName), Integer.valueOf(i2));
                    i = 0;
                }
                zzwz().beginTransaction();
                zzcen zzwz;
                String zzhl;
                try {
                    zzdQ = zzwz().zzdQ(zzceh.packageName);
                    if (!(zzdQ == null || zzdQ.getGmpAppId() == null || zzdQ.getGmpAppId().equals(zzceh.zzboQ))) {
                        zzwF().zzyz().zzj("New GMP App Id passed in. Removing cached database data. appId", zzcfl.zzdZ(zzdQ.zzhl()));
                        zzwz = zzwz();
                        zzhl = zzdQ.zzhl();
                        zzwz.zzkD();
                        zzwz.zzjC();
                        zzbo.zzcF(zzhl);
                        SQLiteDatabase writableDatabase = zzwz.getWritableDatabase();
                        String[] strArr = new String[]{zzhl};
                        i2 = writableDatabase.delete("audience_filter_values", "app_id=?", strArr) + ((((((((writableDatabase.delete("events", "app_id=?", strArr) + 0) + writableDatabase.delete("user_attributes", "app_id=?", strArr)) + writableDatabase.delete("conditional_properties", "app_id=?", strArr)) + writableDatabase.delete("apps", "app_id=?", strArr)) + writableDatabase.delete("raw_events", "app_id=?", strArr)) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr)) + writableDatabase.delete("event_filters", "app_id=?", strArr)) + writableDatabase.delete("property_filters", "app_id=?", strArr));
                        if (i2 > 0) {
                            zzwz.zzwF().zzyD().zze("Deleted application data. app, records", zzhl, Integer.valueOf(i2));
                        }
                        zzdQ = null;
                    }
                } catch (SQLiteException e) {
                    zzwz.zzwF().zzyx().zze("Error deleting application data. appId, error", zzcfl.zzdZ(zzhl), e);
                } catch (Throwable th) {
                    zzwz().endTransaction();
                }
                if (zzdQ != null) {
                    if (!(zzdQ.zzjH() == null || zzdQ.zzjH().equals(zzceh.zzbgW))) {
                        bundle = new Bundle();
                        bundle.putString("_pv", zzdQ.zzjH());
                        zzb(new zzcez("_au", new zzcew(bundle), "auto", j), zzceh);
                    }
                }
                zzf(zzceh);
                zzcev zzcev = null;
                if (i == 0) {
                    zzcev = zzwz().zzE(zzceh.packageName, "_f");
                } else if (i == 1) {
                    zzcev = zzwz().zzE(zzceh.packageName, "_v");
                }
                if (zzcev == null) {
                    long j2 = (1 + (j / 3600000)) * 3600000;
                    if (i == 0) {
                        zzb(new zzcji("_fot", j, Long.valueOf(j2), "auto"), zzceh);
                        zzwE().zzjC();
                        Bundle bundle2 = new Bundle();
                        bundle2.putLong("_c", 1);
                        bundle2.putLong("_r", 1);
                        bundle2.putLong("_uwa", 0);
                        bundle2.putLong("_pfo", 0);
                        bundle2.putLong("_sys", 0);
                        bundle2.putLong("_sysu", 0);
                        if (this.mContext.getPackageManager() == null) {
                            zzwF().zzyx().zzj("PackageManager is null, first open report might be inaccurate. appId", zzcfl.zzdZ(zzceh.packageName));
                        } else {
                            ApplicationInfo applicationInfo;
                            PackageInfo packageInfo = null;
                            try {
                                packageInfo = zzbha.zzaP(this.mContext).getPackageInfo(zzceh.packageName, 0);
                            } catch (NameNotFoundException e2) {
                                zzwF().zzyx().zze("Package info is null, first open report might be inaccurate. appId", zzcfl.zzdZ(zzceh.packageName), e2);
                            }
                            if (packageInfo != null) {
                                if (packageInfo.firstInstallTime != 0) {
                                    Object obj = null;
                                    if (packageInfo.firstInstallTime != packageInfo.lastUpdateTime) {
                                        bundle2.putLong("_uwa", 1);
                                    } else {
                                        obj = 1;
                                    }
                                    zzb(new zzcji("_fi", j, Long.valueOf(obj != null ? 1 : 0), "auto"), zzceh);
                                }
                            }
                            try {
                                applicationInfo = zzbha.zzaP(this.mContext).getApplicationInfo(zzceh.packageName, 0);
                            } catch (NameNotFoundException e22) {
                                zzwF().zzyx().zze("Application info is null, first open report might be inaccurate. appId", zzcfl.zzdZ(zzceh.packageName), e22);
                                applicationInfo = null;
                            }
                            if (applicationInfo != null) {
                                if ((applicationInfo.flags & 1) != 0) {
                                    bundle2.putLong("_sys", 1);
                                }
                                if ((applicationInfo.flags & 128) != 0) {
                                    bundle2.putLong("_sysu", 1);
                                }
                            }
                        }
                        zzcen zzwz2 = zzwz();
                        String str = zzceh.packageName;
                        zzbo.zzcF(str);
                        zzwz2.zzjC();
                        zzwz2.zzkD();
                        j2 = zzwz2.zzL(str, "first_open_count");
                        if (j2 >= 0) {
                            bundle2.putLong("_pfo", j2);
                        }
                        zzb(new zzcez("_f", new zzcew(bundle2), "auto", j), zzceh);
                    } else if (i == 1) {
                        zzb(new zzcji("_fvt", j, Long.valueOf(j2), "auto"), zzceh);
                        zzwE().zzjC();
                        bundle = new Bundle();
                        bundle.putLong("_c", 1);
                        bundle.putLong("_r", 1);
                        zzb(new zzcez("_v", new zzcew(bundle), "auto", j), zzceh);
                    }
                    bundle = new Bundle();
                    bundle.putLong("_et", 1);
                    zzb(new zzcez("_e", new zzcew(bundle), "auto", j), zzceh);
                } else if (zzceh.zzboW) {
                    zzb(new zzcez("_cd", new zzcew(new Bundle()), "auto", j), zzceh);
                }
                zzwz().setTransactionSuccessful();
                zzwz().endTransaction();
                return;
            }
            zzf(zzceh);
        }
    }

    final void zze(zzcek zzcek) {
        zzceh zzel = zzel(zzcek.packageName);
        if (zzel != null) {
            zzc(zzcek, zzel);
        }
    }

    public final String zzem(String str) {
        Object e;
        try {
            return (String) zzwE().zze(new zzcgn(this, str)).get(30000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e2) {
            e = e2;
        } catch (InterruptedException e3) {
            e = e3;
        } catch (ExecutionException e4) {
            e = e4;
        }
        zzwF().zzyx().zze("Failed to get app instance id. appId", zzcfl.zzdZ(str), e);
        return null;
    }

    public final zze zzkq() {
        return this.zzvw;
    }

    final void zzl(Runnable runnable) {
        zzwE().zzjC();
        if (this.zzbsP == null) {
            this.zzbsP = new ArrayList();
        }
        this.zzbsP.add(runnable);
    }

    public final zzcfj zzwA() {
        zza(this.zzbsw);
        return this.zzbsw;
    }

    public final zzcjl zzwB() {
        zza(this.zzbsv);
        return this.zzbsv;
    }

    public final zzcgf zzwC() {
        zza(this.zzbss);
        return this.zzbss;
    }

    public final zzcja zzwD() {
        zza(this.zzbsr);
        return this.zzbsr;
    }

    public final zzcgg zzwE() {
        zza(this.zzbsq);
        return this.zzbsq;
    }

    public final zzcfl zzwF() {
        zza(this.zzbsp);
        return this.zzbsp;
    }

    public final zzcfw zzwG() {
        zza(this.zzbso);
        return this.zzbso;
    }

    public final zzcem zzwH() {
        return this.zzbsn;
    }

    public final zzcec zzwr() {
        zza(this.zzbsI);
        return this.zzbsI;
    }

    public final zzcej zzws() {
        zza(this.zzbsH);
        return this.zzbsH;
    }

    public final zzchl zzwt() {
        zza(this.zzbsD);
        return this.zzbsD;
    }

    public final zzcfg zzwu() {
        zza(this.zzbsE);
        return this.zzbsE;
    }

    public final zzcet zzwv() {
        zza(this.zzbsC);
        return this.zzbsC;
    }

    public final zzcid zzww() {
        zza(this.zzbsB);
        return this.zzbsB;
    }

    public final zzchz zzwx() {
        zza(this.zzbsA);
        return this.zzbsA;
    }

    public final zzcfh zzwy() {
        zza(this.zzbsy);
        return this.zzbsy;
    }

    public final zzcen zzwz() {
        zza(this.zzbsx);
        return this.zzbsx;
    }

    protected final boolean zzyP() {
        zzwE().zzjC();
        if (this.zzbsK == null || this.zzbsL == 0 || !(this.zzbsK == null || this.zzbsK.booleanValue() || Math.abs(this.zzvw.elapsedRealtime() - this.zzbsL) <= 1000)) {
            this.zzbsL = this.zzvw.elapsedRealtime();
            zzcem.zzxE();
            boolean z = zzwB().zzbv("android.permission.INTERNET") && zzwB().zzbv("android.permission.ACCESS_NETWORK_STATE") && (zzbha.zzaP(this.mContext).zzsl() || (zzcgc.zzj$1a552345(this.mContext) && zzciw.zzk$1a552345(this.mContext)));
            this.zzbsK = Boolean.valueOf(z);
            if (this.zzbsK.booleanValue()) {
                this.zzbsK = Boolean.valueOf(zzwB().zzev(zzwu().getGmpAppId()));
            }
        }
        return this.zzbsK.booleanValue();
    }

    public final zzcfl zzyQ() {
        return this.zzbsp.isInitialized() ? this.zzbsp : null;
    }

    final zzcgg zzyR() {
        return this.zzbsq;
    }

    public final AppMeasurement zzyS() {
        return this.zzbst;
    }

    public final FirebaseAnalytics zzyT() {
        return this.zzbsu;
    }

    public final zzcfp zzyU() {
        zza(this.zzbsz);
        return this.zzbsz;
    }

    final long zzyY() {
        Long valueOf = Long.valueOf(zzwG().zzbrp.get());
        return valueOf.longValue() == 0 ? this.zzbsX : Math.min(this.zzbsX, valueOf.longValue());
    }

    public final void zzza() {
        boolean z = true;
        zzwE().zzjC();
        this.zzbsW = true;
        String zzyc;
        String zzxO;
        try {
            zzcem.zzxE();
            Boolean zzyI = zzwG().zzyI();
            if (zzyI == null) {
                zzwF().zzyz().log("Upload data called on the client side before use of service was decided");
                this.zzbsW = false;
                zzzg();
            } else if (zzyI.booleanValue()) {
                zzwF().zzyx().log("Upload called in the client side when service should be used");
                this.zzbsW = false;
                zzzg();
            } else if (this.zzbsT > 0) {
                zzzc();
                this.zzbsW = false;
                zzzg();
            } else {
                zzwE().zzjC();
                if (this.zzbsO != null) {
                    zzwF().zzyD().log("Uploading requested multiple times");
                    this.zzbsW = false;
                    zzzg();
                } else if (zzyU().zzlQ()) {
                    long currentTimeMillis = this.zzvw.currentTimeMillis();
                    zzg$505cfb57(currentTimeMillis - zzcem.zzxP());
                    long j = zzwG().zzbrk.get();
                    if (j != 0) {
                        zzwF().zzyC().zzj("Uploading events. Elapsed time since last upload attempt (ms)", Long.valueOf(Math.abs(currentTimeMillis - j)));
                    }
                    zzyc = zzwz().zzyc();
                    Object zzaa;
                    if (TextUtils.isEmpty(zzyc)) {
                        this.zzbsS = -1;
                        zzaa = zzwz().zzaa(currentTimeMillis - zzcem.zzxP());
                        if (!TextUtils.isEmpty(zzaa)) {
                            zzceg zzdQ = zzwz().zzdQ(zzaa);
                            if (zzdQ != null) {
                                zzb(zzdQ);
                            }
                        }
                    } else {
                        if (this.zzbsS == -1) {
                            this.zzbsS = zzwz().zzyj();
                        }
                        List<Pair> zzl = zzwz().zzl(zzyc, this.zzbsn.zzb(zzyc, zzcfb.zzbqb), Math.max(0, this.zzbsn.zzb(zzyc, zzcfb.zzbqc)));
                        if (!zzl.isEmpty()) {
                            zzcjz zzcjz;
                            Object obj;
                            int i;
                            List subList;
                            for (Pair pair : zzl) {
                                zzcjz = (zzcjz) pair.first;
                                if (!TextUtils.isEmpty(zzcjz.zzbvR)) {
                                    obj = zzcjz.zzbvR;
                                    break;
                                }
                            }
                            obj = null;
                            if (obj != null) {
                                for (i = 0; i < zzl.size(); i++) {
                                    zzcjz = (zzcjz) ((Pair) zzl.get(i)).first;
                                    if (!TextUtils.isEmpty(zzcjz.zzbvR) && !zzcjz.zzbvR.equals(obj)) {
                                        subList = zzl.subList(0, i);
                                        break;
                                    }
                                }
                            }
                            subList = zzl;
                            zzcjy zzcjy = new zzcjy();
                            zzcjy.zzbvB = new zzcjz[subList.size()];
                            Collection arrayList = new ArrayList(subList.size());
                            boolean z2 = zzcem.zzyb() && this.zzbsn.zzdO(zzyc);
                            for (i = 0; i < zzcjy.zzbvB.length; i++) {
                                zzcjy.zzbvB[i] = (zzcjz) ((Pair) subList.get(i)).first;
                                arrayList.add((Long) ((Pair) subList.get(i)).second);
                                zzcjy.zzbvB[i].zzbvQ = Long.valueOf(zzcem.zzwP());
                                zzcjy.zzbvB[i].zzbvG = Long.valueOf(currentTimeMillis);
                                zzcjy.zzbvB[i].zzbvW = Boolean.valueOf(zzcem.zzxE());
                                if (!z2) {
                                    zzcjy.zzbvB[i].zzbwd = null;
                                }
                            }
                            Object zza = zzwF().zzz(2) ? zzwA().zza(zzcjy) : null;
                            byte[] zzb = zzwB().zzb(zzcjy);
                            zzxO = zzcem.zzxO();
                            URL url = new URL(zzxO);
                            if (arrayList.isEmpty()) {
                                z = false;
                            }
                            zzbo.zzaf(z);
                            if (this.zzbsO != null) {
                                zzwF().zzyx().log("Set uploading progress before finishing the previous upload");
                            } else {
                                this.zzbsO = new ArrayList(arrayList);
                            }
                            zzwG().zzbrl.set(currentTimeMillis);
                            zzaa = "?";
                            if (zzcjy.zzbvB.length > 0) {
                                zzaa = zzcjy.zzbvB[0].zzaH;
                            }
                            zzwF().zzyD().zzd("Uploading data. app, uncompressed size, data", zzaa, Integer.valueOf(zzb.length), zza);
                            this.zzbsV = true;
                            zzyU().zza$764ba3ba(zzyc, url, zzb, new zzcgo(this));
                        }
                    }
                    this.zzbsW = false;
                    zzzg();
                } else {
                    zzwF().zzyD().log("Network not connected, ignoring upload request");
                    zzzc();
                    this.zzbsW = false;
                    zzzg();
                }
            }
        } catch (MalformedURLException e) {
            zzwF().zzyx().zze("Failed to parse upload URL. Not uploading. appId", zzcfl.zzdZ(zzyc), zzxO);
        } catch (Throwable th) {
            this.zzbsW = false;
            zzzg();
        }
    }

    final void zzzd() {
        this.zzbsR++;
    }

    final void zzze() {
        zzwE().zzjC();
        if (!this.zzbsJ) {
            zzwF().zzyB().log("This instance being marked as an uploader");
            zzwE().zzjC();
            if (zzzf() && zzyX()) {
                int zza = zza(this.zzbsN);
                int zzyv = zzwu().zzyv();
                zzwE().zzjC();
                if (zza > zzyv) {
                    zzwF().zzyx().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(zza), Integer.valueOf(zzyv));
                } else if (zza < zzyv) {
                    if (zza(zzyv, this.zzbsN)) {
                        zzwF().zzyD().zze("Storage version upgraded. Previous, current version", Integer.valueOf(zza), Integer.valueOf(zzyv));
                    } else {
                        zzwF().zzyx().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(zza), Integer.valueOf(zzyv));
                    }
                }
            }
            this.zzbsJ = true;
            zzzc();
        }
    }
}
