package okhttp3.internal.http2;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;

final class Hpack {
    static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX;
    static final Header[] STATIC_HEADER_TABLE = new Header[]{new Header(Header.TARGET_AUTHORITY, ""), new Header(Header.TARGET_METHOD, "GET"), new Header(Header.TARGET_METHOD, "POST"), new Header(Header.TARGET_PATH, "/"), new Header(Header.TARGET_PATH, "/index.html"), new Header(Header.TARGET_SCHEME, "http"), new Header(Header.TARGET_SCHEME, "https"), new Header(Header.RESPONSE_STATUS, "200"), new Header(Header.RESPONSE_STATUS, "204"), new Header(Header.RESPONSE_STATUS, "206"), new Header(Header.RESPONSE_STATUS, "304"), new Header(Header.RESPONSE_STATUS, "400"), new Header(Header.RESPONSE_STATUS, "404"), new Header(Header.RESPONSE_STATUS, "500"), new Header("accept-charset", ""), new Header("accept-encoding", "gzip, deflate"), new Header("accept-language", ""), new Header("accept-ranges", ""), new Header("accept", ""), new Header("access-control-allow-origin", ""), new Header("age", ""), new Header("allow", ""), new Header("authorization", ""), new Header("cache-control", ""), new Header("content-disposition", ""), new Header("content-encoding", ""), new Header("content-language", ""), new Header("content-length", ""), new Header("content-location", ""), new Header("content-range", ""), new Header("content-type", ""), new Header("cookie", ""), new Header("date", ""), new Header("etag", ""), new Header("expect", ""), new Header("expires", ""), new Header("from", ""), new Header("host", ""), new Header("if-match", ""), new Header("if-modified-since", ""), new Header("if-none-match", ""), new Header("if-range", ""), new Header("if-unmodified-since", ""), new Header("last-modified", ""), new Header("link", ""), new Header("location", ""), new Header("max-forwards", ""), new Header("proxy-authenticate", ""), new Header("proxy-authorization", ""), new Header("range", ""), new Header("referer", ""), new Header("refresh", ""), new Header("retry-after", ""), new Header("server", ""), new Header("set-cookie", ""), new Header("strict-transport-security", ""), new Header("transfer-encoding", ""), new Header("user-agent", ""), new Header("vary", ""), new Header("via", ""), new Header("www-authenticate", "")};

    static final class Reader {
        Header[] dynamicTable;
        int dynamicTableByteCount;
        int headerCount;
        private final List<Header> headerList;
        private final int headerTableSizeSetting;
        private int maxDynamicTableByteCount;
        int nextHeaderIndex;
        private final BufferedSource source;

        Reader(int headerTableSizeSetting, Source source) {
            this(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, source);
        }

        private Reader(int headerTableSizeSetting, int maxDynamicTableByteCount, Source source) {
            this.headerList = new ArrayList();
            this.dynamicTable = new Header[8];
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
            this.headerTableSizeSetting = headerTableSizeSetting;
            this.maxDynamicTableByteCount = maxDynamicTableByteCount;
            this.source = Okio.buffer(source);
        }

        private void adjustDynamicTableByteCount() {
            if (this.maxDynamicTableByteCount >= this.dynamicTableByteCount) {
                return;
            }
            if (this.maxDynamicTableByteCount == 0) {
                clearDynamicTable();
            } else {
                evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
            }
        }

        private void clearDynamicTable() {
            Arrays.fill(this.dynamicTable, null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }

        private int evictToRecoverBytes(int bytesToRecover) {
            int entriesToEvict = 0;
            if (bytesToRecover > 0) {
                for (int j = this.dynamicTable.length - 1; j >= this.nextHeaderIndex && bytesToRecover > 0; j--) {
                    bytesToRecover -= this.dynamicTable[j].hpackSize;
                    this.dynamicTableByteCount -= this.dynamicTable[j].hpackSize;
                    this.headerCount--;
                    entriesToEvict++;
                }
                System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, (this.nextHeaderIndex + 1) + entriesToEvict, this.headerCount);
                this.nextHeaderIndex += entriesToEvict;
            }
            return entriesToEvict;
        }

