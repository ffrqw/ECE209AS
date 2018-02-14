package okhttp3.internal.http2;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

final class Http2Reader implements Closeable {
    static final Logger logger = Logger.getLogger(Http2.class.getName());
    private final boolean client;
    private final ContinuationSource continuation = new ContinuationSource(this.source);
    final Reader hpackReader = new Reader(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, this.continuation);
    private final BufferedSource source;

    interface Handler {
        void data(boolean z, int i, BufferedSource bufferedSource, int i2) throws IOException;

        void goAway$4b802bc(int i, ByteString byteString);

        void headers$64c3d190(boolean z, int i, List<Header> list);

        void ping(boolean z, int i, int i2);

        void pushPromise$16014a7a(int i, List<Header> list) throws IOException;

        void rstStream(int i, ErrorCode errorCode);

        void settings(boolean z, Settings settings);

        void windowUpdate(int i, long j);
    }

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
                int readMedium = Http2Reader.readMedium(this.source);
                this.left = readMedium;
                this.length = readMedium;
                byte readByte = (byte) this.source.readByte();
                this.flags = (byte) this.source.readByte();
                if (Http2Reader.logger.isLoggable(Level.FINE)) {
                    Http2Reader.logger.fine(Http2.frameLog(true, this.streamId, this.length, readByte, this.flags));
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

    public Http2Reader(BufferedSource source, boolean client) {
        this.source = source;
        this.client = client;
    }

    public final void readConnectionPreface(Handler handler) throws IOException {
        if (!this.client) {
            ByteString connectionPreface = this.source.readByteString((long) Http2.CONNECTION_PREFACE.size());
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(Util.format("<< CONNECTION %s", connectionPreface.hex()));
            }
            if (!Http2.CONNECTION_PREFACE.equals(connectionPreface)) {
                throw Http2.ioException("Expected a connection header but was %s", connectionPreface.utf8());
            }
        } else if (!nextFrame(true, handler)) {
            throw Http2.ioException("Required SETTINGS preface not received", new Object[0]);
        }
    }

    public final boolean nextFrame(boolean requireSettings, Handler handler) throws IOException {
        boolean z = false;
        try {
            this.source.require(9);
            int length = readMedium(this.source);
            if (length < 0 || length > 16384) {
                throw Http2.ioException("FRAME_SIZE_ERROR: %s", Integer.valueOf(length));
            }
            byte type = (byte) this.source.readByte();
            if (!requireSettings || type == (byte) 4) {
                byte flags = (byte) this.source.readByte();
                int streamId = this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(Http2.frameLog(true, streamId, length, type, flags));
                }
                boolean z2;
                short readByte;
                int readShort;
                int i;
                switch (type) {
                    case (byte) 0:
                        boolean z3;
                        if ((flags & 1) != 0) {
                            z3 = true;
                        } else {
                            z3 = false;
                        }
                        if ((flags & 32) != 0) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        if (z2) {
                            throw Http2.ioException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA", new Object[0]);
                        }
                        if ((flags & 8) != 0) {
                            readByte = (short) (this.source.readByte() & 255);
                        }
                        handler.data(z3, streamId, this.source, lengthWithoutPadding(length, flags, readByte));
                        this.source.skip((long) readByte);
                        return true;
                    case (byte) 1:
                        if (streamId == 0) {
                            throw Http2.ioException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0", new Object[0]);
                        }
                        if ((flags & 1) != 0) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                        if ((flags & 8) != 0) {
                            readByte = (short) (this.source.readByte() & 255);
                        }
                        if ((flags & 32) != 0) {
                            readPriority(handler, streamId);
                            length -= 5;
                        }
                        handler.headers$64c3d190(z2, streamId, readHeaderBlock(lengthWithoutPadding(length, flags, readByte), readByte, flags, streamId));
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
                                int readInt = this.source.readInt();
                                switch (readShort) {
                                    case 2:
                                        if (!(readInt == 0 || readInt == 1)) {
                                            throw Http2.ioException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1", new Object[0]);
                                        }
                                    case 3:
                                        readShort = 4;
                                        break;
                                    case 4:
                                        readShort = 7;
                                        if (readInt >= 0) {
                                            break;
                                        }
                                        throw Http2.ioException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1", new Object[0]);
                                    case 5:
                                        if (readInt >= 16384 && readInt <= 16777215) {
                                            break;
                                        }
                                        throw Http2.ioException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: %s", Integer.valueOf(readInt));
                                    default:
                                        break;
                                }
                                settings.set(readShort, readInt);
                            }
                            handler.settings(false, settings);
                            return true;
                        }
                    case (byte) 5:
                        if (streamId == 0) {
                            throw Http2.ioException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0", new Object[0]);
                        }
                        if ((flags & 8) != 0) {
                            readByte = (short) (this.source.readByte() & 255);
                        }
                        handler.pushPromise$16014a7a(this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, readHeaderBlock(lengthWithoutPadding(length - 4, flags, readByte), readByte, flags, streamId));
                        return true;
                    case (byte) 6:
                        if (length != 8) {
                            throw Http2.ioException("TYPE_PING length != 8: %s", Integer.valueOf(length));
                        } else if (streamId != 0) {
                            throw Http2.ioException("TYPE_PING streamId != 0", new Object[0]);
                        } else {
                            readShort = this.source.readInt();
                            int readInt2 = this.source.readInt();
                            if ((flags & 1) != 0) {
                                z = true;
                            }
                            handler.ping(z, readShort, readInt2);
                            return true;
                        }
                    case (byte) 7:
                        if (length < 8) {
                            throw Http2.ioException("TYPE_GOAWAY length < 8: %s", Integer.valueOf(length));
                        } else if (streamId != 0) {
                            throw Http2.ioException("TYPE_GOAWAY streamId != 0", new Object[0]);
                        } else {
                            readShort = this.source.readInt();
                            i = length - 8;
                            if (ErrorCode.fromHttp2(this.source.readInt()) == null) {
                                throw Http2.ioException("TYPE_GOAWAY unexpected error code: %d", Integer.valueOf(this.source.readInt()));
                            }
                            ByteString byteString = ByteString.EMPTY;
                            if (i > 0) {
                                byteString = this.source.readByteString((long) i);
                            }
                            handler.goAway$4b802bc(readShort, byteString);
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
            }
            throw Http2.ioException("Expected a SETTINGS frame but was %s", Byte.valueOf(type));
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

    static int readMedium(BufferedSource source) throws IOException {
        return (((source.readByte() & 255) << 16) | ((source.readByte() & 255) << 8)) | (source.readByte() & 255);
    }

    private static int lengthWithoutPadding(int length, byte flags, short padding) throws IOException {
        if ((flags & 8) != 0) {
            short length2 = length - 1;
        }
        if (padding <= length2) {
            return (short) (length2 - padding);
        }
        throw Http2.ioException("PROTOCOL_ERROR padding %s > remaining length %s", Short.valueOf(padding), Integer.valueOf(length2));
    }
}
