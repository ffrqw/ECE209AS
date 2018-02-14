package com.soundcloud.android.crop;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.rachio.iro.R;

public final class Crop {
    private Intent cropIntent = new Intent();

    public static Crop of(Uri source, Uri destination) {
        return new Crop(source, destination);
    }

    private Crop(Uri source, Uri destination) {
        this.cropIntent.setData(source);
        this.cropIntent.putExtra("output", destination);
    }

    public final Crop withAspect(int x, int y) {
        this.cropIntent.putExtra("aspect_x", 2);
        this.cropIntent.putExtra("aspect_y", 1);
        return this;
    }

    public final Intent getIntent(Context context) {
        this.cropIntent.setClass(context, CropImageActivity.class);
        return this.cropIntent;
    }

    public static void pickImage(Activity activity, int requestCode) {
        try {
            activity.startActivityForResult(new Intent("android.intent.action.GET_CONTENT").setType("image/*"), 9162);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.crop__pick_error, 0).show();
        }
    }
}
