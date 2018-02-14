package com.fasterxml.jackson.core.json;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.shinobicontrols.charts.R;
import java.io.DataInput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;

public class UTF8DataInputJsonParser extends ParserBase {
    protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
    private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
    protected DataInput _inputData;
    protected int _nextByte = -1;
    protected ObjectCodec _objectCodec;
    private int _quad1;
    protected int[] _quadBuffer = new int[16];
    protected final ByteQuadsCanonicalizer _symbols;
    protected boolean _tokenIncomplete;

    public UTF8DataInputJsonParser(IOContext ctxt, int features, DataInput inputData, ObjectCodec codec, ByteQuadsCanonicalizer sym, int firstByte) {
        super(ctxt, features);
        this._objectCodec = codec;
        this._symbols = sym;
        this._inputData = inputData;
        this._nextByte = firstByte;
    }

    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    public void setCodec(ObjectCodec c) {
        this._objectCodec = c;
    }

    public int releaseBuffered(OutputStream out) throws IOException {
        return 0;
    }

    public Object getInputSource() {
        return this._inputData;
    }

    protected void _closeInput() throws IOException {
    }

    protected void _releaseBuffers() throws IOException {
        super._releaseBuffers();
        this._symbols.release();
    }

    public String getText() throws IOException {
        if (this._currToken != JsonToken.VALUE_STRING) {
            return _getText2(this._currToken);
        }
        if (!this._tokenIncomplete) {
            return this._textBuffer.contentsAsString();
        }
        this._tokenIncomplete = false;
        return _finishAndReturnString();
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

    public String getValueAsString() throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (!this._tokenIncomplete) {
                return this._textBuffer.contentsAsString();
            }
            this._tokenIncomplete = false;
            return _finishAndReturnString();
        } else if (this._currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        } else {
            return super.getValueAsString(null);
        }
    }

    public String getValueAsString(String defValue) throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (!this._tokenIncomplete) {
                return this._textBuffer.contentsAsString();
            }
            this._tokenIncomplete = false;
            return _finishAndReturnString();
        } else if (this._currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        } else {
            return super.getValueAsString(defValue);
        }
    }

    public int getValueAsInt() throws IOException {
        JsonToken t = this._currToken;
        if (t != JsonToken.VALUE_NUMBER_INT && t != JsonToken.VALUE_NUMBER_FLOAT) {
            return super.getValueAsInt(0);
        }
        if ((this._numTypesValid & 1) == 0) {
            if (this._numTypesValid == 0) {
                return _parseIntValue();
            }
            if ((this._numTypesValid & 1) == 0) {
                convertNumberToInt();
            }
        }
        return this._numberInt;
    }

    public int getValueAsInt(int defValue) throws IOException {
        JsonToken t = this._currToken;
        if (t != JsonToken.VALUE_NUMBER_INT && t != JsonToken.VALUE_NUMBER_FLOAT) {
            return super.getValueAsInt(defValue);
        }
        if ((this._numTypesValid & 1) == 0) {
            if (this._numTypesValid == 0) {
                return _parseIntValue();
            }
            if ((this._numTypesValid & 1) == 0) {
                convertNumberToInt();
            }
        }
        return this._numberInt;
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

    public char[] getTextCharacters() throws IOException {
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

    public int getTextLength() throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.size();
        } else if (this._currToken == JsonToken.FIELD_NAME) {
            return this._parsingContext.getCurrentName().length();
        } else {
            if (this._currToken == null) {
                return 0;
            }
            if (this._currToken.isNumeric()) {
                return this._textBuffer.size();
            }
            return this._currToken.asCharArray().length;
        }
    }

    public int getTextOffset() throws IOException {
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
    protected int _readBinary(com.fasterxml.jackson.core.Base64Variant r13, java.io.OutputStream r14, byte[] r15) throws java.io.IOException {
        /*
        r12 = this;
        r11 = 3;
        r10 = 34;
        r9 = 0;
        r8 = -2;
        r5 = 0;
        r7 = r15.length;
        r4 = r7 + -3;
        r3 = 0;
    L_0x000a:
        r7 = r12._inputData;
        r1 = r7.readUnsignedByte();
        r7 = 32;
        if (r1 <= r7) goto L_0x000a;
    L_0x0014:
        r0 = r13.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x0022;
    L_0x001a:
        if (r1 == r10) goto L_0x005d;
    L_0x001c:
        r0 = r12._decodeBase64Escape(r13, r1, r9);
        if (r0 < 0) goto L_0x000a;
    L_0x0022:
        if (r5 <= r4) goto L_0x0029;
    L_0x0024:
        r3 = r3 + r5;
        r14.write(r15, r9, r5);
        r5 = 0;
    L_0x0029:
        r2 = r0;
        r7 = r12._inputData;
        r1 = r7.readUnsignedByte();
        r0 = r13.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x003b;
    L_0x0036:
        r7 = 1;
        r0 = r12._decodeBase64Escape(r13, r1, r7);
    L_0x003b:
        r7 = r2 << 6;
        r2 = r7 | r0;
        r7 = r12._inputData;
        r1 = r7.readUnsignedByte();
        r0 = r13.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x00a1;
    L_0x004b:
        if (r0 == r8) goto L_0x006b;
    L_0x004d:
        if (r1 != r10) goto L_0x0066;
    L_0x004f:
        r7 = r13.usesPadding();
        if (r7 != 0) goto L_0x0066;
    L_0x0055:
        r2 = r2 >> 4;
        r6 = r5 + 1;
        r7 = (byte) r2;
        r15[r5] = r7;
        r5 = r6;
    L_0x005d:
        r12._tokenIncomplete = r9;
        if (r5 <= 0) goto L_0x0065;
    L_0x0061:
        r3 = r3 + r5;
        r14.write(r15, r9, r5);
    L_0x0065:
        return r3;
    L_0x0066:
        r7 = 2;
        r0 = r12._decodeBase64Escape(r13, r1, r7);
    L_0x006b:
        if (r0 != r8) goto L_0x00a1;
    L_0x006d:
        r7 = r12._inputData;
        r1 = r7.readUnsignedByte();
        r7 = r13.usesPaddingChar(r1);
        if (r7 != 0) goto L_0x0097;
    L_0x0079:
        r7 = new java.lang.StringBuilder;
        r8 = "expected padding character '";
        r7.<init>(r8);
        r8 = r13.getPaddingChar();
        r7 = r7.append(r8);
        r8 = "'";
        r7 = r7.append(r8);
        r7 = r7.toString();
        r7 = r12.reportInvalidBase64Char(r13, r1, r11, r7);
        throw r7;
    L_0x0097:
        r2 = r2 >> 4;
        r6 = r5 + 1;
        r7 = (byte) r2;
        r15[r5] = r7;
        r5 = r6;
        goto L_0x000a;
    L_0x00a1:
        r7 = r2 << 6;
        r2 = r7 | r0;
        r7 = r12._inputData;
        r1 = r7.readUnsignedByte();
        r0 = r13.decodeBase64Char(r1);
        if (r0 >= 0) goto L_0x00e0;
    L_0x00b1:
        if (r0 == r8) goto L_0x00ce;
    L_0x00b3:
        if (r1 != r10) goto L_0x00ca;
    L_0x00b5:
        r7 = r13.usesPadding();
        if (r7 != 0) goto L_0x00ca;
    L_0x00bb:
        r2 = r2 >> 2;
        r6 = r5 + 1;
        r7 = r2 >> 8;
        r7 = (byte) r7;
        r15[r5] = r7;
        r5 = r6 + 1;
        r7 = (byte) r2;
        r15[r6] = r7;
        goto L_0x005d;
    L_0x00ca:
        r0 = r12._decodeBase64Escape(r13, r1, r11);
    L_0x00ce:
        if (r0 != r8) goto L_0x00e0;
    L_0x00d0:
        r2 = r2 >> 2;
        r6 = r5 + 1;
        r7 = r2 >> 8;
        r7 = (byte) r7;
        r15[r5] = r7;
        r5 = r6 + 1;
        r7 = (byte) r2;
        r15[r6] = r7;
        goto L_0x000a;
    L_0x00e0:
        r7 = r2 << 6;
        r2 = r7 | r0;
        r6 = r5 + 1;
        r7 = r2 >> 16;
        r7 = (byte) r7;
        r15[r5] = r7;
        r5 = r6 + 1;
        r7 = r2 >> 8;
        r7 = (byte) r7;
        r15[r6] = r7;
        r6 = r5 + 1;
        r7 = (byte) r2;
        r15[r5] = r7;
        r5 = r6;
        goto L_0x000a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8DataInputJsonParser._readBinary(com.fasterxml.jackson.core.Base64Variant, java.io.OutputStream, byte[]):int");
    }

    public JsonToken nextToken() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            return _nextAfterName();
        }
        this._numTypesValid = 0;
        if (this._tokenIncomplete) {
            _skipString();
        }
        int i = _skipWS();
        this._binaryValue = null;
        this._tokenInputRow = this._currInputRow;
        JsonToken jsonToken;
        if (i == 93) {
            if (!this._parsingContext.inArray()) {
                _reportMismatchedEndMarker(i, '}');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            jsonToken = JsonToken.END_ARRAY;
            this._currToken = jsonToken;
            return jsonToken;
        } else if (i == 125) {
            if (!this._parsingContext.inObject()) {
                _reportMismatchedEndMarker(i, ']');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            jsonToken = JsonToken.END_OBJECT;
            this._currToken = jsonToken;
            return jsonToken;
        } else {
            if (this._parsingContext.expectComma()) {
                if (i != 44) {
                    _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
                }
                i = _skipWS();
            }
            if (!this._parsingContext.inObject()) {
                return _nextTokenNotInObject(i);
            }
            this._parsingContext.setCurrentName(_parseName(i));
            this._currToken = JsonToken.FIELD_NAME;
            i = _skipColon();
            if (i == 34) {
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return this._currToken;
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
                    _matchToken("false", 1);
                    t = JsonToken.VALUE_FALSE;
                    break;
                case 110:
                    _matchToken("null", 1);
                    t = JsonToken.VALUE_NULL;
                    break;
                case 116:
                    _matchToken("true", 1);
                    t = JsonToken.VALUE_TRUE;
                    break;
                case 123:
                    t = JsonToken.START_OBJECT;
                    break;
                default:
                    t = _handleUnexpectedValue(i);
                    break;
            }
            this._nextToken = t;
            return this._currToken;
        }
    }

    private final JsonToken _nextTokenNotInObject(int i) throws IOException {
        if (i == 34) {
            this._tokenIncomplete = true;
            JsonToken jsonToken = JsonToken.VALUE_STRING;
            this._currToken = jsonToken;
            return jsonToken;
        }
        switch (i) {
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
            default:
                jsonToken = _handleUnexpectedValue(i);
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

    public String nextFieldName() throws IOException {
        String str = null;
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            _nextAfterName();
        } else {
            if (this._tokenIncomplete) {
                _skipString();
            }
            int i = _skipWS();
            this._binaryValue = null;
            this._tokenInputRow = this._currInputRow;
            if (i == 93) {
                if (!this._parsingContext.inArray()) {
                    _reportMismatchedEndMarker(i, '}');
                }
                this._parsingContext = this._parsingContext.clearAndGetParent();
                this._currToken = JsonToken.END_ARRAY;
            } else if (i == 125) {
                if (!this._parsingContext.inObject()) {
                    _reportMismatchedEndMarker(i, ']');
                }
                this._parsingContext = this._parsingContext.clearAndGetParent();
                this._currToken = JsonToken.END_OBJECT;
            } else {
                if (this._parsingContext.expectComma()) {
                    if (i != 44) {
                        _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
                    }
                    i = _skipWS();
                }
                if (this._parsingContext.inObject()) {
                    str = _parseName(i);
                    this._parsingContext.setCurrentName(str);
                    this._currToken = JsonToken.FIELD_NAME;
                    i = _skipColon();
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
                                _matchToken("false", 1);
                                t = JsonToken.VALUE_FALSE;
                                break;
                            case 110:
                                _matchToken("null", 1);
                                t = JsonToken.VALUE_NULL;
                                break;
                            case 116:
                                _matchToken("true", 1);
                                t = JsonToken.VALUE_TRUE;
                                break;
                            case 123:
                                t = JsonToken.START_OBJECT;
                                break;
                            default:
                                t = _handleUnexpectedValue(i);
                                break;
                        }
                        this._nextToken = t;
                    }
                } else {
                    _nextTokenNotInObject(i);
                }
            }
        }
        return str;
    }

    public String nextTextValue() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_STRING) {
                if (!this._tokenIncomplete) {
                    return this._textBuffer.contentsAsString();
                }
                this._tokenIncomplete = false;
                return _finishAndReturnString();
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

    public int nextIntValue(int defaultValue) throws IOException {
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

    public long nextLongValue(long defaultValue) throws IOException {
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

    public Boolean nextBooleanValue() throws IOException {
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
        if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
        }
        return null;
    }

    protected JsonToken _parsePosNumber(int c) throws IOException {
        int outPtr;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        if (c == 48) {
            c = _handleLeadingZeroes();
            if (c > 57 || c < 48) {
                outBuf[0] = '0';
                outPtr = 1;
            } else {
                outPtr = 0;
            }
        } else {
            outBuf[0] = (char) c;
            c = this._inputData.readUnsignedByte();
            outPtr = 1;
        }
        int intLen = outPtr;
        while (c <= 57 && c >= 48) {
            intLen++;
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            c = this._inputData.readUnsignedByte();
            outPtr = outPtr2;
        }
        if (c == 46 || c == 101 || c == 69) {
            return _parseFloat(outBuf, outPtr, c, false, intLen);
        }
        this._textBuffer.setCurrentLength(outPtr);
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace();
        } else {
            this._nextByte = c;
        }
        return resetInt(false, intLen);
    }

    protected JsonToken _parseNegNumber() throws IOException {
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0 + 1;
        outBuf[0] = '-';
        int c = this._inputData.readUnsignedByte();
        outPtr++;
        outBuf[1] = (char) c;
        if (c <= 48) {
            if (c != 48) {
                return _handleInvalidNumberStart(c, true);
            }
            c = _handleLeadingZeroes();
        } else if (c > 57) {
            return _handleInvalidNumberStart(c, true);
        } else {
            c = this._inputData.readUnsignedByte();
        }
        int intLen = 1;
        while (c <= 57 && c >= 48) {
            intLen++;
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            c = this._inputData.readUnsignedByte();
            outPtr = outPtr2;
        }
        if (c == 46 || c == 101 || c == 69) {
            return _parseFloat(outBuf, outPtr, c, true, intLen);
        }
        this._textBuffer.setCurrentLength(outPtr);
        this._nextByte = c;
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace();
        }
        return resetInt(true, intLen);
    }

    private final int _handleLeadingZeroes() throws IOException {
        int ch = this._inputData.readUnsignedByte();
        if (ch < 48 || ch > 57) {
            return ch;
        }
        if (!isEnabled(Feature.ALLOW_NUMERIC_LEADING_ZEROS)) {
            reportInvalidNumber("Leading zeroes not allowed");
        }
        while (ch == 48) {
            ch = this._inputData.readUnsignedByte();
        }
        return ch;
    }

    private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
        int outPtr2;
        int fractLen = 0;
        if (c == 46) {
            outPtr2 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            outPtr = outPtr2;
            while (true) {
                c = this._inputData.readUnsignedByte();
                if (c >= 48 && c <= 57) {
                    fractLen++;
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                    }
                    outPtr2 = outPtr + 1;
                    outBuf[outPtr] = (char) c;
                    outPtr = outPtr2;
                } else if (fractLen == 0) {
                    reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
                }
            }
            if (fractLen == 0) {
                reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
            }
        }
        int expLen = 0;
        if (c == 101 || c == 69) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            outPtr2 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            c = this._inputData.readUnsignedByte();
            if (c == 45 || c == 43) {
                if (outPtr2 >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                } else {
                    outPtr = outPtr2;
                }
                outPtr2 = outPtr + 1;
                outBuf[outPtr] = (char) c;
                c = this._inputData.readUnsignedByte();
                outPtr = outPtr2;
            } else {
                outPtr = outPtr2;
            }
            while (c <= 57 && c >= 48) {
                expLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                outPtr2 = outPtr + 1;
                outBuf[outPtr] = (char) c;
                c = this._inputData.readUnsignedByte();
                outPtr = outPtr2;
            }
            if (expLen == 0) {
                reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
            }
        }
        this._nextByte = c;
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace();
        }
        this._textBuffer.setCurrentLength(outPtr);
        return resetFloat(negative, integerPartLength, fractLen, expLen);
    }

    private final void _verifyRootSpace() throws IOException {
        int ch = this._nextByte;
        if (ch <= 32) {
            this._nextByte = -1;
            if (ch == 13 || ch == 10) {
                this._currInputRow++;
                return;
            }
            return;
        }
        _reportMissingRootWS(ch);
    }

    protected final String _parseName(int i) throws IOException {
        if (i != 34) {
            return _handleOddName(i);
        }
        int[] codes = _icLatin1;
        int q = this._inputData.readUnsignedByte();
        if (codes[q] == 0) {
            i = this._inputData.readUnsignedByte();
            if (codes[i] == 0) {
                q = (q << 8) | i;
                i = this._inputData.readUnsignedByte();
                if (codes[i] == 0) {
                    q = (q << 8) | i;
                    i = this._inputData.readUnsignedByte();
                    if (codes[i] == 0) {
                        q = (q << 8) | i;
                        i = this._inputData.readUnsignedByte();
                        if (codes[i] == 0) {
                            this._quad1 = q;
                            return _parseMediumName(i);
                        } else if (i == 34) {
                            return findName(q, 4);
                        } else {
                            return parseName(q, i, 4);
                        }
                    } else if (i == 34) {
                        return findName(q, 3);
                    } else {
                        return parseName(q, i, 3);
                    }
                } else if (i == 34) {
                    return findName(q, 2);
                } else {
                    return parseName(q, i, 2);
                }
            } else if (i == 34) {
                return findName(q, 1);
            } else {
                return parseName(q, i, 1);
            }
        } else if (q == 34) {
            return "";
        } else {
            return parseName(0, q, 0);
        }
    }

    private final String _parseMediumName(int q2) throws IOException {
        int[] codes = _icLatin1;
        int i = this._inputData.readUnsignedByte();
        if (codes[i] == 0) {
            q2 = (q2 << 8) | i;
            i = this._inputData.readUnsignedByte();
            if (codes[i] == 0) {
                q2 = (q2 << 8) | i;
                i = this._inputData.readUnsignedByte();
                if (codes[i] == 0) {
                    q2 = (q2 << 8) | i;
                    i = this._inputData.readUnsignedByte();
                    if (codes[i] == 0) {
                        return _parseMediumName2(i, q2);
                    }
                    if (i == 34) {
                        return findName(this._quad1, q2, 4);
                    }
                    return parseName(this._quad1, q2, i, 4);
                } else if (i == 34) {
                    return findName(this._quad1, q2, 3);
                } else {
                    return parseName(this._quad1, q2, i, 3);
                }
            } else if (i == 34) {
                return findName(this._quad1, q2, 2);
            } else {
                return parseName(this._quad1, q2, i, 2);
            }
        } else if (i == 34) {
            return findName(this._quad1, q2, 1);
        } else {
            return parseName(this._quad1, q2, i, 1);
        }
    }

    private final String _parseMediumName2(int q3, int q2) throws IOException {
        int[] codes = _icLatin1;
        int i = this._inputData.readUnsignedByte();
        if (codes[i] == 0) {
            q3 = (q3 << 8) | i;
            i = this._inputData.readUnsignedByte();
            if (codes[i] == 0) {
                q3 = (q3 << 8) | i;
                i = this._inputData.readUnsignedByte();
                if (codes[i] == 0) {
                    q3 = (q3 << 8) | i;
                    i = this._inputData.readUnsignedByte();
                    if (codes[i] == 0) {
                        return _parseLongName(i, q2, q3);
                    }
                    if (i == 34) {
                        return findName(this._quad1, q2, q3, 4);
                    }
                    return parseName(this._quad1, q2, q3, i, 4);
                } else if (i == 34) {
                    return findName(this._quad1, q2, q3, 3);
                } else {
                    return parseName(this._quad1, q2, q3, i, 3);
                }
            } else if (i == 34) {
                return findName(this._quad1, q2, q3, 2);
            } else {
                return parseName(this._quad1, q2, q3, i, 2);
            }
        } else if (i == 34) {
            return findName(this._quad1, q2, q3, 1);
        } else {
            return parseName(this._quad1, q2, q3, i, 1);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final java.lang.String _parseLongName(int r13, int r14, int r15) throws java.io.IOException {
        /*
        r12 = this;
        r11 = 4;
        r10 = 3;
        r9 = 2;
        r5 = 1;
        r8 = 34;
        r0 = r12._quadBuffer;
        r1 = 0;
        r3 = r12._quad1;
        r0[r1] = r3;
        r0 = r12._quadBuffer;
        r0[r5] = r14;
        r0 = r12._quadBuffer;
        r0[r9] = r15;
        r6 = _icLatin1;
        r2 = 3;
    L_0x0018:
        r0 = r12._inputData;
        r4 = r0.readUnsignedByte();
        r0 = r6[r4];
        if (r0 == 0) goto L_0x0034;
    L_0x0022:
        if (r4 != r8) goto L_0x002b;
    L_0x0024:
        r0 = r12._quadBuffer;
        r0 = r12.findName(r0, r2, r13, r5);
    L_0x002a:
        return r0;
    L_0x002b:
        r1 = r12._quadBuffer;
        r0 = r12;
        r3 = r13;
        r0 = r0.parseEscapedName(r1, r2, r3, r4, r5);
        goto L_0x002a;
    L_0x0034:
        r0 = r13 << 8;
        r13 = r0 | r4;
        r0 = r12._inputData;
        r4 = r0.readUnsignedByte();
        r0 = r6[r4];
        if (r0 == 0) goto L_0x0055;
    L_0x0042:
        if (r4 != r8) goto L_0x004b;
    L_0x0044:
        r0 = r12._quadBuffer;
        r0 = r12.findName(r0, r2, r13, r9);
        goto L_0x002a;
    L_0x004b:
        r1 = r12._quadBuffer;
        r0 = r12;
        r3 = r13;
        r5 = r9;
        r0 = r0.parseEscapedName(r1, r2, r3, r4, r5);
        goto L_0x002a;
    L_0x0055:
        r0 = r13 << 8;
        r13 = r0 | r4;
        r0 = r12._inputData;
        r4 = r0.readUnsignedByte();
        r0 = r6[r4];
        if (r0 == 0) goto L_0x0076;
    L_0x0063:
        if (r4 != r8) goto L_0x006c;
    L_0x0065:
        r0 = r12._quadBuffer;
        r0 = r12.findName(r0, r2, r13, r10);
        goto L_0x002a;
    L_0x006c:
        r1 = r12._quadBuffer;
        r0 = r12;
        r3 = r13;
        r5 = r10;
        r0 = r0.parseEscapedName(r1, r2, r3, r4, r5);
        goto L_0x002a;
    L_0x0076:
        r0 = r13 << 8;
        r13 = r0 | r4;
        r0 = r12._inputData;
        r4 = r0.readUnsignedByte();
        r0 = r6[r4];
        if (r0 == 0) goto L_0x0097;
    L_0x0084:
        if (r4 != r8) goto L_0x008d;
    L_0x0086:
        r0 = r12._quadBuffer;
        r0 = r12.findName(r0, r2, r13, r11);
        goto L_0x002a;
    L_0x008d:
        r1 = r12._quadBuffer;
        r0 = r12;
        r3 = r13;
        r5 = r11;
        r0 = r0.parseEscapedName(r1, r2, r3, r4, r5);
        goto L_0x002a;
    L_0x0097:
        r0 = r12._quadBuffer;
        r0 = r0.length;
        if (r2 < r0) goto L_0x00a4;
    L_0x009c:
        r0 = r12._quadBuffer;
        r0 = _growArrayBy(r0, r2);
        r12._quadBuffer = r0;
    L_0x00a4:
        r0 = r12._quadBuffer;
        r7 = r2 + 1;
        r0[r2] = r13;
        r13 = r4;
        r2 = r7;
        goto L_0x0018;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8DataInputJsonParser._parseLongName(int, int, int):java.lang.String");
    }

    private final String parseName(int q1, int ch, int lastQuadBytes) throws IOException {
        return parseEscapedName(this._quadBuffer, 0, q1, ch, lastQuadBytes);
    }

    private final String parseName(int q1, int q2, int ch, int lastQuadBytes) throws IOException {
        this._quadBuffer[0] = q1;
        return parseEscapedName(this._quadBuffer, 1, q2, ch, lastQuadBytes);
    }

    private final String parseName(int q1, int q2, int q3, int ch, int lastQuadBytes) throws IOException {
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q2;
        return parseEscapedName(this._quadBuffer, 2, q3, ch, lastQuadBytes);
    }

    protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch, int currQuadBytes) throws IOException {
        int[] codes = _icLatin1;
        while (true) {
            int qlen2;
            if (codes[ch] != 0) {
                if (ch == 34) {
                    break;
                }
                if (ch != 92) {
                    _throwUnquotedSpace(ch, "name");
                } else {
                    ch = _decodeEscaped();
                }
                if (ch > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            quads = _growArrayBy(quads, quads.length);
                            this._quadBuffer = quads;
                        }
                        qlen2 = qlen + 1;
                        quads[qlen] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    } else {
                        qlen2 = qlen;
                    }
                    if (ch < ItemAnimator.FLAG_MOVED) {
                        currQuad = (currQuad << 8) | ((ch >> 6) | 192);
                        currQuadBytes++;
                        qlen = qlen2;
                    } else {
                        currQuad = (currQuad << 8) | ((ch >> 12) | 224);
                        currQuadBytes++;
                        if (currQuadBytes >= 4) {
                            if (qlen2 >= quads.length) {
                                quads = _growArrayBy(quads, quads.length);
                                this._quadBuffer = quads;
                            }
                            qlen = qlen2 + 1;
                            quads[qlen2] = currQuad;
                            currQuad = 0;
                            currQuadBytes = 0;
                        } else {
                            qlen = qlen2;
                        }
                        currQuad = (currQuad << 8) | (((ch >> 6) & 63) | 128);
                        currQuadBytes++;
                    }
                    ch = (ch & 63) | 128;
                    qlen2 = qlen;
                    if (currQuadBytes >= 4) {
                        currQuadBytes++;
                        currQuad = (currQuad << 8) | ch;
                        qlen = qlen2;
                    } else {
                        if (qlen2 >= quads.length) {
                            quads = _growArrayBy(quads, quads.length);
                            this._quadBuffer = quads;
                        }
                        qlen = qlen2 + 1;
                        quads[qlen2] = currQuad;
                        currQuad = ch;
                        currQuadBytes = 1;
                    }
                    ch = this._inputData.readUnsignedByte();
                }
            }
            qlen2 = qlen;
            if (currQuadBytes >= 4) {
                if (qlen2 >= quads.length) {
                    quads = _growArrayBy(quads, quads.length);
                    this._quadBuffer = quads;
                }
                qlen = qlen2 + 1;
                quads[qlen2] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            } else {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch;
                qlen = qlen2;
            }
            ch = this._inputData.readUnsignedByte();
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                quads = _growArrayBy(quads, quads.length);
                this._quadBuffer = quads;
            }
            qlen2 = qlen + 1;
            quads[qlen] = pad(currQuad, currQuadBytes);
            qlen = qlen2;
        }
        String name = this._symbols.findName(quads, qlen);
        if (name == null) {
            return addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    protected String _handleOddName(int ch) throws IOException {
        if (ch == 39 && isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
            return _parseAposName();
        }
        int qlen;
        if (!isEnabled(Feature.ALLOW_UNQUOTED_FIELD_NAMES)) {
            _reportUnexpectedChar((char) _decodeCharForError(ch), "was expecting double-quote to start field name");
        }
        int[] codes = CharTypes.getInputCodeUtf8JsNames();
        if (codes[ch] != 0) {
            _reportUnexpectedChar(ch, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int[] quads = this._quadBuffer;
        int currQuad = 0;
        int currQuadBytes = 0;
        int qlen2 = 0;
        while (true) {
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch;
                qlen = qlen2;
            } else {
                if (qlen2 >= quads.length) {
                    quads = _growArrayBy(quads, quads.length);
                    this._quadBuffer = quads;
                }
                qlen = qlen2 + 1;
                quads[qlen2] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            ch = this._inputData.readUnsignedByte();
            if (codes[ch] != 0) {
                break;
            }
            qlen2 = qlen;
        }
        this._nextByte = ch;
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                quads = _growArrayBy(quads, quads.length);
                this._quadBuffer = quads;
            }
            qlen2 = qlen + 1;
            quads[qlen] = currQuad;
            qlen = qlen2;
        }
        String name = this._symbols.findName(quads, qlen);
        if (name == null) {
            return addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    protected String _parseAposName() throws IOException {
        int ch = this._inputData.readUnsignedByte();
        if (ch == 39) {
            return "";
        }
        int qlen;
        int[] quads = this._quadBuffer;
        int currQuad = 0;
        int currQuadBytes = 0;
        int[] codes = _icLatin1;
        int qlen2 = 0;
        while (ch != 39) {
            if (!(ch == 34 || codes[ch] == 0)) {
                if (ch != 92) {
                    _throwUnquotedSpace(ch, "name");
                } else {
                    ch = _decodeEscaped();
                }
                if (ch > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen2 >= quads.length) {
                            quads = _growArrayBy(quads, quads.length);
                            this._quadBuffer = quads;
                        }
                        qlen = qlen2 + 1;
                        quads[qlen2] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                        qlen2 = qlen;
                    }
                    if (ch < ItemAnimator.FLAG_MOVED) {
                        currQuad = (currQuad << 8) | ((ch >> 6) | 192);
                        currQuadBytes++;
                        qlen = qlen2;
                    } else {
                        currQuad = (currQuad << 8) | ((ch >> 12) | 224);
                        currQuadBytes++;
                        if (currQuadBytes >= 4) {
                            if (qlen2 >= quads.length) {
                                quads = _growArrayBy(quads, quads.length);
                                this._quadBuffer = quads;
                            }
                            qlen = qlen2 + 1;
                            quads[qlen2] = currQuad;
                            currQuad = 0;
                            currQuadBytes = 0;
                        } else {
                            qlen = qlen2;
                        }
                        currQuad = (currQuad << 8) | (((ch >> 6) & 63) | 128);
                        currQuadBytes++;
                    }
                    ch = (ch & 63) | 128;
                    qlen2 = qlen;
                }
            }
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch;
                qlen = qlen2;
            } else {
                if (qlen2 >= quads.length) {
                    quads = _growArrayBy(quads, quads.length);
                    this._quadBuffer = quads;
                }
                qlen = qlen2 + 1;
                quads[qlen2] = currQuad;
                currQuad = ch;
                currQuadBytes = 1;
            }
            ch = this._inputData.readUnsignedByte();
            qlen2 = qlen;
        }
        if (currQuadBytes > 0) {
            if (qlen2 >= quads.length) {
                quads = _growArrayBy(quads, quads.length);
                this._quadBuffer = quads;
            }
            qlen = qlen2 + 1;
            quads[qlen2] = pad(currQuad, currQuadBytes);
        } else {
            qlen = qlen2;
        }
        String name = this._symbols.findName(quads, qlen);
        if (name == null) {
            return addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    private final String findName(int q1, int lastQuadBytes) throws JsonParseException {
        q1 = pad(q1, lastQuadBytes);
        String name = this._symbols.findName(q1);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q1;
        return addName(this._quadBuffer, 1, lastQuadBytes);
    }

    private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException {
        q2 = pad(q2, lastQuadBytes);
        String name = this._symbols.findName(q1, q2);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q2;
        return addName(this._quadBuffer, 2, lastQuadBytes);
    }

    private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException {
        q3 = pad(q3, lastQuadBytes);
        String name = this._symbols.findName(q1, q2, q3);
        if (name != null) {
            return name;
        }
        int[] quads = this._quadBuffer;
        quads[0] = q1;
        quads[1] = q2;
        quads[2] = pad(q3, lastQuadBytes);
        return addName(quads, 3, lastQuadBytes);
    }

    private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException {
        if (qlen >= quads.length) {
            quads = _growArrayBy(quads, quads.length);
            this._quadBuffer = quads;
        }
        int qlen2 = qlen + 1;
        quads[qlen] = pad(lastQuad, lastQuadBytes);
        String name = this._symbols.findName(quads, qlen2);
        if (name == null) {
            return addName(quads, qlen2, lastQuadBytes);
        }
        return name;
    }

    private final String addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException {
        int lastQuad;
        int byteLen = ((qlen << 2) - 4) + lastQuadBytes;
        if (lastQuadBytes < 4) {
            lastQuad = quads[qlen - 1];
            quads[qlen - 1] = lastQuad << ((4 - lastQuadBytes) << 3);
        } else {
            lastQuad = 0;
        }
        char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
        int ix = 0;
        int cix = 0;
        while (ix < byteLen) {
            int i;
            int i2 = (quads[ix >> 2] >> ((3 - (ix & 3)) << 3)) & 255;
            ix++;
            if (i2 > 127) {
                int needed;
                if ((i2 & 224) == 192) {
                    i2 &= 31;
                    needed = 1;
                } else if ((i2 & 240) == 224) {
                    i2 &= 15;
                    needed = 2;
                } else if ((i2 & 248) == 240) {
                    i2 &= 7;
                    needed = 3;
                } else {
                    _reportInvalidInitial(i2);
                    i2 = 1;
                    needed = 1;
                }
                if (ix + needed > byteLen) {
                    _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
                }
                int ch2 = quads[ix >> 2] >> ((3 - (ix & 3)) << 3);
                ix++;
                if ((ch2 & 192) != 128) {
                    _reportInvalidOther(ch2);
                }
                i2 = (i2 << 6) | (ch2 & 63);
                if (needed > 1) {
                    ch2 = quads[ix >> 2] >> ((3 - (ix & 3)) << 3);
                    ix++;
                    if ((ch2 & 192) != 128) {
                        _reportInvalidOther(ch2);
                    }
                    i2 = (i2 << 6) | (ch2 & 63);
                    if (needed > 2) {
                        ch2 = quads[ix >> 2] >> ((3 - (ix & 3)) << 3);
                        ix++;
                        if ((ch2 & 192) != 128) {
                            _reportInvalidOther(ch2 & 255);
                        }
                        i2 = (i2 << 6) | (ch2 & 63);
                    }
                }
                if (needed > 2) {
                    i2 -= 65536;
                    if (cix >= cbuf.length) {
                        cbuf = this._textBuffer.expandCurrentSegment();
                    }
                    i = cix + 1;
                    cbuf[cix] = (char) (55296 + (i2 >> 10));
                    i2 = 56320 | (i2 & 1023);
                    if (i >= cbuf.length) {
                        cbuf = this._textBuffer.expandCurrentSegment();
                    }
                    cix = i + 1;
                    cbuf[i] = (char) i2;
                }
            }
            i = cix;
            if (i >= cbuf.length) {
                cbuf = this._textBuffer.expandCurrentSegment();
            }
            cix = i + 1;
            cbuf[i] = (char) i2;
        }
        String baseName = new String(cbuf, 0, cix);
        if (lastQuadBytes < 4) {
            quads[qlen - 1] = lastQuad;
        }
        return this._symbols.addName(baseName, quads, qlen);
    }

    protected void _finishString() throws IOException {
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int outEnd = outBuf.length;
        while (true) {
            int c = this._inputData.readUnsignedByte();
            if (codes[c] != 0) {
                break;
            }
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            if (outPtr2 >= outEnd) {
                _finishString2(outBuf, outPtr2, this._inputData.readUnsignedByte());
                outPtr = outPtr2;
                return;
            }
            outPtr = outPtr2;
        }
        if (c == 34) {
            this._textBuffer.setCurrentLength(outPtr);
        } else {
            _finishString2(outBuf, outPtr, c);
        }
    }

    private String _finishAndReturnString() throws IOException {
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int outEnd = outBuf.length;
        while (true) {
            int c = this._inputData.readUnsignedByte();
            if (codes[c] != 0) {
                break;
            }
            int outPtr2 = outPtr + 1;
            outBuf[outPtr] = (char) c;
            if (outPtr2 >= outEnd) {
                _finishString2(outBuf, outPtr2, this._inputData.readUnsignedByte());
                outPtr = outPtr2;
                return this._textBuffer.contentsAsString();
            }
            outPtr = outPtr2;
        }
        if (c == 34) {
            return this._textBuffer.setCurrentAndReturn(outPtr);
        }
        _finishString2(outBuf, outPtr, c);
        return this._textBuffer.contentsAsString();
    }

    private final void _finishString2(char[] outBuf, int outPtr, int c) throws IOException {
        int[] codes = _icUTF8;
        int outEnd = outBuf.length;
        while (true) {
            int outPtr2;
            if (codes[c] == 0) {
                if (outPtr >= outEnd) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                    outEnd = outBuf.length;
                }
                outPtr2 = outPtr + 1;
                outBuf[outPtr] = (char) c;
                c = this._inputData.readUnsignedByte();
                outPtr = outPtr2;
            } else if (c != 34) {
                switch (codes[c]) {
                    case 1:
                        c = _decodeEscaped();
                        break;
                    case 2:
                        c = _decodeUtf8_2(c);
                        break;
                    case 3:
                        c = _decodeUtf8_3(c);
                        break;
                    case 4:
                        c = _decodeUtf8_4(c);
                        outPtr2 = outPtr + 1;
                        outBuf[outPtr] = (char) (55296 | (c >> 10));
                        if (outPtr2 >= outBuf.length) {
                            outBuf = this._textBuffer.finishCurrentSegment();
                            outPtr = 0;
                            outEnd = outBuf.length;
                        } else {
                            outPtr = outPtr2;
                        }
                        c = 56320 | (c & 1023);
                        break;
                    default:
                        if (c >= 32) {
                            _reportInvalidChar(c);
                            break;
                        } else {
                            _throwUnquotedSpace(c, "string value");
                            break;
                        }
                }
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                    outEnd = outBuf.length;
                }
                outPtr2 = outPtr + 1;
                outBuf[outPtr] = (char) c;
                c = this._inputData.readUnsignedByte();
                outPtr = outPtr2;
            } else {
                this._textBuffer.setCurrentLength(outPtr);
                return;
            }
        }
    }

    protected void _skipString() throws IOException {
        this._tokenIncomplete = false;
        int[] codes = _icUTF8;
        while (true) {
            int c = this._inputData.readUnsignedByte();
            if (codes[c] != 0) {
                if (c != 34) {
                    switch (codes[c]) {
                        case 1:
                            _decodeEscaped();
                            break;
                        case 2:
                            _skipUtf8_2();
                            break;
                        case 3:
                            _skipUtf8_3();
                            break;
                        case 4:
                            _skipUtf8_4();
                            break;
                        default:
                            if (c >= 32) {
                                _reportInvalidChar(c);
                                break;
                            } else {
                                _throwUnquotedSpace(c, "string value");
                                break;
                            }
                    }
                }
                return;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected com.fasterxml.jackson.core.JsonToken _handleUnexpectedValue(int r5) throws java.io.IOException {
        /*
        r4 = this;
        r1 = 1;
        switch(r5) {
            case 39: goto L_0x003e;
            case 43: goto L_0x0083;
            case 44: goto L_0x002c;
            case 73: goto L_0x0067;
            case 78: goto L_0x004b;
            case 93: goto L_0x0024;
            case 125: goto L_0x0039;
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
        r4._reportInvalidToken(r5, r0, r1);
    L_0x001d:
        r0 = "expected a valid value (number, String, array, object, 'true', 'false' or 'null')";
        r4._reportUnexpectedChar(r5, r0);
        r0 = 0;
    L_0x0023:
        return r0;
    L_0x0024:
        r0 = r4._parsingContext;
        r0 = r0.inArray();
        if (r0 == 0) goto L_0x0004;
    L_0x002c:
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_MISSING_VALUES;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x0039;
    L_0x0034:
        r4._nextByte = r5;
        r0 = com.fasterxml.jackson.core.JsonToken.VALUE_NULL;
        goto L_0x0023;
    L_0x0039:
        r0 = "expected a value";
        r4._reportUnexpectedChar(r5, r0);
    L_0x003e:
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x0004;
    L_0x0046:
        r0 = r4._handleApos();
        goto L_0x0023;
    L_0x004b:
        r0 = "NaN";
        r4._matchToken(r0, r1);
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x0061;
    L_0x0058:
        r0 = "NaN";
        r2 = 9221120237041090560; // 0x7ff8000000000000 float:0.0 double:NaN;
        r0 = r4.resetAsNaN(r0, r2);
        goto L_0x0023;
    L_0x0061:
        r0 = "Non-standard token 'NaN': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow";
        r4._reportError(r0);
        goto L_0x0004;
    L_0x0067:
        r0 = "Infinity";
        r4._matchToken(r0, r1);
        r0 = com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS;
        r0 = r4.isEnabled(r0);
        if (r0 == 0) goto L_0x007d;
    L_0x0074:
        r0 = "Infinity";
        r2 = 9218868437227405312; // 0x7ff0000000000000 float:0.0 double:Infinity;
        r0 = r4.resetAsNaN(r0, r2);
        goto L_0x0023;
    L_0x007d:
        r0 = "Non-standard token 'Infinity': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow";
        r4._reportError(r0);
        goto L_0x0004;
    L_0x0083:
        r0 = r4._inputData;
        r0 = r0.readUnsignedByte();
        r1 = 0;
        r0 = r4._handleInvalidNumberStart(r0, r1);
        goto L_0x0023;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8DataInputJsonParser._handleUnexpectedValue(int):com.fasterxml.jackson.core.JsonToken");
    }

    protected JsonToken _handleApos() throws IOException {
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        while (true) {
            int outEnd = outBuf.length;
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
                outEnd = outBuf.length;
            }
            while (true) {
                int c = this._inputData.readUnsignedByte();
                if (c == 39) {
                    this._textBuffer.setCurrentLength(outPtr);
                    return JsonToken.VALUE_STRING;
                } else if (codes[c] == 0) {
                    outPtr = outPtr + 1;
                    outBuf[outPtr] = (char) c;
                    if (outPtr >= outEnd) {
                        outPtr = outPtr;
                    } else {
                        outPtr = outPtr;
                    }
                } else {
                    switch (codes[c]) {
                        case 1:
                            c = _decodeEscaped();
                            break;
                        case 2:
                            c = _decodeUtf8_2(c);
                            break;
                        case 3:
                            c = _decodeUtf8_3(c);
                            break;
                        case 4:
                            c = _decodeUtf8_4(c);
                            outPtr = outPtr + 1;
                            outBuf[outPtr] = (char) (55296 | (c >> 10));
                            if (outPtr >= outBuf.length) {
                                outBuf = this._textBuffer.finishCurrentSegment();
                                outPtr = 0;
                            } else {
                                outPtr = outPtr;
                            }
                            c = 56320 | (c & 1023);
                            break;
                        default:
                            if (c < 32) {
                                _throwUnquotedSpace(c, "string value");
                            }
                            _reportInvalidChar(c);
                            break;
                    }
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                    }
                    outPtr = outPtr + 1;
                    outBuf[outPtr] = (char) c;
                    outPtr = outPtr;
                }
            }
        }
    }

    protected JsonToken _handleInvalidNumberStart(int ch, boolean neg) throws IOException {
        while (ch == 73) {
            String match;
            ch = this._inputData.readUnsignedByte();
            if (ch != 78) {
                if (ch != 110) {
                    break;
                }
                match = neg ? "-Infinity" : "+Infinity";
            } else {
                match = neg ? "-INF" : "+INF";
            }
            _matchToken(match, 3);
            if (isEnabled(Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
                return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            }
            _reportError("Non-standard token '" + match + "': enable JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS to allow");
        }
        reportUnexpectedNumberChar(ch, "expected digit (0-9) to follow minus sign, for valid numeric value");
        return null;
    }

    protected final void _matchToken(String matchStr, int i) throws IOException {
        int len = matchStr.length();
        do {
            char ch = this._inputData.readUnsignedByte();
            if (ch != matchStr.charAt(i)) {
                _reportInvalidToken(ch, matchStr.substring(0, i));
            }
            i++;
        } while (i < len);
        int ch2 = this._inputData.readUnsignedByte();
        if (!(ch2 < 48 || ch2 == 93 || ch2 == 125)) {
            _checkMatchEnd(matchStr, i, ch2);
        }
        this._nextByte = ch2;
    }

    private final void _checkMatchEnd(String matchStr, int i, int ch) throws IOException {
        char c = (char) _decodeCharForError(ch);
        if (Character.isJavaIdentifierPart(c)) {
            _reportInvalidToken(c, matchStr.substring(0, i));
        }
    }

    private final int _skipWS() throws IOException {
        int i = this._nextByte;
        if (i < 0) {
            i = this._inputData.readUnsignedByte();
        } else {
            this._nextByte = -1;
        }
        while (i <= 32) {
            if (i == 13 || i == 10) {
                this._currInputRow++;
            }
            i = this._inputData.readUnsignedByte();
        }
        if (i == 47 || i == 35) {
            return _skipWSComment(i);
        }
        return i;
    }

    private final int _skipWSComment(int i) throws IOException {
        while (true) {
            if (i > 32) {
                if (i == 47) {
                    _skipComment();
                } else if (i != 35 || !_skipYAMLComment()) {
                    return i;
                }
            } else if (i == 13 || i == 10) {
                this._currInputRow++;
            }
            i = this._inputData.readUnsignedByte();
        }
        return i;
    }

    private final int _skipColon() throws IOException {
        int i = this._nextByte;
        if (i < 0) {
            i = this._inputData.readUnsignedByte();
        } else {
            this._nextByte = -1;
        }
        if (i == 58) {
            i = this._inputData.readUnsignedByte();
            if (i <= 32) {
                if (i == 32 || i == 9) {
                    i = this._inputData.readUnsignedByte();
                    if (i > 32) {
                        return (i == 47 || i == 35) ? _skipColon2(i, true) : i;
                    }
                }
                return _skipColon2(i, true);
            } else if (i == 47 || i == 35) {
                return _skipColon2(i, true);
            } else {
                return i;
            }
        }
        if (i == 32 || i == 9) {
            i = this._inputData.readUnsignedByte();
        }
        if (i != 58) {
            return _skipColon2(i, false);
        }
        i = this._inputData.readUnsignedByte();
        if (i > 32) {
            return (i == 47 || i == 35) ? _skipColon2(i, true) : i;
        } else {
            if (i == 32 || i == 9) {
                i = this._inputData.readUnsignedByte();
                if (i > 32) {
                    return (i == 47 || i == 35) ? _skipColon2(i, true) : i;
                }
            }
            return _skipColon2(i, true);
        }
    }

    private final int _skipColon2(int i, boolean gotColon) throws IOException {
        while (true) {
            if (i > 32) {
                if (i == 47) {
                    _skipComment();
                } else if (i != 35 || !_skipYAMLComment()) {
                    if (gotColon) {
                        return i;
                    }
                    if (i != 58) {
                        _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
                    }
                    gotColon = true;
                }
            } else if (i == 13 || i == 10) {
                this._currInputRow++;
            }
            i = this._inputData.readUnsignedByte();
        }
    }

    private final void _skipComment() throws IOException {
        if (!isEnabled(Feature.ALLOW_COMMENTS)) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        int c = this._inputData.readUnsignedByte();
        if (c == 47) {
            _skipLine();
        } else if (c == 42) {
            _skipCComment();
        } else {
            _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    private final void _skipCComment() throws IOException {
        int[] codes = CharTypes.getInputCodeComment();
        int i = this._inputData.readUnsignedByte();
        while (true) {
            int code = codes[i];
            if (code != 0) {
                switch (code) {
                    case 2:
                        _skipUtf8_2();
                        break;
                    case 3:
                        _skipUtf8_3();
                        break;
                    case 4:
                        _skipUtf8_4();
                        break;
                    case 10:
                    case 13:
                        this._currInputRow++;
                        break;
                    case R.styleable.ChartTheme_sc_seriesAreaColor3 /*42*/:
                        i = this._inputData.readUnsignedByte();
                        if (i != 47) {
                            continue;
                        } else {
                            return;
                        }
                    default:
                        _reportInvalidChar(i);
                        break;
                }
            }
            i = this._inputData.readUnsignedByte();
        }
    }

    private final boolean _skipYAMLComment() throws IOException {
        if (!isEnabled(Feature.ALLOW_YAML_COMMENTS)) {
            return false;
        }
        _skipLine();
        return true;
    }

    private final void _skipLine() throws IOException {
        int[] codes = CharTypes.getInputCodeComment();
        while (true) {
            int i = this._inputData.readUnsignedByte();
            int code = codes[i];
            if (code != 0) {
                switch (code) {
                    case 2:
                        _skipUtf8_2();
                        break;
                    case 3:
                        _skipUtf8_3();
                        break;
                    case 4:
                        _skipUtf8_4();
                        break;
                    case 10:
                    case 13:
                        this._currInputRow++;
                        return;
                    case R.styleable.ChartTheme_sc_seriesAreaColor3 /*42*/:
                        break;
                    default:
                        if (code >= 0) {
                            break;
                        }
                        _reportInvalidChar(i);
                        break;
                }
            }
        }
    }

    protected char _decodeEscaped() throws IOException {
        int c = this._inputData.readUnsignedByte();
        switch (c) {
            case R.styleable.ChartTheme_sc_seriesAreaColor1 /*34*/:
            case R.styleable.ChartTheme_sc_seriesAreaGradientColor4 /*47*/:
            case 92:
                return (char) c;
            case 98:
                return '\b';
            case 102:
                return '\f';
            case 110:
                return '\n';
            case 114:
                return '\r';
            case 116:
                return '\t';
            case 117:
                int value = 0;
                for (int i = 0; i < 4; i++) {
                    int ch = this._inputData.readUnsignedByte();
                    int digit = CharTypes.charToHex(ch);
                    if (digit < 0) {
                        _reportUnexpectedChar(ch, "expected a hex-digit for character escape sequence");
                    }
                    value = (value << 4) | digit;
                }
                return (char) value;
            default:
                return _handleUnrecognizedCharacterEscape((char) _decodeCharForError(c));
        }
    }

    protected int _decodeCharForError(int firstByte) throws IOException {
        int c = firstByte & 255;
        if (c <= 127) {
            return c;
        }
        int needed;
        if ((c & 224) == 192) {
            c &= 31;
            needed = 1;
        } else if ((c & 240) == 224) {
            c &= 15;
            needed = 2;
        } else if ((c & 248) == 240) {
            c &= 7;
            needed = 3;
        } else {
            _reportInvalidInitial(c & 255);
            needed = 1;
        }
        int d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        c = (c << 6) | (d & 63);
        if (needed <= 1) {
            return c;
        }
        d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        c = (c << 6) | (d & 63);
        if (needed <= 2) {
            return c;
        }
        d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        return (c << 6) | (d & 63);
    }

    private final int _decodeUtf8_2(int c) throws IOException {
        int d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        return ((c & 31) << 6) | (d & 63);
    }

    private final int _decodeUtf8_3(int c1) throws IOException {
        c1 &= 15;
        int d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        int c = (c1 << 6) | (d & 63);
        d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        return (c << 6) | (d & 63);
    }

    private final int _decodeUtf8_4(int c) throws IOException {
        int d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        c = ((c & 7) << 6) | (d & 63);
        d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        c = (c << 6) | (d & 63);
        d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        return ((c << 6) | (d & 63)) - 65536;
    }

    private final void _skipUtf8_2() throws IOException {
        int c = this._inputData.readUnsignedByte();
        if ((c & 192) != 128) {
            _reportInvalidOther(c & 255);
        }
    }

    private final void _skipUtf8_3() throws IOException {
        int c = this._inputData.readUnsignedByte();
        if ((c & 192) != 128) {
            _reportInvalidOther(c & 255);
        }
        c = this._inputData.readUnsignedByte();
        if ((c & 192) != 128) {
            _reportInvalidOther(c & 255);
        }
    }

    private final void _skipUtf8_4() throws IOException {
        int d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
        d = this._inputData.readUnsignedByte();
        if ((d & 192) != 128) {
            _reportInvalidOther(d & 255);
        }
    }

    protected void _reportInvalidToken(int ch, String matchedPart) throws IOException {
        _reportInvalidToken(ch, matchedPart, "'null', 'true', 'false' or NaN");
    }

    protected void _reportInvalidToken(int ch, String matchedPart, String msg) throws IOException {
        StringBuilder sb = new StringBuilder(matchedPart);
        while (true) {
            char c = (char) _decodeCharForError(ch);
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
                ch = this._inputData.readUnsignedByte();
            } else {
                _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
                return;
            }
        }
    }

    protected void _reportInvalidChar(int c) throws JsonParseException {
        if (c < 32) {
            _throwInvalidSpace(c);
        }
        _reportInvalidInitial(c);
    }

    protected void _reportInvalidInitial(int mask) throws JsonParseException {
        _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
    }

    private void _reportInvalidOther(int mask) throws JsonParseException {
        _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
    }

    private static int[] _growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        return Arrays.copyOf(arr, arr.length + more);
    }

    protected final byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
        ByteArrayBuilder builder = _getByteArrayBuilder();
        while (true) {
            int ch = this._inputData.readUnsignedByte();
            if (ch > 32) {
                int bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (ch == 34) {
                        return builder.toByteArray();
                    }
                    bits = _decodeBase64Escape(b64variant, ch, 0);
                    if (bits < 0) {
                        continue;
                    }
                }
                int decodedData = bits;
                ch = this._inputData.readUnsignedByte();
                bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    bits = _decodeBase64Escape(b64variant, ch, 1);
                }
                decodedData = (decodedData << 6) | bits;
                ch = this._inputData.readUnsignedByte();
                bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (bits != -2) {
                        if (ch != 34 || b64variant.usesPadding()) {
                            bits = _decodeBase64Escape(b64variant, ch, 2);
                        } else {
                            builder.append(decodedData >> 4);
                            return builder.toByteArray();
                        }
                    }
                    if (bits == -2) {
                        ch = this._inputData.readUnsignedByte();
                        if (b64variant.usesPaddingChar(ch)) {
                            builder.append(decodedData >> 4);
                        } else {
                            throw reportInvalidBase64Char(b64variant, ch, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                        }
                    }
                }
                decodedData = (decodedData << 6) | bits;
                ch = this._inputData.readUnsignedByte();
                bits = b64variant.decodeBase64Char(ch);
                if (bits < 0) {
                    if (bits != -2) {
                        if (ch != 34 || b64variant.usesPadding()) {
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
        return new JsonLocation(this._ioContext.getSourceReference(), -1, -1, this._tokenInputRow, -1);
    }

    public JsonLocation getCurrentLocation() {
        return new JsonLocation(this._ioContext.getSourceReference(), -1, -1, this._currInputRow, -1);
    }

    private static final int pad(int q, int bytes) {
        return bytes == 4 ? q : q | (-1 << (bytes << 3));
    }
}
