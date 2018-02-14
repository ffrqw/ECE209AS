package android.support.v4.util;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ArrayMap<K, V> extends SimpleArrayMap<K, V> implements Map<K, V> {
    MapCollections<K, V> mCollections;

    public ArrayMap(int capacity) {
        super(capacity);
    }

    private MapCollections<K, V> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<K, V>() {
                protected final int colGetSize() {
                    return ArrayMap.this.mSize;
                }

                protected final Object colGetEntry(int index, int offset) {
                    return ArrayMap.this.mArray[(index << 1) + offset];
                }

                protected final int colIndexOfKey(Object key) {
                    return ArrayMap.this.indexOfKey(key);
                }

                protected final int colIndexOfValue(Object value) {
                    return ArrayMap.this.indexOfValue(value);
                }

                protected final Map<K, V> colGetMap() {
                    return ArrayMap.this;
                }

                protected final void colPut(K key, V value) {
                    ArrayMap.this.put(key, value);
                }

                protected final V colSetValue(int index, V value) {
                    SimpleArrayMap simpleArrayMap = ArrayMap.this;
                    int i = (index << 1) + 1;
                    V v = simpleArrayMap.mArray[i];
                    simpleArrayMap.mArray[i] = value;
                    return v;
                }

                protected final void colRemoveAt(int index) {
                    ArrayMap.this.removeAt(index);
                }

                protected final void colClear() {
                    ArrayMap.this.clear();
                }
            };
        }
        return this.mCollections;
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(this.mSize + map.size());
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public final boolean retainAll(Collection<?> collection) {
        return MapCollections.retainAllHelper(this, collection);
    }

    public Set<Entry<K, V>> entrySet() {
        MapCollections collection = getCollection();
        if (collection.mEntrySet == null) {
            collection.mEntrySet = new EntrySet();
        }
        return collection.mEntrySet;
    }

    public Set<K> keySet() {
        return getCollection().getKeySet();
    }

    public Collection<V> values() {
        MapCollections collection = getCollection();
        if (collection.mValues == null) {
            collection.mValues = new ValuesCollection();
        }
        return collection.mValues;
    }
}
