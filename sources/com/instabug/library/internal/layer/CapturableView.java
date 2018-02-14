package com.instabug.library.internal.layer;

import android.graphics.Bitmap;

public interface CapturableView {

    public interface SnapshotPreparationCallback {
        void onSnapshotFailed();

        void onSnapshotReady(Bitmap bitmap);
    }

    void getLocationOnScreen(int[] iArr);

    boolean isVisible();

    void snapshot(SnapshotPreparationCallback snapshotPreparationCallback);
}
