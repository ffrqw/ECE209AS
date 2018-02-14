package com.squareup.picasso;

import android.graphics.Bitmap;
import com.squareup.picasso.Picasso.LoadedFrom;

final class FetchAction extends Action<Object> {
    private Callback callback;
    private final Object target = new Object();

    FetchAction(Picasso picasso, Request data, int memoryPolicy, int networkPolicy, Object tag, String key, Callback callback) {
        super(picasso, null, data, memoryPolicy, networkPolicy, 0, null, key, tag, false);
        this.callback = callback;
    }

    final void complete(Bitmap result, LoadedFrom from) {
    }

    final void error(Exception e) {
    }

    final void cancel() {
        super.cancel();
        this.callback = null;
    }

    final Object getTarget() {
        return this.target;
    }
}
