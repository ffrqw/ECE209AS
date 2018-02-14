package com.google.android.gms.internal;

import android.text.TextUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Locale;

public final class zzalx extends zzamh {
    private static boolean zzafv;
    private Object zzafA = new Object();
    private Info zzafw;
    private final zzaoo zzafx;
    private String zzafy;
    private boolean zzafz = false;

    zzalx(zzamj zzamj) {
        super(zzamj);
        this.zzafx = new zzaoo(zzamj.zzkq());
    }

    private final boolean zza(Info info, Info info2) {
        Object obj = null;
        CharSequence id = info2 == null ? null : info2.getId();
        if (TextUtils.isEmpty(id)) {
            return true;
        }
        String zzli = zzkz().zzli();
        synchronized (this.zzafA) {
            String valueOf;
            String valueOf2;
            if (!this.zzafz) {
                this.zzafy = zzkj();
                this.zzafz = true;
            } else if (TextUtils.isEmpty(this.zzafy)) {
                if (info != null) {
                    obj = info.getId();
                }
                if (obj == null) {
                    valueOf = String.valueOf(id);
                    String valueOf3 = String.valueOf(zzli);
                    boolean zzbn = zzbn(valueOf3.length() != 0 ? valueOf.concat(valueOf3) : new String(valueOf));
                    return zzbn;
                }
                valueOf2 = String.valueOf(obj);
                valueOf = String.valueOf(zzli);
                this.zzafy = zzbm(valueOf.length() != 0 ? valueOf2.concat(valueOf) : new String(valueOf2));
            }
            valueOf2 = String.valueOf(id);
            valueOf = String.valueOf(zzli);
            obj = zzbm(valueOf.length() != 0 ? valueOf2.concat(valueOf) : new String(valueOf2));
            if (TextUtils.isEmpty(obj)) {
                return false;
            } else if (obj.equals(this.zzafy)) {
                return true;
            } else {
                if (TextUtils.isEmpty(this.zzafy)) {
                    valueOf = zzli;
                } else {
                    zzbo("Resetting the client id because Advertising Id changed.");
                    obj = zzkz().zzlj();
                    zza("New client Id", obj);
                }
                String valueOf4 = String.valueOf(id);
                valueOf3 = String.valueOf(obj);
                zzbn = zzbn(valueOf3.length() != 0 ? valueOf4.concat(valueOf3) : new String(valueOf4));
                return zzbn;
            }
        }
    }

    private static String zzbm(String str) {
        if (zzaos.zzbE("MD5") == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, zzaos.zzbE("MD5").digest(str.getBytes()))});
    }

    private final boolean zzbn(String str) {
        try {
            String zzbm = zzbm(str);
            zzbo("Storing hashed adid.");
            FileOutputStream openFileOutput = getContext().openFileOutput("gaClientIdData", 0);
            openFileOutput.write(zzbm.getBytes());
            openFileOutput.close();
            this.zzafy = zzbm;
            return true;
        } catch (IOException e) {
            zze("Error creating hash file", e);
            return false;
        }
    }

    private final synchronized Info zzkh() {
        if (this.zzafx.zzu(1000)) {
            this.zzafx.start();
            Info zzki = zzki();
            if (zza(this.zzafw, zzki)) {
                this.zzafw = zzki;
            } else {
                zzbs("Failed to reset client id on adid change. Not using adid");
                this.zzafw = new Info("", false);
            }
        }
        return this.zzafw;
    }

    private final Info zzki() {
        Info info = null;
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
        } catch (IllegalStateException e) {
            zzbr("IllegalStateException getting Ad Id Info. If you would like to see Audience reports, please ensure that you have added '<meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />' to your application manifest file. See http://goo.gl/naFqQk for details.");
        } catch (Throwable th) {
            if (!zzafv) {
                zzafv = true;
                zzd("Error getting advertiser id", th);
            }
        }
        return info;
    }

    private final String zzkj() {
        Object obj;
        String str = null;
        try {
            FileInputStream openFileInput = getContext().openFileInput("gaClientIdData");
            byte[] bArr = new byte[128];
            int read = openFileInput.read(bArr, 0, 128);
            if (openFileInput.available() > 0) {
                zzbr("Hash file seems corrupted, deleting it.");
                openFileInput.close();
                getContext().deleteFile("gaClientIdData");
                return null;
            } else if (read <= 0) {
                zzbo("Hash file is empty.");
                openFileInput.close();
                return null;
            } else {
                String str2 = new String(bArr, 0, read);
                try {
                    openFileInput.close();
                    return str2;
                } catch (FileNotFoundException e) {
                    return str2;
                } catch (IOException e2) {
                    IOException iOException = e2;
                    str = str2;
                    IOException iOException2 = iOException;
                    zzd("Error reading Hash file, deleting it", obj);
                    getContext().deleteFile("gaClientIdData");
                    return str;
                }
            }
        } catch (FileNotFoundException e3) {
            return null;
        } catch (IOException e4) {
            obj = e4;
            zzd("Error reading Hash file, deleting it", obj);
            getContext().deleteFile("gaClientIdData");
            return str;
        }
    }

    protected final void zzjD() {
    }

    public final boolean zzjZ() {
        zzkD();
        Info zzkh = zzkh();
        return (zzkh == null || zzkh.isLimitAdTrackingEnabled()) ? false : true;
    }

    public final String zzkg() {
        zzkD();
        Info zzkh = zzkh();
        CharSequence id = zzkh != null ? zzkh.getId() : null;
        return TextUtils.isEmpty(id) ? null : id;
    }
}
