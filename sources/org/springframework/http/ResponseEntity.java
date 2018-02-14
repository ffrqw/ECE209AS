package org.springframework.http;

import com.j256.ormlite.stmt.query.SimpleComparison;
import org.springframework.util.MultiValueMap;

public final class ResponseEntity<T> extends HttpEntity<T> {
    private final HttpStatus statusCode;

    public ResponseEntity(MultiValueMap<String, String> headers, HttpStatus statusCode) {
        super((MultiValueMap) headers);
        this.statusCode = statusCode;
    }

    public ResponseEntity(T body, MultiValueMap<String, String> headers, HttpStatus statusCode) {
        super(body, headers);
        this.statusCode = statusCode;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder(SimpleComparison.LESS_THAN_OPERATION);
        builder.append(this.statusCode.toString());
        builder.append(' ');
        builder.append(this.statusCode.getReasonPhrase());
        builder.append(',');
        T body = getBody();
        HttpHeaders headers = getHeaders();
        if (body != null) {
            builder.append(body);
            if (headers != null) {
                builder.append(',');
            }
        }
        if (headers != null) {
            builder.append(headers);
        }
        builder.append('>');
        return builder.toString();
    }
}
