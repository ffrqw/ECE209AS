package android.support.v4.util;

public final class SparseArrayCompat<E> implements Cloneable {
    private static final Object DELETED = new Object();
    private boolean mGarbage;
    private int[] mKeys;
    private int mSize;
    private Object[] mValues;

    public SparseArrayCompat() {
        this(10);
    }

    public SparseArrayCompat(int initialCapacity) {
        this.mGarbage = false;
        if (initialCapacity == 0) {
            this.mKeys = ContainerHelpers.EMPTY_INTS;
            this.mValues = ContainerHelpers.EMPTY_OBJECTS;
        } else {
            initialCapacity = ContainerHelpers.idealIntArraySize(initialCapacity);
            this.mKeys = new int[initialCapacity];
            this.mValues = new Object[initialCapacity];
        }
        this.mSize = 0;
    }

    private SparseArrayCompat<E> clone() {
        SparseArrayCompat<E> clone = null;
        try {
            clone = (SparseArrayCompat) super.clone();
            clone.mKeys = (int[]) this.mKeys.clone();
            clone.mValues = (Object[]) this.mValues.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return clone;
        }
    }

    private void gc() {
        int n = this.mSize;
        int o = 0;
        int[] keys = this.mKeys;
        Object[] values = this.mValues;
        for (int i = 0; i < n; i++) {
            Object val = values[i];
            if (val != DELETED) {
                if (i != o) {
                    keys[o] = keys[i];
                    values[o] = val;
                    values[i] = null;
                }
                o++;
            }
        }
        this.mGarbage = false;
        this.mSize = o;
    }

    public final void put(int key, E value) {
        int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (i >= 0) {
            this.mValues[i] = value;
            return;
        }
        i ^= -1;
        if (i >= this.mSize || this.mValues[i] != DELETED) {
            if (this.mGarbage && this.mSize >= this.mKeys.length) {
                gc();
                i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key) ^ -1;
            }
            if (this.mSize >= this.mKeys.length) {
                int n = ContainerHelpers.idealIntArraySize(this.mSize + 1);
                int[] nkeys = new int[n];
                Object[] nvalues = new Object[n];
                System.arraycopy(this.mKeys, 0, nkeys, 0, this.mKeys.length);
                System.arraycopy(this.mValues, 0, nvalues, 0, this.mValues.length);
                this.mKeys = nkeys;
                this.mValues = nvalues;
            }
            if (this.mSize - i != 0) {
                System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
                System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
            }
            this.mKeys[i] = key;
            this.mValues[i] = value;
            this.mSize++;
            return;
        }
        this.mKeys[i] = key;
        this.mValues[i] = value;
    }

    public final int size() {
        if (this.mGarbage) {
            gc();
        }
        return this.mSize;
    }

    public final int keyAt(int index) {
        if (this.mGarbage) {
            gc();
        }
        return this.mKeys[index];
    }

    public final E valueAt(int index) {
        if (this.mGarbage) {
            gc();
        }
        return this.mValues[index];
    }

    public final int indexOfKey(int key) {
        if (this.mGarbage) {
            gc();
        }
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
    }

    public final void clear() {
        int n = this.mSize;
        Object[] values = this.mValues;
        for (int i = 0; i < n; i++) {
            values[i] = null;
        }
        this.mSize = 0;
        this.mGarbage = false;
    }

    public final void append(int key, E value) {
        if (this.mSize == 0 || key > this.mKeys[this.mSize - 1]) {
            if (this.mGarbage && this.mSize >= this.mKeys.length) {
                gc();
            }
            int pos = this.mSize;
            if (pos >= this.mKeys.length) {
                int n = ContainerHelpers.idealIntArraySize(pos + 1);
                int[] nkeys = new int[n];
                Object[] nvalues = new Object[n];
                System.arraycopy(this.mKeys, 0, nkeys, 0, this.mKeys.length);
                System.arraycopy(this.mValues, 0, nvalues, 0, this.mValues.length);
                this.mKeys = nkeys;
                this.mValues = nvalues;
            }
            this.mKeys[pos] = key;
            this.mValues[pos] = value;
            this.mSize = pos + 1;
            return;
        }
        put(key, value);
    }

    public final String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder buffer = new StringBuilder(this.mSize * 28);
        buffer.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(keyAt(i));
            buffer.append('=');
            SparseArrayCompat value = valueAt(i);
            if (value != this) {
                buffer.append(value);
            } else {
                buffer.append("(this Map)");
            }
        }
        buffer.append('}');
        return buffer.toString();
    }

    public final E get(int key) {
        int binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (binarySearch < 0 || this.mValues[binarySearch] == DELETED) {
            return null;
        }
        return this.mValues[binarySearch];
    }

    public final void remove(int key) {
        int binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, key);
        if (binarySearch >= 0 && this.mValues[binarySearch] != DELETED) {
            this.mValues[binarySearch] = DELETED;
            this.mGarbage = true;
        }
    }
}
