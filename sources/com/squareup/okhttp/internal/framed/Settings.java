package com.squareup.okhttp.internal.framed;

import java.util.Arrays;

public final class Settings {
    private int persistValue;
    private int persisted;
    private int set;
    private final int[] values = new int[10];

    final void clear() {
        this.persisted = 0;
        this.persistValue = 0;
        this.set = 0;
        Arrays.fill(this.values, 0);
    }

    final Settings set(int id, int idFlags, int value) {
        if (id < 10) {
            int bit = 1 << id;
            this.set |= bit;
            if ((idFlags & 1) != 0) {
                this.persistValue |= bit;
            } else {
                this.persistValue &= bit ^ -1;
            }
            if ((idFlags & 2) != 0) {
                this.persisted |= bit;
            } else {
                this.persisted &= bit ^ -1;
            }
            this.values[id] = value;
        }
        return this;
    }

    final boolean isSet(int id) {
        if ((this.set & (1 << id)) != 0) {
            return true;
        }
        return false;
    }

    final int get(int id) {
        return this.values[id];
    }

    final int flags(int id) {
        int i;
        int i2 = 1;
        int result = 0;
        if (((1 << id) & this.persisted) != 0) {
            i = 1;
        } else {
            i = 0;
        }
        if (i != 0) {
            result = 2;
        }
        if (((1 << id) & this.persistValue) == 0) {
            i2 = 0;
        }
        if (i2 != 0) {
            return result | 1;
        }
        return result;
    }

    final int size() {
        return Integer.bitCount(this.set);
    }

    final int getHeaderTableSize() {
        return (this.set & 2) != 0 ? this.values[1] : -1;
    }

    final int getMaxConcurrentStreams(int defaultValue) {
        return (this.set & 16) != 0 ? this.values[4] : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    }

    final int getMaxFrameSize(int defaultValue) {
        return (this.set & 32) != 0 ? this.values[5] : defaultValue;
    }

    final int getInitialWindowSize(int defaultValue) {
        return (this.set & 128) != 0 ? this.values[7] : 65536;
    }
}
