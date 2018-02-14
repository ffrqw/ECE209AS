package okio;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class Buffer implements Cloneable, BufferedSink, BufferedSource {
    private static final byte[] DIGITS = new byte[]{(byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102};
    Segment head;
    long size;

    public final /* bridge */ /* synthetic */ BufferedSink emitCompleteSegments() throws IOException {
        return this;
    }

    public final long size() {
        return this.size;
    }

    public final Buffer buffer() {
        return this;
    }

    public final boolean exhausted() {
        return this.size == 0;
    }

    public final void require(long byteCount) throws EOFException {
        if (this.size < byteCount) {
            throw new EOFException();
        }
    }

    public final InputStream inputStream() {
        return new InputStream() {
            public final int read() {
                if (Buffer.this.size > 0) {
                    return Buffer.this.readByte() & 255;
                }
                return -1;
            }

            public final int read(byte[] sink, int offset, int byteCount) {
                return Buffer.this.read(sink, offset, byteCount);
            }

            public final int available() {
                return (int) Math.min(Buffer.this.size, 2147483647L);
            }

            public final void close() {
            }

            public final String toString() {
                return Buffer.this + ".inputStream()";
            }
        };
    }

    public final Buffer copyTo(Buffer out, long offset, long byteCount) {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        }
        Util.checkOffsetAndCount(this.size, offset, byteCount);
        if (byteCount != 0) {
            out.size += byteCount;
            Segment s = this.head;
            while (offset >= ((long) (s.limit - s.pos))) {
                offset -= (long) (s.limit - s.pos);
                s = s.next;
            }
            while (byteCount > 0) {
                Segment copy = new Segment(s);
                copy.pos = (int) (((long) copy.pos) + offset);
                copy.limit = Math.min(copy.pos + ((int) byteCount), copy.limit);
                if (out.head == null) {
                    copy.prev = copy;
                    copy.next = copy;
                    out.head = copy;
                } else {
                    out.head.prev.push(copy);
                }
                byteCount -= (long) (copy.limit - copy.pos);
                offset = 0;
                s = s.next;
            }
        }
        return this;
    }

    public final byte readByte() {
        if (this.size == 0) {
            throw new IllegalStateException("size == 0");
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        int pos2 = pos + 1;
        byte b = segment.data[pos];
        this.size--;
        if (pos2 == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos2;
        }
        return b;
    }

    public final byte getByte(long pos) {
        Util.checkOffsetAndCount(this.size, pos, 1);
        Segment s = this.head;
        while (true) {
            int segmentByteCount = s.limit - s.pos;
            if (pos < ((long) segmentByteCount)) {
                return s.data[s.pos + ((int) pos)];
            }
            pos -= (long) segmentByteCount;
            s = s.next;
        }
    }

    public final short readShort() {
        if (this.size < 2) {
            throw new IllegalStateException("size < 2: " + this.size);
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        if (limit - pos < 2) {
            return (short) (((readByte() & 255) << 8) | (readByte() & 255));
        }
        byte[] data = segment.data;
        int pos2 = pos + 1;
        pos = pos2 + 1;
        int s = ((data[pos] & 255) << 8) | (data[pos2] & 255);
        this.size -= 2;
        if (pos == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
        } else {
            segment.pos = pos;
        }
        return (short) s;
    }

    public final int readInt() {
        if (this.size < 4) {
            throw new IllegalStateException("size < 4: " + this.size);
        }
        Segment segment = this.head;
        int pos = segment.pos;
        int limit = segment.limit;
        if (limit - pos < 4) {
            return ((((readByte() & 255) << 24) | ((readByte() & 255) << 16)) | ((readByte() & 255) << 8)) | (readByte() & 255);
        }
        byte[] data = segment.data;
        int pos2 = pos + 1;
        pos = pos2 + 1;
        pos2 = pos + 1;
        pos = pos2 + 1;
        int i = ((((data[pos] & 255) << 24) | ((data[pos2] & 255) << 16)) | ((data[pos] & 255) << 8)) | (data[pos2] & 255);
        this.size -= 4;
        if (pos == limit) {
            this.head = segment.pop();
            SegmentPool.recycle(segment);
            return i;
        }
        segment.pos = pos;
        return i;
    }

    public final short readShortLe() {
        return Util.reverseBytesShort(readShort());
    }

    public final int readIntLe() {
        return Util.reverseBytesInt(readInt());
    }

    public final long readDecimalLong() {
        if (this.size == 0) {
            throw new IllegalStateException("size == 0");
        }
        long value = 0;
        int seen = 0;
        boolean negative = false;
        boolean done = false;
        long overflowDigit = -7;
        do {
            Segment segment = this.head;
            byte[] data = segment.data;
            int pos = segment.pos;
            int limit = segment.limit;
            while (pos < limit) {
                int b = data[pos];
                if (b >= (byte) 48 && b <= (byte) 57) {
                    int digit = 48 - b;
                    if (value < -922337203685477580L || (value == -922337203685477580L && ((long) digit) < overflowDigit)) {
                        Buffer buffer = new Buffer().writeDecimalLong(value).writeByte(b);
                        if (!negative) {
                            buffer.readByte();
                        }
                        throw new NumberFormatException("Number too large: " + buffer.readUtf8());
                    }
                    value = (value * 10) + ((long) digit);
                } else if (b != 45 || seen != 0) {
                    if (seen != 0) {
                        done = true;
                        if (pos != limit) {
                            this.head = segment.pop();
                            SegmentPool.recycle(segment);
                        } else {
                            segment.pos = pos;
                        }
                        if (!done) {
                            break;
                        }
                    } else {
                        throw new NumberFormatException("Expected leading [0-9] or '-' character but was 0x" + Integer.toHexString(b));
                    }
                } else {
                    negative = true;
                    overflowDigit--;
                }
                pos++;
                seen++;
            }
            if (pos != limit) {
                segment.pos = pos;
            } else {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            }
            if (!done) {
                break;
            }
        } while (this.head != null);
        this.size -= (long) seen;
        return negative ? value : -value;
    }

    public final long readHexadecimalUnsignedLong() {
        if (this.size == 0) {
            throw new IllegalStateException("size == 0");
        }
        long value = 0;
        int seen = 0;
        boolean done = false;
        do {
            Segment segment = this.head;
            byte[] data = segment.data;
            int pos = segment.pos;
            int limit = segment.limit;
            while (pos < limit) {
                int digit;
                byte b = data[pos];
                if (b >= (byte) 48 && b <= (byte) 57) {
                    digit = b - 48;
                } else if (b >= (byte) 97 && b <= (byte) 102) {
                    digit = (b - 97) + 10;
                } else if (b < (byte) 65 || b > (byte) 70) {
                    if (seen != 0) {
                        done = true;
                        if (pos != limit) {
                            this.head = segment.pop();
                            SegmentPool.recycle(segment);
                        } else {
                            segment.pos = pos;
                        }
                        if (!done) {
                            break;
                        }
                    } else {
                        throw new NumberFormatException("Expected leading [0-9a-fA-F] character but was 0x" + Integer.toHexString(b));
                    }
                } else {
                    digit = (b - 65) + 10;
                }
                if ((-1152921504606846976L & value) != 0) {
                    throw new NumberFormatException("Number too large: " + new Buffer().writeHexadecimalUnsignedLong(value).writeByte((int) b).readUtf8());
                }
                value = (value << 4) | ((long) digit);
                pos++;
                seen++;
            }
            if (pos != limit) {
                segment.pos = pos;
            } else {
                this.head = segment.pop();
                SegmentPool.recycle(segment);
            }
            if (!done) {
                break;
            }
        } while (this.head != null);
        this.size -= (long) seen;
        return value;
    }

    public final ByteString readByteString() {
        return new ByteString(readByteArray());
    }

    public final ByteString readByteString(long byteCount) throws EOFException {
        return new ByteString(readByteArray(byteCount));
    }

    public final String readUtf8() {
        try {
            return readString(this.size, Util.UTF_8);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    private String readUtf8(long byteCount) throws EOFException {
        return readString(byteCount, Util.UTF_8);
    }

    private String readString(long byteCount, Charset charset) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, byteCount);
        if (charset == null) {
            throw new IllegalArgumentException("charset == null");
        } else if (byteCount > 2147483647L) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
        } else if (byteCount == 0) {
            return "";
        } else {
            Segment s = this.head;
            if (((long) s.pos) + byteCount > ((long) s.limit)) {
                return new String(readByteArray(byteCount), charset);
            }
            String result = new String(s.data, s.pos, (int) byteCount, charset);
            s.pos = (int) (((long) s.pos) + byteCount);
            this.size -= byteCount;
            if (s.pos != s.limit) {
                return result;
            }
            this.head = s.pop();
            SegmentPool.recycle(s);
            return result;
        }
    }

    public final String readUtf8LineStrict() throws EOFException {
        long newline = indexOf((byte) 10, 0);
        if (newline != -1) {
            return readUtf8Line(newline);
        }
        Buffer data = new Buffer();
        copyTo(data, 0, Math.min(32, this.size));
        throw new EOFException("\\n not found: size=" + this.size + " content=" + data.readByteString().hex() + "…");
    }

    final String readUtf8Line(long newline) throws EOFException {
        if (newline <= 0 || getByte(newline - 1) != (byte) 13) {
            String result = readUtf8(newline);
            skip(1);
            return result;
        }
        result = readUtf8(newline - 1);
        skip(2);
        return result;
    }

    public final byte[] readByteArray() {
        try {
            return readByteArray(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public final byte[] readByteArray(long byteCount) throws EOFException {
        Util.checkOffsetAndCount(this.size, 0, byteCount);
        if (byteCount > 2147483647L) {
            throw new IllegalArgumentException("byteCount > Integer.MAX_VALUE: " + byteCount);
        }
        byte[] result = new byte[((int) byteCount)];
        readFully(result);
        return result;
    }

    private void readFully(byte[] sink) throws EOFException {
        int offset = 0;
        while (offset < sink.length) {
            int read = read(sink, offset, sink.length - offset);
            if (read == -1) {
                throw new EOFException();
            }
            offset += read;
        }
    }

    public final int read(byte[] sink, int offset, int byteCount) {
        Util.checkOffsetAndCount((long) sink.length, (long) offset, (long) byteCount);
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        int toCopy = Math.min(byteCount, s.limit - s.pos);
        System.arraycopy(s.data, s.pos, sink, offset, toCopy);
        s.pos += toCopy;
        this.size -= (long) toCopy;
        if (s.pos != s.limit) {
            return toCopy;
        }
        this.head = s.pop();
        SegmentPool.recycle(s);
        return toCopy;
    }

    public final void clear() {
        try {
            skip(this.size);
        } catch (EOFException e) {
            throw new AssertionError(e);
        }
    }

    public final void skip(long byteCount) throws EOFException {
        while (byteCount > 0) {
            if (this.head == null) {
                throw new EOFException();
            }
            int toSkip = (int) Math.min(byteCount, (long) (this.head.limit - this.head.pos));
            this.size -= (long) toSkip;
            byteCount -= (long) toSkip;
            Segment segment = this.head;
            segment.pos += toSkip;
            if (this.head.pos == this.head.limit) {
                Segment toRecycle = this.head;
                this.head = toRecycle.pop();
                SegmentPool.recycle(toRecycle);
            }
        }
    }

    public final Buffer write(ByteString byteString) {
        if (byteString == null) {
            throw new IllegalArgumentException("byteString == null");
        }
        byteString.write(this);
        return this;
    }

    public final Buffer writeUtf8(String string) {
        return writeUtf8(string, 0, string.length());
    }

    public final Buffer writeUtf8(String string, int beginIndex, int endIndex) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        } else if (beginIndex < 0) {
            throw new IllegalAccessError("beginIndex < 0: " + beginIndex);
        } else if (endIndex < beginIndex) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
        } else if (endIndex > string.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
        } else {
            int i = beginIndex;
            while (i < endIndex) {
                int c = string.charAt(i);
                if (c < 128) {
                    Segment tail = writableSegment(1);
                    byte[] data = tail.data;
                    int segmentOffset = tail.limit - i;
                    int runLimit = Math.min(endIndex, 8192 - segmentOffset);
                    int i2 = i + 1;
                    data[segmentOffset + i] = (byte) c;
                    i = i2;
                    while (i < runLimit) {
                        c = string.charAt(i);
                        if (c >= 128) {
                            break;
                        }
                        i2 = i + 1;
                        data[segmentOffset + i] = (byte) c;
                        i = i2;
                    }
                    int runSize = (i + segmentOffset) - tail.limit;
                    tail.limit += runSize;
                    this.size += (long) runSize;
                } else if (c < ItemAnimator.FLAG_MOVED) {
                    writeByte((c >> 6) | 192);
                    writeByte((c & 63) | 128);
                    i++;
                } else if (c < 55296 || c > 57343) {
                    writeByte((c >> 12) | 224);
                    writeByte(((c >> 6) & 63) | 128);
                    writeByte((c & 63) | 128);
                    i++;
                } else {
                    int low;
                    if (i + 1 < endIndex) {
                        low = string.charAt(i + 1);
                    } else {
                        low = 0;
                    }
                    if (c > 56319 || low < 56320 || low > 57343) {
                        writeByte(63);
                        i++;
                    } else {
                        int codePoint = 65536 + (((-55297 & c) << 10) | (-56321 & low));
                        writeByte((codePoint >> 18) | 240);
                        writeByte(((codePoint >> 12) & 63) | 128);
                        writeByte(((codePoint >> 6) & 63) | 128);
                        writeByte((codePoint & 63) | 128);
                        i += 2;
                    }
                }
            }
            return this;
        }
    }

    public final Buffer writeUtf8CodePoint(int codePoint) {
        if (codePoint < 128) {
            writeByte(codePoint);
        } else if (codePoint < ItemAnimator.FLAG_MOVED) {
            writeByte((codePoint >> 6) | 192);
            writeByte((codePoint & 63) | 128);
        } else if (codePoint < 65536) {
            if (codePoint < 55296 || codePoint > 57343) {
                writeByte((codePoint >> 12) | 224);
                writeByte(((codePoint >> 6) & 63) | 128);
                writeByte((codePoint & 63) | 128);
            } else {
                throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(codePoint));
            }
        } else if (codePoint <= 1114111) {
            writeByte((codePoint >> 18) | 240);
            writeByte(((codePoint >> 12) & 63) | 128);
            writeByte(((codePoint >> 6) & 63) | 128);
            writeByte((codePoint & 63) | 128);
        } else {
            throw new IllegalArgumentException("Unexpected code point: " + Integer.toHexString(codePoint));
        }
        return this;
    }

    public final Buffer write(byte[] source) {
        if (source != null) {
            return write(source, 0, source.length);
        }
        throw new IllegalArgumentException("source == null");
    }

    public final Buffer write(byte[] source, int offset, int byteCount) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        Util.checkOffsetAndCount((long) source.length, (long) offset, (long) byteCount);
        int limit = offset + byteCount;
        while (offset < limit) {
            Segment tail = writableSegment(1);
            int toCopy = Math.min(limit - offset, 8192 - tail.limit);
            System.arraycopy(source, offset, tail.data, tail.limit, toCopy);
            offset += toCopy;
            tail.limit += toCopy;
        }
        this.size += (long) byteCount;
        return this;
    }

    public final long writeAll(Source source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        long totalBytesRead = 0;
        while (true) {
            long readCount = source.read(this, 8192);
            if (readCount == -1) {
                return totalBytesRead;
            }
            totalBytesRead += readCount;
        }
    }

    public final Buffer writeByte(int b) {
        Segment tail = writableSegment(1);
        byte[] bArr = tail.data;
        int i = tail.limit;
        tail.limit = i + 1;
        bArr[i] = (byte) b;
        this.size++;
        return this;
    }

    public final Buffer writeShort(int s) {
        Segment tail = writableSegment(2);
        byte[] data = tail.data;
        int i = tail.limit;
        int i2 = i + 1;
        data[i] = (byte) (s >>> 8);
        i = i2 + 1;
        data[i2] = (byte) s;
        tail.limit = i;
        this.size += 2;
        return this;
    }

    public final Buffer writeInt(int i) {
        Segment tail = writableSegment(4);
        byte[] data = tail.data;
        int i2 = tail.limit;
        int i3 = i2 + 1;
        data[i2] = (byte) (i >>> 24);
        i2 = i3 + 1;
        data[i3] = (byte) (i >>> 16);
        i3 = i2 + 1;
        data[i2] = (byte) (i >>> 8);
        i2 = i3 + 1;
        data[i3] = (byte) i;
        tail.limit = i2;
        this.size += 4;
        return this;
    }

    public final Buffer writeDecimalLong(long v) {
        if (v == 0) {
            return writeByte(48);
        }
        boolean negative = false;
        if (v < 0) {
            v = -v;
            if (v < 0) {
                return writeUtf8("-9223372036854775808");
            }
            negative = true;
        }
        int width = v < 100000000 ? v < 10000 ? v < 100 ? v < 10 ? 1 : 2 : v < 1000 ? 3 : 4 : v < 1000000 ? v < 100000 ? 5 : 6 : v < 10000000 ? 7 : 8 : v < 1000000000000L ? v < 10000000000L ? v < 1000000000 ? 9 : 10 : v < 100000000000L ? 11 : 12 : v < 1000000000000000L ? v < 10000000000000L ? 13 : v < 100000000000000L ? 14 : 15 : v < 100000000000000000L ? v < 10000000000000000L ? 16 : 17 : v < 1000000000000000000L ? 18 : 19;
        if (negative) {
            width++;
        }
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int pos = tail.limit + width;
        while (v != 0) {
            pos--;
            data[pos] = DIGITS[(int) (v % 10)];
            v /= 10;
        }
        if (negative) {
            data[pos - 1] = (byte) 45;
        }
        tail.limit += width;
        this.size += (long) width;
        return this;
    }

    public final Buffer writeHexadecimalUnsignedLong(long v) {
        if (v == 0) {
            return writeByte(48);
        }
        int width = (Long.numberOfTrailingZeros(Long.highestOneBit(v)) / 4) + 1;
        Segment tail = writableSegment(width);
        byte[] data = tail.data;
        int start = tail.limit;
        for (int pos = (tail.limit + width) - 1; pos >= start; pos--) {
            data[pos] = DIGITS[(int) (15 & v)];
            v >>>= 4;
        }
        tail.limit += width;
        this.size += (long) width;
        return this;
    }

    final Segment writableSegment(int minimumCapacity) {
        if (minimumCapacity <= 0 || minimumCapacity > 8192) {
            throw new IllegalArgumentException();
        } else if (this.head == null) {
            this.head = SegmentPool.take();
            Segment segment = this.head;
            Segment segment2 = this.head;
            r0 = this.head;
            segment2.prev = r0;
            segment.next = r0;
            return r0;
        } else {
            r0 = this.head.prev;
            if (r0.limit + minimumCapacity > 8192 || !r0.owner) {
                return r0.push(SegmentPool.take());
            }
            return r0;
        }
    }

    public final void write(Buffer source, long byteCount) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        } else if (source == this) {
            throw new IllegalArgumentException("source == this");
        } else {
            Util.checkOffsetAndCount(source.size, 0, byteCount);
            while (byteCount > 0) {
                Segment tail;
                Segment segment;
                Segment segment2;
                if (byteCount < ((long) (source.head.limit - source.head.pos))) {
                    tail = this.head != null ? this.head.prev : null;
                    if (tail != null && tail.owner) {
                        if ((byteCount + ((long) tail.limit)) - ((long) (tail.shared ? 0 : tail.pos)) <= 8192) {
                            source.head.writeTo(tail, (int) byteCount);
                            source.size -= byteCount;
                            this.size += byteCount;
                            return;
                        }
                    }
                    segment = source.head;
                    int i = (int) byteCount;
                    if (i <= 0 || i > segment.limit - segment.pos) {
                        throw new IllegalArgumentException();
                    }
                    if (i >= 1024) {
                        segment2 = new Segment(segment);
                    } else {
                        segment2 = SegmentPool.take();
                        System.arraycopy(segment.data, segment.pos, segment2.data, 0, i);
                    }
                    segment2.limit = segment2.pos + i;
                    segment.pos = i + segment.pos;
                    segment.prev.push(segment2);
                    source.head = segment2;
                }
                Segment segmentToMove = source.head;
                long movedByteCount = (long) (segmentToMove.limit - segmentToMove.pos);
                source.head = segmentToMove.pop();
                if (this.head == null) {
                    this.head = segmentToMove;
                    segment2 = this.head;
                    segment = this.head;
                    Segment segment3 = this.head;
                    segment.prev = segment3;
                    segment2.next = segment3;
                } else {
                    tail = this.head.prev.push(segmentToMove);
                    if (tail.prev == tail) {
                        throw new IllegalStateException();
                    } else if (tail.prev.owner) {
                        int i2 = tail.limit - tail.pos;
                        if (i2 <= (tail.prev.shared ? 0 : tail.prev.pos) + (8192 - tail.prev.limit)) {
                            tail.writeTo(tail.prev, i2);
                            tail.pop();
                            SegmentPool.recycle(tail);
                        }
                    }
                }
                source.size -= movedByteCount;
                this.size += movedByteCount;
                byteCount -= movedByteCount;
            }
        }
    }

    public final long read(Buffer sink, long byteCount) {
        if (sink == null) {
            throw new IllegalArgumentException("sink == null");
        } else if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
        } else if (this.size == 0) {
            return -1;
        } else {
            if (byteCount > this.size) {
                byteCount = this.size;
            }
            sink.write(this, byteCount);
            return byteCount;
        }
    }

    public final long indexOf(byte b) {
        return indexOf(b, 0);
    }

    public final long indexOf(byte b, long fromIndex) {
        if (fromIndex < 0) {
            throw new IllegalArgumentException("fromIndex < 0");
        }
        Segment s = this.head;
        if (s == null) {
            return -1;
        }
        long offset;
        if (this.size - fromIndex >= fromIndex) {
            offset = 0;
            while (true) {
                long nextOffset = offset + ((long) (s.limit - s.pos));
                if (nextOffset >= fromIndex) {
                    break;
                }
                s = s.next;
                offset = nextOffset;
            }
        } else {
            offset = this.size;
            while (offset > fromIndex) {
                s = s.prev;
                offset -= (long) (s.limit - s.pos);
            }
        }
        while (offset < this.size) {
            byte[] data = s.data;
            int limit = s.limit;
            for (int pos = (int) ((((long) s.pos) + fromIndex) - offset); pos < limit; pos++) {
                if (data[pos] == b) {
                    return ((long) (pos - s.pos)) + offset;
                }
            }
            offset += (long) (s.limit - s.pos);
            fromIndex = offset;
            s = s.next;
        }
        return -1;
    }

    public final boolean rangeEquals(long offset, ByteString bytes) {
        int size = bytes.size();
        if (offset < 0 || size < 0 || this.size - offset < ((long) size) || bytes.size() < size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (getByte(((long) i) + offset) != bytes.getByte(i + 0)) {
                return false;
            }
        }
        return true;
    }

    public final void flush() {
    }

    public final void close() {
    }

    public final Timeout timeout() {
        return Timeout.NONE;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Buffer)) {
            return false;
        }
        Buffer that = (Buffer) o;
        if (this.size != that.size) {
            return false;
        }
        if (this.size == 0) {
            return true;
        }
        Segment sa = this.head;
        Segment sb = that.head;
        int posA = sa.pos;
        int posB = sb.pos;
        long pos = 0;
        while (pos < this.size) {
            long count = (long) Math.min(sa.limit - posA, sb.limit - posB);
            int i = 0;
            int posB2 = posB;
            int posA2 = posA;
            while (((long) i) < count) {
                posA = posA2 + 1;
                posB = posB2 + 1;
                if (sa.data[posA2] != sb.data[posB2]) {
                    return false;
                }
                i++;
                posB2 = posB;
                posA2 = posA;
            }
            if (posA2 == sa.limit) {
                sa = sa.next;
                posA = sa.pos;
            } else {
                posA = posA2;
            }
            if (posB2 == sb.limit) {
                sb = sb.next;
                posB = sb.pos;
            } else {
                posB = posB2;
            }
            pos += count;
        }
        return true;
    }

    public final int hashCode() {
        Segment s = this.head;
        if (s == null) {
            return 0;
        }
        int result = 1;
        do {
            for (int pos = s.pos; pos < s.limit; pos++) {
                result = (result * 31) + s.data[pos];
            }
            s = s.next;
        } while (s != this.head);
        return result;
    }

    public final String toString() {
        if (this.size > 2147483647L) {
            throw new IllegalArgumentException("size > Integer.MAX_VALUE: " + this.size);
        }
        ByteString byteString;
        int i = (int) this.size;
        if (i == 0) {
            byteString = ByteString.EMPTY;
        } else {
            byteString = new SegmentedByteString(this, i);
        }
        return byteString.toString();
    }

    public final /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        Buffer buffer = new Buffer();
        if (this.size == 0) {
            return buffer;
        }
        buffer.head = new Segment(this.head);
        Segment segment = buffer.head;
        Segment segment2 = buffer.head;
        Segment segment3 = buffer.head;
        segment2.prev = segment3;
        segment.next = segment3;
        for (segment = this.head.next; segment != this.head; segment = segment.next) {
            buffer.head.prev.push(new Segment(segment));
        }
        buffer.size = this.size;
        return buffer;
    }
}
