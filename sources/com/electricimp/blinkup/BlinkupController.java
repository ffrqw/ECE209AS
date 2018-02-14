package com.electricimp.blinkup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import org.json.JSONException;
import org.json.JSONObject;

public final class BlinkupController {
    private static BlinkupController instance = null;
    public int countdownSeconds = 3;
    public int drawableIdInterstitial = 0;
    public boolean enablePowerSaveMessage = true;
    private boolean frameRateCheckEnabled = true;
    private ImpController impController;
    public Intent intentBlinkupComplete = null;
    public Intent intentClearComplete = null;
    private String lastMode = null;
    private String preferenceFile = "eimpPreferences";
    public boolean setFullScreenBrightness = true;
    public boolean showClearConfig = true;
    public boolean showLegacyMode = false;
    public boolean showPassword = true;
    public String stringIdBlinkUpDesc = null;
    public String stringIdChangeNetwork = null;
    public String stringIdChooseWiFiNetwork = null;
    public String stringIdClearDeviceSettings = null;
    public String stringIdClearWireless = null;
    public String stringIdConnectUsingWps = null;
    public String stringIdCountdownDesc = null;
    public String stringIdLegacyMode = null;
    public String stringIdLegacyModeDesc = null;
    public String stringIdLowFrameRateDesc = null;
    public String stringIdLowFrameRateGoToSettings = null;
    public String stringIdLowFrameRateProceedAnyway = null;
    public String stringIdLowFrameRateTitle = null;
    public String stringIdNext = null;
    public String stringIdOk = null;
    public String stringIdPasswordHint = null;
    public String stringIdRememberPassword = null;
    public String stringIdSendBlinkUp = null;
    public String stringIdShowPassword = null;
    public String stringIdSsidHint = null;
    public String stringIdWpsInfo = null;
    public String stringIdWpsPinHint = null;

    public interface TokenAcquireCallback {
        void onError(String str);

        void onSuccess(String str, String str2);
    }

    private static class AcquireSetupTokenHandler extends Handler {
        private WeakReference<Activity> activity;
        private TokenAcquireCallback tokenAcquireCallback;

        public AcquireSetupTokenHandler(Activity activity, TokenAcquireCallback tokenAcquireCallback) {
            this.activity = new WeakReference(activity);
            this.tokenAcquireCallback = tokenAcquireCallback;
        }

        public final void handleMessage(Message msg) {
            if (this.activity.get() != null && !((Activity) this.activity.get()).isFinishing()) {
                String errorMsg = null;
                if (msg.arg1 != 1) {
                    errorMsg = msg.obj;
                } else if (this.tokenAcquireCallback != null) {
                    JSONObject json = msg.obj;
                    try {
                        String token = json.getString("id");
                        try {
                            new BigInteger(token, 16).longValue();
                            this.tokenAcquireCallback.onSuccess(json.getString("plan_id"), token);
                        } catch (NumberFormatException e) {
                            errorMsg = e.getMessage();
                        }
                    } catch (JSONException e2) {
                        errorMsg = e2.getMessage();
                    }
                }
                if (!TextUtils.isEmpty(errorMsg) && this.tokenAcquireCallback != null) {
                    this.tokenAcquireCallback.onError(errorMsg);
                }
            }
        }
    }

    public interface ServerErrorHandler {
    }

    public interface TokenStatusCallback {
        void onError(String str);

        void onSuccess(JSONObject jSONObject);

        void onTimeout();
    }

    private static class TokenStatusHandler extends Handler {
        private final TokenStatusCallback callback;

        public TokenStatusHandler(TokenStatusCallback callback) {
            this.callback = callback;
        }

        public final void handleMessage(Message msg) {
            if (this.callback != null) {
                switch (msg.arg1) {
                    case 0:
                        this.callback.onError((String) msg.obj);
                        return;
                    case 1:
                        this.callback.onSuccess(msg.obj);
                        return;
                    case 2:
                        this.callback.onTimeout();
                        return;
                    default:
                        return;
                }
            }
        }
    }

    public static BlinkupController getInstance() {
        if (instance == null) {
            instance = new BlinkupController("https://api.electricimp.com/v1");
        }
        return instance;
    }

    public final void setPlanID(String planID) {
        this.impController.planID = planID;
    }

    public static String getCurrentWifiSSID(Context context) {
        String str = null;
        if (((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
            try {
                WifiInfo wifiInfo = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo();
                if (!(wifiInfo == null || wifiInfo.getSSID() == null)) {
                    str = wifiInfo.getSSID().replaceAll("\"", "");
                }
            } catch (Exception e) {
                Log.v("BlinkUp", "Error getting the current network");
                Log.v("BlinkUp", Log.getStackTraceString(e));
            }
        }
        return str;
    }

    public static void handleActivityResult$51b9da64(Activity activity, int resultCode) {
        BlinkupController blinkup = getInstance();
        if (resultCode == -1 && blinkup.lastMode != null && !blinkup.lastMode.equals("clear") && blinkup.intentBlinkupComplete != null) {
            activity.startActivity(blinkup.intentBlinkupComplete);
        }
    }

    public final void acquireSetupToken(Activity activity, String apiKey, TokenAcquireCallback tokenAcquireCallback) {
        this.impController.acquireSetupToken(apiKey, new AcquireSetupTokenHandler(activity, tokenAcquireCallback));
    }

    public final void getTokenStatus(TokenStatusCallback callback) {
        ImpController impController = this.impController;
        impController.getTokenStatus(impController.setupToken, new TokenStatusHandler(callback));
    }

    public final void cancelTokenStatusPolling() {
        this.impController.cancelTokenStatusPolling();
    }

    final void saveLastMode(String mode) {
        this.lastMode = mode;
    }

    static void setText(TextView view, String str, int defaultResId) {
        if (str != null) {
            view.setText(str);
        } else {
            view.setText(defaultResId);
        }
    }

    static void setHint(TextView view, String str, int defaultResId) {
        if (str != null) {
            view.setHint(str);
        } else {
            view.setHint(defaultResId);
        }
    }

    static String getCustomStringOrDefault(Context context, String custom, int defaultResId) {
        return custom != null ? custom : context.getString(defaultResId);
    }

    final void addBlinkupIntentFields(Context context, Intent intent) {
        if (!intent.getBooleanExtra("slow", false)) {
            intent.putExtra("trilevel", true);
        }
        intent.setClassName(context, "com.electricimp.blinkup.BlinkupGLActivity");
    }

    final boolean shouldCheckFrameRate(Context context) {
        if (this.enablePowerSaveMessage && this.frameRateCheckEnabled && ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRefreshRate() >= 45.0f) {
            return true;
        }
        return false;
    }

    final boolean isFrameRateTooLow(float framerate) {
        if (framerate >= 45.0f) {
            return false;
        }
        this.frameRateCheckEnabled = false;
        return true;
    }

    private BlinkupController(String baseUrl) {
        this.impController = new ImpController(baseUrl);
    }

    public final void setupDevice$cc378d8(Activity activity, String ssid, String password, String apiKey) {
        if (ssid != null && ssid.length() != 0) {
            Intent intent = new Intent();
            intent.putExtra("mode", "wifi");
            intent.putExtra("ssid", ssid);
            intent.putExtra("pwd", password);
            intent.putExtra("token", this.impController.setupToken);
            intent.putExtra("siteid", this.impController.planID);
            intent.putExtra("apiKey", apiKey);
            intent.putExtra("slow", false);
            addBlinkupIntentFields(activity, intent);
            activity.startActivityForResult(intent, 5);
        }
    }
}
