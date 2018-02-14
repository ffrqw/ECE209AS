package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.CompactStringObjectMap;
import com.fasterxml.jackson.databind.util.EnumResolver;
import java.io.IOException;

@JacksonStdImpl
public class EnumDeserializer extends StdScalarDeserializer<Object> {
    private static final long serialVersionUID = 1;
    private final Enum<?> _enumDefaultValue;
    protected Object[] _enumsByIndex;
    protected final CompactStringObjectMap _lookupByName;
    protected CompactStringObjectMap _lookupByToString;

    public EnumDeserializer(EnumResolver byNameResolver) {
        super(byNameResolver.getEnumClass());
        this._lookupByName = byNameResolver.constructLookup();
        this._enumsByIndex = byNameResolver.getRawEnums();
        this._enumDefaultValue = byNameResolver.getDefaultValue();
    }

    @Deprecated
    public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
        return deserializerForCreator(config, enumClass, factory, null, null);
    }

    public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps) {
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return new FactoryBasedEnumDeserializer(enumClass, factory, factory.getParameterType(0), valueInstantiator, creatorProps);
    }

    public static JsonDeserializer<?> deserializerForNoArgsCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return new FactoryBasedEnumDeserializer((Class) enumClass, factory);
    }

    public boolean isCachable() {
        return true;
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken curr = p.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
            CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? _getToStringLookup(ctxt) : this._lookupByName;
            String name = p.getText();
            Object result = lookup.find(name);
            if (result == null) {
                return _deserializeAltString(p, ctxt, lookup, name);
            }
            return result;
        } else if (curr != JsonToken.VALUE_NUMBER_INT) {
            return _deserializeOther(p, ctxt);
        } else {
            int index = p.getIntValue();
            if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                return ctxt.handleWeirdNumberValue(_enumClass(), Integer.valueOf(index), "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow", new Object[0]);
            }
            if (index >= 0 && index < this._enumsByIndex.length) {
                return this._enumsByIndex[index];
            }
            if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
                return this._enumDefaultValue;
            }
            if (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                return null;
            }
            return ctxt.handleWeirdNumberValue(_enumClass(), Integer.valueOf(index), "index value outside legal index range [0..%s]", Integer.valueOf(this._enumsByIndex.length - 1));
        }
    }

    private final Object _deserializeAltString(JsonParser p, DeserializationContext ctxt, CompactStringObjectMap lookup, String name) throws IOException {
        name = name.trim();
        if (name.length() != 0) {
            char c = name.charAt(0);
            if (c >= '0' && c <= '9') {
                try {
                    int index = Integer.parseInt(name);
                    if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                        return ctxt.handleWeirdNumberValue(_enumClass(), Integer.valueOf(index), "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow", new Object[0]);
                    }
                    if (index >= 0 && index < this._enumsByIndex.length) {
                        return this._enumsByIndex[index];
                    }
                } catch (NumberFormatException e) {
                }
            }
        } else if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            return null;
        }
        if (this._enumDefaultValue != null && ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)) {
            return this._enumDefaultValue;
        }
        if (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
            return null;
        }
        return ctxt.handleWeirdStringValue(_enumClass(), name, "value not one of declared Enum instance names: %s", lookup.keys());
    }

    protected Object _deserializeOther(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS) || !p.isExpectedStartArrayToken()) {
            return ctxt.handleUnexpectedToken(_enumClass(), p);
        }
        p.nextToken();
        Object parsed = deserialize(p, ctxt);
        if (p.nextToken() == JsonToken.END_ARRAY) {
            return parsed;
        }
        handleMissingEndArrayForSingle(p, ctxt);
        return parsed;
    }

    protected Class<?> _enumClass() {
        return handledType();
    }

    protected CompactStringObjectMap _getToStringLookup(DeserializationContext ctxt) {
        CompactStringObjectMap lookup = this._lookupByToString;
        if (lookup == null) {
            synchronized (this) {
                lookup = EnumResolver.constructUnsafeUsingToString(_enumClass(), ctxt.getAnnotationIntrospector()).constructLookup();
            }
            this._lookupByToString = lookup;
        }
        return lookup;
    }
}
