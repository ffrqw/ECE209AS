package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.SerializableString;
import java.io.Serializable;

public class SerializedString implements SerializableString, Serializable {
    protected char[] _quotedChars;
    protected byte[] _quotedUTF8Ref;
    protected byte[] _unquotedUTF8Ref;
    protected final String _value;

    public SerializedString(String v) {
        if (v == null) {
            throw new IllegalStateException("Null String illegal for SerializedString");
        }
        this._value = v;
    }

    public final String getValue() {
        return this._value;
    }

    public final char[] asQuotedChars() {
        char[] result = this._quotedChars;
        if (result != null) {
            return result;
        }
        result = JsonStringEncoder.getInstance().quoteAsString(this._value);
        this._quotedChars = result;
        return result;
    }

    public final byte[] asUnquotedUTF8() {
        byte[] result = this._unquotedUTF8Ref;
        if (result != null) {
            return result;
        }
        result = JsonStringEncoder.getInstance().encodeAsUTF8(this._value);
        this._unquotedUTF8Ref = result;
        return result;
    }

    public final byte[] asQuotedUTF8() {
        byte[] result = this._quotedUTF8Ref;
        if (result != null) {
            return result;
        }
        result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
        this._quotedUTF8Ref = result;
        return result;
    }

    public int appendQuotedUTF8(byte[] buffer, int offset) {
        byte[] result = this._quotedUTF8Ref;
        if (result == null) {
            result = JsonStringEncoder.getInstance().quoteAsUTF8(this._value);
            this._quotedUTF8Ref = result;
        }
        int length = result.length;
        if (offset + length > buffer.length) {
            return -1;
        }
        System.arraycopy(result, 0, buffer, offset, length);
        return length;
    }

    public final String toString() {
        return this._value;
    }

    public final int hashCode() {
        return this._value.hashCode();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        return this._value.equals(((SerializedString) o)._value);
    }
}
