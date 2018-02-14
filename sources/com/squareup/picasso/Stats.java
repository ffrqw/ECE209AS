package com.squareup.picasso;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

final class Stats {
    long averageDownloadSize;
    long averageOriginalBitmapSize;
    long averageTransformedBitmapSize;
    final Cache cache;
    long cacheHits;
    long cacheMisses;
    int downloadCount;
    final Handler handler;
    int originalBitmapCount;
    final HandlerThread statsThread = new HandlerThread("Picasso-Stats", 10);
    long totalDownloadSize;
    long totalOriginalBitmapSize;
    long totalTransformedBitmapSize;
    int transformedBitmapCount;

    private static class StatsHandler extends Handler {
        private final Stats stats;

        public StatsHandler(Looper looper, Stats stats) {
            super(looper);
            this.stats = stats;
        }

        public final void handleMessage(final Message msg) {
            Stats stats;
            switch (msg.what) {
                case 0:
                    stats = this.stats;
                    stats.cacheHits++;
                    return;
                case 1:
                    stats = this.stats;
                    stats.cacheMisses++;
                    return;
                case 2:
                    this.stats.performBitmapDecoded((long) msg.arg1);
                    return;
                case 3:
                    this.stats.performBitmapTransformed((long) msg.arg1);
                    return;
                case 4:
                    this.stats.performDownloadFinished((Long) msg.obj);
                    return;
                default:
                    Picasso.HANDLER.post(new Runnable() {
                        public final void run() {
                            throw new AssertionError("Unhandled stats message." + msg.what);
                        }
                    });
                    return;
            }
        }
    }

    Stats(Cache cache) {
        this.cache = cache;
        this.statsThread.start();
        Utils.flushStackLocalLeaks(this.statsThread.getLooper());
        this.handler = new StatsHandler(this.statsThread.getLooper(), this);
    }

    final void dispatchBitmapDecoded(Bitmap bitmap) {
        processBitmap(bitmap, 2);
    }

    final void dispatchBitmapTransformed(Bitmap bitmap) {
        processBitmap(bitmap, 3);
    }

    final void dispatchCacheHit() {
        this.handler.sendEmptyMessage(0);
    }

    final void performDownloadFinished(Long size) {
        this.downloadCount++;
        this.totalDownloadSize += size.longValue();
        this.averageDownloadSize = this.totalDownloadSize / ((long) this.downloadCount);
    }

    final void performBitmapDecoded(long size) {
        this.originalBitmapCount++;
        this.totalOriginalBitmapSize += size;
        this.averageOriginalBitmapSize = this.totalOriginalBitmapSize / ((long) this.originalBitmapCount);
    }

    final void performBitmapTransformed(long size) {
        this.transformedBitmapCount++;
        this.totalTransformedBitmapSize += size;
        this.averageTransformedBitmapSize = this.totalTransformedBitmapSize / ((long) this.originalBitmapCount);
    }

    private void processBitmap(Bitmap bitmap, int what) {
        this.handler.sendMessage(this.handler.obtainMessage(what, Utils.getBitmapBytes(bitmap), 0));
    }
}
