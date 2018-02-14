package com.electricimp.blinkup;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public final class ImpAPIClient {
    private String apiKey;
    private String baseUrl;

    public ImpAPIClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public final void createSetupToken(String planID, Handler handler) {
        JSONObject dict = new JSONObject();
        if (planID != null) {
            try {
                dict.put("plan_id", planID);
            } catch (JSONException e) {
                Log.e("BlinkUp", Log.getStackTraceString(e));
                return;
            }
        }
        request("/setup_tokens", "POST", dict, handler, null);
    }

    public final void readSetupToken(String tokenID, Handler handler) {
        request("/setup_tokens/" + tokenID, "GET", null, handler, null);
    }

    private void request(String path, String method, JSONObject body, Handler handler, String responseField) {
        final Handler handler2 = handler;
        final String str = path;
        final String str2 = method;
        final JSONObject jSONObject = body;
        new Thread(new Runnable(null) {
            public final void run() {
                Message msg = handler2.obtainMessage();
                ImpAPIClient.this.requestTask(str, str2, jSONObject, null, msg);
                handler2.sendMessage(msg);
            }
        }).start();
    }

    public final void requestTask(String path, String method, JSONObject body, String responseField, Message msg) {
        try {
            HttpRequestBase req;
            String url = this.baseUrl + path;
            String authLineFull = "Basic " + Base64.encodeToString((this.apiKey + ":").getBytes("UTF-8"), 2);
            if (method.equals("POST")) {
                HttpRequestBase post = new HttpPost(url);
                if (body != null) {
                    post.addHeader("Content-Type", "application/json");
                    post.setEntity(new StringEntity(body.toString()));
                }
                req = post;
            } else {
                req = new HttpGet(url);
            }
            req.addHeader("Authorization", authLineFull);
            req.addHeader("User-Agent", new StringBuilder(String.valueOf(System.getProperty("http.agent"))).append(" v4.2.14").toString());
            HttpResponse response = BlinkupHttpClient.newInstanceOrDefault().execute(req);
            int expectedResponseCode = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            if (method.equals("POST")) {
                expectedResponseCode = 201;
            }
            int code = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (code == expectedResponseCode) {
                String contentType = entity.getContentType().getValue();
                if (contentType.equals("application/vnd.electricimp-v1+json") || contentType.equals("application/json")) {
                    JSONObject responseObj = new JSONObject(new BufferedReader(new InputStreamReader(entity.getContent())).readLine());
                    if (responseField == null) {
                        msg.obj = responseObj;
                    } else {
                        msg.obj = responseObj.get(responseField);
                    }
                    msg.arg1 = 1;
                    return;
                }
                msg.obj = "Content type not recognized";
                msg.arg1 = 0;
                return;
            }
            msg.obj = "Unexpected server response: " + Integer.toString(code);
            msg.arg1 = 0;
        } catch (JSONException e) {
            Log.e("BlinkUp", "Error parsing JSON response", e);
            msg.obj = e.getMessage();
            msg.arg1 = 0;
        } catch (IOException e2) {
            Log.e("BlinkUp", "Server connection error", e2);
            msg.obj = e2.getMessage();
            msg.arg1 = 0;
        }
    }
}
