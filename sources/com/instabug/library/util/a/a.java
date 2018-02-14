package com.instabug.library.util.a;

import android.util.Log;
import com.instabug.library.util.InstabugSDKLogger;
import org.json.JSONException;
import org.json.JSONObject;

public class a {
    public static JSONObject a(Throwable th, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            Object name = th.getClass().getName();
            if (str != null) {
                name = name + "-" + str;
            }
            jSONObject.put("name", name);
            StackTraceElement stackTraceElement = null;
            if (th.getStackTrace() != null && th.getStackTrace().length > 0) {
                stackTraceElement = th.getStackTrace()[0];
            }
            if (stackTraceElement == null || stackTraceElement.getFileName() == null) {
                InstabugSDKLogger.w(a.class, "Incomplete crash stacktrace, if you're using Proguard, add the following line to your configuration file to have file name and line number in your crash report:");
                InstabugSDKLogger.w(a.class, "-keepattributes SourceFile,LineNumberTable");
            } else {
                jSONObject.put("location", stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber());
            }
            jSONObject.put("exception", th.toString());
            if (th.getMessage() != null) {
                jSONObject.put("message", th.getMessage());
            }
            jSONObject.put("stackTrace", Log.getStackTraceString(th));
            if (th.getCause() != null) {
                jSONObject.put("cause", a(th.getCause(), str));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }
}
