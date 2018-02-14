package com.squareup.picasso;

import android.content.Context;
import android.media.ExifInterface;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestHandler.Result;
import java.io.IOException;
import okio.Okio;

final class FileRequestHandler extends ContentStreamRequestHandler {
    FileRequestHandler(Context context) {
        super(context);
    }

    public final boolean canHandleRequest(Request data) {
        return "file".equals(data.uri.getScheme());
    }

    public final Result load(Request request, int networkPolicy) throws IOException {
        return new Result(null, Okio.source(getInputStream(request)), LoadedFrom.DISK, new ExifInterface(request.uri.getPath()).getAttributeInt("Orientation", 1));
    }
}
