package org.springframework.web.client;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

public final class DefaultResponseErrorHandler implements ResponseErrorHandler {
    public final boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus httpStatusCode = getHttpStatusCode(response);
        return Series.valueOf(httpStatusCode) == Series.CLIENT_ERROR || Series.valueOf(httpStatusCode) == Series.SERVER_ERROR;
    }

    private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        try {
            return response.getStatusCode();
        } catch (IllegalArgumentException e) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
        }
    }

    public final void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = getHttpStatusCode(response);
        switch (Series.valueOf(statusCode)) {
            case CLIENT_ERROR:
                throw new HttpClientErrorException(statusCode, response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
            case SERVER_ERROR:
                throw new HttpServerErrorException(statusCode, response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
        }
    }

    private static byte[] getResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            if (responseBody != null) {
                OutputStream byteArrayOutputStream = new ByteArrayOutputStream(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
                FileCopyUtils.copy(responseBody, byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
        }
        return new byte[0];
    }

    private static Charset getCharset(ClientHttpResponse response) {
        MediaType contentType = response.getHeaders().getContentType();
        return contentType != null ? contentType.getCharSet() : null;
    }
}
