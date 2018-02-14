package org.springframework.http.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

public abstract class AbstractHttpMessageConverter<T> implements HttpMessageConverter<T> {
    private List<MediaType> supportedMediaTypes = Collections.emptyList();

    protected abstract T readInternal(Class<? extends T> cls, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException;

    protected abstract boolean supports(Class<?> cls);

    protected abstract void writeInternal(T t, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException;

    protected AbstractHttpMessageConverter(MediaType supportedMediaType) {
        setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
    }

    protected AbstractHttpMessageConverter(MediaType... supportedMediaTypes) {
        setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    private void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        Assert.notEmpty(supportedMediaTypes, "'supportedMediaTypes' must not be empty");
        this.supportedMediaTypes = new ArrayList(supportedMediaTypes);
    }

    public final List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return supports(clazz) && canRead(mediaType);
    }

    protected final boolean canRead(MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : Collections.unmodifiableList(this.supportedMediaTypes)) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return supports(clazz) && canWrite(mediaType);
    }

    protected final boolean canWrite(MediaType mediaType) {
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : Collections.unmodifiableList(this.supportedMediaTypes)) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    public final T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
        return readInternal(clazz, inputMessage);
    }

    public final void write(T t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        if (headers.getContentType() == null) {
            MediaType contentTypeToUse = contentType;
            if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype()) {
                contentTypeToUse = getDefaultContentType(t);
            }
            if (contentTypeToUse != null) {
                headers.setContentType(contentTypeToUse);
            }
        }
        if (headers.getContentLength() == -1) {
            Long contentLength = getContentLength(t, headers.getContentType());
            if (contentLength != null) {
                headers.setContentLength(contentLength.longValue());
            }
        }
        writeInternal(t, outputMessage);
        outputMessage.getBody().flush();
    }

    protected Long getContentLength(T t, MediaType contentType) throws IOException {
        return null;
    }

    protected MediaType getDefaultContentType(T t) throws IOException {
        List<MediaType> mediaTypes = Collections.unmodifiableList(this.supportedMediaTypes);
        return !mediaTypes.isEmpty() ? (MediaType) mediaTypes.get(0) : null;
    }
}
