package com.fasterxml.jackson.core;

public enum JsonEncoding {
    UTF8("UTF-8", false, 8),
    UTF16_BE("UTF-16BE", true, 16),
    UTF16_LE("UTF-16LE", false, 16),
    UTF32_BE("UTF-32BE", true, 32),
    UTF32_LE("UTF-32LE", false, 32);
    
    private final boolean _bigEndian;
    private final int _bits;
    private final String _javaName;

    private JsonEncoding(String javaName, boolean bigEndian, int bits) {
        this._javaName = javaName;
        this._bigEndian = bigEndian;
        this._bits = bits;
    }

    public final String getJavaName() {
        return this._javaName;
    }

    public final boolean isBigEndian() {
        return this._bigEndian;
    }

    public final int bits() {
        return this._bits;
    }
}
