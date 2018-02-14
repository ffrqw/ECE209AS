package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

public final class StringHttpMessageConverter extends AbstractHttpMessageConverter<String> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
    private final List<Charset> availableCharsets;
    private final Charset defaultCharset;
    private boolean writeAcceptCharset;

    protected final /* bridge */ /* synthetic */ void writeInternal(Object x0, HttpOutputMessage x1) throws IOException, HttpMessageNotWritableException {
        String str = (String) x0;
        if (this.writeAcceptCharset) {
            x1.getHeaders().setAcceptCharset(this.availableCharsets);
        }
        Charset contentTypeCharset = getContentTypeCharset(x1.getHeaders().getContentType());
        OutputStream body = x1.getBody();
        Assert.notNull(str, "No input String specified");
        Assert.notNull(contentTypeCharset, "No charset specified");
        Assert.notNull(body, "No OutputStream specified");
        Writer outputStreamWriter = new OutputStreamWriter(body, contentTypeCharset);
        outputStreamWriter.write(str);
        outputStreamWriter.flush();
    }

    public StringHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    private StringHttpMessageConverter(Charset defaultCharset) {
        super(new MediaType("text", "plain", defaultCharset), MediaType.ALL);
        this.writeAcceptCharset = true;
        this.defaultCharset = defaultCharset;
        this.availableCharsets = new ArrayList(Charset.availableCharsets().values());
    }

    public final void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.writeAcceptCharset = false;
    }

    public final boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    private Long getContentLength(String s, MediaType contentType) {
        try {
            return Long.valueOf((long) s.getBytes(getContentTypeCharset(contentType).name()).length);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType == null || contentType.getCharSet() == null) {
            return this.defaultCharset;
        }
        return contentType.getCharSet();
    }

    protected final /* bridge */ /* synthetic */ Object readInternal(Class x0, HttpInputMessage x1) throws IOException, HttpMessageNotReadableException {
        return StreamUtils.copyToString(x1.getBody(), getContentTypeCharset(x1.getHeaders().getContentType()));
    }
}
