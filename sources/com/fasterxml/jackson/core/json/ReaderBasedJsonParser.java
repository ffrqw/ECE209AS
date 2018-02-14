package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.CharsToNameCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import com.shinobicontrols.charts.R;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class ReaderBasedJsonParser extends ParserBase {
    protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
    protected boolean _bufferRecyclable;
    protected final int _hashSeed;
    protected char[] _inputBuffer;
    protected int _nameStartCol;
    protected long _nameStartOffset;
    protected int _nameStartRow;
    protected ObjectCodec _objectCodec;
    protected Reader _reader;
    protected final CharsToNameCanonicalizer _symbols;
    protected boolean _tokenIncomplete;

    public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st, char[] inputBuffer, int start, int end, boolean bufferRecyclable) {
        super(ctxt, features);
        this._reader = r;
        this._inputBuffer = inputBuffer;
        this._inputPtr = start;
        this._inputEnd = end;
        this._objectCodec = codec;
        this._symbols = st;
        this._hashSeed = st.hashSeed();
        this._bufferRecyclable = bufferRecyclable;
    }

    public ReaderBasedJsonParser(IOContext ctxt, int features, Reader r, ObjectCodec codec, CharsToNameCanonicalizer st) {
        super(ctxt, features);
        this._reader = r;
        this._inputBuffer = ctxt.allocTokenBuffer();
        this._inputPtr = 0;
        this._inputEnd = 0;
        this._objectCodec = codec;
        this._symbols = st;
        this._hashSeed = st.hashSeed();
        this._bufferRecyclable = true;
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public void setCodec(ObjectCodec c) {
        this._objectCodec = c;
    }

    public int releaseBuffered(Writer w) throws IOException {
        int count = this._inputEnd - this._inputPtr;
        if (count <= 0) {
            return 0;
        }
        w.write(this._inputBuffer, this._inputPtr, count);
        return count;
    }

    public Object getInputSource() {
        return this._reader;
    }

    @Deprecated
    protected char getNextChar(String eofMsg) throws IOException {
        return getNextChar(eofMsg, null);
    }

    protected char getNextChar(String eofMsg, JsonToken forToken) throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            _reportInvalidEOF(eofMsg, forToken);
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return cArr[i];
    }

    protected void _closeInput() throws IOException {
        if (this._reader != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(Feature.AUTO_CLOSE_SOURCE)) {
                this._reader.close();
            }
            this._reader = null;
        }
    }

    protected void _releaseBuffers() throws IOException {
        super._releaseBuffers();
        this._symbols.release();
        if (this._bufferRecyclable) {
            char[] buf = this._inputBuffer;
            if (buf != null) {
                this._inputBuffer = null;
                this._ioContext.releaseTokenBuffer(buf);
            }
        }
    }

    protected void _loadMoreGuaranteed() throws IOException {
        if (!_loadMore()) {
            _reportInvalidEOF();
        }
    }

    protected boolean _loadMore() throws IOException {
        int bufSize = this._inputEnd;
        this._currInputProcessed += (long) bufSize;
        this._currInputRowStart -= bufSize;
        this._nameStartOffset -= (long) bufSize;
        if (this._reader == null) {
            return false;
        }
        int count = this._reader.read(this._inputBuffer, 0, this._inputBuffer.length);
        if (count > 0) {
            this._inputPtr = 0;
            this._inputEnd = count;
            return true;
        }
        _closeInput();
        if (count != 0) {
            return false;
        }
        throw new IOException("Reader returned 0 characters when trying to read " + this._inputEnd);
    }

    public final String getText() throws IOException {
        JsonToken t = this._currToken;
        if (t != JsonToken.VALUE_STRING) {
            return _getText2(t);
        }
        if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            _finishString();
        }
        return this._textBuffer.contentsAsString();
    }

    public int getText(Writer writer) throws IOException {
        JsonToken t = this._currToken;
        if (t == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.contentsToWriter(writer);
        } else if (t == JsonToken.FIELD_NAME) {
            String n = this._parsingContext.getCurrentName();
            writer.write(n);
            return n.length();
        } else if (t == null) {
            return 0;
        } else {
            if (t.isNumeric()) {
                return this._textBuffer.contentsToWriter(writer);
            }
            char[] ch = t.asCharArray();
            writer.write(ch);
            return ch.length;
        }
    }

    public final String getValueAsString() throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.contentsAsString();
        } else if (this._currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        } else {
            return super.getValueAsString(null);
        }
    }

    public final String getValueAsString(String defValue) throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.contentsAsString();
        } else if (this._currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        } else {
            return super.getValueAsString(defValue);
        }
    }

    protected final String _getText2(JsonToken t) {
        if (t == null) {
            return null;
        }
        switch (t.id()) {
            case 5:
                return this._parsingContext.getCurrentName();
            case 6:
            case 7:
            case 8:
                return this._textBuffer.contentsAsString();
            default:
                return t.asString();
        }
    }

    public final char[] getTextCharacters() throws IOException {
        if (this._currToken == null) {
            return null;
        }
        switch (this._currToken.id()) {
            case 5:
                if (!this._nameCopied) {
                    String name = this._parsingContext.getCurrentName();
                    int nameLen = name.length();
                    if (this._nameCopyBuffer == null) {
                        this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
                    } else if (this._nameCopyBuffer.length < nameLen) {
                        this._nameCopyBuffer = new char[nameLen];
                    }
                    name.getChars(0, nameLen, this._nameCopyBuffer, 0);
                    this._nameCopied = true;
                }
                return this._nameCopyBuffer;
            case 6:
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                    break;
                }
                break;
            case 7:
            case 8:
                break;
            default:
                return this._currToken.asCharArray();
        }
        return this._textBuffer.getTextBuffer();
    }

    public final int getTextLength() throws IOException {
        if (this._currToken == null) {
            return 0;
        }
        switch (this._currToken.id()) {
            case 5:
                return this._parsingContext.getCurrentName().length();
            case 6:
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                    break;
                }
                break;
            case 7:
            case 8:
                break;
            default:
                return this._currToken.asCharArray().length;
        }
        return this._textBuffer.size();
    }

    public final int getTextOffset() throws IOException {
        if (this._currToken == null) {
            return 0;
        }
        switch (this._currToken.id()) {
            case 6:
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                    break;
                }
                break;
            case 7:
            case 8:
                break;
            default:
                return 0;
        }
        return this._textBuffer.getTextOffset();
    }

    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
        if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null)) {
            _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
        }
        if (this._tokenIncomplete) {
            try {
                this._binaryValue = _decodeBase64(b64variant);
                this._tokenIncomplete = false;
            } catch (IllegalArgumentException iae) {
                throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
            }
        } else if (this._binaryValue == null) {
            ByteArrayBuilder builder = _getByteArrayBuilder();
            _decodeBase64(getText(), builder, b64variant);
            this._binaryValue = builder.toByteArray();
        }
        return this._binaryValue;
    }

    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
        if (this._tokenIncomplete && this._currToken == JsonToken.VALUE_STRING) {
            byte[] buf = this._ioContext.allocBase64Buffer();
            try {
                int _readBinary = _readBinary(b64variant, out, buf);
                return _readBinary;
            } finally {
                this._ioContext.releaseBase64Buffer(buf);
            }
        } else {
            byte[] b = getBinaryValue(b64variant);
            out.write(b);
            return b.length;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int _readBinary(com.fasterxml.jackson.core.Base64Variant r11, java.io.OutputStream r12, byte[] r13) throws java.io.IOException {
        /*
        r10 = this;
        r5 = 0;
        r7 = r13.length;
        r4 = r7 + -3;
        r3 = 0;
    L_0x0005:
        r7 = r10._inputPtr;
        r8 = r10._inputEnd;
        if (r7 < r8) goto L_0x000e;
    L_0x000b:
        r10._loadMoreGuaranteed();
    L_0x000e:
        r7 = r10._inputBuffer;
        r8 = r10._inputPtr;
        r9 = r8 + 1;
        r10._inputPtr = r9;
        r1 = r7[r8];
        r7 = 32;
        if (r1 <= r7) goto L_0x0005;
    L_0x001c:
        r0 = r11.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x002d;
    L_0x0022:
        r7 = 34;
        if (r1 == r7) goto L_0x0086;
    L_0x0026:
        r7 = 0;
        r0 = r10._decodeBase64Escape(r11, r1, r7);
        if (r0 < 0) goto L_0x0005;
    L_0x002d:
        if (r5 <= r4) goto L_0x0035;
    L_0x002f:
        r3 = r3 + r5;
        r7 = 0;
        r12.write(r13, r7, r5);
        r5 = 0;
    L_0x0035:
        r2 = r0;
        r7 = r10._inputPtr;
        r8 = r10._inputEnd;
        if (r7 < r8) goto L_0x003f;
    L_0x003c:
        r10._loadMoreGuaranteed();
    L_0x003f:
        r7 = r10._inputBuffer;
        r8 = r10._inputPtr;
        r9 = r8 + 1;
        r10._inputPtr = r9;
        r1 = r7[r8];
        r0 = r11.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x0054;
    L_0x004f:
        r7 = 1;
        r0 = r10._decodeBase64Escape(r11, r1, r7);
    L_0x0054:
        r7 = r2 << 6;
        r2 = r7 | r0;
        r7 = r10._inputPtr;
        r8 = r10._inputEnd;
        if (r7 < r8) goto L_0x0061;
    L_0x005e:
        r10._loadMoreGuaranteed();
    L_0x0061:
        r7 = r10._inputBuffer;
        r8 = r10._inputPtr;
        r9 = r8 + 1;
        r10._inputPtr = r9;
        r1 = r7[r8];
        r0 = r11.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x00db;
    L_0x0071:
        r7 = -2;
        if (r0 == r7) goto L_0x0096;
    L_0x0074:
        r7 = 34;
        if (r1 != r7) goto L_0x0091;
    L_0x0078:
        r7 = r11.usesPadding();
        if (r7 != 0) goto L_0x0091;
    L_0x007e:
        r2 = r2 >> 4;
        r6 = r5 + 1;
        r7 = (byte) r2;
        r13[r5] = r7;
        r5 = r6;
    L_0x0086:
        r7 = 0;
        r10._tokenIncomplete = r7;
        if (r5 <= 0) goto L_0x0090;
    L_0x008b:
        r3 = r3 + r5;
        r7 = 0;
        r12.write(r13, r7, r5);
    L_0x0090:
        return r3;
    L_0x0091:
        r7 = 2;
        r0 = r10._decodeBase64Escape(r11, r1, r7);
    L_0x0096:
        r7 = -2;
        if (r0 != r7) goto L_0x00db;
    L_0x0099:
        r7 = r10._inputPtr;
        r8 = r10._inputEnd;
        if (r7 < r8) goto L_0x00a2;
    L_0x009f:
        r10._loadMoreGuaranteed();
    L_0x00a2:
        r7 = r10._inputBuffer;
        r8 = r10._inputPtr;
        r9 = r8 + 1;
        r10._inputPtr = r9;
        r1 = r7[r8];
        r7 = r11.usesPaddingChar(r1);
        if (r7 != 0) goto L_0x00d1;
    L_0x00b2:
        r7 = 3;
        r8 = new java.lang.StringBuilder;
        r9 = "expected padding character '";
        r8.<init>(r9);
        r9 = r11.getPaddingChar();
        r8 = r8.append(r9);
        r9 = "'";
        r8 = r8.append(r9);
        r8 = r8.toString();
        r7 = r10.reportInvalidBase64Char(r11, r1, r7, r8);
        throw r7;
    L_0x00d1:
        r2 = r2 >> 4;
        r6 = r5 + 1;
        r7 = (byte) r2;
        r13[r5] = r7;
        r5 = r6;
        goto L_0x0005;
    L_0x00db:
        r7 = r2 << 6;
        r2 = r7 | r0;
        r7 = r10._inputPtr;
        r8 = r10._inputEnd;
        if (r7 < r8) goto L_0x00e8;
    L_0x00e5:
        r10._loadMoreGuaranteed();
    L_0x00e8:
        r7 = r10._inputBuffer;
        r8 = r10._inputPtr;
        r9 = r8 + 1;
        r10._inputPtr = r9;
        r1 = r7[r8];
        r0 = r11.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x012d;
    L_0x00f8:
        r7 = -2;
        if (r0 == r7) goto L_0x011a;
    L_0x00fb:
        r7 = 34;
        if (r1 != r7) goto L_0x0115;
    L_0x00ff:
        r7 = r11.usesPadding();
        if (r7 != 0) goto L_0x0115;
    L_0x0105:
        r2 = r2 >> 2;
        r6 = r5 + 1;
        r7 = r2 >> 8;
        r7 = (byte) r7;
        r13[r5] = r7;
        r5 = r6 + 1;
        r7 = (byte) r2;
        r13[r6] = r7;
        goto L_0x0086;
    L_0x0115:
        r7 = 3;
        r0 = r10._decodeBase64Escape(r11, r1, r7);
    L_0x011a:
        r7 = -2;
        if (r0 != r7) goto L_0x012d;
    L_0x011d:
        r2 = r2 >> 2;
        r6 = r5 + 1;
        r7 = r2 >> 8;
        r7 = (byte) r7;
        r13[r5] = r7;
        r5 = r6 + 1;
        r7 = (byte) r2;
        r13[r6] = r7;
        goto L_0x0005;
    L_0x012d:
        r7 = r2 << 6;
        r2 = r7 | r0;
        r6 = r5 + 1;
        r7 = r2 >> 16;
        r7 = (byte) r7;
        r13[r5] = r7;
        r5 = r6 + 1;
        r7 = r2 >> 8;
        r7 = (byte) r7;
        r13[r6] = r7;
        r6 = r5 + 1;
        r7 = (byte) r2;
        r13[r5] = r7;
        r5 = r6;
        goto L_0x0005;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser._readBinary(com.fasterxml.jackson.core.Base64Variant, java.io.OutputStream, byte[]):int");
    }

    public final JsonToken nextToken() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return _nextAfterName();
        }
        this._numTypesValid = 0;
        if (this._tokenIncomplete) {
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            close();
            this._currToken = null;
            return null;
        }
        this._binaryValue = null;
        JsonToken jsonToken;
        if (i == 93) {
            _updateLocation();
            if (!this._parsingContext.inArray()) {
                _reportMismatchedEndMarker(i, '}');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            jsonToken = JsonToken.END_ARRAY;
            this._currToken = jsonToken;
            return jsonToken;
        } else if (i == 125) {
            _updateLocation();
            if (!this._parsingContext.inObject()) {
                _reportMismatchedEndMarker(i, ']');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            jsonToken = JsonToken.END_OBJECT;
            this._currToken = jsonToken;
            return jsonToken;
        } else {
            if (this._parsingContext.expectComma()) {
                i = _skipComma(i);
            }
            boolean inObject = this._parsingContext.inObject();
            if (inObject) {
                _updateNameLocation();
                this._parsingContext.setCurrentName(i == 34 ? _parseName() : _handleOddName(i));
                this._currToken = JsonToken.FIELD_NAME;
                i = _skipColon();
            }
            _updateLocation();
            switch (i) {
                case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
                    this._tokenIncomplete = true;
                    jsonToken = JsonToken.VALUE_STRING;
                    break;
                case R.styleable.ChartTheme_sc_seriesLineColor4 /*45*/:
                    jsonToken = _parseNegNumber();
                    break;
                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4 /*48*/:
                case R.styleable.ChartTheme_sc_seriesLineColor5 /*49*/:
                case 50:
                case R.styleable.ChartTheme_sc_seriesAreaGradientColor5 /*51*/:
                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor5 /*52*/:
                case R.styleable.ChartTheme_sc_seriesLineColor6 /*53*/:
                case R.styleable.ChartTheme_sc_seriesAreaColor6 /*54*/:
                case R.styleable.ChartTheme_sc_seriesAreaGradientColor6 /*55*/:
                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor6 /*56*/:
                case R.styleable.ChartTheme_sc_pieDonutFlavorColor1 /*57*/:
                    jsonToken = _parsePosNumber(i);
                    break;
                case 91:
                    if (!inObject) {
                        this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                    }
                    jsonToken = JsonToken.START_ARRAY;
                    break;
                case 102:
                    _matchFalse();
                    jsonToken = JsonToken.VALUE_FALSE;
                    break;
                case 110:
                    _matchNull();
                    jsonToken = JsonToken.VALUE_NULL;
                    break;
                case 116:
                    break;
                case 123:
                    if (!inObject) {
                        this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                    }
                    jsonToken = JsonToken.START_OBJECT;
                    break;
                case 125:
                    _reportUnexpectedChar(i, "expected a value");
                    break;
                default:
                    jsonToken = _handleOddValue(i);
                    break;
            }
            _matchTrue();
            jsonToken = JsonToken.VALUE_TRUE;
            if (inObject) {
                this._nextToken = jsonToken;
                return this._currToken;
            }
            this._currToken = jsonToken;
            return jsonToken;
        }
    }

    private final JsonToken _nextAfterName() {
        this._nameCopied = false;
        JsonToken t = this._nextToken;
        this._nextToken = null;
        if (t == JsonToken.START_ARRAY) {
            this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (t == JsonToken.START_OBJECT) {
            this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        this._currToken = t;
        return t;
    }

    public void finishToken() throws IOException {
        if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            _finishString();
        }
    }

    public boolean nextFieldName(SerializableString sstr) throws IOException {
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            _nextAfterName();
            return false;
        }
        if (this._tokenIncomplete) {
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            close();
            this._currToken = null;
            return false;
        }
        this._binaryValue = null;
        if (i == 93) {
            _updateLocation();
            if (!this._parsingContext.inArray()) {
                _reportMismatchedEndMarker(i, '}');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            this._currToken = JsonToken.END_ARRAY;
            return false;
        } else if (i == 125) {
            _updateLocation();
            if (!this._parsingContext.inObject()) {
                _reportMismatchedEndMarker(i, ']');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            this._currToken = JsonToken.END_OBJECT;
            return false;
        } else {
            if (this._parsingContext.expectComma()) {
                i = _skipComma(i);
            }
            if (this._parsingContext.inObject()) {
                _updateNameLocation();
                if (i == 34) {
                    char[] nameChars = sstr.asQuotedChars();
                    int len = nameChars.length;
                    if ((this._inputPtr + len) + 4 < this._inputEnd) {
                        int end = this._inputPtr + len;
                        if (this._inputBuffer[end] == '\"') {
                            int offset = 0;
                            int ptr = this._inputPtr;
                            while (ptr != end) {
                                if (nameChars[offset] == this._inputBuffer[ptr]) {
                                    offset++;
                                    ptr++;
                                }
                            }
                            this._parsingContext.setCurrentName(sstr.getValue());
                            _isNextTokenNameYes(_skipColonFast(ptr + 1));
                            return true;
                        }
                    }
                }
                return _isNextTokenNameMaybe(i, sstr.getValue());
            }
            _updateLocation();
            _nextTokenNotInObject(i);
            return false;
        }
    }

    public String nextFieldName() throws IOException {
        String str = null;
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            _nextAfterName();
        } else {
            if (this._tokenIncomplete) {
                _skipString();
            }
            int i = _skipWSOrEnd();
            if (i < 0) {
                close();
                this._currToken = null;
            } else {
                this._binaryValue = null;
                if (i == 93) {
                    _updateLocation();
                    if (!this._parsingContext.inArray()) {
                        _reportMismatchedEndMarker(i, '}');
                    }
                    this._parsingContext = this._parsingContext.clearAndGetParent();
                    this._currToken = JsonToken.END_ARRAY;
                } else if (i == 125) {
                    _updateLocation();
                    if (!this._parsingContext.inObject()) {
                        _reportMismatchedEndMarker(i, ']');
                    }
                    this._parsingContext = this._parsingContext.clearAndGetParent();
                    this._currToken = JsonToken.END_OBJECT;
                } else {
                    if (this._parsingContext.expectComma()) {
                        i = _skipComma(i);
                    }
                    if (this._parsingContext.inObject()) {
                        _updateNameLocation();
                        str = i == 34 ? _parseName() : _handleOddName(i);
                        this._parsingContext.setCurrentName(str);
                        this._currToken = JsonToken.FIELD_NAME;
                        i = _skipColon();
                        _updateLocation();
                        if (i == 34) {
                            this._tokenIncomplete = true;
                            this._nextToken = JsonToken.VALUE_STRING;
                        } else {
                            JsonToken t;
                            switch (i) {
                                case R.styleable.ChartTheme_sc_seriesLineColor4 /*45*/:
                                    t = _parseNegNumber();
                                    break;
                                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4 /*48*/:
                                case R.styleable.ChartTheme_sc_seriesLineColor5 /*49*/:
                                case 50:
                                case R.styleable.ChartTheme_sc_seriesAreaGradientColor5 /*51*/:
                                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor5 /*52*/:
                                case R.styleable.ChartTheme_sc_seriesLineColor6 /*53*/:
                                case R.styleable.ChartTheme_sc_seriesAreaColor6 /*54*/:
                                case R.styleable.ChartTheme_sc_seriesAreaGradientColor6 /*55*/:
                                case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor6 /*56*/:
                                case R.styleable.ChartTheme_sc_pieDonutFlavorColor1 /*57*/:
                                    t = _parsePosNumber(i);
                                    break;
                                case 91:
                                    t = JsonToken.START_ARRAY;
                                    break;
                                case 102:
                                    _matchFalse();
                                    t = JsonToken.VALUE_FALSE;
                                    break;
                                case 110:
                                    _matchNull();
                                    t = JsonToken.VALUE_NULL;
                                    break;
                                case 116:
                                    _matchTrue();
                                    t = JsonToken.VALUE_TRUE;
                                    break;
                                case 123:
                                    t = JsonToken.START_OBJECT;
                                    break;
                                default:
                                    t = _handleOddValue(i);
                                    break;
                            }
                            this._nextToken = t;
                        }
                    } else {
                        _updateLocation();
                        _nextTokenNotInObject(i);
                    }
                }
            }
        }
        return str;
    }

    private final void _isNextTokenNameYes(int i) throws IOException {
        this._currToken = JsonToken.FIELD_NAME;
        _updateLocation();
        switch (i) {
            case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return;
            case R.styleable.ChartTheme_sc_seriesLineColor4 /*45*/:
                this._nextToken = _parseNegNumber();
                return;
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4 /*48*/:
            case R.styleable.ChartTheme_sc_seriesLineColor5 /*49*/:
            case 50:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor5 /*51*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor5 /*52*/:
            case R.styleable.ChartTheme_sc_seriesLineColor6 /*53*/:
            case R.styleable.ChartTheme_sc_seriesAreaColor6 /*54*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor6 /*55*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor6 /*56*/:
            case R.styleable.ChartTheme_sc_pieDonutFlavorColor1 /*57*/:
                this._nextToken = _parsePosNumber(i);
                return;
            case 91:
                this._nextToken = JsonToken.START_ARRAY;
                return;
            case 102:
                _matchToken("false", 1);
                this._nextToken = JsonToken.VALUE_FALSE;
                return;
            case 110:
                _matchToken("null", 1);
                this._nextToken = JsonToken.VALUE_NULL;
                return;
            case 116:
                _matchToken("true", 1);
                this._nextToken = JsonToken.VALUE_TRUE;
                return;
            case 123:
                this._nextToken = JsonToken.START_OBJECT;
                return;
            default:
                this._nextToken = _handleOddValue(i);
                return;
        }
    }

    protected boolean _isNextTokenNameMaybe(int i, String nameToMatch) throws IOException {
        String name = i == 34 ? _parseName() : _handleOddName(i);
        this._parsingContext.setCurrentName(name);
        this._currToken = JsonToken.FIELD_NAME;
        i = _skipColon();
        _updateLocation();
        if (i == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return nameToMatch.equals(name);
        }
        JsonToken t;
        switch (i) {
            case R.styleable.ChartTheme_sc_seriesLineColor4 /*45*/:
                t = _parseNegNumber();
                break;
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4 /*48*/:
            case R.styleable.ChartTheme_sc_seriesLineColor5 /*49*/:
            case 50:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor5 /*51*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor5 /*52*/:
            case R.styleable.ChartTheme_sc_seriesLineColor6 /*53*/:
            case R.styleable.ChartTheme_sc_seriesAreaColor6 /*54*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor6 /*55*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor6 /*56*/:
            case R.styleable.ChartTheme_sc_pieDonutFlavorColor1 /*57*/:
                t = _parsePosNumber(i);
                break;
            case 91:
                t = JsonToken.START_ARRAY;
                break;
            case 102:
                _matchFalse();
                t = JsonToken.VALUE_FALSE;
                break;
            case 110:
                _matchNull();
                t = JsonToken.VALUE_NULL;
                break;
            case 116:
                _matchTrue();
                t = JsonToken.VALUE_TRUE;
                break;
            case 123:
                t = JsonToken.START_OBJECT;
                break;
            default:
                t = _handleOddValue(i);
                break;
        }
        this._nextToken = t;
        return nameToMatch.equals(name);
    }

    private final JsonToken _nextTokenNotInObject(int i) throws IOException {
        if (i == 34) {
            this._tokenIncomplete = true;
            JsonToken jsonToken = JsonToken.VALUE_STRING;
            this._currToken = jsonToken;
            return jsonToken;
        }
        switch (i) {
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor3 /*44*/:
            case 93:
                if (isEnabled(Feature.ALLOW_MISSING_VALUES)) {
                    this._inputPtr--;
                    jsonToken = JsonToken.VALUE_NULL;
                    this._currToken = jsonToken;
                    return jsonToken;
                }
                break;
            case R.styleable.ChartTheme_sc_seriesLineColor4 /*45*/:
                jsonToken = _parseNegNumber();
                this._currToken = jsonToken;
                return jsonToken;
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor4 /*48*/:
            case R.styleable.ChartTheme_sc_seriesLineColor5 /*49*/:
            case 50:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor5 /*51*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor5 /*52*/:
            case R.styleable.ChartTheme_sc_seriesLineColor6 /*53*/:
            case R.styleable.ChartTheme_sc_seriesAreaColor6 /*54*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor6 /*55*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor6 /*56*/:
            case R.styleable.ChartTheme_sc_pieDonutFlavorColor1 /*57*/:
                jsonToken = _parsePosNumber(i);
                this._currToken = jsonToken;
                return jsonToken;
            case 91:
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                jsonToken = JsonToken.START_ARRAY;
                this._currToken = jsonToken;
                return jsonToken;
            case 102:
                _matchToken("false", 1);
                jsonToken = JsonToken.VALUE_FALSE;
                this._currToken = jsonToken;
                return jsonToken;
            case 110:
                _matchToken("null", 1);
                jsonToken = JsonToken.VALUE_NULL;
                this._currToken = jsonToken;
                return jsonToken;
            case 116:
                _matchToken("true", 1);
                jsonToken = JsonToken.VALUE_TRUE;
                this._currToken = jsonToken;
                return jsonToken;
            case 123:
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                jsonToken = JsonToken.START_OBJECT;
                this._currToken = jsonToken;
                return jsonToken;
        }
        jsonToken = _handleOddValue(i);
        this._currToken = jsonToken;
        return jsonToken;
    }

    public final String nextTextValue() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_STRING) {
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    _finishString();
                }
                return this._textBuffer.contentsAsString();
            } else if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            } else if (t != JsonToken.START_OBJECT) {
                return null;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
        } else if (nextToken() == JsonToken.VALUE_STRING) {
            return getText();
        } else {
            return null;
        }
    }

    public final int nextIntValue(int defaultValue) throws IOException {
        if (this._currToken != JsonToken.FIELD_NAME) {
            return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : defaultValue;
        } else {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return getIntValue();
            }
            if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return defaultValue;
            } else if (t != JsonToken.START_OBJECT) {
                return defaultValue;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return defaultValue;
            }
        }
    }

    public final long nextLongValue(long defaultValue) throws IOException {
        if (this._currToken != JsonToken.FIELD_NAME) {
            return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : defaultValue;
        } else {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_NUMBER_INT) {
                return getLongValue();
            }
            if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return defaultValue;
            } else if (t != JsonToken.START_OBJECT) {
                return defaultValue;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return defaultValue;
            }
        }
    }

    public final Boolean nextBooleanValue() throws IOException {
        JsonToken t;
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (t == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            } else if (t != JsonToken.START_OBJECT) {
                return null;
            } else {
                this._parsingContext = this._parsingContext.createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
        }
        t = nextToken();
        if (t == null) {
            return null;
        }
        int id = t.id();
        if (id == 9) {
            return Boolean.TRUE;
        }
        if (id == 10) {
            return Boolean.FALSE;
        }
        return null;
    }

    protected final JsonToken _parsePosNumber(int ch) throws IOException {
        int ptr = this._inputPtr;
        int startPtr = ptr - 1;
        int inputLen = this._inputEnd;
        if (ch == 48) {
            return _parseNumber2(false, startPtr);
        }
        int intLen = 1;
        int ptr2 = ptr;
        while (ptr2 < inputLen) {
            ptr = ptr2 + 1;
            ch = this._inputBuffer[ptr2];
            if (ch >= 48 && ch <= 57) {
                intLen++;
                ptr2 = ptr;
            } else if (ch == 46 || ch == 101 || ch == 69) {
                this._inputPtr = ptr;
                return _parseFloat(ch, startPtr, ptr, false, intLen);
            } else {
                ptr--;
                this._inputPtr = ptr;
                if (this._parsingContext.inRoot()) {
                    _verifyRootSpace(ch);
                }
                this._textBuffer.resetWithShared(this._inputBuffer, startPtr, ptr - startPtr);
                return resetInt(false, intLen);
            }
        }
        this._inputPtr = startPtr;
        ptr = ptr2;
        return _parseNumber2(false, startPtr);
    }

    private final JsonToken _parseFloat(int ch, int startPtr, int ptr, boolean neg, int intLen) throws IOException {
        int ptr2;
        int inputLen = this._inputEnd;
        int fractLen = 0;
        if (ch == 46) {
            ptr2 = ptr;
            while (ptr2 < inputLen) {
                ptr = ptr2 + 1;
                ch = this._inputBuffer[ptr2];
                if (ch >= 48 && ch <= 57) {
                    fractLen++;
                    ptr2 = ptr;
                } else if (fractLen == 0) {
                    reportUnexpectedNumberChar(ch, "Decimal point not followed by a digit");
                }
            }
            ptr = ptr2;
            return _parseNumber2(neg, startPtr);
        }
        ptr2 = ptr;
        int expLen = 0;
        if (ch == 101 || ch == 69) {
            if (ptr2 >= inputLen) {
                this._inputPtr = startPtr;
                ptr = ptr2;
                return _parseNumber2(neg, startPtr);
            }
            ptr = ptr2 + 1;
            ch = this._inputBuffer[ptr2];
            if (ch != 45 && ch != 43) {
                ptr2 = ptr;
            } else if (ptr >= inputLen) {
                this._inputPtr = startPtr;
                return _parseNumber2(neg, startPtr);
            } else {
                ptr2 = ptr + 1;
                ch = this._inputBuffer[ptr];
            }
            while (ch <= 57 && ch >= 48) {
                expLen++;
                if (ptr2 >= inputLen) {
                    this._inputPtr = startPtr;
                    ptr = ptr2;
                    return _parseNumber2(neg, startPtr);
                }
                ptr = ptr2 + 1;
                ch = this._inputBuffer[ptr2];
                ptr2 = ptr;
            }
            if (expLen == 0) {
                reportUnexpectedNumberChar(ch, "Exponent indicator not followed by a digit");
            }
        }
        ptr = ptr2 - 1;
        this._inputPtr = ptr;
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace(ch);
        }
        this._textBuffer.resetWithShared(this._inputBuffer, startPtr, ptr - startPtr);
        return resetFloat(neg, intLen, fractLen, expLen);
    }

    protected final JsonToken _parseNegNumber() throws IOException {
        int ptr = this._inputPtr;
        int startPtr = ptr - 1;
        int inputLen = this._inputEnd;
        if (ptr >= inputLen) {
            return _parseNumber2(true, startPtr);
        }
        int ptr2 = ptr + 1;
        int ch = this._inputBuffer[ptr];
        if (ch > 57 || ch < 48) {
            this._inputPtr = ptr2;
            ptr = ptr2;
            return _handleInvalidNumberStart(ch, true);
        } else if (ch == 48) {
            ptr = ptr2;
            return _parseNumber2(true, startPtr);
        } else {
            int intLen = 1;
            while (ptr2 < inputLen) {
                ptr = ptr2 + 1;
                ch = this._inputBuffer[ptr2];
                if (ch >= 48 && ch <= 57) {
                    intLen++;
                    ptr2 = ptr;
                } else if (ch == 46 || ch == 101 || ch == 69) {
                    this._inputPtr = ptr;
                    return _parseFloat(ch, startPtr, ptr, true, intLen);
                } else {
                    ptr--;
                    this._inputPtr = ptr;
                    if (this._parsingContext.inRoot()) {
                        _verifyRootSpace(ch);
                    }
                    this._textBuffer.resetWithShared(this._inputBuffer, startPtr, ptr - startPtr);
                    return resetInt(true, intLen);
                }
            }
            ptr = ptr2;
            return _parseNumber2(true, startPtr);
        }
    }

    private final com.fasterxml.jackson.core.JsonToken _parseNumber2(boolean r13, int r14) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:36)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:60)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r12 = this;
        if (r13 == 0) goto L_0x0004;
    L_0x0002:
        r14 = r14 + 1;
    L_0x0004:
        r12._inputPtr = r14;
        r9 = r12._textBuffer;
        r6 = r9.emptyAndGetCurrentSegment();
        r7 = 0;
        if (r13 == 0) goto L_0x0016;
    L_0x000f:
        r9 = 0;
        r7 = r7 + 1;
        r10 = 45;
        r6[r9] = r10;
    L_0x0016:
        r5 = 0;
        r9 = r12._inputPtr;
        r10 = r12._inputEnd;
        if (r9 >= r10) goto L_0x005e;
    L_0x001d:
        r9 = r12._inputBuffer;
        r10 = r12._inputPtr;
        r11 = r10 + 1;
        r12._inputPtr = r11;
        r0 = r9[r10];
    L_0x0027:
        r9 = 48;
        if (r0 != r9) goto L_0x002f;
    L_0x002b:
        r0 = r12._verifyNoLeadingZeroes();
    L_0x002f:
        r2 = 0;
    L_0x0030:
        r9 = 48;
        if (r0 < r9) goto L_0x0057;
    L_0x0034:
        r9 = 57;
        if (r0 > r9) goto L_0x0057;
    L_0x0038:
        r5 = r5 + 1;
        r9 = r6.length;
        if (r7 < r9) goto L_0x0044;
    L_0x003d:
        r9 = r12._textBuffer;
        r6 = r9.finishCurrentSegment();
        r7 = 0;
    L_0x0044:
        r8 = r7 + 1;
        r6[r7] = r0;
        r9 = r12._inputPtr;
        r10 = r12._inputEnd;
        if (r9 < r10) goto L_0x0067;
    L_0x004e:
        r9 = r12._loadMore();
        if (r9 != 0) goto L_0x0067;
    L_0x0054:
        r0 = 0;
        r2 = 1;
        r7 = r8;
    L_0x0057:
        if (r5 != 0) goto L_0x0073;
    L_0x0059:
        r9 = r12._handleInvalidNumberStart(r0, r13);
    L_0x005d:
        return r9;
    L_0x005e:
        r9 = "No digit following minus sign";
        r10 = com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT;
        r0 = r12.getNextChar(r9, r10);
        goto L_0x0027;
    L_0x0067:
        r9 = r12._inputBuffer;
        r10 = r12._inputPtr;
        r11 = r10 + 1;
        r12._inputPtr = r11;
        r0 = r9[r10];
        r7 = r8;
        goto L_0x0030;
    L_0x0073:
        r4 = 0;
        r9 = 46;
        if (r0 != r9) goto L_0x009b;
    L_0x0078:
        r9 = r6.length;
        if (r7 < r9) goto L_0x0082;
    L_0x007b:
        r9 = r12._textBuffer;
        r6 = r9.finishCurrentSegment();
        r7 = 0;
    L_0x0082:
        r8 = r7 + 1;
        r6[r7] = r0;
        r7 = r8;
    L_0x0087:
        r9 = r12._inputPtr;
        r10 = r12._inputEnd;
        if (r9 < r10) goto L_0x0135;
    L_0x008d:
        r9 = r12._loadMore();
        if (r9 != 0) goto L_0x0135;
    L_0x0093:
        r2 = 1;
    L_0x0094:
        if (r4 != 0) goto L_0x009b;
    L_0x0096:
        r9 = "Decimal point not followed by a digit";
        r12.reportUnexpectedNumberChar(r0, r9);
    L_0x009b:
        r3 = 0;
        r9 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r0 == r9) goto L_0x00a4;
    L_0x00a0:
        r9 = 69;
        if (r0 != r9) goto L_0x0117;
    L_0x00a4:
        r9 = r6.length;
        if (r7 < r9) goto L_0x00ae;
    L_0x00a7:
        r9 = r12._textBuffer;
        r6 = r9.finishCurrentSegment();
        r7 = 0;
    L_0x00ae:
        r8 = r7 + 1;
        r6[r7] = r0;
        r9 = r12._inputPtr;
        r10 = r12._inputEnd;
        if (r9 >= r10) goto L_0x015a;
    L_0x00b8:
        r9 = r12._inputBuffer;
        r10 = r12._inputPtr;
        r11 = r10 + 1;
        r12._inputPtr = r11;
        r0 = r9[r10];
    L_0x00c2:
        r9 = 45;
        if (r0 == r9) goto L_0x00ca;
    L_0x00c6:
        r9 = 43;
        if (r0 != r9) goto L_0x017a;
    L_0x00ca:
        r9 = r6.length;
        if (r8 < r9) goto L_0x0177;
    L_0x00cd:
        r9 = r12._textBuffer;
        r6 = r9.finishCurrentSegment();
        r7 = 0;
    L_0x00d4:
        r8 = r7 + 1;
        r6[r7] = r0;
        r9 = r12._inputPtr;
        r10 = r12._inputEnd;
        if (r9 >= r10) goto L_0x0162;
    L_0x00de:
        r9 = r12._inputBuffer;
        r10 = r12._inputPtr;
        r11 = r10 + 1;
        r12._inputPtr = r11;
        r1 = r9[r10];
        r7 = r8;
    L_0x00e9:
        r0 = r1;
    L_0x00ea:
        r9 = 57;
        if (r0 > r9) goto L_0x0110;
    L_0x00ee:
        r9 = 48;
        if (r0 < r9) goto L_0x0110;
    L_0x00f2:
        r3 = r3 + 1;
        r9 = r6.length;
        if (r7 < r9) goto L_0x00fe;
    L_0x00f7:
        r9 = r12._textBuffer;
        r6 = r9.finishCurrentSegment();
        r7 = 0;
    L_0x00fe:
        r8 = r7 + 1;
        r6[r7] = r0;
        r9 = r12._inputPtr;
        r10 = r12._inputEnd;
        if (r9 < r10) goto L_0x016a;
    L_0x0108:
        r9 = r12._loadMore();
        if (r9 != 0) goto L_0x016a;
    L_0x010e:
        r2 = 1;
        r7 = r8;
    L_0x0110:
        if (r3 != 0) goto L_0x0117;
    L_0x0112:
        r9 = "Exponent indicator not followed by a digit";
        r12.reportUnexpectedNumberChar(r0, r9);
    L_0x0117:
        if (r2 != 0) goto L_0x012a;
    L_0x0119:
        r9 = r12._inputPtr;
        r9 = r9 + -1;
        r12._inputPtr = r9;
        r9 = r12._parsingContext;
        r9 = r9.inRoot();
        if (r9 == 0) goto L_0x012a;
    L_0x0127:
        r12._verifyRootSpace(r0);
    L_0x012a:
        r9 = r12._textBuffer;
        r9.setCurrentLength(r7);
        r9 = r12.reset(r13, r5, r4, r3);
        goto L_0x005d;
    L_0x0135:
        r9 = r12._inputBuffer;
        r10 = r12._inputPtr;
        r11 = r10 + 1;
        r12._inputPtr = r11;
        r0 = r9[r10];
        r9 = 48;
        if (r0 < r9) goto L_0x0094;
    L_0x0143:
        r9 = 57;
        if (r0 > r9) goto L_0x0094;
    L_0x0147:
        r4 = r4 + 1;
        r9 = r6.length;
        if (r7 < r9) goto L_0x0153;
    L_0x014c:
        r9 = r12._textBuffer;
        r6 = r9.finishCurrentSegment();
        r7 = 0;
    L_0x0153:
        r8 = r7 + 1;
        r6[r7] = r0;
        r7 = r8;
        goto L_0x0087;
    L_0x015a:
        r9 = "expected a digit for number exponent";
        r0 = r12.getNextChar(r9);
        goto L_0x00c2;
    L_0x0162:
        r9 = "expected a digit for number exponent";
        r1 = r12.getNextChar(r9);
        r7 = r8;
        goto L_0x00e9;
    L_0x016a:
        r9 = r12._inputBuffer;
        r10 = r12._inputPtr;
        r11 = r10 + 1;
        r12._inputPtr = r11;
        r1 = r9[r10];
        r7 = r8;
        goto L_0x00e9;
    L_0x0177:
        r7 = r8;
        goto L_0x00d4;
    L_0x017a:
        r7 = r8;
        goto L_0x00ea;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser._parseNumber2(boolean, int):com.fasterxml.jackson.core.JsonToken");
    }

    private final char _verifyNoLeadingZeroes() throws IOException {
        if (this._inputPtr < this._inputEnd) {
            char ch = this._inputBuffer[this._inputPtr];
            if (ch < '0' || ch > '9') {
                return '0';
            }
        }
        return _verifyNLZ2();
    }

    private char _verifyNLZ2() throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            return '0';
        }
        char ch = this._inputBuffer[this._inputPtr];
        if (ch < '0' || ch > '9') {
            return '0';
        }
        if (!isEnabled(Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
            reportInvalidNumber("Leading zeroes not allowed");
        }
        this._inputPtr++;
        if (ch != '0') {
            return ch;
        }
        do {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                return ch;
            }
            ch = this._inputBuffer[this._inputPtr];
            if (ch < '0' || ch > '9') {
                return '0';
            }
            this._inputPtr++;
        } while (ch == '0');
        return ch;
    }

    protected JsonToken _handleInvalidNumberStart(int ch, boolean negative) throws IOException {
        double d = Double.NEGATIVE_INFINITY;
        if (ch == 73) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_INT);
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            ch = cArr[i];
            String match;
            if (ch == 78) {
                match = negative ? "-INF" : "+INF";
                _matchToken(match, 3);
                if (isEnabled(Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    if (!negative) {
                        d = Double.POSITIVE_INFINITY;
                    }
                    return resetAsNaN(match, d);
                }
                _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            } else if (ch == 110) {
                match = negative ? "-Infinity" : "+Infinity";
                _matchToken(match, 3);
                if (isEnabled(Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                    if (!negative) {
                        d = Double.POSITIVE_INFINITY;
                    }
                    return resetAsNaN(match, d);
                }
                _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
            }
        }
        reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
        return null;
    }

    private final void _verifyRootSpace(int ch) throws IOException {
        this._inputPtr++;
        switch (ch) {
            case 9:
            case 32:
                return;
            case 10:
                this._currInputRow++;
                this._currInputRowStart = this._inputPtr;
                return;
            case 13:
                _skipCR();
                return;
            default:
                _reportMissingRootWS(ch);
                return;
        }
    }

    protected final String _parseName() throws IOException {
        int start;
        int ptr = this._inputPtr;
        int hash = this._hashSeed;
        int[] codes = _icLatin1;
        while (ptr < this._inputEnd) {
            int ch = this._inputBuffer[ptr];
            if (ch >= codes.length || codes[ch] == 0) {
                hash = (hash * 33) + ch;
                ptr++;
            } else {
                if (ch == 34) {
                    start = this._inputPtr;
                    this._inputPtr = ptr + 1;
                    return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                }
                start = this._inputPtr;
                this._inputPtr = ptr;
                return _parseName2(start, hash, 34);
            }
        }
        start = this._inputPtr;
        this._inputPtr = ptr;
        return _parseName2(start, hash, 34);
    }

    private String _parseName2(int startPtr, int hash, int endChar) throws IOException {
        this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
        char[] outBuf = this._textBuffer.getCurrentSegment();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            char i2 = c;
            if (c <= '\\') {
                if (i2 == '\\') {
                    c = _decodeEscaped();
                } else if (i2 <= endChar) {
                    if (i2 == endChar) {
                        this._textBuffer.setCurrentLength(outPtr);
                        TextBuffer tb = this._textBuffer;
                        return this._symbols.findSymbol(tb.getTextBuffer(), tb.getTextOffset(), tb.size(), hash);
                    } else if (i2 < ' ') {
                        _throwUnquotedSpace(i2, "name");
                    }
                }
            }
            hash = (hash * 33) + c;
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = c;
            if (outPtr2 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            } else {
                outPtr = outPtr2;
            }
        }
    }

    protected String _handleOddName(int i) throws IOException {
        if (i == 39 && isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
            return _parseAposName();
        }
        int start;
        if (!isEnabled(Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            _reportUnexpectedChar(i, "was expecting double-quote to start field name");
        }
        int[] codes = CharTypes.getInputCodeLatin1JsNames();
        int maxCode = codes.length;
        boolean firstOk = i < maxCode ? codes[i] == 0 : Character.isJavaIdentifierPart((char) i);
        if (!firstOk) {
            _reportUnexpectedChar(i, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int ptr = this._inputPtr;
        int hash = this._hashSeed;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            do {
                int ch = this._inputBuffer[ptr];
                if (ch < maxCode) {
                    if (codes[ch] != 0) {
                        start = this._inputPtr - 1;
                        this._inputPtr = ptr;
                        return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                    }
                } else if (!Character.isJavaIdentifierPart((char) ch)) {
                    start = this._inputPtr - 1;
                    this._inputPtr = ptr;
                    return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                }
                hash = (hash * 33) + ch;
                ptr++;
            } while (ptr < inputLen);
        }
        start = this._inputPtr - 1;
        this._inputPtr = ptr;
        return _handleOddName2(start, hash, codes);
    }

    protected String _parseAposName() throws IOException {
        int start;
        int ptr = this._inputPtr;
        int hash = this._hashSeed;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            int[] codes = _icLatin1;
            int maxCode = codes.length;
            do {
                int ch = this._inputBuffer[ptr];
                if (ch != 39) {
                    if (ch < maxCode && codes[ch] != 0) {
                        break;
                    }
                    hash = (hash * 33) + ch;
                    ptr++;
                } else {
                    start = this._inputPtr;
                    this._inputPtr = ptr + 1;
                    return this._symbols.findSymbol(this._inputBuffer, start, ptr - start, hash);
                }
            } while (ptr < inputLen);
        }
        start = this._inputPtr;
        this._inputPtr = ptr;
        return _parseName2(start, hash, 39);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected com.fasterxml.jackson.core.JsonToken _handleOddValue(int r5) throws java.io.IOException {
        /*
        r4 = this;
        r1 = 1;
        switch(r5) {
            case 39: goto L_0x0024;
            case 43: goto L_0x0082;
            case 44: goto L_0x0039;
            case 73: goto L_0x0066;
            case 78: goto L_0x004a;
            case 93: goto L_0x0031;
            default: goto L_0x0004;
        };
    L_0x0004:
        r0 = java.lang.Character.isJavaIdentifierStart(r5);
        if (r0 == 0) goto L_0x001d;
    L_0x000a:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = (char) r5;
        r0 = r0.append(r1);
        r0 = r0.toString();
        r1 = "('true', 'false' or 'null')";
        r4._reportInvalidToken(r0, r1);
    L_0x001d:
        r0 = "expected a valid value (number, String, array, object, 'true', 'false' or 'null')";
        r4._reportUnexpectedChar(r5, r0);
        r0 = 0;
    L_0x0023:
        return r0;
    L_0x0024:
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x0004;
    L_0x002c:
        r0 = r4._handleApos();
        goto L_0x0023;
    L_0x0031:
        r0 = r4._parsingContext;
        r0 = r0.inArray();
        if (r0 == 0) goto L_0x0004;
    L_0x0039:
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_MISSING_VALUES;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x0004;
    L_0x0041:
        r0 = r4._inputPtr;
        r0 = r0 + -1;
        r4._inputPtr = r0;
        r0 = com.fasterxml.jackson.core.JsonToken.VALUE_NULL;
        goto L_0x0023;
    L_0x004a:
        r0 = "NaN";
        r4._matchToken(r0, r1);
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x0060;
    L_0x0057:
        r0 = "NaN";
        r2 = 9221120237041090560; // 0x7ff8000000000000 float:0.0 double:NaN;
        r0 = r4.resetAsNaN(r0, r2);
        goto L_0x0023;
    L_0x0060:
        r0 = "Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow";
        r4._reportError(r0);
        goto L_0x0004;
    L_0x0066:
        r0 = "Infinity";
        r4._matchToken(r0, r1);
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x007c;
    L_0x0073:
        r0 = "Infinity";
        r2 = 9218868437227405312; // 0x7ff0000000000000 float:0.0 double:Infinity;
        r0 = r4.resetAsNaN(r0, r2);
        goto L_0x0023;
    L_0x007c:
        r0 = "Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow";
        r4._reportError(r0);
        goto L_0x0004;
    L_0x0082:
        r0 = r4._inputPtr;
        r1 = r4._inputEnd;
        if (r0 < r1) goto L_0x0093;
    L_0x0088:
        r0 = r4._loadMore();
        if (r0 != 0) goto L_0x0093;
    L_0x008e:
        r0 = com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT;
        r4._reportInvalidEOFInValue(r0);
    L_0x0093:
        r0 = r4._inputBuffer;
        r1 = r4._inputPtr;
        r2 = r1 + 1;
        r4._inputPtr = r2;
        r0 = r0[r1];
        r1 = 0;
        r0 = r4._handleInvalidNumberStart(r0, r1);
        goto L_0x0023;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.ReaderBasedJsonParser._handleOddValue(int):com.fasterxml.jackson.core.JsonToken");
    }

    protected JsonToken _handleApos() throws IOException {
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            char i2 = c;
            if (c <= '\\') {
                if (i2 == '\\') {
                    c = _decodeEscaped();
                } else if (i2 <= '\'') {
                    if (i2 == '\'') {
                        this._textBuffer.setCurrentLength(outPtr);
                        return JsonToken.VALUE_STRING;
                    } else if (i2 < ' ') {
                        _throwUnquotedSpace(i2, "string value");
                    }
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = c;
            outPtr = outPtr2;
        }
    }

    private String _handleOddName2(int startPtr, int hash, int[] codes) throws IOException {
        this._textBuffer.resetWithShared(this._inputBuffer, startPtr, this._inputPtr - startPtr);
        char[] outBuf = this._textBuffer.getCurrentSegment();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        char maxCode = codes.length;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                break;
            }
            char c = this._inputBuffer[this._inputPtr];
            char i = c;
            if (c > maxCode) {
                if (!Character.isJavaIdentifierPart(c)) {
                    break;
                }
            } else if (codes[i] != 0) {
                break;
            }
            this._inputPtr++;
            hash = (hash * 33) + i;
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = c;
            if (outPtr2 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            } else {
                outPtr = outPtr2;
            }
        }
        this._textBuffer.setCurrentLength(outPtr);
        TextBuffer tb = this._textBuffer;
        return this._symbols.findSymbol(tb.getTextBuffer(), tb.getTextOffset(), tb.size(), hash);
    }

    protected final void _finishString() throws IOException {
        int ptr = this._inputPtr;
        int inputLen = this._inputEnd;
        if (ptr < inputLen) {
            int[] codes = _icLatin1;
            int maxCode = codes.length;
            do {
                int ch = this._inputBuffer[ptr];
                if (ch >= maxCode || codes[ch] == 0) {
                    ptr++;
                } else if (ch == 34) {
                    this._textBuffer.resetWithShared(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
                    this._inputPtr = ptr + 1;
                    return;
                }
            } while (ptr < inputLen);
        }
        this._textBuffer.resetWithCopy(this._inputBuffer, this._inputPtr, ptr - this._inputPtr);
        this._inputPtr = ptr;
        _finishString2();
    }

    protected void _finishString2() throws IOException {
        char[] outBuf = this._textBuffer.getCurrentSegment();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        int[] codes = _icLatin1;
        char maxCode = codes.length;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = cArr[i];
            char i2 = c;
            if (c < maxCode && codes[i2] != 0) {
                if (i2 == '\"') {
                    this._textBuffer.setCurrentLength(outPtr);
                    return;
                } else if (i2 == '\\') {
                    c = _decodeEscaped();
                } else if (i2 < ' ') {
                    _throwUnquotedSpace(i2, "string value");
                }
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = c;
            outPtr = outPtr2;
        }
    }

    protected final void _skipString() throws IOException {
        this._tokenIncomplete = false;
        int inPtr = this._inputPtr;
        int inLen = this._inputEnd;
        char[] inBuf = this._inputBuffer;
        while (true) {
            if (inPtr >= inLen) {
                this._inputPtr = inPtr;
                if (!_loadMore()) {
                    _reportInvalidEOF(": was expecting closing quote for a string value", JsonToken.VALUE_STRING);
                }
                inPtr = this._inputPtr;
                inLen = this._inputEnd;
            }
            int inPtr2 = inPtr + 1;
            char c = inBuf[inPtr];
            char i = c;
            if (c <= '\\') {
                if (i == '\\') {
                    this._inputPtr = inPtr2;
                    _decodeEscaped();
                    inPtr = this._inputPtr;
                    inLen = this._inputEnd;
                } else if (i <= '\"') {
                    if (i == '\"') {
                        this._inputPtr = inPtr2;
                        return;
                    } else if (i < ' ') {
                        this._inputPtr = inPtr2;
                        _throwUnquotedSpace(i, "string value");
                    }
                }
            }
            inPtr = inPtr2;
        }
    }

    protected final void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || _loadMore()) && this._inputBuffer[this._inputPtr] == '\n') {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private final int _skipColon() throws IOException {
        if (this._inputPtr + 4 >= this._inputEnd) {
            return _skipColon2(false);
        }
        char c = this._inputBuffer[this._inputPtr];
        char[] cArr;
        int i;
        int i2;
        if (c == ':') {
            cArr = this._inputBuffer;
            i = this._inputPtr + 1;
            this._inputPtr = i;
            i2 = cArr[i];
            if (i2 <= 32) {
                if (i2 == 32 || i2 == 9) {
                    cArr = this._inputBuffer;
                    i = this._inputPtr + 1;
                    this._inputPtr = i;
                    i2 = cArr[i];
                    if (i2 > 32) {
                        if (i2 == 47 || i2 == 35) {
                            return _skipColon2(true);
                        }
                        this._inputPtr++;
                        return i2;
                    }
                }
                return _skipColon2(true);
            } else if (i2 == 47 || i2 == 35) {
                return _skipColon2(true);
            } else {
                this._inputPtr++;
                return i2;
            }
        }
        if (c == ' ' || c == '\t') {
            cArr = this._inputBuffer;
            i = this._inputPtr + 1;
            this._inputPtr = i;
            c = cArr[i];
        }
        if (c != ':') {
            return _skipColon2(false);
        }
        cArr = this._inputBuffer;
        i = this._inputPtr + 1;
        this._inputPtr = i;
        i2 = cArr[i];
        if (i2 <= 32) {
            if (i2 == 32 || i2 == 9) {
                cArr = this._inputBuffer;
                i = this._inputPtr + 1;
                this._inputPtr = i;
                i2 = cArr[i];
                if (i2 > 32) {
                    if (i2 == 47 || i2 == 35) {
                        return _skipColon2(true);
                    }
                    this._inputPtr++;
                    return i2;
                }
            }
            return _skipColon2(true);
        } else if (i2 == 47 || i2 == 35) {
            return _skipColon2(true);
        } else {
            this._inputPtr++;
            return i2;
        }
    }

    private final int _skipColon2(boolean gotColon) throws IOException {
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = cArr[i];
                if (i2 > 32) {
                    if (i2 == 47) {
                        _skipComment();
                    } else if (i2 != 35 || !_skipYAMLComment()) {
                        if (gotColon) {
                            return i2;
                        }
                        if (i2 != 58) {
                            _reportUnexpectedChar(i2, "was expecting a colon to separate field name and value");
                        }
                        gotColon = true;
                    }
                } else if (i2 < 32) {
                    if (i2 == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            } else {
                _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
                return -1;
            }
        }
    }

    private final int _skipColonFast(int ptr) throws IOException {
        boolean gotColon = true;
        int i = ptr + 1;
        int i2 = this._inputBuffer[ptr];
        if (i2 == 58) {
            ptr = i + 1;
            i2 = this._inputBuffer[i];
            if (i2 > 32) {
                if (!(i2 == 47 || i2 == 35)) {
                    this._inputPtr = ptr;
                    return i2;
                }
            } else if (i2 == 32 || i2 == 9) {
                i = ptr + 1;
                i2 = this._inputBuffer[ptr];
                if (i2 <= 32 || i2 == 47 || i2 == 35) {
                    ptr = i;
                } else {
                    this._inputPtr = i;
                    ptr = i;
                    return i2;
                }
            }
            this._inputPtr = ptr - 1;
            return _skipColon2(true);
        }
        if (i2 == 32 || i2 == 9) {
            ptr = i + 1;
            i2 = this._inputBuffer[i];
            i = ptr;
        }
        if (i2 != 58) {
            gotColon = false;
        }
        if (gotColon) {
            ptr = i + 1;
            i2 = this._inputBuffer[i];
            if (i2 > 32) {
                if (!(i2 == 47 || i2 == 35)) {
                    this._inputPtr = ptr;
                    return i2;
                }
            } else if (i2 == 32 || i2 == 9) {
                i = ptr + 1;
                i2 = this._inputBuffer[ptr];
                if (!(i2 <= 32 || i2 == 47 || i2 == 35)) {
                    this._inputPtr = i;
                    ptr = i;
                    return i2;
                }
            }
            this._inputPtr = ptr - 1;
            return _skipColon2(gotColon);
        }
        ptr = i;
        this._inputPtr = ptr - 1;
        return _skipColon2(gotColon);
    }

    private final int _skipComma(int i) throws IOException {
        if (i != 44) {
            _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
        }
        while (this._inputPtr < this._inputEnd) {
            char[] cArr = this._inputBuffer;
            int i2 = this._inputPtr;
            this._inputPtr = i2 + 1;
            i = cArr[i2];
            if (i > 32) {
                if (i != 47 && i != 35) {
                    return i;
                }
                this._inputPtr--;
                return _skipAfterComma2();
            } else if (i < 32) {
                if (i == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (i == 13) {
                    _skipCR();
                } else if (i != 9) {
                    _throwInvalidSpace(i);
                }
            }
        }
        return _skipAfterComma2();
    }

    private final int _skipAfterComma2() throws IOException {
        int i;
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                char[] cArr = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                i = cArr[i2];
                if (i > 32) {
                    if (i == 47) {
                        _skipComment();
                    } else if (i != 35 || !_skipYAMLComment()) {
                        return i;
                    }
                } else if (i < 32) {
                    if (i == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                    } else if (i == 13) {
                        _skipCR();
                    } else if (i != 9) {
                        _throwInvalidSpace(i);
                    }
                }
            } else {
                throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
            }
        }
        return i;
    }

    private final int _skipWSOrEnd() throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            return _eofAsNextChar();
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = cArr[i];
        if (i2 <= 32) {
            if (i2 != 32) {
                if (i2 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (i2 == 13) {
                    _skipCR();
                } else if (i2 != 9) {
                    _throwInvalidSpace(i2);
                }
            }
            while (this._inputPtr < this._inputEnd) {
                cArr = this._inputBuffer;
                i = this._inputPtr;
                this._inputPtr = i + 1;
                i2 = cArr[i];
                if (i2 > 32) {
                    if (i2 != 47 && i2 != 35) {
                        return i2;
                    }
                    this._inputPtr--;
                    return _skipWSOrEnd2();
                } else if (i2 != 32) {
                    if (i2 == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            }
            return _skipWSOrEnd2();
        } else if (i2 != 47 && i2 != 35) {
            return i2;
        } else {
            this._inputPtr--;
            return _skipWSOrEnd2();
        }
    }

    private int _skipWSOrEnd2() throws IOException {
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                return _eofAsNextChar();
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = cArr[i];
            if (i2 > 32) {
                if (i2 == 47) {
                    _skipComment();
                } else if (i2 != 35) {
                    return i2;
                } else {
                    if (!_skipYAMLComment()) {
                        return i2;
                    }
                }
            } else if (i2 != 32) {
                if (i2 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (i2 == 13) {
                    _skipCR();
                } else if (i2 != 9) {
                    _throwInvalidSpace(i2);
                }
            }
        }
    }

    private void _skipComment() throws IOException {
        if (!isEnabled(Feature.ALLOW_COMMENTS)) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            _reportInvalidEOF(" in a comment", null);
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        char c = cArr[i];
        if (c == '/') {
            _skipLine();
        } else if (c == '*') {
            _skipCComment();
        } else {
            _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    private void _skipCComment() throws IOException {
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                break;
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = cArr[i];
            if (i2 <= 42) {
                if (i2 == 42) {
                    if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                        break;
                    } else if (this._inputBuffer[this._inputPtr] == '/') {
                        this._inputPtr++;
                        return;
                    }
                } else if (i2 < 32) {
                    if (i2 == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            }
        }
        _reportInvalidEOF(" in a comment", null);
    }

    private boolean _skipYAMLComment() throws IOException {
        if (!isEnabled(Feature.ALLOW_YAML_COMMENTS)) {
            return false;
        }
        _skipLine();
        return true;
    }

    private void _skipLine() throws IOException {
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                char[] cArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = cArr[i];
                if (i2 < 32) {
                    if (i2 == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                        return;
                    } else if (i2 == 13) {
                        _skipCR();
                        return;
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            } else {
                return;
            }
        }
    }

    protected char _decodeEscaped() throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
        }
        char[] cArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        char c = cArr[i];
        switch (c) {
            case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
            case '\\':
                return c;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                int value = 0;
                for (int i2 = 0; i2 < 4; i2++) {
                    if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                        _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
                    }
                    cArr = this._inputBuffer;
                    i = this._inputPtr;
                    this._inputPtr = i + 1;
                    int ch = cArr[i];
                    int digit = CharTypes.charToHex(ch);
                    if (digit < 0) {
                        _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
                    }
                    value = (value << 4) | digit;
                }
                return (char) value;
            default:
                return _handleUnrecognizedCharacterEscape(c);
        }
    }

    private final void _matchTrue() throws IOException {
        int ptr = this._inputPtr;
        if (ptr + 3 < this._inputEnd) {
            char[] b = this._inputBuffer;
            if (b[ptr] == 'r') {
                ptr++;
                if (b[ptr] == 'u') {
                    ptr++;
                    if (b[ptr] == 'e') {
                        ptr++;
                        char c = b[ptr];
                        if (c < '0' || c == ']' || c == '}') {
                            this._inputPtr = ptr;
                            return;
                        }
                    }
                }
            }
        }
        _matchToken("true", 1);
    }

    private final void _matchFalse() throws IOException {
        int ptr = this._inputPtr;
        if (ptr + 4 < this._inputEnd) {
            char[] b = this._inputBuffer;
            if (b[ptr] == 'a') {
                ptr++;
                if (b[ptr] == 'l') {
                    ptr++;
                    if (b[ptr] == 's') {
                        ptr++;
                        if (b[ptr] == 'e') {
                            ptr++;
                            char c = b[ptr];
                            if (c < '0' || c == ']' || c == '}') {
                                this._inputPtr = ptr;
                                return;
                            }
                        }
                    }
                }
            }
        }
        _matchToken("false", 1);
    }

    private final void _matchNull() throws IOException {
        int ptr = this._inputPtr;
        if (ptr + 3 < this._inputEnd) {
            char[] b = this._inputBuffer;
            if (b[ptr] == 'u') {
                ptr++;
                if (b[ptr] == 'l') {
                    ptr++;
                    if (b[ptr] == 'l') {
                        ptr++;
                        char c = b[ptr];
                        if (c < '0' || c == ']' || c == '}') {
                            this._inputPtr = ptr;
                            return;
                        }
                    }
                }
            }
        }
        _matchToken("null", 1);
    }

    protected final void _matchToken(String matchStr, int i) throws IOException {
        int len = matchStr.length();
        do {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidToken(matchStr.substring(0, i));
            }
            if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
                _reportInvalidToken(matchStr.substring(0, i));
            }
            this._inputPtr++;
            i++;
        } while (i < len);
        if (this._inputPtr < this._inputEnd || _loadMore()) {
            char c = this._inputBuffer[this._inputPtr];
            if (c >= '0' && c != ']' && c != '}' && Character.isJavaIdentifierPart(c)) {
                _reportInvalidToken(matchStr.substring(0, i));
            }
        }
    }

    protected byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
        ByteArrayBuilder builder = _getByteArrayBuilder();
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                _loadMoreGuaranteed();
            }
            char[] cArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char ch = cArr[i];
            if (ch > ' ') {
                int bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (ch == '\"') {
                        return builder.toByteArray();
                    }
                    bits = _decodeBase64Escape(b64variant, ch, 0);
                    if (bits < 0) {
                        continue;
                    }
                }
                int decodedData = bits;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                cArr = this._inputBuffer;
                i = this._inputPtr;
                this._inputPtr = i + 1;
                ch = cArr[i];
                bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    bits = _decodeBase64Escape(b64variant, ch, 1);
                }
                decodedData = (decodedData << 6) | bits;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                cArr = this._inputBuffer;
                i = this._inputPtr;
                this._inputPtr = i + 1;
                ch = cArr[i];
                bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (bits != -2) {
                        if (ch != '\"' || b64variant.usesPadding()) {
                            bits = _decodeBase64Escape(b64variant, ch, 2);
                        } else {
                            builder.append(decodedData >> 4);
                            return builder.toByteArray();
                        }
                    }
                    if (bits == -2) {
                        if (this._inputPtr >= this._inputEnd) {
                            _loadMoreGuaranteed();
                        }
                        cArr = this._inputBuffer;
                        i = this._inputPtr;
                        this._inputPtr = i + 1;
                        ch = cArr[i];
                        if (b64variant.usesPaddingChar(ch)) {
                            builder.append(decodedData >> 4);
                        } else {
                            throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                        }
                    }
                }
                decodedData = (decodedData << 6) | bits;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                cArr = this._inputBuffer;
                i = this._inputPtr;
                this._inputPtr = i + 1;
                ch = cArr[i];
                bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (bits != -2) {
                        if (ch != '\"' || b64variant.usesPadding()) {
                            bits = _decodeBase64Escape(b64variant, ch, 3);
                        } else {
                            builder.appendTwoBytes(decodedData >> 2);
                            return builder.toByteArray();
                        }
                    }
                    if (bits == -2) {
                        builder.appendTwoBytes(decodedData >> 2);
                    }
                }
                builder.appendThreeBytes((decodedData << 6) | bits);
            }
        }
    }

    public JsonLocation getTokenLocation() {
        Object src = this._ioContext.getSourceReference();
        if (this._currToken == JsonToken.FIELD_NAME) {
            return new JsonLocation(src, -1, this._currInputProcessed + (this._nameStartOffset - 1), this._nameStartRow, this._nameStartCol);
        }
        return new JsonLocation(src, -1, this._tokenInputTotal - 1, this._tokenInputRow, this._tokenInputCol);
    }

    public JsonLocation getCurrentLocation() {
        return new JsonLocation(this._ioContext.getSourceReference(), -1, this._currInputProcessed + ((long) this._inputPtr), this._currInputRow, (this._inputPtr - this._currInputRowStart) + 1);
    }

    private final void _updateLocation() {
        int ptr = this._inputPtr;
        this._tokenInputTotal = this._currInputProcessed + ((long) ptr);
        this._tokenInputRow = this._currInputRow;
        this._tokenInputCol = ptr - this._currInputRowStart;
    }

    private final void _updateNameLocation() {
        int ptr = this._inputPtr;
        this._nameStartOffset = (long) ptr;
        this._nameStartRow = this._currInputRow;
        this._nameStartCol = ptr - this._currInputRowStart;
    }

    protected void _reportInvalidToken(String matchedPart) throws IOException {
        _reportInvalidToken(matchedPart, "'null', 'true', 'false' or NaN");
    }

    protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
        StringBuilder sb = new StringBuilder(matchedPart);
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                break;
            }
            char c = this._inputBuffer[this._inputPtr];
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            this._inputPtr++;
            sb.append(c);
        }
        _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
    }
}
