package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;
import com.fasterxml.jackson.databind.ser.impl.IndexedStringListSerializer;
import com.fasterxml.jackson.databind.ser.impl.IteratorSerializer;
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.ByteBufferSerializer;
import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
import com.fasterxml.jackson.databind.ser.std.EnumSetSerializer;
import com.fasterxml.jackson.databind.ser.std.InetAddressSerializer;
import com.fasterxml.jackson.databind.ser.std.InetSocketAddressSerializer;
import com.fasterxml.jackson.databind.ser.std.IterableSerializer;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.ser.std.StdJdkSerializers;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.ser.std.TimeZoneSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.TokenBufferSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.RandomAccess;
import java.util.TimeZone;

public abstract class BasicSerializerFactory extends SerializerFactory implements Serializable {
    protected static final HashMap<String, JsonSerializer<?>> _concrete;
    protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy;
    protected final SerializerFactoryConfig _factoryConfig;

    public abstract JsonSerializer<Object> createSerializer(SerializerProvider serializerProvider, JavaType javaType) throws JsonMappingException;

    protected abstract Iterable<Serializers> customSerializers();

    public abstract SerializerFactory withConfig(SerializerFactoryConfig serializerFactoryConfig);

    static {
        HashMap<String, Class<? extends JsonSerializer<?>>> concLazy = new HashMap();
        HashMap<String, JsonSerializer<?>> concrete = new HashMap();
        concrete.put(String.class.getName(), new StringSerializer());
        ToStringSerializer sls = ToStringSerializer.instance;
        concrete.put(StringBuffer.class.getName(), sls);
        concrete.put(StringBuilder.class.getName(), sls);
        concrete.put(Character.class.getName(), sls);
        concrete.put(Character.TYPE.getName(), sls);
        NumberSerializers.addAll(concrete);
        concrete.put(Boolean.TYPE.getName(), new BooleanSerializer(true));
        concrete.put(Boolean.class.getName(), new BooleanSerializer(false));
        concrete.put(BigInteger.class.getName(), new NumberSerializer(BigInteger.class));
        concrete.put(BigDecimal.class.getName(), new NumberSerializer(BigDecimal.class));
        concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
        concrete.put(Date.class.getName(), DateSerializer.instance);
        for (Entry<Class<?>, Object> en : StdJdkSerializers.all()) {
            Class<? extends JsonSerializer<?>> value = en.getValue();
            if (value instanceof JsonSerializer) {
                concrete.put(((Class) en.getKey()).getName(), (JsonSerializer) value);
            } else if (value instanceof Class) {
                concLazy.put(((Class) en.getKey()).getName(), value);
            } else {
                throw new IllegalStateException("Internal error: unrecognized value of type " + en.getClass().getName());
            }
        }
        concLazy.put(TokenBuffer.class.getName(), TokenBufferSerializer.class);
        _concrete = concrete;
        _concreteLazy = concLazy;
    }

    protected BasicSerializerFactory(SerializerFactoryConfig config) {
        if (config == null) {
            config = new SerializerFactoryConfig();
        }
        this._factoryConfig = config;
    }

