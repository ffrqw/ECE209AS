package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector.MixInResolver;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Basic;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ClassUtil.Ctor;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AnnotatedClass extends Annotated implements TypeResolutionContext {
    private static final AnnotationMap[] NO_ANNOTATION_MAPS = new AnnotationMap[0];
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final TypeBindings _bindings;
    protected final Class<?> _class;
    protected AnnotationMap _classAnnotations;
    protected List<AnnotatedConstructor> _constructors;
    protected List<AnnotatedMethod> _creatorMethods;
    protected boolean _creatorsResolved = false;
    protected AnnotatedConstructor _defaultConstructor;
    protected List<AnnotatedField> _fields;
    protected AnnotatedMethodMap _memberMethods;
    protected final MixInResolver _mixInResolver;
    protected final Class<?> _primaryMixIn;
    protected final List<JavaType> _superTypes;
    protected final JavaType _type;
    protected final TypeFactory _typeFactory;

    private AnnotatedClass(JavaType type, Class<?> rawType, TypeBindings bindings, List<JavaType> superTypes, AnnotationIntrospector aintr, MixInResolver mir, TypeFactory tf, AnnotationMap classAnnotations) {
        this._type = type;
        this._class = rawType;
        this._bindings = bindings;
        this._superTypes = superTypes;
        this._annotationIntrospector = aintr;
        this._typeFactory = tf;
        this._mixInResolver = mir;
        this._primaryMixIn = this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(this._class);
        this._classAnnotations = classAnnotations;
    }

    public final AnnotatedClass withAnnotations(AnnotationMap ann) {
        return new AnnotatedClass(this._type, this._class, this._bindings, this._superTypes, this._annotationIntrospector, this._mixInResolver, this._typeFactory, ann);
    }

    public static AnnotatedClass construct(JavaType type, MapperConfig<?> config) {
        AnnotationIntrospector intr;
        if (config.isAnnotationProcessingEnabled()) {
            intr = config.getAnnotationIntrospector();
        } else {
            intr = null;
        }
        return new AnnotatedClass(type, type.getRawClass(), type.getBindings(), ClassUtil.findSuperTypes(type, null, false), intr, config, config.getTypeFactory(), null);
    }

    public static AnnotatedClass construct(JavaType type, MapperConfig<?> config, MixInResolver mir) {
        AnnotationIntrospector intr;
        if (config.isAnnotationProcessingEnabled()) {
            intr = config.getAnnotationIntrospector();
        } else {
            intr = null;
        }
        return new AnnotatedClass(type, type.getRawClass(), type.getBindings(), ClassUtil.findSuperTypes(type, null, false), intr, mir, config.getTypeFactory(), null);
    }

    public static AnnotatedClass constructWithoutSuperTypes(Class<?> cls, MapperConfig<?> config) {
        if (config == null) {
            return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), null, null, null, null);
        }
        AnnotationIntrospector intr;
        if (config.isAnnotationProcessingEnabled()) {
            intr = config.getAnnotationIntrospector();
        } else {
            intr = null;
        }
        return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), intr, config, config.getTypeFactory(), null);
    }

    public static AnnotatedClass constructWithoutSuperTypes(Class<?> cls, MapperConfig<?> config, MixInResolver mir) {
        if (config == null) {
            return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), null, null, null, null);
        }
        AnnotationIntrospector intr;
        if (config.isAnnotationProcessingEnabled()) {
            intr = config.getAnnotationIntrospector();
        } else {
            intr = null;
        }
        return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), intr, mir, config.getTypeFactory(), null);
    }

    public final JavaType resolveType(Type type) {
        return this._typeFactory.constructType(type, this._bindings);
    }

    public final Class<?> getAnnotated() {
        return this._class;
    }

    public final int getModifiers() {
        return this._class.getModifiers();
    }

    public final String getName() {
        return this._class.getName();
    }

    public final <A extends Annotation> A getAnnotation(Class<A> acls) {
        return _classAnnotations().get(acls);
    }

    public final boolean hasAnnotation(Class<?> acls) {
        return _classAnnotations().has(acls);
    }

    public final boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
        return _classAnnotations().hasOneOf(annoClasses);
    }

    public final Class<?> getRawType() {
        return this._class;
    }

    public final Iterable<Annotation> annotations() {
        return _classAnnotations().annotations();
    }

    protected final AnnotationMap getAllAnnotations() {
        return _classAnnotations();
    }

    public final JavaType getType() {
        return this._type;
    }

    public final Annotations getAnnotations() {
        return _classAnnotations();
    }

    public final boolean hasAnnotations() {
        return _classAnnotations().size() > 0;
    }

    public final AnnotatedConstructor getDefaultConstructor() {
        if (!this._creatorsResolved) {
            resolveCreators();
        }
        return this._defaultConstructor;
    }

    public final List<AnnotatedConstructor> getConstructors() {
        if (!this._creatorsResolved) {
            resolveCreators();
        }
        return this._constructors;
    }

    public final List<AnnotatedMethod> getStaticMethods() {
        if (!this._creatorsResolved) {
            resolveCreators();
        }
        return this._creatorMethods;
    }

    public final Iterable<AnnotatedMethod> memberMethods() {
        if (this._memberMethods == null) {
            resolveMemberMethods();
        }
        return this._memberMethods;
    }

    public final int getMemberMethodCount() {
        if (this._memberMethods == null) {
            resolveMemberMethods();
        }
        return this._memberMethods.size();
    }

    public final AnnotatedMethod findMethod(String name, Class<?>[] paramTypes) {
        if (this._memberMethods == null) {
            resolveMemberMethods();
        }
        return this._memberMethods.find(name, paramTypes);
    }

    public final int getFieldCount() {
        if (this._fields == null) {
            resolveFields();
        }
        return this._fields.size();
    }

    public final Iterable<AnnotatedField> fields() {
        if (this._fields == null) {
            resolveFields();
        }
        return this._fields;
    }

    private AnnotationMap _classAnnotations() {
        AnnotationMap anns = this._classAnnotations;
        if (anns == null) {
            synchronized (this) {
                anns = this._classAnnotations;
                if (anns == null) {
                    anns = _resolveClassAnnotations();
                    this._classAnnotations = anns;
                }
            }
        }
        return anns;
    }

    private AnnotationMap _resolveClassAnnotations() {
        AnnotationMap ca = new AnnotationMap();
        if (this._annotationIntrospector != null) {
            if (this._primaryMixIn != null) {
                _addClassMixIns(ca, this._class, this._primaryMixIn);
            }
            _addAnnotationsIfNotPresent(ca, ClassUtil.findClassAnnotations(this._class));
            for (JavaType type : this._superTypes) {
                _addClassMixIns(ca, type);
                _addAnnotationsIfNotPresent(ca, ClassUtil.findClassAnnotations(type.getRawClass()));
            }
            _addClassMixIns(ca, Object.class);
        }
        return ca;
    }

    private void resolveCreators() {
        int len$;
        int i$;
        int i;
        AnnotatedClass typeContext = this;
        List<AnnotatedConstructor> constructors = null;
        if (!this._type.isEnumType()) {
            Ctor[] declaredCtors = ClassUtil.getConstructors(this._class);
            Ctor[] arr$ = declaredCtors;
            len$ = declaredCtors.length;
            for (i$ = 0; i$ < len$; i$++) {
                Ctor ctor = arr$[i$];
                if (_isIncludableConstructor(ctor.getConstructor())) {
                    if (ctor.getParamCount() == 0) {
                        this._defaultConstructor = _constructDefaultConstructor(ctor, typeContext);
                    } else {
                        if (constructors == null) {
                            constructors = new ArrayList(Math.max(10, declaredCtors.length));
                        }
                        constructors.add(_constructNonDefaultConstructor(ctor, typeContext));
                    }
                }
            }
        }
        if (constructors == null) {
            this._constructors = Collections.emptyList();
        } else {
            this._constructors = constructors;
        }
        if (!(this._primaryMixIn == null || (this._defaultConstructor == null && this._constructors.isEmpty()))) {
            _addConstructorMixIns(this._primaryMixIn);
        }
        if (this._annotationIntrospector != null) {
            if (this._defaultConstructor != null && this._annotationIntrospector.hasIgnoreMarker(this._defaultConstructor)) {
                this._defaultConstructor = null;
            }
            if (this._constructors != null) {
                i = this._constructors.size();
                while (true) {
                    i--;
                    if (i < 0) {
                        break;
                    } else if (this._annotationIntrospector.hasIgnoreMarker((AnnotatedMember) this._constructors.get(i))) {
                        this._constructors.remove(i);
                    }
                }
            }
        }
        List<AnnotatedMethod> creatorMethods = null;
        for (Method m : _findClassMethods(this._class)) {
            if (Modifier.isStatic(m.getModifiers())) {
                if (creatorMethods == null) {
                    creatorMethods = new ArrayList(8);
                }
                creatorMethods.add(_constructCreatorMethod(m, typeContext));
            }
        }
        if (creatorMethods == null) {
            this._creatorMethods = Collections.emptyList();
        } else {
            this._creatorMethods = creatorMethods;
            if (this._primaryMixIn != null) {
                _addFactoryMixIns(this._primaryMixIn);
            }
            if (this._annotationIntrospector != null) {
                i = this._creatorMethods.size();
                while (true) {
                    i--;
                    if (i < 0) {
                        break;
                    } else if (this._annotationIntrospector.hasIgnoreMarker((AnnotatedMember) this._creatorMethods.get(i))) {
                        this._creatorMethods.remove(i);
                    }
                }
            }
        }
        this._creatorsResolved = true;
    }

    private void resolveMemberMethods() {
        this._memberMethods = new AnnotatedMethodMap();
        AnnotatedMethodMap mixins = new AnnotatedMethodMap();
        _addMemberMethods(this._class, this, this._memberMethods, this._primaryMixIn, mixins);
        for (JavaType type : this._superTypes) {
            _addMemberMethods(type.getRawClass(), new Basic(this._typeFactory, type.getBindings()), this._memberMethods, this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(type.getRawClass()), mixins);
        }
        if (this._mixInResolver != null) {
            Class<?> mixin = this._mixInResolver.findMixInClassFor(Object.class);
            if (mixin != null) {
                _addMethodMixIns(this._class, this._memberMethods, mixin, mixins);
            }
        }
        if (this._annotationIntrospector != null && !mixins.isEmpty()) {
            Iterator<AnnotatedMethod> it = mixins.iterator();
            while (it.hasNext()) {
                AnnotatedMethod mixIn = (AnnotatedMethod) it.next();
                try {
                    Method m = Object.class.getDeclaredMethod(mixIn.getName(), mixIn.getRawParameterTypes());
                    if (m != null) {
                        AnnotatedMethod am = _constructMethod(m, this);
                        _addMixOvers(mixIn.getAnnotated(), am, false);
                        this._memberMethods.add(am);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void resolveFields() {
        Map<String, AnnotatedField> foundFields = _findFields(this._type, this, null);
        if (foundFields == null || foundFields.size() == 0) {
            this._fields = Collections.emptyList();
            return;
        }
        this._fields = new ArrayList(foundFields.size());
        this._fields.addAll(foundFields.values());
    }

    protected final void _addClassMixIns(AnnotationMap annotations, JavaType target) {
        if (this._mixInResolver != null) {
            Class<?> toMask = target.getRawClass();
            _addClassMixIns(annotations, toMask, this._mixInResolver.findMixInClassFor(toMask));
        }
    }

    protected final void _addClassMixIns(AnnotationMap annotations, Class<?> target) {
        if (this._mixInResolver != null) {
            _addClassMixIns(annotations, target, this._mixInResolver.findMixInClassFor(target));
        }
    }

    protected final void _addClassMixIns(AnnotationMap annotations, Class<?> toMask, Class<?> mixin) {
        if (mixin != null) {
            _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(mixin));
            for (Class<?> parent : ClassUtil.findSuperClasses(mixin, toMask, false)) {
                _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(parent));
            }
        }
    }

    protected final void _addConstructorMixIns(Class<?> mixin) {
        MemberKey[] ctorKeys = null;
        int ctorCount = this._constructors == null ? 0 : this._constructors.size();
        for (Ctor ctor0 : ClassUtil.getConstructors(mixin)) {
            Constructor ctor = ctor0.getConstructor();
            if (ctor.getParameterTypes().length != 0) {
                int i;
                if (ctorKeys == null) {
                    ctorKeys = new MemberKey[ctorCount];
                    for (i = 0; i < ctorCount; i++) {
                        ctorKeys[i] = new MemberKey(((AnnotatedConstructor) this._constructors.get(i)).getAnnotated());
                    }
                }
                MemberKey key = new MemberKey(ctor);
                for (i = 0; i < ctorCount; i++) {
                    if (key.equals(ctorKeys[i])) {
                        _addMixOvers(ctor, (AnnotatedConstructor) this._constructors.get(i), true);
                        break;
                    }
                }
            } else if (this._defaultConstructor != null) {
                _addMixOvers(ctor, this._defaultConstructor, false);
            }
        }
    }

    protected final void _addFactoryMixIns(Class<?> mixin) {
        MemberKey[] methodKeys = null;
        int methodCount = this._creatorMethods.size();
        for (Method m : ClassUtil.getDeclaredMethods(mixin)) {
            if (Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length != 0) {
                int i;
                if (methodKeys == null) {
                    methodKeys = new MemberKey[methodCount];
                    for (i = 0; i < methodCount; i++) {
                        methodKeys[i] = new MemberKey(((AnnotatedMethod) this._creatorMethods.get(i)).getAnnotated());
                    }
                }
                MemberKey key = new MemberKey(m);
                for (i = 0; i < methodCount; i++) {
                    if (key.equals(methodKeys[i])) {
                        _addMixOvers(m, (AnnotatedMethod) this._creatorMethods.get(i), true);
                        break;
                    }
                }
            }
        }
    }

    protected final void _addMemberMethods(Class<?> cls, TypeResolutionContext typeContext, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
        if (mixInCls != null) {
            _addMethodMixIns(cls, methods, mixInCls, mixIns);
        }
        if (cls != null) {
            for (Method m : _findClassMethods(cls)) {
                if (_isIncludableMemberMethod(m)) {
                    AnnotatedMethod old = methods.find(m);
                    if (old == null) {
                        AnnotatedMethod newM = _constructMethod(m, typeContext);
                        methods.add(newM);
                        old = mixIns.remove(m);
                        if (old != null) {
                            _addMixOvers(old.getAnnotated(), newM, false);
                        }
                    } else {
                        _addMixUnders(m, old);
                        if (old.getDeclaringClass().isInterface() && !m.getDeclaringClass().isInterface()) {
                            methods.add(old.withMethod(m));
                        }
                    }
                }
            }
        }
    }

    protected final void _addMethodMixIns(Class<?> targetClass, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns) {
        for (Class<?> mixin : ClassUtil.findRawSuperTypes(mixInCls, targetClass, true)) {
            for (Method m : ClassUtil.getDeclaredMethods(mixin)) {
                if (_isIncludableMemberMethod(m)) {
                    AnnotatedMethod am = methods.find(m);
                    if (am != null) {
                        _addMixUnders(m, am);
                    } else {
                        am = mixIns.find(m);
                        if (am != null) {
                            _addMixUnders(m, am);
                        } else {
                            mixIns.add(_constructMethod(m, this));
                        }
                    }
                }
            }
        }
    }

    protected final Map<String, AnnotatedField> _findFields(JavaType type, TypeResolutionContext typeContext, Map<String, AnnotatedField> fields) {
        JavaType parent = type.getSuperClass();
        if (parent != null) {
            Class<?> cls = type.getRawClass();
            fields = _findFields(parent, new Basic(this._typeFactory, parent.getBindings()), fields);
            for (Field f : ClassUtil.getDeclaredFields(cls)) {
                if (_isIncludableField(f)) {
                    if (fields == null) {
                        fields = new LinkedHashMap();
                    }
                    fields.put(f.getName(), _constructField(f, typeContext));
                }
            }
            if (this._mixInResolver != null) {
                Class<?> mixin = this._mixInResolver.findMixInClassFor(cls);
                if (mixin != null) {
                    _addFieldMixIns(mixin, cls, fields);
                }
            }
        }
        return fields;
    }

    protected final void _addFieldMixIns(Class<?> mixInCls, Class<?> targetClass, Map<String, AnnotatedField> fields) {
        for (Class<?> mixin : ClassUtil.findSuperClasses(mixInCls, targetClass, true)) {
            for (Field mixinField : ClassUtil.getDeclaredFields(mixin)) {
                if (_isIncludableField(mixinField)) {
                    AnnotatedField maskedField = (AnnotatedField) fields.get(mixinField.getName());
                    if (maskedField != null) {
                        _addOrOverrideAnnotations(maskedField, mixinField.getDeclaredAnnotations());
                    }
                }
            }
        }
    }

    protected final AnnotatedMethod _constructMethod(Method m, TypeResolutionContext typeContext) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedMethod(typeContext, m, _emptyAnnotationMap(), null);
        }
        return new AnnotatedMethod(typeContext, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), null);
    }

    protected final AnnotatedConstructor _constructDefaultConstructor(Ctor ctor, TypeResolutionContext typeContext) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _emptyAnnotationMap(), NO_ANNOTATION_MAPS);
        }
        return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), NO_ANNOTATION_MAPS);
    }

    protected final AnnotatedConstructor _constructNonDefaultConstructor(Ctor ctor, TypeResolutionContext typeContext) {
        int paramCount = ctor.getParamCount();
        if (this._annotationIntrospector == null) {
            return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), NO_ANNOTATION_MAPS);
        }
        AnnotationMap[] resolvedAnnotations;
        Annotation[][] paramAnns = ctor.getParameterAnnotations();
        if (paramCount != paramAnns.length) {
            resolvedAnnotations = null;
            Class<?> dc = ctor.getDeclaringClass();
            Annotation[][] old;
            if (dc.isEnum() && paramCount == paramAnns.length + 2) {
                old = paramAnns;
                paramAnns = new Annotation[(paramAnns.length + 2)][];
                System.arraycopy(old, 0, paramAnns, 2, old.length);
                resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
            } else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
                old = paramAnns;
                paramAnns = new Annotation[(paramAnns.length + 1)][];
                System.arraycopy(old, 0, paramAnns, 1, old.length);
                resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
            }
            if (resolvedAnnotations == null) {
                throw new IllegalStateException("Internal error: constructor for " + ctor.getDeclaringClass().getName() + " has mismatch: " + paramCount + " parameters; " + paramAnns.length + " sets of annotations");
            }
        }
        resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
        return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), resolvedAnnotations);
    }

    protected final AnnotatedMethod _constructCreatorMethod(Method m, TypeResolutionContext typeContext) {
        int paramCount = m.getParameterTypes().length;
        if (this._annotationIntrospector == null) {
            return new AnnotatedMethod(typeContext, m, _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedMethod(typeContext, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), NO_ANNOTATION_MAPS);
        }
        return new AnnotatedMethod(typeContext, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), _collectRelevantAnnotations(m.getParameterAnnotations()));
    }

    protected final AnnotatedField _constructField(Field f, TypeResolutionContext typeContext) {
        if (this._annotationIntrospector == null) {
            return new AnnotatedField(typeContext, f, _emptyAnnotationMap());
        }
        return new AnnotatedField(typeContext, f, _collectRelevantAnnotations(f.getDeclaredAnnotations()));
    }

    private AnnotationMap _emptyAnnotationMap() {
        return new AnnotationMap();
    }

    private AnnotationMap[] _emptyAnnotationMaps(int count) {
        if (count == 0) {
            return NO_ANNOTATION_MAPS;
        }
        AnnotationMap[] maps = new AnnotationMap[count];
        for (int i = 0; i < count; i++) {
            maps[i] = _emptyAnnotationMap();
        }
        return maps;
    }

    protected final boolean _isIncludableMemberMethod(Method m) {
        if (Modifier.isStatic(m.getModifiers()) || m.isSynthetic() || m.isBridge() || m.getParameterTypes().length > 2) {
            return false;
        }
        return true;
    }

    private boolean _isIncludableField(Field f) {
        if (f.isSynthetic() || Modifier.isStatic(f.getModifiers())) {
            return false;
        }
        return true;
    }

    private boolean _isIncludableConstructor(Constructor<?> c) {
        return !c.isSynthetic();
    }

    protected final AnnotationMap[] _collectRelevantAnnotations(Annotation[][] anns) {
        int len = anns.length;
        AnnotationMap[] result = new AnnotationMap[len];
        for (int i = 0; i < len; i++) {
            result[i] = _collectRelevantAnnotations(anns[i]);
        }
        return result;
    }

    protected final AnnotationMap _collectRelevantAnnotations(Annotation[] anns) {
        return _addAnnotationsIfNotPresent(new AnnotationMap(), anns);
    }

    private AnnotationMap _addAnnotationsIfNotPresent(AnnotationMap result, Annotation[] anns) {
        if (anns != null) {
            List<Annotation> fromBundles = null;
            Annotation[] arr$ = anns;
            int len$ = anns.length;
            for (int i$ = 0; i$ < len$; i$++) {
                Annotation ann = arr$[i$];
                if (result.addIfNotPresent(ann) && _isAnnotationBundle(ann)) {
                    fromBundles = _addFromBundle(ann, fromBundles);
                }
            }
            if (fromBundles != null) {
                _addAnnotationsIfNotPresent(result, (Annotation[]) fromBundles.toArray(new Annotation[fromBundles.size()]));
            }
        }
        return result;
    }

    private List<Annotation> _addFromBundle(Annotation bundle, List<Annotation> result) {
        for (Annotation a : ClassUtil.findClassAnnotations(bundle.annotationType())) {
            if (!((a instanceof Target) || (a instanceof Retention))) {
                if (result == null) {
                    result = new ArrayList();
                }
                result.add(a);
            }
        }
        return result;
    }

    private void _addAnnotationsIfNotPresent(AnnotatedMember target, Annotation[] anns) {
        while (anns != null) {
            List<Annotation> fromBundles = null;
            Annotation[] arr$ = anns;
            int len$ = anns.length;
            for (int i$ = 0; i$ < len$; i$++) {
                Annotation ann = arr$[i$];
                if (target.addIfNotPresent(ann) && _isAnnotationBundle(ann)) {
                    fromBundles = _addFromBundle(ann, fromBundles);
                }
            }
            if (fromBundles != null) {
                anns = (Annotation[]) fromBundles.toArray(new Annotation[fromBundles.size()]);
            } else {
                return;
            }
        }
    }

    private void _addOrOverrideAnnotations(AnnotatedMember target, Annotation[] anns) {
        while (anns != null) {
            List<Annotation> fromBundles = null;
            Annotation[] arr$ = anns;
            int len$ = anns.length;
            for (int i$ = 0; i$ < len$; i$++) {
                Annotation ann = arr$[i$];
                if (target.addOrOverride(ann) && _isAnnotationBundle(ann)) {
                    fromBundles = _addFromBundle(ann, fromBundles);
                }
            }
            if (fromBundles != null) {
                anns = (Annotation[]) fromBundles.toArray(new Annotation[fromBundles.size()]);
            } else {
                return;
            }
        }
    }

    protected final void _addMixOvers(Constructor<?> mixin, AnnotatedConstructor target, boolean addParamAnnotations) {
        _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
        if (addParamAnnotations) {
            Annotation[][] pa = mixin.getParameterAnnotations();
            int len = pa.length;
            for (int i = 0; i < len; i++) {
                for (Annotation a : pa[i]) {
                    target.addOrOverrideParam(i, a);
                }
            }
        }
    }

    protected final void _addMixOvers(Method mixin, AnnotatedMethod target, boolean addParamAnnotations) {
        _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
        if (addParamAnnotations) {
            Annotation[][] pa = mixin.getParameterAnnotations();
            int len = pa.length;
            for (int i = 0; i < len; i++) {
                for (Annotation a : pa[i]) {
                    target.addOrOverrideParam(i, a);
                }
            }
        }
    }

    protected final void _addMixUnders(Method src, AnnotatedMethod target) {
        _addAnnotationsIfNotPresent((AnnotatedMember) target, src.getDeclaredAnnotations());
    }

    private final boolean _isAnnotationBundle(Annotation ann) {
        return this._annotationIntrospector != null && this._annotationIntrospector.isAnnotationBundle(ann);
    }

    protected final Method[] _findClassMethods(Class<?> cls) {
        try {
            return ClassUtil.getDeclaredMethods(cls);
        } catch (NoClassDefFoundError ex) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                throw ex;
            }
            try {
                return loader.loadClass(cls.getName()).getDeclaredMethods();
            } catch (ClassNotFoundException e) {
                throw ex;
            }
        }
    }

    public final String toString() {
        return "[AnnotedClass " + this._class.getName() + "]";
    }

    public final int hashCode() {
        return this._class.getName().hashCode();
    }

    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        if (((AnnotatedClass) o)._class != this._class) {
            return false;
        }
        return true;
    }
}
