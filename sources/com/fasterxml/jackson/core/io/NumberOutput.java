package com.fasterxml.jackson.core.io;

public final class NumberOutput {
    private static int BILLION = 1000000000;
    private static long BILLION_L = 1000000000;
    private static long MAX_INT_AS_LONG = 2147483647L;
    private static int MILLION = 1000000;
    private static long MIN_INT_AS_LONG = -2147483648L;
    static final String SMALLEST_INT = "-2147483648";
    static final String SMALLEST_LONG = "-9223372036854775808";
    private static final int[] TRIPLET_TO_CHARS = new int[1000];
    private static final String[] sSmallIntStrs = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static final String[] sSmallIntStrs2 = new String[]{"-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10"};

    static {
        int fullIx = 0;
        for (int i1 = 0; i1 < 10; i1++) {
            int i2 = 0;
            while (i2 < 10) {
                int i3 = 0;
                int fullIx2 = fullIx;
                while (i3 < 10) {
                    fullIx = fullIx2 + 1;
                    TRIPLET_TO_CHARS[fullIx2] = (((i1 + 48) << 16) | ((i2 + 48) << 8)) | (i3 + 48);
                    i3++;
                    fullIx2 = fullIx;
                }
                i2++;
                fullIx = fullIx2;
            }
        }
    }

    public static int outputInt(int v, char[] b, int off) {
        if (v < 0) {
            if (v == Integer.MIN_VALUE) {
                return _outputSmallestI(b, off);
            }
            int off2 = off + 1;
            b[off] = '-';
            v = -v;
            off = off2;
        }
        if (v < MILLION) {
            if (v >= 1000) {
                int thousands = v / 1000;
                return _full3(v - (thousands * 1000), b, _leading3(thousands, b, off));
            } else if (v >= 10) {
                return _leading3(v, b, off);
            } else {
                b[off] = (char) (v + 48);
                return off + 1;
            }
        } else if (v >= BILLION) {
            v -= BILLION;
            if (v >= BILLION) {
                v -= BILLION;
                off2 = off + 1;
                b[off] = '2';
                off = off2;
            } else {
                off2 = off + 1;
                b[off] = '1';
                off = off2;
            }
            return _outputFullBillion(v, b, off);
        } else {
            int newValue = v / 1000;
            int ones = v - (newValue * 1000);
            v = newValue;
            newValue /= 1000;
            return _full3(ones, b, _full3(v - (newValue * 1000), b, _leading3(newValue, b, off)));
        }
    }

    public static int outputInt(int v, byte[] b, int off) {
        if (v < 0) {
            if (v == Integer.MIN_VALUE) {
                return _outputSmallestI(b, off);
            }
            int off2 = off + 1;
            b[off] = (byte) 45;
            v = -v;
            off = off2;
        }
        if (v < MILLION) {
            if (v >= 1000) {
                int thousands = v / 1000;
                off = _full3(v - (thousands * 1000), b, _leading3(thousands, b, off));
            } else if (v < 10) {
                off2 = off + 1;
                b[off] = (byte) (v + 48);
                off = off2;
            } else {
                off = _leading3(v, b, off);
            }
            return off;
        } else if (v >= BILLION) {
            v -= BILLION;
            if (v >= BILLION) {
                v -= BILLION;
                off2 = off + 1;
                b[off] = (byte) 50;
                off = off2;
            } else {
                off2 = off + 1;
                b[off] = (byte) 49;
                off = off2;
            }
            return _outputFullBillion(v, b, off);
        } else {
            int newValue = v / 1000;
            int ones = v - (newValue * 1000);
            v = newValue;
            newValue /= 1000;
            return _full3(ones, b, _full3(v - (newValue * 1000), b, _leading3(newValue, b, off)));
        }
    }

    public static int outputLong(long v, char[] b, int off) {
        if (v < 0) {
            if (v > MIN_INT_AS_LONG) {
                return outputInt((int) v, b, off);
            }
            if (v == Long.MIN_VALUE) {
                return _outputSmallestL(b, off);
            }
            int off2 = off + 1;
            b[off] = '-';
            v = -v;
            off = off2;
        } else if (v <= MAX_INT_AS_LONG) {
            return outputInt((int) v, b, off);
        }
        long upper = v / BILLION_L;
        v -= BILLION_L * upper;
        if (upper < BILLION_L) {
            off = _outputUptoBillion((int) upper, b, off);
        } else {
            long hi = upper / BILLION_L;
            upper -= BILLION_L * hi;
            off = _outputFullBillion((int) upper, b, _leading3((int) hi, b, off));
        }
        return _outputFullBillion((int) v, b, off);
    }

