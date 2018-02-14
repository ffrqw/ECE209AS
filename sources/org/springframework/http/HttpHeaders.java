package org.springframework.http;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public final class HttpHeaders implements Serializable, MultiValueMap<String, String> {
    private static final String[] DATE_FORMATS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM dd HH:mm:ss yyyy"};
    private static TimeZone GMT = TimeZone.getTimeZone("GMT");
    private final Map<String, List<String>> headers;

    public final /* bridge */ /* synthetic */ Object put(Object x0, Object x1) {
        return (List) this.headers.put((String) x0, (List) x1);
    }

    public HttpHeaders() {
        this(new LinkedCaseInsensitiveMap(8, Locale.ENGLISH), false);
    }

    private HttpHeaders(Map<String, List<String>> headers, boolean readOnly) {
        Assert.notNull(headers, "'headers' must not be null");
        if (readOnly) {
            Map<String, List<String>> map = new LinkedCaseInsensitiveMap(headers.size(), Locale.ENGLISH);
            for (Entry<String, List<String>> entry : headers.entrySet()) {
                map.put(entry.getKey(), Collections.unmodifiableList((List) entry.getValue()));
            }
            this.headers = Collections.unmodifiableMap(map);
            return;
        }
        this.headers = headers;
    }

    public final void setAccept(List<MediaType> acceptableMediaTypes) {
        set("Accept", MediaType.toString(acceptableMediaTypes));
    }

    public final void setAcceptCharset(List<Charset> acceptableCharsets) {
        StringBuilder builder = new StringBuilder();
        Iterator<Charset> iterator = acceptableCharsets.iterator();
        while (iterator.hasNext()) {
            builder.append(((Charset) iterator.next()).name().toLowerCase(Locale.ENGLISH));
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        set("Accept-Charset", builder.toString());
    }

    public final void setConnection(String connection) {
        set("Connection", connection);
    }

    public final void setContentDispositionFormData(String name, String filename) {
        Assert.notNull(name, "'name' must not be null");
        StringBuilder builder = new StringBuilder("form-data; name=\"");
        builder.append(name).append('\"');
        if (filename != null) {
            builder.append("; filename=\"");
            builder.append(filename).append('\"');
        }
        set("Content-Disposition", builder.toString());
    }

    public final List<ContentCodingType> getContentEncoding() {
        String value = getFirst("Content-Encoding");
        return value != null ? ContentCodingType.parseCodingTypes(value) : Collections.emptyList();
    }

    public final void setContentLength(long contentLength) {
        set("Content-Length", Long.toString(contentLength));
    }

    public final long getContentLength() {
        String value = getFirst("Content-Length");
        return value != null ? Long.parseLong(value) : -1;
    }

    public final void setContentType(MediaType mediaType) {
        boolean z;
        boolean z2 = true;
        if (mediaType.isWildcardType()) {
            z = false;
        } else {
            z = true;
        }
        Assert.isTrue(z, "'Content-Type' cannot contain wildcard type '*'");
        if (mediaType.isWildcardSubtype()) {
            z2 = false;
        }
        Assert.isTrue(z2, "'Content-Type' cannot contain wildcard subtype '*'");
        set("Content-Type", mediaType.toString());
    }

    public final MediaType getContentType() {
        String value = getFirst("Content-Type");
        return StringUtils.hasLength(value) ? MediaType.parseMediaType(value) : null;
    }

    public final String getFirst(String headerName) {
        List<String> headerValues = (List) this.headers.get(headerName);
        return headerValues != null ? (String) headerValues.get(0) : null;
    }

    public final void add(String headerName, String headerValue) {
        List<String> headerValues = (List) this.headers.get(headerName);
        if (headerValues == null) {
            headerValues = new LinkedList();
            this.headers.put(headerName, headerValues);
        }
        headerValues.add(headerValue);
    }

    public final void set(String headerName, String headerValue) {
        List<String> headerValues = new LinkedList();
        headerValues.add(headerValue);
        this.headers.put(headerName, headerValues);
    }

    public final int size() {
        return this.headers.size();
    }

    public final boolean isEmpty() {
        return this.headers.isEmpty();
    }

    public final boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    public final boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    public final List<String> get(Object key) {
        return (List) this.headers.get(key);
    }

    public final List<String> remove(Object key) {
        return (List) this.headers.remove(key);
    }

    public final void putAll(Map<? extends String, ? extends List<String>> map) {
        this.headers.putAll(map);
    }

    public final void clear() {
        this.headers.clear();
    }

    public final Set<String> keySet() {
        return this.headers.keySet();
    }

    public final Collection<List<String>> values() {
        return this.headers.values();
    }

    public final Set<Entry<String, List<String>>> entrySet() {
        return this.headers.entrySet();
    }

    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HttpHeaders)) {
            return false;
        }
        return this.headers.equals(((HttpHeaders) other).headers);
    }

    public final int hashCode() {
        return this.headers.hashCode();
    }

    public final String toString() {
        return this.headers.toString();
    }

    public static HttpHeaders readOnlyHttpHeaders(HttpHeaders headers) {
        return new HttpHeaders(headers, true);
    }
}
