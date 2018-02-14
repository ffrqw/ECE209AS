package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.None;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Attr;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Prop;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Typing;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.fasterxml.jackson.databind.annotation.JsonValueInstantiator;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.ext.Java7Support;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.AttributePropertyWriter;
import com.fasterxml.jackson.databind.ser.std.RawSerializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.LRUMap;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class JacksonAnnotationIntrospector extends AnnotationIntrospector implements Serializable {
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_DESER = new Class[]{JsonDeserialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class};
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_SER = new Class[]{JsonSerialize.class, JsonView.class, JsonFormat.class, JsonTypeInfo.class, JsonRawValue.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class};
    private static final Java7Support _java7Helper;
    private static final long serialVersionUID = 1;
    protected transient LRUMap<Class<?>, Boolean> _annotationsInside = new LRUMap(48, 48);
    protected boolean _cfgConstructorPropertiesImpliesCreator = true;

    static {
        Java7Support x = null;
        try {
            x = Java7Support.instance();
        } catch (Throwable th) {
        }
        _java7Helper = x;
    }

    public Version version() {
        return PackageVersion.VERSION;
    }

    protected Object readResolve() {
        if (this._annotationsInside == null) {
            this._annotationsInside = new LRUMap(48, 48);
        }
        return this;
    }

    public JacksonAnnotationIntrospector setConstructorPropertiesImpliesCreator(boolean b) {
        this._cfgConstructorPropertiesImpliesCreator = b;
        return this;
    }

    public boolean isAnnotationBundle(Annotation ann) {
        Class<?> type = ann.annotationType();
        Boolean b = (Boolean) this._annotationsInside.get(type);
        if (b == null) {
            b = Boolean.valueOf(type.getAnnotation(JacksonAnnotationsInside.class) != null);
            this._annotationsInside.putIfAbsent(type, b);
        }
        return b.booleanValue();
    }

    @Deprecated
    public String findEnumValue(Enum<?> value) {
        try {
            Field f = value.getClass().getField(value.name());
            if (f != null) {
                JsonProperty prop = (JsonProperty) f.getAnnotation(JsonProperty.class);
                if (prop != null) {
                    String n = prop.value();
                    if (!(n == null || n.isEmpty())) {
                        return n;
                    }
                }
            }
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e2) {
        }
        return value.name();
    }

    public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
        HashMap<String, String> expl = null;
        for (Field f : ClassUtil.getDeclaredFields(enumType)) {
            if (f.isEnumConstant()) {
                JsonProperty prop = (JsonProperty) f.getAnnotation(JsonProperty.class);
                if (prop != null) {
                    String n = prop.value();
                    if (!n.isEmpty()) {
                        if (expl == null) {
                            expl = new HashMap();
                        }
                        expl.put(f.getName(), n);
                    }
                }
            }
        }
        if (expl != null) {
            int end = enumValues.length;
            for (int i = 0; i < end; i++) {
                String explValue = (String) expl.get(enumValues[i].name());
                if (explValue != null) {
                    names[i] = explValue;
                }
            }
        }
        return names;
    }

    public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
        return ClassUtil.findFirstAnnotatedEnumValue(enumCls, JsonEnumDefaultValue.class);
    }

    public PropertyName findRootName(AnnotatedClass ac) {
        JsonRootName ann = (JsonRootName) _findAnnotation(ac, JsonRootName.class);
        if (ann == null) {
            return null;
        }
        String ns = ann.namespace();
        if (ns != null && ns.length() == 0) {
            ns = null;
        }
        return PropertyName.construct(ann.value(), ns);
    }

    public Value findPropertyIgnorals(Annotated a) {
        JsonIgnoreProperties v = (JsonIgnoreProperties) _findAnnotation(a, JsonIgnoreProperties.class);
        if (v == null) {
            return null;
        }
        return Value.from(v);
    }

    @Deprecated
    public String[] findPropertiesToIgnore(Annotated a, boolean forSerialization) {
        Value v = findPropertyIgnorals(a);
        if (v == null) {
            return null;
        }
        if (forSerialization) {
            if (v.getAllowGetters()) {
                return null;
            }
        } else if (v.getAllowSetters()) {
            return null;
        }
        Set<String> ignored = v.getIgnored();
        return (String[]) ignored.toArray(new String[ignored.size()]);
    }

    @Deprecated
    public Boolean findIgnoreUnknownProperties(AnnotatedClass a) {
        Value v = findPropertyIgnorals(a);
        return v == null ? null : Boolean.valueOf(v.getIgnoreUnknown());
    }

    public Boolean isIgnorableType(AnnotatedClass ac) {
        JsonIgnoreType ignore = (JsonIgnoreType) _findAnnotation(ac, JsonIgnoreType.class);
        return ignore == null ? null : Boolean.valueOf(ignore.value());
    }

    public Object findFilterId(Annotated a) {
        JsonFilter ann = (JsonFilter) _findAnnotation(a, JsonFilter.class);
        if (ann != null) {
            String id = ann.value();
            if (id.length() > 0) {
                return id;
            }
        }
        return null;
    }

    public Object findNamingStrategy(AnnotatedClass ac) {
        JsonNaming ann = (JsonNaming) _findAnnotation(ac, JsonNaming.class);
        return ann == null ? null : ann.value();
    }

    public String findClassDescription(AnnotatedClass ac) {
        JsonClassDescription ann = (JsonClassDescription) _findAnnotation(ac, JsonClassDescription.class);
        return ann == null ? null : ann.value();
    }

    public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
        JsonAutoDetect ann = (JsonAutoDetect) _findAnnotation(ac, JsonAutoDetect.class);
        return ann == null ? checker : checker.with(ann);
    }

    public String findImplicitPropertyName(AnnotatedMember m) {
        PropertyName n = _findConstructorName(m);
        return n == null ? null : n.getSimpleName();
    }

    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return _isIgnorable(m);
    }

    public Boolean hasRequiredMarker(AnnotatedMember m) {
        JsonProperty ann = (JsonProperty) _findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return Boolean.valueOf(ann.required());
        }
        return null;
    }

    public Access findPropertyAccess(Annotated m) {
        JsonProperty ann = (JsonProperty) _findAnnotation(m, JsonProperty.class);
        if (ann != null) {
            return ann.access();
        }
        return null;
    }

    public String findPropertyDescription(Annotated ann) {
        JsonPropertyDescription desc = (JsonPropertyDescription) _findAnnotation(ann, JsonPropertyDescription.class);
        return desc == null ? null : desc.value();
    }

    public Integer findPropertyIndex(Annotated ann) {
        JsonProperty prop = (JsonProperty) _findAnnotation(ann, JsonProperty.class);
        if (prop != null) {
            int ix = prop.index();
            if (ix != -1) {
                return Integer.valueOf(ix);
            }
        }
        return null;
    }

    public String findPropertyDefaultValue(Annotated ann) {
        JsonProperty prop = (JsonProperty) _findAnnotation(ann, JsonProperty.class);
        if (prop == null) {
            return null;
        }
        String str = prop.defaultValue();
        return str.isEmpty() ? null : str;
    }

    public JsonFormat.Value findFormat(Annotated ann) {
        JsonFormat f = (JsonFormat) _findAnnotation(ann, JsonFormat.class);
        return f == null ? null : new JsonFormat.Value(f);
    }

    public ReferenceProperty findReferenceType(AnnotatedMember member) {
        JsonManagedReference ref1 = (JsonManagedReference) _findAnnotation(member, JsonManagedReference.class);
        if (ref1 != null) {
            return ReferenceProperty.managed(ref1.value());
        }
        JsonBackReference ref2 = (JsonBackReference) _findAnnotation(member, JsonBackReference.class);
        if (ref2 != null) {
            return ReferenceProperty.back(ref2.value());
        }
        return null;
    }

    public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
        JsonUnwrapped ann = (JsonUnwrapped) _findAnnotation(member, JsonUnwrapped.class);
        if (ann == null || !ann.enabled()) {
            return null;
        }
        return NameTransformer.simpleTransformer(ann.prefix(), ann.suffix());
    }

    public Object findInjectableValueId(AnnotatedMember m) {
        JacksonInject ann = (JacksonInject) _findAnnotation(m, JacksonInject.class);
        if (ann == null) {
            return null;
        }
        Object id = ann.value();
        if (id.length() != 0) {
            return id;
        }
        if (!(m instanceof AnnotatedMethod)) {
            return m.getRawType().getName();
        }
        AnnotatedMethod am = (AnnotatedMethod) m;
        if (am.getParameterCount() == 0) {
            return m.getRawType().getName();
        }
        return am.getRawParameterType(0).getName();
    }

    public Class<?>[] findViews(Annotated a) {
        JsonView ann = (JsonView) _findAnnotation(a, JsonView.class);
        return ann == null ? null : ann.value();
    }

    public AnnotatedMethod resolveSetterConflict(MapperConfig<?> mapperConfig, AnnotatedMethod setter1, AnnotatedMethod setter2) {
        Class<?> cls1 = setter1.getRawParameterType(0);
        Class<?> cls2 = setter2.getRawParameterType(0);
        if (cls1.isPrimitive()) {
            if (!cls2.isPrimitive()) {
                return setter1;
            }
        } else if (cls2.isPrimitive()) {
            return setter2;
        }
        if (cls1 == String.class) {
            if (cls2 != String.class) {
                return setter1;
            }
        } else if (cls2 == String.class) {
            return setter2;
        }
        return null;
    }

    public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
        return _findTypeResolver(config, ac, baseType);
    }

    public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
        if (baseType.isContainerType() || baseType.isReferenceType()) {
            return null;
        }
        return _findTypeResolver(config, am, baseType);
    }

    public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
        if (containerType.getContentType() != null) {
            return _findTypeResolver(config, am, containerType);
        }
        throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
    }

    public List<NamedType> findSubtypes(Annotated a) {
        JsonSubTypes t = (JsonSubTypes) _findAnnotation(a, JsonSubTypes.class);
        if (t == null) {
            return null;
        }
        Type[] types = t.value();
        List<NamedType> result = new ArrayList(types.length);
        Type[] arr$ = types;
        int len$ = types.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Type type = arr$[i$];
            result.add(new NamedType(type.value(), type.name()));
        }
        return result;
    }

    public String findTypeName(AnnotatedClass ac) {
        JsonTypeName tn = (JsonTypeName) _findAnnotation(ac, JsonTypeName.class);
        return tn == null ? null : tn.value();
    }

    public Boolean isTypeId(AnnotatedMember member) {
        return Boolean.valueOf(_hasAnnotation(member, JsonTypeId.class));
    }

    public ObjectIdInfo findObjectIdInfo(Annotated ann) {
        JsonIdentityInfo info = (JsonIdentityInfo) _findAnnotation(ann, JsonIdentityInfo.class);
        if (info == null || info.generator() == None.class) {
            return null;
        }
        return new ObjectIdInfo(PropertyName.construct(info.property()), info.scope(), info.generator(), info.resolver());
    }

    public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
        JsonIdentityReference ref = (JsonIdentityReference) _findAnnotation(ann, JsonIdentityReference.class);
        if (ref != null) {
            return objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
        }
        return objectIdInfo;
    }

    public Object findSerializer(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            Class<? extends JsonSerializer> serClass = ann.using();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        JsonRawValue annRaw = (JsonRawValue) _findAnnotation(a, JsonRawValue.class);
        if (annRaw == null || !annRaw.value()) {
            return null;
        }
        return new RawSerializer(a.getRawType());
    }

    public Object findKeySerializer(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            Class<? extends JsonSerializer> serClass = ann.keyUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }

    public Object findContentSerializer(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            Class<? extends JsonSerializer> serClass = ann.contentUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }

    public Object findNullSerializer(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            Class<? extends JsonSerializer> serClass = ann.nullsUsing();
            if (serClass != JsonSerializer.None.class) {
                return serClass;
            }
        }
        return null;
    }

    public Include findSerializationInclusion(Annotated a, Include defValue) {
        JsonInclude inc = (JsonInclude) _findAnnotation(a, JsonInclude.class);
        if (inc != null) {
            Include v = inc.value();
            if (v != Include.USE_DEFAULTS) {
                return v;
            }
        }
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        if (ann != null) {
            switch (ann.include()) {
                case ALWAYS:
                    return Include.ALWAYS;
                case NON_NULL:
                    return Include.NON_NULL;
                case NON_DEFAULT:
                    return Include.NON_DEFAULT;
                case NON_EMPTY:
                    return Include.NON_EMPTY;
            }
        }
        return defValue;
    }

    @Deprecated
    public Include findSerializationInclusionForContent(Annotated a, Include defValue) {
        JsonInclude inc = (JsonInclude) _findAnnotation(a, JsonInclude.class);
        if (inc != null) {
            Include incl = inc.content();
            if (incl != Include.USE_DEFAULTS) {
                return incl;
            }
        }
        return defValue;
    }

    public JsonInclude.Value findPropertyInclusion(Annotated a) {
        JsonInclude inc = (JsonInclude) _findAnnotation(a, JsonInclude.class);
        Include valueIncl = inc == null ? Include.USE_DEFAULTS : inc.value();
        if (valueIncl == Include.USE_DEFAULTS) {
            JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
            if (ann != null) {
                switch (ann.include()) {
                    case ALWAYS:
                        valueIncl = Include.ALWAYS;
                        break;
                    case NON_NULL:
                        valueIncl = Include.NON_NULL;
                        break;
                    case NON_DEFAULT:
                        valueIncl = Include.NON_DEFAULT;
                        break;
                    case NON_EMPTY:
                        valueIncl = Include.NON_EMPTY;
                        break;
                }
            }
        }
        return JsonInclude.Value.construct(valueIncl, inc == null ? Include.USE_DEFAULTS : inc.content());
    }

    @Deprecated
    public Class<?> findSerializationType(Annotated am) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(am, JsonSerialize.class);
        return ann == null ? null : _classIfExplicit(ann.as());
    }

    @Deprecated
    public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(am, JsonSerialize.class);
        return ann == null ? null : _classIfExplicit(ann.keyAs());
    }

    @Deprecated
    public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(am, JsonSerialize.class);
        return ann == null ? null : _classIfExplicit(ann.contentAs());
    }

    public Typing findSerializationTyping(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        return ann == null ? null : ann.typing();
    }

    public Object findSerializationConverter(Annotated a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        return ann == null ? null : _classIfExplicit(ann.converter(), Converter.None.class);
    }

    public Object findSerializationContentConverter(AnnotatedMember a) {
        JsonSerialize ann = (JsonSerialize) _findAnnotation(a, JsonSerialize.class);
        return ann == null ? null : _classIfExplicit(ann.contentConverter(), Converter.None.class);
    }

    public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
        JsonPropertyOrder order = (JsonPropertyOrder) _findAnnotation(ac, JsonPropertyOrder.class);
        return order == null ? null : order.value();
    }

    public Boolean findSerializationSortAlphabetically(Annotated ann) {
        return _findSortAlpha(ann);
    }

    private final Boolean _findSortAlpha(Annotated ann) {
        JsonPropertyOrder order = (JsonPropertyOrder) _findAnnotation(ann, JsonPropertyOrder.class);
        if (order == null || !order.alphabetic()) {
            return null;
        }
        return Boolean.TRUE;
    }

    public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
        JsonAppend ann = (JsonAppend) _findAnnotation(ac, JsonAppend.class);
        if (ann != null) {
            int i;
            BeanPropertyWriter bpw;
            boolean prepend = ann.prepend();
            JavaType propType = null;
            Attr[] attrs = ann.attrs();
            int len = attrs.length;
            for (i = 0; i < len; i++) {
                if (propType == null) {
                    propType = config.constructType(Object.class);
                }
                bpw = _constructVirtualProperty(attrs[i], config, ac, propType);
                if (prepend) {
                    properties.add(i, bpw);
                } else {
                    properties.add(bpw);
                }
            }
            Prop[] props = ann.props();
            len = props.length;
            for (i = 0; i < len; i++) {
                bpw = _constructVirtualProperty(props[i], config, ac);
                if (prepend) {
                    properties.add(i, bpw);
                } else {
                    properties.add(bpw);
                }
            }
        }
    }

    protected BeanPropertyWriter _constructVirtualProperty(Attr attr, MapperConfig<?> config, AnnotatedClass ac, JavaType type) {
        PropertyMetadata metadata = attr.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        String attrName = attr.value();
        PropertyName propName = _propertyName(attr.propName(), attr.propNamespace());
        if (!propName.hasSimpleName()) {
            propName = PropertyName.construct(attrName);
        }
        return AttributePropertyWriter.construct(attrName, SimpleBeanPropertyDefinition.construct((MapperConfig) config, new VirtualAnnotatedMember((TypeResolutionContext) ac, ac.getRawType(), attrName, type), propName, metadata, attr.include()), ac.getAnnotations(), type);
    }

    protected BeanPropertyWriter _constructVirtualProperty(Prop prop, MapperConfig<?> config, AnnotatedClass ac) {
        PropertyMetadata metadata = prop.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        PropertyName propName = _propertyName(prop.name(), prop.namespace());
        JavaType type = config.constructType(prop.type());
        SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct((MapperConfig) config, new VirtualAnnotatedMember((TypeResolutionContext) ac, ac.getRawType(), propName.getSimpleName(), type), propName, metadata, prop.include());
        Class<?> implClass = prop.value();
        HandlerInstantiator hi = config.getHandlerInstantiator();
        VirtualBeanPropertyWriter bpw = hi == null ? null : hi.virtualPropertyWriterInstance(config, implClass);
        if (bpw == null) {
            bpw = (VirtualBeanPropertyWriter) ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return bpw.withConfig(config, ac, propDef, type);
    }

    public PropertyName findNameForSerialization(Annotated a) {
        JsonGetter jg = (JsonGetter) _findAnnotation(a, JsonGetter.class);
        if (jg != null) {
            return PropertyName.construct(jg.value());
        }
        JsonProperty pann = (JsonProperty) _findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        if (_hasOneOf(a, ANNOTATIONS_TO_INFER_SER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    public boolean hasAsValueAnnotation(AnnotatedMethod am) {
        JsonValue ann = (JsonValue) _findAnnotation(am, JsonValue.class);
        return ann != null && ann.value();
    }

    public Object findDeserializer(Annotated a) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann != null) {
            Class<? extends JsonDeserializer> deserClass = ann.using();
            if (deserClass != JsonDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }

    public Object findKeyDeserializer(Annotated a) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann != null) {
            Class<? extends KeyDeserializer> deserClass = ann.keyUsing();
            if (deserClass != KeyDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }

    public Object findContentDeserializer(Annotated a) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        if (ann != null) {
            Class<? extends JsonDeserializer> deserClass = ann.contentUsing();
            if (deserClass != JsonDeserializer.None.class) {
                return deserClass;
            }
        }
        return null;
    }

    public Object findDeserializationConverter(Annotated a) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        return ann == null ? null : _classIfExplicit(ann.converter(), Converter.None.class);
    }

    public Object findDeserializationContentConverter(AnnotatedMember a) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(a, JsonDeserialize.class);
        return ann == null ? null : _classIfExplicit(ann.contentConverter(), Converter.None.class);
    }

    @Deprecated
    public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(am, JsonDeserialize.class);
        return ann == null ? null : _classIfExplicit(ann.contentAs());
    }

    @Deprecated
    public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(am, JsonDeserialize.class);
        return ann == null ? null : _classIfExplicit(ann.as());
    }

    @Deprecated
    public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(am, JsonDeserialize.class);
        return ann == null ? null : _classIfExplicit(ann.keyAs());
    }

    public Object findValueInstantiator(AnnotatedClass ac) {
        JsonValueInstantiator ann = (JsonValueInstantiator) _findAnnotation(ac, JsonValueInstantiator.class);
        return ann == null ? null : ann.value();
    }

    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        JsonDeserialize ann = (JsonDeserialize) _findAnnotation(ac, JsonDeserialize.class);
        return ann == null ? null : _classIfExplicit(ann.builder());
    }

    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
        JsonPOJOBuilder ann = (JsonPOJOBuilder) _findAnnotation(ac, JsonPOJOBuilder.class);
        return ann == null ? null : new JsonPOJOBuilder.Value(ann);
    }

    public PropertyName findNameForDeserialization(Annotated a) {
        JsonSetter js = (JsonSetter) _findAnnotation(a, JsonSetter.class);
        if (js != null) {
            return PropertyName.construct(js.value());
        }
        JsonProperty pann = (JsonProperty) _findAnnotation(a, JsonProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.value());
        }
        if (_hasOneOf(a, ANNOTATIONS_TO_INFER_DESER)) {
            return PropertyName.USE_DEFAULT;
        }
        return null;
    }

    public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
        return _hasAnnotation(am, JsonAnySetter.class);
    }

    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return _hasAnnotation(am, JsonAnyGetter.class);
    }

    public boolean hasCreatorAnnotation(Annotated a) {
        JsonCreator ann = (JsonCreator) _findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            if (ann.mode() != Mode.DISABLED) {
                return true;
            }
            return false;
        } else if (!this._cfgConstructorPropertiesImpliesCreator || !(a instanceof AnnotatedConstructor) || _java7Helper == null) {
            return false;
        } else {
            Boolean b = _java7Helper.hasCreatorAnnotation(a);
            if (b != null) {
                return b.booleanValue();
            }
            return false;
        }
    }

    public Mode findCreatorBinding(Annotated a) {
        JsonCreator ann = (JsonCreator) _findAnnotation(a, JsonCreator.class);
        return ann == null ? null : ann.mode();
    }

    protected boolean _isIgnorable(Annotated a) {
        JsonIgnore ann = (JsonIgnore) _findAnnotation(a, JsonIgnore.class);
        if (ann != null) {
            return ann.value();
        }
        if (_java7Helper != null) {
            Boolean b = _java7Helper.findTransient(a);
            if (b != null) {
                return b.booleanValue();
            }
        }
        return false;
    }

    protected Class<?> _classIfExplicit(Class<?> cls) {
        if (cls == null || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }

    protected Class<?> _classIfExplicit(Class<?> cls, Class<?> implicit) {
        cls = _classIfExplicit(cls);
        return (cls == null || cls == implicit) ? null : cls;
    }

    protected PropertyName _propertyName(String localName, String namespace) {
        if (localName.isEmpty()) {
            return PropertyName.USE_DEFAULT;
        }
        if (namespace == null || namespace.isEmpty()) {
            return PropertyName.construct(localName);
        }
        return PropertyName.construct(localName, namespace);
    }

    protected PropertyName _findConstructorName(Annotated a) {
        if (a instanceof AnnotatedParameter) {
            AnnotatedParameter p = (AnnotatedParameter) a;
            if (!(p.getOwner() == null || _java7Helper == null)) {
                PropertyName name = _java7Helper.findConstructorName(p);
                if (name != null) {
                    return name;
                }
            }
        }
        return null;
    }

    protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {
        TypeResolverBuilder<?> b;
        JsonTypeInfo info = (JsonTypeInfo) _findAnnotation(ann, JsonTypeInfo.class);
        JsonTypeResolver resAnn = (JsonTypeResolver) _findAnnotation(ann, JsonTypeResolver.class);
        if (resAnn != null) {
            if (info == null) {
                return null;
            }
            b = config.typeResolverBuilderInstance(ann, resAnn.value());
        } else if (info == null) {
            return null;
        } else {
            if (info.use() == Id.NONE) {
                return _constructNoTypeResolverBuilder();
            }
            b = _constructStdTypeResolverBuilder();
        }
        JsonTypeIdResolver idResInfo = (JsonTypeIdResolver) _findAnnotation(ann, JsonTypeIdResolver.class);
        TypeIdResolver idRes = idResInfo == null ? null : config.typeIdResolverInstance(ann, idResInfo.value());
        if (idRes != null) {
            idRes.init(baseType);
        }
        b = b.init(info.use(), idRes);
        As inclusion = info.include();
        if (inclusion == As.EXTERNAL_PROPERTY && (ann instanceof AnnotatedClass)) {
            inclusion = As.PROPERTY;
        }
        b = b.inclusion(inclusion).typeProperty(info.property());
        Class<?> defaultImpl = info.defaultImpl();
        if (!(defaultImpl == JsonTypeInfo.None.class || defaultImpl.isAnnotation())) {
            b = b.defaultImpl(defaultImpl);
        }
        return b.typeIdVisibility(info.visible());
    }

    protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
        return new StdTypeResolverBuilder();
    }

    protected StdTypeResolverBuilder _constructNoTypeResolverBuilder() {
        return StdTypeResolverBuilder.noTypeInfoBuilder();
    }
}
