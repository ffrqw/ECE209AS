package com.instabug.library.e;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.instabug.library.Instabug;
import com.instabug.library.e.c.b;
import com.instabug.library.e.c.d;
import com.instabug.library.util.InstabugSDKLogger;
import com.squareup.mimecraft.Multipart;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Scanner;
import org.json.JSONException;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public class a {

    /* renamed from: com.instabug.library.e.a$2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] a = new int[3];

        static {
            a.values$55f5806f();
            try {
                int[] iArr = a;
                int i = a.a$656280e9;
                iArr[0] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr = a;
                i = a.c$656280e9;
                iArr[2] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr = a;
                i = a.b$656280e9;
                iArr[1] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public enum a {
        ;

        public static int[] values$55f5806f() {
            return (int[]) d$4a03b8f2.clone();
        }

        static {
            a$656280e9 = 1;
            b$656280e9 = 2;
            c$656280e9 = 3;
            d$4a03b8f2 = new int[]{1, 2, 3};
        }
    }

    static /* synthetic */ HttpURLConnection c(a aVar, c cVar) throws IOException {
        InstabugSDKLogger.v(aVar, "connectWithMultiPartType");
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(cVar.b()).openConnection();
        httpURLConnection.setRequestProperty("Content-type", "application/json; charset=utf-8");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod(cVar.c().toString());
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoOutput(true);
        Multipart a = cVar.a(cVar.g(), cVar.e());
        for (Entry entry : a.getHeaders().entrySet()) {
            httpURLConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
        }
        a.writeBodyTo(httpURLConnection.getOutputStream());
        return httpURLConnection;
    }

    public static boolean a(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (SecurityException e) {
            InstabugSDKLogger.w(a.class, "Could not read network state. To enable please add the following line in your AndroidManifest.xml <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\"/>\n" + e.getMessage());
        } catch (Throwable e2) {
            InstabugSDKLogger.wtf(a.class, "Something went wrong while checking network state", e2);
        }
        return false;
    }

    public final Observable<d> a(final c cVar) {
        return Observable.create(new OnSubscribe<d>(this) {
            final /* synthetic */ a b;

            public final /* synthetic */ void call(Object obj) {
                Throwable e;
                Subscriber subscriber = (Subscriber) obj;
                try {
                    subscriber.onStart();
                    InstabugSDKLogger.v(this, "Request Url: " + cVar.b());
                    InstabugSDKLogger.v(this, "Request Type: " + cVar.c().toString());
                    InstabugSDKLogger.v(this, "Request Body: " + cVar.f());
                    HttpURLConnection httpURLConnection = null;
                    switch (AnonymousClass2.a[cVar.d$5363e898() - 1]) {
                        case 1:
                            httpURLConnection = this.b.d(cVar);
                            break;
                        case 2:
                            httpURLConnection = this.b.d(cVar);
                            break;
                        case 3:
                            httpURLConnection = a.c(this.b, cVar);
                            break;
                    }
                    if (httpURLConnection.getResponseCode() >= 300) {
                        InstabugSDKLogger.d(this, "Network request got error");
                        a.a(this.b, httpURLConnection);
                    }
                    InstabugSDKLogger.d(this, "Network request completed successfully");
                    switch (AnonymousClass2.a[cVar.d$5363e898() - 1]) {
                        case 1:
                            subscriber.onNext(this.b.b(httpURLConnection));
                            break;
                        case 2:
                            subscriber.onNext(a.a(this.b, cVar, httpURLConnection));
                            break;
                        case 3:
                            subscriber.onNext(this.b.b(httpURLConnection));
                            break;
                    }
                    subscriber.onCompleted();
                } catch (IOException e2) {
                    e = e2;
                    try {
                        subscriber.onError(e);
                    } finally {
                        subscriber.onCompleted();
                    }
                } catch (b e3) {
                    e = e3;
                    subscriber.onError(e);
                }
            }
        });
    }

    public final c a(Context context, b bVar, d dVar) throws JSONException {
        return a$2a0f3fa8(context, bVar, dVar, a.a$656280e9);
    }

    public final c a$2a0f3fa8(Context context, b bVar, d dVar, int i) throws JSONException {
        c cVar = new c(bVar, i);
        cVar.a(dVar);
        return a(context, cVar);
    }

    public final c a$1ab3202(Context context, String str, d dVar, int i) throws JSONException {
        c cVar = new c(str, i);
        cVar.a(dVar);
        return a(context, cVar);
    }

    private static c a(Context context, c cVar) throws JSONException {
        com.instabug.library.internal.module.a aVar = new com.instabug.library.internal.module.a();
        com.instabug.library.internal.a.b a = com.instabug.library.internal.module.a.a(context);
        cVar.a("application_token", Instabug.getAppToken());
        cVar.a("uuid", a.l());
        return cVar;
    }

    private HttpURLConnection d(c cVar) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(cVar.b()).openConnection();
        httpURLConnection.setRequestProperty("Content-type", "application/json; charset=utf-8");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setRequestMethod(cVar.c().toString());
        if (cVar.c() == d.Post || cVar.c() == d.put) {
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            String f = cVar.f();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(outputStream), "UTF-8"));
            bufferedWriter.write(f);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        return httpURLConnection;
    }

    private d b(HttpURLConnection httpURLConnection) throws IOException {
        d dVar = new d();
        int responseCode = httpURLConnection.getResponseCode();
        dVar.a(responseCode);
        InstabugSDKLogger.v(this, "Request response code: " + responseCode);
        Object a = a(httpURLConnection.getInputStream());
        dVar.a(a);
        InstabugSDKLogger.v(this, "Request response: " + a);
        httpURLConnection.disconnect();
        return dVar;
    }

    private static String a(InputStream inputStream) {
        Scanner useDelimiter = new Scanner(inputStream).useDelimiter("\\A");
        return useDelimiter.hasNext() ? useDelimiter.next() : "";
    }

    private static void a(InputStream inputStream, OutputStream outputStream) throws IOException {
        while (true) {
            int read = inputStream.read();
            if (read != -1) {
                outputStream.write(read);
            } else {
                return;
            }
        }
    }

    static /* synthetic */ void a(a aVar, HttpURLConnection httpURLConnection) throws IOException, b {
        int responseCode = httpURLConnection.getResponseCode();
        InstabugSDKLogger.d(aVar, "Error getting Network request response: " + a(httpURLConnection.getErrorStream()));
        throw new b(responseCode);
    }

    static /* synthetic */ d a(a aVar, c cVar, HttpURLConnection httpURLConnection) throws IOException {
        d dVar = new d();
        int responseCode = httpURLConnection.getResponseCode();
        dVar.a(responseCode);
        InstabugSDKLogger.v(aVar, "File downloader request response code: " + responseCode);
        a(httpURLConnection.getInputStream(), new FileOutputStream(cVar.h()));
        dVar.a(cVar.h());
        InstabugSDKLogger.v(aVar, "File downloader request response: " + cVar.h().getPath());
        httpURLConnection.disconnect();
        return dVar;
    }
}
