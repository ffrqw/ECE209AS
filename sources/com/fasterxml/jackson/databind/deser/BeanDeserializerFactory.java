package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector.ReferenceProperty;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty.Std;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder.Value;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
import com.fasterxml.jackson.databind.deser.impl.MethodProperty;
import com.fasterxml.jackson.databind.deser.impl.NoClassDefFoundDeserializer;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import com.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable {
    private static final Class<?>[] INIT_CAUSE_PARAMS = new Class[]{Throwable.class};
    private static final Class<?>[] NO_VIEWS = new Class[0];
    public static final BeanDeserializerFactory instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
    private static final long serialVersionUID = 1;

    public BeanDeserializerFactory(DeserializerFactoryConfig config) {
        super(config);
    }

    public DeserializerFactory withConfig(DeserializerFactoryConfig config) {
        if (this._factoryConfig == config) {
            return this;
        }
        if (getClass() != BeanDeserializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanDeserializerFactory (" + getClass().getName() + ") has not properly overridden method 'withAdditionalDeserializers': can not instantiate subtype with additional deserializer definitions");
        }
        this(config);
        return this;
    }

    public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        JsonDeserializer<Object> custom = _findCustomBeanDeserializer(type, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        if (type.isThrowable()) {
            return buildThrowableDeserializer(ctxt, type, beanDesc);
        }
        if (!(!type.isAbstract() || type.isPrimitive() || type.isEnumType())) {
            JavaType concreteType = materializeAbstractType(ctxt, type, beanDesc);
            if (concreteType != null) {
                return buildBeanDeserializer(ctxt, concreteType, config.introspect(concreteType));
            }
        }
        JsonDeserializer<Object> deser = findStdDeserializer(ctxt, type, beanDesc);
        if (deser != null) {
            return deser;
        }
        if (isPotentialBeanType(type.getRawClass())) {
            return buildBeanDeserializer(ctxt, type, beanDesc);
        }
        return null;
    }

    public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription beanDesc, Class<?> builderClass) throws JsonMappingException {
        return buildBuilderBasedDeserializer(ctxt, valueType, ctxt.getConfig().introspectForBuilder(ctxt.constructType(builderClass)));
    }

    protected JsonDeserializer<?> findStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> deser = findDefaultDeserializer(ctxt, type, beanDesc);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
            }
        }
        return deser;
    }

    protected JavaType materializeAbstractType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        for (AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
            JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
            if (concrete != null) {
                return concrete;
            }
        }
        return null;
    }

    public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        try {
            JsonDeserializer<Object> deserializer;
            ValueInstantiator valueInstantiator = findValueInstantiator(ctxt, beanDesc);
            BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
            builder.setValueInstantiator(valueInstantiator);
            addBeanProps(ctxt, beanDesc, builder);
            addObjectIdReader(ctxt, beanDesc, builder);
            addReferenceProperties(ctxt, beanDesc, builder);
            addInjectables(ctxt, beanDesc, builder);
            DeserializationConfig config = ctxt.getConfig();
            if (this._factoryConfig.hasDeserializerModifiers()) {
                for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                    builder = mod.updateBuilder(config, beanDesc, builder);
                }
            }
            if (!type.isAbstract() || valueInstantiator.canInstantiate()) {
                deserializer = builder.build();
            } else {
                deserializer = builder.buildAbstract();
            }
            if (!this._factoryConfig.hasDeserializerModifiers()) {
                return deserializer;
            }
            for (BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, beanDesc, deserializer);
            }
            return deserializer;
        } catch (NoClassDefFoundError error) {
            return new NoClassDefFoundDeserializer(error);
        }
    }

    protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription builderDesc) throws JsonMappingException {
        ValueInstantiator valueInstantiator = findValueInstantiator(ctxt, builderDesc);
        DeserializationConfig config = ctxt.getConfig();
        BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, builderDesc);
        builder.setValueInstantiator(valueInstantiator);
        addBeanProps(ctxt, builderDesc, builder);
        addObjectIdReader(ctxt, builderDesc, builder);
        addReferenceProperties(ctxt, builderDesc, builder);
        addInjectables(ctxt, builderDesc, builder);
        Value builderConfig = builderDesc.findPOJOBuilderConfig();
        String buildMethodName = builderConfig == null ? "build" : builderConfig.buildMethodName;
        AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, null);
        if (buildMethod != null && config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        builder.setPOJOBuilder(buildMethod, builderConfig);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, builderDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, builderDesc, deserializer);
            }
        }
        return deserializer;
    }

    protected void addObjectIdReader(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo != null) {
            SettableBeanProperty idProp;
            JavaType idType;
            ObjectIdGenerator<?> gen;
            Class<?> implClass = objectIdInfo.getGeneratorType();
            ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
            if (implClass == PropertyGenerator.class) {
                PropertyName propName = objectIdInfo.getPropertyName();
                idProp = builder.findProperty(propName);
                if (idProp == null) {
                    throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": can not find property with name '" + propName + "'");
                }
                idType = idProp.getType();
                gen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
            } else {
                idType = ctxt.getTypeFactory().findTypeParameters(ctxt.constructType(implClass), ObjectIdGenerator.class)[0];
                idProp = null;
                gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
            }
            builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), gen, ctxt.findRootValueDeserializer(idType), idProp, resolver));
        }
    }

    public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
        builder.setValueInstantiator(findValueInstantiator(ctxt, beanDesc));
        addBeanProps(ctxt, beanDesc, builder);
        AnnotatedMember am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
        if (am != null) {
            SettableBeanProperty prop = constructSettableProperty(ctxt, beanDesc, SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, new PropertyName("cause")), am.getParameterType(0));
            if (prop != null) {
                builder.addOrReplaceProperty(prop, true);
            }
        }
        builder.addIgnorable("localizedMessage");
        builder.addIgnorable("suppressed");
        builder.addIgnorable("message");
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonDeserializer<?> build = builder.build();
        if (build instanceof BeanDeserializer) {
            build = new ThrowableDeserializer((BeanDeserializer) build);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                build = mod2.modifyDeserializer(config, beanDesc, build);
            }
        }
        return build;
    }

    protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext ctxt, BeanDescription beanDesc) {
        return new BeanDeserializerBuilder(beanDesc, ctxt.getConfig());
    }

    protected void addBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        Set<String> ignored;
        boolean useGettersAsSetters;
        List<BeanPropertyDefinition> propDefs;
        SettableBeanProperty prop;
        Class<?>[] views;
        SettableBeanProperty[] creatorProps = builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig());
        boolean isConcrete = !beanDesc.getType().isAbstract();
        JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        if (ignorals != null) {
            builder.setIgnoreUnknownProperties(ignorals.getIgnoreUnknown());
            ignored = ignorals.getIgnored();
            for (String propName : ignored) {
                builder.addIgnorable(propName);
            }
        } else {
            ignored = Collections.emptySet();
        }
        AnnotatedMethod anySetterMethod = beanDesc.findAnySetter();
        AnnotatedMember anySetterField = null;
        if (anySetterMethod != null) {
            builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetterMethod));
        } else {
            anySetterField = beanDesc.findAnySetterField();
            if (anySetterField != null) {
                builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetterField));
            }
        }
        if (anySetterMethod == null && anySetterField == null) {
            Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
            if (ignored2 != null) {
                for (String propName2 : ignored2) {
                    builder.addIgnorable(propName2);
                }
            }
        }
        if (ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS)) {
            if (ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
                useGettersAsSetters = true;
                propDefs = filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored);
                if (this._factoryConfig.hasDeserializerModifiers()) {
                    for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                        propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
                    }
                }
                for (BeanPropertyDefinition propDef : propDefs) {
                    prop = null;
                    if (propDef.hasSetter()) {
                        prop = constructSettableProperty(ctxt, beanDesc, propDef, propDef.getSetter().getParameterType(0));
                    } else if (propDef.hasField()) {
                        prop = constructSettableProperty(ctxt, beanDesc, propDef, propDef.getField().getType());
                    } else if (useGettersAsSetters && propDef.hasGetter()) {
                        Class<?> rawPropertyType = propDef.getGetter().getRawType();
                        if (Collection.class.isAssignableFrom(rawPropertyType) || Map.class.isAssignableFrom(rawPropertyType)) {
                            prop = constructSetterlessProperty(ctxt, beanDesc, propDef);
                        }
                    }
                    if (!isConcrete && propDef.hasConstructorParameter()) {
                        String name = propDef.getName();
                        CreatorProperty cprop = null;
                        if (creatorProps != null) {
                            SettableBeanProperty[] arr$ = creatorProps;
                            int len$ = creatorProps.length;
                            for (int i$ = 0; i$ < len$; i$++) {
                                SettableBeanProperty cp = arr$[i$];
                                if (name.equals(cp.getName()) && (cp instanceof CreatorProperty)) {
                                    cprop = (CreatorProperty) cp;
                                    break;
                                }
                            }
                        }
                        if (cprop == null) {
                            ctxt.reportMappingException("Could not find creator property with name '%s' (in class %s)", name, beanDesc.getBeanClass().getName());
                        } else {
                            if (prop != null) {
                                cprop.setFallbackSetter(prop);
                            }
                            builder.addCreatorProperty(cprop);
                        }
                    } else if (prop != null) {
                        views = propDef.findViews();
                        if (views == null) {
                            if (!ctxt.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
                                views = NO_VIEWS;
                            }
                        }
                        prop.setViews(views);
                        builder.addProperty(prop);
                    }
                }
            }
        }
        useGettersAsSetters = false;
        propDefs = filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            while (i$.hasNext()) {
                propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
            }
        }
        for (BeanPropertyDefinition propDef2 : propDefs) {
            prop = null;
            if (propDef2.hasSetter()) {
                prop = constructSettableProperty(ctxt, beanDesc, propDef2, propDef2.getSetter().getParameterType(0));
            } else if (propDef2.hasField()) {
                prop = constructSettableProperty(ctxt, beanDesc, propDef2, propDef2.getField().getType());
            } else {
                Class<?> rawPropertyType2 = propDef2.getGetter().getRawType();
                prop = constructSetterlessProperty(ctxt, beanDesc, propDef2);
            }
            if (!isConcrete) {
            }
            if (prop != null) {
                views = propDef2.findViews();
                if (views == null) {
                    if (ctxt.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
                        views = NO_VIEWS;
                    }
                }
                prop.setViews(views);
                builder.addProperty(prop);
            }
        }
    }

    protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored) throws JsonMappingException {
        ArrayList<BeanPropertyDefinition> result = new ArrayList(Math.max(4, propDefsIn.size()));
        HashMap<Class<?>, Boolean> ignoredTypes = new HashMap();
        for (BeanPropertyDefinition property : propDefsIn) {
            String name = property.getName();
            if (!ignored.contains(name)) {
                if (!property.hasConstructorParameter()) {
                    Class<?> rawPropertyType = null;
                    if (property.hasSetter()) {
                        rawPropertyType = property.getSetter().getRawParameterType(0);
                    } else if (property.hasField()) {
                        rawPropertyType = property.getField().getRawType();
                    }
                    if (rawPropertyType != null && isIgnorableType(ctxt.getConfig(), beanDesc, rawPropertyType, ignoredTypes)) {
                        builder.addIgnorable(name);
                    }
                }
                result.add(property);
            }
        }
        return result;
    }

    protected void addReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        Map<String, AnnotatedMember> refs = beanDesc.findBackReferenceProperties();
        if (refs != null) {
            for (Entry<String, AnnotatedMember> en : refs.entrySet()) {
                JavaType type;
                String name = (String) en.getKey();
                AnnotatedMember m = (AnnotatedMember) en.getValue();
                if (m instanceof AnnotatedMethod) {
                    type = ((AnnotatedMethod) m).getParameterType(0);
                } else {
                    type = m.getType();
                }
                builder.addBackReferenceProperty(name, constructSettableProperty(ctxt, beanDesc, SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), m), type));
            }
        }
    }

    protected void addInjectables(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
        if (raw != null) {
            for (Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
                AnnotatedMember m = (AnnotatedMember) entry.getValue();
                builder.addInjectable(PropertyName.construct(m.getName()), m.getType(), beanDesc.getClassAnnotations(), m, entry.getKey());
            }
        }
    }

    protected SettableAnyProperty constructAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedMember mutator) throws JsonMappingException {
        JavaType type = null;
        if (mutator instanceof AnnotatedMethod) {
            type = ((AnnotatedMethod) mutator).getParameterType(1);
        } else if (mutator instanceof AnnotatedField) {
            type = ((AnnotatedField) mutator).getType().getContentType();
        }
        type = resolveMemberAndTypeAnnotations(ctxt, mutator, type);
        Std prop = new Std(PropertyName.construct(mutator.getName()), type, null, beanDesc.getClassAnnotations(), mutator, PropertyMetadata.STD_OPTIONAL);
        JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, mutator);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        if (deser != null) {
            deser = ctxt.handlePrimaryContextualization(deser, prop, type);
        }
        return new SettableAnyProperty(prop, mutator, type, deser, (TypeDeserializer) type.getTypeHandler());
    }

    protected SettableBeanProperty constructSettableProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef, JavaType propType0) throws JsonMappingException {
        SettableBeanProperty prop;
        AnnotatedMember mutator = propDef.getNonConstructorMutator();
        if (mutator == null) {
            ctxt.reportBadPropertyDefinition(beanDesc, propDef, "No non-constructor mutator available", new Object[0]);
        }
        JavaType type = resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
        TypeDeserializer typeDeser = (TypeDeserializer) type.getTypeHandler();
        if (mutator instanceof AnnotatedMethod) {
            prop = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod) mutator);
        } else {
            prop = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField) mutator);
        }
        JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, mutator);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        if (deser != null) {
            prop = prop.withValueDeserializer(ctxt.handlePrimaryContextualization(deser, prop, type));
        }
        ReferenceProperty ref = propDef.findReferenceType();
        if (ref != null && ref.isManagedReference()) {
            prop.setManagedReferenceName(ref.getName());
        }
        ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
        if (objectIdInfo != null) {
            prop.setObjectIdInfo(objectIdInfo);
        }
        return prop;
    }

    protected SettableBeanProperty constructSetterlessProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef) throws JsonMappingException {
        AnnotatedMethod getter = propDef.getGetter();
        JavaType type = resolveMemberAndTypeAnnotations(ctxt, getter, getter.getType());
        SettableBeanProperty prop = new SetterlessProperty(propDef, type, (TypeDeserializer) type.getTypeHandler(), beanDesc.getClassAnnotations(), getter);
        JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, getter);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        if (deser != null) {
            return prop.withValueDeserializer(ctxt.handlePrimaryContextualization(deser, prop, type));
        }
        return prop;
    }

    protected boolean isPotentialBeanType(Class<?> type) {
        String typeStr = ClassUtil.canBeABeanType(type);
        if (typeStr != null) {
            throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        } else if (ClassUtil.isProxyType(type)) {
            throw new IllegalArgumentException("Can not deserialize Proxy class " + type.getName() + " as a Bean");
        } else {
            typeStr = ClassUtil.isLocalType(type, true);
            if (typeStr == null) {
                return true;
            }
            throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
    }

    protected boolean isIgnorableType(DeserializationConfig config, BeanDescription beanDesc, Class<?> type, Map<Class<?>, Boolean> ignoredTypes) {
        Boolean status = (Boolean) ignoredTypes.get(type);
        if (status != null) {
            return status.booleanValue();
        }
        ConfigOverride override = config.findConfigOverride(type);
        if (override != null) {
            status = override.getIsIgnoredType();
        }
        if (status == null) {
            status = config.getAnnotationIntrospector().isIgnorableType(config.introspectClassAnnotations((Class) type).getClassInfo());
            if (status == null) {
                status = Boolean.FALSE;
            }
        }
        ignoredTypes.put(type, status);
        return status.booleanValue();
    }
}
