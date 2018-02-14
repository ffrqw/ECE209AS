package com.google.android.gms.internal;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import com.google.android.gms.analytics.zzl;
import com.google.android.gms.common.internal.zzbo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

final class zzaoe extends zzamh {
    private static final byte[] zzait = "\n".getBytes();
    private final String zzJP;
    private final zzaoo zzais;

    zzaoe(zzamj zzamj) {
        super(zzamj);
        String str = zzami.VERSION;
        String str2 = VERSION.RELEASE;
        String zza = zzaos.zza(Locale.getDefault());
        String str3 = Build.MODEL;
        String str4 = Build.ID;
        this.zzJP = String.format("%s/%s (Linux; U; Android %s; %s; %s Build/%s)", new Object[]{"GoogleAnalytics", str, str2, zza, str3, str4});
        this.zzais = new zzaoo(zzamj.zzkq());
    }

    private final int zza(java.net.URL r5) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0030 in list [B:7:0x002d]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:43)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
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
        r4 = this;
        com.google.android.gms.common.internal.zzbo.zzu(r5);
        r0 = "GET request";
        r4.zzb(r0, r5);
        r1 = 0;
        r1 = r4.zzb(r5);	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r1.connect();	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r4.zzb(r1);	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r0 = r1.getResponseCode();	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r2 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        if (r0 != r2) goto L_0x0022;	 Catch:{ IOException -> 0x0031, all -> 0x003e }
    L_0x001b:
        r2 = r4.zzkv();	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r2.zzko();	 Catch:{ IOException -> 0x0031, all -> 0x003e }
    L_0x0022:
        r2 = "GET status";	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r3 = java.lang.Integer.valueOf(r0);	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r4.zzb(r2, r3);	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        if (r1 == 0) goto L_0x0030;
    L_0x002d:
        r1.disconnect();
    L_0x0030:
        return r0;
    L_0x0031:
        r0 = move-exception;
        r2 = "Network GET connection error";	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        r4.zzd(r2, r0);	 Catch:{ IOException -> 0x0031, all -> 0x003e }
        if (r1 == 0) goto L_0x003c;
    L_0x0039:
        r1.disconnect();
    L_0x003c:
        r0 = 0;
        goto L_0x0030;
    L_0x003e:
        r0 = move-exception;
        if (r1 == 0) goto L_0x0044;
    L_0x0041:
        r1.disconnect();
    L_0x0044:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzaoe.zza(java.net.URL):int");
    }

    private final int zza(URL url, byte[] bArr) {
        HttpURLConnection zzb;
        Object e;
        Throwable th;
        OutputStream outputStream = null;
        zzbo.zzu(url);
        zzbo.zzu(bArr);
        zzb("POST bytes, url", Integer.valueOf(bArr.length), url);
        if (zzamg.zzhM()) {
            zza("Post payload\n", new String(bArr));
        }
        try {
            getContext().getPackageName();
            zzb = zzb(url);
            try {
                zzb.setDoOutput(true);
                zzb.setFixedLengthStreamingMode(bArr.length);
                zzb.connect();
                outputStream = zzb.getOutputStream();
                outputStream.write(bArr);
                zzb(zzb);
                int responseCode = zzb.getResponseCode();
                if (responseCode == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    zzkv().zzko();
                }
                zzb("POST status", Integer.valueOf(responseCode));
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e2) {
                        zze("Error closing http post connection output stream", e2);
                    }
                }
                if (zzb == null) {
                    return responseCode;
                }
                zzb.disconnect();
                return responseCode;
            } catch (IOException e3) {
                e = e3;
                try {
                    zzd("Network POST connection error", e);
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e4) {
                            zze("Error closing http post connection output stream", e4);
                        }
                    }
                    if (zzb != null) {
                        zzb.disconnect();
                    }
                    return 0;
                } catch (Throwable th2) {
                    th = th2;
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e22) {
                            zze("Error closing http post connection output stream", e22);
                        }
                    }
                    if (zzb != null) {
                        zzb.disconnect();
                    }
                    throw th;
                }
            }
        } catch (IOException e5) {
            e = e5;
            zzb = outputStream;
            zzd("Network POST connection error", e);
            if (outputStream != null) {
                outputStream.close();
            }
            if (zzb != null) {
                zzb.disconnect();
            }
            return 0;
        } catch (Throwable th3) {
            th = th3;
            zzb = outputStream;
            if (outputStream != null) {
                outputStream.close();
            }
            if (zzb != null) {
                zzb.disconnect();
            }
            throw th;
        }
    }

    private static void zza(StringBuilder stringBuilder, String str, String str2) throws UnsupportedEncodingException {
        if (stringBuilder.length() != 0) {
            stringBuilder.append('&');
        }
        stringBuilder.append(URLEncoder.encode(str, "UTF-8"));
        stringBuilder.append('=');
        stringBuilder.append(URLEncoder.encode(str2, "UTF-8"));
    }

    private final int zzb(URL url, byte[] bArr) {
        HttpURLConnection zzb;
        OutputStream outputStream;
        Object e;
        HttpURLConnection httpURLConnection;
        Throwable th;
        OutputStream outputStream2 = null;
        zzbo.zzu(url);
        zzbo.zzu(bArr);
        try {
            getContext().getPackageName();
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(bArr);
            gZIPOutputStream.close();
            byteArrayOutputStream.close();
            byte[] toByteArray = byteArrayOutputStream.toByteArray();
            zza("POST compressed size, ratio %, url", Integer.valueOf(toByteArray.length), Long.valueOf((100 * ((long) toByteArray.length)) / ((long) bArr.length)), url);
            if (toByteArray.length > bArr.length) {
                zzc("Compressed payload is larger then uncompressed. compressed, uncompressed", Integer.valueOf(toByteArray.length), Integer.valueOf(bArr.length));
            }
            if (zzamg.zzhM()) {
                String str = "Post payload";
                String str2 = "\n";
                String valueOf = String.valueOf(new String(bArr));
                zza(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            }
            zzb = zzb(url);
            try {
                zzb.setDoOutput(true);
                zzb.addRequestProperty("Content-Encoding", "gzip");
                zzb.setFixedLengthStreamingMode(toByteArray.length);
                zzb.connect();
                outputStream = zzb.getOutputStream();
            } catch (IOException e2) {
                e = e2;
                httpURLConnection = zzb;
                try {
                    zzd("Network compressed POST connection error", e);
                    if (outputStream2 != null) {
                        try {
                            outputStream2.close();
                        } catch (IOException e3) {
                            zze("Error closing http compressed post connection output stream", e3);
                        }
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    return 0;
                } catch (Throwable th2) {
                    th = th2;
                    zzb = httpURLConnection;
                    if (outputStream2 != null) {
                        try {
                            outputStream2.close();
                        } catch (IOException e4) {
                            zze("Error closing http compressed post connection output stream", e4);
                        }
                    }
                    if (zzb != null) {
                        zzb.disconnect();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (outputStream2 != null) {
                    outputStream2.close();
                }
                if (zzb != null) {
                    zzb.disconnect();
                }
                throw th;
            }
            try {
                outputStream.write(toByteArray);
                outputStream.close();
                zzb(zzb);
                int responseCode = zzb.getResponseCode();
                if (responseCode == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                    zzkv().zzko();
                }
                zzb("POST status", Integer.valueOf(responseCode));
                if (zzb == null) {
                    return responseCode;
                }
                zzb.disconnect();
                return responseCode;
            } catch (IOException e5) {
                e = e5;
                outputStream2 = outputStream;
                httpURLConnection = zzb;
                zzd("Network compressed POST connection error", e);
                if (outputStream2 != null) {
                    outputStream2.close();
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return 0;
            } catch (Throwable th4) {
                th = th4;
                outputStream2 = outputStream;
                if (outputStream2 != null) {
                    outputStream2.close();
                }
                if (zzb != null) {
                    zzb.disconnect();
                }
                throw th;
            }
        } catch (IOException e6) {
            e = e6;
            httpURLConnection = null;
            zzd("Network compressed POST connection error", e);
            if (outputStream2 != null) {
                outputStream2.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            return 0;
        } catch (Throwable th5) {
            th = th5;
            zzb = null;
            if (outputStream2 != null) {
                outputStream2.close();
            }
            if (zzb != null) {
                zzb.disconnect();
            }
            throw th;
        }
    }

    private final HttpURLConnection zzb(URL url) throws IOException {
        URLConnection openConnection = url.openConnection();
        if (openConnection instanceof HttpURLConnection) {
            HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
            httpURLConnection.setDefaultUseCaches(false);
            httpURLConnection.setConnectTimeout(((Integer) zzans.zzahI.get()).intValue());
            httpURLConnection.setReadTimeout(((Integer) zzans.zzahJ.get()).intValue());
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setRequestProperty("User-Agent", this.zzJP);
            httpURLConnection.setDoInput(true);
            return httpURLConnection;
        }
        throw new IOException("Failed to obtain http connection");
    }

    private final URL zzb(zzanx zzanx, String str) {
        String valueOf;
        String valueOf2;
        if (zzanx.zzlI()) {
            valueOf2 = String.valueOf(zzank.zzlu());
            valueOf = String.valueOf(zzank.zzlw());
            valueOf = new StringBuilder(((String.valueOf(valueOf2).length() + 1) + String.valueOf(valueOf).length()) + String.valueOf(str).length()).append(valueOf2).append(valueOf).append("?").append(str).toString();
        } else {
            valueOf2 = String.valueOf(zzank.zzlv());
            valueOf = String.valueOf(zzank.zzlw());
            valueOf = new StringBuilder(((String.valueOf(valueOf2).length() + 1) + String.valueOf(valueOf).length()) + String.valueOf(str).length()).append(valueOf2).append(valueOf).append("?").append(str).toString();
        }
        try {
            return new URL(valueOf);
        } catch (MalformedURLException e) {
            zze("Error trying to parse the hardcoded host url", e);
            return null;
        }
    }

    private final void zzb(HttpURLConnection httpURLConnection) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = httpURLConnection.getInputStream();
            do {
            } while (inputStream.read(new byte[1024]) > 0);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    zze("Error closing http connection input stream", e);
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                    zze("Error closing http connection input stream", e2);
                }
            }
        }
    }

    private final URL zzd(zzanx zzanx) {
        String valueOf;
        String valueOf2;
        if (zzanx.zzlI()) {
            valueOf = String.valueOf(zzank.zzlu());
            valueOf2 = String.valueOf(zzank.zzlw());
            valueOf = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        } else {
            valueOf = String.valueOf(zzank.zzlv());
            valueOf2 = String.valueOf(zzank.zzlw());
            valueOf = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        }
        try {
            return new URL(valueOf);
        } catch (MalformedURLException e) {
            zze("Error trying to parse the hardcoded host url", e);
            return null;
        }
    }

    private final URL zzlR() {
        String valueOf = String.valueOf(zzank.zzlu());
        String valueOf2 = String.valueOf((String) zzans.zzahx.get());
        try {
            return new URL(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        } catch (MalformedURLException e) {
            zze("Error trying to parse the hardcoded host url", e);
            return null;
        }
    }

    private final List<Long> zzv(List<zzanx> list) {
        List<Long> arrayList = new ArrayList(list.size());
        for (zzanx zzanx : list) {
            boolean z;
            zzbo.zzu(zzanx);
            String zza = zza(zzanx, !zzanx.zzlI());
            if (zza == null) {
                zzkr().zza(zzanx, "Error formatting hit for upload");
                z = true;
            } else {
                URL zzb;
                if (zza.length() <= ((Integer) zzans.zzahy.get()).intValue()) {
                    zzb = zzb(zzanx, zza);
                    if (zzb == null) {
                        zzbs("Failed to build collect GET endpoint url");
                    } else {
                        z = zza(zzb) == Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                    }
                } else {
                    String zza2 = zza(zzanx, false);
                    if (zza2 == null) {
                        zzkr().zza(zzanx, "Error formatting hit for POST upload");
                        z = true;
                    } else {
                        byte[] bytes = zza2.getBytes();
                        if (bytes.length > ((Integer) zzans.zzahD.get()).intValue()) {
                            zzkr().zza(zzanx, "Hit payload exceeds size limit");
                            z = true;
                        } else {
                            zzb = zzd(zzanx);
                            if (zzb == null) {
                                zzbs("Failed to build collect POST endpoint url");
                            } else if (zza(zzb, bytes) == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                                z = true;
                            }
                        }
                    }
                }
                z = false;
            }
            if (!z) {
                break;
            }
            arrayList.add(Long.valueOf(zzanx.zzlF()));
            if (arrayList.size() >= zzank.zzls()) {
                break;
            }
        }
        return arrayList;
    }

    final String zza(zzanx zzanx, boolean z) {
        zzbo.zzu(zzanx);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (Entry entry : zzanx.zzdV().entrySet()) {
                String str = (String) entry.getKey();
                if (!("ht".equals(str) || "qt".equals(str) || "AppUID".equals(str) || "z".equals(str) || "_gmsv".equals(str))) {
                    zza(stringBuilder, str, (String) entry.getValue());
                }
            }
            zza(stringBuilder, "ht", String.valueOf(zzanx.zzlG()));
            zza(stringBuilder, "qt", String.valueOf(zzkq().currentTimeMillis() - zzanx.zzlG()));
            if (z) {
                long zzlJ = zzanx.zzlJ();
                zza(stringBuilder, "z", zzlJ != 0 ? String.valueOf(zzlJ) : String.valueOf(zzanx.zzlF()));
            }
            return stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            zze("Failed to encode name or value", e);
            return null;
        }
    }

    protected final void zzjD() {
        zza("Network initialized. User agent", this.zzJP);
    }

    public final boolean zzlQ() {
        NetworkInfo activeNetworkInfo;
        zzl.zzjC();
        zzkD();
        try {
            activeNetworkInfo = ((ConnectivityManager) getContext().getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (SecurityException e) {
            activeNetworkInfo = null;
        }
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        zzbo("No network connectivity");
        return false;
    }

    public final List<Long> zzu(List<zzanx> list) {
        boolean z;
        boolean z2;
        zzaof zzaof;
        List<Long> arrayList;
        URL zzlR;
        int zzb;
        boolean z3 = true;
        zzl.zzjC();
        zzkD();
        zzbo.zzu(list);
        if (zzks().zzlx().isEmpty() || !this.zzais.zzu(((long) ((Integer) zzans.zzahG.get()).intValue()) * 1000)) {
            z = false;
        } else {
            z = zzana.zzbx((String) zzans.zzahz.get()) != zzana.NONE;
            if (zzang.zzby((String) zzans.zzahA.get()) == zzang.GZIP) {
                z2 = true;
                if (z) {
                    return zzv(list);
                }
                if (list.isEmpty()) {
                    z3 = false;
                }
                zzbo.zzaf(z3);
                zza("Uploading batched hits. compression, count", Boolean.valueOf(z2), Integer.valueOf(list.size()));
                zzaof = new zzaof(this);
                arrayList = new ArrayList();
                for (zzanx zzanx : list) {
                    if (zzaof.zze(zzanx)) {
                        break;
                    }
                    arrayList.add(Long.valueOf(zzanx.zzlF()));
                }
                if (zzaof.zzlT() == 0) {
                    return arrayList;
                }
                zzlR = zzlR();
                if (zzlR != null) {
                    zzbs("Failed to build batching endpoint url");
                } else {
                    zzb = z2 ? zzb(zzlR, zzaof.getPayload()) : zza(zzlR, zzaof.getPayload());
                    if (Callback.DEFAULT_DRAG_ANIMATION_DURATION != zzb) {
                        zza("Batched upload completed. Hits batched", Integer.valueOf(zzaof.zzlT()));
                        return arrayList;
                    }
                    zza("Network error uploading hits. status code", Integer.valueOf(zzb));
                    if (zzks().zzlx().contains(Integer.valueOf(zzb))) {
                        zzbr("Server instructed the client to stop batching");
                        this.zzais.start();
                    }
                }
                return Collections.emptyList();
            }
        }
        z2 = false;
        if (z) {
            return zzv(list);
        }
        if (list.isEmpty()) {
            z3 = false;
        }
        zzbo.zzaf(z3);
        zza("Uploading batched hits. compression, count", Boolean.valueOf(z2), Integer.valueOf(list.size()));
        zzaof = new zzaof(this);
        arrayList = new ArrayList();
        for (zzanx zzanx2 : list) {
            if (zzaof.zze(zzanx2)) {
                break;
            }
            arrayList.add(Long.valueOf(zzanx2.zzlF()));
        }
        if (zzaof.zzlT() == 0) {
            return arrayList;
        }
        zzlR = zzlR();
        if (zzlR != null) {
            if (z2) {
            }
            if (Callback.DEFAULT_DRAG_ANIMATION_DURATION != zzb) {
                zza("Network error uploading hits. status code", Integer.valueOf(zzb));
                if (zzks().zzlx().contains(Integer.valueOf(zzb))) {
                    zzbr("Server instructed the client to stop batching");
                    this.zzais.start();
                }
            } else {
                zza("Batched upload completed. Hits batched", Integer.valueOf(zzaof.zzlT()));
                return arrayList;
            }
        }
        zzbs("Failed to build batching endpoint url");
        return Collections.emptyList();
    }
}
