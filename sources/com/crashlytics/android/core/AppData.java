package com.crashlytics.android.core;

final class AppData {
    public final String apiKey;
    public final String buildId;
    public final String installerPackageName;
    public final String packageName;
    public final String versionCode;
    public final String versionName;

    AppData(String apiKey, String buildId, String installerPackageName, String packageName, String versionCode, String versionName) {
        this.apiKey = apiKey;
        this.buildId = buildId;
        this.installerPackageName = installerPackageName;
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }
}
