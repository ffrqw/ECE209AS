package com.electricimp.blinkup;

import android.util.Log;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class MCrypt {
    private String SecretKey = "e:imp:02:07:2013";
    private Cipher cipher;
    private String iv = "deadbeefbeefdead";
    private IvParameterSpec ivspec = new IvParameterSpec(this.iv.getBytes());
    private SecretKeySpec keyspec = new SecretKeySpec(this.SecretKey.getBytes(), "AES");

    public MCrypt(String i, String key) {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            Log.e("BlinkUp", Log.getStackTraceString(e));
        } catch (NoSuchPaddingException e2) {
            Log.e("BlinkUp", Log.getStackTraceString(e2));
        }
    }

    public final byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.cipher.init(1, this.keyspec, this.ivspec);
            return this.cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }
    }

    public final byte[] decrypt(String code) throws Exception {
        if (code == null || code.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.cipher.init(2, this.keyspec, this.ivspec);
            return this.cipher.doFinal(hexToBytes(code));
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
    }

    private static byte[] hexToBytes(String str) {
        byte[] bArr = null;
        if (str != null && str.length() >= 2) {
            int len = str.length() / 2;
            bArr = new byte[len];
            for (int i = 0; i < len; i++) {
                bArr[i] = (byte) Integer.parseInt(str.substring(i << 1, (i << 1) + 2), 16);
            }
        }
        return bArr;
    }

    private static String padString(String source) {
        int padLength = 16 - (source.length() % 16);
        for (int i = 0; i < padLength; i++) {
            source = new StringBuilder(String.valueOf(source)).append('\u0000').toString();
        }
        return source;
    }
}
