package com.crashlytics.android.core;

import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class InvalidSessionReport implements Report {
    private final Map<String, String> customHeaders = new HashMap(ReportUploader.HEADER_INVALID_CLS_FILE);
    private final File[] files;
    private final String identifier;

    public InvalidSessionReport(String identifier, File[] files) {
        this.files = files;
        this.identifier = identifier;
    }

    public final String getFileName() {
        return this.files[0].getName();
    }

    public final String getIdentifier() {
        return this.identifier;
    }

    public final File getFile() {
        return this.files[0];
    }

    public final File[] getFiles() {
        return this.files;
    }

    public final Map<String, String> getCustomHeaders() {
        return Collections.unmodifiableMap(this.customHeaders);
    }

    public final void remove() {
        for (File file : this.files) {
            Fabric.getLogger().d("CrashlyticsCore", "Removing invalid report file at " + file.getPath());
            file.delete();
        }
    }
}
