package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

public class FormHttpMessageConverter implements HttpMessageConverter<MultiValueMap<String, ?>> {
    private static final byte[] BOUNDARY_CHARS = new byte[]{(byte) 45, (byte) 95, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 48, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90};
    private Charset charset = Charset.forName("UTF-8");
    private List<HttpMessageConverter<?>> partConverters = new ArrayList();
    private final Random rnd = new Random();
    private List<MediaType> supportedMediaTypes = new ArrayList();

    private class MultipartHttpOutputMessage implements HttpOutputMessage {
        private final HttpHeaders headers = new HttpHeaders();
        private boolean headersWritten = false;
        private final OutputStream os;

        public MultipartHttpOutputMessage(OutputStream os) {
            this.os = os;
        }

        public final HttpHeaders getHeaders() {
            return this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
        }

        private static byte[] getAsciiBytes(String name) {
            try {
                return name.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }

        public final OutputStream getBody() throws IOException {
            if (!this.headersWritten) {
                for (Entry entry : this.headers.entrySet()) {
                    byte[] asciiBytes = getAsciiBytes((String) entry.getKey());
                    for (String asciiBytes2 : (List) entry.getValue()) {
                        byte[] asciiBytes3 = getAsciiBytes(asciiBytes2);
                        this.os.write(asciiBytes);
                        this.os.write(58);
                        this.os.write(32);
                        this.os.write(asciiBytes3);
                        FormHttpMessageConverter.writeNewLine(this.os);
                    }
                }
                FormHttpMessageConverter.writeNewLine(this.os);
                this.headersWritten = true;
            }
            return this.os;
        }
    }

    public final /* bridge */ /* synthetic */ Object read(Class x0, HttpInputMessage x1) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = x1.getHeaders().getContentType();
        Charset charSet = contentType.getCharSet() != null ? contentType.getCharSet() : this.charset;
        String[] tokenizeToStringArray = StringUtils.tokenizeToStringArray(StreamUtils.copyToString(x1.getBody(), charSet), "&");
        MultiValueMap linkedMultiValueMap = new LinkedMultiValueMap(tokenizeToStringArray.length);
        for (String str : tokenizeToStringArray) {
            int indexOf = str.indexOf(61);
            if (indexOf == -1) {
                linkedMultiValueMap.add(URLDecoder.decode(str, charSet.name()), null);
            } else {
                linkedMultiValueMap.add(URLDecoder.decode(str.substring(0, indexOf), charSet.name()), URLDecoder.decode(str.substring(indexOf + 1), charSet.name()));
            }
        }
        return linkedMultiValueMap;
    }

    public final /* bridge */ /* synthetic */ void write(Object x0, MediaType x1, HttpOutputMessage x2) throws IOException, HttpMessageNotWritableException {
        boolean includes;
        MultiValueMap multiValueMap = (MultiValueMap) x0;
        if (x1 != null) {
            includes = MediaType.MULTIPART_FORM_DATA.includes(x1);
        } else {
            loop2:
            for (String str : multiValueMap.keySet()) {
                for (Object next : (List) multiValueMap.get(str)) {
                    if (next != null && !(next instanceof String)) {
                        includes = true;
                        break loop2;
                    }
                }
            }
            includes = false;
        }
        if (includes) {
            byte[] generateMultipartBoundary = generateMultipartBoundary();
            x2.getHeaders().setContentType(new MediaType(MediaType.MULTIPART_FORM_DATA, Collections.singletonMap("boundary", new String(generateMultipartBoundary, "US-ASCII"))));
            writeParts(x2.getBody(), multiValueMap, generateMultipartBoundary);
            OutputStream body = x2.getBody();
            body.write(45);
            body.write(45);
            body.write(generateMultipartBoundary);
            body.write(45);
            body.write(45);
            writeNewLine(body);
            return;
        }
        Charset charSet;
        if (x1 != null) {
            x2.getHeaders().setContentType(x1);
            charSet = x1.getCharSet() != null ? x1.getCharSet() : this.charset;
        } else {
            x2.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            charSet = this.charset;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Iterator it = multiValueMap.keySet().iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            Iterator it2 = ((List) multiValueMap.get(str2)).iterator();
            while (it2.hasNext()) {
                String str3 = (String) it2.next();
                stringBuilder.append(URLEncoder.encode(str2, charSet.name()));
                if (str3 != null) {
                    stringBuilder.append('=');
                    stringBuilder.append(URLEncoder.encode(str3, charSet.name()));
                    if (it2.hasNext()) {
                        stringBuilder.append('&');
                    }
                }
            }
            if (it.hasNext()) {
                stringBuilder.append('&');
            }
        }
        generateMultipartBoundary = stringBuilder.toString().getBytes(charSet.name());
        x2.getHeaders().setContentLength((long) generateMultipartBoundary.length);
        StreamUtils.copy(generateMultipartBoundary, x2.getBody());
    }

    public FormHttpMessageConverter() {
        this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        this.partConverters.add(new ByteArrayHttpMessageConverter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        this.partConverters.add(stringHttpMessageConverter);
        this.partConverters.add(new ResourceHttpMessageConverter());
    }

    public final void addPartConverter(HttpMessageConverter<?> partConverter) {
        Assert.notNull(partConverter, "'partConverter' must not be NULL");
        this.partConverters.add(partConverter);
    }

    public final boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : Collections.unmodifiableList(this.supportedMediaTypes)) {
            if (!supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) && supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    public final boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!MultiValueMap.class.isAssignableFrom(clazz)) {
            return false;
        }
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

    public final List<MediaType> getSupportedMediaTypes() {
        return Collections.unmodifiableList(this.supportedMediaTypes);
    }

    private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary) throws IOException {
        for (Entry<String, List<Object>> entry : parts.entrySet()) {
            String name = (String) entry.getKey();
            for (Object part : (List) entry.getValue()) {
                if (part != null) {
                    HttpEntity entity;
                    os.write(45);
                    os.write(45);
                    os.write(boundary);
                    writeNewLine(os);
                    if (part instanceof HttpEntity) {
                        entity = (HttpEntity) part;
                    } else {
                        entity = new HttpEntity(part);
                    }
                    Object body = entity.getBody();
                    Class cls = body.getClass();
                    Map headers = entity.getHeaders();
                    MediaType contentType = headers.getContentType();
                    for (HttpMessageConverter httpMessageConverter : this.partConverters) {
                        if (httpMessageConverter.canWrite(cls, contentType)) {
                            String filename;
                            HttpOutputMessage multipartHttpOutputMessage = new MultipartHttpOutputMessage(os);
                            HttpHeaders headers2 = multipartHttpOutputMessage.getHeaders();
                            if (body instanceof Resource) {
                                filename = ((Resource) body).getFilename();
                            } else {
                                filename = null;
                            }
                            headers2.setContentDispositionFormData(name, filename);
                            if (!headers.isEmpty()) {
                                multipartHttpOutputMessage.getHeaders().putAll(headers);
                            }
                            httpMessageConverter.write(body, contentType, multipartHttpOutputMessage);
                            writeNewLine(os);
                        }
                    }
                    throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter found for request type [" + cls.getName() + "]");
                }
            }
        }
    }

    private static void writeNewLine(OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }

    private byte[] generateMultipartBoundary() {
        byte[] boundary = new byte[(this.rnd.nextInt(11) + 30)];
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_CHARS[this.rnd.nextInt(64)];
        }
        return boundary;
    }
}