    public static int outputLong(long v, byte[] b, int off) {
        if (v < 0) {
            if (v > MIN_INT_AS_LONG) {
                return outputInt((int) v, b, off);
            }
            if (v == Long.MIN_VALUE) {
                return _outputSmallestL(b, off);
            }
            int off2 = off + 1;
            b[off] = (byte) 45;
            v = -v;
            off = off2;
        } else if (v <= MAX_INT_AS_LONG) {
            return outputInt((int) v, b, off);
        }
        long upper = v / BILLION_L;
        v -= BILLION_L * upper;
        if (upper < BILLION_L) {
            off = _outputUptoBillion((int) upper, b, off);
        } else {
            long hi = upper / BILLION_L;
            upper -= BILLION_L * hi;
            off = _outputFullBillion((int) upper, b, _leading3((int) hi, b, off));
        }
        return _outputFullBillion((int) v, b, off);
    }

    public static String toString(int v) {
        if (v < sSmallIntStrs.length) {
            if (v >= 0) {
                return sSmallIntStrs[v];
            }
            int v2 = (-v) - 1;
            if (v2 < sSmallIntStrs2.length) {
                return sSmallIntStrs2[v2];
            }
        }
        return Integer.toString(v);
    }

    public static String toString(long v) {
        if (v > 2147483647L || v < -2147483648L) {
            return Long.toString(v);
        }
        return toString((int) v);
    }

    public static String toString(double v) {
        return Double.toString(v);
    }

    private static int _outputUptoBillion(int v, char[] b, int off) {
        int thousands;
        if (v >= MILLION) {
            thousands = v / 1000;
            int ones = v - (thousands * 1000);
            int millions = thousands / 1000;
            thousands -= millions * 1000;
            off = _leading3(millions, b, off);
            int enc = TRIPLET_TO_CHARS[thousands];
            int i = off + 1;
            b[off] = enc >>> 16;
            off = i + 1;
            b[i] = (char) ((enc >> 8) & 127);
            i = off + 1;
            b[off] = (char) (enc & 127);
            enc = TRIPLET_TO_CHARS[ones];
            off = i + 1;
            b[i] = enc >>> 16;
            i = off + 1;
            b[off] = (char) ((enc >> 8) & 127);
            off = i + 1;
            b[i] = (char) (enc & 127);
            return off;
        } else if (v < 1000) {
            return _leading3(v, b, off);
        } else {
            thousands = v / 1000;
            return _outputUptoMillion(b, off, thousands, v - (thousands * 1000));
        }
    }

    private static int _outputFullBillion(int v, char[] b, int off) {
        int thousands = v / 1000;
        int ones = v - (thousands * 1000);
        int millions = thousands / 1000;
        int enc = TRIPLET_TO_CHARS[millions];
        int i = off + 1;
        b[off] = enc >>> 16;
        off = i + 1;
        b[i] = (char) ((enc >> 8) & 127);
        i = off + 1;
        b[off] = (char) (enc & 127);
        enc = TRIPLET_TO_CHARS[thousands - (millions * 1000)];
        off = i + 1;
        b[i] = enc >>> 16;
        i = off + 1;
        b[off] = (char) ((enc >> 8) & 127);
        off = i + 1;
        b[i] = (char) (enc & 127);
        enc = TRIPLET_TO_CHARS[ones];
        i = off + 1;
        b[off] = enc >>> 16;
        off = i + 1;
        b[i] = (char) ((enc >> 8) & 127);
        i = off + 1;
        b[off] = (char) (enc & 127);
        return i;
    }

    private static int _outputUptoBillion(int v, byte[] b, int off) {
        int thousands;
        if (v >= MILLION) {
            thousands = v / 1000;
            int ones = v - (thousands * 1000);
            int millions = thousands / 1000;
            thousands -= millions * 1000;
            off = _leading3(millions, b, off);
            int enc = TRIPLET_TO_CHARS[thousands];
            int i = off + 1;
            b[off] = (byte) (enc >> 16);
            off = i + 1;
            b[i] = (byte) (enc >> 8);
            i = off + 1;
            b[off] = (byte) enc;
            enc = TRIPLET_TO_CHARS[ones];
            off = i + 1;
            b[i] = (byte) (enc >> 16);
            i = off + 1;
            b[off] = (byte) (enc >> 8);
            off = i + 1;
            b[i] = (byte) enc;
            return off;
        } else if (v < 1000) {
            return _leading3(v, b, off);
        } else {
            thousands = v / 1000;
            return _outputUptoMillion(b, off, thousands, v - (thousands * 1000));
        }
    }

