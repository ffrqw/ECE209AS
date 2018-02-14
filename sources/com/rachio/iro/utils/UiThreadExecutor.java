package com.rachio.iro.utils;

import android.os.Handler;
import android.os.Looper;

public class UiThreadExecutor {
    private Handler handler = new Handler(Looper.getMainLooper());

    public final void execute(Runnable r) {
        this.handler.post(r);
    }
}
