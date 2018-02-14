package com.rachio.iro.fcm;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rachio.iro.IroApplication;
import java.util.Map;

public class Service extends FirebaseMessagingService {
    private static final String TAG = Service.class.getName();
    private EventHandler eventHandler = new EventHandler();

    public void onCreate() {
        super.onCreate();
        IroApplication.get(this).component().inject(this.eventHandler);
    }

    public final void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived");
        remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            String event = (String) data.get("event");
            if (event != null) {
                Log.d(TAG, "event");
                this.eventHandler.handleEvent(event);
            }
        }
    }
}
