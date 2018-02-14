package com.fasterxml.jackson.core.sym;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.core.util.InternCache;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public final class ByteQuadsCanonicalizer {
    private int _count;
    private final boolean _failOnDoS;
    private int[] _hashArea;
    private boolean _hashShared;
    private int _hashSize;
    private boolean _intern;
    private int _longNameOffset;
    private String[] _names;
    private transient boolean _needRehash;
    private final ByteQuadsCanonicalizer _parent;
    private int _secondaryStart;
    private final int _seed;
    private int _spilloverEnd;
    private final AtomicReference<TableInfo> _tableInfo;
    private int _tertiaryShift;
    private int _tertiaryStart;

    private static final class TableInfo {
        public final int count;
        public final int longNameOffset;
        public final int[] mainHash;
        public final String[] names;
        public final int size;
        public final int spilloverEnd;
        public final int tertiaryShift;

        public TableInfo(int size, int count, int tertiaryShift, int[] mainHash, String[] names, int spilloverEnd, int longNameOffset) {
            this.size = size;
            this.count = count;
            this.tertiaryShift = tertiaryShift;
            this.mainHash = mainHash;
            this.names = names;
            this.spilloverEnd = spilloverEnd;
            this.longNameOffset = longNameOffset;
        }

        public TableInfo(ByteQuadsCanonicalizer src) {
            this.size = src._hashSize;
            this.count = src._count;
            this.tertiaryShift = src._tertiaryShift;
            this.mainHash = src._hashArea;
            this.names = src._names;
            this.spilloverEnd = src._spilloverEnd;
            this.longNameOffset = src._longNameOffset;
        }

        public static TableInfo createInitial(int sz) {
            int hashAreaSize = sz << 3;
            return new TableInfo(sz, 0, ByteQuadsCanonicalizer._calcTertiaryShift(sz), new int[hashAreaSize], new String[(sz << 1)], hashAreaSize - sz, hashAreaSize);
        }
    }

    private ByteQuadsCanonicalizer(int sz, boolean intern, int seed, boolean failOnDoS) {
        this._parent = null;
        this._seed = seed;
        this._intern = intern;
        this._failOnDoS = failOnDoS;
        if (sz < 16) {
            sz = 16;
        } else if (((sz - 1) & sz) != 0) {
            int curr = 16;
            while (curr < sz) {
                curr += curr;
            }
            sz = curr;
        }
        this._tableInfo = new AtomicReference(TableInfo.createInitial(sz));
    }

    private ByteQuadsCanonicalizer(ByteQuadsCanonicalizer parent, boolean intern, int seed, boolean failOnDoS, TableInfo state) {
        this._parent = parent;
        this._seed = seed;
        this._intern = intern;
        this._failOnDoS = failOnDoS;
        this._tableInfo = null;
        this._count = state.count;
        this._hashSize = state.size;
        this._secondaryStart = this._hashSize << 2;
        this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
        this._tertiaryShift = state.tertiaryShift;
        this._hashArea = state.mainHash;
        this._names = state.names;
        this._spilloverEnd = state.spilloverEnd;
        this._longNameOffset = state.longNameOffset;
        this._needRehash = false;
        this._hashShared = true;
    }

    public static ByteQuadsCanonicalizer createRoot() {
        long now = System.currentTimeMillis();
        return createRoot((((int) now) + ((int) (now >>> 32))) | 1);
    }

    protected static ByteQuadsCanonicalizer createRoot(int seed) {
        return new ByteQuadsCanonicalizer(64, true, seed, true);
    }

    public final ByteQuadsCanonicalizer makeChild(int flags) {
        return new ByteQuadsCanonicalizer(this, Feature.INTERN_FIELD_NAMES.enabledIn(flags), this._seed, Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(flags), (TableInfo) this._tableInfo.get());
    }

    public final void release() {
        if (this._parent != null && maybeDirty()) {
            this._parent.mergeChild(new TableInfo(this));
            this._hashShared = true;
        }
    }

    private void mergeChild(TableInfo childState) {
        int childCount = childState.count;
        TableInfo currState = (TableInfo) this._tableInfo.get();
        if (childCount != currState.count) {
            if (childCount > 6000) {
                childState = TableInfo.createInitial(64);
            }
            this._tableInfo.compareAndSet(currState, childState);
        }
    }

    public final boolean maybeDirty() {
        return !this._hashShared;
    }

    public final int primaryCount() {
        int count = 0;
        int end = this._secondaryStart;
        for (int offset = 3; offset < end; offset += 4) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
        }
        return count;
    }

    public final int secondaryCount() {
        int count = 0;
        int end = this._tertiaryStart;
        for (int offset = this._secondaryStart + 3; offset < end; offset += 4) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
        }
        return count;
    }

    public final int tertiaryCount() {
        int count = 0;
        int offset = this._tertiaryStart + 3;
        int end = offset + this._hashSize;
        while (offset < end) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
            offset += 4;
        }
        return count;
    }

    public final int spilloverCount() {
        return (this._spilloverEnd - _spilloverStart()) >> 2;
    }

    public final int totalCount() {
        int count = 0;
        int end = this._hashSize << 3;
        for (int offset = 3; offset < end; offset += 4) {
            if (this._hashArea[offset] != 0) {
                count++;
            }
        }
        return count;
    }

    public final String toString() {
        int pri = primaryCount();
        int sec = secondaryCount();
        int tert = tertiaryCount();
        int spill = spilloverCount();
        int total = totalCount();
        return String.format("[%s: size=%d, hashSize=%d, %d/%d/%d/%d pri/sec/ter/spill (=%s), total:%d]", new Object[]{getClass().getName(), Integer.valueOf(this._count), Integer.valueOf(this._hashSize), Integer.valueOf(pri), Integer.valueOf(sec), Integer.valueOf(tert), Integer.valueOf(spill), Integer.valueOf(((pri + sec) + tert) + spill), Integer.valueOf(total)});
    }

    public final String findName(int q1) {
        int offset = _calcOffset(calcHash(q1));
        int[] hashArea = this._hashArea;
        int len = hashArea[offset + 3];
        if (len == 1) {
            if (hashArea[offset] == q1) {
                return this._names[offset >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        len = hashArea[offset2 + 3];
        if (len == 1) {
            if (hashArea[offset2] == q1) {
                return this._names[offset2 >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        return _findSecondary(offset, q1);
    }

    public final String findName(int q1, int q2) {
        int offset = _calcOffset(calcHash(q1, q2));
        int[] hashArea = this._hashArea;
        int len = hashArea[offset + 3];
        if (len == 2) {
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1]) {
                return this._names[offset >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        len = hashArea[offset2 + 3];
        if (len == 2) {
            if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1]) {
                return this._names[offset2 >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        return _findSecondary(offset, q1, q2);
    }

    public final String findName(int q1, int q2, int q3) {
        int offset = _calcOffset(calcHash(q1, q2, q3));
        int[] hashArea = this._hashArea;
        int len = hashArea[offset + 3];
        if (len == 3) {
            if (q1 == hashArea[offset] && hashArea[offset + 1] == q2 && hashArea[offset + 2] == q3) {
                return this._names[offset >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        len = hashArea[offset2 + 3];
        if (len == 3) {
            if (q1 == hashArea[offset2] && hashArea[offset2 + 1] == q2 && hashArea[offset2 + 2] == q3) {
                return this._names[offset2 >> 2];
            }
        } else if (len == 0) {
            return null;
        }
        return _findSecondary(offset, q1, q2, q3);
    }

    public final String findName(int[] q, int qlen) {
        if (qlen >= 4) {
            int hash = calcHash(q, qlen);
            int offset = _calcOffset(hash);
            int[] hashArea = this._hashArea;
            int len = hashArea[offset + 3];
            if (hash == hashArea[offset] && len == qlen && _verifyLongName(q, qlen, hashArea[offset + 1])) {
                return this._names[offset >> 2];
            }
            if (len == 0) {
                return null;
            }
            int offset2 = this._secondaryStart + ((offset >> 3) << 2);
            int len2 = hashArea[offset2 + 3];
            if (hash == hashArea[offset2] && len2 == qlen && _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
                return this._names[offset2 >> 2];
            }
            return _findSecondary(offset, hash, q, qlen);
        } else if (qlen == 3) {
            return findName(q[0], q[1], q[2]);
        } else {
            if (qlen == 2) {
                return findName(q[0], q[1]);
            }
            return findName(q[0]);
        }
    }

    private final int _calcOffset(int hash) {
        return (hash & (this._hashSize - 1)) << 2;
    }

    private String _findSecondary(int origOffset, int q1) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int end = offset + (1 << this._tertiaryShift);
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (q1 == hashArea[offset] && 1 == len) {
                return this._names[offset >> 2];
            }
            if (len == 0) {
                return null;
            }
            offset += 4;
        }
        offset = _spilloverStart();
        while (offset < this._spilloverEnd) {
            if (q1 == hashArea[offset] && 1 == hashArea[offset + 3]) {
                return this._names[offset >> 2];
            }
            offset += 4;
        }
        return null;
    }

    private String _findSecondary(int origOffset, int q1, int q2) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int end = offset + (1 << this._tertiaryShift);
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == len) {
                return this._names[offset >> 2];
            }
            if (len == 0) {
                return null;
            }
            offset += 4;
        }
        offset = _spilloverStart();
        while (offset < this._spilloverEnd) {
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == hashArea[offset + 3]) {
                return this._names[offset >> 2];
            }
            offset += 4;
        }
        return null;
    }

    private String _findSecondary(int origOffset, int q1, int q2, int q3) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int end = offset + (1 << this._tertiaryShift);
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == len) {
                return this._names[offset >> 2];
            }
            if (len == 0) {
                return null;
            }
            offset += 4;
        }
        offset = _spilloverStart();
        while (offset < this._spilloverEnd) {
            if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == hashArea[offset + 3]) {
                return this._names[offset >> 2];
            }
            offset += 4;
        }
        return null;
    }

    private String _findSecondary(int origOffset, int hash, int[] q, int qlen) {
        int offset = this._tertiaryStart + ((origOffset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int[] hashArea = this._hashArea;
        int end = offset + (1 << this._tertiaryShift);
        while (offset < end) {
            int len = hashArea[offset + 3];
            if (hash == hashArea[offset] && qlen == len && _verifyLongName(q, qlen, hashArea[offset + 1])) {
                return this._names[offset >> 2];
            }
            if (len == 0) {
                return null;
            }
            offset += 4;
        }
        offset = _spilloverStart();
        while (offset < this._spilloverEnd) {
            if (hash == hashArea[offset] && qlen == hashArea[offset + 3] && _verifyLongName(q, qlen, hashArea[offset + 1])) {
                return this._names[offset >> 2];
            }
            offset += 4;
        }
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean _verifyLongName(int[] r8, int r9, int r10) {
        /*
        r7 = this;
        r4 = 0;
        r0 = r7._hashArea;
        r1 = 0;
        switch(r9) {
            case 4: goto L_0x0046;
            case 5: goto L_0x0037;
            case 6: goto L_0x0028;
            case 7: goto L_0x0019;
            case 8: goto L_0x000c;
            default: goto L_0x0007;
        };
    L_0x0007:
        r4 = r7._verifyLongName2(r8, r9, r10);
    L_0x000b:
        return r4;
    L_0x000c:
        r1 = r1 + 1;
        r5 = r8[r4];
        r3 = r10 + 1;
        r6 = r0[r10];
        if (r5 == r6) goto L_0x0018;
    L_0x0016:
        r10 = r3;
        goto L_0x000b;
    L_0x0018:
        r10 = r3;
    L_0x0019:
        r2 = r1 + 1;
        r5 = r8[r1];
        r3 = r10 + 1;
        r6 = r0[r10];
        if (r5 == r6) goto L_0x0026;
    L_0x0023:
        r1 = r2;
        r10 = r3;
        goto L_0x000b;
    L_0x0026:
        r1 = r2;
        r10 = r3;
    L_0x0028:
        r2 = r1 + 1;
        r5 = r8[r1];
        r3 = r10 + 1;
        r6 = r0[r10];
        if (r5 == r6) goto L_0x0035;
    L_0x0032:
        r1 = r2;
        r10 = r3;
        goto L_0x000b;
    L_0x0035:
        r1 = r2;
        r10 = r3;
    L_0x0037:
        r2 = r1 + 1;
        r5 = r8[r1];
        r3 = r10 + 1;
        r6 = r0[r10];
        if (r5 == r6) goto L_0x0044;
    L_0x0041:
        r1 = r2;
        r10 = r3;
        goto L_0x000b;
    L_0x0044:
        r1 = r2;
        r10 = r3;
    L_0x0046:
        r2 = r1 + 1;
        r5 = r8[r1];
        r3 = r10 + 1;
        r6 = r0[r10];
        if (r5 == r6) goto L_0x0053;
    L_0x0050:
        r1 = r2;
        r10 = r3;
        goto L_0x000b;
    L_0x0053:
        r1 = r2 + 1;
        r5 = r8[r2];
        r10 = r3 + 1;
        r6 = r0[r3];
        if (r5 != r6) goto L_0x000b;
    L_0x005d:
        r2 = r1 + 1;
        r5 = r8[r1];
        r3 = r10 + 1;
        r6 = r0[r10];
        if (r5 == r6) goto L_0x006a;
    L_0x0067:
        r1 = r2;
        r10 = r3;
        goto L_0x000b;
    L_0x006a:
        r5 = r8[r2];
        r6 = r0[r3];
        if (r5 == r6) goto L_0x0073;
    L_0x0070:
        r1 = r2;
        r10 = r3;
        goto L_0x000b;
    L_0x0073:
        r4 = 1;
        r1 = r2;
        r10 = r3;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer._verifyLongName(int[], int, int):boolean");
    }

    private boolean _verifyLongName2(int[] q, int qlen, int spillOffset) {
        int ix = 0;
        while (true) {
            int ix2 = ix + 1;
            int spillOffset2 = spillOffset + 1;
            if (q[ix] != this._hashArea[spillOffset]) {
                return false;
            }
            if (ix2 >= qlen) {
                return true;
            }
            ix = ix2;
            spillOffset = spillOffset2;
        }
    }

    public final String addName(String name, int[] q, int qlen) {
        int offset;
        _verifySharing();
        if (this._intern) {
            name = InternCache.instance.intern(name);
        }
        switch (qlen) {
            case 1:
                offset = _findOffsetForAdd(calcHash(q[0]));
                this._hashArea[offset] = q[0];
                this._hashArea[offset + 3] = 1;
                break;
            case 2:
                offset = _findOffsetForAdd(calcHash(q[0], q[1]));
                this._hashArea[offset] = q[0];
                this._hashArea[offset + 1] = q[1];
                this._hashArea[offset + 3] = 2;
                break;
            case 3:
                offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2]));
                this._hashArea[offset] = q[0];
                this._hashArea[offset + 1] = q[1];
                this._hashArea[offset + 2] = q[2];
                this._hashArea[offset + 3] = 3;
                break;
            default:
                int hash = calcHash(q, qlen);
                offset = _findOffsetForAdd(hash);
                this._hashArea[offset] = hash;
                this._hashArea[offset + 1] = _appendLongName(q, qlen);
                this._hashArea[offset + 3] = qlen;
                break;
        }
        this._names[offset >> 2] = name;
        this._count++;
        _verifyNeedForRehash();
        return name;
    }

    private void _verifyNeedForRehash() {
        if (this._count <= (this._hashSize >> 1)) {
            return;
        }
        if (((this._spilloverEnd - _spilloverStart()) >> 2) > ((this._count + 1) >> 7) || ((double) this._count) > ((double) this._hashSize) * 0.8d) {
            this._needRehash = true;
        }
    }

    private void _verifySharing() {
        if (this._hashShared) {
            this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
            this._names = (String[]) Arrays.copyOf(this._names, this._names.length);
            this._hashShared = false;
            _verifyNeedForRehash();
        }
        if (this._needRehash) {
            rehash();
        }
    }

    private int _findOffsetForAdd(int hash) {
        int offset = _calcOffset(hash);
        int[] hashArea = this._hashArea;
        if (hashArea[offset + 3] == 0) {
            return offset;
        }
        int offset2 = this._secondaryStart + ((offset >> 3) << 2);
        if (hashArea[offset2 + 3] == 0) {
            return offset2;
        }
        offset2 = this._tertiaryStart + ((offset >> (this._tertiaryShift + 2)) << this._tertiaryShift);
        int end = offset2 + (1 << this._tertiaryShift);
        while (offset2 < end) {
            if (hashArea[offset2 + 3] == 0) {
                return offset2;
            }
            offset2 += 4;
        }
        offset = this._spilloverEnd;
        this._spilloverEnd += 4;
        if (this._spilloverEnd >= (this._hashSize << 3)) {
            if (this._failOnDoS) {
                _reportTooManyCollisions();
            }
            this._needRehash = true;
        }
        return offset;
    }

    private int _appendLongName(int[] quads, int qlen) {
        int start = this._longNameOffset;
        if (start + qlen > this._hashArea.length) {
            this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length + Math.max((start + qlen) - this._hashArea.length, Math.min(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, this._hashSize)));
        }
        System.arraycopy(quads, 0, this._hashArea, start, qlen);
        this._longNameOffset += qlen;
        return start;
    }

    public final int calcHash(int q1) {
        int hash = q1 ^ this._seed;
        hash += hash >>> 16;
        hash ^= hash << 3;
        return hash + (hash >>> 12);
    }

    public final int calcHash(int q1, int q2) {
        int hash = q1 + (q1 >>> 15);
        hash = ((hash ^ (hash >>> 9)) + (q2 * 33)) ^ this._seed;
        hash += hash >>> 16;
        hash ^= hash >>> 4;
        return hash + (hash << 3);
    }

    public final int calcHash(int q1, int q2, int q3) {
        int hash = q1 ^ this._seed;
        hash = (((hash + (hash >>> 9)) * 31) + q2) * 33;
        hash = (hash + (hash >>> 15)) ^ q3;
        hash += hash >>> 4;
        hash += hash >>> 15;
        return hash ^ (hash << 9);
    }

    public final int calcHash(int[] q, int qlen) {
        if (qlen < 4) {
            throw new IllegalArgumentException();
        }
        int hash = q[0] ^ this._seed;
        hash = (hash + (hash >>> 9)) + q[1];
        hash = ((hash + (hash >>> 15)) * 33) ^ q[2];
        hash += hash >>> 4;
        for (int i = 3; i < qlen; i++) {
            int next = q[i];
            hash += next ^ (next >> 21);
        }
        hash *= 65599;
        hash += hash >>> 19;
        return hash ^ (hash << 5);
    }

    private void rehash() {
        this._needRehash = false;
        this._hashShared = false;
        int[] oldHashArea = this._hashArea;
        String[] oldNames = this._names;
        int oldSize = this._hashSize;
        int oldCount = this._count;
        int newSize = oldSize + oldSize;
        int oldEnd = this._spilloverEnd;
        if (newSize > 65536) {
            nukeSymbols(true);
            return;
        }
        this._hashArea = new int[(oldHashArea.length + (oldSize << 3))];
        this._hashSize = newSize;
        this._secondaryStart = newSize << 2;
        this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
        this._tertiaryShift = _calcTertiaryShift(newSize);
        this._names = new String[(oldNames.length << 1)];
        nukeSymbols(false);
        int copyCount = 0;
        int[] q = new int[16];
        for (int offset = 0; offset < oldEnd; offset += 4) {
            int len = oldHashArea[offset + 3];
            if (len != 0) {
                copyCount++;
                String name = oldNames[offset >> 2];
                switch (len) {
                    case 1:
                        q[0] = oldHashArea[offset];
                        addName(name, q, 1);
                        break;
                    case 2:
                        q[0] = oldHashArea[offset];
                        q[1] = oldHashArea[offset + 1];
                        addName(name, q, 2);
                        break;
                    case 3:
                        q[0] = oldHashArea[offset];
                        q[1] = oldHashArea[offset + 1];
                        q[2] = oldHashArea[offset + 2];
                        addName(name, q, 3);
                        break;
                    default:
                        if (len > q.length) {
                            q = new int[len];
                        }
                        System.arraycopy(oldHashArea, oldHashArea[offset + 1], q, 0, len);
                        addName(name, q, len);
                        break;
                }
            }
        }
        if (copyCount != oldCount) {
            throw new IllegalStateException("Failed rehash(): old count=" + oldCount + ", copyCount=" + copyCount);
        }
    }

    private void nukeSymbols(boolean fill) {
        this._count = 0;
        this._spilloverEnd = _spilloverStart();
        this._longNameOffset = this._hashSize << 3;
        if (fill) {
            Arrays.fill(this._hashArea, 0);
            Arrays.fill(this._names, null);
        }
    }

    private final int _spilloverStart() {
        int offset = this._hashSize;
        return (offset << 3) - offset;
    }

    protected final void _reportTooManyCollisions() {
        if (this._hashSize > 1024) {
            throw new IllegalStateException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions. You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
        }
    }

    static int _calcTertiaryShift(int primarySlots) {
        int tertSlots = primarySlots >> 2;
        if (tertSlots < 64) {
            return 4;
        }
        if (tertSlots <= 256) {
            return 5;
        }
        if (tertSlots <= 1024) {
            return 6;
        }
        return 7;
    }
}