        final void readHeaders() throws IOException {
            while (!this.source.exhausted()) {
                int b = this.source.readByte() & 255;
                if (b == 128) {
                    throw new IOException("index == 0");
                } else if ((b & 128) == 128) {
                    int readInt = readInt(b, 127) - 1;
                    if (isStaticHeader(readInt)) {
                        this.headerList.add(Hpack.STATIC_HEADER_TABLE[readInt]);
                    } else {
                        Header[] headerArr = Hpack.STATIC_HEADER_TABLE;
                        int dynamicTableIndex = dynamicTableIndex(readInt - 61);
                        if (dynamicTableIndex < 0 || dynamicTableIndex > this.dynamicTable.length - 1) {
                            throw new IOException("Header index too large " + (readInt + 1));
                        }
                        this.headerList.add(this.dynamicTable[dynamicTableIndex]);
                    }
                } else if (b == 64) {
                    insertIntoDynamicTable(-1, new Header(Hpack.checkLowercase(readByteString()), readByteString()));
                } else if ((b & 64) == 64) {
                    insertIntoDynamicTable(-1, new Header(getName(readInt(b, 63) - 1), readByteString()));
                } else if ((b & 32) == 32) {
                    this.maxDynamicTableByteCount = readInt(b, 31);
                    if (this.maxDynamicTableByteCount < 0 || this.maxDynamicTableByteCount > this.headerTableSizeSetting) {
                        throw new IOException("Invalid dynamic table size update " + this.maxDynamicTableByteCount);
                    }
                    adjustDynamicTableByteCount();
                } else if (b == 16 || b == 0) {
                    this.headerList.add(new Header(Hpack.checkLowercase(readByteString()), readByteString()));
                } else {
                    this.headerList.add(new Header(getName(readInt(b, 15) - 1), readByteString()));
                }
            }
        }

        public final List<Header> getAndResetHeaderList() {
            List<Header> result = new ArrayList(this.headerList);
            this.headerList.clear();
            return result;
        }

        private int dynamicTableIndex(int index) {
            return (this.nextHeaderIndex + 1) + index;
        }

        private ByteString getName(int index) {
            if (isStaticHeader(index)) {
                return Hpack.STATIC_HEADER_TABLE[index].name;
            }
            Header[] headerArr = this.dynamicTable;
            Header[] headerArr2 = Hpack.STATIC_HEADER_TABLE;
            return headerArr[dynamicTableIndex(index - 61)].name;
        }

        private static boolean isStaticHeader(int index) {
            if (index >= 0) {
                Header[] headerArr = Hpack.STATIC_HEADER_TABLE;
                if (index <= 60) {
                    return true;
                }
            }
            return false;
        }

        private void insertIntoDynamicTable(int index, Header entry) {
            this.headerList.add(entry);
            int delta = entry.hpackSize;
            if (delta > this.maxDynamicTableByteCount) {
                clearDynamicTable();
                return;
            }
            evictToRecoverBytes((this.dynamicTableByteCount + delta) - this.maxDynamicTableByteCount);
            if (this.headerCount + 1 > this.dynamicTable.length) {
                Header[] doubled = new Header[(this.dynamicTable.length << 1)];
                System.arraycopy(this.dynamicTable, 0, doubled, this.dynamicTable.length, this.dynamicTable.length);
                this.nextHeaderIndex = this.dynamicTable.length - 1;
                this.dynamicTable = doubled;
            }
            index = this.nextHeaderIndex;
            this.nextHeaderIndex = index - 1;
            this.dynamicTable[index] = entry;
            this.headerCount++;
            this.dynamicTableByteCount += delta;
        }

        private int readByte() throws IOException {
            return this.source.readByte() & 255;
        }

        private int readInt(int firstByte, int prefixMask) throws IOException {
            int prefix = firstByte & prefixMask;
            if (prefix < prefixMask) {
                return prefix;
            }
            int result = prefixMask;
            int shift = 0;
            while (true) {
                int b = readByte();
                if ((b & 128) == 0) {
                    return result + (b << shift);
                }
                result += (b & 127) << shift;
                shift += 7;
            }
        }

        private ByteString readByteString() throws IOException {
            int firstByte = readByte();
            boolean huffmanDecode = (firstByte & 128) == 128;
            int length = readInt(firstByte, 127);
            if (huffmanDecode) {
                return ByteString.of(Huffman.get().decode(this.source.readByteArray((long) length)));
            }
            return this.source.readByteString((long) length);
        }
    }

