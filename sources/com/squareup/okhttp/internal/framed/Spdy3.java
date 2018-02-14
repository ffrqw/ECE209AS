package com.squareup.okhttp.internal.framed;

import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.framed.FrameReader.Handler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.util.List;
import java.util.zip.Deflater;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.DeflaterSink;
import okio.Okio;

public final class Spdy3 implements Variant {
    static final byte[] DICTIONARY;

    static final class Reader implements FrameReader {
        private final boolean client;
        private final NameValueBlockReader headerBlockReader = new NameValueBlockReader(this.source);
        private final BufferedSource source;

        Reader(BufferedSource source, boolean client) {
            this.source = source;
            this.client = client;
        }

        public final void readConnectionPreface() {
        }

        public final boolean nextFrame(Handler handler) throws IOException {
            try {
                int w1 = this.source.readInt();
                int w2 = this.source.readInt();
                int flags = (-16777216 & w2) >>> 24;
                int length = w2 & 16777215;
                if ((Integer.MIN_VALUE & w1) != 0) {
                    int version = (2147418112 & w1) >>> 16;
                    int type = w1 & 65535;
                    if (version != 3) {
                        throw new ProtocolException("version != 3: " + version);
                    }
                    int readInt;
                    int readInt2;
                    switch (type) {
                        case 1:
                            readInt = this.source.readInt();
                            this.source.readInt();
                            int i = readInt & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                            this.source.readShort();
                            handler.headers$37c2d766((flags & 2) != 0, (flags & 1) != 0, i, this.headerBlockReader.readNameValueBlock(length - 10), HeadersMode.SPDY_SYN_STREAM);
                            return true;
                        case 2:
                            handler.headers$37c2d766(false, (flags & 1) != 0, this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, this.headerBlockReader.readNameValueBlock(length - 4), HeadersMode.SPDY_REPLY);
                            return true;
                        case 3:
                            if (length != 8) {
                                throw ioException("TYPE_RST_STREAM length: %d != 8", Integer.valueOf(length));
                            }
                            readInt = this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                            ErrorCode fromSpdy3Rst = ErrorCode.fromSpdy3Rst(this.source.readInt());
                            if (fromSpdy3Rst == null) {
                                throw ioException("TYPE_RST_STREAM unexpected error code: %d", Integer.valueOf(readInt2));
                            }
                            handler.rstStream(readInt, fromSpdy3Rst);
                            return true;
                        case 4:
                            readSettings(handler, flags, length);
                            return true;
                        case 6:
                            if (length != 4) {
                                throw ioException("TYPE_PING length: %d != 4", Integer.valueOf(length));
                            }
                            readInt2 = this.source.readInt();
                            handler.ping(this.client == ((readInt2 & 1) == 1), readInt2, 0);
                            return true;
                        case 7:
                            if (length != 8) {
                                throw ioException("TYPE_GOAWAY length: %d != 8", Integer.valueOf(length));
                            }
                            readInt = this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                            if (ErrorCode.fromSpdyGoAway(this.source.readInt()) == null) {
                                throw ioException("TYPE_GOAWAY unexpected error code: %d", Integer.valueOf(this.source.readInt()));
                            }
                            handler.goAway$4b4c5c6b(readInt, ByteString.EMPTY);
                            return true;
                        case 8:
                            handler.headers$37c2d766(false, false, this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, this.headerBlockReader.readNameValueBlock(length - 4), HeadersMode.SPDY_HEADERS);
                            return true;
                        case 9:
                            if (length != 8) {
                                throw ioException("TYPE_WINDOW_UPDATE length: %d != 8", Integer.valueOf(length));
                            }
                            readInt = this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                            long readInt3 = (long) (this.source.readInt() & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                            if (readInt3 == 0) {
                                throw ioException("windowSizeIncrement was 0", Long.valueOf(readInt3));
                            }
                            handler.windowUpdate(readInt, readInt3);
                            return true;
                        default:
                            this.source.skip((long) length);
                            return true;
                    }
                }
                handler.data((flags & 1) != 0, w1 & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, this.source, length);
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        private void readSettings(Handler handler, int flags, int length) throws IOException {
            boolean clearPrevious = true;
            int numberOfEntries = this.source.readInt();
            if (length != (numberOfEntries * 8) + 4) {
                throw ioException("TYPE_SETTINGS length: %d != 4 + 8 * %d", Integer.valueOf(length), Integer.valueOf(numberOfEntries));
            }
            Settings settings = new Settings();
            for (int i = 0; i < numberOfEntries; i++) {
                int w1 = this.source.readInt();
                int id = w1 & 16777215;
                settings.set(id, (-16777216 & w1) >>> 24, this.source.readInt());
            }
            if ((flags & 1) == 0) {
                clearPrevious = false;
            }
            handler.settings(clearPrevious, settings);
        }

        private static IOException ioException(String message, Object... args) throws IOException {
            throw new IOException(String.format(message, args));
        }

        public final void close() throws IOException {
            this.headerBlockReader.close();
        }
    }

    static final class Writer implements FrameWriter {
        private final boolean client;
        private boolean closed;
        private final Buffer headerBlockBuffer = new Buffer();
        private final BufferedSink headerBlockOut;
        private final BufferedSink sink;

        Writer(BufferedSink sink, boolean client) {
            this.sink = sink;
            this.client = client;
            Deflater deflater = new Deflater();
            deflater.setDictionary(Spdy3.DICTIONARY);
            this.headerBlockOut = Okio.buffer(new DeflaterSink(this.headerBlockBuffer, deflater));
        }

        public final void ackSettings(Settings peerSettings) {
        }

        public final synchronized void connectionPreface() {
        }

        public final synchronized void flush() throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            this.sink.flush();
        }

        public final synchronized void synStream(boolean outFinished, boolean inFinished, int streamId, int associatedStreamId, List<Header> headerBlock) throws IOException {
            int i = 0;
            synchronized (this) {
                if (this.closed) {
                    throw new IOException("closed");
                }
                int i2;
                writeNameValueBlockToBuffer(headerBlock);
                int length = (int) (10 + this.headerBlockBuffer.size());
                if (outFinished) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                if (inFinished) {
                    i = 2;
                }
                int flags = i2 | i;
                this.sink.writeInt(-2147287039);
                this.sink.writeInt(((flags & 255) << 24) | (16777215 & length));
                this.sink.writeInt(streamId & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                this.sink.writeInt(associatedStreamId & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                this.sink.writeShort(0);
                this.sink.writeAll(this.headerBlockBuffer);
                this.sink.flush();
            }
        }

        public final synchronized void rstStream(int streamId, ErrorCode errorCode) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            } else if (errorCode.spdyRstCode == -1) {
                throw new IllegalArgumentException();
            } else {
                this.sink.writeInt(-2147287037);
                this.sink.writeInt(8);
                this.sink.writeInt(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED & streamId);
                this.sink.writeInt(errorCode.spdyRstCode);
                this.sink.flush();
            }
        }

        public final int maxDataLength() {
            return 16383;
        }

        public final synchronized void data(boolean outFinished, int streamId, Buffer source, int byteCount) throws IOException {
            int flags;
            if (outFinished) {
                flags = 1;
            } else {
                flags = 0;
            }
            if (this.closed) {
                throw new IOException("closed");
            } else if (((long) byteCount) > 16777215) {
                throw new IllegalArgumentException("FRAME_TOO_LARGE max size is 16Mib: " + byteCount);
            } else {
                this.sink.writeInt(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED & streamId);
                this.sink.writeInt(((flags & 255) << 24) | (16777215 & byteCount));
                if (byteCount > 0) {
                    this.sink.write(source, (long) byteCount);
                }
            }
        }

        private void writeNameValueBlockToBuffer(List<Header> headerBlock) throws IOException {
            this.headerBlockOut.writeInt(headerBlock.size());
            int size = headerBlock.size();
            for (int i = 0; i < size; i++) {
                ByteString name = ((Header) headerBlock.get(i)).name;
                this.headerBlockOut.writeInt(name.size());
                this.headerBlockOut.write(name);
                ByteString value = ((Header) headerBlock.get(i)).value;
                this.headerBlockOut.writeInt(value.size());
                this.headerBlockOut.write(value);
            }
            this.headerBlockOut.flush();
        }

        public final synchronized void settings(Settings settings) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            }
            int size = settings.size();
            int length = (size << 3) + 4;
            this.sink.writeInt(-2147287036);
            this.sink.writeInt((length & 16777215) | 0);
            this.sink.writeInt(size);
            for (int i = 0; i <= 10; i++) {
                if (settings.isSet(i)) {
                    this.sink.writeInt(((settings.flags(i) & 255) << 24) | (i & 16777215));
                    this.sink.writeInt(settings.get(i));
                }
            }
            this.sink.flush();
        }

