package okhttp3.internal;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.IDN;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.ByteString;
import okio.Source;

public final class Util {
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final RequestBody EMPTY_REQUEST = RequestBody.create(null, EMPTY_BYTE_ARRAY);
    public static final ResponseBody EMPTY_RESPONSE = ResponseBody.create(null, EMPTY_BYTE_ARRAY);
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final TimeZone UTC = TimeZone.getTimeZone("GMT");
    private static final Charset UTF_16_BE = Charset.forName("UTF-16BE");
    private static final ByteString UTF_16_BE_BOM = ByteString.decodeHex("feff");
    private static final Charset UTF_16_LE = Charset.forName("UTF-16LE");
    private static final ByteString UTF_16_LE_BOM = ByteString.decodeHex("fffe");
    private static final Charset UTF_32_BE = Charset.forName("UTF-32BE");
    private static final ByteString UTF_32_BE_BOM = ByteString.decodeHex("0000ffff");
    private static final Charset UTF_32_LE = Charset.forName("UTF-32LE");
    private static final ByteString UTF_32_LE_BOM = ByteString.decodeHex("ffff0000");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final ByteString UTF_8_BOM = ByteString.decodeHex("efbbbf");
    private static final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");

    public static boolean skipAll(okio.Source r12, int r13, java.util.concurrent.TimeUnit r14) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x006a in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:43)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:56)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r6 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r0 = java.lang.System.nanoTime();
        r5 = r12.timeout();
        r5 = r5.hasDeadline();
        if (r5 == 0) goto L_0x0051;
    L_0x0013:
        r5 = r12.timeout();
        r8 = r5.deadlineNanoTime();
        r2 = r8 - r0;
    L_0x001d:
        r5 = r12.timeout();
        r8 = (long) r13;
        r8 = r14.toNanos(r8);
        r8 = java.lang.Math.min(r2, r8);
        r8 = r8 + r0;
        r5.deadlineNanoTime(r8);
        r4 = new okio.Buffer;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
        r4.<init>();	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
    L_0x0033:
        r8 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
        r8 = r12.read(r4, r8);	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
        r10 = -1;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
        if (r5 == 0) goto L_0x0053;	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
    L_0x003f:
        r4.clear();	 Catch:{ InterruptedIOException -> 0x0043, all -> 0x0074 }
        goto L_0x0033;
    L_0x0043:
        r5 = move-exception;
        r5 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r5 != 0) goto L_0x006a;
    L_0x0048:
        r5 = r12.timeout();
        r5.clearDeadline();
    L_0x004f:
        r5 = 0;
    L_0x0050:
        return r5;
    L_0x0051:
        r2 = r6;
        goto L_0x001d;
    L_0x0053:
        r5 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r5 != 0) goto L_0x0060;
    L_0x0057:
        r5 = r12.timeout();
        r5.clearDeadline();
    L_0x005e:
        r5 = 1;
        goto L_0x0050;
    L_0x0060:
        r5 = r12.timeout();
        r6 = r0 + r2;
        r5.deadlineNanoTime(r6);
        goto L_0x005e;
    L_0x006a:
        r5 = r12.timeout();
        r6 = r0 + r2;
        r5.deadlineNanoTime(r6);
        goto L_0x004f;
    L_0x0074:
        r5 = move-exception;
        r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r6 != 0) goto L_0x0081;
    L_0x0079:
        r6 = r12.timeout();
        r6.clearDeadline();
    L_0x0080:
        throw r5;
    L_0x0081:
        r6 = r12.timeout();
        r8 = r0 + r2;
        r6.deadlineNanoTime(r8);
        goto L_0x0080;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.Util.skipAll(okio.Source, int, java.util.concurrent.TimeUnit):boolean");
    }

    public static void checkOffsetAndCount(long arrayLength, long offset, long count) {
        if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e) {
            }
        }
    }

    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (AssertionError e) {
                if (!isAndroidGetsocknameError(e)) {
                    throw e;
                }
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception e2) {
            }
        }
    }

    public static boolean discard(Source source, int timeout, TimeUnit timeUnit) {
        try {
            return skipAll(source, 100, timeUnit);
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> List<T> immutableList(List<T> list) {
        return Collections.unmodifiableList(new ArrayList(list));
    }

    public static <T> List<T> immutableList(T... elements) {
        return Collections.unmodifiableList(Arrays.asList((Object[]) elements.clone()));
    }

    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            public final Thread newThread(Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }

    public static <T> T[] intersect(Class<T> arrayType, T[] first, T[] second) {
        List<T> result = new ArrayList();
        for (Object obj : first) {
            for (Object obj2 : second) {
                if (obj.equals(obj2)) {
                    result.add(obj2);
                    break;
                }
            }
        }
        return result.toArray((Object[]) Array.newInstance(arrayType, result.size()));
    }

    public static String hostHeader(HttpUrl url, boolean includeDefaultPort) {
        String host;
        if (url.host().contains(":")) {
            host = "[" + url.host() + "]";
        } else {
            host = url.host();
        }
        return (includeDefaultPort || url.port() != HttpUrl.defaultPort(url.scheme())) ? host + ":" + url.port() : host;
    }

    public static boolean isAndroidGetsocknameError(AssertionError e) {
        return (e.getCause() == null || e.getMessage() == null || !e.getMessage().contains("getsockname failed")) ? false : true;
    }

    public static <T> int indexOf(T[] array, T value) {
        int size = array.length;
        for (int i = 0; i < size; i++) {
            if (equal(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static String[] concat(String[] array, String value) {
        String[] result = new String[(array.length + 1)];
        System.arraycopy(array, 0, result, 0, array.length);
        result[result.length - 1] = value;
        return result;
    }

    public static int skipLeadingAsciiWhitespace(String input, int pos, int limit) {
        int i = pos;
        while (i < limit) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    i++;
                default:
                    return i;
            }
        }
        return limit;
    }

    public static int skipTrailingAsciiWhitespace(String input, int pos, int limit) {
        int i = limit - 1;
        while (i >= pos) {
            switch (input.charAt(i)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ':
                    i--;
                default:
                    return i + 1;
            }
        }
        return pos;
    }

    public static String trimSubstring(String string, int pos, int limit) {
        int start = skipLeadingAsciiWhitespace(string, pos, limit);
        return string.substring(start, skipTrailingAsciiWhitespace(string, start, limit));
    }

    public static int delimiterOffset(String input, int pos, int limit, String delimiters) {
        for (int i = pos; i < limit; i++) {
            if (delimiters.indexOf(input.charAt(i)) != -1) {
                return i;
            }
        }
        return limit;
    }

    public static int delimiterOffset(String input, int pos, int limit, char delimiter) {
        for (int i = pos; i < limit; i++) {
            if (input.charAt(i) == delimiter) {
                return i;
            }
        }
        return limit;
    }

    public static String domainToAscii(String input) {
        Object obj = 1;
        try {
            String result = IDN.toASCII(input).toLowerCase(Locale.US);
            if (result.isEmpty()) {
                return null;
            }
            for (int i = 0; i < result.length(); i++) {
                char charAt = result.charAt(i);
                if (charAt <= '\u001f' || charAt >= '' || " #%/:?@[\\]".indexOf(charAt) != -1) {
                    break;
                }
            }
            obj = null;
            if (obj != null) {
                return null;
            }
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static int indexOfControlOrNonAscii(String input) {
        int i = 0;
        int length = input.length();
        while (i < length) {
            char c = input.charAt(i);
            if (c <= '\u001f' || c >= '') {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static boolean verifyAsIpAddress(String host) {
        return VERIFY_AS_IP_ADDRESS.matcher(host).matches();
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }
}
