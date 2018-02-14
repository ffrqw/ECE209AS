package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualKeyDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.LinkedNode;
import com.fasterxml.jackson.databind.util.ObjectBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public abstract class DeserializationContext extends DatabindContext implements Serializable {
    private static final int MAX_ERROR_STR_LEN = 500;
    private static final long serialVersionUID = 1;
    protected transient ArrayBuilders _arrayBuilders;
    protected transient ContextAttributes _attributes;
    protected final DeserializerCache _cache;
    protected final DeserializationConfig _config;
    protected LinkedNode<JavaType> _currentType;
    protected transient DateFormat _dateFormat;
    protected final DeserializerFactory _factory;
    protected final int _featureFlags;
    protected final InjectableValues _injectableValues;
    protected transient ObjectBuffer _objectBuffer;
    protected transient JsonParser _parser;
    protected final Class<?> _view;

    public abstract void checkUnresolvedObjectId() throws UnresolvedForwardReference;

    public abstract JsonDeserializer<Object> deserializerInstance(Annotated annotated, Object obj) throws JsonMappingException;

    public abstract ReadableObjectId findObjectId(Object obj, ObjectIdGenerator<?> objectIdGenerator, ObjectIdResolver objectIdResolver);

    public abstract KeyDeserializer keyDeserializerInstance(Annotated annotated, Object obj) throws JsonMappingException;

    protected DeserializationContext(DeserializerFactory df) {
        this(df, null);
    }

    protected DeserializationContext(DeserializerFactory df, DeserializerCache cache) {
        if (df == null) {
            throw new IllegalArgumentException("Can not pass null DeserializerFactory");
        }
        this._factory = df;
        if (cache == null) {
            cache = new DeserializerCache();
        }
        this._cache = cache;
        this._featureFlags = 0;
        this._config = null;
        this._injectableValues = null;
        this._view = null;
        this._attributes = null;
    }

    protected DeserializationContext(DeserializationContext src, DeserializerFactory factory) {
        this._cache = src._cache;
        this._factory = factory;
        this._config = src._config;
        this._featureFlags = src._featureFlags;
        this._view = src._view;
        this._parser = src._parser;
        this._injectableValues = src._injectableValues;
        this._attributes = src._attributes;
    }

    protected DeserializationContext(DeserializationContext src, DeserializationConfig config, JsonParser p, InjectableValues injectableValues) {
        this._cache = src._cache;
        this._factory = src._factory;
        this._config = config;
        this._featureFlags = config.getDeserializationFeatures();
        this._view = config.getActiveView();
        this._parser = p;
        this._injectableValues = injectableValues;
        this._attributes = config.getAttributes();
    }

    protected DeserializationContext(DeserializationContext src) {
        this._cache = new DeserializerCache();
        this._factory = src._factory;
        this._config = src._config;
        this._featureFlags = src._featureFlags;
        this._view = src._view;
        this._injectableValues = null;
    }

    public DeserializationConfig getConfig() {
        return this._config;
    }

    public final Class<?> getActiveView() {
        return this._view;
    }

    public final boolean canOverrideAccessModifiers() {
        return this._config.canOverrideAccessModifiers();
    }

    public final boolean isEnabled(MapperFeature feature) {
        return this._config.isEnabled(feature);
    }

    public final Value getDefaultPropertyFormat(Class<?> baseType) {
        return this._config.getDefaultPropertyFormat(baseType);
    }

    public final AnnotationIntrospector getAnnotationIntrospector() {
        return this._config.getAnnotationIntrospector();
    }

    public final TypeFactory getTypeFactory() {
        return this._config.getTypeFactory();
    }

    public Locale getLocale() {
        return this._config.getLocale();
    }

    public TimeZone getTimeZone() {
        return this._config.getTimeZone();
    }

    public Object getAttribute(Object key) {
        return this._attributes.getAttribute(key);
    }

    public DeserializationContext setAttribute(Object key, Object value) {
        this._attributes = this._attributes.withPerCallAttribute(key, value);
        return this;
    }

    public JavaType getContextualType() {
        return this._currentType == null ? null : (JavaType) this._currentType.value();
    }

    public DeserializerFactory getFactory() {
        return this._factory;
    }

    public final boolean isEnabled(DeserializationFeature feat) {
        return (this._featureFlags & feat.getMask()) != 0;
    }

    public final int getDeserializationFeatures() {
        return this._featureFlags;
    }

    public final boolean hasDeserializationFeatures(int featureMask) {
        return (this._featureFlags & featureMask) == featureMask;
    }

    public final boolean hasSomeOfFeatures(int featureMask) {
        return (this._featureFlags & featureMask) != 0;
    }

    public final JsonParser getParser() {
        return this._parser;
    }

    public final Object findInjectableValue(Object valueId, BeanProperty forProperty, Object beanInstance) {
        if (this._injectableValues != null) {
            return this._injectableValues.findInjectableValue(valueId, this, forProperty, beanInstance);
        }
        throw new IllegalStateException("No 'injectableValues' configured, can not inject value with id [" + valueId + "]");
    }

    public final Base64Variant getBase64Variant() {
        return this._config.getBase64Variant();
    }

    public final JsonNodeFactory getNodeFactory() {
        return this._config.getNodeFactory();
    }

    public boolean hasValueDeserializerFor(JavaType type, AtomicReference<Throwable> cause) {
        try {
            return this._cache.hasValueDeserializerFor(this, this._factory, type);
        } catch (JsonMappingException e) {
            if (cause != null) {
                cause.set(e);
            }
            return false;
        } catch (RuntimeException e2) {
            if (cause == null) {
                throw e2;
            }
            cause.set(e2);
            return false;
        }
    }

    public final JsonDeserializer<Object> findContextualValueDeserializer(JavaType type, BeanProperty prop) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser != null) {
            return handleSecondaryContextualization(deser, prop, type);
        }
        return deser;
    }

    public final JsonDeserializer<Object> findNonContextualValueDeserializer(JavaType type) throws JsonMappingException {
        return this._cache.findValueDeserializer(this, this._factory, type);
    }

    public final JsonDeserializer<Object> findRootValueDeserializer(JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._cache.findValueDeserializer(this, this._factory, type);
        if (deser == null) {
            return null;
        }
        deser = handleSecondaryContextualization(deser, null, type);
        TypeDeserializer typeDeser = this._factory.findTypeDeserializer(this._config, type);
        return typeDeser != null ? new TypeWrappedDeserializer(typeDeser.forProperty(null), deser) : deser;
    }

    public final KeyDeserializer findKeyDeserializer(JavaType keyType, BeanProperty prop) throws JsonMappingException {
        KeyDeserializer kd = this._cache.findKeyDeserializer(this, this._factory, keyType);
        if (kd instanceof ContextualKeyDeserializer) {
            return ((ContextualKeyDeserializer) kd).createContextual(this, prop);
        }
        return kd;
    }

    public final JavaType constructType(Class<?> cls) {
        return this._config.constructType((Class) cls);
    }

    public Class<?> findClass(String className) throws ClassNotFoundException {
        return getTypeFactory().findClass(className);
    }

    public final ObjectBuffer leaseObjectBuffer() {
        ObjectBuffer buf = this._objectBuffer;
        if (buf == null) {
            return new ObjectBuffer();
        }
        this._objectBuffer = null;
        return buf;
    }

    public final void returnObjectBuffer(ObjectBuffer buf) {
        if (this._objectBuffer == null || buf.initialCapacity() >= this._objectBuffer.initialCapacity()) {
            this._objectBuffer = buf;
        }
    }

    public final ArrayBuilders getArrayBuilders() {
        if (this._arrayBuilders == null) {
            this._arrayBuilders = new ArrayBuilders();
        }
        return this._arrayBuilders;
    }

    public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
        if (deser instanceof ContextualDeserializer) {
            this._currentType = new LinkedNode(type, this._currentType);
            try {
                deser = ((ContextualDeserializer) deser).createContextual(this, prop);
            } finally {
                this._currentType = this._currentType.next();
            }
        }
        return deser;
    }

    public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop, JavaType type) throws JsonMappingException {
        if (deser instanceof ContextualDeserializer) {
            this._currentType = new LinkedNode(type, this._currentType);
            try {
                deser = ((ContextualDeserializer) deser).createContextual(this, prop);
            } finally {
                this._currentType = this._currentType.next();
            }
        }
        return deser;
    }

    @Deprecated
    public JsonDeserializer<?> handlePrimaryContextualization(JsonDeserializer<?> deser, BeanProperty prop) throws JsonMappingException {
        return handlePrimaryContextualization(deser, prop, TypeFactory.unknownType());
    }

    @Deprecated
    public JsonDeserializer<?> handleSecondaryContextualization(JsonDeserializer<?> deser, BeanProperty prop) throws JsonMappingException {
        if (deser instanceof ContextualDeserializer) {
            return ((ContextualDeserializer) deser).createContextual(this, prop);
        }
        return deser;
    }

    public Date parseDate(String dateStr) throws IllegalArgumentException {
        try {
            return getDateFormat().parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException(String.format("Failed to parse Date value '%s': %s", new Object[]{dateStr, e.getMessage()}));
        }
    }

    public Calendar constructCalendar(Date d) {
        Calendar c = Calendar.getInstance(getTimeZone());
        c.setTime(d);
        return c;
    }

    public <T> T readValue(JsonParser p, Class<T> type) throws IOException {
        return readValue(p, getTypeFactory().constructType((Type) type));
    }

    public <T> T readValue(JsonParser p, JavaType type) throws IOException {
        JsonDeserializer<Object> deser = findRootValueDeserializer(type);
        if (deser == null) {
            reportMappingException("Could not find JsonDeserializer for type %s", type);
        }
        return deser.deserialize(p, this);
    }

    public <T> T readPropertyValue(JsonParser p, BeanProperty prop, Class<T> type) throws IOException {
        return readPropertyValue(p, prop, getTypeFactory().constructType((Type) type));
    }

    public <T> T readPropertyValue(JsonParser p, BeanProperty prop, JavaType type) throws IOException {
        JsonDeserializer<Object> deser = findContextualValueDeserializer(type, prop);
        if (deser == null) {
            String propName = prop == null ? "NULL" : "'" + prop.getName() + "'";
            reportMappingException("Could not find JsonDeserializer for type %s (via property %s)", type, propName);
        }
        return deser.deserialize(p, this);
    }

    public boolean handleUnknownProperty(JsonParser p, JsonDeserializer<?> deser, Object instanceOrClass, String propName) throws IOException {
        for (LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers(); h != null; h = h.next()) {
            if (((DeserializationProblemHandler) h.value()).handleUnknownProperty(this, p, deser, instanceOrClass, propName)) {
                break;
            }
        }
        if (isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, propName, deser == null ? null : deser.getKnownPropertyNames());
        }
        p.skipChildren();
        return true;
    }

    public Object handleWeirdKey(Class<?> keyClass, String keyValue, String msg, Object... msgArgs) throws IOException {
        if (msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            Object key = ((DeserializationProblemHandler) h.value()).handleWeirdKey(this, keyClass, keyValue, msg);
            if (key == DeserializationProblemHandler.NOT_HANDLED) {
                h = h.next();
            } else if (key == null || keyClass.isInstance(key)) {
                return key;
            } else {
                throw weirdStringException(keyValue, keyClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[]{keyClass, key.getClass()}));
            }
        }
        throw weirdKeyException(keyClass, keyValue, msg);
    }

    public Object handleWeirdStringValue(Class<?> targetClass, String value, String msg, Object... msgArgs) throws IOException {
        if (msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            Object instance = ((DeserializationProblemHandler) h.value()).handleWeirdStringValue(this, targetClass, value, msg);
            if (instance == DeserializationProblemHandler.NOT_HANDLED) {
                h = h.next();
            } else if (instance == null || targetClass.isInstance(instance)) {
                return instance;
            } else {
                throw weirdStringException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdStringValue() for type %s returned value of type %s", new Object[]{targetClass, instance.getClass()}));
            }
        }
        throw weirdStringException(value, targetClass, msg);
    }

    public Object handleWeirdNumberValue(Class<?> targetClass, Number value, String msg, Object... msgArgs) throws IOException {
        if (msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            Object key = ((DeserializationProblemHandler) h.value()).handleWeirdNumberValue(this, targetClass, value, msg);
            if (key == DeserializationProblemHandler.NOT_HANDLED) {
                h = h.next();
            } else if (key == null || targetClass.isInstance(key)) {
                return key;
            } else {
                throw weirdNumberException(value, targetClass, String.format("DeserializationProblemHandler.handleWeirdNumberValue() for type %s returned value of type %s", new Object[]{targetClass, key.getClass()}));
            }
        }
        throw weirdNumberException(value, targetClass, msg);
    }

    public Object handleMissingInstantiator(Class<?> instClass, JsonParser p, String msg, Object... msgArgs) throws IOException {
        if (msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            Object instance = ((DeserializationProblemHandler) h.value()).handleMissingInstantiator(this, instClass, p, msg);
            if (instance == DeserializationProblemHandler.NOT_HANDLED) {
                h = h.next();
            } else if (instance == null || instClass.isInstance(instance)) {
                return instance;
            } else {
                throw instantiationException((Class) instClass, String.format("DeserializationProblemHandler.handleMissingInstantiator() for type %s returned value of type %s", new Object[]{instClass, instance.getClass()}));
            }
        }
        throw instantiationException((Class) instClass, msg);
    }

    public Object handleInstantiationProblem(Class<?> instClass, Object argument, Throwable t) throws IOException {
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            Object instance = ((DeserializationProblemHandler) h.value()).handleInstantiationProblem(this, instClass, argument, t);
            if (instance == DeserializationProblemHandler.NOT_HANDLED) {
                h = h.next();
            } else if (instance == null || instClass.isInstance(instance)) {
                return instance;
            } else {
                throw instantiationException((Class) instClass, String.format("DeserializationProblemHandler.handleInstantiationProblem() for type %s returned value of type %s", new Object[]{instClass, instance.getClass()}));
            }
        }
        if (t instanceof IOException) {
            throw ((IOException) t);
        }
        throw instantiationException((Class) instClass, t);
    }

    public Object handleUnexpectedToken(Class<?> instClass, JsonParser p) throws IOException {
        return handleUnexpectedToken(instClass, p.getCurrentToken(), p, null, new Object[0]);
    }

    public Object handleUnexpectedToken(Class<?> instClass, JsonToken t, JsonParser p, String msg, Object... msgArgs) throws IOException {
        if (msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        for (LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers(); h != null; h = h.next()) {
            Object instance = ((DeserializationProblemHandler) h.value()).handleUnexpectedToken(this, instClass, t, p, msg);
            if (instance != DeserializationProblemHandler.NOT_HANDLED) {
                if (instance == null || instClass.isInstance(instance)) {
                    return instance;
                }
                reportMappingException("DeserializationProblemHandler.handleUnexpectedToken() for type %s returned value of type %s", instClass, instance.getClass());
            }
        }
        if (msg == null) {
            if (t == null) {
                msg = String.format("Unexpected end-of-input when binding data into %s", new Object[]{_calcName(instClass)});
            } else {
                msg = String.format("Can not deserialize instance of %s out of %s token", new Object[]{_calcName(instClass), t});
            }
        }
        reportMappingException(msg, new Object[0]);
        return null;
    }

    public JavaType handleUnknownTypeId(JavaType baseType, String id, TypeIdResolver idResolver, String extraDesc) throws IOException {
        LinkedNode<DeserializationProblemHandler> h = this._config.getProblemHandlers();
        while (h != null) {
            JavaType type = ((DeserializationProblemHandler) h.value()).handleUnknownTypeId(this, baseType, id, idResolver, extraDesc);
            if (type == null) {
                h = h.next();
            } else if (type.hasRawClass(Void.class)) {
                return null;
            } else {
                if (type.isTypeOrSubTypeOf(baseType.getRawClass())) {
                    return type;
                }
                throw unknownTypeIdException(baseType, id, "problem handler tried to resolve into non-subtype: " + type);
            }
        }
        if (!isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
            return null;
        }
        throw unknownTypeIdException(baseType, id, extraDesc);
    }

    public void reportWrongTokenException(JsonParser p, JsonToken expToken, String msg, Object... msgArgs) throws JsonMappingException {
        if (msg != null && msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        throw wrongTokenException(p, expToken, msg);
    }

    @Deprecated
    public void reportUnknownProperty(Object instanceOrClass, String fieldName, JsonDeserializer<?> deser) throws JsonMappingException {
        if (isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
            throw UnrecognizedPropertyException.from(this._parser, instanceOrClass, fieldName, deser == null ? null : deser.getKnownPropertyNames());
        }
    }

    public void reportMappingException(String msg, Object... msgArgs) throws JsonMappingException {
        if (msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        throw JsonMappingException.from(getParser(), msg);
    }

    public void reportMissingContent(String msg, Object... msgArgs) throws JsonMappingException {
        if (msg == null) {
            msg = "No content to map due to end-of-input";
        } else if (msgArgs.length > 0) {
            msg = String.format(msg, msgArgs);
        }
        throw JsonMappingException.from(getParser(), msg);
    }

    public void reportUnresolvedObjectId(ObjectIdReader oidReader, Object bean) throws JsonMappingException {
        throw JsonMappingException.from(getParser(), String.format("No Object Id found for an instance of %s, to assign to property '%s'", new Object[]{bean.getClass().getName(), oidReader.propertyName}));
    }

    public <T> T reportBadTypeDefinition(BeanDescription bean, String message, Object... args) throws JsonMappingException {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        String beanDesc = bean == null ? "N/A" : _desc(bean.getType().getGenericSignature());
        throw mappingException("Invalid type definition for type %s: %s", beanDesc, message);
    }

    public <T> T reportBadPropertyDefinition(BeanDescription bean, BeanPropertyDefinition prop, String message, Object... args) throws JsonMappingException {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }
        String propName = prop == null ? "N/A" : _quotedString(prop.getName());
        String beanDesc = bean == null ? "N/A" : _desc(bean.getType().getGenericSignature());
        throw mappingException("Invalid definition for property %s (of type %s): %s", propName, beanDesc, message);
    }

    public JsonMappingException mappingException(String message) {
        return JsonMappingException.from(getParser(), message);
    }

    public JsonMappingException mappingException(String msgTemplate, Object... args) {
        if (args != null && args.length > 0) {
            msgTemplate = String.format(msgTemplate, args);
        }
        return JsonMappingException.from(getParser(), msgTemplate);
    }

    @Deprecated
    public JsonMappingException mappingException(Class<?> targetClass) {
        return mappingException((Class) targetClass, this._parser.getCurrentToken());
    }

    @Deprecated
    public JsonMappingException mappingException(Class<?> targetClass, JsonToken token) {
        String tokenDesc;
        if (token == null) {
            tokenDesc = "<end of input>";
        } else {
            tokenDesc = String.format("%s token", new Object[]{token});
        }
        return JsonMappingException.from(this._parser, String.format("Can not deserialize instance of %s out of %s", new Object[]{_calcName(targetClass), tokenDesc}));
    }

    public JsonMappingException wrongTokenException(JsonParser p, JsonToken expToken, String msg0) {
        String msg = String.format("Unexpected token (%s), expected %s", new Object[]{p.getCurrentToken(), expToken});
        if (msg0 != null) {
            msg = msg + ": " + msg0;
        }
        return JsonMappingException.from(p, msg);
    }

    public JsonMappingException weirdKeyException(Class<?> keyClass, String keyValue, String msg) {
        return InvalidFormatException.from(this._parser, String.format("Can not deserialize Map key of type %s from String %s: %s", new Object[]{keyClass.getName(), _quotedString(keyValue), msg}), keyValue, keyClass);
    }

    public JsonMappingException weirdStringException(String value, Class<?> instClass, String msg) {
        return InvalidFormatException.from(this._parser, String.format("Can not deserialize value of type %s from String %s: %s", new Object[]{instClass.getName(), _quotedString(value), msg}), value, instClass);
    }

    public JsonMappingException weirdNumberException(Number value, Class<?> instClass, String msg) {
        return InvalidFormatException.from(this._parser, String.format("Can not deserialize value of type %s from number %s: %s", new Object[]{instClass.getName(), String.valueOf(value), msg}), value, instClass);
    }

    public JsonMappingException instantiationException(Class<?> instClass, Throwable t) {
        return JsonMappingException.from(this._parser, String.format("Can not construct instance of %s, problem: %s", new Object[]{instClass.getName(), t.getMessage()}), t);
    }

    public JsonMappingException instantiationException(Class<?> instClass, String msg) {
        return JsonMappingException.from(this._parser, String.format("Can not construct instance of %s: %s", new Object[]{instClass.getName(), msg}));
    }

    public JsonMappingException unknownTypeIdException(JavaType baseType, String typeId, String extraDesc) {
        String msg = String.format("Could not resolve type id '%s' into a subtype of %s", new Object[]{typeId, baseType});
        if (extraDesc != null) {
            msg = msg + ": " + extraDesc;
        }
        return InvalidTypeIdException.from(this._parser, msg, baseType, typeId);
    }

    @Deprecated
    public JsonMappingException unknownTypeException(JavaType type, String id, String extraDesc) {
        String msg = String.format("Could not resolve type id '%s' into a subtype of %s", new Object[]{id, type});
        if (extraDesc != null) {
            msg = msg + ": " + extraDesc;
        }
        return JsonMappingException.from(this._parser, msg);
    }

    @Deprecated
    public JsonMappingException endOfInputException(Class<?> instClass) {
        return JsonMappingException.from(this._parser, "Unexpected end-of-input when trying to deserialize a " + instClass.getName());
    }

    protected DateFormat getDateFormat() {
        if (this._dateFormat != null) {
            return this._dateFormat;
        }
        DateFormat df = (DateFormat) this._config.getDateFormat().clone();
        this._dateFormat = df;
        return df;
    }

    protected String determineClassName(Object instance) {
        return ClassUtil.getClassDescription(instance);
    }

    protected String _calcName(Class<?> cls) {
        if (cls.isArray()) {
            return _calcName(cls.getComponentType()) + "[]";
        }
        return cls.getName();
    }

    protected String _valueDesc() {
        try {
            return _desc(this._parser.getText());
        } catch (Exception e) {
            return "[N/A]";
        }
    }

    protected String _desc(String desc) {
        if (desc == null) {
            return "[N/A]";
        }
        if (desc.length() > MAX_ERROR_STR_LEN) {
            desc = desc.substring(0, MAX_ERROR_STR_LEN) + "]...[" + desc.substring(desc.length() - 500);
        }
        return desc;
    }

    protected String _quotedString(String desc) {
        if (desc == null) {
            return "[N/A]";
        }
        if (desc.length() <= MAX_ERROR_STR_LEN) {
            return "\"" + desc + "\"";
        }
        return String.format("\"%s]...[%s\"", new Object[]{desc.substring(0, MAX_ERROR_STR_LEN), desc.substring(desc.length() - 500)});
    }
}
