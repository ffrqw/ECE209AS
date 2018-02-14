package com.instabug.library.internal.layer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.instabug.library.internal.layer.c.a;
import com.instabug.library.util.InstabugSDKLogger;
import java.lang.ref.WeakReference;

public final class b implements c {
    private WeakReference<GoogleMap> a;
    private WeakReference<View> b;
    private Bitmap c;

    public b(View view, GoogleMap googleMap) {
        this.a = new WeakReference(googleMap);
        this.b = new WeakReference(view);
    }

    public final void a(final a aVar) {
        if (this.a == null || this.a.get() == null || this.b == null || this.b.get() == null || ((View) this.b.get()).getVisibility() != 0) {
            aVar.a();
        }
        ((GoogleMap) this.a.get()).snapshot(new SnapshotReadyCallback(this) {
            final /* synthetic */ b b;

            public final void onSnapshotReady(Bitmap bitmap) {
                this.b.c = bitmap;
                aVar.a();
            }
        });
    }

    public final void a(Canvas canvas) {
        int[] iArr = new int[2];
        ((View) this.b.get()).getLocationOnScreen(iArr);
        canvas.drawBitmap(this.c, (float) iArr[0], (float) iArr[1], null);
    }

    public final boolean a() {
        if (this.a.get() == null || this.b.get() == null) {
            InstabugSDKLogger.w(this, "Registered Google MapView no longer exists. Skipping.");
            return false;
        } else if (((View) this.b.get()).getWindowToken() != null) {
            return true;
        } else {
            InstabugSDKLogger.w(this, "Registered Google MapView is not attached to window. Skipping.");
            return false;
        }
    }

    public final /* synthetic */ Object b() {
        return (View) this.b.get();
    }
}
