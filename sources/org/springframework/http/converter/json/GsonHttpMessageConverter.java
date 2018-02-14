package org.springframework.http.converter.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public final class GsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Gson gson = new Gson();

    public GsonHttpMessageConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET), new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    public final boolean canRead(Class<?> cls, MediaType mediaType) {
        return canRead(mediaType);
    }

    public final boolean canRead(Type type, Class<?> cls, MediaType mediaType) {
        return canRead(mediaType);
    }

    public final boolean canWrite(Class<?> cls, MediaType mediaType) {
        return canWrite(mediaType);
    }

    protected final boolean supports(Class<?> cls) {
        throw new UnsupportedOperationException();
    }

    private Object readTypeToken(TypeToken<?> token, HttpInputMessage inputMessage) throws IOException {
        try {
            return this.gson.fromJson(new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders())), token.getType());
        } catch (JsonParseException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    private static Charset getCharset(HttpHeaders headers) {
        if (headers == null || headers.getContentType() == null || headers.getContentType().getCharSet() == null) {
            return DEFAULT_CHARSET;
        }
        return headers.getContentType().getCharSet();
    }

    protected final void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStreamWriter writer = new OutputStreamWriter(outputMessage.getBody(), getCharset(outputMessage.getHeaders()));
        try {
            Gson gson = this.gson;
            if (o != null) {
                gson.toJson(o, o.getClass(), writer);
            } else {
                gson.toJson(JsonNull.INSTANCE, writer);
            }
            writer.close();
        } catch (JsonIOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readTypeToken(TypeToken.get((Type) clazz), inputMessage);
    }

    public final Object read(Type type, Class<?> cls, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readTypeToken(TypeToken.get(type), inputMessage);
    }
}
