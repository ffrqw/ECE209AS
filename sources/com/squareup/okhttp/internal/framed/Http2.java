package com.squareup.okhttp.internal.framed;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import com.squareup.okhttp.internal.framed.FrameReader.Handler;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

public final class Http2 implements Variant {
    private static final ByteString CONNECTION_PREFACE = ByteString.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");
    private static final Logger logger = Logger.getLogger(FrameLogger.class.getName());

    static final class ContinuationSource implements Source {
        byte flags;
        int left;
        int length;
        short padding;
        private final BufferedSource source;
        int streamId;

        public ContinuationSource(BufferedSource source) {
            this.source = source;
        }

        public final long read(Buffer sink, long byteCount) throws IOException {
            while (this.left == 0) {
                this.source.skip((long) this.padding);
                this.padding = (short) 0;
                if ((this.flags & 4) != 0) {
                    return -1;
                }
                int i = this.streamId;
                int access$300 = ((((this.source.readByte() & 255) << 16) | ((this.source.readByte() & 255) << 8)) | (this.source.readByte() & 255));
                this.left = access$300;
                this.length = access$300;
                byte readByte = (byte) this.source.readByte();
                this.flags = (byte) this.source.readByte();
                if (Http2.logger.isLoggable(Level.FINE)) {
                    Http2.logger.fine(FrameLogger.formatHeader(true, this.streamId, this.length, readByte, this.flags));
                }
                this.streamId = this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                if (readByte != (byte) 9) {
                    throw Http2.ioException("%s != TYPE_CONTINUATION", Byte.valueOf(readByte));
                } else if (this.streamId != i) {
                    throw Http2.ioException("TYPE_CONTINUATION streamId changed", new Object[0]);
                }
            }
            long read = this.source.read(sink, Math.min(byteCount, (long) this.left));
            if (read == -1) {
                return -1;
            }
            this.left = (int) (((long) this.left) - read);
            return read;
        }

        public final Timeout timeout() {
            return this.source.timeout();
        }

        public final void close() throws IOException {
        }
    }

    static final class FrameLogger {
        private static final String[] BINARY = new String[256];
        private static final String[] FLAGS = new String[64];
        private static final String[] TYPES = new String[]{"DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION"};

        FrameLogger() {
        }

        static String formatHeader(boolean inbound, int streamId, int length, byte type, byte flags) {
            String formattedType;
            String formattedFlags;
            if (type < (byte) 10) {
                formattedType = TYPES[type];
            } else {
                formattedType = String.format("0x%02x", new Object[]{Byte.valueOf(type)});
            }
            if (flags != (byte) 0) {
                switch (type) {
                    case (byte) 2:
                    case (byte) 3:
                    case (byte) 7:
                    case (byte) 8:
                        formattedFlags = BINARY[flags];
                        break;
                    case (byte) 4:
                    case (byte) 6:
                        if (flags != (byte) 1) {
                            formattedFlags = BINARY[flags];
                            break;
                        }
                        formattedFlags = "ACK";
                        break;
                    default:
                        formattedFlags = flags < (byte) 64 ? FLAGS[flags] : BINARY[flags];
                        if (type != (byte) 5 || (flags & 4) == 0) {
                            if (type == (byte) 0 && (flags & 32) != 0) {
                                formattedFlags = formattedFlags.replace("PRIORITY", "COMPRESSED");
                                break;
                            }
                        }
                        formattedFlags = formattedFlags.replace("HEADERS", "PUSH_PROMISE");
                        break;
                        break;
                }
            }
            formattedFlags = "";
            String str = "%s 0x%08x %5d %-13s %s";
            Object[] objArr = new Object[5];
            objArr[0] = inbound ? "<<" : ">>";
            objArr[1] = Integer.valueOf(streamId);
            objArr[2] = Integer.valueOf(length);
            objArr[3] = formattedType;
            objArr[4] = formattedFlags;
            return String.format(str, objArr);
        }

