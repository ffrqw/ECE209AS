package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class TypeNameIdResolver extends TypeIdResolverBase {
    protected final MapperConfig<?> _config;
    protected final Map<String, JavaType> _idToType;
    protected final Map<String, String> _typeToId;

    protected TypeNameIdResolver(MapperConfig<?> config, JavaType baseType, Map<String, String> typeToId, Map<String, JavaType> idToType) {
        super(baseType, config.getTypeFactory());
        this._config = config;
        this._typeToId = typeToId;
        this._idToType = idToType;
    }

    public static TypeNameIdResolver construct(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
        if (forSer == forDeser) {
            throw new IllegalArgumentException();
        }
        Map<String, String> map = null;
        Map<String, JavaType> idToType = null;
        if (forSer) {
            map = new HashMap();
        }
        if (forDeser) {
            idToType = new HashMap();
            map = new TreeMap();
        }
        if (subtypes != null) {
            for (NamedType t : subtypes) {
                Class cls = t.getType();
                String id = t.hasName() ? t.getName() : _defaultTypeId(cls);
                if (forSer) {
                    map.put(cls.getName(), id);
                }
                if (forDeser) {
                    JavaType prev = (JavaType) idToType.get(id);
                    if (prev == null || !cls.isAssignableFrom(prev.getRawClass())) {
                        idToType.put(id, config.constructType(cls));
                    }
                }
            }
        }
        return new TypeNameIdResolver(config, baseType, map, idToType);
    }

    public Id getMechanism() {
        return Id.NAME;
    }

    public String idFromValue(Object value) {
        return idFromClass(value.getClass());
    }

    protected String idFromClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        String name;
        Class cls = this._typeFactory.constructType((Type) clazz).getRawClass();
        String key = cls.getName();
        synchronized (this._typeToId) {
            name = (String) this._typeToId.get(key);
            if (name == null) {
                if (this._config.isAnnotationProcessingEnabled()) {
                    name = this._config.getAnnotationIntrospector().findTypeName(this._config.introspectClassAnnotations(cls).getClassInfo());
                }
                if (name == null) {
                    name = _defaultTypeId(cls);
                }
                this._typeToId.put(key, name);
            }
        }
        return name;
    }

    public String idFromValueAndType(Object value, Class<?> type) {
        if (value == null) {
            return idFromClass(type);
        }
        return idFromValue(value);
    }

    public JavaType typeFromId(DatabindContext context, String id) {
        return _typeFromId(id);
    }

    protected JavaType _typeFromId(String id) {
        return (JavaType) this._idToType.get(id);
    }

    public String getDescForKnownTypeIds() {
        return new TreeSet(this._idToType.keySet()).toString();
    }

    public String toString() {
        return String.format("[%s; id-to-type=%s]", new Object[]{getClass().getName(), this._idToType});
    }

    protected static String _defaultTypeId(Class<?> cls) {
        String n = cls.getName();
        int ix = n.lastIndexOf(46);
        return ix < 0 ? n : n.substring(ix + 1);
    }
}
