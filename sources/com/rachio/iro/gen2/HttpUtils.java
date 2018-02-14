package com.rachio.iro.gen2;

import android.net.Network;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import com.rachio.iro.utils.CrashReporterUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {
    private static final String TAG = HttpUtils.class.getCanonicalName();
    private static Network network = null;

    public static class HttpResponse {
        public final int code;
        public final String content;

        public HttpResponse(int code, String content) {
            this.code = code;
            this.content = content;
        }

        public final boolean haveContent() {
            return !TextUtils.isEmpty(this.content);
        }
    }

    public static HttpResponse doHttpGet(String url) {
        return doHttpRequestWithoutInput(url, "GET");
    }

    private static HttpURLConnection openConnection(URL source) throws IOException {
        if (network == null) {
            return (HttpURLConnection) source.openConnection();
        }
        if (VERSION.SDK_INT >= 21) {
            return (HttpURLConnection) network.openConnection(source);
        }
        return null;
    }

    private static HttpResponse doHttpRequestWithoutInput(String url, String method) {
        BufferedReader br;
        CrashReporterUtils.logDebug(TAG, "http " + method + ": " + url);
        HttpURLConnection connection = null;
        StringBuilder result;
        try {
            connection = openConnection(new URL(url));
            connection.setRequestMethod(method);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            result = new StringBuilder();
            char[] charbuff = new char[1024];
            while (true) {
                int read = br.read(charbuff);
                if (read == -1 || result.length() >= 1048576) {
                    break;
                }
                result.append(charbuff, 0, read);
                Log.d(TAG, String.format("Have %d chars in buffer", new Object[]{Integer.valueOf(result.length())}));
            }
            CrashReporterUtils.logDebug(TAG, "http result: " + result.toString());
            br.close();
            return new HttpResponse(connection.getResponseCode(), result.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            if (connection != null) {
                InputStream is = connection.getErrorStream();
                if (is != null) {
                    result = new StringBuilder();
                    br = new BufferedReader(new InputStreamReader(is));
                    while (true) {
                        try {
                            String line = br.readLine();
                            if (line != null) {
                                result.append(line);
                            } else {
                                CrashReporterUtils.logDebug(TAG, "http result: " + result.toString());
                                return new HttpResponse(connection.getResponseCode(), result.toString());
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    e2.printStackTrace();
                }
            } else {
                e2.printStackTrace();
            }
            return null;
        }
    }

    public static HttpResponse doHttpPost(String url, String contentType, String data) {
        return doHttpRequestWithInput(url, contentType, data, "POST");
    }

    private static HttpResponse doHttpRequestWithInput(String url, String contentType, String data, String method) {
        CrashReporterUtils.logDebug(TAG, "http " + method + ": " + url + " data: " + data);
        HttpURLConnection connection = null;
        BufferedReader br;
        StringBuilder result;
        try {
            connection = openConnection(new URL(url));
            byte[] dataBytes = data.getBytes();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Content-Length", Integer.toString(dataBytes.length));
            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
            bos.write(dataBytes);
            bos.flush();
            bos.close();
            char[] charbuff = new char[1024];
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            result = new StringBuilder();
            while (true) {
                int read = br.read(charbuff);
                if (read == -1 || result.length() >= 1048576) {
                    break;
                }
                result.append(charbuff, 0, read);
            }
            CrashReporterUtils.logDebug(TAG, "http result: " + result.toString());
            br.close();
            return new HttpResponse(connection.getResponseCode(), result.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e2) {
            if (connection != null) {
                InputStream is = connection.getErrorStream();
                if (is != null) {
                    result = new StringBuilder();
                    br = new BufferedReader(new InputStreamReader(is));
                    while (true) {
                        try {
                            String line = br.readLine();
                            if (line != null) {
                                result.append(line);
                            } else {
                                CrashReporterUtils.logDebug(TAG, "result" + result.toString());
                                return new HttpResponse(connection.getResponseCode(), result.toString());
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    e2.printStackTrace();
                }
            } else {
                e2.printStackTrace();
            }
            return null;
        }
    }

    public static void setNetwork(Network network) {
        network = network;
    }
}
