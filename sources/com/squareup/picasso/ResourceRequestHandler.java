package com.squareup.picasso;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestHandler.Result;
import java.io.IOException;

final class ResourceRequestHandler extends RequestHandler {
    private final Context context;

    ResourceRequestHandler(Context context) {
        this.context = context;
    }

    public final boolean canHandleRequest(Request data) {
        if (data.resourceId != 0) {
            return true;
        }
        return "android.resource".equals(data.uri.getScheme());
    }

    public final Result load(Request request, int networkPolicy) throws IOException {
        Resources res = Utils.getResources(this.context, request);
        int id = Utils.getResourceId(res, request);
        Options createBitmapOptions = RequestHandler.createBitmapOptions(request);
        if (RequestHandler.requiresInSampleSize(createBitmapOptions)) {
            BitmapFactory.decodeResource(res, id, createBitmapOptions);
            RequestHandler.calculateInSampleSize(request.targetWidth, request.targetHeight, createBitmapOptions, request);
        }
        return new Result(BitmapFactory.decodeResource(res, id, createBitmapOptions), LoadedFrom.DISK);
    }
}
