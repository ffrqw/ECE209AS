package com.instabug.library.internal.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import com.instabug.library.internal.layer.CapturableView.SnapshotPreparationCallback;
import com.instabug.library.util.InstabugSDKLogger;
import java.lang.ref.WeakReference;

public final class a implements c {
    private WeakReference<CapturableView> a;
    private Bitmap b;

    public a(CapturableView capturableView) {
        this.a = new WeakReference(capturableView);
    }

    public final void a(final com.instabug.library.internal.layer.c.a aVar) {
        if (this.a == null || this.a.get() == null || !((CapturableView) this.a.get()).isVisible()) {
            aVar.a();
        }
        ((CapturableView) this.a.get()).snapshot(new SnapshotPreparationCallback(this) {
            final /* synthetic */ a b;

            public final void onSnapshotReady(Bitmap bitmap) {
                this.b.b = bitmap;
                aVar.a();
            }

            public final void onSnapshotFailed() {
                aVar.a();
            }
        });
    }

    public final void a(Canvas canvas) {
        int[] iArr = new int[2];
        ((CapturableView) this.a.get()).getLocationOnScreen(iArr);
        canvas.drawBitmap(this.b, (float) iArr[0], (float) iArr[1], null);
    }

    public final boolean a() {
        if (this.a.get() != null) {
            return true;
        }
        InstabugSDKLogger.w(this, "Capturable view reference no longer exists. Skipping.");
        return false;
    }

    public final Object b() {
        if (this.a == null) {
            return null;
        }
        return this.a.get();
    }
}
