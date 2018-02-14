package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

final class RealBufferedSource implements BufferedSource {
    public final Buffer buffer = new Buffer();
    boolean closed;
    public final Source source;

    RealBufferedSource(Source source) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        this.source = source;
    }

    public final Buffer buffer() {
        return this.buffer;
    }

    public final long read(Buffer sink, long byteCount) throws IOException {
        if (sink == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        } else if (this.closed) {
            throw new IllegalStateException("closed");
        } else if (this.buffer.size == 0 && this.source.read(this.buffer, 8192) == -1) {
            return -1;
        } else {
            return this.buffer.read(sink, Math.min(byteCount, this.buffer.size));
        }
    }

    public final boolean exhausted() throws IOException {
        if (!this.closed) {
            return this.buffer.exhausted() && this.source.read(this.buffer, 8192) == -1;
        } else {
            throw new IllegalStateException("closed");
        }
    }

    public final void require(long byteCount) throws IOException {
        if (!request(byteCount)) {
            throw new EOFException();
        }
    }

    private boolean request(long byteCount) throws IOException {
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        } else if (this.closed) {
            throw new IllegalStateException("closed");
        } else {
            while (this.buffer.size < byteCount) {
                if (this.source.read(this.buffer, 8192) == -1) {
                    return false;
                }
            }
            return true;
        }
    }

    public final byte readByte() throws IOException {
        require(1);
        return this.buffer.readByte();
    }

    public final ByteString readByteString(long byteCount) throws IOException {
        require(byteCount);
        return this.buffer.readByteString(byteCount);
    }

    public final byte[] readByteArray() throws IOException {
        this.buffer.writeAll(this.source);
        return this.buffer.readByteArray();
    }

    public final byte[] readByteArray(long byteCount) throws IOException {
        require(byteCount);
        return this.buffer.readByteArray(byteCount);
    }

    public final String readUtf8LineStrict() throws IOException {
        long newline = indexOf((byte) 10);
        if (newline != -1) {
            return this.buffer.readUtf8Line(newline);
        }
        Buffer data = new Buffer();
        this.buffer.copyTo(data, 0, Math.min(32, this.buffer.size));
        throw new EOFException("\\n not found: size=" + this.buffer.size + " content=" + data.readByteString().hex() + "…");
    }

    public final short readShort() throws IOException {
        require(2);
        return this.buffer.readShort();
    }

    public final short readShortLe() throws IOException {
        require(2);
        return Util.reverseBytesShort(this.buffer.readShort());
    }

    public final int readInt() throws IOException {
        require(4);
        return this.buffer.readInt();
    }

    public final int readIntLe() throws IOException {
        require(4);
        return Util.reverseBytesInt(this.buffer.readInt());
    }

    public final long readDecimalLong() throws IOException {
        require(1);
        int pos = 0;
        while (request((long) (pos + 1))) {
            byte b = this.buffer.getByte((long) pos);
            if ((b < (byte) 48 || b > (byte) 57) && !(pos == 0 && b == (byte) 45)) {
                if (pos == 0) {
                    throw new NumberFormatException(String.format("Expected leading [0-9] or '-' character but was %#x", new Object[]{Byte.valueOf(b)}));
                }
                return this.buffer.readDecimalLong();
            }
            pos++;
        }
        return this.buffer.readDecimalLong();
    }

    public final long readHexadecimalUnsignedLong() throws IOException {
        require(1);
        for (int pos = 0; request((long) (pos + 1)); pos++) {
            byte b = this.buffer.getByte((long) pos);
            if ((b < (byte) 48 || b > (byte) 57) && ((b < (byte) 97 || b > (byte) 102) && (b < (byte) 65 || b > (byte) 70))) {
                if (pos == 0) {
                    throw new NumberFormatException(String.format("Expected leading [0-9a-fA-F] character but was %#x", new Object[]{Byte.valueOf(b)}));
                }
                return this.buffer.readHexadecimalUnsignedLong();
            }
        }
        return this.buffer.readHexadecimalUnsignedLong();
    }

    public final void skip(long byteCount) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        while (byteCount > 0) {
            if (this.buffer.size == 0 && this.source.read(this.buffer, 8192) == -1) {
                throw new EOFException();
            }
            long toSkip = Math.min(byteCount, this.buffer.size);
            this.buffer.skip(toSkip);
            byteCount -= toSkip;
        }
    }

    public final long indexOf(byte b) throws IOException {
        long j = 0;
        if (this.closed) {
            throw new IllegalStateException("closed");
        }
        while (true) {
            long indexOf = this.buffer.indexOf(b, j);
            if (indexOf != -1) {
                return indexOf;
            }
            indexOf = this.buffer.size;
            if (this.source.read(this.buffer, 8192) == -1) {
                return -1;
            }
            j = Math.max(j, indexOf);
        }
    }

    public final boolean rangeEquals(long offset, ByteString bytes) throws IOException {
        int size = bytes.size();
        if (this.closed) {
            throw new IllegalStateException("closed");
        } else if (offset < 0 || size < 0 || bytes.size() < size) {
            return false;
        } else {
            int i = 0;
            while (i < size) {
                long j = ((long) i) + offset;
                if (!request(1 + j) || this.buffer.getByte(j) != bytes.getByte(i + 0)) {
                    return false;
                }
                i++;
            }
            return true;
        }
    }

    public final InputStream inputStream() {
        return new InputStream() {
            public final int read() throws IOException {
                if (RealBufferedSource.this.closed) {
                    throw new IOException("closed");
                } else if (RealBufferedSource.this.buffer.size == 0 && RealBufferedSource.this.source.read(RealBufferedSource.this.buffer, 8192) == -1) {
                    return -1;
                } else {
                    return RealBufferedSource.this.buffer.readByte() & 255;
                }
            }

            public final int read(byte[] data, int offset, int byteCount) throws IOException {
                if (RealBufferedSource.this.closed) {
                    throw new IOException("closed");
                }
                Util.checkOffsetAndCount((long) data.length, (long) offset, (long) byteCount);
                if (RealBufferedSource.this.buffer.size == 0 && RealBufferedSource.this.source.read(RealBufferedSource.this.buffer, 8192) == -1) {
                    return -1;
                }
                return RealBufferedSource.this.buffer.read(data, offset, byteCount);
            }

            public final int available() throws IOException {
                if (!RealBufferedSource.this.closed) {
                    return (int) Math.min(RealBufferedSource.this.buffer.size, 2147483647L);
                }
                throw new IOException("closed");
            }

            public final void close() throws IOException {
                RealBufferedSource.this.close();
            }

            public final String toString() {
                return RealBufferedSource.this + ".inputStream()";
            }
        };
    }

    public final void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.source.close();
            this.buffer.clear();
        }
    }

    public final Timeout timeout() {
        return this.source.timeout();
    }

    public final String toString() {
        return "buffer(" + this.source + ")";
    }
}