    private static int _outputFullBillion(int v, byte[] b, int off) {
        int thousands = v / 1000;
        int ones = v - (thousands * 1000);
        int millions = thousands / 1000;
        thousands -= millions * 1000;
        int enc = TRIPLET_TO_CHARS[millions];
        int i = off + 1;
        b[off] = (byte) (enc >> 16);
        off = i + 1;
        b[i] = (byte) (enc >> 8);
        i = off + 1;
        b[off] = (byte) enc;
        enc = TRIPLET_TO_CHARS[thousands];
        off = i + 1;
        b[i] = (byte) (enc >> 16);
        i = off + 1;
        b[off] = (byte) (enc >> 8);
        off = i + 1;
        b[i] = (byte) enc;
        enc = TRIPLET_TO_CHARS[ones];
        i = off + 1;
        b[off] = (byte) (enc >> 16);
        off = i + 1;
        b[i] = (byte) (enc >> 8);
        i = off + 1;
        b[off] = (byte) enc;
        return i;
    }

    private static int _outputUptoMillion(char[] b, int off, int thousands, int ones) {
        int off2;
        int enc = TRIPLET_TO_CHARS[thousands];
        if (thousands > 9) {
            if (thousands > 99) {
                off2 = off + 1;
                b[off] = enc >>> 16;
                off = off2;
            }
            off2 = off + 1;
            b[off] = (char) ((enc >> 8) & 127);
            off = off2;
        }
        off2 = off + 1;
        b[off] = (char) (enc & 127);
        enc = TRIPLET_TO_CHARS[ones];
        off = off2 + 1;
        b[off2] = enc >>> 16;
        off2 = off + 1;
        b[off] = (char) ((enc >> 8) & 127);
        off = off2 + 1;
        b[off2] = (char) (enc & 127);
        return off;
    }

    private static int _outputUptoMillion(byte[] b, int off, int thousands, int ones) {
        int off2;
        int enc = TRIPLET_TO_CHARS[thousands];
        if (thousands > 9) {
            if (thousands > 99) {
                off2 = off + 1;
                b[off] = (byte) (enc >> 16);
                off = off2;
            }
            off2 = off + 1;
            b[off] = (byte) (enc >> 8);
            off = off2;
        }
        off2 = off + 1;
        b[off] = (byte) enc;
        enc = TRIPLET_TO_CHARS[ones];
        off = off2 + 1;
        b[off2] = (byte) (enc >> 16);
        off2 = off + 1;
        b[off] = (byte) (enc >> 8);
        off = off2 + 1;
        b[off2] = (byte) enc;
        return off;
    }

    private static int _leading3(int t, char[] b, int off) {
        int off2;
        int enc = TRIPLET_TO_CHARS[t];
        if (t > 9) {
            if (t > 99) {
                off2 = off + 1;
                b[off] = enc >>> 16;
                off = off2;
            }
            off2 = off + 1;
            b[off] = (char) ((enc >> 8) & 127);
            off = off2;
        }
        off2 = off + 1;
        b[off] = (char) (enc & 127);
        return off2;
    }

    private static int _leading3(int t, byte[] b, int off) {
        int off2;
        int enc = TRIPLET_TO_CHARS[t];
        if (t > 9) {
            if (t > 99) {
                off2 = off + 1;
                b[off] = (byte) (enc >> 16);
                off = off2;
            }
            off2 = off + 1;
            b[off] = (byte) (enc >> 8);
            off = off2;
        }
        off2 = off + 1;
        b[off] = (byte) enc;
        return off2;
    }

    private static int _full3(int t, char[] b, int off) {
        int enc = TRIPLET_TO_CHARS[t];
        int i = off + 1;
        b[off] = enc >>> 16;
        off = i + 1;
        b[i] = (char) ((enc >> 8) & 127);
        i = off + 1;
        b[off] = (char) (enc & 127);
        return i;
    }

    private static int _full3(int t, byte[] b, int off) {
        int enc = TRIPLET_TO_CHARS[t];
        int i = off + 1;
        b[off] = (byte) (enc >> 16);
        off = i + 1;
        b[i] = (byte) (enc >> 8);
        i = off + 1;
        b[off] = (byte) enc;
        return i;
    }

    private static int _outputSmallestL(char[] b, int off) {
        int len = SMALLEST_LONG.length();
        SMALLEST_LONG.getChars(0, len, b, off);
        return off + len;
    }

    private static int _outputSmallestL(byte[] b, int off) {
        int len = SMALLEST_LONG.length();
        int i = 0;
        int off2 = off;
        while (i < len) {
            off = off2 + 1;
            b[off2] = (byte) SMALLEST_LONG.charAt(i);
            i++;
            off2 = off;
        }
        return off2;
    }

    private static int _outputSmallestI(char[] b, int off) {
        int len = SMALLEST_INT.length();
        SMALLEST_INT.getChars(0, len, b, off);
        return off + len;
    }

    private static int _outputSmallestI(byte[] b, int off) {
        int len = SMALLEST_INT.length();
        int i = 0;
        int off2 = off;
        while (i < len) {
            off = off2 + 1;
            b[off2] = (byte) SMALLEST_INT.charAt(i);
            i++;
            off2 = off;
        }
        return off2;
    }
}
