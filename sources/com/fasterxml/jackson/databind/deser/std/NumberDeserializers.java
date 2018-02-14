package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;

public class NumberDeserializers {
    private static final HashSet<String> _classNames = new HashSet();

    @JacksonStdImpl
    public static class BigDecimalDeserializer extends StdScalarDeserializer<BigDecimal> {
        public static final BigDecimalDeserializer instance = new BigDecimalDeserializer();

        public BigDecimalDeserializer() {
            super(BigDecimal.class);
        }

        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        BigDecimal value = deserialize(p, ctxt);
                        if (p.nextToken() != JsonToken.END_ARRAY) {
                            handleMissingEndArrayForSingle(p, ctxt);
                        }
                        return value;
                    }
                    break;
                case 6:
                    String text = p.getText().trim();
                    if (text.length() == 0) {
                        return null;
                    }
                    try {
                        return new BigDecimal(text);
                    } catch (IllegalArgumentException e) {
                        return (BigDecimal) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
                    }
                case 7:
                case 8:
                    return p.getDecimalValue();
            }
            return (BigDecimal) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    @JacksonStdImpl
    public static class BigIntegerDeserializer extends StdScalarDeserializer<BigInteger> {
        public static final BigIntegerDeserializer instance = new BigIntegerDeserializer();

        public BigIntegerDeserializer() {
            super(BigInteger.class);
        }

        public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        BigInteger value = deserialize(p, ctxt);
                        if (p.nextToken() != JsonToken.END_ARRAY) {
                            handleMissingEndArrayForSingle(p, ctxt);
                        }
                        return value;
                    }
                    break;
                case 6:
                    String text = p.getText().trim();
                    if (text.length() == 0) {
                        return null;
                    }
                    try {
                        return new BigInteger(text);
                    } catch (IllegalArgumentException e) {
                        return (BigInteger) ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
                    }
                case 7:
                    switch (p.getNumberType()) {
                        case INT:
                        case LONG:
                        case BIG_INTEGER:
                            return p.getBigIntegerValue();
                        default:
                            break;
                    }
                case 8:
                    if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
                        _failDoubleToIntCoercion(p, ctxt, "java.math.BigInteger");
                    }
                    return p.getDecimalValue().toBigInteger();
            }
            return (BigInteger) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    protected static abstract class PrimitiveOrWrapperDeserializer<T> extends StdScalarDeserializer<T> {
        private static final long serialVersionUID = 1;
        protected final T _nullValue;
        protected final boolean _primitive;

        protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl) {
            super((Class) vc);
            this._nullValue = nvl;
            this._primitive = vc.isPrimitive();
        }

        public final T getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                ctxt.reportMappingException("Can not map JSON null into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", handledType().toString());
            }
            return this._nullValue;
        }

        public T getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
            if (this._primitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                ctxt.reportMappingException("Can not map Empty String as null into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", handledType().toString());
            }
            return this._nullValue;
        }
    }

    @JacksonStdImpl
    public static final class BooleanDeserializer extends PrimitiveOrWrapperDeserializer<Boolean> {
        static final BooleanDeserializer primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
        private static final long serialVersionUID = 1;
        static final BooleanDeserializer wrapperInstance = new BooleanDeserializer(Boolean.class, null);

        public BooleanDeserializer(Class<Boolean> cls, Boolean nvl) {
            super(cls, nvl);
        }

        public final Boolean deserialize(JsonParser j, DeserializationContext ctxt) throws IOException {
            return _parseBoolean(j, ctxt);
        }

        public final Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            return _parseBoolean(p, ctxt);
        }
    }

    @JacksonStdImpl
    public static class ByteDeserializer extends PrimitiveOrWrapperDeserializer<Byte> {
        static final ByteDeserializer primitiveInstance = new ByteDeserializer(Byte.TYPE, Byte.valueOf((byte) 0));
        private static final long serialVersionUID = 1;
        static final ByteDeserializer wrapperInstance = new ByteDeserializer(Byte.class, null);

        public ByteDeserializer(Class<Byte> cls, Byte nvl) {
            super(cls, nvl);
        }

        public Byte deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return _parseByte(p, ctxt);
        }
    }

    @JacksonStdImpl
    public static class CharacterDeserializer extends PrimitiveOrWrapperDeserializer<Character> {
        static final CharacterDeserializer primitiveInstance = new CharacterDeserializer(Character.TYPE, Character.valueOf('\u0000'));
        private static final long serialVersionUID = 1;
        static final CharacterDeserializer wrapperInstance = new CharacterDeserializer(Character.class, null);

        public CharacterDeserializer(Class<Character> cls, Character nvl) {
            super(cls, nvl);
        }

        public Character deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        Character C = deserialize(p, ctxt);
                        if (p.nextToken() != JsonToken.END_ARRAY) {
                            handleMissingEndArrayForSingle(p, ctxt);
                        }
                        return C;
                    }
                    break;
                case 6:
                    String text = p.getText();
                    if (text.length() == 1) {
                        return Character.valueOf(text.charAt(0));
                    }
                    if (text.length() == 0) {
                        return (Character) getEmptyValue(ctxt);
                    }
                    break;
                case 7:
                    int value = p.getIntValue();
                    if (value >= 0 && value <= 65535) {
                        return Character.valueOf((char) value);
                    }
            }
            return (Character) ctxt.handleUnexpectedToken(this._valueClass, p);
        }
    }

    @JacksonStdImpl
    public static class DoubleDeserializer extends PrimitiveOrWrapperDeserializer<Double> {
        static final DoubleDeserializer primitiveInstance = new DoubleDeserializer(Double.TYPE, Double.valueOf(0.0d));
        private static final long serialVersionUID = 1;
        static final DoubleDeserializer wrapperInstance = new DoubleDeserializer(Double.class, null);

        public DoubleDeserializer(Class<Double> cls, Double nvl) {
            super(cls, nvl);
        }

        public Double deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return _parseDouble(jp, ctxt);
        }

        public Double deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            return _parseDouble(jp, ctxt);
        }
    }

    @JacksonStdImpl
    public static class FloatDeserializer extends PrimitiveOrWrapperDeserializer<Float> {
        static final FloatDeserializer primitiveInstance = new FloatDeserializer(Float.TYPE, Float.valueOf(0.0f));
        private static final long serialVersionUID = 1;
        static final FloatDeserializer wrapperInstance = new FloatDeserializer(Float.class, null);

        public FloatDeserializer(Class<Float> cls, Float nvl) {
            super(cls, nvl);
        }

        public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return _parseFloat(p, ctxt);
        }
    }

    @JacksonStdImpl
    public static final class IntegerDeserializer extends PrimitiveOrWrapperDeserializer<Integer> {
        static final IntegerDeserializer primitiveInstance = new IntegerDeserializer(Integer.TYPE, Integer.valueOf(0));
        private static final long serialVersionUID = 1;
        static final IntegerDeserializer wrapperInstance = new IntegerDeserializer(Integer.class, null);

        public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
            super(cls, nvl);
        }

        public final boolean isCachable() {
            return true;
        }

        public final Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Integer.valueOf(p.getIntValue());
            }
            return _parseInteger(p, ctxt);
        }

        public final Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Integer.valueOf(p.getIntValue());
            }
            return _parseInteger(p, ctxt);
        }
    }

    @JacksonStdImpl
    public static final class LongDeserializer extends PrimitiveOrWrapperDeserializer<Long> {
        static final LongDeserializer primitiveInstance = new LongDeserializer(Long.TYPE, Long.valueOf(0));
        private static final long serialVersionUID = 1;
        static final LongDeserializer wrapperInstance = new LongDeserializer(Long.class, null);

        public LongDeserializer(Class<Long> cls, Long nvl) {
            super(cls, nvl);
        }

        public final boolean isCachable() {
            return true;
        }

        public final Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return Long.valueOf(p.getLongValue());
            }
            return _parseLong(p, ctxt);
        }
    }

    @JacksonStdImpl
    public static class NumberDeserializer extends StdScalarDeserializer<Object> {
        public static final NumberDeserializer instance = new NumberDeserializer();

        public NumberDeserializer() {
            super(Number.class);
        }

        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.getCurrentTokenId()) {
                case 3:
                    if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                        p.nextToken();
                        Object value = deserialize(p, ctxt);
                        if (p.nextToken() == JsonToken.END_ARRAY) {
                            return value;
                        }
                        handleMissingEndArrayForSingle(p, ctxt);
                        return value;
                    }
                    break;
                case 6:
                    String text = p.getText().trim();
                    if (text.length() == 0) {
                        return getEmptyValue(ctxt);
                    }
                    if (_hasTextualNull(text)) {
                        return getNullValue(ctxt);
                    }
                    if (_isPosInf(text)) {
                        return Double.valueOf(Double.POSITIVE_INFINITY);
                    }
                    if (_isNegInf(text)) {
                        return Double.valueOf(Double.NEGATIVE_INFINITY);
                    }
                    if (_isNaN(text)) {
                        return Double.valueOf(Double.NaN);
                    }
                    try {
                        if (_isIntNumber(text)) {
                            if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
                                return new BigInteger(text);
                            }
                            long value2 = Long.parseLong(text);
                            if (ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS) || value2 > 2147483647L || value2 < -2147483648L) {
                                return Long.valueOf(value2);
                            }
                            return Integer.valueOf((int) value2);
                        } else if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                            return new BigDecimal(text);
                        } else {
                            return new Double(text);
                        }
                    } catch (IllegalArgumentException e) {
                        return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number", new Object[0]);
                    }
                case 7:
                    if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
                        return _coerceIntegral(p, ctxt);
                    }
                    return p.getNumberValue();
                case 8:
                    if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
                        return p.getDecimalValue();
                    }
                    return Double.valueOf(p.getDoubleValue());
            }
            return ctxt.handleUnexpectedToken(this._valueClass, p);
        }

        public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
            switch (jp.getCurrentTokenId()) {
                case 6:
                case 7:
                case 8:
                    return deserialize(jp, ctxt);
                default:
                    return typeDeserializer.deserializeTypedFromScalar(jp, ctxt);
            }
        }
    }

    @JacksonStdImpl
    public static class ShortDeserializer extends PrimitiveOrWrapperDeserializer<Short> {
        static final ShortDeserializer primitiveInstance = new ShortDeserializer(Short.TYPE, Short.valueOf((short) 0));
        private static final long serialVersionUID = 1;
        static final ShortDeserializer wrapperInstance = new ShortDeserializer(Short.class, null);

        public ShortDeserializer(Class<Short> cls, Short nvl) {
            super(cls, nvl);
        }

        public Short deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return _parseShort(jp, ctxt);
        }
    }

    static {
        Class<?>[] arr$ = new Class[]{Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class};
        for (int i$ = 0; i$ < 11; i$++) {
            _classNames.add(arr$[i$].getName());
        }
    }

    public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
        if (rawType.isPrimitive()) {
            if (rawType == Integer.TYPE) {
                return IntegerDeserializer.primitiveInstance;
            }
            if (rawType == Boolean.TYPE) {
                return BooleanDeserializer.primitiveInstance;
            }
            if (rawType == Long.TYPE) {
                return LongDeserializer.primitiveInstance;
            }
            if (rawType == Double.TYPE) {
                return DoubleDeserializer.primitiveInstance;
            }
            if (rawType == Character.TYPE) {
                return CharacterDeserializer.primitiveInstance;
            }
            if (rawType == Byte.TYPE) {
                return ByteDeserializer.primitiveInstance;
            }
            if (rawType == Short.TYPE) {
                return ShortDeserializer.primitiveInstance;
            }
            if (rawType == Float.TYPE) {
                return FloatDeserializer.primitiveInstance;
            }
        } else if (!_classNames.contains(clsName)) {
            return null;
        } else {
            if (rawType == Integer.class) {
                return IntegerDeserializer.wrapperInstance;
            }
            if (rawType == Boolean.class) {
                return BooleanDeserializer.wrapperInstance;
            }
            if (rawType == Long.class) {
                return LongDeserializer.wrapperInstance;
            }
            if (rawType == Double.class) {
                return DoubleDeserializer.wrapperInstance;
            }
            if (rawType == Character.class) {
                return CharacterDeserializer.wrapperInstance;
            }
            if (rawType == Byte.class) {
                return ByteDeserializer.wrapperInstance;
            }
            if (rawType == Short.class) {
                return ShortDeserializer.wrapperInstance;
            }
            if (rawType == Float.class) {
                return FloatDeserializer.wrapperInstance;
            }
            if (rawType == Number.class) {
                return NumberDeserializer.instance;
            }
            if (rawType == BigDecimal.class) {
                return BigDecimalDeserializer.instance;
            }
            if (rawType == BigInteger.class) {
                return BigIntegerDeserializer.instance;
            }
        }
        throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
    }
}
