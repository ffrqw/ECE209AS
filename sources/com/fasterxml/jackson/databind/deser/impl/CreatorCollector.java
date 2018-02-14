package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CreatorCollector {
    protected static final int C_ARRAY_DELEGATE = 8;
    protected static final int C_BOOLEAN = 5;
    protected static final int C_DEFAULT = 0;
    protected static final int C_DELEGATE = 6;
    protected static final int C_DOUBLE = 4;
    protected static final int C_INT = 2;
    protected static final int C_LONG = 3;
    protected static final int C_PROPS = 7;
    protected static final int C_STRING = 1;
    protected static final String[] TYPE_DESCS = new String[]{"default", "from-String", "from-int", "from-long", "from-double", "from-boolean", "delegate", "property-based"};
    protected SettableBeanProperty[] _arrayDelegateArgs;
    protected final BeanDescription _beanDesc;
    protected final boolean _canFixAccess;
    protected final AnnotatedWithParams[] _creators = new AnnotatedWithParams[9];
    protected SettableBeanProperty[] _delegateArgs;
    protected int _explicitCreators = 0;
    protected final boolean _forceAccess;
    protected boolean _hasNonDefaultCreator = false;
    protected AnnotatedParameter _incompleteParameter;
    protected SettableBeanProperty[] _propertyBasedArgs;

    protected static final class StdTypeConstructor extends AnnotatedWithParams implements Serializable {
        public static final int TYPE_ARRAY_LIST = 1;
        public static final int TYPE_HASH_MAP = 2;
        public static final int TYPE_LINKED_HASH_MAP = 3;
        private static final long serialVersionUID = 1;
        private final AnnotatedWithParams _base;
        private final int _type;

        public StdTypeConstructor(AnnotatedWithParams base, int t) {
            super(base, null);
            this._base = base;
            this._type = t;
        }

        public static AnnotatedWithParams tryToOptimize(AnnotatedWithParams src) {
            if (src == null) {
                return src;
            }
            Class<?> rawType = src.getDeclaringClass();
            if (rawType == List.class || rawType == ArrayList.class) {
                return new StdTypeConstructor(src, 1);
            }
            if (rawType == LinkedHashMap.class) {
                return new StdTypeConstructor(src, 3);
            }
            if (rawType == HashMap.class) {
                return new StdTypeConstructor(src, 2);
            }
            return src;
        }

        protected final Object _construct() {
            switch (this._type) {
                case 1:
                    return new ArrayList();
                case 2:
                    return new HashMap();
                case 3:
                    return new LinkedHashMap();
                default:
                    throw new IllegalStateException("Unknown type " + this._type);
            }
        }

        public final int getParameterCount() {
            return this._base.getParameterCount();
        }

        public final Class<?> getRawParameterType(int index) {
            return this._base.getRawParameterType(index);
        }

        public final JavaType getParameterType(int index) {
            return this._base.getParameterType(index);
        }

        @Deprecated
        public final Type getGenericParameterType(int index) {
            return this._base.getGenericParameterType(index);
        }

        public final Object call() throws Exception {
            return _construct();
        }

        public final Object call(Object[] args) throws Exception {
            return _construct();
        }

        public final Object call1(Object arg) throws Exception {
            return _construct();
        }

        public final Class<?> getDeclaringClass() {
            return this._base.getDeclaringClass();
        }

        public final Member getMember() {
            return this._base.getMember();
        }

        public final void setValue(Object pojo, Object value) throws UnsupportedOperationException, IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public final Object getValue(Object pojo) throws UnsupportedOperationException, IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        public final Annotated withAnnotations(AnnotationMap fallback) {
            throw new UnsupportedOperationException();
        }

        public final AnnotatedElement getAnnotated() {
            return this._base.getAnnotated();
        }

        protected final int getModifiers() {
            return this._base.getMember().getModifiers();
        }

        public final String getName() {
            return this._base.getName();
        }

        public final JavaType getType() {
            return this._base.getType();
        }

        public final Class<?> getRawType() {
            return this._base.getRawType();
        }

        public final boolean equals(Object o) {
            return o == this;
        }

        public final int hashCode() {
            return this._base.hashCode();
        }

        public final String toString() {
            return this._base.toString();
        }
    }

    public CreatorCollector(BeanDescription beanDesc, MapperConfig<?> config) {
        this._beanDesc = beanDesc;
        this._canFixAccess = config.canOverrideAccessModifiers();
        this._forceAccess = config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
    }

    public ValueInstantiator constructValueInstantiator(DeserializationConfig config) {
        JavaType delegateType = _computeDelegateType(this._creators[6], this._delegateArgs);
        JavaType arrayDelegateType = _computeDelegateType(this._creators[8], this._arrayDelegateArgs);
        JavaType type = this._beanDesc.getType();
        AnnotatedWithParams defaultCtor = StdTypeConstructor.tryToOptimize(this._creators[0]);
        StdValueInstantiator inst = new StdValueInstantiator(config, type);
        inst.configureFromObjectSettings(defaultCtor, this._creators[6], delegateType, this._delegateArgs, this._creators[7], this._propertyBasedArgs);
        inst.configureFromArraySettings(this._creators[8], arrayDelegateType, this._arrayDelegateArgs);
        inst.configureFromStringCreator(this._creators[1]);
        inst.configureFromIntCreator(this._creators[2]);
        inst.configureFromLongCreator(this._creators[3]);
        inst.configureFromDoubleCreator(this._creators[4]);
        inst.configureFromBooleanCreator(this._creators[5]);
        inst.configureIncompleteParameter(this._incompleteParameter);
        return inst;
    }

    public void setDefaultCreator(AnnotatedWithParams creator) {
        this._creators[0] = (AnnotatedWithParams) _fixAccess(creator);
    }

    public void addStringCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 1, explicit);
    }

    public void addIntCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 2, explicit);
    }

    public void addLongCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 3, explicit);
    }

    public void addDoubleCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 4, explicit);
    }

    public void addBooleanCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 5, explicit);
    }

    public void addDelegatingCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] injectables) {
        if (creator.getParameterType(0).isCollectionLikeType()) {
            verifyNonDup(creator, 8, explicit);
            this._arrayDelegateArgs = injectables;
            return;
        }
        verifyNonDup(creator, 6, explicit);
        this._delegateArgs = injectables;
    }

    public void addPropertyCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] properties) {
        verifyNonDup(creator, 7, explicit);
        if (properties.length > 1) {
            HashMap<String, Integer> names = new HashMap();
            int i = 0;
            int len = properties.length;
            while (i < len) {
                String name = properties[i].getName();
                if ((name.length() != 0 || properties[i].getInjectableValueId() == null) && ((Integer) names.put(name, Integer.valueOf(i))) != null) {
                    throw new IllegalArgumentException(String.format("Duplicate creator property \"%s\" (index %s vs %d)", new Object[]{name, (Integer) names.put(name, Integer.valueOf(i)), Integer.valueOf(i)}));
                }
                i++;
            }
        }
        this._propertyBasedArgs = properties;
    }

    public void addIncompeteParameter(AnnotatedParameter parameter) {
        if (this._incompleteParameter == null) {
            this._incompleteParameter = parameter;
        }
    }

    @Deprecated
    public void addStringCreator(AnnotatedWithParams creator) {
        addStringCreator(creator, false);
    }

    @Deprecated
    public void addIntCreator(AnnotatedWithParams creator) {
        addBooleanCreator(creator, false);
    }

    @Deprecated
    public void addLongCreator(AnnotatedWithParams creator) {
        addBooleanCreator(creator, false);
    }

    @Deprecated
    public void addDoubleCreator(AnnotatedWithParams creator) {
        addBooleanCreator(creator, false);
    }

    @Deprecated
    public void addBooleanCreator(AnnotatedWithParams creator) {
        addBooleanCreator(creator, false);
    }

    @Deprecated
    public void addDelegatingCreator(AnnotatedWithParams creator, CreatorProperty[] injectables) {
        addDelegatingCreator(creator, false, injectables);
    }

    @Deprecated
    public void addPropertyCreator(AnnotatedWithParams creator, CreatorProperty[] properties) {
        addPropertyCreator(creator, false, properties);
    }

    public boolean hasDefaultCreator() {
        return this._creators[0] != null;
    }

    public boolean hasDelegatingCreator() {
        return this._creators[6] != null;
    }

    public boolean hasPropertyBasedCreator() {
        return this._creators[7] != null;
    }

    private JavaType _computeDelegateType(AnnotatedWithParams creator, SettableBeanProperty[] delegateArgs) {
        if (!this._hasNonDefaultCreator || creator == null) {
            return null;
        }
        int ix = 0;
        if (delegateArgs != null) {
            int len = delegateArgs.length;
            for (int i = 0; i < len; i++) {
                if (delegateArgs[i] == null) {
                    ix = i;
                    break;
                }
            }
        }
        return creator.getParameterType(ix);
    }

    private <T extends AnnotatedMember> T _fixAccess(T member) {
        if (member != null && this._canFixAccess) {
            ClassUtil.checkAndFixAccess((Member) member.getAnnotated(), this._forceAccess);
        }
        return member;
    }

    protected void verifyNonDup(AnnotatedWithParams newOne, int typeIndex, boolean explicit) {
        int mask = 1 << typeIndex;
        this._hasNonDefaultCreator = true;
        AnnotatedWithParams oldOne = this._creators[typeIndex];
        if (oldOne != null) {
            boolean verify;
            if ((this._explicitCreators & mask) == 0) {
                verify = !explicit;
            } else if (explicit) {
                verify = true;
            } else {
                return;
            }
            if (verify && oldOne.getClass() == newOne.getClass()) {
                Class<?> oldType = oldOne.getRawParameterType(0);
                Class<?> newType = newOne.getRawParameterType(0);
                if (oldType == newType) {
                    if (!_isEnumValueOf(newOne)) {
                        if (!_isEnumValueOf(oldOne)) {
                            String str;
                            String str2 = "Conflicting %s creators: already had %s creator %s, encountered another: %s";
                            Object[] objArr = new Object[4];
                            objArr[0] = TYPE_DESCS[typeIndex];
                            if (explicit) {
                                str = "explicitly marked";
                            } else {
                                str = "implicitly discovered";
                            }
                            objArr[1] = str;
                            objArr[2] = oldOne;
                            objArr[3] = newOne;
                            throw new IllegalArgumentException(String.format(str2, objArr));
                        }
                    }
                    return;
                } else if (newType.isAssignableFrom(oldType)) {
                    return;
                }
            }
        }
        if (explicit) {
            this._explicitCreators |= mask;
        }
        this._creators[typeIndex] = (AnnotatedWithParams) _fixAccess(newOne);
    }

    protected boolean _isEnumValueOf(AnnotatedWithParams creator) {
        return creator.getDeclaringClass().isEnum() && "valueOf".equals(creator.getName());
    }
}
