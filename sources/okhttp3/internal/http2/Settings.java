package okhttp3.internal.http2;

public final class Settings {
    private int set;
    private final int[] values = new int[10];

    final Settings set(int id, int value) {
        if (id < 10) {
            this.set |= 1 << id;
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

    final int getInitialWindowSize() {
        return (this.set & 128) != 0 ? this.values[7] : 65535;
    }
}
