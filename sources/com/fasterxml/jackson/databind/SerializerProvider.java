package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.impl.FailingSerializer;
import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class SerializerProvider extends DatabindContext {
    protected static final boolean CACHE_UNKNOWN_MAPPINGS = false;
    public static final JsonSerializer<Object> DEFAULT_NULL_KEY_SERIALIZER = new FailingSerializer("Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)");
    protected static final JsonSerializer<Object> DEFAULT_UNKNOWN_SERIALIZER = new UnknownSerializer();
    protected transient ContextAttributes _attributes;
    protected final SerializationConfig _config;
    protected DateFormat _dateFormat;
    protected JsonSerializer<Object> _keySerializer;
    protected final ReadOnlyClassToSerializerMap _knownSerializers;
    protected JsonSerializer<Object> _nullKeySerializer;
    protected JsonSerializer<Object> _nullValueSerializer;
    protected final Class<?> _serializationView;
    protected final SerializerCache _serializerCache;
    protected final SerializerFactory _serializerFactory;
    protected final boolean _stdNullValueSerializer;
    protected JsonSerializer<Object> _unknownTypeSerializer;

    public abstract WritableObjectId findObjectId(Object obj, ObjectIdGenerator<?> objectIdGenerator);

    public abstract JsonSerializer<Object> serializerInstance(Annotated annotated, Object obj) throws JsonMappingException;

    public SerializerProvider() {
        this._unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
        this._config = null;
        this._serializerFactory = null;
        this._serializerCache = new SerializerCache();
        this._knownSerializers = null;
        this._serializationView = null;
        this._attributes = null;
        this._stdNullValueSerializer = true;
    }

    protected SerializerProvider(SerializerProvider src, SerializationConfig config, SerializerFactory f) {
        this._unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
        if (config == null) {
            throw new NullPointerException();
        }
        this._serializerFactory = f;
        this._config = config;
        this._serializerCache = src._serializerCache;
        this._unknownTypeSerializer = src._unknownTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._nullValueSerializer = src._nullValueSerializer;
        this._nullKeySerializer = src._nullKeySerializer;
        this._stdNullValueSerializer = this._nullValueSerializer == DEFAULT_NULL_KEY_SERIALIZER;
        this._serializationView = config.getActiveView();
        this._attributes = config.getAttributes();
        this._knownSerializers = this._serializerCache.getReadOnlyLookupMap();
    }

    protected SerializerProvider(SerializerProvider src) {
        this._unknownTypeSerializer = DEFAULT_UNKNOWN_SERIALIZER;
        this._nullValueSerializer = NullSerializer.instance;
        this._nullKeySerializer = DEFAULT_NULL_KEY_SERIALIZER;
        this._config = null;
        this._serializationView = null;
        this._serializerFactory = null;
        this._knownSerializers = null;
        this._serializerCache = new SerializerCache();
        this._unknownTypeSerializer = src._unknownTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._nullValueSerializer = src._nullValueSerializer;
        this._nullKeySerializer = src._nullKeySerializer;
        this._stdNullValueSerializer = src._stdNullValueSerializer;
    }

    public void setDefaultKeySerializer(JsonSerializer<Object> ks) {
        if (ks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._keySerializer = ks;
    }

    public void setNullValueSerializer(JsonSerializer<Object> nvs) {
        if (nvs == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._nullValueSerializer = nvs;
    }

    public void setNullKeySerializer(JsonSerializer<Object> nks) {
        if (nks == null) {
            throw new IllegalArgumentException("Can not pass null JsonSerializer");
        }
        this._nullKeySerializer = nks;
    }

    public final SerializationConfig getConfig() {
        return this._config;
    }

    public final AnnotationIntrospector getAnnotationIntrospector() {
        return this._config.getAnnotationIntrospector();
    }

    public final TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }

    public final Class<?> getActiveView() {
        return this._serializationView;
    }

    @Deprecated
    public final Class<?> getSerializationView() {
        return this._serializationView;
    }

    public final boolean canOverrideAccessModifiers() {
        return this._config.canOverrideAccessModifiers();
    }

    public final boolean isEnabled(MapperFeature feature) {
        return this._config.isEnabled(feature);
    }

    public final Value getDefaultPropertyFormat(Class<?> baseType) {
        return this._config.getDefaultPropertyFormat(baseType);
    }

    public final JsonInclude.Value getDefaultPropertyInclusion(Class<?> cls) {
        return this._config.getDefaultPropertyInclusion();
    }

    public Locale getLocale() {
        return this._config.getLocale();
    }

    public TimeZone getTimeZone() {
        return this._config.getTimeZone();
    }

    public Object getAttribute(Object key) {
        return this._attributes.getAttribute(key);
    }

    public SerializerProvider setAttribute(Object key, Object value) {
        this._attributes = this._attributes.withPerCallAttribute(key, value);
        return this;
    }

    public final boolean isEnabled(SerializationFeature feature) {
        return this._config.isEnabled(feature);
    }

    public final boolean hasSerializationFeatures(int featureMask) {
        return this._config.hasSerializationFeatures(featureMask);
    }

    public final FilterProvider getFilterProvider() {
        return this._config.getFilterProvider();
    }

    public JsonSerializer<Object> findValueSerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer((Class) valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer((Class) valueType);
            if (ser == null) {
                ser = this._serializerCache.untypedValueSerializer(this._config.constructType((Class) valueType));
                if (ser == null) {
                    ser = _createAndCacheUntypedSerializer((Class) valueType);
                    if (ser == null) {
                        return getUnknownTypeSerializer(valueType);
                    }
                }
            }
        }
        return handleSecondaryContextualization(ser, property);
    }

    public JsonSerializer<Object> findValueSerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = _createAndCacheUntypedSerializer(valueType);
                if (ser == null) {
                    return getUnknownTypeSerializer(valueType.getRawClass());
                }
            }
        }
        return handleSecondaryContextualization(ser, property);
    }

    public JsonSerializer<Object> findValueSerializer(Class<?> valueType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer((Class) valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.untypedValueSerializer((Class) valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.untypedValueSerializer(this._config.constructType((Class) valueType));
        if (ser != null) {
            return ser;
        }
        ser = _createAndCacheUntypedSerializer((Class) valueType);
        if (ser == null) {
            return getUnknownTypeSerializer(valueType);
        }
        return ser;
    }

    public JsonSerializer<Object> findValueSerializer(JavaType valueType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.untypedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = _createAndCacheUntypedSerializer(valueType);
        if (ser == null) {
            return getUnknownTypeSerializer(valueType.getRawClass());
        }
        return ser;
    }

    public JsonSerializer<Object> findPrimaryPropertySerializer(JavaType valueType, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer(valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer(valueType);
            if (ser == null) {
                ser = _createAndCacheUntypedSerializer(valueType);
                if (ser == null) {
                    return getUnknownTypeSerializer(valueType.getRawClass());
                }
            }
        }
        return handlePrimaryContextualization(ser, property);
    }

    public JsonSerializer<Object> findPrimaryPropertySerializer(Class<?> valueType, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer((Class) valueType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer((Class) valueType);
            if (ser == null) {
                ser = this._serializerCache.untypedValueSerializer(this._config.constructType((Class) valueType));
                if (ser == null) {
                    ser = _createAndCacheUntypedSerializer((Class) valueType);
                    if (ser == null) {
                        return getUnknownTypeSerializer(valueType);
                    }
                }
            }
        }
        return handlePrimaryContextualization(ser, property);
    }

    public JsonSerializer<Object> findTypedValueSerializer(Class<?> valueType, boolean cache, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer((Class) valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer((Class) valueType);
        if (ser != null) {
            return ser;
        }
        ser = findValueSerializer((Class) valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, this._config.constructType((Class) valueType));
        if (typeSer != null) {
            ser = new TypeWrappedSerializer(typeSer.forProperty(property), ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer((Class) valueType, (JsonSerializer) ser);
        }
        return ser;
    }

    public JsonSerializer<Object> findTypedValueSerializer(JavaType valueType, boolean cache, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = this._serializerCache.typedValueSerializer(valueType);
        if (ser != null) {
            return ser;
        }
        ser = findValueSerializer(valueType, property);
        TypeSerializer typeSer = this._serializerFactory.createTypeSerializer(this._config, valueType);
        if (typeSer != null) {
            ser = new TypeWrappedSerializer(typeSer.forProperty(property), ser);
        }
        if (cache) {
            this._serializerCache.addTypedSerializer(valueType, (JsonSerializer) ser);
        }
        return ser;
    }

    public TypeSerializer findTypeSerializer(JavaType javaType) throws JsonMappingException {
        return this._serializerFactory.createTypeSerializer(this._config, javaType);
    }

    public JsonSerializer<Object> findKeySerializer(JavaType keyType, BeanProperty property) throws JsonMappingException {
        return _handleContextualResolvable(this._serializerFactory.createKeySerializer(this._config, keyType, this._keySerializer), property);
    }

    public JsonSerializer<Object> findKeySerializer(Class<?> rawKeyType, BeanProperty property) throws JsonMappingException {
        return findKeySerializer(this._config.constructType((Class) rawKeyType), property);
    }

    public JsonSerializer<Object> getDefaultNullKeySerializer() {
        return this._nullKeySerializer;
    }

    public JsonSerializer<Object> getDefaultNullValueSerializer() {
        return this._nullValueSerializer;
    }

    public JsonSerializer<Object> findNullKeySerializer(JavaType serializationType, BeanProperty property) throws JsonMappingException {
        return this._nullKeySerializer;
    }

    public JsonSerializer<Object> findNullValueSerializer(BeanProperty property) throws JsonMappingException {
        return this._nullValueSerializer;
    }

    public JsonSerializer<Object> getUnknownTypeSerializer(Class<?> unknownType) {
        if (unknownType == Object.class) {
            return this._unknownTypeSerializer;
        }
        return new UnknownSerializer(unknownType);
    }

    public boolean isUnknownTypeSerializer(JsonSerializer<?> ser) {
        if (ser == this._unknownTypeSerializer || ser == null) {
            return true;
        }
        if (isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS) && ser.getClass() == UnknownSerializer.class) {
            return true;
        }
        return false;
    }

    public JsonSerializer<?> handlePrimaryContextualization(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
        if (ser == null || !(ser instanceof ContextualSerializer)) {
            return ser;
        }
        return ((ContextualSerializer) ser).createContextual(this, property);
    }

    public JsonSerializer<?> handleSecondaryContextualization(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
        if (ser == null || !(ser instanceof ContextualSerializer)) {
            return ser;
        }
        return ((ContextualSerializer) ser).createContextual(this, property);
    }

    public final void defaultSerializeValue(Object value, JsonGenerator gen) throws IOException {
        if (value != null) {
            findTypedValueSerializer(value.getClass(), true, null).serialize(value, gen, this);
        } else if (this._stdNullValueSerializer) {
            gen.writeNull();
        } else {
            this._nullValueSerializer.serialize(null, gen, this);
        }
    }

    public final void defaultSerializeField(String fieldName, Object value, JsonGenerator gen) throws IOException {
        gen.writeFieldName(fieldName);
        if (value != null) {
            findTypedValueSerializer(value.getClass(), true, null).serialize(value, gen, this);
        } else if (this._stdNullValueSerializer) {
            gen.writeNull();
        } else {
            this._nullValueSerializer.serialize(null, gen, this);
        }
    }

    public final void defaultSerializeDateValue(long timestamp, JsonGenerator gen) throws IOException {
        if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            gen.writeNumber(timestamp);
        } else {
            gen.writeString(_dateFormat().format(new Date(timestamp)));
        }
    }

    public final void defaultSerializeDateValue(Date date, JsonGenerator gen) throws IOException {
        if (isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)) {
            gen.writeNumber(date.getTime());
        } else {
            gen.writeString(_dateFormat().format(date));
        }
    }

    public void defaultSerializeDateKey(long timestamp, JsonGenerator gen) throws IOException {
        if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            gen.writeFieldName(String.valueOf(timestamp));
        } else {
            gen.writeFieldName(_dateFormat().format(new Date(timestamp)));
        }
    }

    public void defaultSerializeDateKey(Date date, JsonGenerator gen) throws IOException {
        if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
            gen.writeFieldName(String.valueOf(date.getTime()));
        } else {
            gen.writeFieldName(_dateFormat().format(date));
        }
    }

    public final void defaultSerializeNull(JsonGenerator gen) throws IOException {
        if (this._stdNullValueSerializer) {
            gen.writeNull();
        } else {
            this._nullValueSerializer.serialize(null, gen, this);
        }
    }

    public JsonMappingException mappingException(String message, Object... args) {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        return JsonMappingException.from(getGenerator(), message);
    }

    protected JsonMappingException mappingException(Throwable t, String message, Object... args) {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        return JsonMappingException.from(getGenerator(), message, t);
    }

    public void reportMappingProblem(String message, Object... args) throws JsonMappingException {
        throw mappingException(message, args);
    }

    public void reportMappingProblem(Throwable t, String message, Object... args) throws JsonMappingException {
        throw mappingException(t, message, args);
    }

    public <T> T reportBadTypeDefinition(BeanDescription bean, String message, Object... args) throws JsonMappingException {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        String beanDesc = bean == null ? "N/A" : _desc(bean.getType().getGenericSignature());
        throw mappingException("Invalid type definition for type %s: %s", beanDesc, message);
    }

    public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String message, Object... args) throws JsonMappingException {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        String propName = prop == null ? "N/A" : _quotedString(prop.getName());
        String beanDesc = bean == null ? "N/A" : _desc(bean.getType().getGenericSignature());
        throw mappingException("Invalid definition for property %s (of type %s): %s", propName, beanDesc, message);
    }

    public JsonGenerator getGenerator() {
        return null;
    }

    protected void _reportIncompatibleRootType(Object value, JavaType rootType) throws IOException {
        if (!rootType.isPrimitive() || !ClassUtil.wrapperType(rootType.getRawClass()).isAssignableFrom(value.getClass())) {
            reportMappingProblem("Incompatible types: declared root type (%s) vs %s", rootType, value.getClass().getName());
        }
    }

    protected JsonSerializer<Object> _findExplicitUntypedSerializer(Class<?> runtimeType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._knownSerializers.untypedValueSerializer((Class) runtimeType);
        if (ser == null) {
            ser = this._serializerCache.untypedValueSerializer((Class) runtimeType);
            if (ser == null) {
                ser = _createAndCacheUntypedSerializer((Class) runtimeType);
            }
        }
        if (isUnknownTypeSerializer(ser)) {
            return null;
        }
        return ser;
    }

    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(Class<?> rawType) throws JsonMappingException {
        JavaType fullType = this._config.constructType((Class) rawType);
        try {
            JsonSerializer<Object> ser = _createUntypedSerializer(fullType);
            if (ser == null) {
                return ser;
            }
            this._serializerCache.addAndResolveNonTypedSerializer(rawType, fullType, ser, this);
            return ser;
        } catch (IllegalArgumentException iae) {
            reportMappingProblem(iae, iae.getMessage(), new Object[0]);
            return null;
        }
    }

    protected JsonSerializer<Object> _createAndCacheUntypedSerializer(JavaType type) throws JsonMappingException {
        try {
            JsonSerializer ser = _createUntypedSerializer(type);
            if (ser == null) {
                return ser;
            }
            this._serializerCache.addAndResolveNonTypedSerializer(type, ser, this);
            return ser;
        } catch (IllegalArgumentException iae) {
            reportMappingProblem(iae, iae.getMessage(), new Object[0]);
            return null;
        }
    }

    protected JsonSerializer<Object> _createUntypedSerializer(JavaType type) throws JsonMappingException {
        JsonSerializer<Object> createSerializer;
        synchronized (this._serializerCache) {
            createSerializer = this._serializerFactory.createSerializer(this, type);
        }
        return createSerializer;
    }

    protected JsonSerializer<Object> _handleContextualResolvable(JsonSerializer<?> ser, BeanProperty property) throws JsonMappingException {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer) ser).resolve(this);
        }
        return handleSecondaryContextualization(ser, property);
    }

    protected JsonSerializer<Object> _handleResolvable(JsonSerializer<?> ser) throws JsonMappingException {
        if (ser instanceof ResolvableSerializer) {
            ((ResolvableSerializer) ser).resolve(this);
        }
        return ser;
    }

    protected String _desc(Object value) {
        if (value == null) {
            return "N/A";
        }
        return "'" + value + "'";
    }

    protected String _quotedString(Object value) {
        if (value == null) {
            return "N/A";
        }
        return String.valueOf(value);
    }

    protected final DateFormat _dateFormat() {
        if (this._dateFormat != null) {
            return this._dateFormat;
        }
        DateFormat df = (DateFormat) this._config.getDateFormat().clone();
        this._dateFormat = df;
        return df;
    }
}
