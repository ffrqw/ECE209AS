package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public class AbstractDeserializer extends JsonDeserializer<Object> implements Serializable {
    private static final long serialVersionUID = 1;
    protected final boolean _acceptBoolean;
    protected final boolean _acceptDouble;
    protected final boolean _acceptInt;
    protected final boolean _acceptString;
    protected final Map<String, SettableBeanProperty> _backRefProperties;
    protected final JavaType _baseType;
    protected final ObjectIdReader _objectIdReader;

    public AbstractDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, Map<String, SettableBeanProperty> backRefProps) {
        boolean z;
        boolean z2 = false;
        this._baseType = beanDesc.getType();
        this._objectIdReader = builder.getObjectIdReader();
        this._backRefProperties = backRefProps;
        Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        if (cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class)) {
            z = true;
        } else {
            z = false;
        }
        this._acceptBoolean = z;
        if (cls == Integer.TYPE || cls.isAssignableFrom(Integer.class)) {
            z = true;
        } else {
            z = false;
        }
        this._acceptInt = z;
        if (cls == Double.TYPE || cls.isAssignableFrom(Double.class)) {
            z2 = true;
        }
        this._acceptDouble = z2;
    }

    protected AbstractDeserializer(BeanDescription beanDesc) {
        boolean z;
        boolean z2 = false;
        this._baseType = beanDesc.getType();
        this._objectIdReader = null;
        this._backRefProperties = null;
        Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        if (cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class)) {
            z = true;
        } else {
            z = false;
        }
        this._acceptBoolean = z;
        if (cls == Integer.TYPE || cls.isAssignableFrom(Integer.class)) {
            z = true;
        } else {
            z = false;
        }
        this._acceptInt = z;
        if (cls == Double.TYPE || cls.isAssignableFrom(Double.class)) {
            z2 = true;
        }
        this._acceptDouble = z2;
    }

    public static AbstractDeserializer constructForNonPOJO(BeanDescription beanDesc) {
        return new AbstractDeserializer(beanDesc);
    }

    public Class<?> handledType() {
        return this._baseType.getRawClass();
    }

    public boolean isCachable() {
        return true;
    }

    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }

    public SettableBeanProperty findBackReference(String logicalName) {
        return this._backRefProperties == null ? null : (SettableBeanProperty) this._backRefProperties.get(logicalName);
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        if (this._objectIdReader != null) {
            JsonToken t = p.getCurrentToken();
            if (t != null) {
                if (t.isScalarValue()) {
                    return _deserializeFromObjectId(p, ctxt);
                }
                if (t == JsonToken.START_OBJECT) {
                    t = p.nextToken();
                }
                if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
                    return _deserializeFromObjectId(p, ctxt);
                }
            }
        }
        Object result = _deserializeIfNatural(p, ctxt);
        return result == null ? typeDeserializer.deserializeTypedFromObject(p, ctxt) : result;
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ctxt.handleMissingInstantiator(this._baseType.getRawClass(), p, "abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information", new Object[0]);
    }

    protected Object _deserializeIfNatural(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.getCurrentTokenId()) {
            case 6:
                if (this._acceptString) {
                    return p.getText();
                }
                break;
            case 7:
                if (this._acceptInt) {
                    return Integer.valueOf(p.getIntValue());
                }
                break;
            case 8:
                if (this._acceptDouble) {
                    return Double.valueOf(p.getDoubleValue());
                }
                break;
            case 9:
                if (this._acceptBoolean) {
                    return Boolean.TRUE;
                }
                break;
            case 10:
                if (this._acceptBoolean) {
                    return Boolean.FALSE;
                }
                break;
        }
        return null;
    }

    protected Object _deserializeFromObjectId(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object id = this._objectIdReader.readObjectReference(p, ctxt);
        ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        Object pojo = roid.resolve();
        if (pojo != null) {
            return pojo;
        }
        throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] -- unresolved forward-reference?", p.getCurrentLocation(), roid);
    }
}
