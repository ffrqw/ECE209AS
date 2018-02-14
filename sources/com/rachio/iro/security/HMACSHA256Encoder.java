package com.rachio.iro.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMACSHA256Encoder {
    public static byte[] encodeString(String str, Object salt) {
        byte[] hmacData = null;
        if (str != null) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(str.getBytes("UTF-8"), "HmacSHA256");
                Mac mac = getMac();
                mac.init(secretKey);
                hmacData = mac.doFinal(salt.toString().getBytes("UTF-8"));
            } catch (InvalidKeyException ike) {
                throw new RuntimeException("Invalid Key while encrypting.", ike);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Unsupported Encoding while encrypting.", e);
            }
        }
        return hmacData;
    }

    private static final Mac getMac() throws IllegalArgumentException {
        try {
            return Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [HmacSHA256]");
        }
    }
}
