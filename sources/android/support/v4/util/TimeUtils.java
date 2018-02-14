package android.support.v4.util;

import java.io.PrintWriter;

public final class TimeUtils {
    private static char[] sFormatStr = new char[24];
    private static final Object sFormatSync = new Object();

    private static int accumField(int amt, int suffix, boolean always, int zeropad) {
        if (amt > 99 || (always && zeropad >= 3)) {
            return suffix + 3;
        }
        if (amt > 9 || (always && zeropad >= 2)) {
            return suffix + 2;
        }
        if (always || amt > 0) {
            return suffix + 1;
        }
        return 0;
    }

    private static int printField(char[] formatStr, int amt, char suffix, int pos, boolean always, int zeropad) {
        if (!always && amt <= 0) {
            return pos;
        }
        int startPos = pos;
        if ((always && zeropad >= 3) || amt > 99) {
            int dig = amt / 100;
            formatStr[pos] = (char) (dig + 48);
            pos++;
            amt -= dig * 100;
        }
        if ((always && zeropad >= 2) || amt > 9 || startPos != pos) {
            dig = amt / 10;
            formatStr[pos] = (char) (dig + 48);
            pos++;
            amt -= dig * 10;
        }
        formatStr[pos] = (char) (amt + 48);
        pos++;
        formatStr[pos] = suffix;
        return pos + 1;
    }

    private static void formatDuration(long duration, PrintWriter pw, int fieldLen) {
        synchronized (sFormatSync) {
            int len;
            if (sFormatStr.length < 0) {
                sFormatStr = new char[0];
            }
            char[] cArr = sFormatStr;
            if (duration == 0) {
                cArr[0] = '0';
                len = 1;
            } else {
                char c;
                int i;
                int i2;
                int i3;
                int i4;
                if (duration > 0) {
                    c = '+';
                } else {
                    duration = -duration;
                    c = '-';
                }
                int i5 = (int) (duration % 1000);
                int floor = (int) Math.floor((double) (duration / 1000));
                int i6 = 0;
                if (floor > 86400) {
                    i6 = floor / 86400;
                    floor -= 86400 * i6;
                }
                if (floor > 3600) {
                    i = floor / 3600;
                    i2 = i;
                    i = floor - (i * 3600);
                } else {
                    i2 = 0;
                    i = floor;
                }
                if (i > 60) {
                    int i7 = i / 60;
                    i3 = i7;
                    i4 = i - (i7 * 60);
                } else {
                    i3 = 0;
                    i4 = i;
                }
                cArr[0] = c;
                int printField = printField(cArr, i6, 'd', 1, false, 0);
                printField = printField(cArr, i2, 'h', printField, printField != 1, 0);
                int printField2 = printField(cArr, i3, 'm', printField, printField != 1, 0);
                i6 = printField(cArr, i5, 'm', printField(cArr, i4, 's', printField2, printField2 != 1, 0), true, 0);
                cArr[i6] = 's';
                len = i6 + 1;
            }
            pw.print(new String(sFormatStr, 0, len));
        }
    }

    public static void formatDuration(long duration, PrintWriter pw) {
        formatDuration(duration, pw, 0);
    }

    public static void formatDuration(long time, long now, PrintWriter pw) {
        if (time == 0) {
            pw.print("--");
        } else {
            formatDuration(time - now, pw, 0);
        }
    }
}
