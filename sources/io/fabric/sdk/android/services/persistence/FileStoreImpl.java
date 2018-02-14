package io.fabric.sdk.android.services.persistence;

import android.content.Context;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;
import java.io.File;

public final class FileStoreImpl implements FileStore {
    private final String contentPath;
    private final Context context;
    private final String legacySupport;

    public FileStoreImpl(Kit kit) {
        if (kit.getContext() == null) {
            throw new IllegalStateException("Cannot get directory before context has been set. Call Fabric.with() first");
        }
        this.context = kit.getContext();
        this.contentPath = kit.getPath();
        this.legacySupport = "Android/" + this.context.getPackageName();
    }

    public final File getFilesDir() {
        File filesDir = this.context.getFilesDir();
        if (filesDir == null) {
            Fabric.getLogger().d("Fabric", "Null File");
        } else if (filesDir.exists() || filesDir.mkdirs()) {
            return filesDir;
        } else {
            Fabric.getLogger().w("Fabric", "Couldn't create file");
        }
        return null;
    }
}
