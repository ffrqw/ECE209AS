package com.soundcloud.android.crop;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import com.soundcloud.android.crop.MonitoredActivity.LifeCycleAdapter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

final class CropUtil {

    private static class BackgroundJob extends LifeCycleAdapter implements Runnable {
        private final MonitoredActivity activity;
        private final Runnable cleanupRunner = new Runnable() {
            public final void run() {
                BackgroundJob.this.activity.removeLifeCycleListener(BackgroundJob.this);
                if (BackgroundJob.this.dialog.getWindow() != null) {
                    BackgroundJob.this.dialog.dismiss();
                }
            }
        };
        private final ProgressDialog dialog;
        private final Handler handler;
        private final Runnable job;

        public BackgroundJob(MonitoredActivity activity, Runnable job, ProgressDialog dialog, Handler handler) {
            this.activity = activity;
            this.dialog = dialog;
            this.job = job;
            this.activity.addLifeCycleListener(this);
            this.handler = handler;
        }

        public final void run() {
            try {
                this.job.run();
            } finally {
                this.handler.post(this.cleanupRunner);
            }
        }

        public final void onActivityDestroyed$18452068() {
            this.cleanupRunner.run();
            this.handler.removeCallbacks(this.cleanupRunner);
        }

        public final void onActivityStopped$18452068() {
            this.dialog.hide();
        }

        public final void onActivityStarted$18452068() {
            this.dialog.show();
        }
    }

    public static java.io.File getFromMediaUri(android.content.Context r10, android.content.ContentResolver r11, android.net.Uri r12) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0004 in list [B:35:0x0085]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:43)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = 0;
        if (r12 != 0) goto L_0x0005;
    L_0x0003:
        r0 = r9;
    L_0x0004:
        return r0;
    L_0x0005:
        r0 = "file";
        r1 = r12.getScheme();
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x001b;
    L_0x0011:
        r0 = new java.io.File;
        r1 = r12.getPath();
        r0.<init>(r1);
        goto L_0x0004;
    L_0x001b:
        r0 = "content";
        r1 = r12.getScheme();
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x007c;
    L_0x0027:
        r0 = 2;
        r2 = new java.lang.String[r0];
        r0 = 0;
        r1 = "_data";
        r2[r0] = r1;
        r0 = 1;
        r1 = "_display_name";
        r2[r0] = r1;
        r7 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r11;
        r1 = r12;
        r7 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        if (r7 == 0) goto L_0x0077;	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
    L_0x0040:
        r0 = r7.moveToFirst();	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        if (r0 == 0) goto L_0x0077;	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
    L_0x0046:
        r0 = r12.toString();	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        r1 = "content://com.google.android.gallery3d";	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        r0 = r0.startsWith(r1);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        if (r0 == 0) goto L_0x0070;	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
    L_0x0052:
        r0 = "_display_name";	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        r6 = r7.getColumnIndex(r0);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
    L_0x0058:
        r0 = -1;	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        if (r6 == r0) goto L_0x0077;	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
    L_0x005b:
        r8 = r7.getString(r6);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        r0 = android.text.TextUtils.isEmpty(r8);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        if (r0 != 0) goto L_0x0077;	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
    L_0x0065:
        r0 = new java.io.File;	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        r0.<init>(r8);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        if (r7 == 0) goto L_0x0004;
    L_0x006c:
        r7.close();
        goto L_0x0004;
    L_0x0070:
        r0 = "_data";	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        r6 = r7.getColumnIndex(r0);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        goto L_0x0058;
    L_0x0077:
        if (r7 == 0) goto L_0x007c;
    L_0x0079:
        r7.close();
    L_0x007c:
        r0 = r9;
        goto L_0x0004;
    L_0x007e:
        r0 = move-exception;
        r0 = getFromMediaUriPfd(r10, r11, r12);	 Catch:{ IllegalArgumentException -> 0x007e, SecurityException -> 0x008a, all -> 0x0091 }
        if (r7 == 0) goto L_0x0004;
    L_0x0085:
        r7.close();
        goto L_0x0004;
    L_0x008a:
        r0 = move-exception;
        if (r7 == 0) goto L_0x007c;
    L_0x008d:
        r7.close();
        goto L_0x007c;
    L_0x0091:
        r0 = move-exception;
        if (r7 == 0) goto L_0x0097;
    L_0x0094:
        r7.close();
    L_0x0097:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.soundcloud.android.crop.CropUtil.getFromMediaUri(android.content.Context, android.content.ContentResolver, android.net.Uri):java.io.File");
    }

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable th) {
            }
        }
    }

    public static int getExifRotation(File imageFile) {
        if (imageFile == null) {
            return 0;
        }
        try {
            switch (new ExifInterface(imageFile.getAbsolutePath()).getAttributeInt("Orientation", 0)) {
                case 3:
                    return 180;
                case 6:
                    return 90;
                case 8:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            Log.e("Error getting Exif data", e);
            return 0;
        }
    }

    public static boolean copyExifRotation(File sourceFile, File destFile) {
        if (sourceFile == null || destFile == null) {
            return false;
        }
        try {
            ExifInterface exifSource = new ExifInterface(sourceFile.getAbsolutePath());
            ExifInterface exifDest = new ExifInterface(destFile.getAbsolutePath());
            exifDest.setAttribute("Orientation", exifSource.getAttribute("Orientation"));
            exifDest.saveAttributes();
            return true;
        } catch (IOException e) {
            Log.e("Error copying Exif data", e);
            return false;
        }
    }

    private static File getFromMediaUriPfd(Context context, ContentResolver resolver, Uri uri) {
        FileOutputStream output;
        Throwable th;
        if (uri == null) {
            return null;
        }
        FileInputStream input = null;
        FileOutputStream output2 = null;
        try {
            String tempFilename;
            FileInputStream input2 = new FileInputStream(resolver.openFileDescriptor(uri, "r").getFileDescriptor());
            try {
                tempFilename = File.createTempFile("image", "tmp", context.getCacheDir()).getAbsolutePath();
                output = new FileOutputStream(tempFilename);
            } catch (IOException e) {
                input = input2;
                closeSilently(input);
                closeSilently(output2);
                return null;
            } catch (Throwable th2) {
                th = th2;
                input = input2;
                closeSilently(input);
                closeSilently(output2);
                throw th;
            }
            try {
                byte[] bytes = new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT];
                while (true) {
                    int read = input2.read(bytes);
                    if (read != -1) {
                        output.write(bytes, 0, read);
                    } else {
                        File file = new File(tempFilename);
                        closeSilently(input2);
                        closeSilently(output);
                        return file;
                    }
                }
            } catch (IOException e2) {
                output2 = output;
                input = input2;
                closeSilently(input);
                closeSilently(output2);
                return null;
            } catch (Throwable th3) {
                th = th3;
                output2 = output;
                input = input2;
                closeSilently(input);
                closeSilently(output2);
                throw th;
            }
        } catch (IOException e3) {
            closeSilently(input);
            closeSilently(output2);
            return null;
        } catch (Throwable th4) {
            th = th4;
            closeSilently(input);
            closeSilently(output2);
            throw th;
        }
    }

    public static void startBackgroundJob(MonitoredActivity activity, String title, String message, Runnable job, Handler handler) {
        new Thread(new BackgroundJob(activity, job, ProgressDialog.show(activity, null, message, true, false), handler)).start();
    }
}
