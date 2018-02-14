package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

@JacksonStdImpl
public class StdValueInstantiator extends ValueInstantiator implements Serializable {
    private static final long serialVersionUID = 1;
    protected SettableBeanProperty[] _arrayDelegateArguments;
    protected AnnotatedWithParams _arrayDelegateCreator;
    protected JavaType _arrayDelegateType;
    protected SettableBeanProperty[] _constructorArguments;
    protected AnnotatedWithParams _defaultCreator;
    protected SettableBeanProperty[] _delegateArguments;
    protected AnnotatedWithParams _delegateCreator;
    protected JavaType _delegateType;
    protected AnnotatedWithParams _fromBooleanCreator;
    protected AnnotatedWithParams _fromDoubleCreator;
    protected AnnotatedWithParams _fromIntCreator;
    protected AnnotatedWithParams _fromLongCreator;
    protected AnnotatedWithParams _fromStringCreator;
    protected AnnotatedParameter _incompleteParameter;
    protected final Class<?> _valueClass;
    protected final String _valueTypeDesc;
    protected AnnotatedWithParams _withArgsCreator;

    @Deprecated
    public StdValueInstantiator(DeserializationConfig config, Class<?> valueType) {
        this._valueTypeDesc = valueType == null ? "UNKNOWN TYPE" : valueType.getName();
        if (valueType == null) {
            valueType = Object.class;
        }
        this._valueClass = valueType;
    }

    public StdValueInstantiator(DeserializationConfig config, JavaType valueType) {
        this._valueTypeDesc = valueType == null ? "UNKNOWN TYPE" : valueType.toString();
        this._valueClass = valueType == null ? Object.class : valueType.getRawClass();
    }

    protected StdValueInstantiator(StdValueInstantiator src) {
        this._valueTypeDesc = src._valueTypeDesc;
        this._valueClass = src._valueClass;
        this._defaultCreator = src._defaultCreator;
        this._constructorArguments = src._constructorArguments;
        this._withArgsCreator = src._withArgsCreator;
        this._delegateType = src._delegateType;
        this._delegateCreator = src._delegateCreator;
        this._delegateArguments = src._delegateArguments;
        this._arrayDelegateType = src._arrayDelegateType;
        this._arrayDelegateCreator = src._arrayDelegateCreator;
        this._arrayDelegateArguments = src._arrayDelegateArguments;
        this._fromStringCreator = src._fromStringCreator;
        this._fromIntCreator = src._fromIntCreator;
        this._fromLongCreator = src._fromLongCreator;
        this._fromDoubleCreator = src._fromDoubleCreator;
        this._fromBooleanCreator = src._fromBooleanCreator;
    }

    public void configureFromObjectSettings(AnnotatedWithParams defaultCreator, AnnotatedWithParams delegateCreator, JavaType delegateType, SettableBeanProperty[] delegateArgs, AnnotatedWithParams withArgsCreator, SettableBeanProperty[] constructorArgs) {
        this._defaultCreator = defaultCreator;
        this._delegateCreator = delegateCreator;
        this._delegateType = delegateType;
        this._delegateArguments = delegateArgs;
        this._withArgsCreator = withArgsCreator;
        this._constructorArguments = constructorArgs;
    }

    public void configureFromArraySettings(AnnotatedWithParams arrayDelegateCreator, JavaType arrayDelegateType, SettableBeanProperty[] arrayDelegateArgs) {
        this._arrayDelegateCreator = arrayDelegateCreator;
        this._arrayDelegateType = arrayDelegateType;
        this._arrayDelegateArguments = arrayDelegateArgs;
    }

    public void configureFromStringCreator(AnnotatedWithParams creator) {
        this._fromStringCreator = creator;
    }

    public void configureFromIntCreator(AnnotatedWithParams creator) {
        this._fromIntCreator = creator;
    }

    public void configureFromLongCreator(AnnotatedWithParams creator) {
        this._fromLongCreator = creator;
    }

