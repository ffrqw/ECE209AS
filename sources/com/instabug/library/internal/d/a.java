package com.instabug.library.internal.d;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import com.instabug.library.util.InstabugSDKLogger;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class a {
    private Context a;

    public interface a {
        void a(String str);

        void a$300829f2(Uri uri);
    }

    public a(Context context) {
        this.a = context;
    }

    public static File a(Context context) {
        String absolutePath;
        File file;
        if (context.getExternalFilesDir(null) != null && Environment.getExternalStorageState().equals("mounted")) {
            try {
                absolutePath = context.getExternalFilesDir(null).getAbsolutePath();
            } catch (NullPointerException e) {
            }
            file = new File(absolutePath + "/instabug/");
            if (!file.exists()) {
                file.mkdirs();
                try {
                    new File(file, ".nomedia").createNewFile();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return file;
        }
        absolutePath = b(context);
        file = new File(absolutePath + "/instabug/");
        if (file.exists()) {
            file.mkdirs();
            new File(file, ".nomedia").createNewFile();
        }
        return file;
    }

    private static String b(Context context) {
        InstabugSDKLogger.i(a.class, "External storage not available, saving file to internal storage.");
        return context.getFilesDir().getAbsolutePath();
    }

    private static void a(Context context, Uri uri, File file) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
        InstabugSDKLogger.d(a.class, "Target file path: " + file.getPath());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(context.getContentResolver().openOutputStream(Uri.fromFile(file)));
        byte[] bArr = new byte[32768];
        while (true) {
            int read = bufferedInputStream.read(bArr);
            if (read > 0) {
                bufferedOutputStream.write(bArr, 0, read);
            } else {
                bufferedOutputStream.close();
                bufferedInputStream.close();
                return;
            }
        }
    }

    public static String a(Activity activity, Uri uri) {
        Cursor managedQuery = activity.managedQuery(uri, new String[]{"_data"}, null, null, null);
        int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
        managedQuery.moveToFirst();
        return managedQuery.getString(columnIndexOrThrow);
    }

    public static Uri a(Context context, Uri uri, String str) {
        if (uri == null) {
            return null;
        }
        Object obj = null;
        String toLowerCase = uri.getLastPathSegment().toLowerCase();
        File a = a(context);
        if (str == null) {
            str = toLowerCase;
        } else {
            obj = 1;
        }
        File file = new File(a, str);
        if (file.exists()) {
            file = new File(a, String.valueOf(System.currentTimeMillis()) + "_" + str);
        }
        try {
            a(context, uri, file);
            if (obj != null) {
                long length = file.length();
                double d = ((double) length) / 1048576.0d;
                InstabugSDKLogger.d(a.class, "External attachment file size is " + length + " bytes or " + d + " MBs");
                if (d > 1.0d) {
                    InstabugSDKLogger.i(a.class, "Attachment exceeds 1.0 MBs file size limit, ignoring attachment");
                    return null;
                }
            }
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final void a(Bitmap bitmap, a aVar) {
        File file = new File(a(this.a), "bug_" + System.currentTimeMillis() + "_.jpg");
        try {
            OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            InstabugSDKLogger.d(this, "Image Path: " + file.toString());
            bitmap.compress(CompressFormat.JPEG, 100, bufferedOutputStream);
            bufferedOutputStream.close();
            Uri fromFile = Uri.fromFile(file);
            if (fromFile != null) {
                aVar.a$300829f2(fromFile);
            } else {
                aVar.a("Could not store screenshot to file. Please make sure you requested the proper permissions");
            }
        } catch (Throwable e) {
            InstabugSDKLogger.e(this, "File not found", e);
        } catch (Throwable e2) {
            InstabugSDKLogger.e(this, "Error accessing file", e2);
        }
    }
}
