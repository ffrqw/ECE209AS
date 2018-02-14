package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;

public abstract class ReferenceTypeSerializer<T> extends StdSerializer<T> implements ContextualSerializer {
    private static final long serialVersionUID = 1;
    protected final Include _contentInclusion;
    protected transient PropertySerializerMap _dynamicSerializers;
    protected final BeanProperty _property;
    protected final JavaType _referredType;
    protected final NameTransformer _unwrapper;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;

    protected abstract Object _getReferenced(T t);

    protected abstract Object _getReferencedIfPresent(T t);

    protected abstract boolean _isValueEmpty(T t);

    protected abstract ReferenceTypeSerializer<T> withResolved(BeanProperty beanProperty, TypeSerializer typeSerializer, JsonSerializer<?> jsonSerializer, NameTransformer nameTransformer, Include include);

    public ReferenceTypeSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser) {
        super((JavaType) fullType);
        this._referredType = fullType.getReferencedType();
        this._property = null;
        this._valueTypeSerializer = vts;
        this._valueSerializer = ser;
        this._unwrapper = null;
        this._contentInclusion = null;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }

    protected ReferenceTypeSerializer(ReferenceTypeSerializer<?> base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Include contentIncl) {
        super((StdSerializer) base);
        this._referredType = base._referredType;
        this._dynamicSerializers = base._dynamicSerializers;
        this._property = property;
        this._valueTypeSerializer = vts;
        this._valueSerializer = valueSer;
        this._unwrapper = unwrapper;
        if (contentIncl == Include.USE_DEFAULTS || contentIncl == Include.ALWAYS) {
            this._contentInclusion = null;
        } else {
            this._contentInclusion = contentIncl;
        }
    }

    public JsonSerializer<T> unwrappingSerializer(NameTransformer transformer) {
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser != null) {
            ser = ser.unwrappingSerializer(transformer);
        }
        return withResolved(this._property, this._valueTypeSerializer, ser, this._unwrapper == null ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper), this._contentInclusion);
    }

    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        TypeSerializer typeSer = this._valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }
        JsonSerializer<?> ser = findAnnotatedContentSerializer(provider, property);
        if (ser == null) {
            ser = this._valueSerializer;
            if (ser != null) {
                ser = provider.handlePrimaryContextualization(ser, property);
            } else if (_useStatic(provider, property, this._referredType)) {
                ser = _findSerializer(provider, this._referredType, property);
            }
        }
        Include contentIncl = this._contentInclusion;
        Include newIncl = findIncludeOverrides(provider, property, handledType()).getContentInclusion();
        if (!(newIncl == contentIncl || newIncl == Include.USE_DEFAULTS)) {
            contentIncl = newIncl;
        }
        return withResolved(property, typeSer, ser, this._unwrapper, contentIncl);
    }

    protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType) {
        if (referredType.isJavaLangObject()) {
            return false;
        }
        if (referredType.isFinal()) {
            return true;
        }
        if (referredType.useStaticType()) {
            return true;
        }
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (!(intr == null || property == null || property.getMember() == null)) {
            Typing t = intr.findSerializationTyping(property.getMember());
            if (t == Typing.STATIC) {
                return true;
            }
            if (t == Typing.DYNAMIC) {
                return false;
            }
        }
        return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
    }

    public boolean isEmpty(SerializerProvider provider, T value) {
        if (value == null || _isValueEmpty(value)) {
            return true;
        }
        if (this._contentInclusion == null) {
            return false;
        }
        Object contents = _getReferenced(value);
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            try {
                ser = _findCachedSerializer(provider, contents.getClass());
            } catch (JsonMappingException e) {
                throw new RuntimeJsonMappingException(e);
            }
        }
        return ser.isEmpty(provider, contents);
    }

    public boolean isUnwrappingSerializer() {
        return this._unwrapper != null;
    }

    public void serialize(T ref, JsonGenerator g, SerializerProvider provider) throws IOException {
        Object value = _getReferencedIfPresent(ref);
        if (value != null) {
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                ser = _findCachedSerializer(provider, value.getClass());
            }
            if (this._valueTypeSerializer != null) {
                ser.serializeWithType(value, g, provider, this._valueTypeSerializer);
            } else {
                ser.serialize(value, g, provider);
            }
        } else if (this._unwrapper == null) {
            provider.defaultSerializeNull(g);
        }
    }

    public void serializeWithType(T ref, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        Object value = _getReferencedIfPresent(ref);
        if (value != null) {
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                ser = _findCachedSerializer(provider, value.getClass());
            }
            ser.serializeWithType(value, g, provider, typeSer);
        } else if (this._unwrapper == null) {
            provider.defaultSerializeNull(g);
        }
    }

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonSerializer<?> ser = this._valueSerializer;
        if (ser == null) {
            ser = _findSerializer(visitor.getProvider(), this._referredType, this._property);
            if (this._unwrapper != null) {
                ser = ser.unwrappingSerializer(this._unwrapper);
            }
        }
        ser.acceptJsonFormatVisitor(visitor, this._referredType);
    }

    private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider, Class<?> type) throws JsonMappingException {
        JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type);
        if (ser == null) {
            ser = _findSerializer(provider, (Class) type, this._property);
            if (this._unwrapper != null) {
                ser = ser.unwrappingSerializer(this._unwrapper);
            }
            this._dynamicSerializers = this._dynamicSerializers.newWith(type, ser);
        }
        return ser;
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Class<?> type, BeanProperty prop) throws JsonMappingException {
        return provider.findTypedValueSerializer((Class) type, true, prop);
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop) throws JsonMappingException {
        return provider.findTypedValueSerializer(type, true, prop);
    }
}
