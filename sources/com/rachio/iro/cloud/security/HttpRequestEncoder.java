package com.rachio.iro.cloud.security;

import com.rachio.iro.security.HMACSHA256Encoder;
import com.rachio.iro.utils.StringUtils;
import io.fabric.sdk.android.services.network.HttpRequest.Base64;
import java.security.MessageDigest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class HttpRequestEncoder {
    public static void encodeHttpHeaders(HttpHeaders headers, String requestUri, HttpMethod method, byte[] body, String secretKey) {
        String signature;
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String md5 = body != null ? MD5(body) : null;
        StringBuilder sb = new StringBuilder();
        sb.append(method.name());
        sb.append("\n");
        if (StringUtils.isNotBlank(md5)) {
            sb.append(md5);
        }
        sb.append("\n");
        MediaType contentType = headers.getContentType();
        sb.append(contentType.getType() + "/" + contentType.getSubtype());
        sb.append("\n");
        sb.append(timestamp);
        sb.append("\n");
        sb.append(requestUri);
        String stringBuilder = sb.toString();
        if (secretKey != null) {
            signature = Base64.encodeBytes(HMACSHA256Encoder.encodeString(secretKey, stringBuilder));
        } else {
            signature = "";
        }
        headers.add("timestamp", timestamp);
        headers.add("signature", signature);
        if (md5 != null) {
            headers.add("content-md5", md5);
        }
    }

    private static String MD5(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(data);
            return new String(Base64.encodeBytes(digest.digest()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