    public SerializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }

    public final SerializerFactory withAdditionalSerializers(Serializers additional) {
        return withConfig(this._factoryConfig.withAdditionalSerializers(additional));
    }

    public final SerializerFactory withAdditionalKeySerializers(Serializers additional) {
        return withConfig(this._factoryConfig.withAdditionalKeySerializers(additional));
    }

    public final SerializerFactory withSerializerModifier(BeanSerializerModifier modifier) {
        return withConfig(this._factoryConfig.withSerializerModifier(modifier));
    }

    public JsonSerializer<Object> createKeySerializer(SerializationConfig config, JavaType keyType, JsonSerializer<Object> defaultImpl) {
        BeanDescription beanDesc = config.introspectClassAnnotations(keyType.getRawClass());
        JsonSerializer<?> ser = null;
        if (this._factoryConfig.hasKeySerializers()) {
            for (Serializers serializers : this._factoryConfig.keySerializers()) {
                ser = serializers.findSerializer(config, keyType, beanDesc);
                if (ser != null) {
                    break;
                }
            }
        }
        if (ser == null) {
            ser = defaultImpl;
            if (defaultImpl == null) {
                ser = StdKeySerializers.getStdKeySerializer(config, keyType.getRawClass(), false);
                if (ser == null) {
                    beanDesc = config.introspect(keyType);
                    AnnotatedMethod am = beanDesc.findJsonValueMethod();
                    if (am != null) {
                        JsonSerializer<?> delegate = StdKeySerializers.getStdKeySerializer(config, am.getRawReturnType(), true);
                        Method m = am.getAnnotated();
                        if (config.canOverrideAccessModifiers()) {
                            ClassUtil.checkAndFixAccess(m, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }
                        ser = new JsonValueSerializer(am, delegate);
                    } else {
                        ser = StdKeySerializers.getFallbackKeySerializer(config, keyType.getRawClass());
                    }
                }
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyKeySerializer(config, keyType, beanDesc, ser);
            }
        }
        return ser;
    }

    public TypeSerializer createTypeSerializer(SerializationConfig config, JavaType baseType) {
        AnnotatedClass ac = config.introspectClassAnnotations(baseType.getRawClass()).getClassInfo();
        TypeResolverBuilder<?> b = config.getAnnotationIntrospector().findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
        } else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, ac);
        }
        if (b == null) {
            return null;
        }
        return b.buildTypeSerializer(config, baseType, subtypes);
    }

    protected final JsonSerializer<?> findSerializerByLookup(JavaType type, SerializationConfig config, BeanDescription beanDesc, boolean staticTyping) {
        String clsName = type.getRawClass().getName();
        JsonSerializer<?> ser = (JsonSerializer) _concrete.get(clsName);
        if (ser == null) {
            Class<? extends JsonSerializer<?>> serClass = (Class) _concreteLazy.get(clsName);
            if (serClass != null) {
                try {
                    return (JsonSerializer) serClass.newInstance();
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to instantiate standard serializer (of type " + serClass.getName() + "): " + e.getMessage(), e);
                }
            }
        }
        return ser;
    }

    protected final JsonSerializer<?> findSerializerByAnnotations(SerializerProvider prov, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        if (JsonSerializable.class.isAssignableFrom(type.getRawClass())) {
            return SerializableSerializer.instance;
        }
        AnnotatedMethod valueMethod = beanDesc.findJsonValueMethod();
        if (valueMethod == null) {
            return null;
        }
        Method m = valueMethod.getAnnotated();
        if (prov.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(m, prov.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return new JsonValueSerializer(valueMethod, findSerializerFromAnnotation(prov, valueMethod));
    }

    protected final JsonSerializer<?> findSerializerByPrimaryType(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        Class<?> raw = type.getRawClass();
        JsonSerializer<?> ser = findOptionalStdSerializer(prov, type, beanDesc, staticTyping);
        if (ser != null) {
            return ser;
        }
        if (Calendar.class.isAssignableFrom(raw)) {
            return CalendarSerializer.instance;
        }
        if (Date.class.isAssignableFrom(raw)) {
            return DateSerializer.instance;
        }
        if (Entry.class.isAssignableFrom(raw)) {
            JavaType mapEntryType = type.findSuperType(Entry.class);
            return buildMapEntrySerializer(prov.getConfig(), type, beanDesc, staticTyping, mapEntryType.containedTypeOrUnknown(0), mapEntryType.containedTypeOrUnknown(1));
        } else if (ByteBuffer.class.isAssignableFrom(raw)) {
            return new ByteBufferSerializer();
        } else {
            if (InetAddress.class.isAssignableFrom(raw)) {
                return new InetAddressSerializer();
            }
            if (InetSocketAddress.class.isAssignableFrom(raw)) {
                return new InetSocketAddressSerializer();
            }
            if (TimeZone.class.isAssignableFrom(raw)) {
                return new TimeZoneSerializer();
            }
            if (Charset.class.isAssignableFrom(raw)) {
                return ToStringSerializer.instance;
            }
            if (!Number.class.isAssignableFrom(raw)) {
                return Enum.class.isAssignableFrom(raw) ? buildEnumSerializer(prov.getConfig(), type, beanDesc) : null;
            } else {
                Value format = beanDesc.findExpectedFormat(null);
                if (format != null) {
                    switch (format.getShape()) {
                        case STRING:
                            return ToStringSerializer.instance;
                        case OBJECT:
                        case ARRAY:
                            return null;
                    }
                }
                return NumberSerializer.instance;
            }
        }
    }

    protected JsonSerializer<?> findOptionalStdSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findSerializer(prov.getConfig(), type, beanDesc);
    }

    protected final JsonSerializer<?> findSerializerByAddonType(SerializationConfig config, JavaType javaType, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        Class<?> rawType = javaType.getRawClass();
        JavaType[] params;
        JavaType vt;
        if (Iterator.class.isAssignableFrom(rawType)) {
            params = config.getTypeFactory().findTypeParameters(javaType, Iterator.class);
            vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
            return buildIteratorSerializer(config, javaType, beanDesc, staticTyping, vt);
        } else if (Iterable.class.isAssignableFrom(rawType)) {
            params = config.getTypeFactory().findTypeParameters(javaType, Iterable.class);
            vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
            return buildIterableSerializer(config, javaType, beanDesc, staticTyping, vt);
        } else if (CharSequence.class.isAssignableFrom(rawType)) {
            return ToStringSerializer.instance;
        } else {
            return null;
        }
    }

    protected JsonSerializer<Object> findSerializerFromAnnotation(SerializerProvider prov, Annotated a) throws JsonMappingException {
        Object serDef = prov.getAnnotationIntrospector().findSerializer(a);
        if (serDef == null) {
            return null;
        }
        return findConvertingSerializer(prov, a, prov.serializerInstance(a, serDef));
    }

    protected JsonSerializer<?> findConvertingSerializer(SerializerProvider prov, Annotated a, JsonSerializer<?> ser) throws JsonMappingException {
        Converter<Object, Object> conv = findConverter(prov, a);
        return conv == null ? ser : new StdDelegatingSerializer(conv, conv.getOutputType(prov.getTypeFactory()), ser);
    }

    protected Converter<Object, Object> findConverter(SerializerProvider prov, Annotated a) throws JsonMappingException {
        Object convDef = prov.getAnnotationIntrospector().findSerializationConverter(a);
        if (convDef == null) {
            return null;
        }
        return prov.converterInstance(a, convDef);
    }

    protected JsonSerializer<?> buildContainerSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        SerializationConfig config = prov.getConfig();
        if (!(staticTyping || !type.useStaticType() || (type.isContainerType() && type.getContentType().getRawClass() == Object.class))) {
            staticTyping = true;
        }
        TypeSerializer elementTypeSerializer = createTypeSerializer(config, type.getContentType());
        if (elementTypeSerializer != null) {
            staticTyping = false;
        }
        JsonSerializer<Object> elementValueSerializer = _findContentSerializer(prov, beanDesc.getClassInfo());
        JsonSerializer<?> ser;
        if (type.isMapLikeType()) {
            MapLikeType mlt = (MapLikeType) type;
            JsonSerializer<Object> keySerializer = _findKeySerializer(prov, beanDesc.getClassInfo());
            if (mlt.isTrueMapType()) {
                return buildMapSerializer(prov, (MapType) mlt, beanDesc, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
            }
            ser = null;
            MapLikeType mlType = (MapLikeType) type;
            for (Serializers serializers : customSerializers()) {
                ser = serializers.findMapLikeSerializer(config, mlType, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
                if (ser != null) {
                    break;
                }
            }
            if (ser == null) {
                ser = findSerializerByAnnotations(prov, type, beanDesc);
            }
            if (ser == null || !this._factoryConfig.hasSerializerModifiers()) {
                return ser;
            }
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyMapLikeSerializer(config, mlType, beanDesc, ser);
            }
            return ser;
        } else if (type.isCollectionLikeType()) {
            CollectionLikeType clt = (CollectionLikeType) type;
            if (clt.isTrueCollectionType()) {
                return buildCollectionSerializer(prov, (CollectionType) clt, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
            }
            ser = null;
            CollectionLikeType clType = (CollectionLikeType) type;
            for (Serializers serializers2 : customSerializers()) {
                ser = serializers2.findCollectionLikeSerializer(config, clType, beanDesc, elementTypeSerializer, elementValueSerializer);
                if (ser != null) {
                    break;
                }
            }
            if (ser == null) {
                ser = findSerializerByAnnotations(prov, type, beanDesc);
            }
            if (ser == null || !this._factoryConfig.hasSerializerModifiers()) {
                return ser;
            }
            for (BeanSerializerModifier mod2 : this._factoryConfig.serializerModifiers()) {
                ser = mod2.modifyCollectionLikeSerializer(config, clType, beanDesc, ser);
            }
            return ser;
        } else if (!type.isArrayType()) {
            return null;
        } else {
            return buildArraySerializer(prov, (ArrayType) type, beanDesc, staticTyping, elementTypeSerializer, elementValueSerializer);
        }
    }

    protected JsonSerializer<?> buildCollectionSerializer(SerializerProvider prov, CollectionType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        SerializationConfig config = prov.getConfig();
        JsonSerializer<?> ser = null;
        for (Serializers serializers : customSerializers()) {
            ser = serializers.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            ser = findSerializerByAnnotations(prov, type, beanDesc);
            if (ser == null) {
                Value format = beanDesc.findExpectedFormat(null);
                if (format != null && format.getShape() == Shape.OBJECT) {
                    return null;
                }
                Class<?> raw = type.getRawClass();
                if (EnumSet.class.isAssignableFrom(raw)) {
                    JavaType enumType = type.getContentType();
                    if (!enumType.isEnumType()) {
                        enumType = null;
                    }
                    ser = buildEnumSetSerializer(enumType);
                } else {
                    Class<?> elementRaw = type.getContentType().getRawClass();
                    if (isIndexedList(raw)) {
                        if (elementRaw != String.class) {
                            ser = buildIndexedListSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
                        } else if (elementValueSerializer == null || ClassUtil.isJacksonStdImpl((Object) elementValueSerializer)) {
                            ser = IndexedStringListSerializer.instance;
                        }
                    } else if (elementRaw == String.class && (elementValueSerializer == null || ClassUtil.isJacksonStdImpl((Object) elementValueSerializer))) {
                        ser = StringCollectionSerializer.instance;
                    }
                    if (ser == null) {
                        ser = buildCollectionSerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
                    }
                }
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyCollectionSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }

    protected boolean isIndexedList(Class<?> cls) {
        return RandomAccess.class.isAssignableFrom(cls);
    }

    public ContainerSerializer<?> buildIndexedListSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
        return new IndexedListSerializer(elemType, staticTyping, vts, valueSerializer);
    }

    public ContainerSerializer<?> buildCollectionSerializer(JavaType elemType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
        return new CollectionSerializer(elemType, staticTyping, vts, valueSerializer);
    }

    public JsonSerializer<?> buildEnumSetSerializer(JavaType enumType) {
        return new EnumSetSerializer(enumType);
    }

    protected JsonSerializer<?> buildMapSerializer(SerializerProvider prov, MapType type, BeanDescription beanDesc, boolean staticTyping, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        SerializationConfig config = prov.getConfig();
        JsonSerializer<?> ser = null;
        for (Serializers serializers : customSerializers()) {
            ser = serializers.findMapSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            ser = findSerializerByAnnotations(prov, type, beanDesc);
            if (ser == null) {
                Object filterId = findFilterId(config, beanDesc);
                JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
                JsonSerializer<?> mapSer = MapSerializer.construct(ignorals == null ? null : ignorals.findIgnoredForSerialization(), (JavaType) type, staticTyping, elementTypeSerializer, (JsonSerializer) keySerializer, (JsonSerializer) elementValueSerializer, filterId);
                Object suppressableValue = findSuppressableContentValue(config, type.getContentType(), beanDesc);
                if (suppressableValue != null) {
                    mapSer = mapSer.withContentInclusion(suppressableValue);
                }
                ser = mapSer;
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyMapSerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }

    protected Object findSuppressableContentValue(SerializationConfig config, JavaType contentType, BeanDescription beanDesc) throws JsonMappingException {
        JsonInclude.Value inclV = beanDesc.findPropertyInclusion(config.getDefaultPropertyInclusion());
        if (inclV == null) {
            return null;
        }
        Object incl = inclV.getContentInclusion();
        switch (incl) {
            case USE_DEFAULTS:
                return null;
            default:
                return incl;
        }
    }

    protected JsonSerializer<?> buildArraySerializer(SerializerProvider prov, ArrayType type, BeanDescription beanDesc, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) throws JsonMappingException {
        SerializationConfig config = prov.getConfig();
        JsonSerializer<?> ser = null;
        for (Serializers serializers : customSerializers()) {
            ser = serializers.findArraySerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
            if (ser != null) {
                break;
            }
        }
        if (ser == null) {
            Class<?> raw = type.getRawClass();
            if (elementValueSerializer == null || ClassUtil.isJacksonStdImpl((Object) elementValueSerializer)) {
                if (String[].class == raw) {
                    ser = StringArraySerializer.instance;
                } else {
                    ser = StdArraySerializers.findStandardImpl(raw);
                }
            }
            if (ser == null) {
                ser = new ObjectArraySerializer(type.getContentType(), staticTyping, elementTypeSerializer, elementValueSerializer);
            }
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifyArraySerializer(config, type, beanDesc, ser);
            }
        }
        return ser;
    }

    protected JsonSerializer<?> buildIteratorSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType) throws JsonMappingException {
        return new IteratorSerializer(valueType, staticTyping, createTypeSerializer(config, valueType));
    }

    protected JsonSerializer<?> buildIterableSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType valueType) throws JsonMappingException {
        return new IterableSerializer(valueType, staticTyping, createTypeSerializer(config, valueType));
    }

    protected JsonSerializer<?> buildMapEntrySerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, boolean staticTyping, JavaType keyType, JavaType valueType) throws JsonMappingException {
        return new MapEntrySerializer(valueType, keyType, valueType, staticTyping, createTypeSerializer(config, valueType), null);
    }

    protected JsonSerializer<?> buildEnumSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        JsonSerializer<?> jsonSerializer = null;
        Value format = beanDesc.findExpectedFormat(null);
        if (format == null || format.getShape() != Shape.OBJECT) {
            jsonSerializer = EnumSerializer.construct(type.getRawClass(), config, beanDesc, format);
            if (this._factoryConfig.hasSerializerModifiers()) {
                for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                    jsonSerializer = mod.modifyEnumSerializer(config, type, beanDesc, jsonSerializer);
                }
            }
        } else {
            ((BasicBeanDescription) beanDesc).removeProperty("declaringClass");
        }
        return jsonSerializer;
    }

    protected JsonSerializer<Object> _findKeySerializer(SerializerProvider prov, Annotated a) throws JsonMappingException {
        Object serDef = prov.getAnnotationIntrospector().findKeySerializer(a);
        if (serDef != null) {
            return prov.serializerInstance(a, serDef);
        }
        return null;
    }

    protected JsonSerializer<Object> _findContentSerializer(SerializerProvider prov, Annotated a) throws JsonMappingException {
        Object serDef = prov.getAnnotationIntrospector().findContentSerializer(a);
        if (serDef != null) {
            return prov.serializerInstance(a, serDef);
        }
        return null;
    }

    protected Object findFilterId(SerializationConfig config, BeanDescription beanDesc) {
        return config.getAnnotationIntrospector().findFilterId(beanDesc.getClassInfo());
    }

    protected boolean usesStaticTyping(SerializationConfig config, BeanDescription beanDesc, TypeSerializer typeSer) {
        if (typeSer != null) {
            return false;
        }
        Typing t = config.getAnnotationIntrospector().findSerializationTyping(beanDesc.getClassInfo());
        if (t == null || t == Typing.DEFAULT_TYPING) {
            return config.isEnabled(MapperFeature.USE_STATIC_TYPING);
        }
        if (t == Typing.STATIC) {
            return true;
        }
        return false;
    }

    protected Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass) {
        if (src == null) {
            return null;
        }
        if (src instanceof Class) {
            Class<?> cls = (Class) src;
            if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
                return null;
            }
            return cls;
        }
        throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
    }
}