        static {
            int i;
            int i2;
            for (i = 0; i < 256; i++) {
                BINARY[i] = String.format("%8s", new Object[]{Integer.toBinaryString(i)}).replace(' ', '0');
            }
            FLAGS[0] = "";
            FLAGS[1] = "END_STREAM";
            int[] prefixFlags = new int[]{1};
            FLAGS[8] = "PADDED";
            for (i2 = 0; i2 <= 0; i2++) {
                int prefixFlag = prefixFlags[0];
                FLAGS[prefixFlag | 8] = FLAGS[prefixFlag] + "|PADDED";
            }
            FLAGS[4] = "END_HEADERS";
            FLAGS[32] = "PRIORITY";
            FLAGS[36] = "END_HEADERS|PRIORITY";
            int[] frameFlags = new int[]{4, 32, 36};
            for (int i3 = 0; i3 < 3; i3++) {
                int frameFlag = frameFlags[i3];
                for (i2 = 0; i2 <= 0; i2++) {
                    prefixFlag = prefixFlags[i2];
                    FLAGS[prefixFlag | frameFlag] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag];
                    FLAGS[(prefixFlag | frameFlag) | 8] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag] + "|PADDED";
                }
            }
            for (i = 0; i < 64; i++) {
                if (FLAGS[i] == null) {
                    FLAGS[i] = BINARY[i];
                }
            }
        }
    }

    static final class Reader implements FrameReader {
        private final boolean client;
        private final ContinuationSource continuation = new ContinuationSource(this.source);
        final Reader hpackReader = new Reader(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, this.continuation);
        private final BufferedSource source;

        Reader(BufferedSource source, int headerTableSize, boolean client) {
            this.source = source;
            this.client = client;
        }

        public final void readConnectionPreface() throws IOException {
            if (!this.client) {
                ByteString connectionPreface = this.source.readByteString((long) Http2.CONNECTION_PREFACE.size());
                if (Http2.logger.isLoggable(Level.FINE)) {
                    Http2.logger.fine(String.format("<< CONNECTION %s", new Object[]{connectionPreface.hex()}));
                }
                if (!Http2.CONNECTION_PREFACE.equals(connectionPreface)) {
                    throw Http2.ioException("Expected a connection header but was %s", connectionPreface.utf8());
                }
            }
        }

        public final boolean nextFrame(Handler handler) throws IOException {
            boolean z = false;
            try {
                this.source.require(9);
                int length = ((((this.source.readByte() & 255) << 16) | ((this.source.readByte() & 255) << 8)) | (this.source.readByte() & 255));
                if (length < 0 || length > 16384) {
                    throw Http2.ioException("FRAME_SIZE_ERROR: %s", Integer.valueOf(length));
                }
                byte type = (byte) this.source.readByte();
                byte flags = (byte) this.source.readByte();
                int streamId = this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                if (Http2.logger.isLoggable(Level.FINE)) {
                    Http2.logger.fine(FrameLogger.formatHeader(true, streamId, length, type, flags));
                }
                boolean z2;
                short readByte;
                int readShort;
                int i;
                int readInt;
                switch (type) {
                    case (byte) 0:
                        boolean z3;
                        if ((flags & 1) != 0) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        if ((flags & 32) != 0) {
                            z3 = true;
                        } else {
                            z3 = false;
                        }
                        if (z3) {
                            throw Http2.ioException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA", new Object[0]);
                        }
                        if ((flags & 8) != 0) {
                            readByte = (short) (this.source.readByte() & 255);
                        }
                        handler.data(z2, streamId, this.source, Http2.access$400(length, flags, readByte));
                        this.source.skip((long) readByte);
                        return true;
                    case (byte) 1:
                        if (streamId == 0) {
                            throw Http2.ioException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0", new Object[0]);
                        }
                        short readByte2;
                        if ((flags & 1) != 0) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        if ((flags & 8) != 0) {
                            readByte2 = (short) (this.source.readByte() & 255);
                        } else {
                            readByte2 = (short) 0;
                        }
                        if ((flags & 32) != 0) {
                            readPriority(handler, streamId);
                            length -= 5;
                        }
                        handler.headers$37c2d766(false, z2, streamId, readHeaderBlock(Http2.access$400(length, flags, readByte2), readByte2, flags, streamId), HeadersMode.HTTP_20_HEADERS);
                        return true;
                    case (byte) 2:
                        if (length != 5) {
                            throw Http2.ioException("TYPE_PRIORITY length: %d != 5", Integer.valueOf(length));
                        } else if (streamId == 0) {
                            throw Http2.ioException("TYPE_PRIORITY streamId == 0", new Object[0]);
                        } else {
                            readPriority(handler, streamId);
                            return true;
                        }
                    case (byte) 3:
                        if (length != 4) {
                            throw Http2.ioException("TYPE_RST_STREAM length: %d != 4", Integer.valueOf(length));
                        } else if (streamId == 0) {
                            throw Http2.ioException("TYPE_RST_STREAM streamId == 0", new Object[0]);
                        } else {
                            ErrorCode fromHttp2 = ErrorCode.fromHttp2(this.source.readInt());
                            if (fromHttp2 == null) {
                                throw Http2.ioException("TYPE_RST_STREAM unexpected error code: %d", Integer.valueOf(readShort));
                            }
                            handler.rstStream(streamId, fromHttp2);
                            return true;
                        }
                    case (byte) 4:
                        if (streamId != 0) {
                            throw Http2.ioException("TYPE_SETTINGS streamId != 0", new Object[0]);
                        } else if ((flags & 1) != 0) {
                            if (length == 0) {
                                return true;
                            }
                            throw Http2.ioException("FRAME_SIZE_ERROR ack frame should be empty!", new Object[0]);
                        } else if (length % 6 != 0) {
                            throw Http2.ioException("TYPE_SETTINGS length %% 6 != 0: %s", Integer.valueOf(length));
                        } else {
                            Settings settings = new Settings();
                            for (i = 0; i < length; i += 6) {
                                readShort = this.source.readShort();
                                int readInt2 = this.source.readInt();
                                switch (readShort) {
                                    case 1:
                                    case 6:
                                        break;
                                    case 2:
                                        if (!(readInt2 == 0 || readInt2 == 1)) {
                                            throw Http2.ioException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1", new Object[0]);
                                        }
                                    case 3:
                                        readShort = 4;
                                        break;
                                    case 4:
                                        readShort = 7;
                                        if (readInt2 >= 0) {
                                            break;
                                        }
                                        throw Http2.ioException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1", new Object[0]);
                                    case 5:
                                        if (readInt2 >= 16384 && readInt2 <= 16777215) {
                                            break;
                                        }
                                        throw Http2.ioException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: %s", Integer.valueOf(readInt2));
                                    default:
                                        throw Http2.ioException("PROTOCOL_ERROR invalid settings id: %s", Short.valueOf(readShort));
                                }
                                settings.set(readShort, 0, readInt2);
                            }
                            handler.settings(false, settings);
                            if (settings.getHeaderTableSize() < 0) {
                                return true;
                            }
                            this.hpackReader.headerTableSizeSetting(settings.getHeaderTableSize());
                            return true;
                        }
                    case (byte) 5:
                        if (streamId == 0) {
                            throw Http2.ioException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0", new Object[0]);
                        }
                        if ((flags & 8) != 0) {
                            readByte = (short) (this.source.readByte() & 255);
                        }
                        handler.pushPromise$16014a7a(this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, readHeaderBlock(Http2.access$400(length - 4, flags, readByte), readByte, flags, streamId));
                        return true;
                    case (byte) 6:
                        if (length != 8) {
                            throw Http2.ioException("TYPE_PING length != 8: %s", Integer.valueOf(length));
                        } else if (streamId != 0) {
                            throw Http2.ioException("TYPE_PING streamId != 0", new Object[0]);
                        } else {
                            readShort = this.source.readInt();
                            readInt = this.source.readInt();
                            if ((flags & 1) != 0) {
                                z = true;
                            }
                            handler.ping(z, readShort, readInt);
                            return true;
                        }
                    case (byte) 7:
                        if (length < 8) {
                            throw Http2.ioException("TYPE_GOAWAY length < 8: %s", Integer.valueOf(length));
                        } else if (streamId != 0) {
                            throw Http2.ioException("TYPE_GOAWAY streamId != 0", new Object[0]);
                        } else {
                            readInt = this.source.readInt();
                            i = length - 8;
                            if (ErrorCode.fromHttp2(this.source.readInt()) == null) {
                                throw Http2.ioException("TYPE_GOAWAY unexpected error code: %d", Integer.valueOf(this.source.readInt()));
                            }
                            ByteString byteString = ByteString.EMPTY;
                            if (i > 0) {
                                byteString = this.source.readByteString((long) i);
                            }
                            handler.goAway$4b4c5c6b(readInt, byteString);
                            return true;
                        }
                    case (byte) 8:
                        if (length != 4) {
                            throw Http2.ioException("TYPE_WINDOW_UPDATE length !=4: %s", Integer.valueOf(length));
                        }
                        long readInt3 = ((long) this.source.readInt()) & 2147483647L;
                        if (readInt3 == 0) {
                            throw Http2.ioException("windowSizeIncrement was 0", Long.valueOf(readInt3));
                        }
                        handler.windowUpdate(streamId, readInt3);
                        return true;
                    default:
                        this.source.skip((long) length);
                        return true;
                }
            } catch (IOException e) {
                return false;
            }
        }

        private List<Header> readHeaderBlock(int length, short padding, byte flags, int streamId) throws IOException {
            ContinuationSource continuationSource = this.continuation;
            this.continuation.left = length;
            continuationSource.length = length;
            this.continuation.padding = padding;
            this.continuation.flags = flags;
            this.continuation.streamId = streamId;
            this.hpackReader.readHeaders();
            return this.hpackReader.getAndResetHeaderList();
        }

        private void readPriority(Handler handler, int streamId) throws IOException {
            this.source.readInt();
            this.source.readByte();
        }

        public final void close() throws IOException {
            this.source.close();
        }
    }

    static final class Writer implements FrameWriter {
        private final boolean client;
        private boolean closed;
        private final Buffer hpackBuffer = new Buffer();
        private final Writer hpackWriter = new Writer(this.hpackBuffer);
        private int maxFrameSize = 16384;
        private final BufferedSink sink;

        Writer(BufferedSink sink, boolean client) {
            this.sink = sink;
            this.client = client;
        }

        public final synchronized void flush() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            this.sink.flush();
        }

        public final synchronized void ackSettings(Settings peerSettings) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            this.maxFrameSize = peerSettings.getMaxFrameSize(this.maxFrameSize);
            frameHeader(0, 0, (byte) 4, (byte) 1);
            this.sink.flush();
        }

        public final synchronized void connectionPreface() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            } else if (this.client) {
                if (Http2.logger.isLoggable(Level.FINE)) {
                    Http2.logger.fine(String.format(">> CONNECTION %s", new Object[]{Http2.CONNECTION_PREFACE.hex()}));
                }
                this.sink.write(Http2.CONNECTION_PREFACE.toByteArray());
                this.sink.flush();
            }
        }

        public final synchronized void synStream(boolean outFinished, boolean inFinished, int streamId, int associatedStreamId, List<Header> headerBlock) throws IOException {
            if (inFinished) {
                throw new UnsupportedOperationException();
            } else if (this.closed) {
                throw new IOException("closed");
            } else if (this.closed) {
                throw new IOException("closed");
            } else {
                this.hpackWriter.writeHeaders(headerBlock);
                long size = this.hpackBuffer.size();
                int min = (int) Math.min((long) this.maxFrameSize, size);
                byte b = size == ((long) min) ? (byte) 4 : (byte) 0;
                if (outFinished) {
                    b = (byte) (b | 1);
                }
                frameHeader(streamId, min, (byte) 1, b);
                this.sink.write(this.hpackBuffer, (long) min);
                if (size > ((long) min)) {
                    writeContinuationFrames(streamId, size - ((long) min));
                }
            }
        }

        private void writeContinuationFrames(int streamId, long byteCount) throws IOException {
            while (byteCount > 0) {
                int length = (int) Math.min((long) this.maxFrameSize, byteCount);
                byteCount -= (long) length;
                frameHeader(streamId, length, (byte) 9, byteCount == 0 ? (byte) 4 : (byte) 0);
                this.sink.write(this.hpackBuffer, (long) length);
            }
        }

        public final synchronized void rstStream(int streamId, ErrorCode errorCode) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            } else if (errorCode.httpCode == -1) {
                throw new IllegalArgumentException();
            } else {
                frameHeader(streamId, 4, (byte) 3, (byte) 0);
                this.sink.writeInt(errorCode.httpCode);
                this.sink.flush();
            }
        }

        public final int maxDataLength() {
            return this.maxFrameSize;
        }

        public final synchronized void data(boolean outFinished, int streamId, Buffer source, int byteCount) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            byte flags = (byte) 0;
            if (outFinished) {
                flags = (byte) 1;
            }
            frameHeader(streamId, byteCount, (byte) 0, flags);
            if (byteCount > 0) {
                this.sink.write(source, (long) byteCount);
            }
        }

        public final synchronized void settings(Settings settings) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            frameHeader(0, settings.size() * 6, (byte) 4, (byte) 0);
            for (int i = 0; i < 10; i++) {
                if (settings.isSet(i)) {
                    int id = i;
                    if (i == 4) {
                        id = 3;
                    } else if (id == 7) {
                        id = 4;
                    }
                    this.sink.writeShort(id);
                    this.sink.writeInt(settings.get(i));
                }
            }
            this.sink.flush();
        }

        public final synchronized void ping(boolean ack, int payload1, int payload2) throws IOException {
            byte flags = (byte) 0;
            synchronized (this) {
                if (this.closed) {
                    throw new IOException("closed");
                }
                if (ack) {
                    flags = (byte) 1;
                }
                frameHeader(0, 8, (byte) 6, flags);
                this.sink.writeInt(payload1);
                this.sink.writeInt(payload2);
                this.sink.flush();
            }
        }

        public final synchronized void goAway(int lastGoodStreamId, ErrorCode errorCode, byte[] debugData) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            } else if (errorCode.httpCode == -1) {
                throw Http2.access$500("errorCode.httpCode == -1", new Object[0]);
            } else {
                frameHeader(0, debugData.length + 8, (byte) 7, (byte) 0);
                this.sink.writeInt(lastGoodStreamId);
                this.sink.writeInt(errorCode.httpCode);
                if (debugData.length > 0) {
                    this.sink.write(debugData);
                }
                this.sink.flush();
            }
        }

        public final synchronized void windowUpdate(int streamId, long windowSizeIncrement) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            } else if (windowSizeIncrement == 0 || windowSizeIncrement > 2147483647L) {
                throw Http2.access$500("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: %s", new Object[]{Long.valueOf(windowSizeIncrement)});
            } else {
                frameHeader(streamId, 4, (byte) 8, (byte) 0);
                this.sink.writeInt((int) windowSizeIncrement);
                this.sink.flush();
            }
        }

        public final synchronized void close() throws IOException {
            this.closed = true;
            this.sink.close();
        }

        private void frameHeader(int streamId, int length, byte type, byte flags) throws IOException {
            if (Http2.logger.isLoggable(Level.FINE)) {
                Http2.logger.fine(FrameLogger.formatHeader(false, streamId, length, type, flags));
            }
            if (length > this.maxFrameSize) {
                throw Http2.access$500("FRAME_SIZE_ERROR length > %d: %d", new Object[]{Integer.valueOf(this.maxFrameSize), Integer.valueOf(length)});
            } else if ((Integer.MIN_VALUE & streamId) != 0) {
                throw Http2.access$500("reserved bit set: %s", new Object[]{Integer.valueOf(streamId)});
            } else {
                Http2.access$600(this.sink, length);
                this.sink.writeByte(type & 255);
                this.sink.writeByte(flags & 255);
                this.sink.writeInt(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED & streamId);
            }
        }
    }

    public final FrameReader newReader(BufferedSource source, boolean client) {
        return new Reader(source, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, client);
    }

    public final FrameWriter newWriter(BufferedSink sink, boolean client) {
        return new Writer(sink, client);
    }

    private static IOException ioException(String message, Object... args) throws IOException {
        throw new IOException(String.format(message, args));
    }

    static /* synthetic */ int access$400(int x0, byte x1, short x2) throws IOException {
        if ((x1 & 8) != 0) {
            short x02 = x0 - 1;
        }
        if (x2 <= x02) {
            return (short) (x02 - x2);
        }
        throw ioException("PROTOCOL_ERROR padding %s > remaining length %s", Short.valueOf(x2), Integer.valueOf(x02));
    }

    static /* synthetic */ IllegalArgumentException access$500(String x0, Object[] x1) {
        throw new IllegalArgumentException(String.format(x0, x1));
    }

    static /* synthetic */ void access$600(BufferedSink x0, int x1) throws IOException {
        x0.writeByte((x1 >>> 16) & 255);
        x0.writeByte((x1 >>> 8) & 255);
        x0.writeByte(x1 & 255);
    }
}
