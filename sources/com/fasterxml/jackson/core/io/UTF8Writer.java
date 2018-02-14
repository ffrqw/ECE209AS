package com.fasterxml.jackson.core.io;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public final class UTF8Writer extends Writer {
    private final IOContext _context;
    private OutputStream _out;
    private byte[] _outBuffer;
    private final int _outBufferEnd = (this._outBuffer.length - 4);
    private int _outPtr = 0;
    private int _surrogate;

    public UTF8Writer(IOContext ctxt, OutputStream out) {
        this._context = ctxt;
        this._out = out;
        this._outBuffer = ctxt.allocWriteEncodingBuffer();
    }

    public final Writer append(char c) throws IOException {
        write((int) c);
        return this;
    }

    public final void close() throws IOException {
        if (this._out != null) {
            if (this._outPtr > 0) {
                this._out.write(this._outBuffer, 0, this._outPtr);
                this._outPtr = 0;
            }
            OutputStream out = this._out;
            this._out = null;
            byte[] buf = this._outBuffer;
            if (buf != null) {
                this._outBuffer = null;
                this._context.releaseWriteEncodingBuffer(buf);
            }
            out.close();
            int code = this._surrogate;
            this._surrogate = 0;
            if (code > 0) {
                illegalSurrogate(code);
            }
        }
    }

    public final void flush() throws IOException {
        if (this._out != null) {
            if (this._outPtr > 0) {
                this._out.write(this._outBuffer, 0, this._outPtr);
                this._outPtr = 0;
            }
            this._out.flush();
        }
    }

    public final void write(char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    public final void write(char[] cbuf, int off, int len) throws IOException {
        if (len >= 2) {
            int off2;
            if (this._surrogate > 0) {
                off2 = off + 1;
                len--;
                write(convertSurrogate(cbuf[off]));
                off = off2;
            }
            int outPtr = this._outPtr;
            byte[] outBuf = this._outBuffer;
            int outBufLast = this._outBufferEnd;
            len += off;
            off2 = off;
            while (off2 < len) {
                int outPtr2;
                if (outPtr >= outBufLast) {
                    this._out.write(outBuf, 0, outPtr);
                    outPtr = 0;
                }
                off = off2 + 1;
                int c = cbuf[off2];
                if (c < 128) {
                    outPtr2 = outPtr + 1;
                    outBuf[outPtr] = (byte) c;
                    int maxInCount = len - off;
                    int maxOutCount = outBufLast - outPtr2;
                    if (maxInCount > maxOutCount) {
                        maxInCount = maxOutCount;
                    }
                    maxInCount += off;
                    off2 = off;
                    while (off2 < maxInCount) {
                        off = off2 + 1;
                        c = cbuf[off2];
                        if (c < 128) {
                            outPtr = outPtr2 + 1;
                            outBuf[outPtr2] = (byte) c;
                            outPtr2 = outPtr;
                            off2 = off;
                        } else {
                            off2 = off;
                        }
                    }
                    outPtr = outPtr2;
                } else {
                    outPtr2 = outPtr;
                    off2 = off;
                }
                if (c >= ItemAnimator.FLAG_MOVED) {
                    if (c >= 55296 && c <= 57343) {
                        if (c > 56319) {
                            this._outPtr = outPtr2;
                            illegalSurrogate(c);
                        }
                        this._surrogate = c;
                        if (off2 >= len) {
                            outPtr = outPtr2;
                            off = off2;
                            break;
                        }
                        off = off2 + 1;
                        c = convertSurrogate(cbuf[off2]);
                        if (c > 1114111) {
                            this._outPtr = outPtr2;
                            illegalSurrogate(c);
                        }
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) ((c >> 18) | 240);
                        outPtr2 = outPtr + 1;
                        outBuf[outPtr] = (byte) (((c >> 12) & 63) | 128);
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) (((c >> 6) & 63) | 128);
                        outPtr2 = outPtr + 1;
                        outBuf[outPtr] = (byte) ((c & 63) | 128);
                        outPtr = outPtr2;
                        off2 = off;
                    } else {
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) ((c >> 12) | 224);
                        outPtr2 = outPtr + 1;
                        outBuf[outPtr] = (byte) (((c >> 6) & 63) | 128);
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) ((c & 63) | 128);
                    }
                } else {
                    outPtr = outPtr2 + 1;
                    outBuf[outPtr2] = (byte) ((c >> 6) | 192);
                    outPtr2 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c & 63) | 128);
                    outPtr = outPtr2;
                }
            }
            off = off2;
            this._outPtr = outPtr;
        } else if (len == 1) {
            write(cbuf[off]);
        }
    }

    public final void write(int c) throws IOException {
        if (this._surrogate > 0) {
            c = convertSurrogate(c);
        } else if (c >= 55296 && c <= 57343) {
            if (c > 56319) {
                illegalSurrogate(c);
            }
            this._surrogate = c;
            return;
        }
        if (this._outPtr >= this._outBufferEnd) {
            this._out.write(this._outBuffer, 0, this._outPtr);
            this._outPtr = 0;
        }
        if (c < 128) {
            byte[] bArr = this._outBuffer;
            int i = this._outPtr;
            this._outPtr = i + 1;
            bArr[i] = (byte) c;
            return;
        }
        int i2 = this._outPtr;
        int i3;
        if (c < ItemAnimator.FLAG_MOVED) {
            i3 = i2 + 1;
            this._outBuffer[i2] = (byte) ((c >> 6) | 192);
            i2 = i3 + 1;
            this._outBuffer[i3] = (byte) ((c & 63) | 128);
        } else if (c <= 65535) {
            i3 = i2 + 1;
            this._outBuffer[i2] = (byte) ((c >> 12) | 224);
            i2 = i3 + 1;
            this._outBuffer[i3] = (byte) (((c >> 6) & 63) | 128);
            i3 = i2 + 1;
            this._outBuffer[i2] = (byte) ((c & 63) | 128);
            i2 = i3;
        } else {
            if (c > 1114111) {
                illegalSurrogate(c);
            }
            i3 = i2 + 1;
            this._outBuffer[i2] = (byte) ((c >> 18) | 240);
            i2 = i3 + 1;
            this._outBuffer[i3] = (byte) (((c >> 12) & 63) | 128);
            i3 = i2 + 1;
            this._outBuffer[i2] = (byte) (((c >> 6) & 63) | 128);
            i2 = i3 + 1;
            this._outBuffer[i3] = (byte) ((c & 63) | 128);
        }
        this._outPtr = i2;
    }

    public final void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    public final void write(String str, int off, int len) throws IOException {
        if (len >= 2) {
            int off2;
            if (this._surrogate > 0) {
                off2 = off + 1;
                len--;
                write(convertSurrogate(str.charAt(off)));
                off = off2;
            }
            int outPtr = this._outPtr;
            byte[] outBuf = this._outBuffer;
            int outBufLast = this._outBufferEnd;
            len += off;
            off2 = off;
            while (off2 < len) {
                int outPtr2;
                if (outPtr >= outBufLast) {
                    this._out.write(outBuf, 0, outPtr);
                    outPtr = 0;
                }
                off = off2 + 1;
                int c = str.charAt(off2);
                if (c < 128) {
                    outPtr2 = outPtr + 1;
                    outBuf[outPtr] = (byte) c;
                    int maxInCount = len - off;
                    int maxOutCount = outBufLast - outPtr2;
                    if (maxInCount > maxOutCount) {
                        maxInCount = maxOutCount;
                    }
                    maxInCount += off;
                    off2 = off;
                    while (off2 < maxInCount) {
                        off = off2 + 1;
                        c = str.charAt(off2);
                        if (c < 128) {
                            outPtr = outPtr2 + 1;
                            outBuf[outPtr2] = (byte) c;
                            outPtr2 = outPtr;
                            off2 = off;
                        } else {
                            off2 = off;
                        }
                    }
                    outPtr = outPtr2;
                } else {
                    outPtr2 = outPtr;
                    off2 = off;
                }
                if (c >= ItemAnimator.FLAG_MOVED) {
                    if (c >= 55296 && c <= 57343) {
                        if (c > 56319) {
                            this._outPtr = outPtr2;
                            illegalSurrogate(c);
                        }
                        this._surrogate = c;
                        if (off2 >= len) {
                            outPtr = outPtr2;
                            off = off2;
                            break;
                        }
                        off = off2 + 1;
                        c = convertSurrogate(str.charAt(off2));
                        if (c > 1114111) {
                            this._outPtr = outPtr2;
                            illegalSurrogate(c);
                        }
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) ((c >> 18) | 240);
                        outPtr2 = outPtr + 1;
                        outBuf[outPtr] = (byte) (((c >> 12) & 63) | 128);
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) (((c >> 6) & 63) | 128);
                        outPtr2 = outPtr + 1;
                        outBuf[outPtr] = (byte) ((c & 63) | 128);
                        outPtr = outPtr2;
                        off2 = off;
                    } else {
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) ((c >> 12) | 224);
                        outPtr2 = outPtr + 1;
                        outBuf[outPtr] = (byte) (((c >> 6) & 63) | 128);
                        outPtr = outPtr2 + 1;
                        outBuf[outPtr2] = (byte) ((c & 63) | 128);
                    }
                } else {
                    outPtr = outPtr2 + 1;
                    outBuf[outPtr2] = (byte) ((c >> 6) | 192);
                    outPtr2 = outPtr + 1;
                    outBuf[outPtr] = (byte) ((c & 63) | 128);
                    outPtr = outPtr2;
                }
            }
            off = off2;
            this._outPtr = outPtr;
        } else if (len == 1) {
            write(str.charAt(off));
        }
    }

    protected final int convertSurrogate(int secondPart) throws IOException {
        int firstPart = this._surrogate;
        this._surrogate = 0;
        if (secondPart >= 56320 && secondPart <= 57343) {
            return (65536 + ((firstPart - 55296) << 10)) + (secondPart - 56320);
        }
        throw new IOException("Broken surrogate pair: first char 0x" + Integer.toHexString(firstPart) + ", second 0x" + Integer.toHexString(secondPart) + "; illegal combination");
    }

    protected static void illegalSurrogate(int code) throws IOException {
        throw new IOException(illegalSurrogateDesc(code));
    }

    protected static String illegalSurrogateDesc(int code) {
        if (code > 1114111) {
            return "Illegal character point (0x" + Integer.toHexString(code) + ") to output; max is 0x10FFFF as per RFC 4627";
        }
        if (code < 55296) {
            return "Illegal character point (0x" + Integer.toHexString(code) + ") to output";
        }
        if (code <= 56319) {
            return "Unmatched first part of surrogate pair (0x" + Integer.toHexString(code) + ")";
        }
        return "Unmatched second part of surrogate pair (0x" + Integer.toHexString(code) + ")";
    }
}