    static final class Writer {
        Header[] dynamicTable;
        int dynamicTableByteCount;
        private boolean emitDynamicTableSizeUpdate;
        int headerCount;
        int headerTableSizeSetting;
        int maxDynamicTableByteCount;
        int nextHeaderIndex;
        private final Buffer out;
        private int smallestHeaderTableSizeSetting;
        private final boolean useCompression;

        Writer(Buffer out) {
            this(ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT, true, out);
        }

        private Writer(int headerTableSizeSetting, boolean useCompression, Buffer out) {
            this.smallestHeaderTableSizeSetting = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            this.dynamicTable = new Header[8];
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
            this.headerTableSizeSetting = ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT;
            this.maxDynamicTableByteCount = ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT;
            this.useCompression = true;
            this.out = out;
        }

        private void clearDynamicTable() {
            Arrays.fill(this.dynamicTable, null);
            this.nextHeaderIndex = this.dynamicTable.length - 1;
            this.headerCount = 0;
            this.dynamicTableByteCount = 0;
        }

        private int evictToRecoverBytes(int bytesToRecover) {
            int entriesToEvict = 0;
            if (bytesToRecover > 0) {
                for (int j = this.dynamicTable.length - 1; j >= this.nextHeaderIndex && bytesToRecover > 0; j--) {
                    bytesToRecover -= this.dynamicTable[j].hpackSize;
                    this.dynamicTableByteCount -= this.dynamicTable[j].hpackSize;
                    this.headerCount--;
                    entriesToEvict++;
                }
                System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, (this.nextHeaderIndex + 1) + entriesToEvict, this.headerCount);
                Arrays.fill(this.dynamicTable, this.nextHeaderIndex + 1, (this.nextHeaderIndex + 1) + entriesToEvict, null);
                this.nextHeaderIndex += entriesToEvict;
            }
            return entriesToEvict;
        }

        private void insertIntoDynamicTable(Header entry) {
            int delta = entry.hpackSize;
            if (delta > this.maxDynamicTableByteCount) {
                clearDynamicTable();
                return;
            }
            evictToRecoverBytes((this.dynamicTableByteCount + delta) - this.maxDynamicTableByteCount);
            if (this.headerCount + 1 > this.dynamicTable.length) {
                Header[] doubled = new Header[(this.dynamicTable.length << 1)];
                System.arraycopy(this.dynamicTable, 0, doubled, this.dynamicTable.length, this.dynamicTable.length);
                this.nextHeaderIndex = this.dynamicTable.length - 1;
                this.dynamicTable = doubled;
            }
            int index = this.nextHeaderIndex;
            this.nextHeaderIndex = index - 1;
            this.dynamicTable[index] = entry;
            this.headerCount++;
            this.dynamicTableByteCount += delta;
        }

