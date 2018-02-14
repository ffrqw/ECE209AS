package com.rachio.iro.fcm;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.rachio.iro.IroApplication;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.cloud.RestClient.HttpResponseErrorHandler;
import com.rachio.iro.model.apionly.FCMAddTokenRequest;
import com.rachio.iro.model.apionly.FCMAddTokenResponse;

public class IDService extends FirebaseInstanceIdService {
    private static final String TAG = IDService.class.getName();

    public static void sendToken(RestClient restClient, String userId) {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null) {
            FCMAddTokenRequest fcmAddToken = new FCMAddTokenRequest();
            fcmAddToken.personId = userId;
            fcmAddToken.token = token;
            restClient.postObject(FCMAddTokenResponse.class, fcmAddToken, new HttpResponseErrorHandler());
        }
    }

    public final void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d(TAG, "onTokenRefresh");
        IroApplication app = IroApplication.get(this);
        String userId = app.getPrefsWrapper().getLoggedInUserId();
        if (userId != null) {
            sendToken(app.getRestClient(), userId);
        }
    }
}
