package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty.Std;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import com.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import com.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import com.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import com.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import com.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.deser.std.MapEntryDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable {
    private static final Class<?> CLASS_CHAR_BUFFER = CharSequence.class;
    private static final Class<?> CLASS_ITERABLE = Iterable.class;
    private static final Class<?> CLASS_MAP_ENTRY = Entry.class;
    private static final Class<?> CLASS_OBJECT = Object.class;
    private static final Class<?> CLASS_STRING = String.class;
    protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
    static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
    static final HashMap<String, Class<? extends Map>> _mapFallbacks;
    protected final DeserializerFactoryConfig _factoryConfig;

    protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig deserializerFactoryConfig);

    static {
        HashMap hashMap = new HashMap();
        _mapFallbacks = hashMap;
        hashMap.put(Map.class.getName(), LinkedHashMap.class);
        _mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
        _mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
        _mapFallbacks.put(NavigableMap.class.getName(), TreeMap.class);
        _mapFallbacks.put(ConcurrentNavigableMap.class.getName(), ConcurrentSkipListMap.class);
        hashMap = new HashMap();
        _collectionFallbacks = hashMap;
        hashMap.put(Collection.class.getName(), ArrayList.class);
        _collectionFallbacks.put(List.class.getName(), ArrayList.class);
        _collectionFallbacks.put(Set.class.getName(), HashSet.class);
        _collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
        _collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
        _collectionFallbacks.put("java.util.Deque", LinkedList.class);
        _collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
    }

    protected BasicDeserializerFactory(DeserializerFactoryConfig config) {
        this._factoryConfig = config;
    }

    public DeserializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }

    public final DeserializerFactory withAdditionalDeserializers(Deserializers additional) {
        return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
    }

    public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional) {
        return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
    }

    public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier) {
        return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
    }

    public final DeserializerFactory withAbstractTypeResolver(AbstractTypeResolver resolver) {
        return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
    }

    public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators) {
        return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
    }

    public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
        JavaType next;
        while (true) {
            next = _mapAbstractType2(config, type);
            if (next == null) {
                return type;
            }
            Class<?> prevCls = type.getRawClass();
            Class<?> nextCls = next.getRawClass();
            if (prevCls != nextCls && prevCls.isAssignableFrom(nextCls)) {
                type = next;
            }
        }
        throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
    }

    private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
        Class<?> currClass = type.getRawClass();
        if (this._factoryConfig.hasAbstractTypeResolvers()) {
            for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
                JavaType concrete = resolver.findTypeMapping(config, type);
                if (concrete != null && concrete.getRawClass() != currClass) {
                    return concrete;
                }
            }
        }
        return null;
    }

    public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        ValueInstantiator instantiator = null;
        AnnotatedClass ac = beanDesc.getClassInfo();
        Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
        if (instDef != null) {
            instantiator = _valueInstantiatorInstance(config, ac, instDef);
        }
        if (instantiator == null) {
            instantiator = _findStdValueInstantiator(config, beanDesc);
            if (instantiator == null) {
                instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
            }
        }
        if (this._factoryConfig.hasValueInstantiators()) {
            for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
                instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
                if (instantiator == null) {
                    ctxt.reportMappingException("Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", insts.getClass().getName());
                }
            }
        }
        if (instantiator.getIncompleteParameter() == null) {
            return instantiator;
        }
        AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
        throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + nonAnnotatedParam.getOwner() + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
    }

    private ValueInstantiator _findStdValueInstantiator(DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        if (beanDesc.getBeanClass() == JsonLocation.class) {
            return new JsonLocationInstantiator();
        }
        return null;
    }

    protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        DeserializationConfig config = ctxt.getConfig();
        VisibilityChecker<?> vchecker = intr.findAutoDetectVisibility(beanDesc.getClassInfo(), config.getDefaultVisibilityChecker());
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
        _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
        if (beanDesc.getType().isConcrete()) {
            _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
        }
        return creators.constructValueInstantiator(config);
    }

    protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc) throws JsonMappingException {
        Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = Collections.emptyMap();
        for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
            Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
            while (it.hasNext()) {
                AnnotatedParameter param = (AnnotatedParameter) it.next();
                AnnotatedWithParams owner = param.getOwner();
                BeanPropertyDefinition[] defs = (BeanPropertyDefinition[]) result.get(owner);
                int index = param.getIndex();
                if (defs == null) {
                    if (result.isEmpty()) {
                        result = new LinkedHashMap();
                    }
                    defs = new BeanPropertyDefinition[owner.getParameterCount()];
                    result.put(owner, defs);
                } else if (defs[index] != null) {
                    throw new IllegalStateException("Conflict: parameter #" + index + " of " + owner + " bound to more than one property; " + defs[index] + " vs " + propDef);
                }
                defs[index] = propDef;
            }
        }
        return result;
    }

    public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef) throws JsonMappingException {
        if (instDef == null) {
            return null;
        }
        if (instDef instanceof ValueInstantiator) {
            return (ValueInstantiator) instDef;
        }
        if (instDef instanceof Class) {
            Class<?> instClass = (Class) instDef;
            if (ClassUtil.isBogusClass(instClass)) {
                return null;
            }
            if (ValueInstantiator.class.isAssignableFrom(instClass)) {
                HandlerInstantiator hi = config.getHandlerInstantiator();
                if (hi != null) {
                    ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
                    if (inst != null) {
                        return inst;
                    }
                }
                return (ValueInstantiator) ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
            }
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
        }
        throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
    }

    protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
        Annotated defaultCtor = beanDesc.findDefaultConstructor();
        if (defaultCtor != null && (!creators.hasDefaultCreator() || intr.hasCreatorAnnotation(defaultCtor))) {
            creators.setDefaultCreator(defaultCtor);
        }
        List<AnnotatedConstructor> implicitCtors = null;
        for (AnnotatedMember ctor : beanDesc.getConstructors()) {
            boolean isCreator = intr.hasCreatorAnnotation(ctor);
            BeanPropertyDefinition[] propDefs = (BeanPropertyDefinition[]) creatorParams.get(ctor);
            int argCount = ctor.getParameterCount();
            SettableBeanProperty[] properties;
            PropertyName name;
            if (argCount == 1) {
                BeanPropertyDefinition argDef = propDefs == null ? null : propDefs[0];
                if (_checkIfCreatorPropertyBased(intr, ctor, argDef)) {
                    properties = new SettableBeanProperty[1];
                    name = argDef == null ? null : argDef.getFullName();
                    AnnotatedParameter arg = ctor.getParameter(0);
                    properties[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, arg, intr.findInjectableValueId(arg));
                    creators.addPropertyCreator(ctor, isCreator, properties);
                } else {
                    _handleSingleArgumentConstructor(ctxt, beanDesc, vchecker, intr, creators, ctor, isCreator, vchecker.isCreatorVisible(ctor));
                    if (argDef != null) {
                        ((POJOPropertyBuilder) argDef).removeConstructors();
                    }
                }
            } else {
                AnnotatedParameter nonAnnotatedParam = null;
                properties = new SettableBeanProperty[argCount];
                int explicitNameCount = 0;
                int implicitWithCreatorCount = 0;
                int injectCount = 0;
                int i = 0;
                while (i < argCount) {
                    AnnotatedMember param = ctor.getParameter(i);
                    BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
                    Object injectId = intr.findInjectableValueId(param);
                    name = propDef == null ? null : propDef.getFullName();
                    if (propDef != null && propDef.isExplicitlyNamed()) {
                        explicitNameCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else if (injectId != null) {
                        injectCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else if (intr.findUnwrappingNameTransformer(param) != null) {
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
                        explicitNameCount++;
                    } else if (isCreator && name != null && !name.isEmpty()) {
                        implicitWithCreatorCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else if (nonAnnotatedParam == null) {
                        AnnotatedMember nonAnnotatedParam2 = param;
                    }
                    i++;
                }
                int namedCount = explicitNameCount + implicitWithCreatorCount;
                if (isCreator || explicitNameCount > 0 || injectCount > 0) {
                    if (namedCount + injectCount == argCount) {
                        creators.addPropertyCreator(ctor, isCreator, properties);
                    } else if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                        creators.addDelegatingCreator(ctor, isCreator, properties);
                    } else {
                        PropertyName impl = _findImplicitParamName(nonAnnotatedParam, intr);
                        if (impl == null || impl.isEmpty()) {
                            int ix = nonAnnotatedParam.getIndex();
                            if (ix == 0 && ClassUtil.isNonStaticInnerClass(ctor.getDeclaringClass())) {
                                throw new IllegalArgumentException("Non-static inner classes like " + ctor.getDeclaringClass().getName() + " can not use @JsonCreator for constructors");
                            }
                            throw new IllegalArgumentException("Argument #" + ix + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
                        }
                    }
                }
                if (!creators.hasDefaultCreator()) {
                    if (implicitCtors == null) {
                        implicitCtors = new LinkedList();
                    }
                    implicitCtors.add(ctor);
                }
            }
        }
        if (implicitCtors != null && !creators.hasDelegatingCreator() && !creators.hasPropertyBasedCreator()) {
            _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
        }
    }

    protected void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedConstructor> implicitCtors) throws JsonMappingException {
        Iterator i$;
        AnnotatedConstructor found = null;
        SettableBeanProperty[] foundProps = null;
        for (AnnotatedMember ctor : implicitCtors) {
            if (vchecker.isCreatorVisible(ctor)) {
                int argCount = ctor.getParameterCount();
                SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                for (int i = 0; i < argCount; i++) {
                    AnnotatedParameter param = ctor.getParameter(i);
                    PropertyName name = _findParamName(param, intr);
                    if (name == null || name.isEmpty()) {
                        break;
                    }
                    properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
                }
                if (found != null) {
                    found = null;
                    break;
                } else {
                    found = ctor;
                    foundProps = properties;
                }
            }
        }
        if (found != null) {
            creators.addPropertyCreator(found, false, foundProps);
            BasicBeanDescription bbd = (BasicBeanDescription) beanDesc;
            SettableBeanProperty[] arr$ = foundProps;
            int len$ = foundProps.length;
            for (i$ = null; i$ < len$; i$++) {
                SettableBeanProperty prop = arr$[i$];
                PropertyName pn = prop.getFullName();
                if (!bbd.hasProperty(pn)) {
                    bbd.addProperty(SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn));
                }
            }
        }
    }

    protected boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef) {
        Mode mode = intr.findCreatorBinding(creator);
        if (mode == Mode.PROPERTIES) {
            return true;
        }
        if (mode == Mode.DELEGATING) {
            return false;
        }
        if ((propDef != null && propDef.isExplicitlyNamed()) || intr.findInjectableValueId(creator.getParameter(0)) != null) {
            return true;
        }
        if (propDef != null) {
            String implName = propDef.getName();
            if (!(implName == null || implName.isEmpty() || !propDef.couldSerialize())) {
                return true;
            }
        }
        return false;
    }

    protected boolean _handleSingleArgumentConstructor(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> visibilityChecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedConstructor ctor, boolean isCreator, boolean isVisible) throws JsonMappingException {
        Class<?> type = ctor.getRawParameterType(0);
        if (type == String.class || type == CharSequence.class) {
            if (!isCreator && !isVisible) {
                return true;
            }
            creators.addStringCreator(ctor, isCreator);
            return true;
        } else if (type == Integer.TYPE || type == Integer.class) {
            if (!isCreator && !isVisible) {
                return true;
            }
            creators.addIntCreator(ctor, isCreator);
            return true;
        } else if (type == Long.TYPE || type == Long.class) {
            if (!isCreator && !isVisible) {
                return true;
            }
            creators.addLongCreator(ctor, isCreator);
            return true;
        } else if (type == Double.TYPE || type == Double.class) {
            if (!isCreator && !isVisible) {
                return true;
            }
            creators.addDoubleCreator(ctor, isCreator);
            return true;
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            if (!isCreator && !isVisible) {
                return true;
            }
            creators.addBooleanCreator(ctor, isCreator);
            return true;
        } else if (!isCreator) {
            return false;
        } else {
            creators.addDelegatingCreator(ctor, isCreator, null);
            return true;
        }
    }

    protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            boolean isCreator = intr.hasCreatorAnnotation(factory);
            int argCount = factory.getParameterCount();
            if (argCount != 0) {
                BeanPropertyDefinition[] propDefs = (BeanPropertyDefinition[]) creatorParams.get(factory);
                if (argCount == 1) {
                    BeanPropertyDefinition argDef;
                    if (propDefs == null) {
                        argDef = null;
                    } else {
                        argDef = propDefs[0];
                    }
                    if (!_checkIfCreatorPropertyBased(intr, factory, argDef)) {
                        _handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
                    }
                } else if (!isCreator) {
                    continue;
                }
                AnnotatedParameter nonAnnotatedParam = null;
                SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
                int implicitNameCount = 0;
                int explicitNameCount = 0;
                int injectCount = 0;
                int i = 0;
                while (i < argCount) {
                    AnnotatedMember param = factory.getParameter(i);
                    BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
                    Object injectId = intr.findInjectableValueId(param);
                    PropertyName name = propDef == null ? null : propDef.getFullName();
                    if (propDef != null && propDef.isExplicitlyNamed()) {
                        explicitNameCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else if (injectId != null) {
                        injectCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else if (intr.findUnwrappingNameTransformer(param) != null) {
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
                        implicitNameCount++;
                    } else if (isCreator && name != null && !name.isEmpty()) {
                        implicitNameCount++;
                        properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
                    } else if (nonAnnotatedParam == null) {
                        AnnotatedMember nonAnnotatedParam2 = param;
                    }
                    i++;
                }
                int namedCount = explicitNameCount + implicitNameCount;
                if (isCreator || explicitNameCount > 0 || injectCount > 0) {
                    if (namedCount + injectCount == argCount) {
                        creators.addPropertyCreator(factory, isCreator, properties);
                    } else if (explicitNameCount == 0 && injectCount + 1 == argCount) {
                        creators.addDelegatingCreator(factory, isCreator, properties);
                    } else {
                        throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of factory method " + factory + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
                    }
                }
            } else if (isCreator) {
                creators.setDefaultCreator(factory);
            }
        }
    }

    protected boolean _handleSingleArgumentFactory(DeserializationConfig config, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedMethod factory, boolean isCreator) throws JsonMappingException {
        Class<?> type = factory.getRawParameterType(0);
        if (type == String.class || type == CharSequence.class) {
            if (!isCreator && !vchecker.isCreatorVisible((AnnotatedMember) factory)) {
                return true;
            }
            creators.addStringCreator(factory, isCreator);
            return true;
        } else if (type == Integer.TYPE || type == Integer.class) {
            if (!isCreator && !vchecker.isCreatorVisible((AnnotatedMember) factory)) {
                return true;
            }
            creators.addIntCreator(factory, isCreator);
            return true;
        } else if (type == Long.TYPE || type == Long.class) {
            if (!isCreator && !vchecker.isCreatorVisible((AnnotatedMember) factory)) {
                return true;
            }
            creators.addLongCreator(factory, isCreator);
            return true;
        } else if (type == Double.TYPE || type == Double.class) {
            if (!isCreator && !vchecker.isCreatorVisible((AnnotatedMember) factory)) {
                return true;
            }
            creators.addDoubleCreator(factory, isCreator);
            return true;
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            if (!isCreator && !vchecker.isCreatorVisible((AnnotatedMember) factory)) {
                return true;
            }
            creators.addBooleanCreator(factory, isCreator);
            return true;
        } else if (!isCreator) {
            return false;
        } else {
            creators.addDelegatingCreator(factory, isCreator, null);
            return true;
        }
    }

    protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, Object injectableValueId) throws JsonMappingException {
        PropertyMetadata metadata;
        DeserializationConfig config = ctxt.getConfig();
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            metadata = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        } else {
            Boolean b = intr.hasRequiredMarker(param);
            boolean req = b != null && b.booleanValue();
            metadata = PropertyMetadata.construct(req, intr.findPropertyDescription(param), intr.findPropertyIndex(param), intr.findPropertyDefaultValue(param));
        }
        JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
        Std property = new Std(name, type, intr.findWrapperName(param), beanDesc.getClassAnnotations(), param, metadata);
        TypeDeserializer typeDeser = (TypeDeserializer) type.getTypeHandler();
        if (typeDeser == null) {
            typeDeser = findTypeDeserializer(config, type);
        }
        SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
        JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
        if (deser == null) {
            deser = (JsonDeserializer) type.getValueHandler();
        }
        if (deser != null) {
            return prop.withValueDeserializer(ctxt.handlePrimaryContextualization(deser, prop, type));
        }
        return prop;
    }

    protected PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
        if (!(param == null || intr == null)) {
            PropertyName name = intr.findNameForDeserialization(param);
            if (name != null) {
                return name;
            }
            String str = intr.findImplicitPropertyName(param);
            if (!(str == null || str.isEmpty())) {
                return PropertyName.construct(str);
            }
        }
        return null;
    }

    protected PropertyName _findImplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
        String str = intr.findImplicitPropertyName(param);
        if (str == null || str.isEmpty()) {
            return null;
        }
        return PropertyName.construct(str);
    }

    @Deprecated
    protected PropertyName _findExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
        if (param == null || intr == null) {
            return null;
        }
        return intr.findNameForDeserialization(param);
    }

    @Deprecated
    protected boolean _hasExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr) {
        if (param == null || intr == null) {
            return false;
        }
        PropertyName n = intr.findNameForDeserialization(param);
        if (n == null || !n.hasSimpleName()) {
            return false;
        }
        return true;
    }

    public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        JavaType elemType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) elemType.getValueHandler();
        TypeDeserializer elemTypeDeser = (TypeDeserializer) elemType.getTypeHandler();
        if (elemTypeDeser == null) {
            elemTypeDeser = findTypeDeserializer(config, elemType);
        }
        JsonDeserializer<?> _findCustomArrayDeserializer = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
        if (_findCustomArrayDeserializer == null) {
            if (contentDeser == null) {
                Class<?> raw = elemType.getRawClass();
                if (elemType.isPrimitive()) {
                    return PrimitiveArrayDeserializers.forType(raw);
                }
                if (raw == String.class) {
                    return StringArrayDeserializer.instance;
                }
            }
            _findCustomArrayDeserializer = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                _findCustomArrayDeserializer = mod.modifyArrayDeserializer(config, type, beanDesc, _findCustomArrayDeserializer);
            }
        }
        return _findCustomArrayDeserializer;
    }

    public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> _findCustomCollectionDeserializer = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (_findCustomCollectionDeserializer == null) {
            Class<?> collectionClass = type.getRawClass();
            if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
                _findCustomCollectionDeserializer = new EnumSetDeserializer(contentType, null);
            }
        }
        if (_findCustomCollectionDeserializer == null) {
            JavaType type2;
            if (type.isInterface() || type.isAbstract()) {
                JavaType implType = _mapAbstractCollectionType(type, config);
                if (implType != null) {
                    type2 = implType;
                    beanDesc = config.introspectForCreation(type2);
                } else if (type.getTypeHandler() == null) {
                    throw new IllegalArgumentException("Can not find a deserializer for non-concrete Collection type " + type);
                } else {
                    _findCustomCollectionDeserializer = AbstractDeserializer.constructForNonPOJO(beanDesc);
                }
            }
            if (_findCustomCollectionDeserializer == null) {
                ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
                if (!inst.canCreateUsingDefault() && type2.getRawClass() == ArrayBlockingQueue.class) {
                    return new ArrayBlockingQueueDeserializer(type2, contentDeser, contentTypeDeser, inst);
                }
                if (contentType.getRawClass() == String.class) {
                    _findCustomCollectionDeserializer = new StringCollectionDeserializer(type2, contentDeser, inst);
                } else {
                    _findCustomCollectionDeserializer = new CollectionDeserializer(type2, contentDeser, contentTypeDeser, inst);
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                _findCustomCollectionDeserializer = mod.modifyCollectionDeserializer(config, type, beanDesc, _findCustomCollectionDeserializer);
            }
        }
        return _findCustomCollectionDeserializer;
    }

    protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config) {
        Class<?> collectionClass = (Class) _collectionFallbacks.get(type.getRawClass().getName());
        if (collectionClass == null) {
            return null;
        }
        return (CollectionType) config.constructSpecializedType(type, collectionClass);
    }

    public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer) keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> mapClass = type.getRawClass();
            if (EnumMap.class.isAssignableFrom(mapClass)) {
                Class<?> kt = keyType.getRawClass();
                if (kt == null || !kt.isEnum()) {
                    throw new IllegalArgumentException("Can not construct EnumMap; generic (key) type not available");
                }
                JsonDeserializer<?> enumMapDeserializer = new EnumMapDeserializer(type, null, contentDeser, contentTypeDeser);
            }
            if (deser == null) {
                if (type.isInterface() || type.isAbstract()) {
                    Class<? extends Map> fallback = (Class) _mapFallbacks.get(mapClass.getName());
                    if (fallback != null) {
                        type = (MapType) config.constructSpecializedType(type, fallback);
                        beanDesc = config.introspectForCreation(type);
                    } else if (type.getTypeHandler() == null) {
                        throw new IllegalArgumentException("Can not find a deserializer for non-concrete Map type " + type);
                    } else {
                        deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                    }
                }
                if (deser == null) {
                    JsonDeserializer<?> md = new MapDeserializer((JavaType) type, findValueInstantiator(ctxt, beanDesc), keyDes, (JsonDeserializer) contentDeser, contentTypeDeser);
                    Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
                    md.setIgnorableProperties((Set) ignorals == null ? null : ignorals.findIgnoredForDeserialization());
                    deser = md;
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        DeserializationConfig config = ctxt.getConfig();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer) keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        Class<?> enumClass = type.getRawClass();
        JsonDeserializer<?> _findCustomEnumDeserializer = _findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (_findCustomEnumDeserializer == null) {
            ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
            SettableBeanProperty[] creatorProps = valueInstantiator == null ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
                if (ctxt.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
                    if (factory.getParameterCount() == 0) {
                        _findCustomEnumDeserializer = EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
                        break;
                    } else if (factory.getRawReturnType().isAssignableFrom(enumClass)) {
                        _findCustomEnumDeserializer = EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
                        break;
                    }
                }
            }
            if (_findCustomEnumDeserializer == null) {
                _findCustomEnumDeserializer = new EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod()));
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                _findCustomEnumDeserializer = mod.modifyEnumDeserializer(config, type, beanDesc, _findCustomEnumDeserializer);
            }
        }
        return _findCustomEnumDeserializer;
    }

    public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc) throws JsonMappingException {
        Class<? extends JsonNode> nodeClass = nodeType.getRawClass();
        JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
        return custom != null ? custom : JsonNodeDeserializer.getDeserializer(nodeClass);
    }

    public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc) throws JsonMappingException {
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer) contentType.getValueHandler();
        DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = (TypeDeserializer) contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null && AtomicReference.class.isAssignableFrom(type.getRawClass())) {
            return new AtomicReferenceDeserializer(type, contentTypeDeser, contentDeser);
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }

    public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType) throws JsonMappingException {
        AnnotatedClass ac = config.introspectClassAnnotations(baseType.getRawClass()).getClassInfo();
        TypeResolverBuilder<?> b = config.getAnnotationIntrospector().findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
        if (b.getDefaultImpl() == null && baseType.isAbstract()) {
            JavaType defaultType = mapAbstractType(config, baseType);
            if (!(defaultType == null || defaultType.getRawClass() == baseType.getRawClass())) {
                b = b.defaultImpl(defaultType.getRawClass());
            }
        }
        return b.buildTypeDeserializer(config, baseType, subtypes);
    }

    protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
    }

    public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        KeyDeserializer deser = null;
        if (this._factoryConfig.hasKeyDeserializers()) {
            BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
            for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
                deser = d.findKeyDeserializer(type, config, beanDesc);
                if (deser != null) {
                    break;
                }
            }
        }
        if (deser == null) {
            if (type.isEnumType()) {
                return _createEnumKeyDeserializer(ctxt, type);
            }
            deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyKeyDeserializer(config, type, deser);
            }
        }
        return deser;
    }

    private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        Class<?> enumClass = type.getRawClass();
        BeanDescription beanDesc = config.introspect(type);
        KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (des != null) {
            return des;
        }
        JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
        }
        JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (valueDesForKey != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
        }
        EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod());
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            if (ai.hasCreatorAnnotation(factory)) {
                if (factory.getParameterCount() != 1 || !factory.getRawReturnType().isAssignableFrom(enumClass)) {
                    throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
                } else if (factory.getRawParameterType(0) != String.class) {
                    throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
                } else {
                    if (config.canOverrideAccessModifiers()) {
                        ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
                    }
                    return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
                }
            }
        }
        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
    }

    public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated) throws JsonMappingException {
        TypeResolverBuilder<?> b = config.getAnnotationIntrospector().findPropertyTypeResolver(config, annotated, baseType);
        if (b == null) {
            return findTypeDeserializer(config, baseType);
        }
        return b.buildTypeDeserializer(config, baseType, config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType));
    }

    public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity) throws JsonMappingException {
        TypeResolverBuilder<?> b = config.getAnnotationIntrospector().findPropertyContentTypeResolver(config, propertyEntity, containerType);
        JavaType contentType = containerType.getContentType();
        if (b == null) {
            return findTypeDeserializer(config, contentType);
        }
        return b.buildTypeDeserializer(config, contentType, config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType));
    }

    public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        Class<?> rawType = type.getRawClass();
        if (rawType == CLASS_OBJECT) {
            JavaType lt;
            JavaType mt;
            DeserializationConfig config = ctxt.getConfig();
            if (this._factoryConfig.hasAbstractTypeResolvers()) {
                lt = _findRemappedType(config, List.class);
                mt = _findRemappedType(config, Map.class);
            } else {
                mt = null;
                lt = null;
            }
            return new UntypedObjectDeserializer(lt, mt);
        } else if (rawType == CLASS_STRING || rawType == CLASS_CHAR_BUFFER) {
            return StringDeserializer.instance;
        } else {
            if (rawType == CLASS_ITERABLE) {
                TypeFactory tf = ctxt.getTypeFactory();
                JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
                JavaType elemType = (tps == null || tps.length != 1) ? TypeFactory.unknownType() : tps[0];
                return createCollectionDeserializer(ctxt, tf.constructCollectionType(Collection.class, elemType), beanDesc);
            } else if (rawType == CLASS_MAP_ENTRY) {
                JavaType kt = type.containedType(0);
                if (kt == null) {
                    kt = TypeFactory.unknownType();
                }
                JavaType vt = type.containedType(1);
                if (vt == null) {
                    vt = TypeFactory.unknownType();
                }
                TypeDeserializer vts = (TypeDeserializer) vt.getTypeHandler();
                if (vts == null) {
                    vts = findTypeDeserializer(ctxt.getConfig(), vt);
                }
                return new MapEntryDeserializer(type, (KeyDeserializer) kt.getValueHandler(), (JsonDeserializer) vt.getValueHandler(), vts);
            } else {
                JsonDeserializer<?> deser;
                String clsName = rawType.getName();
                if (rawType.isPrimitive() || clsName.startsWith("java.")) {
                    deser = NumberDeserializers.find(rawType, clsName);
                    if (deser == null) {
                        deser = DateDeserializers.find(rawType, clsName);
                    }
                    if (deser != null) {
                        return deser;
                    }
                }
                if (rawType == TokenBuffer.class) {
                    return new TokenBufferDeserializer();
                }
                deser = findOptionalStdDeserializer(ctxt, type, beanDesc);
                return deser == null ? JdkDeserializers.find(rawType, clsName) : deser;
            }
        }
    }

    protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
        JavaType type = mapAbstractType(config, config.constructType((Class) rawType));
        return (type == null || type.hasRawClass(rawType)) ? null : type;
    }

    protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<Object> deser = d.findBeanDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }

    protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            Object deserDef = intr.findDeserializer(ann);
            if (deserDef != null) {
                return ctxt.deserializerInstance(ann, deserDef);
            }
        }
        return null;
    }

    protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            Object deserDef = intr.findKeyDeserializer(ann);
            if (deserDef != null) {
                return ctxt.keyDeserializerInstance(ann, deserDef);
            }
        }
        return null;
    }

    protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType() && type.getKeyType() != null) {
            KeyDeserializer kd = ctxt.keyDeserializerInstance(member, intr.findKeyDeserializer(member));
            if (kd != null) {
                type = ((MapLikeType) type).withKeyValueHandler(kd);
                type.getKeyType();
            }
        }
        if (type.hasContentType()) {
            JsonDeserializer<?> cd = ctxt.deserializerInstance(member, intr.findContentDeserializer(member));
            if (cd != null) {
                type = type.withContentValueHandler(cd);
            }
            TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
            if (contentTypeDeser != null) {
                type = type.withContentTypeHandler(contentTypeDeser);
            }
        }
        TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
        if (valueTypeDeser != null) {
            type = type.withTypeHandler(valueTypeDeser);
        }
        return intr.refineDeserializationType(ctxt.getConfig(), member, type);
    }

    protected EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMethod jsonValueMethod) {
        if (jsonValueMethod == null) {
            return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
        }
        Method accessor = jsonValueMethod.getAnnotated();
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(accessor, config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }
        return EnumResolver.constructUnsafeUsingMethod(enumClass, accessor, config.getAnnotationIntrospector());
    }

    @Deprecated
    protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        return intr == null ? type : intr.refineDeserializationType(ctxt.getConfig(), a, type);
    }

    @Deprecated
    protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member) throws JsonMappingException {
        return resolveMemberAndTypeAnnotations(ctxt, member, type);
    }

    @Deprecated
    protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType) {
        if (enumType == null) {
            return null;
        }
        return config.introspect(enumType).findJsonValueMethod();
    }
}
