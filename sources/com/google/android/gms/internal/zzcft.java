package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

final class zzcft implements Runnable {
    private final String mPackageName;
    private final URL zzJu;
    private final byte[] zzaKA;
    private final zzcfr zzbrd;
    private final Map<String, String> zzbre;
    private /* synthetic */ zzcfp zzbrf;

    public zzcft(zzcfp zzcfp, String str, URL url, byte[] bArr, Map<String, String> map, zzcfr zzcfr) {
        this.zzbrf = zzcfp;
        zzbo.zzcF(str);
        zzbo.zzu(url);
        zzbo.zzu(zzcfr);
        this.zzJu = url;
        this.zzaKA = bArr;
        this.zzbrd = zzcfr;
        this.mPackageName = str;
        this.zzbre = map;
    }

    public final void run() {
        HttpURLConnection httpURLConnection;
        OutputStream outputStream;
        Throwable e;
        OutputStream outputStream2;
        OutputStream outputStream3;
        Throwable th;
        this.zzbrf.zzwq();
        HttpURLConnection httpURLConnection2 = null;
        OutputStream outputStream4 = null;
        int i = 0;
        Map map = null;
        try {
            URLConnection openConnection = this.zzJu.openConnection();
            if (openConnection instanceof HttpURLConnection) {
                httpURLConnection = (HttpURLConnection) openConnection;
                httpURLConnection.setDefaultUseCaches(false);
                zzcem.zzxz();
                httpURLConnection.setConnectTimeout(60000);
                zzcem.zzxA();
                httpURLConnection.setReadTimeout(61000);
                httpURLConnection.setInstanceFollowRedirects(false);
                httpURLConnection.setDoInput(true);
                try {
                    if (this.zzbre != null) {
                        for (Entry entry : this.zzbre.entrySet()) {
                            httpURLConnection.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                        }
                    }
                    if (this.zzaKA != null) {
                        byte[] zzl = this.zzbrf.zzwB().zzl(this.zzaKA);
                        this.zzbrf.zzwF().zzyD().zzj("Uploading data. size", Integer.valueOf(zzl.length));
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.addRequestProperty("Content-Encoding", "gzip");
                        httpURLConnection.setFixedLengthStreamingMode(zzl.length);
                        httpURLConnection.connect();
                        outputStream = httpURLConnection.getOutputStream();
                        try {
                            outputStream.write(zzl);
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e2) {
                            e = e2;
                            outputStream2 = outputStream;
                            httpURLConnection2 = httpURLConnection;
                            outputStream3 = outputStream2;
                            if (outputStream3 != null) {
                                try {
                                    outputStream3.close();
                                } catch (IOException e3) {
                                    this.zzbrf.zzwF().zzyx().zze("Error closing HTTP compressed POST connection output stream. appId", zzcfl.zzdZ(this.mPackageName), e3);
                                }
                            }
                            if (httpURLConnection2 != null) {
                                httpURLConnection2.disconnect();
                            }
                            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
                        } catch (Throwable th2) {
                            th = th2;
                            outputStream4 = outputStream;
                            if (outputStream4 != null) {
                                try {
                                    outputStream4.close();
                                } catch (IOException e4) {
                                    this.zzbrf.zzwF().zzyx().zze("Error closing HTTP compressed POST connection output stream. appId", zzcfl.zzdZ(this.mPackageName), e4);
                                }
                            }
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, null, null, map));
                            throw th;
                        }
                    }
                    outputStream = null;
                } catch (IOException e5) {
                    e = e5;
                    httpURLConnection2 = httpURLConnection;
                    outputStream3 = null;
                    if (outputStream3 != null) {
                        outputStream3.close();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
                } catch (Throwable th3) {
                    th = th3;
                    if (outputStream4 != null) {
                        outputStream4.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, null, null, map));
                    throw th;
                }
                try {
                    i = httpURLConnection.getResponseCode();
                    map = httpURLConnection.getHeaderFields();
                    byte[] zza$35d7bb93 = zzcfp.zzc(httpURLConnection);
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, null, zza$35d7bb93, map));
                    return;
                } catch (IOException e6) {
                    e = e6;
                    outputStream2 = outputStream;
                    httpURLConnection2 = httpURLConnection;
                    outputStream3 = outputStream2;
                    if (outputStream3 != null) {
                        outputStream3.close();
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
                } catch (Throwable th22) {
                    th = th22;
                    outputStream4 = outputStream;
                    if (outputStream4 != null) {
                        outputStream4.close();
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, null, null, map));
                    throw th;
                }
            }
            throw new IOException("Failed to obtain HTTP connection");
        } catch (IOException e7) {
            e = e7;
            outputStream3 = null;
            if (outputStream3 != null) {
                outputStream3.close();
            }
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, e, null, map));
        } catch (Throwable th4) {
            th = th4;
            httpURLConnection = null;
            if (outputStream4 != null) {
                outputStream4.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            this.zzbrf.zzwE().zzj(new zzcfs(this.mPackageName, this.zzbrd, i, null, null, map));
            throw th;
        }
    }
}
