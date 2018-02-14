package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.LinkedNode;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.TimeZone;

public final class DeserializationConfig extends MapperConfigBase<DeserializationFeature, DeserializationConfig> implements Serializable {
    private static final long serialVersionUID = 1;
    protected final int _deserFeatures;
    protected final int _formatReadFeatures;
    protected final int _formatReadFeaturesToChange;
    protected final JsonNodeFactory _nodeFactory;
    protected final int _parserFeatures;
    protected final int _parserFeaturesToChange;
    protected final LinkedNode<DeserializationProblemHandler> _problemHandlers;

    public DeserializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super(base, str, mixins, rootNames, configOverrides);
        this._deserFeatures = MapperConfig.collectFeatureDefaults(DeserializationFeature.class);
        this._nodeFactory = JsonNodeFactory.instance;
        this._problemHandlers = null;
        this._parserFeatures = 0;
        this._parserFeaturesToChange = 0;
        this._formatReadFeatures = 0;
        this._formatReadFeaturesToChange = 0;
    }

    @Deprecated
    public DeserializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames) {
        this(base, str, mixins, rootNames, null);
    }

    private DeserializationConfig(DeserializationConfig src, int mapperFeatures, int deserFeatures, int parserFeatures, int parserFeatureMask, int formatFeatures, int formatFeatureMask) {
        super((MapperConfigBase) src, mapperFeatures);
        this._deserFeatures = deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
        this._parserFeatures = parserFeatures;
        this._parserFeaturesToChange = parserFeatureMask;
        this._formatReadFeatures = formatFeatures;
        this._formatReadFeaturesToChange = formatFeatureMask;
    }

    private DeserializationConfig(DeserializationConfig src, SubtypeResolver str) {
        super((MapperConfigBase) src, str);
        this._deserFeatures = src._deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    private DeserializationConfig(DeserializationConfig src, BaseSettings base) {
        super((MapperConfigBase) src, base);
        this._deserFeatures = src._deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    private DeserializationConfig(DeserializationConfig src, JsonNodeFactory f) {
        super(src);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = f;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    private DeserializationConfig(DeserializationConfig src, LinkedNode<DeserializationProblemHandler> problemHandlers) {
        super(src);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    private DeserializationConfig(DeserializationConfig src, PropertyName rootName) {
        super((MapperConfigBase) src, rootName);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    private DeserializationConfig(DeserializationConfig src, Class<?> view) {
        super((MapperConfigBase) src, (Class) view);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    protected DeserializationConfig(DeserializationConfig src, ContextAttributes attrs) {
        super((MapperConfigBase) src, attrs);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins) {
        super((MapperConfigBase) src, mixins);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super((MapperConfigBase) src, mixins, rootNames, configOverrides);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._parserFeatures = src._parserFeatures;
        this._parserFeaturesToChange = src._parserFeaturesToChange;
        this._formatReadFeatures = src._formatReadFeatures;
        this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
    }

    protected final BaseSettings getBaseSettings() {
        return this._base;
    }

    public final DeserializationConfig with(MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        MapperFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newMapperFlags |= arr$[i$].getMask();
        }
        return newMapperFlags == this._mapperFeatures ? this : new DeserializationConfig(this, newMapperFlags, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig without(MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        MapperFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newMapperFlags &= arr$[i$].getMask() ^ -1;
        }
        return newMapperFlags == this._mapperFeatures ? this : new DeserializationConfig(this, newMapperFlags, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig with(MapperFeature feature, boolean state) {
        int newMapperFlags;
        if (state) {
            newMapperFlags = this._mapperFeatures | feature.getMask();
        } else {
            newMapperFlags = this._mapperFeatures & (feature.getMask() ^ -1);
        }
        return newMapperFlags == this._mapperFeatures ? this : new DeserializationConfig(this, newMapperFlags, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig with(ClassIntrospector ci) {
        return _withBase(this._base.withClassIntrospector(ci));
    }

    public final DeserializationConfig with(AnnotationIntrospector ai) {
        return _withBase(this._base.withAnnotationIntrospector(ai));
    }

    public final DeserializationConfig with(VisibilityChecker<?> vc) {
        return _withBase(this._base.withVisibilityChecker(vc));
    }

    public final DeserializationConfig withVisibility(PropertyAccessor forMethod, Visibility visibility) {
        return _withBase(this._base.withVisibility(forMethod, visibility));
    }

    public final DeserializationConfig with(TypeResolverBuilder<?> trb) {
        return _withBase(this._base.withTypeResolverBuilder(trb));
    }

    public final DeserializationConfig with(SubtypeResolver str) {
        return this._subtypeResolver == str ? this : new DeserializationConfig(this, str);
    }

    public final DeserializationConfig with(PropertyNamingStrategy pns) {
        return _withBase(this._base.withPropertyNamingStrategy(pns));
    }

    public final DeserializationConfig withRootName(PropertyName rootName) {
        if (rootName == null) {
            if (this._rootName == null) {
                return this;
            }
        } else if (rootName.equals(this._rootName)) {
            return this;
        }
        return new DeserializationConfig(this, rootName);
    }

    public final DeserializationConfig with(TypeFactory tf) {
        return _withBase(this._base.withTypeFactory(tf));
    }

    public final DeserializationConfig with(DateFormat df) {
        return _withBase(this._base.withDateFormat(df));
    }

    public final DeserializationConfig with(HandlerInstantiator hi) {
        return _withBase(this._base.withHandlerInstantiator(hi));
    }

    public final DeserializationConfig withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
        return _withBase(this._base.withInsertedAnnotationIntrospector(ai));
    }

    public final DeserializationConfig withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
        return _withBase(this._base.withAppendedAnnotationIntrospector(ai));
    }

    public final DeserializationConfig withView(Class<?> view) {
        return this._view == view ? this : new DeserializationConfig(this, (Class) view);
    }

    public final DeserializationConfig with(Locale l) {
        return _withBase(this._base.with(l));
    }

    public final DeserializationConfig with(TimeZone tz) {
        return _withBase(this._base.with(tz));
    }

    public final DeserializationConfig with(Base64Variant base64) {
        return _withBase(this._base.with(base64));
    }

    public final DeserializationConfig with(ContextAttributes attrs) {
        return attrs == this._attributes ? this : new DeserializationConfig(this, attrs);
    }

    private final DeserializationConfig _withBase(BaseSettings newBase) {
        return this._base == newBase ? this : new DeserializationConfig(this, newBase);
    }

    public final DeserializationConfig with(DeserializationFeature feature) {
        int newDeserFeatures = this._deserFeatures | feature.getMask();
        return newDeserFeatures == this._deserFeatures ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig with(DeserializationFeature first, DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures | first.getMask();
        DeserializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newDeserFeatures |= arr$[i$].getMask();
        }
        return newDeserFeatures == this._deserFeatures ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig withFeatures(DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures;
        DeserializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newDeserFeatures |= arr$[i$].getMask();
        }
        return newDeserFeatures == this._deserFeatures ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig without(DeserializationFeature feature) {
        int newDeserFeatures = this._deserFeatures & (feature.getMask() ^ -1);
        return newDeserFeatures == this._deserFeatures ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig without(DeserializationFeature first, DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures & (first.getMask() ^ -1);
        DeserializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newDeserFeatures &= arr$[i$].getMask() ^ -1;
        }
        return newDeserFeatures == this._deserFeatures ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig withoutFeatures(DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures;
        DeserializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newDeserFeatures &= arr$[i$].getMask() ^ -1;
        }
        return newDeserFeatures == this._deserFeatures ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig with(Feature feature) {
        int newSet = this._parserFeatures | feature.getMask();
        int newMask = this._parserFeaturesToChange | feature.getMask();
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig withFeatures(Feature... features) {
        int newSet = this._parserFeatures;
        int newMask = this._parserFeaturesToChange;
        Feature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig without(Feature feature) {
        int newSet = this._parserFeatures & (feature.getMask() ^ -1);
        int newMask = this._parserFeaturesToChange | feature.getMask();
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig withoutFeatures(Feature... features) {
        int newSet = this._parserFeatures;
        int newMask = this._parserFeaturesToChange;
        Feature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet &= mask ^ -1;
            newMask |= mask;
        }
        return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
    }

    public final DeserializationConfig with(FormatFeature feature) {
        int newSet = this._formatReadFeatures | feature.getMask();
        int newMask = this._formatReadFeaturesToChange | feature.getMask();
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }

    public final DeserializationConfig withFeatures(FormatFeature... features) {
        int newSet = this._formatReadFeatures;
        int newMask = this._formatReadFeaturesToChange;
        FormatFeature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }

    public final DeserializationConfig without(FormatFeature feature) {
        int newSet = this._formatReadFeatures & (feature.getMask() ^ -1);
        int newMask = this._formatReadFeaturesToChange | feature.getMask();
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }

    public final DeserializationConfig withoutFeatures(FormatFeature... features) {
        int newSet = this._formatReadFeatures;
        int newMask = this._formatReadFeaturesToChange;
        FormatFeature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet &= mask ^ -1;
            newMask |= mask;
        }
        return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
    }

    public final DeserializationConfig with(JsonNodeFactory f) {
        return this._nodeFactory == f ? this : new DeserializationConfig(this, f);
    }

    public final DeserializationConfig withHandler(DeserializationProblemHandler h) {
        return LinkedNode.contains(this._problemHandlers, h) ? this : new DeserializationConfig(this, new LinkedNode(h, this._problemHandlers));
    }

    public final DeserializationConfig withNoProblemHandlers() {
        return this._problemHandlers == null ? this : new DeserializationConfig(this, null);
    }

    public final void initialize(JsonParser p) {
        if (this._parserFeaturesToChange != 0) {
            p.overrideStdFeatures(this._parserFeatures, this._parserFeaturesToChange);
        }
        if (this._formatReadFeaturesToChange != 0) {
            p.overrideFormatFeatures(this._formatReadFeatures, this._formatReadFeaturesToChange);
        }
    }

    public final AnnotationIntrospector getAnnotationIntrospector() {
        if (isEnabled(MapperFeature.USE_ANNOTATIONS)) {
            return super.getAnnotationIntrospector();
        }
        return NopAnnotationIntrospector.instance;
    }

    public final BeanDescription introspectClassAnnotations(JavaType type) {
        return getClassIntrospector().forClassAnnotations(this, type, this);
    }

    public final BeanDescription introspectDirectClassAnnotations(JavaType type) {
        return getClassIntrospector().forDirectClassAnnotations(this, type, this);
    }

    public final Value getDefaultPropertyInclusion() {
        return EMPTY_INCLUDE;
    }

    public final Value getDefaultPropertyInclusion(Class<?> baseType) {
        ConfigOverride overrides = findConfigOverride(baseType);
        if (overrides != null) {
            Value v = overrides.getInclude();
            if (v != null) {
                return v;
            }
        }
        return EMPTY_INCLUDE;
    }

    public final Value getDefaultPropertyInclusion(Class<?> baseType, Value defaultIncl) {
        ConfigOverride overrides = findConfigOverride(baseType);
        if (overrides != null) {
            Value v = overrides.getInclude();
            if (v != null) {
                return v;
            }
        }
        return defaultIncl;
    }

    public final boolean useRootWrapping() {
        if (this._rootName != null) {
            return !this._rootName.isEmpty();
        } else {
            return isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE);
        }
    }

    public final boolean isEnabled(DeserializationFeature f) {
        return (this._deserFeatures & f.getMask()) != 0;
    }

    public final boolean isEnabled(Feature f, JsonFactory factory) {
        if ((this._parserFeaturesToChange & f.getMask()) != 0) {
            return (this._parserFeatures & f.getMask()) != 0;
        } else {
            return factory.isEnabled(f);
        }
    }

    public final boolean hasDeserializationFeatures(int featureMask) {
        return (this._deserFeatures & featureMask) == featureMask;
    }

    public final boolean hasSomeOfFeatures(int featureMask) {
        return (this._deserFeatures & featureMask) != 0;
    }

    public final int getDeserializationFeatures() {
        return this._deserFeatures;
    }

    public final LinkedNode<DeserializationProblemHandler> getProblemHandlers() {
        return this._problemHandlers;
    }

    public final JsonNodeFactory getNodeFactory() {
        return this._nodeFactory;
    }

    public final <T extends BeanDescription> T introspect(JavaType type) {
        return getClassIntrospector().forDeserialization(this, type, this);
    }

    public final <T extends BeanDescription> T introspectForCreation(JavaType type) {
        return getClassIntrospector().forCreation(this, type, this);
    }

    public final <T extends BeanDescription> T introspectForBuilder(JavaType type) {
        return getClassIntrospector().forDeserializationWithBuilder(this, type, this);
    }

    public final TypeDeserializer findTypeDeserializer(JavaType baseType) throws JsonMappingException {
        AnnotatedClass ac = introspectClassAnnotations(baseType.getRawClass()).getClassInfo();
        TypeResolverBuilder<?> b = getAnnotationIntrospector().findTypeResolver(this, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        subtypes = getSubtypeResolver().collectAndResolveSubtypesByTypeId(this, ac);
        return b.buildTypeDeserializer(this, baseType, subtypes);
    }
}
