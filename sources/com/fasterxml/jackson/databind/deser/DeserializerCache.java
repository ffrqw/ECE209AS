package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class DeserializerCache implements Serializable {
    private static final long serialVersionUID = 1;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers = new ConcurrentHashMap(64, 0.75f, 4);
    protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers = new HashMap(8);

    final Object writeReplace() {
        this._incompleteDeserializers.clear();
        return this;
    }

    public final int cachedDeserializersCount() {
        return this._cachedDeserializers.size();
    }

    public final void flushCachedDeserializers() {
        this._cachedDeserializers.clear();
    }

    public final JsonDeserializer<Object> findValueDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType propertyType) throws JsonMappingException {
        JsonDeserializer<Object> deser = _findCachedDeserializer(propertyType);
        if (deser != null) {
            return deser;
        }
        deser = _createAndCacheValueDeserializer(ctxt, factory, propertyType);
        if (deser == null) {
            return _handleUnknownValueDeserializer(ctxt, propertyType);
        }
        return deser;
    }

    public final KeyDeserializer findKeyDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
        if (kd == null) {
            return _handleUnknownKeyDeserializer(ctxt, type);
        }
        if (!(kd instanceof ResolvableDeserializer)) {
            return kd;
        }
        ((ResolvableDeserializer) kd).resolve(ctxt);
        return kd;
    }

    public final boolean hasValueDeserializerFor(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = _findCachedDeserializer(type);
        if (deser == null) {
            deser = _createAndCacheValueDeserializer(ctxt, factory, type);
        }
        return deser != null;
    }

    protected final JsonDeserializer<Object> _findCachedDeserializer(JavaType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null JavaType passed");
        } else if (_hasCustomValueHandler(type)) {
            return null;
        } else {
            return (JsonDeserializer) this._cachedDeserializers.get(type);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected final com.fasterxml.jackson.databind.JsonDeserializer<java.lang.Object> _createAndCacheValueDeserializer(com.fasterxml.jackson.databind.DeserializationContext r6, com.fasterxml.jackson.databind.deser.DeserializerFactory r7, com.fasterxml.jackson.databind.JavaType r8) throws com.fasterxml.jackson.databind.JsonMappingException {
        /*
        r5 = this;
        r3 = r5._incompleteDeserializers;
        monitor-enter(r3);
        r1 = r5._findCachedDeserializer(r8);	 Catch:{ all -> 0x0036 }
        if (r1 == 0) goto L_0x000c;
    L_0x0009:
        monitor-exit(r3);	 Catch:{ all -> 0x0036 }
        r2 = r1;
    L_0x000b:
        return r2;
    L_0x000c:
        r2 = r5._incompleteDeserializers;	 Catch:{ all -> 0x0036 }
        r0 = r2.size();	 Catch:{ all -> 0x0036 }
        if (r0 <= 0) goto L_0x0021;
    L_0x0014:
        r2 = r5._incompleteDeserializers;	 Catch:{ all -> 0x0036 }
        r1 = r2.get(r8);	 Catch:{ all -> 0x0036 }
        r1 = (com.fasterxml.jackson.databind.JsonDeserializer) r1;	 Catch:{ all -> 0x0036 }
        if (r1 == 0) goto L_0x0021;
    L_0x001e:
        monitor-exit(r3);	 Catch:{ all -> 0x0036 }
        r2 = r1;
        goto L_0x000b;
    L_0x0021:
        r2 = r5._createAndCache2(r6, r7, r8);	 Catch:{ all -> 0x0039 }
        if (r0 != 0) goto L_0x0034;
    L_0x0027:
        r4 = r5._incompleteDeserializers;	 Catch:{ all -> 0x0036 }
        r4 = r4.size();	 Catch:{ all -> 0x0036 }
        if (r4 <= 0) goto L_0x0034;
    L_0x002f:
        r4 = r5._incompleteDeserializers;	 Catch:{ all -> 0x0036 }
        r4.clear();	 Catch:{ all -> 0x0036 }
    L_0x0034:
        monitor-exit(r3);	 Catch:{ all -> 0x0036 }
        goto L_0x000b;
    L_0x0036:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0036 }
        throw r2;
    L_0x0039:
        r2 = move-exception;
        if (r0 != 0) goto L_0x0049;
    L_0x003c:
        r4 = r5._incompleteDeserializers;	 Catch:{ all -> 0x0036 }
        r4 = r4.size();	 Catch:{ all -> 0x0036 }
        if (r4 <= 0) goto L_0x0049;
    L_0x0044:
        r4 = r5._incompleteDeserializers;	 Catch:{ all -> 0x0036 }
        r4.clear();	 Catch:{ all -> 0x0036 }
    L_0x0049:
        throw r2;	 Catch:{ all -> 0x0036 }
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCacheValueDeserializer(com.fasterxml.jackson.databind.DeserializationContext, com.fasterxml.jackson.databind.deser.DeserializerFactory, com.fasterxml.jackson.databind.JavaType):com.fasterxml.jackson.databind.JsonDeserializer<java.lang.Object>");
    }

    protected final JsonDeserializer<Object> _createAndCache2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        try {
            JsonDeserializer<Object> deser = _createDeserializer(ctxt, factory, type);
            if (deser == null) {
                return null;
            }
            boolean addToCache;
            boolean isResolvable = deser instanceof ResolvableDeserializer;
            if (_hasCustomValueHandler(type) || !deser.isCachable()) {
                addToCache = false;
            } else {
                addToCache = true;
            }
            if (isResolvable) {
                this._incompleteDeserializers.put(type, deser);
                ((ResolvableDeserializer) deser).resolve(ctxt);
                this._incompleteDeserializers.remove(type);
            }
            if (!addToCache) {
                return deser;
            }
            this._cachedDeserializers.put(type, deser);
            return deser;
        } catch (Throwable iae) {
            throw JsonMappingException.from(ctxt, iae.getMessage(), iae);
        }
    }

    protected final JsonDeserializer<Object> _createDeserializer(DeserializationContext ctxt, DeserializerFactory factory, JavaType type) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
            type = factory.mapAbstractType(config, type);
        }
        BeanDescription beanDesc = config.introspect(type);
        JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (deser != null) {
            return deser;
        }
        JavaType newType = modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
        if (newType != type) {
            type = newType;
            beanDesc = config.introspect(newType);
        }
        Class<?> builder = beanDesc.findPOJOBuilder();
        if (builder != null) {
            return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
        }
        Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
        if (conv == null) {
            return _createDeserializer2(ctxt, factory, type, beanDesc);
        }
        JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
        }
        return new StdDelegatingDeserializer(conv, delegateType, _createDeserializer2(ctxt, factory, delegateType, beanDesc));
    }

    protected final JsonDeserializer<?> _createDeserializer2(DeserializationContext ctxt, DeserializerFactory factory, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        if (type.isEnumType()) {
            return factory.createEnumDeserializer(ctxt, type, beanDesc);
        }
        if (type.isContainerType()) {
            if (type.isArrayType()) {
                return factory.createArrayDeserializer(ctxt, (ArrayType) type, beanDesc);
            }
            if (type.isMapLikeType()) {
                MapLikeType mlt = (MapLikeType) type;
                if (mlt.isTrueMapType()) {
                    return factory.createMapDeserializer(ctxt, (MapType) mlt, beanDesc);
                }
                return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
            } else if (type.isCollectionLikeType()) {
                Value format = beanDesc.findExpectedFormat(null);
                if (format == null || format.getShape() != Shape.OBJECT) {
                    CollectionLikeType clt = (CollectionLikeType) type;
                    if (clt.isTrueCollectionType()) {
                        return factory.createCollectionDeserializer(ctxt, (CollectionType) clt, beanDesc);
                    }
                    return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
                }
            }
        }
        if (type.isReferenceType()) {
            return factory.createReferenceDeserializer(ctxt, (ReferenceType) type, beanDesc);
        }
        if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
            return factory.createTreeDeserializer(config, type, beanDesc);
        }
        return factory.createBeanDeserializer(ctxt, type, beanDesc);
    }

    protected final JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann) throws JsonMappingException {
        Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
        if (deserDef == null) {
            return null;
        }
        return findConvertingDeserializer(ctxt, ann, ctxt.deserializerInstance(ann, deserDef));
    }

    protected final JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, Annotated a, JsonDeserializer<Object> deser) throws JsonMappingException {
        Converter<Object, Object> conv = findConverter(ctxt, a);
        return conv == null ? deser : new StdDelegatingDeserializer(conv, conv.getInputType(ctxt.getTypeFactory()), deser);
    }

    protected final Converter<Object, Object> findConverter(DeserializationContext ctxt, Annotated a) throws JsonMappingException {
        Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
        if (convDef == null) {
            return null;
        }
        return ctxt.converterInstance(a, convDef);
    }

    private JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type) throws JsonMappingException {
        AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr == null) {
            return type;
        }
        if (type.isMapLikeType()) {
            JavaType keyType = type.getKeyType();
            if (keyType != null && keyType.getValueHandler() == null) {
                Object kdDef = intr.findKeyDeserializer(a);
                if (kdDef != null) {
                    KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
                    if (kd != null) {
                        type = ((MapLikeType) type).withKeyValueHandler(kd);
                        type.getKeyType();
                    }
                }
            }
        }
        JavaType contentType = type.getContentType();
        if (contentType != null && contentType.getValueHandler() == null) {
            Object cdDef = intr.findContentDeserializer(a);
            if (cdDef != null) {
                JsonDeserializer<?> cd = null;
                if (!(cdDef instanceof JsonDeserializer)) {
                    Class<?> cdClass = _verifyAsClass(cdDef, "findContentDeserializer", None.class);
                    if (cdClass != null) {
                        cd = ctxt.deserializerInstance(a, cdClass);
                    }
                }
                if (cd != null) {
                    type = type.withContentValueHandler(cd);
                }
            }
        }
        return intr.refineDeserializationType(ctxt.getConfig(), a, type);
    }

    private boolean _hasCustomValueHandler(JavaType t) {
        if (!t.isContainerType()) {
            return false;
        }
        JavaType ct = t.getContentType();
        if (ct == null) {
            return false;
        }
        if (ct.getValueHandler() == null && ct.getTypeHandler() == null) {
            return false;
        }
        return true;
    }

    private Class<?> _verifyAsClass(Object src, String methodName, Class<?> noneClass) {
        if (src == null) {
            return null;
        }
        if (src instanceof Class) {
            Class<?> cls = (Class) src;
            if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
                return null;
            }
            return cls;
        }
        throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
    }

    protected final JsonDeserializer<Object> _handleUnknownValueDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        if (!ClassUtil.isConcrete(type.getRawClass())) {
            ctxt.reportMappingException("Can not find a Value deserializer for abstract type %s", type);
        }
        ctxt.reportMappingException("Can not find a Value deserializer for type %s", type);
        return null;
    }

    protected final KeyDeserializer _handleUnknownKeyDeserializer(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        ctxt.reportMappingException("Can not find a (Map) Key deserializer for type %s", type);
        return null;
    }
}
