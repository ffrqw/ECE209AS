package io.fabric.sdk.android.services.common;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueFile implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(QueueFile.class.getName());
    private final byte[] buffer = new byte[16];
    private int elementCount;
    int fileLength;
    private Element first;
    private Element last;
    private final RandomAccessFile raf;

    public interface ElementReader {
        void read(InputStream inputStream, int i) throws IOException;
    }

    static class Element {
        static final Element NULL = new Element(0, 0);
        final int length;
        final int position;

        Element(int position, int length) {
            this.position = position;
            this.length = length;
        }

        public final String toString() {
            return getClass().getSimpleName() + "[position = " + this.position + ", length = " + this.length + "]";
        }
    }

    private final class ElementInputStream extends InputStream {
        private int position;
        private int remaining;

        private ElementInputStream(Element element) {
            this.position = QueueFile.this.wrapPosition(element.position + 4);
            this.remaining = element.length;
        }

        public final int read(byte[] buffer, int offset, int length) throws IOException {
            QueueFile.nonNull(buffer, "buffer");
            if ((offset | length) < 0 || length > buffer.length - offset) {
                throw new ArrayIndexOutOfBoundsException();
            } else if (this.remaining <= 0) {
                return -1;
            } else {
                if (length > this.remaining) {
                    length = this.remaining;
                }
                QueueFile.this.ringRead(this.position, buffer, offset, length);
                this.position = QueueFile.this.wrapPosition(this.position + length);
                this.remaining -= length;
                return length;
            }
        }

        public final int read() throws IOException {
            if (this.remaining == 0) {
                return -1;
            }
            QueueFile.this.raf.seek((long) this.position);
            int b = QueueFile.this.raf.read();
            this.position = QueueFile.this.wrapPosition(this.position + 1);
            this.remaining--;
            return b;
        }
    }

    public QueueFile(File file) throws IOException {
        if (!file.exists()) {
            File file2 = new File(file.getPath() + ".tmp");
            RandomAccessFile open = open(file2);
            try {
                open.setLength(4096);
                open.seek(0);
                byte[] bArr = new byte[16];
                writeInts(bArr, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, 0, 0, 0);
                open.write(bArr);
                if (!file2.renameTo(file)) {
                    throw new IOException("Rename failed!");
                }
            } finally {
                open.close();
            }
        }
        this.raf = open(file);
        this.raf.seek(0);
        this.raf.readFully(this.buffer);
        this.fileLength = readInt(this.buffer, 0);
        if (((long) this.fileLength) > this.raf.length()) {
            throw new IOException("File is truncated. Expected length: " + this.fileLength + ", Actual length: " + this.raf.length());
        }
        this.elementCount = readInt(this.buffer, 4);
        int readInt = readInt(this.buffer, 8);
        int readInt2 = readInt(this.buffer, 12);
        this.first = readElement(readInt);
        this.last = readElement(readInt2);
    }

    private static void writeInt(byte[] buffer, int offset, int value) {
        buffer[offset] = value >> 24;
        buffer[offset + 1] = (byte) (value >> 16);
        buffer[offset + 2] = (byte) (value >> 8);
        buffer[offset + 3] = (byte) value;
    }

    private static void writeInts(byte[] buffer, int... values) {
        int offset = 0;
        for (int i = 0; i < 4; i++) {
            writeInt(buffer, offset, values[i]);
            offset += 4;
        }
    }

    private static int readInt(byte[] buffer, int offset) {
        return ((((buffer[offset] & 255) << 24) + ((buffer[offset + 1] & 255) << 16)) + ((buffer[offset + 2] & 255) << 8)) + (buffer[offset + 3] & 255);
    }

    private void writeHeader(int fileLength, int elementCount, int firstPosition, int lastPosition) throws IOException {
        writeInts(this.buffer, fileLength, elementCount, firstPosition, lastPosition);
        this.raf.seek(0);
        this.raf.write(this.buffer);
    }

    private Element readElement(int position) throws IOException {
        if (position == 0) {
            return Element.NULL;
        }
        this.raf.seek((long) position);
        return new Element(position, this.raf.readInt());
    }

    private static RandomAccessFile open(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "rwd");
    }

    private int wrapPosition(int position) {
        return position < this.fileLength ? position : (position + 16) - this.fileLength;
    }

    private void ringWrite(int position, byte[] buffer, int offset, int count) throws IOException {
        position = wrapPosition(position);
        if (position + count <= this.fileLength) {
            this.raf.seek((long) position);
            this.raf.write(buffer, offset, count);
            return;
        }
        int beforeEof = this.fileLength - position;
        this.raf.seek((long) position);
        this.raf.write(buffer, offset, beforeEof);
        this.raf.seek(16);
        this.raf.write(buffer, offset + beforeEof, count - beforeEof);
    }

    private void ringRead(int position, byte[] buffer, int offset, int count) throws IOException {
        position = wrapPosition(position);
        if (position + count <= this.fileLength) {
            this.raf.seek((long) position);
            this.raf.readFully(buffer, offset, count);
            return;
        }
        int beforeEof = this.fileLength - position;
        this.raf.seek((long) position);
        this.raf.readFully(buffer, offset, beforeEof);
        this.raf.seek(16);
        this.raf.readFully(buffer, offset + beforeEof, count - beforeEof);
    }

    public final void add(byte[] data) throws IOException {
        add(data, 0, data.length);
    }

    private synchronized void add(byte[] data, int offset, int count) throws IOException {
        nonNull(data, "buffer");
        if ((count | 0) < 0 || count > data.length) {
            throw new IndexOutOfBoundsException();
        }
        int position;
        expandIfNecessary(count);
        boolean wasEmpty = isEmpty();
        if (wasEmpty) {
            position = 16;
        } else {
            position = wrapPosition((this.last.position + 4) + this.last.length);
        }
        Element newLast = new Element(position, count);
        writeInt(this.buffer, 0, count);
        ringWrite(newLast.position, this.buffer, 0, 4);
        ringWrite(newLast.position + 4, data, 0, count);
        writeHeader(this.fileLength, this.elementCount + 1, wasEmpty ? newLast.position : this.first.position, newLast.position);
        this.last = newLast;
        this.elementCount++;
        if (wasEmpty) {
            this.first = this.last;
        }
    }

    public final int usedBytes() {
        if (this.elementCount == 0) {
            return 16;
        }
        if (this.last.position >= this.first.position) {
            return (((this.last.position - this.first.position) + 4) + this.last.length) + 16;
        }
        return (((this.last.position + 4) + this.last.length) + this.fileLength) - this.first.position;
    }

    public final synchronized boolean isEmpty() {
        return this.elementCount == 0;
    }

    private void expandIfNecessary(int dataLength) throws IOException {
        int elementLength = dataLength + 4;
        int remainingBytes = this.fileLength - usedBytes();
        if (remainingBytes < elementLength) {
            int newLength;
            int previousLength = this.fileLength;
            do {
                remainingBytes += previousLength;
                newLength = previousLength << 1;
                previousLength = newLength;
            } while (remainingBytes < elementLength);
            setLength(newLength);
            int endOfLastElement = wrapPosition((this.last.position + 4) + this.last.length);
            if (endOfLastElement < this.first.position) {
                FileChannel channel = this.raf.getChannel();
                channel.position((long) this.fileLength);
                int count = endOfLastElement - 4;
                if (channel.transferTo(16, (long) count, channel) != ((long) count)) {
                    throw new AssertionError("Copied insufficient number of bytes!");
                }
            }
            if (this.last.position < this.first.position) {
                int newLastPosition = (this.fileLength + this.last.position) - 16;
                writeHeader(newLength, this.elementCount, this.first.position, newLastPosition);
                this.last = new Element(newLastPosition, this.last.length);
            } else {
                writeHeader(newLength, this.elementCount, this.first.position, this.last.position);
            }
            this.fileLength = newLength;
        }
    }

    private void setLength(int newLength) throws IOException {
        this.raf.setLength((long) newLength);
        this.raf.getChannel().force(true);
    }

    public final synchronized void forEach(ElementReader reader) throws IOException {
        int position = this.first.position;
        for (int i = 0; i < this.elementCount; i++) {
            Element current = readElement(position);
            reader.read(new ElementInputStream(current), current.length);
            position = wrapPosition((current.position + 4) + current.length);
        }
    }

    private static <T> T nonNull(T t, String name) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(name);
    }

    public final synchronized void remove() throws IOException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else if (this.elementCount == 1) {
            clear();
        } else {
            int newFirstPosition = wrapPosition((this.first.position + 4) + this.first.length);
            ringRead(newFirstPosition, this.buffer, 0, 4);
            int length = readInt(this.buffer, 0);
            writeHeader(this.fileLength, this.elementCount - 1, newFirstPosition, this.last.position);
            this.elementCount--;
            this.first = new Element(newFirstPosition, length);
        }
    }

    private synchronized void clear() throws IOException {
        writeHeader(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, 0, 0, 0);
        this.elementCount = 0;
        this.first = Element.NULL;
        this.last = Element.NULL;
        if (this.fileLength > ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT) {
            setLength(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
        }
        this.fileLength = ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT;
    }

    public synchronized void close() throws IOException {
        this.raf.close();
    }

    public final boolean hasSpaceFor(int dataSizeBytes, int maxSizeBytes) {
        return (usedBytes() + 4) + dataSizeBytes <= maxSizeBytes;
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append('[');
        builder.append("fileLength=").append(this.fileLength);
        builder.append(", size=").append(this.elementCount);
        builder.append(", first=").append(this.first);
        builder.append(", last=").append(this.last);
        builder.append(", element lengths=[");
        try {
            forEach(new ElementReader() {
                boolean first = true;

                public final void read(InputStream in, int length) throws IOException {
                    if (this.first) {
                        this.first = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(length);
                }
            });
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "read error", e);
        }
        builder.append("]]");
        return builder.toString();
    }
}
