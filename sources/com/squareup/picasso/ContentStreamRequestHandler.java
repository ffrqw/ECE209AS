package com.squareup.picasso;

import android.content.Context;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestHandler.Result;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import okio.Okio;

class ContentStreamRequestHandler extends RequestHandler {
    final Context context;

    ContentStreamRequestHandler(Context context) {
        this.context = context;
    }

    public boolean canHandleRequest(Request data) {
        return "content".equals(data.uri.getScheme());
    }

    public Result load(Request request, int networkPolicy) throws IOException {
        return new Result(Okio.source(getInputStream(request)), LoadedFrom.DISK);
    }

    final InputStream getInputStream(Request request) throws FileNotFoundException {
        return this.context.getContentResolver().openInputStream(request.uri);
    }
}
