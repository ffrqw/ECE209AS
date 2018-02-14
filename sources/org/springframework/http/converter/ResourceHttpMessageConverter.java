package org.springframework.http.converter;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

public final class ResourceHttpMessageConverter extends AbstractHttpMessageConverter<Resource> {
    protected final /* bridge */ /* synthetic */ Long getContentLength(Object x0, MediaType x1) throws IOException {
        Resource resource = (Resource) x0;
        return InputStreamResource.class.equals(resource.getClass()) ? null : Long.valueOf(resource.contentLength());
    }

    protected final /* bridge */ /* synthetic */ void writeInternal(Object x0, HttpOutputMessage x1) throws IOException, HttpMessageNotWritableException {
        InputStream inputStream = ((Resource) x0).getInputStream();
        try {
            StreamUtils.copy(inputStream, x1.getBody());
            x1.getBody().flush();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public ResourceHttpMessageConverter() {
        super(MediaType.ALL);
    }

    protected final boolean supports(Class<?> clazz) {
        return Resource.class.isAssignableFrom(clazz);
    }

    protected final /* bridge */ /* synthetic */ Object readInternal(Class x0, HttpInputMessage x1) throws IOException, HttpMessageNotReadableException {
        return new ByteArrayResource(StreamUtils.copyToByteArray(x1.getBody()));
    }

    protected final /* bridge */ /* synthetic */ MediaType getDefaultContentType(Object x0) throws IOException {
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
