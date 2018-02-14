package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

public class MappingJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object> implements GenericHttpMessageConverter<Object> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private ObjectMapper objectMapper = new ObjectMapper();

    public MappingJackson2HttpMessageConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET), new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    public final void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
    }

    public final boolean canRead(Class<?> clazz, MediaType mediaType) {
        return canRead(clazz, null, mediaType);
    }

    public final boolean canRead(Type type, Class<?> cls, MediaType mediaType) {
        return this.objectMapper.canDeserialize(getJavaType(type, null)) && canRead(mediaType);
    }

    public final boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return this.objectMapper.canSerialize(clazz) && canWrite(mediaType);
    }

    protected final boolean supports(Class<?> cls) {
        throw new UnsupportedOperationException();
    }

    protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readJavaType(getJavaType(clazz, null), inputMessage);
    }

    public final Object read(Type type, Class<?> cls, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readJavaType(getJavaType(type, null), inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (!(contentType == null || contentType.getCharSet() == null)) {
            Charset charSet = contentType.getCharSet();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charSet.name().equals(encoding.getJavaName())) {
                    break;
                }
            }
        }
        JsonEncoding encoding2 = JsonEncoding.UTF8;
        JsonGenerator jsonGenerator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding2);
        if (this.objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }
        try {
            this.objectMapper.writeValue(jsonGenerator, object);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    private JavaType getJavaType(Type type, Class<?> contextClass) {
        return contextClass != null ? this.objectMapper.getTypeFactory().constructType(type, (Class) contextClass) : this.objectMapper.constructType(type);
    }
}
