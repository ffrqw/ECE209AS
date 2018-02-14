package com.squareup.picasso;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MarkableInputStream extends InputStream {
    private boolean allowExpire;
    private long defaultMark;
    private final InputStream in;
    private long limit;
    private int limitIncrement;
    private long offset;
    private long reset;

    public MarkableInputStream(InputStream in) {
        this(in, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
    }

    private MarkableInputStream(InputStream in, int size) {
        this(in, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, 1024);
    }

    private MarkableInputStream(InputStream in, int size, int limitIncrement) {
        this.defaultMark = -1;
        this.allowExpire = true;
        this.limitIncrement = -1;
        if (!in.markSupported()) {
            in = new BufferedInputStream(in, size);
        }
        this.in = in;
        this.limitIncrement = 1024;
    }

    public final void mark(int readLimit) {
        this.defaultMark = savePosition(readLimit);
    }

    public final long savePosition(int readLimit) {
        long offsetLimit = this.offset + ((long) readLimit);
        if (this.limit < offsetLimit) {
            setLimit(offsetLimit);
        }
        return this.offset;
    }

    public final void allowMarksToExpire(boolean allowExpire) {
        this.allowExpire = allowExpire;
    }

    private void setLimit(long limit) {
        try {
            if (this.reset >= this.offset || this.offset > this.limit) {
                this.reset = this.offset;
                this.in.mark((int) (limit - this.offset));
            } else {
                this.in.reset();
                this.in.mark((int) (limit - this.reset));
                skip(this.reset, this.offset);
            }
            this.limit = limit;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to mark: " + e);
        }
    }

    public final void reset() throws IOException {
        reset(this.defaultMark);
    }

    public final void reset(long token) throws IOException {
        if (this.offset > this.limit || token < this.reset) {
            throw new IOException("Cannot reset");
        }
        this.in.reset();
        skip(this.reset, token);
        this.offset = token;
    }

    private void skip(long current, long target) throws IOException {
        while (current < target) {
            long skipped = this.in.skip(target - current);
            if (skipped == 0) {
                if (read() != -1) {
                    skipped = 1;
                } else {
                    return;
                }
            }
            current += skipped;
        }
    }

    public final int read() throws IOException {
        if (!this.allowExpire && this.offset + 1 > this.limit) {
            setLimit(this.limit + ((long) this.limitIncrement));
        }
        int result = this.in.read();
        if (result != -1) {
            this.offset++;
        }
        return result;
    }

    public final int read(byte[] buffer) throws IOException {
        if (!this.allowExpire && this.offset + ((long) buffer.length) > this.limit) {
            setLimit((this.offset + ((long) buffer.length)) + ((long) this.limitIncrement));
        }
        int count = this.in.read(buffer);
        if (count != -1) {
            this.offset += (long) count;
        }
        return count;
    }

    public final int read(byte[] buffer, int offset, int length) throws IOException {
        if (!this.allowExpire && this.offset + ((long) length) > this.limit) {
            setLimit((this.offset + ((long) length)) + ((long) this.limitIncrement));
        }
        int count = this.in.read(buffer, offset, length);
        if (count != -1) {
            this.offset += (long) count;
        }
        return count;
    }

    public final long skip(long byteCount) throws IOException {
        if (!this.allowExpire && this.offset + byteCount > this.limit) {
            setLimit((this.offset + byteCount) + ((long) this.limitIncrement));
        }
        long skipped = this.in.skip(byteCount);
        this.offset += skipped;
        return skipped;
    }

    public final int available() throws IOException {
        return this.in.available();
    }

    public final void close() throws IOException {
        this.in.close();
    }

    public final boolean markSupported() {
        return this.in.markSupported();
    }
}
