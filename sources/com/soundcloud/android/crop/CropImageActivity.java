package com.soundcloud.android.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.view.View;
import android.view.View.OnClickListener;
import com.rachio.iro.R;
import com.soundcloud.android.crop.ImageViewTouchBase.Recycler;
import com.soundcloud.android.crop.MonitoredActivity.LifeCycleListener;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class CropImageActivity extends MonitoredActivity {
    private int aspectX;
    private int aspectY;
    private HighlightView cropView;
    private int exifRotation;
    private final Handler handler = new Handler();
    private CropImageView imageView;
    private boolean isSaving;
    private int maxX;
    private int maxY;
    private RotateBitmap rotateBitmap;
    private int sampleSize;
    private Uri saveUri;
    private Uri sourceUri;

    private class Cropper {
        private Cropper() {
        }

        static /* synthetic */ void access$700(Cropper x0) {
            if (CropImageActivity.this.rotateBitmap != null) {
                int i;
                boolean z;
                HighlightView highlightView = new HighlightView(CropImageActivity.this.imageView);
                int width = CropImageActivity.this.rotateBitmap.getWidth();
                int height = CropImageActivity.this.rotateBitmap.getHeight();
                Rect rect = new Rect(0, 0, width, height);
                int min = (Math.min(width, height) << 2) / 5;
                if (CropImageActivity.this.aspectX == 0 || CropImageActivity.this.aspectY == 0) {
                    i = min;
                } else if (CropImageActivity.this.aspectX > CropImageActivity.this.aspectY) {
                    i = (CropImageActivity.this.aspectY * min) / CropImageActivity.this.aspectX;
                } else {
                    int i2 = min;
                    min = (CropImageActivity.this.aspectX * min) / CropImageActivity.this.aspectY;
                    i = i2;
                }
                width = (width - min) / 2;
                height = (height - i) / 2;
                RectF rectF = new RectF((float) width, (float) height, (float) (min + width), (float) (i + height));
                Matrix unrotatedMatrix = CropImageActivity.this.imageView.getUnrotatedMatrix();
                if (CropImageActivity.this.aspectX == 0 || CropImageActivity.this.aspectY == 0) {
                    z = false;
                } else {
                    z = true;
                }
                highlightView.setup(unrotatedMatrix, rect, rectF, z);
                CropImageView access$100 = CropImageActivity.this.imageView;
                access$100.highlightViews.add(highlightView);
                access$100.invalidate();
            }
        }
    }

    static /* synthetic */ void access$000(CropImageActivity x0) {
        if (x0.cropView != null && !x0.isSaving) {
            x0.isSaving = true;
            HighlightView highlightView = x0.cropView;
            float f = (float) x0.sampleSize;
            Rect rect = new Rect((int) (highlightView.cropRect.left * f), (int) (highlightView.cropRect.top * f), (int) (highlightView.cropRect.right * f), (int) (highlightView.cropRect.bottom * f));
            int width = rect.width();
            int height = rect.height();
            if (x0.maxX > 0 && x0.maxY > 0 && (width > x0.maxX || height > x0.maxY)) {
                float f2 = ((float) width) / ((float) height);
                if (((float) x0.maxX) / ((float) x0.maxY) > f2) {
                    height = x0.maxY;
                    width = (int) ((((float) x0.maxY) * f2) + 0.5f);
                } else {
                    width = x0.maxX;
                    height = (int) ((((float) x0.maxX) / f2) + 0.5f);
                }
            }
            try {
                final Bitmap decodeRegionCrop = x0.decodeRegionCrop(rect, width, height);
                if (decodeRegionCrop != null) {
                    x0.imageView.setImageRotateBitmapResetBase(new RotateBitmap(decodeRegionCrop, x0.exifRotation), true);
                    x0.imageView.center();
                    x0.imageView.highlightViews.clear();
                }
                if (decodeRegionCrop != null) {
                    CropUtil.startBackgroundJob(x0, null, x0.getResources().getString(R.string.crop__saving), new Runnable() {
                        public final void run() {
                            CropImageActivity.access$900(CropImageActivity.this, decodeRegionCrop);
                        }
                    }, x0.handler);
                } else {
                    x0.finish();
                }
            } catch (Throwable e) {
                x0.setResultException(e);
                x0.finish();
            }
        }
    }

    public final /* bridge */ /* synthetic */ void addLifeCycleListener(LifeCycleListener lifeCycleListener) {
        super.addLifeCycleListener(lifeCycleListener);
    }

    public final /* bridge */ /* synthetic */ void removeLifeCycleListener(LifeCycleListener lifeCycleListener) {
        super.removeLifeCycleListener(lifeCycleListener);
    }

    public void onCreate(Bundle icicle) {
        Closeable openInputStream;
        Throwable e;
        super.onCreate(icicle);
        requestWindowFeature(1);
        if (VERSION.SDK_INT >= 19) {
            getWindow().clearFlags(67108864);
        }
        setContentView(R.layout.crop__activity_crop);
        this.imageView = (CropImageView) findViewById(R.id.crop_image);
        this.imageView.context = this;
        this.imageView.setRecycler(new Recycler() {
            public final void recycle(Bitmap b) {
                b.recycle();
                System.gc();
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
            public final void onClick(View v) {
                CropImageActivity.this.setResult(0);
                CropImageActivity.this.finish();
            }
        });
        findViewById(R.id.btn_done).setOnClickListener(new OnClickListener() {
            public final void onClick(View v) {
                CropImageActivity.access$000(CropImageActivity.this);
            }
        });
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.aspectX = extras.getInt("aspect_x");
            this.aspectY = extras.getInt("aspect_y");
            this.maxX = extras.getInt("max_x");
            this.maxY = extras.getInt("max_y");
            this.saveUri = (Uri) extras.getParcelable("output");
        }
        this.sourceUri = intent.getData();
        if (this.sourceUri != null) {
            this.exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(this, getContentResolver(), this.sourceUri));
            try {
                this.sampleSize = calculateBitmapSampleSize(this.sourceUri);
                openInputStream = getContentResolver().openInputStream(this.sourceUri);
                try {
                    Options options = new Options();
                    options.inSampleSize = this.sampleSize;
                    this.rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(openInputStream, null, options), this.exifRotation);
                    CropUtil.closeSilently(openInputStream);
                } catch (IOException e2) {
                    e = e2;
                    try {
                        Log.e("Error reading image: " + e.getMessage(), e);
                        setResultException(e);
                        CropUtil.closeSilently(openInputStream);
                        if (this.rotateBitmap == null) {
                            finish();
                        } else if (!isFinishing()) {
                            this.imageView.setImageRotateBitmapResetBase(this.rotateBitmap, true);
                            CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait), new Runnable() {
                                public final void run() {
                                    final CountDownLatch latch = new CountDownLatch(1);
                                    CropImageActivity.this.handler.post(new Runnable() {
                                        public final void run() {
                                            if (CropImageActivity.this.imageView.getScale() == 1.0f) {
                                                CropImageActivity.this.imageView.center();
                                            }
                                            latch.countDown();
                                        }
                                    });
                                    try {
                                        latch.await();
                                        Cropper cropper = new Cropper();
                                        cropper.this$0.handler.post(new Runnable() {
                                            public final void run() {
                                                Cropper.access$700(Cropper.this);
                                                CropImageActivity.this.imageView.invalidate();
                                                if (CropImageActivity.this.imageView.highlightViews.size() == 1) {
                                                    CropImageActivity.this.cropView = (HighlightView) CropImageActivity.this.imageView.highlightViews.get(0);
                                                    CropImageActivity.this.cropView.setFocus(true);
                                                }
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, this.handler);
                        }
                    } catch (Throwable th) {
                        e = th;
                        CropUtil.closeSilently(openInputStream);
                        throw e;
                    }
                } catch (OutOfMemoryError e3) {
                    e = e3;
                    Log.e("OOM reading image: " + e.getMessage(), e);
                    setResultException(e);
                    CropUtil.closeSilently(openInputStream);
                    if (this.rotateBitmap == null) {
                        finish();
                    } else if (!isFinishing()) {
                        this.imageView.setImageRotateBitmapResetBase(this.rotateBitmap, true);
                        CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait), /* anonymous class already generated */, this.handler);
                    }
                }
            } catch (IOException e4) {
                e = e4;
                openInputStream = null;
                Log.e("Error reading image: " + e.getMessage(), e);
                setResultException(e);
                CropUtil.closeSilently(openInputStream);
                if (this.rotateBitmap == null) {
                    finish();
                } else if (!isFinishing()) {
                    this.imageView.setImageRotateBitmapResetBase(this.rotateBitmap, true);
                    CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait), /* anonymous class already generated */, this.handler);
                }
            } catch (OutOfMemoryError e5) {
                e = e5;
                openInputStream = null;
                Log.e("OOM reading image: " + e.getMessage(), e);
                setResultException(e);
                CropUtil.closeSilently(openInputStream);
                if (this.rotateBitmap == null) {
                    finish();
                } else if (!isFinishing()) {
                    this.imageView.setImageRotateBitmapResetBase(this.rotateBitmap, true);
                    CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait), /* anonymous class already generated */, this.handler);
                }
            } catch (Throwable th2) {
                e = th2;
                openInputStream = null;
                CropUtil.closeSilently(openInputStream);
                throw e;
            }
        }
        if (this.rotateBitmap == null) {
            finish();
        } else if (!isFinishing()) {
            this.imageView.setImageRotateBitmapResetBase(this.rotateBitmap, true);
            CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait), /* anonymous class already generated */, this.handler);
        }
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        try {
            int maxSize;
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options);
            int[] iArr = new int[1];
            GLES10.glGetIntegerv(3379, iArr, 0);
            int i = iArr[0];
            if (i == 0) {
                maxSize = ItemAnimator.FLAG_MOVED;
            } else {
                maxSize = Math.min(i, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
            }
            int sampleSize = 1;
            while (true) {
                if (options.outHeight / sampleSize <= maxSize && options.outWidth / sampleSize <= maxSize) {
                    return sampleSize;
                }
                sampleSize <<= 1;
            }
        } finally {
            CropUtil.closeSilently(is);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.rotateBitmap != null) {
            this.rotateBitmap.recycle();
        }
    }

    public boolean onSearchRequested() {
        return false;
    }

    public final boolean isSaving() {
        return this.isSaving;
    }

    private void setResultException(Throwable throwable) {
        setResult(404, new Intent().putExtra("error", throwable));
    }

    private Bitmap decodeRegionCrop(Rect rect, int outWidth, int outHeight) {
        this.imageView.clear();
        if (this.rotateBitmap != null) {
            this.rotateBitmap.recycle();
        }
        System.gc();
        InputStream is = null;
        Bitmap croppedImage = null;
        int width;
        int height;
        try {
            Matrix matrix;
            is = getContentResolver().openInputStream(this.sourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            width = decoder.getWidth();
            height = decoder.getHeight();
            if (this.exifRotation != 0) {
                float f;
                float f2;
                matrix = new Matrix();
                matrix.setRotate((float) (-this.exifRotation));
                RectF adjusted = new RectF();
                matrix.mapRect(adjusted, new RectF(rect));
                if (adjusted.left < 0.0f) {
                    f = (float) width;
                } else {
                    f = 0.0f;
                }
                if (adjusted.top < 0.0f) {
                    f2 = (float) height;
                } else {
                    f2 = 0.0f;
                }
                adjusted.offset(f, f2);
                rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
            }
            croppedImage = decoder.decodeRegion(rect, new Options());
            if (rect.width() > outWidth || rect.height() > outHeight) {
                matrix = new Matrix();
                matrix.postScale(((float) outWidth) / ((float) rect.width()), ((float) outHeight) / ((float) rect.height()));
                croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
            }
            CropUtil.closeSilently(is);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image (" + width + "," + height + "," + this.exifRotation + ")", e);
        } catch (IOException e2) {
            try {
                Log.e("Error cropping image: " + e2.getMessage(), e2);
                setResultException(e2);
            } finally {
                CropUtil.closeSilently(is);
            }
        } catch (OutOfMemoryError e3) {
            Log.e("OOM cropping image: " + e3.getMessage(), e3);
            setResultException(e3);
            CropUtil.closeSilently(is);
        }
        return croppedImage;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void access$900(com.soundcloud.android.crop.CropImageActivity r4, final android.graphics.Bitmap r5) {
        /*
        r0 = r4.saveUri;
        if (r0 == 0) goto L_0x0043;
    L_0x0004:
        r1 = 0;
        r0 = r4.getContentResolver();	 Catch:{ IOException -> 0x0051 }
        r2 = r4.saveUri;	 Catch:{ IOException -> 0x0051 }
        r1 = r0.openOutputStream(r2);	 Catch:{ IOException -> 0x0051 }
        if (r1 == 0) goto L_0x0018;
    L_0x0011:
        r0 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ IOException -> 0x0051 }
        r2 = 90;
        r5.compress(r0, r2, r1);	 Catch:{ IOException -> 0x0051 }
    L_0x0018:
        com.soundcloud.android.crop.CropUtil.closeSilently(r1);
    L_0x001b:
        r0 = r4.getContentResolver();
        r1 = r4.sourceUri;
        r0 = com.soundcloud.android.crop.CropUtil.getFromMediaUri(r4, r0, r1);
        r1 = r4.getContentResolver();
        r2 = r4.saveUri;
        r1 = com.soundcloud.android.crop.CropUtil.getFromMediaUri(r4, r1, r2);
        com.soundcloud.android.crop.CropUtil.copyExifRotation(r0, r1);
        r0 = r4.saveUri;
        r1 = -1;
        r2 = new android.content.Intent;
        r2.<init>();
        r3 = "output";
        r0 = r2.putExtra(r3, r0);
        r4.setResult(r1, r0);
    L_0x0043:
        r0 = r4.handler;
        r1 = new com.soundcloud.android.crop.CropImageActivity$6;
        r1.<init>(r5);
        r0.post(r1);
        r4.finish();
        return;
    L_0x0051:
        r0 = move-exception;
        r4.setResultException(r0);	 Catch:{ all -> 0x006d }
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x006d }
        r3 = "Cannot open file: ";
        r2.<init>(r3);	 Catch:{ all -> 0x006d }
        r3 = r4.saveUri;	 Catch:{ all -> 0x006d }
        r2 = r2.append(r3);	 Catch:{ all -> 0x006d }
        r2 = r2.toString();	 Catch:{ all -> 0x006d }
        com.soundcloud.android.crop.Log.e(r2, r0);	 Catch:{ all -> 0x006d }
        com.soundcloud.android.crop.CropUtil.closeSilently(r1);
        goto L_0x001b;
    L_0x006d:
        r0 = move-exception;
        com.soundcloud.android.crop.CropUtil.closeSilently(r1);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.soundcloud.android.crop.CropImageActivity.access$900(com.soundcloud.android.crop.CropImageActivity, android.graphics.Bitmap):void");
    }
}
