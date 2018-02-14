package com.crashlytics.android.core;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.CommonUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

final class MetaDataStore {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final File filesDir;

    public MetaDataStore(File filesDir) {
        this.filesDir = filesDir;
    }

    public final void writeUserData(String sessionId, final UserMetaData data) {
        Exception e;
        Throwable th;
        File f = getUserDataFileForSession(sessionId);
        Writer writer = null;
        try {
            String userDataString = new JSONObject() {
            }.toString();
            Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), UTF_8));
            try {
                writer2.write(userDataString);
                writer2.flush();
                CommonUtils.closeOrLog(writer2, "Failed to close user metadata file.");
                writer = writer2;
            } catch (Exception e2) {
                e = e2;
                writer = writer2;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "Error serializing user metadata.", e);
                    CommonUtils.closeOrLog(writer, "Failed to close user metadata file.");
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.closeOrLog(writer, "Failed to close user metadata file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                writer = writer2;
                CommonUtils.closeOrLog(writer, "Failed to close user metadata file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e("CrashlyticsCore", "Error serializing user metadata.", e);
            CommonUtils.closeOrLog(writer, "Failed to close user metadata file.");
        }
    }

    public final UserMetaData readUserData(String sessionId) {
        Exception e;
        Throwable th;
        File f = getUserDataFileForSession(sessionId);
        if (!f.exists()) {
            return UserMetaData.EMPTY;
        }
        InputStream is = null;
        try {
            InputStream is2 = new FileInputStream(f);
            try {
                JSONObject jSONObject = new JSONObject(CommonUtils.streamToString(is2));
                UserMetaData userMetaData = new UserMetaData(valueOrNull(jSONObject, "userId"), valueOrNull(jSONObject, "userName"), valueOrNull(jSONObject, "userEmail"));
                CommonUtils.closeOrLog(is2, "Failed to close user metadata file.");
                return userMetaData;
            } catch (Exception e2) {
                e = e2;
                is = is2;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "Error deserializing user metadata.", e);
                    CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
                    return UserMetaData.EMPTY;
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                is = is2;
                CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e("CrashlyticsCore", "Error deserializing user metadata.", e);
            CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
            return UserMetaData.EMPTY;
        }
    }

    public final void writeKeyData(String sessionId, Map<String, String> keyData) {
        Exception e;
        Throwable th;
        File f = getKeysFileForSession(sessionId);
        Writer writer = null;
        try {
            String keyDataString = new JSONObject(keyData).toString();
            Writer writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), UTF_8));
            try {
                writer2.write(keyDataString);
                writer2.flush();
                CommonUtils.closeOrLog(writer2, "Failed to close key/value metadata file.");
                writer = writer2;
            } catch (Exception e2) {
                e = e2;
                writer = writer2;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "Error serializing key/value metadata.", e);
                    CommonUtils.closeOrLog(writer, "Failed to close key/value metadata file.");
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.closeOrLog(writer, "Failed to close key/value metadata file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                writer = writer2;
                CommonUtils.closeOrLog(writer, "Failed to close key/value metadata file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e("CrashlyticsCore", "Error serializing key/value metadata.", e);
            CommonUtils.closeOrLog(writer, "Failed to close key/value metadata file.");
        }
    }

    public final Map<String, String> readKeyData(String sessionId) {
        Exception e;
        Throwable th;
        File f = getKeysFileForSession(sessionId);
        if (!f.exists()) {
            return Collections.emptyMap();
        }
        InputStream is = null;
        try {
            InputStream is2 = new FileInputStream(f);
            try {
                Map<String, String> jsonToKeysData = jsonToKeysData(CommonUtils.streamToString(is2));
                CommonUtils.closeOrLog(is2, "Failed to close user metadata file.");
                return jsonToKeysData;
            } catch (Exception e2) {
                e = e2;
                is = is2;
                try {
                    Fabric.getLogger().e("CrashlyticsCore", "Error deserializing user metadata.", e);
                    CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
                    return Collections.emptyMap();
                } catch (Throwable th2) {
                    th = th2;
                    CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                is = is2;
                CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Fabric.getLogger().e("CrashlyticsCore", "Error deserializing user metadata.", e);
            CommonUtils.closeOrLog(is, "Failed to close user metadata file.");
            return Collections.emptyMap();
        }
    }

    private File getUserDataFileForSession(String sessionId) {
        return new File(this.filesDir, sessionId + "user.meta");
    }

    private File getKeysFileForSession(String sessionId) {
        return new File(this.filesDir, sessionId + "keys.meta");
    }

    private static Map<String, String> jsonToKeysData(String json) throws JSONException {
        JSONObject dataObj = new JSONObject(json);
        Map<String, String> keyData = new HashMap();
        Iterator<String> keyIter = dataObj.keys();
        while (keyIter.hasNext()) {
            String key = (String) keyIter.next();
            keyData.put(key, valueOrNull(dataObj, key));
        }
        return keyData;
    }

    private static String valueOrNull(JSONObject json, String key) {
        return !json.isNull(key) ? json.optString(key, null) : null;
    }
}
