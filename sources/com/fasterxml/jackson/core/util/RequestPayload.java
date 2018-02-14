package com.fasterxml.jackson.core.util;

import java.io.IOException;
import java.io.Serializable;

public class RequestPayload implements Serializable {
    protected String _charset;
    protected byte[] _payloadAsBytes;
    protected CharSequence _payloadAsText;

    public RequestPayload(byte[] bytes, String charset) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        this._payloadAsBytes = bytes;
        if (charset == null || charset.isEmpty()) {
            charset = "UTF-8";
        }
        this._charset = charset;
    }

    public RequestPayload(CharSequence str) {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        this._payloadAsText = str;
    }

    public String toString() {
        if (this._payloadAsBytes == null) {
            return this._payloadAsText.toString();
        }
        try {
            return new String(this._payloadAsBytes, this._charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
