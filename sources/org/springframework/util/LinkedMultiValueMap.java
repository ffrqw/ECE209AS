package org.springframework.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class LinkedMultiValueMap<K, V> implements Serializable, MultiValueMap<K, V> {
    private final Map<K, List<V>> targetMap;

    public final /* bridge */ /* synthetic */ Object put(Object x0, Object x1) {
        return (List) this.targetMap.put(x0, (List) x1);
    }

    public LinkedMultiValueMap() {
        this.targetMap = new LinkedHashMap();
    }

    public LinkedMultiValueMap(int initialCapacity) {
        this.targetMap = new LinkedHashMap(initialCapacity);
    }

    public final void add(K key, V value) {
        List<V> values = (List) this.targetMap.get(key);
        if (values == null) {
            values = new LinkedList();
            this.targetMap.put(key, values);
        }
        values.add(value);
    }

    public final int size() {
        return this.targetMap.size();
    }

    public final boolean isEmpty() {
        return this.targetMap.isEmpty();
    }

    public final boolean containsKey(Object key) {
        return this.targetMap.containsKey(key);
    }

    public final boolean containsValue(Object value) {
        return this.targetMap.containsValue(value);
    }

    public final void putAll(Map<? extends K, ? extends List<V>> m) {
        this.targetMap.putAll(m);
    }

    public final void clear() {
        this.targetMap.clear();
    }

    public final Set<K> keySet() {
        return this.targetMap.keySet();
    }

    public final Collection<List<V>> values() {
        return this.targetMap.values();
    }

    public final Set<Entry<K, List<V>>> entrySet() {
        return this.targetMap.entrySet();
    }

    public final boolean equals(Object obj) {
        return this.targetMap.equals(obj);
    }

    public final int hashCode() {
        return this.targetMap.hashCode();
    }

    public final String toString() {
        return this.targetMap.toString();
    }

    public final /* bridge */ /* synthetic */ Object remove(Object x0) {
        return (List) this.targetMap.remove(x0);
    }

    public final /* bridge */ /* synthetic */ Object get(Object x0) {
        return (List) this.targetMap.get(x0);
    }
}
