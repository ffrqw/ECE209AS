package com.rachio.iro;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import com.rachio.iro.model.apionly.LoginResponse;
import com.rachio.iro.model.user.UserCredentials;

public class PrefsWrapper {
    private final SharedPreferences preferences;

    public PrefsWrapper(Context context) {
        this.preferences = context.getSharedPreferences("SECURE", 0);
    }

    public final void storeLoggedInUserInfo(LoginResponse userInfo) {
        String userId = userInfo.userId;
        UserCredentials credentials = userInfo.getUserCredentials();
        String apiKey = credentials.apiKey;
        String secretKey = credentials.secretKey;
        String messagingKey = credentials.messagingAuthKey;
        String accessToken = credentials.accessToken;
        Editor edit = this.preferences.edit();
        edit.putString("com.rachio.iro.LOGGED_IN_USER_ID", userId);
        edit.putString("com.rachio.iro.LOGGED_IN_USER_API_KEY", apiKey);
        edit.putString("com.rachio.iro.LOGGED_IN_USER_SECRET_KEY", secretKey);
        edit.putString("com.rachio.iro.LOGGED_IN_USER_MESSAGING_KEY", messagingKey);
        edit.putString("com.rachio.iro.LOGGED_IN_USER_ACCESS_TOKEN", accessToken);
        edit.commit();
    }

    public final String getLoggedInUserId() {
        return this.preferences.getString("com.rachio.iro.LOGGED_IN_USER_ID", null);
    }

    public final boolean isUserLoggedIn() {
        return (getLoggedInUserId() == null || getLoggedInUserAccessToken() == null) ? false : true;
    }

    public final String getLoggedInUserAccessToken() {
        return this.preferences.getString("com.rachio.iro.LOGGED_IN_USER_ACCESS_TOKEN", null);
    }

    public final String getSelectedDeviceId() {
        return this.preferences.getString("com.rachio.iro.LOGGED_IN_USER_SELECTED_DEVICE", null);
    }

    public final void setSelectedDeviceId(String deviceId) {
        Editor e = this.preferences.edit();
        if (deviceId != null) {
            e.putString("com.rachio.iro.LOGGED_IN_USER_SELECTED_DEVICE", deviceId);
        } else {
            e.remove("com.rachio.iro.LOGGED_IN_USER_SELECTED_DEVICE");
        }
        e.commit();
    }

    public final void clear() {
        this.preferences.edit().clear().putBoolean("com.rachio.iro.WELCOME_SHOWN", welcomeShown()).commit();
    }

    public final UserCredentials getLoggedInUserCredentials() {
        if (getLoggedInUserId() == null) {
            return null;
        }
        return new UserCredentials(null, null, this.preferences.getString("com.rachio.iro.LOGGED_IN_USER_API_KEY", null), this.preferences.getString("com.rachio.iro.LOGGED_IN_USER_SECRET_KEY", null), this.preferences.getString("com.rachio.iro.LOGGED_IN_USER_MESSAGING_KEY", null), getLoggedInUserAccessToken());
    }

    public final void stashGen2NetworkId(int id) {
        this.preferences.edit().putInt("com.rachio.iro.STASHED_NETWORK_ID", id);
    }

    public final int getStashedGen2NetworkId() {
        return this.preferences.getInt("com.rachio.iro.STASHED_NETWORK_ID", -1);
    }

    public final void clearGen2NetworkId() {
        this.preferences.edit().remove("com.rachio.iro.STASHED_NETWORK_ID");
    }

    public final void stashProvSession(int sessionId, byte[] key) {
        this.preferences.edit().putInt("com.rachio.iro.STASHED_SESSION_ID", sessionId).putString("com.rachio.iro.STASHED_SESSION_KEY", Base64.encodeToString(key, 0)).commit();
        this.preferences.edit().clear();
    }

    public final int getStashedSessionId() {
        return this.preferences.getInt("com.rachio.iro.STASHED_SESSION_ID", -1);
    }

    public final byte[] getStashedSessionKey() {
        String encodedKey = this.preferences.getString("com.rachio.iro.STASHED_SESSION_KEY", null);
        if (encodedKey != null) {
            return Base64.decode(encodedKey, 0);
        }
        return null;
    }

    public final boolean welcomeShown() {
        return this.preferences.getBoolean("com.rachio.iro.WELCOME_SHOWN", false);
    }

    public final void onWelcomeShown() {
        this.preferences.edit().putBoolean("com.rachio.iro.WELCOME_SHOWN", true).commit();
    }
}