    public void configureFromDoubleCreator(AnnotatedWithParams creator) {
        this._fromDoubleCreator = creator;
    }

    public void configureFromBooleanCreator(AnnotatedWithParams creator) {
        this._fromBooleanCreator = creator;
    }

    public void configureIncompleteParameter(AnnotatedParameter parameter) {
        this._incompleteParameter = parameter;
    }

    public String getValueTypeDesc() {
        return this._valueTypeDesc;
    }

    public Class<?> getValueClass() {
        return this._valueClass;
    }

    public boolean canCreateFromString() {
        return this._fromStringCreator != null;
    }

    public boolean canCreateFromInt() {
        return this._fromIntCreator != null;
    }

    public boolean canCreateFromLong() {
        return this._fromLongCreator != null;
    }

    public boolean canCreateFromDouble() {
        return this._fromDoubleCreator != null;
    }

    public boolean canCreateFromBoolean() {
        return this._fromBooleanCreator != null;
    }

    public boolean canCreateUsingDefault() {
        return this._defaultCreator != null;
    }

    public boolean canCreateUsingDelegate() {
        return this._delegateType != null;
    }

    public boolean canCreateUsingArrayDelegate() {
        return this._arrayDelegateType != null;
    }

    public boolean canCreateFromObjectWith() {
        return this._withArgsCreator != null;
    }

    public JavaType getDelegateType(DeserializationConfig config) {
        return this._delegateType;
    }

