package com.crashlytics.android.core;

import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SessionReport implements Report {
    private final Map<String, String> customHeaders;
    private final File file;
    private final File[] files;

    public SessionReport(File file) {
        this(file, Collections.emptyMap());
    }

    public SessionReport(File file, Map<String, String> customHeaders) {
        this.file = file;
        this.files = new File[]{file};
        this.customHeaders = new HashMap(customHeaders);
        if (this.file.length() == 0) {
            this.customHeaders.putAll(ReportUploader.HEADER_INVALID_CLS_FILE);
        }
    }

    public final File getFile() {
        return this.file;
    }

    public final File[] getFiles() {
        return this.files;
    }

    public final String getIdentifier() {
        String fileName = getFileName();
        return fileName.substring(0, fileName.lastIndexOf(46));
    }

    public final Map<String, String> getCustomHeaders() {
        return Collections.unmodifiableMap(this.customHeaders);
    }

    public final void remove() {
        Fabric.getLogger().d("CrashlyticsCore", "Removing report at " + this.file.getPath());
        this.file.delete();
    }

    public final String getFileName() {
        return this.file.getName();
    }
}
