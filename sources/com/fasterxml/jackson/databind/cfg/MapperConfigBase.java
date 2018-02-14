package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements Serializable {
    private static final int DEFAULT_MAPPER_FEATURES = MapperConfig.collectFeatureDefaults(MapperFeature.class);
    protected final ContextAttributes _attributes;
    protected final ConfigOverrides _configOverrides;
    protected final SimpleMixInResolver _mixIns;
    protected final PropertyName _rootName;
    protected final RootNameLookup _rootNames;
    protected final SubtypeResolver _subtypeResolver;
    protected final Class<?> _view;

    public abstract T with(Base64Variant base64Variant);

    public abstract T with(AnnotationIntrospector annotationIntrospector);

    public abstract T with(PropertyNamingStrategy propertyNamingStrategy);

    public abstract T with(ContextAttributes contextAttributes);

    public abstract T with(HandlerInstantiator handlerInstantiator);

    public abstract T with(ClassIntrospector classIntrospector);

    public abstract T with(VisibilityChecker<?> visibilityChecker);

    public abstract T with(SubtypeResolver subtypeResolver);

    public abstract T with(TypeResolverBuilder<?> typeResolverBuilder);

    public abstract T with(TypeFactory typeFactory);

    public abstract T with(DateFormat dateFormat);

    public abstract T with(Locale locale);

    public abstract T with(TimeZone timeZone);

    public abstract T withAppendedAnnotationIntrospector(AnnotationIntrospector annotationIntrospector);

    public abstract T withInsertedAnnotationIntrospector(AnnotationIntrospector annotationIntrospector);

    public abstract T withRootName(PropertyName propertyName);

    public abstract T withView(Class<?> cls);

    public abstract T withVisibility(PropertyAccessor propertyAccessor, Visibility visibility);

    protected MapperConfigBase(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super(base, DEFAULT_MAPPER_FEATURES);
        this._mixIns = mixins;
        this._subtypeResolver = str;
        this._rootNames = rootNames;
        this._rootName = null;
        this._view = null;
        this._attributes = ContextAttributes.getEmpty();
        this._configOverrides = configOverrides;
    }

    @Deprecated
    protected MapperConfigBase(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames) {
        this(base, str, mixins, rootNames, null);
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, BaseSettings base) {
        super((MapperConfig) src, base);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, int mapperFeatures) {
        super((MapperConfig) src, mapperFeatures);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, SubtypeResolver str) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = str;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, PropertyName rootName) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, Class<?> view) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins) {
        super(src);
        this._mixIns = mixins;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, ContextAttributes attr) {
        super(src);
        this._mixIns = src._mixIns;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = src._rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = attr;
        this._configOverrides = src._configOverrides;
    }

    protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
        super(src);
        this._mixIns = mixins;
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = rootNames;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
        this._configOverrides = configOverrides;
    }

    public VisibilityChecker<?> getDefaultVisibilityChecker() {
        VisibilityChecker<?> vchecker = super.getDefaultVisibilityChecker();
        if (!isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
            vchecker = vchecker.withSetterVisibility(Visibility.NONE);
        }
        if (!isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
            vchecker = vchecker.withCreatorVisibility(Visibility.NONE);
        }
        if (!isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
            vchecker = vchecker.withGetterVisibility(Visibility.NONE);
        }
        if (!isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
            vchecker = vchecker.withIsGetterVisibility(Visibility.NONE);
        }
        if (isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
            return vchecker;
        }
        return vchecker.withFieldVisibility(Visibility.NONE);
    }

    public T withRootName(String rootName) {
        if (rootName == null) {
            return withRootName(null);
        }
        return withRootName(PropertyName.construct(rootName));
    }

    public T withAttributes(Map<?, ?> attributes) {
        return with(getAttributes().withSharedAttributes(attributes));
    }

    public T withAttribute(Object key, Object value) {
        return with(getAttributes().withSharedAttribute(key, value));
    }

    public T withoutAttribute(Object key) {
        return with(getAttributes().withoutSharedAttribute(key));
    }

    public final SubtypeResolver getSubtypeResolver() {
        return this._subtypeResolver;
    }

    @Deprecated
    public final String getRootName() {
        return this._rootName == null ? null : this._rootName.getSimpleName();
    }

    public final PropertyName getFullRootName() {
        return this._rootName;
    }

    public final Class<?> getActiveView() {
        return this._view;
    }

    public final ContextAttributes getAttributes() {
        return this._attributes;
    }

    public final ConfigOverride findConfigOverride(Class<?> type) {
        return this._configOverrides.findOverride(type);
    }

    public final Value getDefaultPropertyFormat(Class<?> type) {
        ConfigOverride overrides = this._configOverrides.findOverride(type);
        if (overrides != null) {
            Value v = overrides.getFormat();
            if (v != null) {
                return v;
            }
        }
        return EMPTY_FORMAT;
    }

    public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> type) {
        ConfigOverride overrides = this._configOverrides.findOverride(type);
        if (overrides != null) {
            JsonIgnoreProperties.Value v = overrides.getIgnorals();
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    public final JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> baseType, AnnotatedClass actualClass) {
        AnnotationIntrospector intr = getAnnotationIntrospector();
        return JsonIgnoreProperties.Value.merge(intr == null ? null : intr.findPropertyIgnorals(actualClass), getDefaultPropertyIgnorals(baseType));
    }

    public PropertyName findRootName(JavaType rootType) {
        if (this._rootName != null) {
            return this._rootName;
        }
        return this._rootNames.findRootName(rootType, (MapperConfig) this);
    }

    public PropertyName findRootName(Class<?> rawRootType) {
        if (this._rootName != null) {
            return this._rootName;
        }
        return this._rootNames.findRootName((Class) rawRootType, (MapperConfig) this);
    }

    public final Class<?> findMixInClassFor(Class<?> cls) {
        return this._mixIns.findMixInClassFor(cls);
    }

    public MixInResolver copy() {
        throw new UnsupportedOperationException();
    }

    public final int mixInCount() {
        return this._mixIns.localSize();
    }
}