    public JavaType getArrayDelegateType(DeserializationConfig config) {
        return this._arrayDelegateType;
    }

    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
        return this._constructorArguments;
    }

    public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
        if (this._defaultCreator == null) {
            return super.createUsingDefault(ctxt);
        }
        try {
            return this._defaultCreator.call();
        } catch (Throwable t) {
            return ctxt.handleInstantiationProblem(this._defaultCreator.getDeclaringClass(), null, rewrapCtorProblem(ctxt, t));
        }
    }

    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
        if (this._withArgsCreator == null) {
            return super.createFromObjectWith(ctxt, args);
        }
        try {
            return this._withArgsCreator.call(args);
        } catch (Throwable t) {
            return ctxt.handleInstantiationProblem(this._withArgsCreator.getDeclaringClass(), args, rewrapCtorProblem(ctxt, t));
        }
    }

    public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        return _createUsingDelegate(this._delegateCreator, this._delegateArguments, ctxt, delegate);
    }

    public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws IOException {
        if (this._arrayDelegateCreator == null) {
            return createUsingDelegate(ctxt, delegate);
        }
        return _createUsingDelegate(this._arrayDelegateCreator, this._arrayDelegateArguments, ctxt, delegate);
    }

    public Object createFromString(DeserializationContext ctxt, String value) throws IOException {
        if (this._fromStringCreator == null) {
            return _createFromStringFallbacks(ctxt, value);
        }
        try {
            return this._fromStringCreator.call1(value);
        } catch (Throwable t) {
            return ctxt.handleInstantiationProblem(this._fromStringCreator.getDeclaringClass(), value, rewrapCtorProblem(ctxt, t));
        }
    }

    public Object createFromInt(DeserializationContext ctxt, int value) throws IOException {
        if (this._fromIntCreator != null) {
            Integer arg = Integer.valueOf(value);
            try {
                return this._fromIntCreator.call1(arg);
            } catch (Throwable t0) {
                return ctxt.handleInstantiationProblem(this._fromIntCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
            }
        } else if (this._fromLongCreator == null) {
            return super.createFromInt(ctxt, value);
        } else {
            Long arg2 = Long.valueOf((long) value);
            try {
                return this._fromLongCreator.call1(arg2);
            } catch (Throwable t02) {
                return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg2, rewrapCtorProblem(ctxt, t02));
            }
        }
    }

    public Object createFromLong(DeserializationContext ctxt, long value) throws IOException {
        if (this._fromLongCreator == null) {
            return super.createFromLong(ctxt, value);
        }
        Long arg = Long.valueOf(value);
        try {
            return this._fromLongCreator.call1(arg);
        } catch (Throwable t0) {
            return ctxt.handleInstantiationProblem(this._fromLongCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
        }
    }

    public Object createFromDouble(DeserializationContext ctxt, double value) throws IOException {
        if (this._fromDoubleCreator == null) {
            return super.createFromDouble(ctxt, value);
        }
        Double arg = Double.valueOf(value);
        try {
            return this._fromDoubleCreator.call1(arg);
        } catch (Throwable t0) {
            return ctxt.handleInstantiationProblem(this._fromDoubleCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
        }
    }

    public Object createFromBoolean(DeserializationContext ctxt, boolean value) throws IOException {
        if (this._fromBooleanCreator == null) {
            return super.createFromBoolean(ctxt, value);
        }
        Boolean arg = Boolean.valueOf(value);
        try {
            return this._fromBooleanCreator.call1(arg);
        } catch (Throwable t0) {
            return ctxt.handleInstantiationProblem(this._fromBooleanCreator.getDeclaringClass(), arg, rewrapCtorProblem(ctxt, t0));
        }
    }

    public AnnotatedWithParams getDelegateCreator() {
        return this._delegateCreator;
    }

    public AnnotatedWithParams getArrayDelegateCreator() {
        return this._arrayDelegateCreator;
    }

    public AnnotatedWithParams getDefaultCreator() {
        return this._defaultCreator;
    }

    public AnnotatedWithParams getWithArgsCreator() {
        return this._withArgsCreator;
    }

    public AnnotatedParameter getIncompleteParameter() {
        return this._incompleteParameter;
    }

    @Deprecated
    protected JsonMappingException wrapException(Throwable t) {
        for (Throwable curr = t; curr != null; curr = curr.getCause()) {
            if (curr instanceof JsonMappingException) {
                return (JsonMappingException) curr;
            }
        }
        return new JsonMappingException(null, "Instantiation of " + getValueTypeDesc() + " value failed: " + t.getMessage(), t);
    }

    protected JsonMappingException unwrapAndWrapException(DeserializationContext ctxt, Throwable t) {
        for (Throwable curr = t; curr != null; curr = curr.getCause()) {
            if (curr instanceof JsonMappingException) {
                return (JsonMappingException) curr;
            }
        }
        return ctxt.instantiationException(getValueClass(), t);
    }

    protected JsonMappingException wrapAsJsonMappingException(DeserializationContext ctxt, Throwable t) {
        if (t instanceof JsonMappingException) {
            return (JsonMappingException) t;
        }
        return ctxt.instantiationException(getValueClass(), t);
    }

    protected JsonMappingException rewrapCtorProblem(DeserializationContext ctxt, Throwable t) {
        if ((t instanceof ExceptionInInitializerError) || (t instanceof InvocationTargetException)) {
            Throwable cause = t.getCause();
            if (cause != null) {
                t = cause;
            }
        }
        return wrapAsJsonMappingException(ctxt, t);
    }

    private Object _createUsingDelegate(AnnotatedWithParams delegateCreator, SettableBeanProperty[] delegateArguments, DeserializationContext ctxt, Object delegate) throws IOException {
        if (delegateCreator == null) {
            throw new IllegalStateException("No delegate constructor for " + getValueTypeDesc());
        } else if (delegateArguments == null) {
            try {
                return delegateCreator.call1(delegate);
            } catch (Throwable t) {
                JsonMappingException rewrapCtorProblem = rewrapCtorProblem(ctxt, t);
            }
        } else {
            int len = delegateArguments.length;
            Object[] args = new Object[len];
            for (int i = 0; i < len; i++) {
                SettableBeanProperty prop = delegateArguments[i];
                if (prop == null) {
                    args[i] = delegate;
                } else {
                    args[i] = ctxt.findInjectableValue(prop.getInjectableValueId(), prop, null);
                }
            }
            return delegateCreator.call(args);
        }
    }
}
