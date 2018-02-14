package com.fasterxml.jackson.core.format;

import com.fasterxml.jackson.core.JsonFactory;
import java.io.InputStream;

public class DataFormatMatcher {
    protected final byte[] _bufferedData;
    protected final int _bufferedLength;
    protected final int _bufferedStart;
    protected final JsonFactory _match;
    protected final MatchStrength _matchStrength;
    protected final InputStream _originalStream;

    protected DataFormatMatcher(InputStream in, byte[] buffered, int bufferedStart, int bufferedLength, JsonFactory match, MatchStrength strength) {
        this._originalStream = in;
        this._bufferedData = buffered;
        this._bufferedStart = bufferedStart;
        this._bufferedLength = bufferedLength;
        this._match = match;
        this._matchStrength = strength;
    }
}
