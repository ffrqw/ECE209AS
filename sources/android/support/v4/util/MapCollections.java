package android.support.v4.util;

import com.j256.ormlite.stmt.query.SimpleComparison;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class MapCollections<K, V> {
    EntrySet mEntrySet;
    KeySet mKeySet;
    ValuesCollection mValues;

    final class ArrayIterator<T> implements Iterator<T> {
        boolean mCanRemove = false;
        int mIndex;
        final int mOffset;
        int mSize;

        ArrayIterator(int offset) {
            this.mOffset = offset;
            this.mSize = MapCollections.this.colGetSize();
        }

        public final boolean hasNext() {
            return this.mIndex < this.mSize;
        }

        public final T next() {
            Object res = MapCollections.this.colGetEntry(this.mIndex, this.mOffset);
            this.mIndex++;
            this.mCanRemove = true;
            return res;
        }

        public final void remove() {
            if (this.mCanRemove) {
                this.mIndex--;
                this.mSize--;
                this.mCanRemove = false;
                MapCollections.this.colRemoveAt(this.mIndex);
                return;
            }
            throw new IllegalStateException();
        }
    }

    final class EntrySet implements Set<Entry<K, V>> {
        EntrySet() {
        }

        public final boolean addAll(Collection<? extends Entry<K, V>> collection) {
            int oldSize = MapCollections.this.colGetSize();
            for (Entry<K, V> entry : collection) {
                MapCollections.this.colPut(entry.getKey(), entry.getValue());
            }
            return oldSize != MapCollections.this.colGetSize();
        }

        public final void clear() {
            MapCollections.this.colClear();
        }

        public final boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            int index = MapCollections.this.colIndexOfKey(e.getKey());
            if (index >= 0) {
                return ContainerHelpers.equal(MapCollections.this.colGetEntry(index, 1), e.getValue());
            }
            return false;
        }

        public final boolean containsAll(Collection<?> collection) {
            for (Object contains : collection) {
                if (!contains(contains)) {
                    return false;
                }
            }
            return true;
        }

        public final boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        public final Iterator<Entry<K, V>> iterator() {
            return new MapIterator();
        }

        public final boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        public final boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public final boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public final int size() {
            return MapCollections.this.colGetSize();
        }

        public final Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        public final <T> T[] toArray(T[] tArr) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object object) {
            return MapCollections.equalsSetHelper(this, object);
        }

        public final int hashCode() {
            int result = 0;
            for (int i = MapCollections.this.colGetSize() - 1; i >= 0; i--) {
                int i2;
                Object key = MapCollections.this.colGetEntry(i, 0);
                Object value = MapCollections.this.colGetEntry(i, 1);
                int hashCode = key == null ? 0 : key.hashCode();
                if (value == null) {
                    i2 = 0;
                } else {
                    i2 = value.hashCode();
                }
                result += i2 ^ hashCode;
            }
            return result;
        }

        public final /* bridge */ /* synthetic */ boolean add(Object obj) {
            throw new UnsupportedOperationException();
        }
    }

    final class KeySet implements Set<K> {
        KeySet() {
        }

        public final boolean add(K k) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection<? extends K> collection) {
            throw new UnsupportedOperationException();
        }

        public final void clear() {
            MapCollections.this.colClear();
        }

        public final boolean contains(Object object) {
            return MapCollections.this.colIndexOfKey(object) >= 0;
        }

        public final boolean containsAll(Collection<?> collection) {
            Map colGetMap = MapCollections.this.colGetMap();
            for (Object containsKey : collection) {
                if (!colGetMap.containsKey(containsKey)) {
                    return false;
                }
            }
            return true;
        }

        public final boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        public final Iterator<K> iterator() {
            return new ArrayIterator(0);
        }

        public final boolean remove(Object object) {
            int index = MapCollections.this.colIndexOfKey(object);
            if (index < 0) {
                return false;
            }
            MapCollections.this.colRemoveAt(index);
            return true;
        }

        public final boolean removeAll(Collection<?> collection) {
            Map colGetMap = MapCollections.this.colGetMap();
            int size = colGetMap.size();
            for (Object remove : collection) {
                colGetMap.remove(remove);
            }
            return size != colGetMap.size();
        }

        public final boolean retainAll(Collection<?> collection) {
            return MapCollections.retainAllHelper(MapCollections.this.colGetMap(), collection);
        }

        public final int size() {
            return MapCollections.this.colGetSize();
        }

        public final Object[] toArray() {
            return MapCollections.this.toArrayHelper(0);
        }

        public final <T> T[] toArray(T[] array) {
            return MapCollections.this.toArrayHelper(array, 0);
        }

        public final boolean equals(Object object) {
            return MapCollections.equalsSetHelper(this, object);
        }

        public final int hashCode() {
            int result = 0;
            for (int i = MapCollections.this.colGetSize() - 1; i >= 0; i--) {
                Object obj = MapCollections.this.colGetEntry(i, 0);
                result += obj == null ? 0 : obj.hashCode();
            }
            return result;
        }
    }

    final class MapIterator implements Iterator<Entry<K, V>>, Entry<K, V> {
        int mEnd;
        boolean mEntryValid = false;
        int mIndex;

        MapIterator() {
            this.mEnd = MapCollections.this.colGetSize() - 1;
            this.mIndex = -1;
        }

        public final boolean hasNext() {
            return this.mIndex < this.mEnd;
        }

        public final void remove() {
            if (this.mEntryValid) {
                MapCollections.this.colRemoveAt(this.mIndex);
                this.mIndex--;
                this.mEnd--;
                this.mEntryValid = false;
                return;
            }
            throw new IllegalStateException();
        }

        public final K getKey() {
            if (this.mEntryValid) {
                return MapCollections.this.colGetEntry(this.mIndex, 0);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final V getValue() {
            if (this.mEntryValid) {
                return MapCollections.this.colGetEntry(this.mIndex, 1);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final V setValue(V object) {
            if (this.mEntryValid) {
                return MapCollections.this.colSetValue(this.mIndex, object);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final boolean equals(Object o) {
            if (!this.mEntryValid) {
                throw new IllegalStateException("This container does not support retaining Map.Entry objects");
            } else if (!(o instanceof Entry)) {
                return false;
            } else {
                Entry<?, ?> e = (Entry) o;
                if (ContainerHelpers.equal(e.getKey(), MapCollections.this.colGetEntry(this.mIndex, 0)) && ContainerHelpers.equal(e.getValue(), MapCollections.this.colGetEntry(this.mIndex, 1))) {
                    return true;
                }
                return false;
            }
        }

        public final int hashCode() {
            int i = 0;
            if (this.mEntryValid) {
                Object key = MapCollections.this.colGetEntry(this.mIndex, 0);
                Object value = MapCollections.this.colGetEntry(this.mIndex, 1);
                int hashCode = key == null ? 0 : key.hashCode();
                if (value != null) {
                    i = value.hashCode();
                }
                return i ^ hashCode;
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final String toString() {
            return getKey() + SimpleComparison.EQUAL_TO_OPERATION + getValue();
        }

        public final /* bridge */ /* synthetic */ Object next() {
            this.mIndex++;
            this.mEntryValid = true;
            return this;
        }
    }

    final class ValuesCollection implements Collection<V> {
        ValuesCollection() {
        }

        public final boolean add(V v) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }

        public final void clear() {
            MapCollections.this.colClear();
        }

        public final boolean contains(Object object) {
            return MapCollections.this.colIndexOfValue(object) >= 0;
        }

        public final boolean containsAll(Collection<?> collection) {
            for (Object contains : collection) {
                if (!contains(contains)) {
                    return false;
                }
            }
            return true;
        }

        public final boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        public final Iterator<V> iterator() {
            return new ArrayIterator(1);
        }

        public final boolean remove(Object object) {
            int index = MapCollections.this.colIndexOfValue(object);
            if (index < 0) {
                return false;
            }
            MapCollections.this.colRemoveAt(index);
            return true;
        }

        public final boolean removeAll(Collection<?> collection) {
            int N = MapCollections.this.colGetSize();
            boolean changed = false;
            int i = 0;
            while (i < N) {
                if (collection.contains(MapCollections.this.colGetEntry(i, 1))) {
                    MapCollections.this.colRemoveAt(i);
                    i--;
                    N--;
                    changed = true;
                }
                i++;
            }
            return changed;
        }

        public final boolean retainAll(Collection<?> collection) {
            int N = MapCollections.this.colGetSize();
            boolean changed = false;
            int i = 0;
            while (i < N) {
                if (!collection.contains(MapCollections.this.colGetEntry(i, 1))) {
                    MapCollections.this.colRemoveAt(i);
                    i--;
                    N--;
                    changed = true;
                }
                i++;
            }
            return changed;
        }

        public final int size() {
            return MapCollections.this.colGetSize();
        }

        public final Object[] toArray() {
            return MapCollections.this.toArrayHelper(1);
        }

        public final <T> T[] toArray(T[] array) {
            return MapCollections.this.toArrayHelper(array, 1);
        }
    }

    protected abstract void colClear();

    protected abstract Object colGetEntry(int i, int i2);

    protected abstract Map<K, V> colGetMap();

    protected abstract int colGetSize();

    protected abstract int colIndexOfKey(Object obj);

    protected abstract int colIndexOfValue(Object obj);

    protected abstract void colPut(K k, V v);

    protected abstract void colRemoveAt(int i);

    protected abstract V colSetValue(int i, V v);

    MapCollections() {
    }

    public static <K, V> boolean retainAllHelper(Map<K, V> map, Collection<?> collection) {
        int oldSize = map.size();
        Iterator<K> it = map.keySet().iterator();
        while (it.hasNext()) {
            if (!collection.contains(it.next())) {
                it.remove();
            }
        }
        return oldSize != map.size();
    }

    public final Object[] toArrayHelper(int offset) {
        int N = colGetSize();
        Object[] result = new Object[N];
        for (int i = 0; i < N; i++) {
            result[i] = colGetEntry(i, offset);
        }
        return result;
    }

    public final <T> T[] toArrayHelper(T[] array, int offset) {
        int N = colGetSize();
        if (array.length < N) {
            array = (Object[]) Array.newInstance(array.getClass().getComponentType(), N);
        }
        for (int i = 0; i < N; i++) {
            array[i] = colGetEntry(i, offset);
        }
        if (array.length > N) {
            array[N] = null;
        }
        return array;
    }

    public static <T> boolean equalsSetHelper(Set<T> set, Object object) {
        if (set == object) {
            return true;
        }
        if (!(object instanceof Set)) {
            return false;
        }
        Set<?> s = (Set) object;
        try {
            if (set.size() == s.size() && set.containsAll(s)) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException e2) {
            return false;
        }
    }

    public final Set<K> getKeySet() {
        if (this.mKeySet == null) {
            this.mKeySet = new KeySet();
        }
        return this.mKeySet;
    }
}