        public final synchronized void ping(boolean reply, int payload1, int payload2) throws IOException {
            boolean payloadIsReply = true;
            synchronized (this) {
                if (this.closed) {
                    throw new IOException("closed");
                }
                boolean z;
                boolean z2 = this.client;
                if ((payload1 & 1) == 1) {
                    z = true;
                } else {
                    z = false;
                }
                if (z2 == z) {
                    payloadIsReply = false;
                }
                if (reply != payloadIsReply) {
                    throw new IllegalArgumentException("payload != reply");
                }
                this.sink.writeInt(-2147287034);
                this.sink.writeInt(4);
                this.sink.writeInt(payload1);
                this.sink.flush();
            }
        }

        public final synchronized void goAway(int lastGoodStreamId, ErrorCode errorCode, byte[] ignored) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            } else if (errorCode.spdyGoAwayCode == -1) {
                throw new IllegalArgumentException("errorCode.spdyGoAwayCode == -1");
            } else {
                this.sink.writeInt(-2147287033);
                this.sink.writeInt(8);
                this.sink.writeInt(lastGoodStreamId);
                this.sink.writeInt(errorCode.spdyGoAwayCode);
                this.sink.flush();
            }
        }

        public final synchronized void windowUpdate(int streamId, long increment) throws IOException {
            if (this.closed) {
                throw new IOException("closed");
            } else if (increment == 0 || increment > 2147483647L) {
                throw new IllegalArgumentException("windowSizeIncrement must be between 1 and 0x7fffffff: " + increment);
            } else {
                this.sink.writeInt(-2147287031);
                this.sink.writeInt(8);
                this.sink.writeInt(streamId);
                this.sink.writeInt((int) increment);
                this.sink.flush();
            }
        }

        public final synchronized void close() throws IOException {
            this.closed = true;
            Util.closeAll(this.sink, this.headerBlockOut);
        }
    }

    static {
        try {
            DICTIONARY = "\u0000\u0000\u0000\u0007options\u0000\u0000\u0000\u0004head\u0000\u0000\u0000\u0004post\u0000\u0000\u0000\u0003put\u0000\u0000\u0000\u0006delete\u0000\u0000\u0000\u0005trace\u0000\u0000\u0000\u0006accept\u0000\u0000\u0000\u000eaccept-charset\u0000\u0000\u0000\u000faccept-encoding\u0000\u0000\u0000\u000faccept-language\u0000\u0000\u0000\raccept-ranges\u0000\u0000\u0000\u0003age\u0000\u0000\u0000\u0005allow\u0000\u0000\u0000\rauthorization\u0000\u0000\u0000\rcache-control\u0000\u0000\u0000\nconnection\u0000\u0000\u0000\fcontent-base\u0000\u0000\u0000\u0010content-encoding\u0000\u0000\u0000\u0010content-language\u0000\u0000\u0000\u000econtent-length\u0000\u0000\u0000\u0010content-location\u0000\u0000\u0000\u000bcontent-md5\u0000\u0000\u0000\rcontent-range\u0000\u0000\u0000\fcontent-type\u0000\u0000\u0000\u0004date\u0000\u0000\u0000\u0004etag\u0000\u0000\u0000\u0006expect\u0000\u0000\u0000\u0007expires\u0000\u0000\u0000\u0004from\u0000\u0000\u0000\u0004host\u0000\u0000\u0000\bif-match\u0000\u0000\u0000\u0011if-modified-since\u0000\u0000\u0000\rif-none-match\u0000\u0000\u0000\bif-range\u0000\u0000\u0000\u0013if-unmodified-since\u0000\u0000\u0000\rlast-modified\u0000\u0000\u0000\blocation\u0000\u0000\u0000\fmax-forwards\u0000\u0000\u0000\u0006pragma\u0000\u0000\u0000\u0012proxy-authenticate\u0000\u0000\u0000\u0013proxy-authorization\u0000\u0000\u0000\u0005range\u0000\u0000\u0000\u0007referer\u0000\u0000\u0000\u000bretry-after\u0000\u0000\u0000\u0006server\u0000\u0000\u0000\u0002te\u0000\u0000\u0000\u0007trailer\u0000\u0000\u0000\u0011transfer-encoding\u0000\u0000\u0000\u0007upgrade\u0000\u0000\u0000\nuser-agent\u0000\u0000\u0000\u0004vary\u0000\u0000\u0000\u0003via\u0000\u0000\u0000\u0007warning\u0000\u0000\u0000\u0010www-authenticate\u0000\u0000\u0000\u0006method\u0000\u0000\u0000\u0003get\u0000\u0000\u0000\u0006status\u0000\u0000\u0000\u0006200 OK\u0000\u0000\u0000\u0007version\u0000\u0000\u0000\bHTTP/1.1\u0000\u0000\u0000\u0003url\u0000\u0000\u0000\u0006public\u0000\u0000\u0000\nset-cookie\u0000\u0000\u0000\nkeep-alive\u0000\u0000\u0000\u0006origin100101201202205206300302303304305306307402405406407408409410411412413414415416417502504505203 Non-Authoritative Information204 No Content301 Moved Permanently400 Bad Request401 Unauthorized403 Forbidden404 Not Found500 Internal Server Error501 Not Implemented503 Service UnavailableJan Feb Mar Apr May Jun Jul Aug Sept Oct Nov Dec 00:00:00 Mon, Tue, Wed, Thu, Fri, Sat, Sun, GMTchunked,text/html,image/png,image/jpg,image/gif,application/xml,application/xhtml+xml,text/plain,text/javascript,publicprivatemax-age=gzip,deflate,sdchcharset=utf-8charset=iso-8859-1,utf-,*,enq=0.".getBytes(Util.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }

    public final FrameReader newReader(BufferedSource source, boolean client) {
        return new Reader(source, client);
    }

    public final FrameWriter newWriter(BufferedSink sink, boolean client) {
        return new Writer(sink, client);
    }
}
