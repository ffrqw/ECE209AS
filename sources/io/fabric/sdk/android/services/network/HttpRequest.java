package io.fabric.sdk.android.services.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public final class HttpRequest {
    private static ConnectionFactory CONNECTION_FACTORY = ConnectionFactory.DEFAULT;
    private static final String[] EMPTY_STRINGS = new String[0];
    private int bufferSize = 8192;
    private HttpURLConnection connection = null;
    private boolean ignoreCloseExceptions = true;
    private boolean multipart;
    private RequestOutputStream output;
    private final String requestMethod;
    private boolean uncompress = false;
    public final URL url;

    protected static abstract class Operation<V> implements Callable<V> {
        protected abstract void done() throws IOException;

        protected abstract V run() throws HttpRequestException, IOException;

        protected Operation() {
        }

        public V call() throws HttpRequestException {
            boolean thrown;
            try {
                V run = run();
                try {
                    done();
                    return run;
                } catch (IOException e) {
                    throw new HttpRequestException(e);
                }
            } catch (HttpRequestException e2) {
                thrown = true;
                throw e2;
            } catch (IOException e3) {
                thrown = true;
                throw new HttpRequestException(e3);
            } catch (Throwable th) {
                try {
                    done();
                } catch (IOException e32) {
                    if (!thrown) {
                        throw new HttpRequestException(e32);
                    }
                }
            }
        }
    }

    protected static abstract class CloseOperation<V> extends Operation<V> {
        private final Closeable closeable;
        private final boolean ignoreCloseExceptions;

        protected CloseOperation(Closeable closeable, boolean ignoreCloseExceptions) {
            this.closeable = closeable;
            this.ignoreCloseExceptions = ignoreCloseExceptions;
        }

        protected final void done() throws IOException {
            if (this.closeable instanceof Flushable) {
                ((Flushable) this.closeable).flush();
            }
            if (this.ignoreCloseExceptions) {
                try {
                    this.closeable.close();
                    return;
                } catch (IOException e) {
                    return;
                }
            }
            this.closeable.close();
        }
    }

    public static class Base64 {
        private static final byte[] _STANDARD_ALPHABET = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};

        private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
            int i;
            int i2 = 0;
            byte[] ALPHABET = _STANDARD_ALPHABET;
            if (numSigBytes > 0) {
                i = (source[srcOffset] << 24) >>> 8;
            } else {
                i = 0;
            }
            int i3 = (numSigBytes > 1 ? (source[srcOffset + 1] << 24) >>> 16 : 0) | i;
            if (numSigBytes > 2) {
                i2 = (source[srcOffset + 2] << 24) >>> 24;
            }
            int inBuff = i3 | i2;
            switch (numSigBytes) {
                case 1:
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                    destination[destOffset + 2] = (byte) 61;
                    destination[destOffset + 3] = (byte) 61;
                    break;
                case 2:
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                    destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 63];
                    destination[destOffset + 3] = (byte) 61;
                    break;
                case 3:
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                    destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 63];
                    destination[destOffset + 3] = ALPHABET[inBuff & 63];
                    break;
            }
            return destination;
        }

        public static String encodeBytes(byte[] source) {
            return encodeBytes(source, 0, source.length);
        }

        private static String encodeBytes(byte[] source, int off, int len) {
            if (source == null) {
                throw new NullPointerException("Cannot serialize a null array.");
            } else if (len < 0) {
                throw new IllegalArgumentException("Cannot have length offset: " + len);
            } else if (0 + len > source.length) {
                throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Cannot have offset of %d and length of %d with array of length %d", new Object[]{Integer.valueOf(0), Integer.valueOf(len), Integer.valueOf(source.length)}));
            } else {
                byte[] encoded;
                Object obj = new byte[((len % 3 > 0 ? 4 : 0) + ((len / 3) << 2))];
                int i = len - 2;
                int i2 = 0;
                int i3 = 0;
                while (i3 < i) {
                    encode3to4(source, i3 + 0, 3, obj, i2);
                    i3 += 3;
                    i2 += 4;
                }
                if (i3 < len) {
                    encode3to4(source, i3 + 0, len - i3, obj, i2);
                    i2 += 4;
                }
                if (i2 <= obj.length - 1) {
                    encoded = new byte[i2];
                    System.arraycopy(obj, 0, encoded, 0, i2);
                } else {
                    Object encoded2 = obj;
                }
                try {
                    return new String(encoded, "US-ASCII");
                } catch (UnsupportedEncodingException e) {
                    return new String(encoded);
                }
            }
        }
    }

    public interface ConnectionFactory {
        public static final ConnectionFactory DEFAULT = new ConnectionFactory() {
            public final HttpURLConnection create(URL url) throws IOException {
                return (HttpURLConnection) url.openConnection();
            }
        };

        HttpURLConnection create(URL url) throws IOException;
    }

    public static class HttpRequestException extends RuntimeException {
        protected HttpRequestException(IOException cause) {
            super(cause);
        }

        public final /* bridge */ /* synthetic */ Throwable getCause() {
            return (IOException) super.getCause();
        }
    }

    public static class RequestOutputStream extends BufferedOutputStream {
        private final CharsetEncoder encoder;

        public RequestOutputStream(OutputStream stream, String charset, int bufferSize) {
            super(stream, bufferSize);
            this.encoder = Charset.forName(HttpRequest.getValidCharset(charset)).newEncoder();
        }

        public final RequestOutputStream write(String value) throws IOException {
            ByteBuffer bytes = this.encoder.encode(CharBuffer.wrap(value));
            super.write(bytes.array(), 0, bytes.limit());
            return this;
        }
    }

    private static String getValidCharset(String charset) {
        return (charset == null || charset.length() <= 0) ? "UTF-8" : charset;
    }

    private static String encode(CharSequence url) throws HttpRequestException {
        try {
            URL parsed = new URL(url.toString());
            String host = parsed.getHost();
            int port = parsed.getPort();
            if (port != -1) {
                host = host + ':' + Integer.toString(port);
            }
            try {
                String encoded = new URI(parsed.getProtocol(), host, parsed.getPath(), parsed.getQuery(), null).toASCIIString();
                int paramsStart = encoded.indexOf(63);
                if (paramsStart > 0 && paramsStart + 1 < encoded.length()) {
                    encoded = encoded.substring(0, paramsStart + 1) + encoded.substring(paramsStart + 1).replace("+", "%2B");
                }
                return encoded;
            } catch (URISyntaxException e) {
                IOException io = new IOException("Parsing URI failed");
                io.initCause(e);
                throw new HttpRequestException(io);
            }
        } catch (IOException e2) {
            throw new HttpRequestException(e2);
        }
    }

    private static String append(CharSequence url, Map<?, ?> params) {
        String baseUrl = url.toString();
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        StringBuilder result = new StringBuilder(baseUrl);
        if (baseUrl.indexOf(58) + 2 == baseUrl.lastIndexOf(47)) {
            result.append('/');
        }
        int indexOf = baseUrl.indexOf(63);
        int length = result.length() - 1;
        if (indexOf == -1) {
            result.append('?');
        } else if (indexOf < length && baseUrl.charAt(length) != '&') {
            result.append('&');
        }
        Iterator<?> iterator = params.entrySet().iterator();
        Entry<?, ?> entry = (Entry) iterator.next();
        result.append(entry.getKey().toString());
        result.append('=');
        Object value = entry.getValue();
        if (value != null) {
            result.append(value);
        }
        while (iterator.hasNext()) {
            result.append('&');
            entry = (Entry) iterator.next();
            result.append(entry.getKey().toString());
            result.append('=');
            value = entry.getValue();
            if (value != null) {
                result.append(value);
            }
        }
        return result.toString();
    }

    public static HttpRequest get(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        return new HttpRequest(encode(append(baseUrl, params)), "GET");
    }

    public static HttpRequest post(CharSequence baseUrl, Map<?, ?> params, boolean encode) {
        return new HttpRequest(encode(append(baseUrl, params)), "POST");
    }

    public static HttpRequest put(CharSequence url) throws HttpRequestException {
        return new HttpRequest(url, "PUT");
    }

    public static HttpRequest delete(CharSequence url) throws HttpRequestException {
        return new HttpRequest(url, "DELETE");
    }

    private HttpRequest(CharSequence url, String method) throws HttpRequestException {
        try {
            this.url = new URL(url.toString());
            this.requestMethod = method;
        } catch (MalformedURLException e) {
            throw new HttpRequestException(e);
        }
    }

    private HttpURLConnection createConnection() {
        try {
            HttpURLConnection connection = CONNECTION_FACTORY.create(this.url);
            connection.setRequestMethod(this.requestMethod);
            return connection;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public final String toString() {
        return getConnection().getRequestMethod() + ' ' + getConnection().getURL();
    }

    public final HttpURLConnection getConnection() {
        if (this.connection == null) {
            this.connection = createConnection();
        }
        return this.connection;
    }

    public final int code() throws HttpRequestException {
        try {
            closeOutput();
            return getConnection().getResponseCode();
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    private InputStream stream() throws HttpRequestException {
        InputStream stream;
        if (code() < 400) {
            try {
                stream = getConnection().getInputStream();
            } catch (IOException e) {
                throw new HttpRequestException(e);
            }
        }
        stream = getConnection().getErrorStream();
        if (stream == null) {
            try {
                stream = getConnection().getInputStream();
            } catch (IOException e2) {
                throw new HttpRequestException(e2);
            }
        }
        return stream;
    }

    public final HttpRequest header(String name, String value) {
        getConnection().setRequestProperty(name, value);
        return this;
    }

    public final String header(String name) throws HttpRequestException {
        closeOutputQuietly();
        return getConnection().getHeaderField(name);
    }

    private static String getParam(String value, String paramName) {
        if (value == null || value.length() == 0) {
            return null;
        }
        int length = value.length();
        int start = value.indexOf(59) + 1;
        if (start == 0 || start == length) {
            return null;
        }
        int end = value.indexOf(59, start);
        if (end == -1) {
            end = length;
        }
        while (start < end) {
            int nameEnd = value.indexOf(61, start);
            if (nameEnd != -1 && nameEnd < end && paramName.equals(value.substring(start, nameEnd).trim())) {
                String paramValue = value.substring(nameEnd + 1, end).trim();
                int valueLength = paramValue.length();
                if (valueLength != 0) {
                    if (valueLength > 2 && '\"' == paramValue.charAt(0) && '\"' == paramValue.charAt(valueLength - 1)) {
                        return paramValue.substring(1, valueLength - 1);
                    }
                    return paramValue;
                }
            }
            start = end + 1;
            end = value.indexOf(59, start);
            if (end == -1) {
                end = length;
            }
        }
        return null;
    }

    private HttpRequest copy(InputStream input, OutputStream output) throws IOException {
        final InputStream inputStream = input;
        final OutputStream outputStream = output;
        return (HttpRequest) new CloseOperation<HttpRequest>(input, this.ignoreCloseExceptions) {
            public final /* bridge */ /* synthetic */ Object run() throws HttpRequestException, IOException {
                byte[] bArr = new byte[HttpRequest.this.bufferSize];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        return HttpRequest.this;
                    }
                    outputStream.write(bArr, 0, read);
                }
            }
        }.call();
    }

    private HttpRequest closeOutput() throws IOException {
        if (this.output != null) {
            if (this.multipart) {
                this.output.write("\r\n--00content0boundary00--\r\n");
            }
            if (this.ignoreCloseExceptions) {
                try {
                    this.output.close();
                } catch (IOException e) {
                }
            } else {
                this.output.close();
            }
            this.output = null;
        }
        return this;
    }

    private HttpRequest closeOutputQuietly() throws HttpRequestException {
        try {
            return closeOutput();
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    private HttpRequest openOutput() throws IOException {
        if (this.output == null) {
            getConnection().setDoOutput(true);
            this.output = new RequestOutputStream(getConnection().getOutputStream(), getParam(getConnection().getRequestProperty("Content-Type"), "charset"), this.bufferSize);
        }
        return this;
    }

    private HttpRequest startPart() throws IOException {
        if (this.multipart) {
            this.output.write("\r\n--00content0boundary00\r\n");
        } else {
            this.multipart = true;
            header("Content-Type", "multipart/form-data; boundary=00content0boundary00").openOutput();
            this.output.write("--00content0boundary00\r\n");
        }
        return this;
    }

    private HttpRequest writePartHeader(String name, String filename, String contentType) throws IOException {
        StringBuilder partBuffer = new StringBuilder();
        partBuffer.append("form-data; name=\"").append(name);
        if (filename != null) {
            partBuffer.append("\"; filename=\"").append(filename);
        }
        partBuffer.append('\"');
        partHeader("Content-Disposition", partBuffer.toString());
        if (contentType != null) {
            partHeader("Content-Type", contentType);
        }
        return send("\r\n");
    }

    public final HttpRequest part(String name, String filename, String part) throws HttpRequestException {
        return part(name, filename, null, part);
    }

    private HttpRequest part(String name, String filename, String contentType, String part) throws HttpRequestException {
        try {
            startPart();
            writePartHeader(name, filename, null);
            this.output.write(part);
            return this;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public final HttpRequest part(String name, Number part) throws HttpRequestException {
        String obj;
        if (part != null) {
            obj = part.toString();
        } else {
            obj = null;
        }
        return part(name, null, obj);
    }

    public final HttpRequest part(String name, String filename, String contentType, File part) throws HttpRequestException {
        IOException e;
        Throwable th;
        InputStream stream = null;
        try {
            InputStream stream2 = new BufferedInputStream(new FileInputStream(part));
            try {
                HttpRequest part2 = part(name, filename, contentType, stream2);
                try {
                    stream2.close();
                } catch (IOException e2) {
                }
                return part2;
            } catch (IOException e3) {
                e = e3;
                stream = stream2;
                try {
                    throw new HttpRequestException(e);
                } catch (Throwable th2) {
                    th = th2;
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e4) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                stream = stream2;
                if (stream != null) {
                    stream.close();
                }
                throw th;
            }
        } catch (IOException e5) {
            e = e5;
            throw new HttpRequestException(e);
        }
    }

    public final HttpRequest part(String name, String filename, String contentType, InputStream part) throws HttpRequestException {
        try {
            startPart();
            writePartHeader(name, filename, contentType);
            copy(part, this.output);
            return this;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    private HttpRequest partHeader(String name, String value) throws HttpRequestException {
        return send(name).send(": ").send(value).send("\r\n");
    }

    private HttpRequest send(CharSequence value) throws HttpRequestException {
        try {
            openOutput();
            this.output.write(value.toString());
            return this;
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public final String method() {
        return getConnection().getRequestMethod();
    }

    private String body(String charset) throws HttpRequestException {
        ByteArrayOutputStream output;
        closeOutputQuietly();
        int headerFieldInt = getConnection().getHeaderFieldInt("Content-Length", -1);
        if (headerFieldInt > 0) {
            output = new ByteArrayOutputStream(headerFieldInt);
        } else {
            output = new ByteArrayOutputStream();
        }
        try {
            copy(new BufferedInputStream(stream(), this.bufferSize), output);
            return output.toString(getValidCharset(charset));
        } catch (IOException e) {
            throw new HttpRequestException(e);
        }
    }

    public final String body() throws HttpRequestException {
        return body(getParam(header("Content-Type"), "charset"));
    }
}
