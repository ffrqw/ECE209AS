package com.fasterxml.jackson.core.io;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.lang.ref.SoftReference;

public final class JsonStringEncoder {
    private static final byte[] HB = CharTypes.copyHexBytes();
    private static final char[] HC = CharTypes.copyHexChars();
    protected static final ThreadLocal<SoftReference<JsonStringEncoder>> _threadEncoder = new ThreadLocal();
    protected ByteArrayBuilder _bytes;
    protected final char[] _qbuf = new char[6];
    protected TextBuffer _text;

    public JsonStringEncoder() {
        this._qbuf[0] = '\\';
        this._qbuf[2] = '0';
        this._qbuf[3] = '0';
    }

    public static JsonStringEncoder getInstance() {
        SoftReference<JsonStringEncoder> ref = (SoftReference) _threadEncoder.get();
        JsonStringEncoder enc = ref == null ? null : (JsonStringEncoder) ref.get();
        if (enc != null) {
            return enc;
        }
        enc = new JsonStringEncoder();
        _threadEncoder.set(new SoftReference(enc));
        return enc;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final char[] quoteAsString(java.lang.String r20) {
        /*
        r19 = this;
        r0 = r19;
        r0 = r0._text;
        r16 = r0;
        if (r16 != 0) goto L_0x0015;
    L_0x0008:
        r16 = new com.fasterxml.jackson.core.util.TextBuffer;
        r17 = 0;
        r16.<init>(r17);
        r0 = r16;
        r1 = r19;
        r1._text = r0;
    L_0x0015:
        r14 = r16.emptyAndGetCurrentSegment();
        r6 = com.fasterxml.jackson.core.io.CharTypes.get7BitOutputEscapes();
        r5 = r6.length;
        r8 = 0;
        r10 = r20.length();
        r12 = 0;
    L_0x0024:
        if (r8 >= r10) goto L_0x00be;
    L_0x0026:
        r0 = r20;
        r2 = r0.charAt(r8);
        if (r2 >= r5) goto L_0x0032;
    L_0x002e:
        r17 = r6[r2];
        if (r17 != 0) goto L_0x0048;
    L_0x0032:
        r0 = r14.length;
        r17 = r0;
        r0 = r17;
        if (r12 < r0) goto L_0x003e;
    L_0x0039:
        r14 = r16.finishCurrentSegment();
        r12 = 0;
    L_0x003e:
        r13 = r12 + 1;
        r14[r12] = r2;
        r8 = r8 + 1;
        if (r8 >= r10) goto L_0x00bd;
    L_0x0046:
        r12 = r13;
        goto L_0x0026;
    L_0x0048:
        r9 = r8 + 1;
        r0 = r20;
        r3 = r0.charAt(r8);
        r4 = r6[r3];
        if (r4 >= 0) goto L_0x009b;
    L_0x0054:
        r0 = r19;
        r0 = r0._qbuf;
        r17 = r0;
        r0 = r19;
        r1 = r17;
        r11 = r0._appendNumeric(r3, r1);
    L_0x0062:
        r17 = r12 + r11;
        r0 = r14.length;
        r18 = r0;
        r0 = r17;
        r1 = r18;
        if (r0 <= r1) goto L_0x00aa;
    L_0x006d:
        r0 = r14.length;
        r17 = r0;
        r7 = r17 - r12;
        if (r7 <= 0) goto L_0x0083;
    L_0x0074:
        r0 = r19;
        r0 = r0._qbuf;
        r17 = r0;
        r18 = 0;
        r0 = r17;
        r1 = r18;
        java.lang.System.arraycopy(r0, r1, r14, r12, r7);
    L_0x0083:
        r14 = r16.finishCurrentSegment();
        r15 = r11 - r7;
        r0 = r19;
        r0 = r0._qbuf;
        r17 = r0;
        r18 = 0;
        r0 = r17;
        r1 = r18;
        java.lang.System.arraycopy(r0, r7, r14, r1, r15);
        r12 = r15;
        r8 = r9;
        goto L_0x0024;
    L_0x009b:
        r0 = r19;
        r0 = r0._qbuf;
        r17 = r0;
        r0 = r19;
        r1 = r17;
        r11 = r0._appendNamed(r4, r1);
        goto L_0x0062;
    L_0x00aa:
        r0 = r19;
        r0 = r0._qbuf;
        r17 = r0;
        r18 = 0;
        r0 = r17;
        r1 = r18;
        java.lang.System.arraycopy(r0, r1, r14, r12, r11);
        r12 = r12 + r11;
        r8 = r9;
        goto L_0x0024;
    L_0x00bd:
        r12 = r13;
    L_0x00be:
        r0 = r16;
        r0.setCurrentLength(r12);
        r17 = r16.contentsAsArray();
        return r17;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.quoteAsString(java.lang.String):char[]");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final byte[] quoteAsUTF8(java.lang.String r13) {
        /*
        r12 = this;
        r11 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        r0 = r12._bytes;
        if (r0 != 0) goto L_0x000e;
    L_0x0006:
        r0 = new com.fasterxml.jackson.core.util.ByteArrayBuilder;
        r10 = 0;
        r0.<init>(r10);
        r12._bytes = r0;
    L_0x000e:
        r5 = 0;
        r4 = r13.length();
        r8 = 0;
        r7 = r0.resetAndGetFirstSegment();
    L_0x0018:
        if (r5 >= r4) goto L_0x00fc;
    L_0x001a:
        r2 = com.fasterxml.jackson.core.io.CharTypes.get7BitOutputEscapes();
    L_0x001e:
        r1 = r13.charAt(r5);
        if (r1 > r11) goto L_0x003b;
    L_0x0024:
        r10 = r2[r1];
        if (r10 != 0) goto L_0x003b;
    L_0x0028:
        r10 = r7.length;
        if (r8 < r10) goto L_0x0030;
    L_0x002b:
        r7 = r0.finishCurrentSegment();
        r8 = 0;
    L_0x0030:
        r9 = r8 + 1;
        r10 = (byte) r1;
        r7[r8] = r10;
        r5 = r5 + 1;
        if (r5 >= r4) goto L_0x00fb;
    L_0x0039:
        r8 = r9;
        goto L_0x001e;
    L_0x003b:
        r10 = r7.length;
        if (r8 < r10) goto L_0x0043;
    L_0x003e:
        r7 = r0.finishCurrentSegment();
        r8 = 0;
    L_0x0043:
        r6 = r5 + 1;
        r1 = r13.charAt(r5);
        if (r1 > r11) goto L_0x0057;
    L_0x004b:
        r3 = r2[r1];
        r8 = r12._appendByte(r1, r3, r0, r8);
        r7 = r0.getCurrentSegment();
        r5 = r6;
        goto L_0x0018;
    L_0x0057:
        r10 = 2047; // 0x7ff float:2.868E-42 double:1.0114E-320;
        if (r1 > r10) goto L_0x0079;
    L_0x005b:
        r9 = r8 + 1;
        r10 = r1 >> 6;
        r10 = r10 | 192;
        r10 = (byte) r10;
        r7[r8] = r10;
        r10 = r1 & 63;
        r1 = r10 | 128;
        r8 = r9;
        r5 = r6;
    L_0x006a:
        r10 = r7.length;
        if (r8 < r10) goto L_0x0072;
    L_0x006d:
        r7 = r0.finishCurrentSegment();
        r8 = 0;
    L_0x0072:
        r9 = r8 + 1;
        r10 = (byte) r1;
        r7[r8] = r10;
        r8 = r9;
        goto L_0x0018;
    L_0x0079:
        r10 = 55296; // 0xd800 float:7.7486E-41 double:2.732E-319;
        if (r1 < r10) goto L_0x0083;
    L_0x007e:
        r10 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r1 <= r10) goto L_0x00a6;
    L_0x0083:
        r9 = r8 + 1;
        r10 = r1 >> 12;
        r10 = r10 | 224;
        r10 = (byte) r10;
        r7[r8] = r10;
        r10 = r7.length;
        if (r9 < r10) goto L_0x0107;
    L_0x008f:
        r7 = r0.finishCurrentSegment();
        r8 = 0;
    L_0x0094:
        r9 = r8 + 1;
        r10 = r1 >> 6;
        r10 = r10 & 63;
        r10 = r10 | 128;
        r10 = (byte) r10;
        r7[r8] = r10;
        r10 = r1 & 63;
        r1 = r10 | 128;
        r8 = r9;
        r5 = r6;
        goto L_0x006a;
    L_0x00a6:
        r10 = 56319; // 0xdbff float:7.892E-41 double:2.78253E-319;
        if (r1 <= r10) goto L_0x00ae;
    L_0x00ab:
        _illegal(r1);
    L_0x00ae:
        if (r6 < r4) goto L_0x00b3;
    L_0x00b0:
        _illegal(r1);
    L_0x00b3:
        r5 = r6 + 1;
        r10 = r13.charAt(r6);
        r1 = _convert(r1, r10);
        r10 = 1114111; // 0x10ffff float:1.561202E-39 double:5.50444E-318;
        if (r1 <= r10) goto L_0x00c5;
    L_0x00c2:
        _illegal(r1);
    L_0x00c5:
        r9 = r8 + 1;
        r10 = r1 >> 18;
        r10 = r10 | 240;
        r10 = (byte) r10;
        r7[r8] = r10;
        r10 = r7.length;
        if (r9 < r10) goto L_0x0105;
    L_0x00d1:
        r7 = r0.finishCurrentSegment();
        r8 = 0;
    L_0x00d6:
        r9 = r8 + 1;
        r10 = r1 >> 12;
        r10 = r10 & 63;
        r10 = r10 | 128;
        r10 = (byte) r10;
        r7[r8] = r10;
        r10 = r7.length;
        if (r9 < r10) goto L_0x0103;
    L_0x00e4:
        r7 = r0.finishCurrentSegment();
        r8 = 0;
    L_0x00e9:
        r9 = r8 + 1;
        r10 = r1 >> 6;
        r10 = r10 & 63;
        r10 = r10 | 128;
        r10 = (byte) r10;
        r7[r8] = r10;
        r10 = r1 & 63;
        r1 = r10 | 128;
        r8 = r9;
        goto L_0x006a;
    L_0x00fb:
        r8 = r9;
    L_0x00fc:
        r10 = r12._bytes;
        r10 = r10.completeAndCoalesce(r8);
        return r10;
    L_0x0103:
        r8 = r9;
        goto L_0x00e9;
    L_0x0105:
        r8 = r9;
        goto L_0x00d6;
    L_0x0107:
        r8 = r9;
        goto L_0x0094;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.JsonStringEncoder.quoteAsUTF8(java.lang.String):byte[]");
    }

    public final byte[] encodeAsUTF8(String text) {
        int inputPtr;
        ByteArrayBuilder byteBuilder = this._bytes;
        if (byteBuilder == null) {
            byteBuilder = new ByteArrayBuilder(null);
            this._bytes = byteBuilder;
        }
        int inputEnd = text.length();
        int outputPtr = 0;
        byte[] outputBuffer = byteBuilder.resetAndGetFirstSegment();
        int outputEnd = outputBuffer.length;
        int inputPtr2 = 0;
        loop0:
        while (inputPtr2 < inputEnd) {
            int outputPtr2;
            inputPtr = inputPtr2 + 1;
            int c = text.charAt(inputPtr2);
            inputPtr2 = inputPtr;
            while (c <= 127) {
                if (outputPtr >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                }
                outputPtr2 = outputPtr + 1;
                outputBuffer[outputPtr] = (byte) c;
                if (inputPtr2 >= inputEnd) {
                    outputPtr = outputPtr2;
                    inputPtr = inputPtr2;
                    break loop0;
                }
                inputPtr = inputPtr2 + 1;
                c = text.charAt(inputPtr2);
                outputPtr = outputPtr2;
                inputPtr2 = inputPtr;
            }
            if (outputPtr >= outputEnd) {
                outputBuffer = byteBuilder.finishCurrentSegment();
                outputEnd = outputBuffer.length;
                outputPtr2 = 0;
            } else {
                outputPtr2 = outputPtr;
            }
            if (c < ItemAnimator.FLAG_MOVED) {
                outputPtr = outputPtr2 + 1;
                outputBuffer[outputPtr2] = (byte) ((c >> 6) | 192);
                inputPtr = inputPtr2;
            } else if (c < 55296 || c > 57343) {
                outputPtr = outputPtr2 + 1;
                outputBuffer[outputPtr2] = (byte) ((c >> 12) | 224);
                if (outputPtr >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                }
                outputPtr2 = outputPtr + 1;
                outputBuffer[outputPtr] = (byte) (((c >> 6) & 63) | 128);
                outputPtr = outputPtr2;
                inputPtr = inputPtr2;
            } else {
                if (c > 56319) {
                    _illegal(c);
                }
                if (inputPtr2 >= inputEnd) {
                    _illegal(c);
                }
                inputPtr = inputPtr2 + 1;
                c = _convert(c, text.charAt(inputPtr2));
                if (c > 1114111) {
                    _illegal(c);
                }
                outputPtr = outputPtr2 + 1;
                outputBuffer[outputPtr2] = (byte) ((c >> 18) | 240);
                if (outputPtr >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                }
                outputPtr2 = outputPtr + 1;
                outputBuffer[outputPtr] = (byte) (((c >> 12) & 63) | 128);
                if (outputPtr2 >= outputEnd) {
                    outputBuffer = byteBuilder.finishCurrentSegment();
                    outputEnd = outputBuffer.length;
                    outputPtr = 0;
                } else {
                    outputPtr = outputPtr2;
                }
                outputPtr2 = outputPtr + 1;
                outputBuffer[outputPtr] = (byte) (((c >> 6) & 63) | 128);
                outputPtr = outputPtr2;
            }
            if (outputPtr >= outputEnd) {
                outputBuffer = byteBuilder.finishCurrentSegment();
                outputEnd = outputBuffer.length;
                outputPtr = 0;
            }
            outputPtr2 = outputPtr + 1;
            outputBuffer[outputPtr] = (byte) ((c & 63) | 128);
            outputPtr = outputPtr2;
            inputPtr2 = inputPtr;
        }
        inputPtr = inputPtr2;
        return this._bytes.completeAndCoalesce(outputPtr);
    }

    private int _appendNumeric(int value, char[] qbuf) {
        qbuf[1] = 'u';
        qbuf[4] = HC[value >> 4];
        qbuf[5] = HC[value & 15];
        return 6;
    }

    private int _appendNamed(int esc, char[] qbuf) {
        qbuf[1] = (char) esc;
        return 2;
    }

    private int _appendByte(int ch, int esc, ByteArrayBuilder bb, int ptr) {
        bb.setCurrentSegmentLength(ptr);
        bb.append(92);
        if (esc < 0) {
            bb.append(117);
            if (ch > 255) {
                int hi = ch >> 8;
                bb.append(HB[hi >> 4]);
                bb.append(HB[hi & 15]);
                ch &= 255;
            } else {
                bb.append(48);
                bb.append(48);
            }
            bb.append(HB[ch >> 4]);
            bb.append(HB[ch & 15]);
        } else {
            bb.append((byte) esc);
        }
        return bb.getCurrentSegmentLength();
    }

    private static int _convert(int p1, int p2) {
        if (p2 >= 56320 && p2 <= 57343) {
            return (65536 + ((p1 - 55296) << 10)) + (p2 - 56320);
        }
        throw new IllegalArgumentException("Broken surrogate pair: first char 0x" + Integer.toHexString(p1) + ", second 0x" + Integer.toHexString(p2) + "; illegal combination");
    }

    private static void _illegal(int c) {
        throw new IllegalArgumentException(UTF8Writer.illegalSurrogateDesc(c));
    }
}
