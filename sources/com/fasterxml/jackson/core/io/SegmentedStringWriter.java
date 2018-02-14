package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.Writer;

public final class SegmentedStringWriter extends Writer {
    private final TextBuffer _buffer;

    public SegmentedStringWriter(BufferRecycler br) {
        this._buffer = new TextBuffer(br);
    }

    public final Writer append(char c) {
        write((int) c);
        return this;
    }

    public final Writer append(CharSequence csq) {
        String str = csq.toString();
        this._buffer.append(str, 0, str.length());
        return this;
    }

    public final Writer append(CharSequence csq, int start, int end) {
        String str = csq.subSequence(start, end).toString();
        this._buffer.append(str, 0, str.length());
        return this;
    }

    public final void close() {
    }

    public final void flush() {
    }

    public final void write(char[] cbuf) {
        this._buffer.append(cbuf, 0, cbuf.length);
    }

    public final void write(char[] cbuf, int off, int len) {
        this._buffer.append(cbuf, off, len);
    }

    public final void write(int c) {
        this._buffer.append((char) c);
    }

    public final void write(String str) {
        this._buffer.append(str, 0, str.length());
    }

    public final void write(String str, int off, int len) {
        this._buffer.append(str, off, len);
    }

    public final String getAndClear() {
        String result = this._buffer.contentsAsString();
        this._buffer.releaseBuffers();
        return result;
    }
}
