package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class PropertyBuilder {
    private static final Object NO_DEFAULT_MARKER = Boolean.FALSE;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final BeanDescription _beanDesc;
    protected final SerializationConfig _config;
    protected Object _defaultBean;
    protected final Value _defaultInclusion;
    protected final boolean _useRealPropertyDefaults;

    public PropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
        this._config = config;
        this._beanDesc = beanDesc;
        Value inclPerType = Value.merge(beanDesc.findPropertyInclusion(Value.empty()), config.getDefaultPropertyInclusion(beanDesc.getBeanClass(), Value.empty()));
        this._defaultInclusion = Value.merge(config.getDefaultPropertyInclusion(), inclPerType);
        this._useRealPropertyDefaults = inclPerType.getValueInclusion() == Include.NON_DEFAULT;
        this._annotationIntrospector = this._config.getAnnotationIntrospector();
    }

    public Annotations getClassAnnotations() {
        return this._beanDesc.getClassAnnotations();
    }

    protected BeanPropertyWriter buildWriter(SerializerProvider prov, BeanPropertyDefinition propDef, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, TypeSerializer contentTypeSer, AnnotatedMember am, boolean defaultUseStaticTyping) throws JsonMappingException {
        try {
            JavaType actualType;
            JavaType serializationType = findSerializationType(am, defaultUseStaticTyping, declaredType);
            if (contentTypeSer != null) {
                if (serializationType == null) {
                    serializationType = declaredType;
                }
                if (serializationType.getContentType() == null) {
                    prov.reportBadPropertyDefinition(this._beanDesc, propDef, "serialization type " + serializationType + " has no content", new Object[0]);
                }
                serializationType = serializationType.withContentTypeHandler(contentTypeSer);
                serializationType.getContentType();
            }
            Object valueToSuppress = null;
            boolean suppressNulls = false;
            if (serializationType == null) {
                actualType = declaredType;
            } else {
                actualType = serializationType;
            }
            Include inclusion = this._config.getDefaultPropertyInclusion(actualType.getRawClass(), this._defaultInclusion).withOverrides(propDef.findInclusion()).getValueInclusion();
            if (inclusion == Include.USE_DEFAULTS) {
                inclusion = Include.ALWAYS;
            }
            switch (inclusion) {
                case NON_DEFAULT:
                    if (this._useRealPropertyDefaults) {
                        if (prov.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                            am.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                        }
                        valueToSuppress = getPropertyDefaultValue(propDef.getName(), am, actualType);
                    } else {
                        valueToSuppress = getDefaultValue(actualType);
                        suppressNulls = true;
                    }
                    if (valueToSuppress != null) {
                        if (valueToSuppress.getClass().isArray()) {
                            valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                            break;
                        }
                    }
                    suppressNulls = true;
                    break;
                    break;
                case NON_ABSENT:
                    suppressNulls = true;
                    if (actualType.isReferenceType()) {
                        valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
                        break;
                    }
                    break;
                case NON_EMPTY:
                    suppressNulls = true;
                    valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
                    break;
                case NON_NULL:
                    suppressNulls = true;
                    break;
            }
            if (actualType.isContainerType() && !this._config.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)) {
                valueToSuppress = BeanPropertyWriter.MARKER_FOR_EMPTY;
            }
            BeanPropertyWriter bpw = new BeanPropertyWriter(propDef, am, this._beanDesc.getClassAnnotations(), declaredType, ser, typeSer, serializationType, suppressNulls, valueToSuppress);
            Object serDef = this._annotationIntrospector.findNullSerializer(am);
            if (serDef != null) {
                bpw.assignNullSerializer(prov.serializerInstance(am, serDef));
            }
            NameTransformer unwrapper = this._annotationIntrospector.findUnwrappingNameTransformer(am);
            if (unwrapper != null) {
                return bpw.unwrappingWriter(unwrapper);
            }
            return bpw;
        } catch (JsonMappingException e) {
            return (BeanPropertyWriter) prov.reportBadPropertyDefinition(this._beanDesc, propDef, e.getMessage(), new Object[0]);
        }
    }

    protected JavaType findSerializationType(Annotated a, boolean useStaticTyping, JavaType declaredType) throws JsonMappingException {
        JavaType secondary = this._annotationIntrospector.refineSerializationType(this._config, a, declaredType);
        if (secondary != declaredType) {
            Class<?> serClass = secondary.getRawClass();
            Class<?> rawDeclared = declaredType.getRawClass();
            if (serClass.isAssignableFrom(rawDeclared) || rawDeclared.isAssignableFrom(serClass)) {
                useStaticTyping = true;
                declaredType = secondary;
            } else {
                throw new IllegalArgumentException("Illegal concrete-type annotation for method '" + a.getName() + "': class " + serClass.getName() + " not a super-type of (declared) class " + rawDeclared.getName());
            }
        }
        Typing typing = this._annotationIntrospector.findSerializationTyping(a);
        if (!(typing == null || typing == Typing.DEFAULT_TYPING)) {
            useStaticTyping = typing == Typing.STATIC;
        }
        if (useStaticTyping) {
            return declaredType.withStaticTyping();
        }
        return null;
    }

    protected Object getDefaultBean() {
        Object def = this._defaultBean;
        if (def == null) {
            def = this._beanDesc.instantiateBean(this._config.canOverrideAccessModifiers());
            if (def == null) {
                def = NO_DEFAULT_MARKER;
            }
            this._defaultBean = def;
        }
        return def == NO_DEFAULT_MARKER ? null : this._defaultBean;
    }

    protected Object getPropertyDefaultValue(String name, AnnotatedMember member, JavaType type) {
        Object defaultBean = getDefaultBean();
        if (defaultBean == null) {
            return getDefaultValue(type);
        }
        try {
            return member.getValue(defaultBean);
        } catch (Exception e) {
            return _throwWrapped(e, name, defaultBean);
        }
    }

    protected Object getDefaultValue(JavaType type) {
        Class<?> cls = type.getRawClass();
        Class<?> prim = ClassUtil.primitiveType(cls);
        if (prim != null) {
            return ClassUtil.defaultValue(prim);
        }
        if (type.isContainerType() || type.isReferenceType()) {
            return Include.NON_EMPTY;
        }
        if (cls == String.class) {
            return "";
        }
        return null;
    }

    protected Object _throwWrapped(Exception e, String propName, Object defaultBean) {
        Throwable t = e;
        while (t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw ((Error) t);
        } else if (t instanceof RuntimeException) {
            throw ((RuntimeException) t);
        } else {
            throw new IllegalArgumentException("Failed to get property '" + propName + "' of default " + defaultBean.getClass().getName() + " instance");
        }
    }
}
