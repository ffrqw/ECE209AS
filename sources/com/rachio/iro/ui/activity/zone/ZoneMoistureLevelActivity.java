package com.rachio.iro.ui.activity.zone;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.CrashReporterUtils;
import java.util.Map;
import java.util.TreeMap;

public class ZoneMoistureLevelActivity extends BaseActivity {
    private static final String BASE_URL = "https://app.rach.io";
    private static final String TAG = ZoneMoistureLevelActivity.class.getSimpleName();
    private static boolean preloaded = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_zone_moisture_level);
        wireupToolbarActionBar();
        String userId = getUserIdFromExtras();
        String deviceId = getDeviceIdFromExtras();
        String zoneId = getZoneIdFromExtras();
        String authKey = getIntent().getStringExtra("authtoken");
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.please_wait));
        pd.setCancelable(false);
        WebView webView = (WebView) findViewById(R.id.zone_moisturelevel_webview);
        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setCacheMode(-1);
        if (VERSION.SDK_INT >= 19) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.show();
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
            }

            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                CrashReporterUtils.silentException(new Exception("error " + errorCode + " " + failingUrl));
            }

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (VERSION.SDK_INT >= 21) {
                    CrashReporterUtils.silentException(new Exception("error " + 0 + " " + request.getUrl().toString()));
                }
            }

            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (VERSION.SDK_INT >= 23) {
                    CrashReporterUtils.silentException(new Exception("error " + errorResponse.getStatusCode() + " " + request.getUrl().toString()));
                }
            }
        });
        webView.loadUrl(buildUri(userId, deviceId, zoneId), buildAuthHeaders(authKey));
    }

    private static Map<String, String> buildAuthHeaders(String authKey) {
        Map<String, String> authHeaders = new TreeMap();
        authHeaders.put("Authorization", "Bearer " + authKey);
        return authHeaders;
    }

    private static String buildUri(String userId, String deviceId, String zoneId) {
        return Uri.parse(BASE_URL).buildUpon().appendPath("user").appendPath(userId).appendPath("device").appendPath(deviceId).appendPath("zone").appendPath(zoneId).appendPath("moisture_graph").build().toString();
    }

    public static void preCache(WebView dummy, String userId, String deviceId, String zoneId, String authToken) {
        if (!preloaded) {
            preloaded = true;
            WebSettings webViewSettings = dummy.getSettings();
            webViewSettings.setJavaScriptEnabled(true);
            webViewSettings.setCacheMode(1);
            dummy.setWebViewClient(new WebViewClient() {
                public final void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                public final void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.setVisibility(8);
                }
            });
            dummy.loadUrl(buildUri(userId, deviceId, zoneId), buildAuthHeaders(authToken));
        }
    }
}