        final void writeHeaders(List<Header> headerBlock) throws IOException {
            if (this.emitDynamicTableSizeUpdate) {
                if (this.smallestHeaderTableSizeSetting < this.maxDynamicTableByteCount) {
                    writeInt(this.smallestHeaderTableSizeSetting, 31, 32);
                }
                this.emitDynamicTableSizeUpdate = false;
                this.smallestHeaderTableSizeSetting = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                writeInt(this.maxDynamicTableByteCount, 31, 32);
            }
            int size = headerBlock.size();
            for (int i = 0; i < size; i++) {
                Header header = (Header) headerBlock.get(i);
                ByteString name = header.name.toAsciiLowercase();
                ByteString value = header.value;
                int headerIndex = -1;
                int headerNameIndex = -1;
                Integer staticIndex = (Integer) Hpack.NAME_TO_FIRST_INDEX.get(name);
                if (staticIndex != null) {
                    headerNameIndex = staticIndex.intValue() + 1;
                    if (headerNameIndex > 1 && headerNameIndex < 8) {
                        if (Util.equal(Hpack.STATIC_HEADER_TABLE[headerNameIndex - 1].value, value)) {
                            headerIndex = headerNameIndex;
                        } else if (Util.equal(Hpack.STATIC_HEADER_TABLE[headerNameIndex].value, value)) {
                            headerIndex = headerNameIndex + 1;
                        }
                    }
                }
                if (headerIndex == -1) {
                    int length = this.dynamicTable.length;
                    for (int j = this.nextHeaderIndex + 1; j < length; j++) {
                        if (Util.equal(this.dynamicTable[j].name, name)) {
                            int i2;
                            Header[] headerArr;
                            if (Util.equal(this.dynamicTable[j].value, value)) {
                                i2 = j - this.nextHeaderIndex;
                                headerArr = Hpack.STATIC_HEADER_TABLE;
                                headerIndex = i2 + 61;
                                break;
                            } else if (headerNameIndex == -1) {
                                i2 = j - this.nextHeaderIndex;
                                headerArr = Hpack.STATIC_HEADER_TABLE;
                                headerNameIndex = i2 + 61;
                            }
                        }
                    }
                }
                if (headerIndex != -1) {
                    writeInt(headerIndex, 127, 128);
                } else if (headerNameIndex == -1) {
                    this.out.writeByte(64);
                    writeByteString(name);
                    writeByteString(value);
                    insertIntoDynamicTable(header);
                } else {
                    ByteString byteString = Header.PSEUDO_PREFIX;
                    if (!name.rangeEquals(0, byteString, 0, byteString.size()) || Header.TARGET_AUTHORITY.equals(name)) {
                        writeInt(headerNameIndex, 63, 64);
                        writeByteString(value);
                        insertIntoDynamicTable(header);
                    } else {
                        writeInt(headerNameIndex, 15, 0);
                        writeByteString(value);
                    }
                }
            }
        }

        private void writeInt(int value, int prefixMask, int bits) {
            if (value < prefixMask) {
                this.out.writeByte(bits | value);
                return;
            }
            this.out.writeByte(bits | prefixMask);
            value -= prefixMask;
            while (value >= 128) {
                this.out.writeByte((value & 127) | 128);
                value >>>= 7;
            }
            this.out.writeByte(value);
        }

        private void writeByteString(ByteString data) throws IOException {
            if (this.useCompression) {
                Huffman.get();
                if (Huffman.encodedLength(data) < data.size()) {
                    Buffer huffmanBuffer = new Buffer();
                    Huffman.get();
                    Huffman.encode(data, huffmanBuffer);
                    ByteString huffmanBytes = huffmanBuffer.readByteString();
                    writeInt(huffmanBytes.size(), 127, 128);
                    this.out.write(huffmanBytes);
                    return;
                }
            }
            writeInt(data.size(), 127, 0);
            this.out.write(data);
        }

        final void setHeaderTableSizeSetting(int headerTableSizeSetting) {
            this.headerTableSizeSetting = headerTableSizeSetting;
            int effectiveHeaderTableSize = Math.min(headerTableSizeSetting, 16384);
            if (this.maxDynamicTableByteCount != effectiveHeaderTableSize) {
                if (effectiveHeaderTableSize < this.maxDynamicTableByteCount) {
                    this.smallestHeaderTableSizeSetting = Math.min(this.smallestHeaderTableSizeSetting, effectiveHeaderTableSize);
                }
                this.emitDynamicTableSizeUpdate = true;
                this.maxDynamicTableByteCount = effectiveHeaderTableSize;
                if (this.maxDynamicTableByteCount >= this.dynamicTableByteCount) {
                    return;
                }
                if (this.maxDynamicTableByteCount == 0) {
                    clearDynamicTable();
                } else {
                    evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
                }
            }
        }
    }

    static {
        int i = 0;
        Map linkedHashMap = new LinkedHashMap(61);
        while (i < 61) {
            if (!linkedHashMap.containsKey(STATIC_HEADER_TABLE[i].name)) {
                linkedHashMap.put(STATIC_HEADER_TABLE[i].name, Integer.valueOf(i));
            }
            i++;
        }
        NAME_TO_FIRST_INDEX = Collections.unmodifiableMap(linkedHashMap);
    }

    static ByteString checkLowercase(ByteString name) throws IOException {
        int i = 0;
        int length = name.size();
        while (i < length) {
            byte c = name.getByte(i);
            if (c < (byte) 65 || c > (byte) 90) {
                i++;
            } else {
                throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + name.utf8());
            }
        }
        return name;
    }
}
