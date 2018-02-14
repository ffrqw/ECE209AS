package com.instabug.library.util;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import java.io.FileNotFoundException;

public final class b {
    public static Bitmap a(ContentResolver contentResolver, Uri uri) throws FileNotFoundException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options);
        int i = options.outWidth;
        int i2 = ((((float) i) / ((float) options.outHeight)) > 1.0f ? 1 : ((((float) i) / ((float) options.outHeight)) == 1.0f ? 0 : -1));
        i2 = i / 900;
        Options options2 = new Options();
        options2.inSampleSize = i2;
        Bitmap decodeStream = BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options2);
        decodeStream.compress(CompressFormat.JPEG, 100, contentResolver.openOutputStream(uri));
        return decodeStream;
    }
}
