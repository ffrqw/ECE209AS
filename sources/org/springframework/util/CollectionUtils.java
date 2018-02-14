package org.springframework.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class CollectionUtils {

    private static class MultiValueMapAdapter<K, V> implements Serializable, MultiValueMap<K, V> {
        private final Map<K, List<V>> map;

        public final /* bridge */ /* synthetic */ Object put(Object x0, Object x1) {
            return (List) this.map.put(x0, (List) x1);
        }

        public MultiValueMapAdapter(Map<K, List<V>> map) {
            Assert.notNull(map, "'map' must not be null");
            this.map = map;
        }

        public final void add(K key, V value) {
            List<V> values = (List) this.map.get(key);
            if (values == null) {
                values = new LinkedList();
                this.map.put(key, values);
            }
            values.add(value);
        }

        public final int size() {
            return this.map.size();
        }

        public final boolean isEmpty() {
            return this.map.isEmpty();
        }

        public final boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        public final boolean containsValue(Object value) {
            return this.map.containsValue(value);
        }

        public final void putAll(Map<? extends K, ? extends List<V>> m) {
            this.map.putAll(m);
        }

        public final void clear() {
            this.map.clear();
        }

        public final Set<K> keySet() {
            return this.map.keySet();
        }

        public final Collection<List<V>> values() {
            return this.map.values();
        }

        public final Set<Entry<K, List<V>>> entrySet() {
            return this.map.entrySet();
        }

        public final boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return this.map.equals(other);
        }

        public final int hashCode() {
            return this.map.hashCode();
        }

        public final String toString() {
            return this.map.toString();
        }

        public final /* bridge */ /* synthetic */ Object remove(Object x0) {
            return (List) this.map.remove(x0);
        }

        public final /* bridge */ /* synthetic */ Object get(Object x0) {
            return (List) this.map.get(x0);
        }
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(MultiValueMap<? extends K, ? extends V> map) {
        Assert.notNull(map, "'map' must not be null");
        Map<K, List<V>> result = new LinkedHashMap(map.size());
        for (Entry<? extends K, ? extends List<? extends V>> entry : map.entrySet()) {
            result.put(entry.getKey(), Collections.unmodifiableList((List) entry.getValue()));
        }
        return new MultiValueMapAdapter(Collections.unmodifiableMap(result));
    }
}
