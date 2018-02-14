package org.springframework.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public final class LinkedCaseInsensitiveMap<V> extends LinkedHashMap<String, V> {
    private final Map<String, String> caseInsensitiveKeys;
    private final Locale locale;

    public LinkedCaseInsensitiveMap() {
        this(null);
    }

    private LinkedCaseInsensitiveMap(Locale locale) {
        this.caseInsensitiveKeys = new HashMap();
        this.locale = Locale.getDefault();
    }

    public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
        super(initialCapacity);
        this.caseInsensitiveKeys = new HashMap(initialCapacity);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.locale = locale;
    }

    private V put(String key, V value) {
        String oldKey = (String) this.caseInsensitiveKeys.put(convertKey(key), key);
        if (!(oldKey == null || oldKey.equals(key))) {
            super.remove(oldKey);
        }
        return super.put(key, value);
    }

    public final void putAll(Map<? extends String, ? extends V> map) {
        if (!map.isEmpty()) {
            for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
                put((String) entry.getKey(), entry.getValue());
            }
        }
    }

    public final boolean containsKey(Object key) {
        return (key instanceof String) && this.caseInsensitiveKeys.containsKey(convertKey((String) key));
    }

    public final V get(Object key) {
        if (key instanceof String) {
            return super.get(this.caseInsensitiveKeys.get(convertKey((String) key)));
        }
        return null;
    }

    public final V remove(Object key) {
        if (key instanceof String) {
            return super.remove(this.caseInsensitiveKeys.remove(convertKey((String) key)));
        }
        return null;
    }

    public final void clear() {
        this.caseInsensitiveKeys.clear();
        super.clear();
    }

    private String convertKey(String key) {
        return key.toLowerCase(this.locale);
    }
}
