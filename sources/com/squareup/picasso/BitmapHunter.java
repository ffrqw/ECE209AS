package com.squareup.picasso;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Build.VERSION;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Picasso.Priority;
import com.squareup.picasso.RequestHandler.Result;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

final class BitmapHunter implements Runnable {
    private static final Object DECODE_LOCK = new Object();
    private static final RequestHandler ERRORING_HANDLER = new RequestHandler() {
        public final boolean canHandleRequest(Request data) {
            return true;
        }

        public final Result load(Request request, int networkPolicy) throws IOException {
            throw new IllegalStateException("Unrecognized type of request: " + request);
        }
    };
    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        protected final /* bridge */ /* synthetic */ Object initialValue() {
            return new StringBuilder("Picasso-");
        }
    };
    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();
    Action action;
    List<Action> actions;
    final Cache cache;
    final Request data;
    final Dispatcher dispatcher;
    Exception exception;
    int exifOrientation;
    Future<?> future;
    final String key;
    LoadedFrom loadedFrom;
    final int memoryPolicy;
    int networkPolicy;
    final Picasso picasso;
    int priority$159b5429;
    final RequestHandler requestHandler;
    Bitmap result;
    int retryCount;
    final int sequence = SEQUENCE_GENERATOR.incrementAndGet();
    final Stats stats;

    private BitmapHunter(Picasso picasso, Dispatcher dispatcher, Cache cache, Stats stats, Action action, RequestHandler requestHandler) {
        this.picasso = picasso;
        this.dispatcher = dispatcher;
        this.cache = cache;
        this.stats = stats;
        this.action = action;
        this.key = action.key;
        this.data = action.request;
        this.priority$159b5429 = action.request.priority$159b5429;
        this.memoryPolicy = action.memoryPolicy;
        this.networkPolicy = action.networkPolicy;
        this.requestHandler = requestHandler;
        this.retryCount = requestHandler.getRetryCount();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        /*
        r28 = this;
        r0 = r28;
        r3 = r0.data;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r4 = r3.uri;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        if (r4 == 0) goto L_0x006c;
    L_0x0008:
        r3 = r3.uri;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = r3.getPath();	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r4 = r3;
    L_0x0013:
        r3 = NAME_BUILDER;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = r3.get();	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = (java.lang.StringBuilder) r3;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r5 = r4.length();	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r5 = r5 + 8;
        r3.ensureCapacity(r5);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r5 = 8;
        r6 = r3.length();	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3.replace(r5, r6, r4);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r4 = java.lang.Thread.currentThread();	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = r3.toString();	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r4.setName(r3);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r0 = r28;
        r3 = r0.picasso;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = r3.loggingEnabled;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        if (r3 == 0) goto L_0x004b;
    L_0x0040:
        r3 = "Hunter";
        r4 = "executing";
        r5 = com.squareup.picasso.Utils.getLogIdsForHunter(r28);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        com.squareup.picasso.Utils.log(r3, r4, r5);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
    L_0x004b:
        r3 = r28.hunt();	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r0 = r28;
        r0.result = r3;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r0 = r28;
        r3 = r0.result;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        if (r3 != 0) goto L_0x0074;
    L_0x0059:
        r0 = r28;
        r3 = r0.dispatcher;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r0 = r28;
        r3.dispatchFailed(r0);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
    L_0x0062:
        r3 = java.lang.Thread.currentThread();
        r4 = "Picasso-Idle";
        r3.setName(r4);
    L_0x006b:
        return;
    L_0x006c:
        r3 = r3.resourceId;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = java.lang.Integer.toHexString(r3);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r4 = r3;
        goto L_0x0013;
    L_0x0074:
        r0 = r28;
        r3 = r0.dispatcher;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r4 = r3.handler;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r3 = r3.handler;	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r5 = 4;
        r0 = r28;
        r3 = r3.obtainMessage(r5, r0);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        r4.sendMessage(r3);	 Catch:{ ResponseException -> 0x0087, IOException -> 0x00ad, OutOfMemoryError -> 0x00d0, Exception -> 0x020b }
        goto L_0x0062;
    L_0x0087:
        r2 = move-exception;
        r3 = r2.networkPolicy;	 Catch:{ all -> 0x0224 }
        r3 = com.squareup.picasso.NetworkPolicy.isOfflineOnly(r3);	 Catch:{ all -> 0x0224 }
        if (r3 == 0) goto L_0x0096;
    L_0x0090:
        r3 = r2.code;	 Catch:{ all -> 0x0224 }
        r4 = 504; // 0x1f8 float:7.06E-43 double:2.49E-321;
        if (r3 == r4) goto L_0x009a;
    L_0x0096:
        r0 = r28;
        r0.exception = r2;	 Catch:{ all -> 0x0224 }
    L_0x009a:
        r0 = r28;
        r3 = r0.dispatcher;	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r3.dispatchFailed(r0);	 Catch:{ all -> 0x0224 }
        r3 = java.lang.Thread.currentThread();
        r4 = "Picasso-Idle";
        r3.setName(r4);
        goto L_0x006b;
    L_0x00ad:
        r2 = move-exception;
        r0 = r28;
        r0.exception = r2;	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r3 = r0.dispatcher;	 Catch:{ all -> 0x0224 }
        r4 = r3.handler;	 Catch:{ all -> 0x0224 }
        r3 = r3.handler;	 Catch:{ all -> 0x0224 }
        r5 = 5;
        r0 = r28;
        r3 = r3.obtainMessage(r5, r0);	 Catch:{ all -> 0x0224 }
        r6 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r4.sendMessageDelayed(r3, r6);	 Catch:{ all -> 0x0224 }
        r3 = java.lang.Thread.currentThread();
        r4 = "Picasso-Idle";
        r3.setName(r4);
        goto L_0x006b;
    L_0x00d0:
        r2 = move-exception;
        r27 = new java.io.StringWriter;	 Catch:{ all -> 0x0224 }
        r27.<init>();	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r0 = r0.stats;	 Catch:{ all -> 0x0224 }
        r24 = r0;
        r3 = new com.squareup.picasso.StatsSnapshot;	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r4 = r0.cache;	 Catch:{ all -> 0x0224 }
        r4 = r4.maxSize();	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r5 = r0.cache;	 Catch:{ all -> 0x0224 }
        r5 = r5.size();	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r6 = r0.cacheHits;	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r8 = r0.cacheMisses;	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r10 = r0.totalDownloadSize;	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r12 = r0.totalOriginalBitmapSize;	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r14 = r0.totalTransformedBitmapSize;	 Catch:{ all -> 0x0224 }
        r0 = r24;
        r0 = r0.averageDownloadSize;	 Catch:{ all -> 0x0224 }
        r16 = r0;
        r0 = r24;
        r0 = r0.averageOriginalBitmapSize;	 Catch:{ all -> 0x0224 }
        r18 = r0;
        r0 = r24;
        r0 = r0.averageTransformedBitmapSize;	 Catch:{ all -> 0x0224 }
        r20 = r0;
        r0 = r24;
        r0 = r0.downloadCount;	 Catch:{ all -> 0x0224 }
        r22 = r0;
        r0 = r24;
        r0 = r0.originalBitmapCount;	 Catch:{ all -> 0x0224 }
        r23 = r0;
        r0 = r24;
        r0 = r0.transformedBitmapCount;	 Catch:{ all -> 0x0224 }
        r24 = r0;
        r25 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0224 }
        r3.<init>(r4, r5, r6, r8, r10, r12, r14, r16, r18, r20, r22, r23, r24, r25);	 Catch:{ all -> 0x0224 }
        r4 = new java.io.PrintWriter;	 Catch:{ all -> 0x0224 }
        r0 = r27;
        r4.<init>(r0);	 Catch:{ all -> 0x0224 }
        r5 = "===============BEGIN PICASSO STATS ===============";
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "Memory Cache Stats";
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Max Cache Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r5 = r3.maxSize;	 Catch:{ all -> 0x0224 }
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Cache Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r5 = r3.size;	 Catch:{ all -> 0x0224 }
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Cache % Full: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r5 = r3.size;	 Catch:{ all -> 0x0224 }
        r5 = (float) r5;	 Catch:{ all -> 0x0224 }
        r6 = r3.maxSize;	 Catch:{ all -> 0x0224 }
        r6 = (float) r6;	 Catch:{ all -> 0x0224 }
        r5 = r5 / r6;
        r6 = 1120403456; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r5 = r5 * r6;
        r6 = (double) r5;	 Catch:{ all -> 0x0224 }
        r6 = java.lang.Math.ceil(r6);	 Catch:{ all -> 0x0224 }
        r5 = (int) r6;	 Catch:{ all -> 0x0224 }
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Cache Hits: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.cacheHits;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r5 = "  Cache Misses: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.cacheMisses;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r5 = "Network Stats";
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Download Count: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r5 = r3.downloadCount;	 Catch:{ all -> 0x0224 }
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Total Download Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.totalDownloadSize;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r5 = "  Average Download Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.averageDownloadSize;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r5 = "Bitmap Stats";
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Total Bitmaps Decoded: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r5 = r3.originalBitmapCount;	 Catch:{ all -> 0x0224 }
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Total Bitmap Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.totalOriginalBitmapSize;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r5 = "  Total Transformed Bitmaps: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r5 = r3.transformedBitmapCount;	 Catch:{ all -> 0x0224 }
        r4.println(r5);	 Catch:{ all -> 0x0224 }
        r5 = "  Total Transformed Bitmap Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.totalTransformedBitmapSize;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r5 = "  Average Bitmap Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.averageOriginalBitmapSize;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r5 = "  Average Transformed Bitmap Size: ";
        r4.print(r5);	 Catch:{ all -> 0x0224 }
        r6 = r3.averageTransformedBitmapSize;	 Catch:{ all -> 0x0224 }
        r4.println(r6);	 Catch:{ all -> 0x0224 }
        r3 = "===============END PICASSO STATS ===============";
        r4.println(r3);	 Catch:{ all -> 0x0224 }
        r4.flush();	 Catch:{ all -> 0x0224 }
        r3 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0224 }
        r4 = r27.toString();	 Catch:{ all -> 0x0224 }
        r3.<init>(r4, r2);	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r0.exception = r3;	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r3 = r0.dispatcher;	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r3.dispatchFailed(r0);	 Catch:{ all -> 0x0224 }
        r3 = java.lang.Thread.currentThread();
        r4 = "Picasso-Idle";
        r3.setName(r4);
        goto L_0x006b;
    L_0x020b:
        r2 = move-exception;
        r0 = r28;
        r0.exception = r2;	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r3 = r0.dispatcher;	 Catch:{ all -> 0x0224 }
        r0 = r28;
        r3.dispatchFailed(r0);	 Catch:{ all -> 0x0224 }
        r3 = java.lang.Thread.currentThread();
        r4 = "Picasso-Idle";
        r3.setName(r4);
        goto L_0x006b;
    L_0x0224:
        r3 = move-exception;
        r4 = java.lang.Thread.currentThread();
        r5 = "Picasso-Idle";
        r4.setName(r5);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.BitmapHunter.run():void");
    }

    private Bitmap hunt() throws IOException {
        int i;
        Bitmap bitmap = null;
        if (MemoryPolicy.shouldReadFromMemoryCache(this.memoryPolicy)) {
            bitmap = this.cache.get(this.key);
            if (bitmap != null) {
                this.stats.dispatchCacheHit();
                this.loadedFrom = LoadedFrom.MEMORY;
                if (this.picasso.loggingEnabled) {
                    Utils.log("Hunter", "decoded", this.data.logId(), "from cache");
                }
                return bitmap;
            }
        }
        if (this.retryCount == 0) {
            i = NetworkPolicy.OFFLINE.index;
        } else {
            i = this.networkPolicy;
        }
        this.networkPolicy = i;
        Result result = this.requestHandler.load(this.data, this.networkPolicy);
        if (result != null) {
            this.loadedFrom = result.getLoadedFrom();
            this.exifOrientation = result.getExifOrientation();
            bitmap = result.getBitmap();
            if (bitmap == null) {
                Source source = result.getSource();
                try {
                    Request request = this.data;
                    BufferedSource buffer = Okio.buffer(source);
                    boolean isWebPFile = Utils.isWebPFile(buffer);
                    Object obj = (!request.purgeable || VERSION.SDK_INT >= 21) ? null : 1;
                    Options createBitmapOptions = RequestHandler.createBitmapOptions(request);
                    boolean requiresInSampleSize = RequestHandler.requiresInSampleSize(createBitmapOptions);
                    if (isWebPFile || obj != null) {
                        byte[] readByteArray = buffer.readByteArray();
                        if (requiresInSampleSize) {
                            BitmapFactory.decodeByteArray(readByteArray, 0, readByteArray.length, createBitmapOptions);
                            RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, createBitmapOptions, request);
                        }
                        bitmap = BitmapFactory.decodeByteArray(readByteArray, 0, readByteArray.length, createBitmapOptions);
                    } else {
                        InputStream markableInputStream;
                        InputStream inputStream = buffer.inputStream();
                        if (requiresInSampleSize) {
                            markableInputStream = new MarkableInputStream(inputStream);
                            markableInputStream.allowMarksToExpire(false);
                            long savePosition = markableInputStream.savePosition(1024);
                            BitmapFactory.decodeStream(markableInputStream, null, createBitmapOptions);
                            RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, createBitmapOptions, request);
                            markableInputStream.reset(savePosition);
                            markableInputStream.allowMarksToExpire(true);
                        } else {
                            markableInputStream = inputStream;
                        }
                        bitmap = BitmapFactory.decodeStream(markableInputStream, null, createBitmapOptions);
                        if (bitmap == null) {
                            throw new IOException("Failed to decode stream.");
                        }
                    }
                } finally {
                    try {
                        source.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        if (bitmap != null) {
            if (this.picasso.loggingEnabled) {
                Utils.log("Hunter", "decoded", this.data.logId());
            }
            this.stats.dispatchBitmapDecoded(bitmap);
            Request request2 = this.data;
            if (request2.needsMatrixTransform() || request2.hasCustomTransformations()) {
                obj = 1;
            } else {
                obj = null;
            }
            if (!(obj == null && this.exifOrientation == 0)) {
                synchronized (DECODE_LOCK) {
                    if (this.data.needsMatrixTransform() || this.exifOrientation != 0) {
                        Bitmap createBitmap;
                        Request request3 = this.data;
                        int i2 = this.exifOrientation;
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        boolean z = request3.onlyScaleDown;
                        int i3 = 0;
                        Matrix matrix = new Matrix();
                        if (request3.needsMatrixTransform() || i2 != 0) {
                            int i4;
                            int i5;
                            int i6 = request3.targetWidth;
                            i = request3.targetHeight;
                            float f = request3.rotationDegrees;
                            if (f != 0.0f) {
                                double cos = Math.cos(Math.toRadians((double) f));
                                double sin = Math.sin(Math.toRadians((double) f));
                                double d;
                                double d2;
                                double d3;
                                double d4;
                                if (request3.hasRotationPivot) {
                                    matrix.setRotate(f, request3.rotationPivotX, request3.rotationPivotY);
                                    d = (((double) request3.rotationPivotX) * (1.0d - cos)) + (((double) request3.rotationPivotY) * sin);
                                    d2 = (((double) request3.rotationPivotY) * (1.0d - cos)) - (((double) request3.rotationPivotX) * sin);
                                    d3 = (((double) request3.targetWidth) * cos) + d;
                                    d4 = (((double) request3.targetWidth) * sin) + d2;
                                    double d5 = ((((double) request3.targetWidth) * cos) + d) - (((double) request3.targetHeight) * sin);
                                    double d6 = ((((double) request3.targetWidth) * sin) + d2) + (((double) request3.targetHeight) * cos);
                                    sin = d - (sin * ((double) request3.targetHeight));
                                    cos = (cos * ((double) request3.targetHeight)) + d2;
                                    i6 = (int) Math.floor(Math.max(sin, Math.max(d5, Math.max(d, d3))) - Math.min(sin, Math.min(d5, Math.min(d, d3))));
                                    i = (int) Math.floor(Math.max(cos, Math.max(d6, Math.max(d2, d4))) - Math.min(cos, Math.min(d6, Math.min(d2, d4))));
                                } else {
                                    matrix.setRotate(f);
                                    d = ((double) request3.targetWidth) * cos;
                                    d2 = ((double) request3.targetWidth) * sin;
                                    d3 = (((double) request3.targetWidth) * cos) - (((double) request3.targetHeight) * sin);
                                    d4 = (((double) request3.targetWidth) * sin) + (((double) request3.targetHeight) * cos);
                                    sin = -(sin * ((double) request3.targetHeight));
                                    cos *= (double) request3.targetHeight;
                                    i6 = (int) Math.floor(Math.max(sin, Math.max(d3, Math.max(0.0d, d))) - Math.min(sin, Math.min(d3, Math.min(0.0d, d))));
                                    i = (int) Math.floor(Math.max(cos, Math.max(d4, Math.max(0.0d, d2))) - Math.min(cos, Math.min(d4, Math.min(0.0d, d2))));
                                }
                            }
                            if (i2 != 0) {
                                int i7;
                                switch (i2) {
                                    case 3:
                                    case 4:
                                        i7 = 180;
                                        break;
                                    case 5:
                                    case 6:
                                        i7 = 90;
                                        break;
                                    case 7:
                                    case 8:
                                        i7 = 270;
                                        break;
                                    default:
                                        i7 = 0;
                                        break;
                                }
                                switch (i2) {
                                    case 2:
                                    case 4:
                                    case 5:
                                    case 7:
                                        i4 = -1;
                                        break;
                                    default:
                                        i4 = 1;
                                        break;
                                }
                                if (i7 != 0) {
                                    matrix.preRotate((float) i7);
                                    if (i7 == 90 || i7 == 270) {
                                        i5 = i6;
                                        i6 = i;
                                        i = i5;
                                    }
                                }
                                if (i4 != 1) {
                                    matrix.postScale((float) i4, 1.0f);
                                }
                            }
                            i2 = i;
                            int i8 = i6;
                            float f2;
                            if (request3.centerCrop) {
                                float f3;
                                int i9;
                                f = i8 != 0 ? ((float) i8) / ((float) width) : ((float) i2) / ((float) height);
                                f2 = i2 != 0 ? ((float) i2) / ((float) height) : ((float) i8) / ((float) width);
                                if (f > f2) {
                                    i6 = (int) Math.ceil((double) (((float) height) * (f2 / f)));
                                    if ((request3.centerCropGravity & 48) == 48) {
                                        i = 0;
                                    } else if ((request3.centerCropGravity & 80) == 80) {
                                        i = height - i6;
                                    } else {
                                        i = (height - i6) / 2;
                                    }
                                    f3 = f;
                                    f = ((float) i2) / ((float) i6);
                                    i3 = width;
                                    i5 = i6;
                                    i6 = 0;
                                    i9 = i;
                                    i = i5;
                                } else if (f < f2) {
                                    i4 = (int) Math.ceil((double) (((float) width) * (f / f2)));
                                    if ((request3.centerCropGravity & 3) == 3) {
                                        i = 0;
                                    } else if ((request3.centerCropGravity & 5) == 5) {
                                        i = width - i4;
                                    } else {
                                        i = (width - i4) / 2;
                                    }
                                    f3 = ((float) i8) / ((float) i4);
                                    i9 = 0;
                                    i3 = i4;
                                    f = f2;
                                    i6 = i;
                                    i = height;
                                } else {
                                    f = f2;
                                    f3 = f2;
                                    i = height;
                                    i6 = 0;
                                    i9 = 0;
                                    i3 = width;
                                }
                                if (shouldResize(z, width, height, i8, i2)) {
                                    matrix.preScale(f3, f);
                                }
                                height = i;
                                width = i3;
                                i3 = i9;
                                i = i6;
                            } else if (request3.centerInside) {
                                r5 = i8 != 0 ? ((float) i8) / ((float) width) : ((float) i2) / ((float) height);
                                f2 = i2 != 0 ? ((float) i2) / ((float) height) : ((float) i8) / ((float) width);
                                if (r5 >= f2) {
                                    r5 = f2;
                                }
                                if (shouldResize(z, width, height, i8, i2)) {
                                    matrix.preScale(r5, r5);
                                }
                                i = 0;
                            } else if (!((i8 == 0 && i2 == 0) || (i8 == width && i2 == height))) {
                                f2 = i8 != 0 ? ((float) i8) / ((float) width) : ((float) i2) / ((float) height);
                                if (i2 != 0) {
                                    r5 = ((float) i2) / ((float) height);
                                } else {
                                    r5 = ((float) i8) / ((float) width);
                                }
                                if (shouldResize(z, width, height, i8, i2)) {
                                    matrix.preScale(f2, r5);
                                }
                            }
                            createBitmap = Bitmap.createBitmap(bitmap, i, i3, width, height, matrix, true);
                            if (createBitmap != bitmap) {
                                bitmap.recycle();
                                bitmap = createBitmap;
                            }
                            if (this.picasso.loggingEnabled) {
                                Utils.log("Hunter", "transformed", this.data.logId());
                            }
                        }
                        i = 0;
                        createBitmap = Bitmap.createBitmap(bitmap, i, i3, width, height, matrix, true);
                        if (createBitmap != bitmap) {
                            bitmap.recycle();
                            bitmap = createBitmap;
                        }
                        if (this.picasso.loggingEnabled) {
                            Utils.log("Hunter", "transformed", this.data.logId());
                        }
                    }
                    if (this.data.hasCustomTransformations()) {
                        bitmap = applyCustomTransformations(this.data.transformations, bitmap);
                        if (this.picasso.loggingEnabled) {
                            Utils.log("Hunter", "transformed", this.data.logId(), "from custom transformations");
                        }
                    }
                }
                if (bitmap != null) {
                    this.stats.dispatchBitmapTransformed(bitmap);
                }
            }
        }
        return bitmap;
    }

    final void detach(Action action) {
        int i = 1;
        int i2 = 0;
        boolean detached = false;
        if (this.action == action) {
            this.action = null;
            detached = true;
        } else if (this.actions != null) {
            detached = this.actions.remove(action);
        }
        if (detached && action.request.priority$159b5429 == this.priority$159b5429) {
            int i3;
            int i4 = Priority.LOW$159b5429;
            int i5 = (this.actions == null || this.actions.isEmpty()) ? 0 : 1;
            if (this.action == null && i5 == 0) {
                i3 = 0;
            } else {
                i3 = 1;
            }
            if (i3 != 0) {
                if (this.action != null) {
                    i = this.action.request.priority$159b5429;
                } else {
                    i = i4;
                }
                if (i5 != 0) {
                    i4 = this.actions.size();
                    while (i2 < i4) {
                        i5 = ((Action) this.actions.get(i2)).request.priority$159b5429;
                        if (i5 - 1 <= i - 1) {
                            i5 = i;
                        }
                        i2++;
                        i = i5;
                    }
                }
            }
            this.priority$159b5429 = i;
        }
        if (this.picasso.loggingEnabled) {
            Utils.log("Hunter", "removed", action.request.logId(), Utils.getLogIdsForHunter(this, "from "));
        }
    }

    final boolean cancel() {
        if (this.action != null) {
            return false;
        }
        if ((this.actions == null || this.actions.isEmpty()) && this.future != null && this.future.cancel(false)) {
            return true;
        }
        return false;
    }

    final boolean isCancelled() {
        return this.future != null && this.future.isCancelled();
    }

    private static Bitmap applyCustomTransformations(List<Transformation> transformations, Bitmap result) {
        int i = 0;
        int count = transformations.size();
        while (i < count) {
            final Transformation transformation = (Transformation) transformations.get(i);
            try {
                Bitmap newResult = transformation.transform$34dbf037();
                if (newResult == null) {
                    final StringBuilder builder = new StringBuilder("Transformation ").append(transformation.key()).append(" returned null after ").append(i).append(" previous transformation(s).\n\nTransformation list:\n");
                    for (Transformation t : transformations) {
                        builder.append(t.key()).append('\n');
                    }
                    Picasso.HANDLER.post(new Runnable() {
                        public final void run() {
                            throw new NullPointerException(builder.toString());
                        }
                    });
                    return null;
                } else if (newResult == result && result.isRecycled()) {
                    Picasso.HANDLER.post(new Runnable() {
                        public final void run() {
                            throw new IllegalStateException("Transformation " + transformation.key() + " returned input Bitmap but recycled it.");
                        }
                    });
                    return null;
                } else if (newResult == result || result.isRecycled()) {
                    result = newResult;
                    i++;
                } else {
                    Picasso.HANDLER.post(new Runnable() {
                        public final void run() {
                            throw new IllegalStateException("Transformation " + transformation.key() + " mutated input Bitmap but failed to recycle the original.");
                        }
                    });
                    return null;
                }
            } catch (final RuntimeException e) {
                Picasso.HANDLER.post(new Runnable() {
                    public final void run() {
                        throw new RuntimeException("Transformation " + transformation.key() + " crashed with exception.", e);
                    }
                });
                return null;
            }
        }
        return result;
    }

    private static boolean shouldResize(boolean onlyScaleDown, int inWidth, int inHeight, int targetWidth, int targetHeight) {
        return !onlyScaleDown || ((targetWidth != 0 && inWidth > targetWidth) || (targetHeight != 0 && inHeight > targetHeight));
    }

    static BitmapHunter forRequest(Picasso picasso, Dispatcher dispatcher, Cache cache, Stats stats, Action action) {
        Request request = action.request;
        List<RequestHandler> requestHandlers = picasso.getRequestHandlers();
        int count = requestHandlers.size();
        for (int i = 0; i < count; i++) {
            RequestHandler requestHandler = (RequestHandler) requestHandlers.get(i);
            if (requestHandler.canHandleRequest(request)) {
                return new BitmapHunter(picasso, dispatcher, cache, stats, action, requestHandler);
            }
        }
        return new BitmapHunter(picasso, dispatcher, cache, stats, action, ERRORING_HANDLER);
    }
}
