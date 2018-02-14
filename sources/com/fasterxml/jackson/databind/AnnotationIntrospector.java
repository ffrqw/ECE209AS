package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AnnotationIntrospector implements Versioned, Serializable {

    public static class ReferenceProperty {
        private final String _name;
        private final Type _type;

        public enum Type {
            MANAGED_REFERENCE,
            BACK_REFERENCE
        }

        public ReferenceProperty(Type t, String n) {
            this._type = t;
            this._name = n;
        }

        public static ReferenceProperty managed(String name) {
            return new ReferenceProperty(Type.MANAGED_REFERENCE, name);
        }

        public static ReferenceProperty back(String name) {
            return new ReferenceProperty(Type.BACK_REFERENCE, name);
        }

        public Type getType() {
            return this._type;
        }

        public String getName() {
            return this._name;
        }

        public boolean isManagedReference() {
            return this._type == Type.MANAGED_REFERENCE;
        }

        public boolean isBackReference() {
            return this._type == Type.BACK_REFERENCE;
        }
    }

    public abstract Version version();

    public static AnnotationIntrospector nopInstance() {
        return NopAnnotationIntrospector.instance;
    }

    public static AnnotationIntrospector pair(AnnotationIntrospector a1, AnnotationIntrospector a2) {
        return new AnnotationIntrospectorPair(a1, a2);
    }

    public Collection<AnnotationIntrospector> allIntrospectors() {
        return Collections.singletonList(this);
    }

    public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
        result.add(this);
        return result;
    }

    public boolean isAnnotationBundle(Annotation ann) {
        return false;
    }

    public ObjectIdInfo findObjectIdInfo(Annotated ann) {
        return null;
    }

    public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
        return objectIdInfo;
    }

    public PropertyName findRootName(AnnotatedClass ac) {
        return null;
    }

    public Value findPropertyIgnorals(Annotated ac) {
        Boolean b;
        Value value = null;
        String[] ignorals = findPropertiesToIgnore(ac, true);
        if (ac instanceof AnnotatedClass) {
            b = findIgnoreUnknownProperties((AnnotatedClass) ac);
        } else {
            b = null;
        }
        if (ignorals != null) {
            value = Value.forIgnoredProperties(ignorals);
            if (b != null) {
                if (b.booleanValue()) {
                }
            }
        } else if (b != null) {
            value = Value.empty();
            if (b != null) {
                value = b.booleanValue() ? value.withIgnoreUnknown() : value.withoutIgnoreUnknown();
            }
        }
        return value;
    }

    @Deprecated
    public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
        return null;
    }

    @Deprecated
    public String[] findPropertiesToIgnore(Annotated ac) {
        return null;
    }

    @Deprecated
    public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
        return null;
    }

    public Boolean isIgnorableType(AnnotatedClass ac) {
        return null;
    }

    public Object findFilterId(Annotated ann) {
        return null;
    }

    public Object findNamingStrategy(AnnotatedClass ac) {
        return null;
    }

    public String findClassDescription(AnnotatedClass ac) {
        return null;
    }

    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        return checker;
    }

    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> mapperConfig, AnnotatedClass ac, JavaType baseType) {
        return null;
    }

    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> mapperConfig, AnnotatedMember am, JavaType baseType) {
        return null;
    }

    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> mapperConfig, AnnotatedMember am, JavaType containerType) {
        return null;
    }

    public List<NamedType> findSubtypes(Annotated a) {
        return null;
    }

    public String findTypeName(AnnotatedClass ac) {
        return null;
    }

    public Boolean isTypeId(AnnotatedMember member) {
        return null;
    }

    public ReferenceProperty findReferenceType(AnnotatedMember member) {
        return null;
    }

    public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
        return null;
    }

    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return false;
    }

    public Object findInjectableValueId(AnnotatedMember m) {
        return null;
    }

    public Boolean hasRequiredMarker(AnnotatedMember m) {
        return null;
    }

    public Class<?>[] findViews(Annotated a) {
        return null;
    }

    public JsonFormat.Value findFormat(Annotated memberOrClass) {
        return null;
    }

    public PropertyName findWrapperName(Annotated ann) {
        return null;
    }

    public String findPropertyDefaultValue(Annotated ann) {
        return null;
    }

    public String findPropertyDescription(Annotated ann) {
        return null;
    }

    public Integer findPropertyIndex(Annotated ann) {
        return null;
    }

    public String findImplicitPropertyName(AnnotatedMember member) {
        return null;
    }

    public Access findPropertyAccess(Annotated ann) {
        return null;
    }

    public AnnotatedMethod resolveSetterConflict(MapperConfig<?> mapperConfig, AnnotatedMethod setter1, AnnotatedMethod setter2) {
        return null;
    }

    public Object findSerializer(Annotated am) {
        return null;
    }

    public Object findKeySerializer(Annotated am) {
        return null;
    }

    public Object findContentSerializer(Annotated am) {
        return null;
    }

    public Object findNullSerializer(Annotated am) {
        return null;
    }

    public Typing findSerializationTyping(Annotated a) {
        return null;
    }

    public Object findSerializationConverter(Annotated a) {
        return null;
    }

    public Object findSerializationContentConverter(AnnotatedMember a) {
        return null;
    }

    @Deprecated
    public Include findSerializationInclusion(Annotated a, Include defValue) {
        return defValue;
    }

    @Deprecated
    public Include findSerializationInclusionForContent(Annotated a, Include defValue) {
        return defValue;
    }

    public JsonInclude.Value findPropertyInclusion(Annotated a) {
        return JsonInclude.Value.empty();
    }

    @Deprecated
    public Class<?> findSerializationType(Annotated a) {
        return null;
    }

    @Deprecated
    public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
        return null;
    }

    @Deprecated
    public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
        return null;
    }

    public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        Class<?> currRaw;
        JavaType type = baseType;
        TypeFactory tf = config.getTypeFactory();
        Class<?> serClass = findSerializationType(a);
        if (serClass != null) {
            if (type.hasRawClass(serClass)) {
                type = type.withStaticTyping();
            } else {
                currRaw = type.getRawClass();
                try {
                    if (serClass.isAssignableFrom(currRaw)) {
                        type = tf.constructGeneralizedType(type, serClass);
                    } else if (currRaw.isAssignableFrom(serClass)) {
                        type = tf.constructSpecializedType(type, serClass);
                    } else {
                        throw new JsonMappingException(null, String.format("Can not refine serialization type %s into %s; types not related", new Object[]{type, serClass.getName()}));
                    }
                } catch (Throwable iae) {
                    throw new JsonMappingException(null, String.format("Failed to widen type %s with annotation (value %s), from '%s': %s", new Object[]{type, serClass.getName(), a.getName(), iae.getMessage()}), iae);
                }
            }
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            Class<?> keyClass = findSerializationKeyType(a, keyType);
            if (keyClass != null) {
                if (keyType.hasRawClass(keyClass)) {
                    keyType = keyType.withStaticTyping();
                } else {
                    currRaw = keyType.getRawClass();
                    try {
                        if (keyClass.isAssignableFrom(currRaw)) {
                            keyType = tf.constructGeneralizedType(keyType, keyClass);
                        } else if (currRaw.isAssignableFrom(keyClass)) {
                            keyType = tf.constructSpecializedType(keyType, keyClass);
                        } else {
                            throw new JsonMappingException(null, String.format("Can not refine serialization key type %s into %s; types not related", new Object[]{keyType, keyClass.getName()}));
                        }
                    } catch (Throwable iae2) {
                        throw new JsonMappingException(null, String.format("Failed to widen key type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[]{type, keyClass.getName(), a.getName(), iae2.getMessage()}), iae2);
                    }
                }
                type = ((MapLikeType) type).withKeyType(keyType);
            }
        }
        JavaType contentType = type.getContentType();
        if (contentType == null) {
            return type;
        }
        Class<?> contentClass = findSerializationContentType(a, contentType);
        if (contentClass == null) {
            return type;
        }
        if (contentType.hasRawClass(contentClass)) {
            contentType = contentType.withStaticTyping();
        } else {
            currRaw = contentType.getRawClass();
            try {
                if (contentClass.isAssignableFrom(currRaw)) {
                    contentType = tf.constructGeneralizedType(contentType, contentClass);
                } else if (currRaw.isAssignableFrom(contentClass)) {
                    contentType = tf.constructSpecializedType(contentType, contentClass);
                } else {
                    throw new JsonMappingException(null, String.format("Can not refine serialization content type %s into %s; types not related", new Object[]{contentType, contentClass.getName()}));
                }
            } catch (Throwable iae22) {
                throw new JsonMappingException(null, String.format("Internal error: failed to refine value type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[]{type, contentClass.getName(), a.getName(), iae22.getMessage()}), iae22);
            }
        }
        return type.withContentType(contentType);
    }

    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        return null;
    }

    public Boolean findSerializationSortAlphabetically(Annotated ann) {
        return null;
    }

    public void findAndAddVirtualProperties(MapperConfig<?> mapperConfig, AnnotatedClass ac, List<BeanPropertyWriter> list) {
    }

    public PropertyName findNameForSerialization(Annotated a) {
        return null;
    }

    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        return false;
    }

    @Deprecated
    public String findEnumValue(Enum<?> value) {
        return value.name();
    }

    public String[] findEnumValues(Class<?> cls, Enum<?>[] enumValues, String[] names) {
        int len = enumValues.length;
        for (int i = 0; i < len; i++) {
            if (names[i] == null) {
                names[i] = findEnumValue(enumValues[i]);
            }
        }
        return names;
    }

    public Enum<?> findDefaultEnumValue(Class<Enum<?>> cls) {
        return null;
    }

    public Object findDeserializer(Annotated am) {
        return null;
    }

    public Object findKeyDeserializer(Annotated am) {
        return null;
    }

    public Object findContentDeserializer(Annotated am) {
        return null;
    }

    public Object findDeserializationConverter(Annotated a) {
        return null;
    }

    public Object findDeserializationContentConverter(AnnotatedMember a) {
        return null;
    }

    public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
        JavaType type = baseType;
        TypeFactory tf = config.getTypeFactory();
        Class<?> valueClass = findDeserializationType(a, type);
        if (!(valueClass == null || type.hasRawClass(valueClass))) {
            try {
                type = tf.constructSpecializedType(type, valueClass);
            } catch (Throwable iae) {
                throw new JsonMappingException(null, String.format("Failed to narrow type %s with annotation (value %s), from '%s': %s", new Object[]{type, valueClass.getName(), a.getName(), iae.getMessage()}), iae);
            }
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            Class<?> keyClass = findDeserializationKeyType(a, keyType);
            if (keyClass != null) {
                try {
                    type = ((MapLikeType) type).withKeyType(tf.constructSpecializedType(keyType, keyClass));
                } catch (Throwable iae2) {
                    throw new JsonMappingException(null, String.format("Failed to narrow key type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[]{type, keyClass.getName(), a.getName(), iae2.getMessage()}), iae2);
                }
            }
        }
        JavaType contentType = type.getContentType();
        if (contentType != null) {
            Class<?> contentClass = findDeserializationContentType(a, contentType);
            if (contentClass != null) {
                try {
                    type = type.withContentType(tf.constructSpecializedType(contentType, contentClass));
                } catch (Throwable iae22) {
                    throw new JsonMappingException(null, String.format("Failed to narrow value type of %s with concrete-type annotation (value %s), from '%s': %s", new Object[]{type, contentClass.getName(), a.getName(), iae22.getMessage()}), iae22);
                }
            }
        }
        return type;
    }

    @Deprecated
    public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
        return null;
    }

    @Deprecated
    public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
        return null;
    }

    @Deprecated
    public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
        return null;
    }

    public Object findValueInstantiator(AnnotatedClass ac) {
        return null;
    }

    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        return null;
    }

    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
        return null;
    }

    public PropertyName findNameForDeserialization(Annotated a) {
        return null;
    }

    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return false;
    }

    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return false;
    }

    public boolean hasCreatorAnnotation(Annotated a) {
        return false;
    }

    public Mode findCreatorBinding(Annotated a) {
        return null;
    }

    protected <A extends Annotation> A _findAnnotation(Annotated annotated, Class<A> annoClass) {
        return annotated.getAnnotation(annoClass);
    }

    protected boolean _hasAnnotation(Annotated annotated, Class<? extends Annotation> annoClass) {
        return annotated.hasAnnotation(annoClass);
    }

    protected boolean _hasOneOf(Annotated annotated, Class<? extends Annotation>[] annoClasses) {
        return annotated.hasOneOf(annoClasses);
    }
}
