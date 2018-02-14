package com.instabug.library;

import com.instabug.library.e.c;
import com.instabug.library.internal.a.b;
import com.instabug.library.internal.module.a;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

public class InstabugFeaturesFetcherService extends n {
    protected final void b() throws Exception {
        a aVar = new a();
        final b a = a.a(this);
        final File file = new File(getExternalCacheDir() != null ? getExternalCacheDir() : getCacheDir(), "com.instabug.library.settings");
        try {
            if (!file.exists() || Math.abs(System.currentTimeMillis() - file.lastModified()) > 86400000) {
                InstabugSDKLogger.d(this, "Feature file doesn't exist or too old, fetching features again");
                com.instabug.library.e.a.a.a().a(this, new c.a<String, Throwable>(this) {
                    final /* synthetic */ InstabugFeaturesFetcherService c;

                    public final /* synthetic */ void b(Object obj) {
                        Exception e;
                        String str = (String) obj;
                        try {
                            InstabugSDKLogger.d(this.c, "Features fetched successfully");
                            JSONObject jSONObject = new JSONObject(str);
                            jSONObject.put("device", a.l());
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.CRASH_REPORTING, jSONObject.optBoolean("crash_reporting", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.PUSH_NOTIFICATION, jSONObject.optBoolean("push_notifications", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.WHITE_LABELING, jSONObject.optBoolean("white_label", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.IN_APP_MESSAGING, jSONObject.optBoolean("in_app_messaging", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.MULTIPLE_ATTACHMENTS, jSONObject.optBoolean("multiple_attachments", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.TRACK_USER_STEPS, jSONObject.optBoolean("user_steps", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.CONSOLE_LOGS, jSONObject.optBoolean("console_log", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.INSTABUG_LOGS, jSONObject.optBoolean("ibg_log", true));
                            InstabugFeaturesManager.getInstance().updateFeatureAvailability(Feature.USER_DATA, jSONObject.optBoolean("user_data", true));
                            FileWriter fileWriter = new FileWriter(file);
                            fileWriter.write(jSONObject.toString());
                            fileWriter.close();
                            return;
                        } catch (JSONException e2) {
                            e = e2;
                        } catch (IOException e3) {
                            e = e3;
                        }
                        e.printStackTrace();
                    }

                    public final /* bridge */ /* synthetic */ void a(Object obj) {
                        InstabugSDKLogger.d(this.c, "Something went wrong while fetching features");
                    }
                });
            }
        } catch (Exception e) {
            InstabugSDKLogger.d(this, "Something went wrong while fetching features");
        }
    }
}
