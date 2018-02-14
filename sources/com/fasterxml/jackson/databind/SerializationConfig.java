package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class SerializationConfig extends MapperConfigBase<SerializationFeature, SerializationConfig> implements Serializable {
    protected static final Value DEFAULT_INCLUSION = Value.empty();
    protected static final PrettyPrinter DEFAULT_PRETTY_PRINTER = new DefaultPrettyPrinter();
    private static final long serialVersionUID = 1;
    protected final PrettyPrinter _defaultPrettyPrinter;
    protected final FilterProvider _filterProvider;
    protected final int _formatWriteFeatures;
    protected final int _formatWriteFeaturesToChange;
    protected final int _generatorFeatures;
    protected final int _generatorFeaturesToChange;
    protected final int _serFeatures;
    protected final Value _serializationInclusion;

    public SerializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super(base, str, mixins, rootNames, configOverrides);
        this._serFeatures = MapperConfig.collectFeatureDefaults(SerializationFeature.class);
        this._filterProvider = null;
        this._defaultPrettyPrinter = DEFAULT_PRETTY_PRINTER;
        this._generatorFeatures = 0;
        this._generatorFeaturesToChange = 0;
        this._formatWriteFeatures = 0;
        this._formatWriteFeaturesToChange = 0;
        this._serializationInclusion = DEFAULT_INCLUSION;
    }

    @Deprecated
    public SerializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames) {
        this(base, str, mixins, rootNames, null);
    }

    private SerializationConfig(SerializationConfig src, SubtypeResolver str) {
        super((MapperConfigBase) src, str);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    private SerializationConfig(SerializationConfig src, int mapperFeatures, int serFeatures, int generatorFeatures, int generatorFeatureMask, int formatFeatures, int formatFeaturesMask) {
        super((MapperConfigBase) src, mapperFeatures);
        this._serFeatures = serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = generatorFeatures;
        this._generatorFeaturesToChange = generatorFeatureMask;
        this._formatWriteFeatures = formatFeatures;
        this._formatWriteFeaturesToChange = formatFeaturesMask;
    }

    private SerializationConfig(SerializationConfig src, BaseSettings base) {
        super((MapperConfigBase) src, base);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    private SerializationConfig(SerializationConfig src, FilterProvider filters) {
        super(src);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = filters;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    private SerializationConfig(SerializationConfig src, Class<?> view) {
        super((MapperConfigBase) src, (Class) view);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    private SerializationConfig(SerializationConfig src, Value incl) {
        super(src);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = incl;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    private SerializationConfig(SerializationConfig src, PropertyName rootName) {
        super((MapperConfigBase) src, rootName);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    protected SerializationConfig(SerializationConfig src, ContextAttributes attrs) {
        super((MapperConfigBase) src, attrs);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins) {
        super((MapperConfigBase) src, mixins);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    protected SerializationConfig(SerializationConfig src, PrettyPrinter defaultPP) {
        super(src);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = defaultPP;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super((MapperConfigBase) src, mixins, rootNames, configOverrides);
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
        this._defaultPrettyPrinter = src._defaultPrettyPrinter;
        this._generatorFeatures = src._generatorFeatures;
        this._generatorFeaturesToChange = src._generatorFeaturesToChange;
        this._formatWriteFeatures = src._formatWriteFeatures;
        this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
    }

    public final SerializationConfig with(MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        MapperFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newMapperFlags |= arr$[i$].getMask();
        }
        return newMapperFlags == this._mapperFeatures ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig without(MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        MapperFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newMapperFlags &= arr$[i$].getMask() ^ -1;
        }
        return newMapperFlags == this._mapperFeatures ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig with(MapperFeature feature, boolean state) {
        int newMapperFlags;
        if (state) {
            newMapperFlags = this._mapperFeatures | feature.getMask();
        } else {
            newMapperFlags = this._mapperFeatures & (feature.getMask() ^ -1);
        }
        return newMapperFlags == this._mapperFeatures ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig with(AnnotationIntrospector ai) {
        return _withBase(this._base.withAnnotationIntrospector(ai));
    }

    public final SerializationConfig withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
        return _withBase(this._base.withAppendedAnnotationIntrospector(ai));
    }

    public final SerializationConfig withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
        return _withBase(this._base.withInsertedAnnotationIntrospector(ai));
    }

    public final SerializationConfig with(ClassIntrospector ci) {
        return _withBase(this._base.withClassIntrospector(ci));
    }

    public final SerializationConfig with(DateFormat df) {
        SerializationConfig cfg = new SerializationConfig(this, this._base.withDateFormat(df));
        if (df == null) {
            return cfg.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        return cfg.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public final SerializationConfig with(HandlerInstantiator hi) {
        return _withBase(this._base.withHandlerInstantiator(hi));
    }

    public final SerializationConfig with(PropertyNamingStrategy pns) {
        return _withBase(this._base.withPropertyNamingStrategy(pns));
    }

    public final SerializationConfig withRootName(PropertyName rootName) {
        if (rootName == null) {
            if (this._rootName == null) {
                return this;
            }
        } else if (rootName.equals(this._rootName)) {
            return this;
        }
        return new SerializationConfig(this, rootName);
    }

    public final SerializationConfig with(SubtypeResolver str) {
        return str == this._subtypeResolver ? this : new SerializationConfig(this, str);
    }

    public final SerializationConfig with(TypeFactory tf) {
        return _withBase(this._base.withTypeFactory(tf));
    }

    public final SerializationConfig with(TypeResolverBuilder<?> trb) {
        return _withBase(this._base.withTypeResolverBuilder(trb));
    }

    public final SerializationConfig withView(Class<?> view) {
        return this._view == view ? this : new SerializationConfig(this, (Class) view);
    }

    public final SerializationConfig with(VisibilityChecker<?> vc) {
        return _withBase(this._base.withVisibilityChecker(vc));
    }

    public final SerializationConfig withVisibility(PropertyAccessor forMethod, Visibility visibility) {
        return _withBase(this._base.withVisibility(forMethod, visibility));
    }

    public final SerializationConfig with(Locale l) {
        return _withBase(this._base.with(l));
    }

    public final SerializationConfig with(TimeZone tz) {
        return _withBase(this._base.with(tz));
    }

    public final SerializationConfig with(Base64Variant base64) {
        return _withBase(this._base.with(base64));
    }

    public final SerializationConfig with(ContextAttributes attrs) {
        return attrs == this._attributes ? this : new SerializationConfig(this, attrs);
    }

    private final SerializationConfig _withBase(BaseSettings newBase) {
        return this._base == newBase ? this : new SerializationConfig(this, newBase);
    }

    public final SerializationConfig with(SerializationFeature feature) {
        int newSerFeatures = this._serFeatures | feature.getMask();
        return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig with(SerializationFeature first, SerializationFeature... features) {
        int newSerFeatures = this._serFeatures | first.getMask();
        SerializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newSerFeatures |= arr$[i$].getMask();
        }
        return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig withFeatures(SerializationFeature... features) {
        int newSerFeatures = this._serFeatures;
        SerializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newSerFeatures |= arr$[i$].getMask();
        }
        return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig without(SerializationFeature feature) {
        int newSerFeatures = this._serFeatures & (feature.getMask() ^ -1);
        return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig without(SerializationFeature first, SerializationFeature... features) {
        int newSerFeatures = this._serFeatures & (first.getMask() ^ -1);
        SerializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newSerFeatures &= arr$[i$].getMask() ^ -1;
        }
        return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig withoutFeatures(SerializationFeature... features) {
        int newSerFeatures = this._serFeatures;
        SerializationFeature[] arr$ = features;
        for (int i$ = 0; i$ < features.length; i$++) {
            newSerFeatures &= arr$[i$].getMask() ^ -1;
        }
        return newSerFeatures == this._serFeatures ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig with(Feature feature) {
        int newSet = this._generatorFeatures | feature.getMask();
        int newMask = this._generatorFeaturesToChange | feature.getMask();
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig withFeatures(Feature... features) {
        int newSet = this._generatorFeatures;
        int newMask = this._generatorFeaturesToChange;
        Feature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig without(Feature feature) {
        int newSet = this._generatorFeatures & (feature.getMask() ^ -1);
        int newMask = this._generatorFeaturesToChange | feature.getMask();
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig withoutFeatures(Feature... features) {
        int newSet = this._generatorFeatures;
        int newMask = this._generatorFeaturesToChange;
        Feature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet &= mask ^ -1;
            newMask |= mask;
        }
        return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
    }

    public final SerializationConfig with(FormatFeature feature) {
        int newSet = this._formatWriteFeatures | feature.getMask();
        int newMask = this._formatWriteFeaturesToChange | feature.getMask();
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }

    public final SerializationConfig withFeatures(FormatFeature... features) {
        int newSet = this._formatWriteFeatures;
        int newMask = this._formatWriteFeaturesToChange;
        FormatFeature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet |= mask;
            newMask |= mask;
        }
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }

    public final SerializationConfig without(FormatFeature feature) {
        int newSet = this._formatWriteFeatures & (feature.getMask() ^ -1);
        int newMask = this._formatWriteFeaturesToChange | feature.getMask();
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }

    public final SerializationConfig withoutFeatures(FormatFeature... features) {
        int newSet = this._formatWriteFeatures;
        int newMask = this._formatWriteFeaturesToChange;
        FormatFeature[] arr$ = features;
        int len$ = features.length;
        for (int i$ = 0; i$ < len$; i$++) {
            int mask = arr$[i$].getMask();
            newSet &= mask ^ -1;
            newMask |= mask;
        }
        return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
    }

    public final SerializationConfig withFilters(FilterProvider filterProvider) {
        return filterProvider == this._filterProvider ? this : new SerializationConfig(this, filterProvider);
    }

    @Deprecated
    public final SerializationConfig withSerializationInclusion(Include incl) {
        return withPropertyInclusion(DEFAULT_INCLUSION.withValueInclusion(incl));
    }

    public final SerializationConfig withPropertyInclusion(Value incl) {
        return this._serializationInclusion.equals(incl) ? this : new SerializationConfig(this, incl);
    }

    public final SerializationConfig withDefaultPrettyPrinter(PrettyPrinter pp) {
        return this._defaultPrettyPrinter == pp ? this : new SerializationConfig(this, pp);
    }

    public final PrettyPrinter constructDefaultPrettyPrinter() {
        PrettyPrinter pp = this._defaultPrettyPrinter;
        if (pp instanceof Instantiatable) {
            return (PrettyPrinter) ((Instantiatable) pp).createInstance();
        }
        return pp;
    }

    public final void initialize(JsonGenerator g) {
        if (SerializationFeature.INDENT_OUTPUT.enabledIn(this._serFeatures) && g.getPrettyPrinter() == null) {
            PrettyPrinter pp = constructDefaultPrettyPrinter();
            if (pp != null) {
                g.setPrettyPrinter(pp);
            }
        }
        boolean useBigDec = SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._serFeatures);
        int mask = this._generatorFeaturesToChange;
        if (mask != 0 || useBigDec) {
            int newFlags = this._generatorFeatures;
            if (useBigDec) {
                int f = Feature.WRITE_BIGDECIMAL_AS_PLAIN.getMask();
                newFlags |= f;
                mask |= f;
            }
            g.overrideStdFeatures(newFlags, mask);
        }
        if (this._formatWriteFeaturesToChange != 0) {
            g.overrideFormatFeatures(this._formatWriteFeatures, this._formatWriteFeaturesToChange);
        }
    }

    public final AnnotationIntrospector getAnnotationIntrospector() {
        if (isEnabled(MapperFeature.USE_ANNOTATIONS)) {
            return super.getAnnotationIntrospector();
        }
        return AnnotationIntrospector.nopInstance();
    }

    public final BeanDescription introspectClassAnnotations(JavaType type) {
        return getClassIntrospector().forClassAnnotations(this, type, this);
    }

    public final BeanDescription introspectDirectClassAnnotations(JavaType type) {
        return getClassIntrospector().forDirectClassAnnotations(this, type, this);
    }

    @Deprecated
    public final Include getSerializationInclusion() {
        Include incl = this._serializationInclusion.getValueInclusion();
        return incl == Include.USE_DEFAULTS ? Include.ALWAYS : incl;
    }

    public final Value getDefaultPropertyInclusion() {
        return this._serializationInclusion;
    }

    public final Value getDefaultPropertyInclusion(Class<?> baseType) {
        ConfigOverride overrides = findConfigOverride(baseType);
        if (overrides != null) {
            Value v = overrides.getInclude();
            if (v != null) {
                return v;
            }
        }
        return this._serializationInclusion;
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
            return isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
        }
    }

    public final boolean isEnabled(SerializationFeature f) {
        return (this._serFeatures & f.getMask()) != 0;
    }

    public final boolean isEnabled(Feature f, JsonFactory factory) {
        if ((this._generatorFeaturesToChange & f.getMask()) != 0) {
            return (this._generatorFeatures & f.getMask()) != 0;
        } else {
            return factory.isEnabled(f);
        }
    }

    public final boolean hasSerializationFeatures(int featureMask) {
        return (this._serFeatures & featureMask) == featureMask;
    }

    public final int getSerializationFeatures() {
        return this._serFeatures;
    }

    public final FilterProvider getFilterProvider() {
        return this._filterProvider;
    }

    public final PrettyPrinter getDefaultPrettyPrinter() {
        return this._defaultPrettyPrinter;
    }

    public final <T extends BeanDescription> T introspect(JavaType type) {
        return getClassIntrospector().forSerialization(this, type, this);
    }

    public final String toString() {
        return "[SerializationConfig: flags=0x" + Integer.toHexString(this._serFeatures) + "]";
    }
}
