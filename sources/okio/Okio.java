package okio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Okio {
    static final Logger logger = Logger.getLogger(Okio.class.getName());

    private Okio() {
    }

    public static BufferedSource buffer(Source source) {
        return new RealBufferedSource(source);
    }

    public static BufferedSink buffer(Sink sink) {
        return new RealBufferedSink(sink);
    }

    private static Sink sink(OutputStream out) {
        return sink(out, new Timeout());
    }

    private static Sink sink(final OutputStream out, final Timeout timeout) {
        if (out == null) {
            throw new IllegalArgumentException("out == null");
        } else if (timeout != null) {
            return new Sink() {
                public final void write(Buffer source, long byteCount) throws IOException {
                    Util.checkOffsetAndCount(source.size, 0, byteCount);
                    while (byteCount > 0) {
                        timeout.throwIfReached();
                        Segment head = source.head;
                        int toCopy = (int) Math.min(byteCount, (long) (head.limit - head.pos));
                        out.write(head.data, head.pos, toCopy);
                        head.pos += toCopy;
                        byteCount -= (long) toCopy;
                        source.size -= (long) toCopy;
                        if (head.pos == head.limit) {
                            source.head = head.pop();
                            SegmentPool.recycle(head);
                        }
                    }
                }

                public final void flush() throws IOException {
                    out.flush();
                }

                public final void close() throws IOException {
                    out.close();
                }

                public final Timeout timeout() {
                    return timeout;
                }

                public final String toString() {
                    return "sink(" + out + ")";
                }
            };
        } else {
            throw new IllegalArgumentException("timeout == null");
        }
    }

    public static Sink sink(Socket socket) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("socket == null");
        }
        AsyncTimeout timeout = timeout(socket);
        return new okio.AsyncTimeout.AnonymousClass1(sink(socket.getOutputStream(), timeout));
    }

    public static Source source(InputStream in) {
        return source(in, new Timeout());
    }

    private static Source source(final InputStream in, final Timeout timeout) {
        if (in == null) {
            throw new IllegalArgumentException("in == null");
        } else if (timeout != null) {
            return new Source() {
                public final long read(Buffer sink, long byteCount) throws IOException {
                    if (byteCount < 0) {
                        throw new IllegalArgumentException("byteCount < 0: " + byteCount);
                    } else if (byteCount == 0) {
                        return 0;
                    } else {
                        try {
                            timeout.throwIfReached();
                            Segment tail = sink.writableSegment(1);
                            int bytesRead = in.read(tail.data, tail.limit, (int) Math.min(byteCount, (long) (8192 - tail.limit)));
                            if (bytesRead == -1) {
                                return -1;
                            }
                            tail.limit += bytesRead;
                            sink.size += (long) bytesRead;
                            return (long) bytesRead;
                        } catch (AssertionError e) {
                            if (Okio.isAndroidGetsocknameError(e)) {
                                throw new IOException(e);
                            }
                            throw e;
                        }
                    }
                }

                public final void close() throws IOException {
                    in.close();
                }

                public final Timeout timeout() {
                    return timeout;
                }

                public final String toString() {
                    return "source(" + in + ")";
                }
            };
        } else {
            throw new IllegalArgumentException("timeout == null");
        }
    }

    public static Source source(File file) throws FileNotFoundException {
        if (file != null) {
            return source(new FileInputStream(file));
        }
        throw new IllegalArgumentException("file == null");
    }

    public static Sink sink(File file) throws FileNotFoundException {
        if (file != null) {
            return sink(new FileOutputStream(file));
        }
        throw new IllegalArgumentException("file == null");
    }

    public static Sink appendingSink(File file) throws FileNotFoundException {
        if (file != null) {
            return sink(new FileOutputStream(file, true));
        }
        throw new IllegalArgumentException("file == null");
    }

    public static Sink blackhole() {
        return new Sink() {
            public final void write(Buffer source, long byteCount) throws IOException {
                source.skip(byteCount);
            }

            public final void flush() throws IOException {
            }

            public final Timeout timeout() {
                return Timeout.NONE;
            }

            public final void close() throws IOException {
            }
        };
    }

    public static Source source(Socket socket) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("socket == null");
        }
        AsyncTimeout timeout = timeout(socket);
        return new okio.AsyncTimeout.AnonymousClass2(source(socket.getInputStream(), timeout));
    }

    private static AsyncTimeout timeout(final Socket socket) {
        return new AsyncTimeout() {
            protected final IOException newTimeoutException(IOException cause) {
                InterruptedIOException ioe = new SocketTimeoutException("timeout");
                if (cause != null) {
                    ioe.initCause(cause);
                }
                return ioe;
            }

            protected final void timedOut() {
                try {
                    socket.close();
                } catch (Exception e) {
                    Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + socket, e);
                } catch (AssertionError e2) {
                    if (Okio.isAndroidGetsocknameError(e2)) {
                        Okio.logger.log(Level.WARNING, "Failed to close timed out socket " + socket, e2);
                        return;
                    }
                    throw e2;
                }
            }
        };
    }

    static boolean isAndroidGetsocknameError(AssertionError e) {
        return (e.getCause() == null || e.getMessage() == null || !e.getMessage().contains("getsockname failed")) ? false : true;
    }
}
