package org.springframework.http.converter;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

public final class ByteArrayHttpMessageConverter extends AbstractHttpMessageConverter<byte[]> {
    protected final /* bridge */ /* synthetic */ Long getContentLength(Object x0, MediaType x1) throws IOException {
        return Long.valueOf((long) ((byte[]) x0).length);
    }

    protected final /* bridge */ /* synthetic */ void writeInternal(Object x0, HttpOutputMessage x1) throws IOException, HttpMessageNotWritableException {
        StreamUtils.copy((byte[]) x0, x1.getBody());
    }

    public ByteArrayHttpMessageConverter() {
        super(new MediaType("application", "octet-stream"), MediaType.ALL);
    }

    public final boolean supports(Class<?> clazz) {
        return byte[].class.equals(clazz);
    }

    public final /* bridge */ /* synthetic */ Object readInternal(Class x0, HttpInputMessage x1) throws IOException, HttpMessageNotReadableException {
        long contentLength = x1.getHeaders().getContentLength();
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream(contentLength >= 0 ? (int) contentLength : ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
        StreamUtils.copy(x1.getBody(), byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
