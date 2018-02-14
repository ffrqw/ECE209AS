package com.fasterxml.jackson.core.io;

import java.io.IOException;
import java.io.InputStream;

public final class MergedStream extends InputStream {
    private byte[] _b;
    private final IOContext _ctxt;
    private final int _end;
    private final InputStream _in;
    private int _ptr;

    public MergedStream(IOContext ctxt, InputStream in, byte[] buf, int start, int end) {
        this._ctxt = ctxt;
        this._in = in;
        this._b = buf;
        this._ptr = start;
        this._end = end;
    }

    public final int available() throws IOException {
        if (this._b != null) {
            return this._end - this._ptr;
        }
        return this._in.available();
    }

    public final void close() throws IOException {
        _free();
        this._in.close();
    }

    public final void mark(int readlimit) {
        if (this._b == null) {
            this._in.mark(readlimit);
        }
    }

    public final boolean markSupported() {
        return this._b == null && this._in.markSupported();
    }

    public final int read() throws IOException {
        if (this._b == null) {
            return this._in.read();
        }
        byte[] bArr = this._b;
        int i = this._ptr;
        this._ptr = i + 1;
        int c = bArr[i] & 255;
        if (this._ptr < this._end) {
            return c;
        }
        _free();
        return c;
    }

    public final int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public final int read(byte[] b, int off, int len) throws IOException {
        if (this._b == null) {
            return this._in.read(b, off, len);
        }
        int avail = this._end - this._ptr;
        if (len > avail) {
            len = avail;
        }
        System.arraycopy(this._b, this._ptr, b, off, len);
        this._ptr += len;
        if (this._ptr >= this._end) {
            _free();
        }
        return len;
    }

    public final void reset() throws IOException {
        if (this._b == null) {
            this._in.reset();
        }
    }

    public final long skip(long n) throws IOException {
        long count = 0;
        if (this._b != null) {
            int amount = this._end - this._ptr;
            if (((long) amount) > n) {
                this._ptr += (int) n;
                return n;
            }
            _free();
            count = 0 + ((long) amount);
            n -= (long) amount;
        }
        if (n > 0) {
            count += this._in.skip(n);
        }
        return count;
    }

    private void _free() {
        byte[] buf = this._b;
        if (buf != null) {
            this._b = null;
            if (this._ctxt != null) {
                this._ctxt.releaseReadIOBuffer(buf);
            }
        }
    }
}
