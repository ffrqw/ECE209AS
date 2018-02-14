package com.crashlytics.android.beta;

final class BuildProperties {
    public final String buildId;
    public final String packageName;
    public final String versionCode;
    public final String versionName;

    BuildProperties(String versionCode, String versionName, String buildId, String packageName) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.buildId = buildId;
        this.packageName = packageName;
    }
}
