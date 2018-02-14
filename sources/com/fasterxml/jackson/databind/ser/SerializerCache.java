package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import com.fasterxml.jackson.databind.util.TypeKey;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class SerializerCache {
    private final AtomicReference<ReadOnlyClassToSerializerMap> _readOnlyMap = new AtomicReference();
    private final HashMap<TypeKey, JsonSerializer<Object>> _sharedMap = new HashMap(64);

    public final ReadOnlyClassToSerializerMap getReadOnlyLookupMap() {
        ReadOnlyClassToSerializerMap m = (ReadOnlyClassToSerializerMap) this._readOnlyMap.get();
        return m != null ? m : _makeReadOnlyLookupMap();
    }

    private final synchronized ReadOnlyClassToSerializerMap _makeReadOnlyLookupMap() {
        ReadOnlyClassToSerializerMap m;
        m = (ReadOnlyClassToSerializerMap) this._readOnlyMap.get();
        if (m == null) {
            m = ReadOnlyClassToSerializerMap.from(this._sharedMap);
            this._readOnlyMap.set(m);
        }
        return m;
    }

    public final synchronized int size() {
        return this._sharedMap.size();
    }

    public final JsonSerializer<Object> untypedValueSerializer(Class<?> type) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = (JsonSerializer) this._sharedMap.get(new TypeKey((Class) type, false));
        }
        return jsonSerializer;
    }

    public final JsonSerializer<Object> untypedValueSerializer(JavaType type) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = (JsonSerializer) this._sharedMap.get(new TypeKey(type, false));
        }
        return jsonSerializer;
    }

    public final JsonSerializer<Object> typedValueSerializer(JavaType type) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = (JsonSerializer) this._sharedMap.get(new TypeKey(type, true));
        }
        return jsonSerializer;
    }

    public final JsonSerializer<Object> typedValueSerializer(Class<?> cls) {
        JsonSerializer<Object> jsonSerializer;
        synchronized (this) {
            jsonSerializer = (JsonSerializer) this._sharedMap.get(new TypeKey((Class) cls, true));
        }
        return jsonSerializer;
    }

    public final void addTypedSerializer(JavaType type, JsonSerializer<Object> ser) {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, true), ser) == null) {
                this._readOnlyMap.set(null);
            }
        }
    }

    public final void addTypedSerializer(Class<?> cls, JsonSerializer<Object> ser) {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey((Class) cls, true), ser) == null) {
                this._readOnlyMap.set(null);
            }
        }
    }

    public final void addAndResolveNonTypedSerializer(Class<?> type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey((Class) type, false), ser) == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer) ser).resolve(provider);
            }
        }
    }

    public final void addAndResolveNonTypedSerializer(JavaType type, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            if (this._sharedMap.put(new TypeKey(type, false), ser) == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer) ser).resolve(provider);
            }
        }
    }

    public final void addAndResolveNonTypedSerializer(Class<?> rawType, JavaType fullType, JsonSerializer<Object> ser, SerializerProvider provider) throws JsonMappingException {
        synchronized (this) {
            Object ob1 = this._sharedMap.put(new TypeKey((Class) rawType, false), ser);
            Object ob2 = this._sharedMap.put(new TypeKey(fullType, false), ser);
            if (ob1 == null || ob2 == null) {
                this._readOnlyMap.set(null);
            }
            if (ser instanceof ResolvableSerializer) {
                ((ResolvableSerializer) ser).resolve(provider);
            }
        }
    }

    public final synchronized void flush() {
        this._sharedMap.clear();
    }
}
