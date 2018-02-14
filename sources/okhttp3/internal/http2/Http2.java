package okhttp3.internal.http2;

import java.io.IOException;
import okhttp3.internal.Util;
import okio.ByteString;

public final class Http2 {
    static final String[] BINARY = new String[256];
    static final ByteString CONNECTION_PREFACE = ByteString.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");
    static final String[] FLAGS = new String[64];
    private static final String[] FRAME_NAMES = new String[]{"DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION"};

    static {
        int i;
        int i2;
        for (i = 0; i < 256; i++) {
            BINARY[i] = Util.format("%8s", Integer.toBinaryString(i)).replace(' ', '0');
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

    private Http2() {
    }

    static IllegalArgumentException illegalArgument(String message, Object... args) {
        throw new IllegalArgumentException(Util.format(message, args));
    }

    static IOException ioException(String message, Object... args) throws IOException {
        throw new IOException(Util.format(message, args));
    }

    static String frameLog(boolean inbound, int streamId, int length, byte type, byte flags) {
        String formattedType;
        String formattedFlags;
        if (type < (byte) 10) {
            formattedType = FRAME_NAMES[type];
        } else {
            formattedType = Util.format("0x%02x", Byte.valueOf(type));
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
        return Util.format(str, objArr);
    }
}
