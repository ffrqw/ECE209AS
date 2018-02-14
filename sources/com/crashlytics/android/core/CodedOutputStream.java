package com.crashlytics.android.core;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class CodedOutputStream implements Flushable {
    private final byte[] buffer;
    private final int limit;
    private final OutputStream output;
    private int position = 0;

    static class OutOfSpaceException extends IOException {
        OutOfSpaceException() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }
    }

    private CodedOutputStream(OutputStream output, byte[] buffer) {
        this.output = output;
        this.buffer = buffer;
        this.limit = buffer.length;
    }

    public final void writeFloat(int fieldNumber, float value) throws IOException {
        writeTag(1, 5);
        int floatToRawIntBits = Float.floatToRawIntBits(value);
        writeRawByte(floatToRawIntBits & 255);
        writeRawByte((floatToRawIntBits >> 8) & 255);
        writeRawByte((floatToRawIntBits >> 16) & 255);
        writeRawByte(floatToRawIntBits >>> 24);
    }

    public final void writeUInt64(int fieldNumber, long value) throws IOException {
        writeTag(fieldNumber, 0);
        writeRawVarint64(value);
    }

    public final void writeBool(int fieldNumber, boolean value) throws IOException {
        int i = 0;
        writeTag(fieldNumber, 0);
        if (value) {
            i = 1;
        }
        writeRawByte(i);
    }

    public final void writeBytes(int fieldNumber, ByteString value) throws IOException {
        writeTag(fieldNumber, 2);
        writeRawVarint32(value.size());
        int size = value.size();
        if (this.limit - this.position >= size) {
            value.copyTo(this.buffer, 0, this.position, size);
            this.position = size + this.position;
            return;
        }
        int i = this.limit - this.position;
        value.copyTo(this.buffer, 0, this.position, i);
        int i2 = i + 0;
        size -= i;
        this.position = this.limit;
        refreshBuffer();
        if (size <= this.limit) {
            value.copyTo(this.buffer, i2, 0, size);
            this.position = size;
            return;
        }
        InputStream newInput = value.newInput();
        if (((long) i2) != newInput.skip((long) i2)) {
            throw new IllegalStateException("Skip failed.");
        }
        while (size > 0) {
            i2 = Math.min(size, this.limit);
            int read = newInput.read(this.buffer, 0, i2);
            if (read != i2) {
                throw new IllegalStateException("Read failed.");
            }
            this.output.write(this.buffer, 0, read);
            size -= read;
        }
    }

    public final void writeUInt32(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        writeRawVarint32(value);
    }

    public final void writeEnum(int fieldNumber, int value) throws IOException {
        writeTag(fieldNumber, 0);
        if (value >= 0) {
            writeRawVarint32(value);
        } else {
            writeRawVarint64((long) value);
        }
    }

    public final void writeSInt32(int fieldNumber, int value) throws IOException {
        writeTag(2, 0);
        writeRawVarint32(encodeZigZag32(value));
    }

    public static int computeFloatSize(int fieldNumber, float value) {
        return computeTagSize(1) + 4;
    }

    public static int computeUInt64Size(int fieldNumber, long value) {
        int i;
        int computeTagSize = computeTagSize(fieldNumber);
        if ((-128 & value) == 0) {
            i = 1;
        } else if ((-16384 & value) == 0) {
            i = 2;
        } else if ((-2097152 & value) == 0) {
            i = 3;
        } else if ((-268435456 & value) == 0) {
            i = 4;
        } else if ((-34359738368L & value) == 0) {
            i = 5;
        } else if ((-4398046511104L & value) == 0) {
            i = 6;
        } else if ((-562949953421312L & value) == 0) {
            i = 7;
        } else if ((-72057594037927936L & value) == 0) {
            i = 8;
        } else if ((Long.MIN_VALUE & value) == 0) {
            i = 9;
        } else {
            i = 10;
        }
        return i + computeTagSize;
    }

    public static int computeBoolSize(int fieldNumber, boolean value) {
        return computeTagSize(fieldNumber) + 1;
    }

    public static int computeBytesSize(int fieldNumber, ByteString value) {
        return computeTagSize(fieldNumber) + (computeRawVarint32Size(value.size()) + value.size());
    }

    public static int computeUInt32Size(int fieldNumber, int value) {
        return computeTagSize(fieldNumber) + computeRawVarint32Size(value);
    }

    public static int computeEnumSize(int fieldNumber, int value) {
        int computeRawVarint32Size;
        int computeTagSize = computeTagSize(fieldNumber);
        if (value >= 0) {
            computeRawVarint32Size = computeRawVarint32Size(value);
        } else {
            computeRawVarint32Size = 10;
        }
        return computeRawVarint32Size + computeTagSize;
    }

    public static int computeSInt32Size(int fieldNumber, int value) {
        return computeTagSize(2) + computeRawVarint32Size(encodeZigZag32(value));
    }

    private void refreshBuffer() throws IOException {
        if (this.output == null) {
            throw new OutOfSpaceException();
        }
        this.output.write(this.buffer, 0, this.position);
        this.position = 0;
    }

    public final void flush() throws IOException {
        if (this.output != null) {
            refreshBuffer();
        }
    }

    private void writeRawByte(int value) throws IOException {
        byte b = (byte) value;
        if (this.position == this.limit) {
            refreshBuffer();
        }
        byte[] bArr = this.buffer;
        int i = this.position;
        this.position = i + 1;
        bArr[i] = b;
    }

    public final void writeRawBytes(byte[] value) throws IOException {
        int length = value.length;
        if (this.limit - this.position >= length) {
            System.arraycopy(value, 0, this.buffer, this.position, length);
            this.position = length + this.position;
            return;
        }
        int i = this.limit - this.position;
        System.arraycopy(value, 0, this.buffer, this.position, i);
        int i2 = i + 0;
        length -= i;
        this.position = this.limit;
        refreshBuffer();
        if (length <= this.limit) {
            System.arraycopy(value, i2, this.buffer, 0, length);
            this.position = length;
            return;
        }
        this.output.write(value, i2, length);
    }

    public final void writeTag(int fieldNumber, int wireType) throws IOException {
        writeRawVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }

    public static int computeTagSize(int fieldNumber) {
        return computeRawVarint32Size(WireFormat.makeTag(fieldNumber, 0));
    }

    public final void writeRawVarint32(int value) throws IOException {
        while ((value & -128) != 0) {
            writeRawByte((value & 127) | 128);
            value >>>= 7;
        }
        writeRawByte(value);
    }

    public static int computeRawVarint32Size(int value) {
        if ((value & -128) == 0) {
            return 1;
        }
        if ((value & -16384) == 0) {
            return 2;
        }
        if ((-2097152 & value) == 0) {
            return 3;
        }
        if ((-268435456 & value) == 0) {
            return 4;
        }
        return 5;
    }

    private void writeRawVarint64(long value) throws IOException {
        while ((-128 & value) != 0) {
            writeRawByte((((int) value) & 127) | 128);
            value >>>= 7;
        }
        writeRawByte((int) value);
    }

    private static int encodeZigZag32(int n) {
        return (n << 1) ^ (n >> 31);
    }

    public static CodedOutputStream newInstance(OutputStream output) {
        return new CodedOutputStream(output, new byte[ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT]);
    }
}
