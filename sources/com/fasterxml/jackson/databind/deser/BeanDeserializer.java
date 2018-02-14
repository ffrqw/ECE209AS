package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanDeserializer extends BeanDeserializerBase implements Serializable {
    private static final long serialVersionUID = 1;
    protected transient Exception _nullFromCreator;

    static class BeanReferring extends Referring {
        private Object _bean;
        private final DeserializationContext _context;
        private final SettableBeanProperty _prop;

        BeanReferring(DeserializationContext ctxt, UnresolvedForwardReference ref, JavaType valueType, PropertyValueBuffer buffer, SettableBeanProperty prop) {
            super(ref, valueType);
            this._context = ctxt;
            this._prop = prop;
        }

        public void setBean(Object bean) {
            this._bean = bean;
        }

        public void handleResolvedForwardReference(Object id, Object value) throws IOException {
            if (this._bean == null) {
                this._context.reportMappingException("Can not resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", this._prop.getName(), this._prop.getDeclaringClass().getName());
            }
            this._prop.set(this._bean, value);
        }
    }

    public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
    }

    protected BeanDeserializer(BeanDeserializerBase src) {
        super(src, src._ignoreAllUnknown);
    }

    protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
        super(src, ignoreAllUnknown);
    }

    protected BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
        super(src, unwrapper);
    }

    public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
        super(src, oir);
    }

    public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps) {
        super(src, (Set) ignorableProps);
    }

    public BeanDeserializer(BeanDeserializerBase src, BeanPropertyMap props) {
        super(src, props);
    }

    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return getClass() != BeanDeserializer.class ? this : new BeanDeserializer((BeanDeserializerBase) this, unwrapper);
    }

    public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {
        return new BeanDeserializer((BeanDeserializerBase) this, oir);
    }

    public BeanDeserializer withIgnorableProperties(Set<String> ignorableProps) {
        return new BeanDeserializer((BeanDeserializerBase) this, (Set) ignorableProps);
    }

    public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
        return new BeanDeserializer((BeanDeserializerBase) this, props);
    }

    protected BeanDeserializerBase asArrayDeserializer() {
        return new BeanAsArrayDeserializer(this, this._beanProperties.getPropertiesInInsertionOrder());
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartObjectToken()) {
            return _deserializeOther(p, ctxt, p.getCurrentToken());
        }
        if (this._vanillaProcessing) {
            return vanillaDeserialize(p, ctxt, p.nextToken());
        }
        p.nextToken();
        if (this._objectIdReader != null) {
            return deserializeWithObjectId(p, ctxt);
        }
        return deserializeFromObject(p, ctxt);
    }

    protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
        switch (t) {
            case VALUE_STRING:
                return deserializeFromString(p, ctxt);
            case VALUE_NUMBER_INT:
                return deserializeFromNumber(p, ctxt);
            case VALUE_NUMBER_FLOAT:
                return deserializeFromDouble(p, ctxt);
            case VALUE_EMBEDDED_OBJECT:
                return deserializeFromEmbedded(p, ctxt);
            case VALUE_TRUE:
            case VALUE_FALSE:
                return deserializeFromBoolean(p, ctxt);
            case VALUE_NULL:
                return deserializeFromNull(p, ctxt);
            case START_ARRAY:
                return deserializeFromArray(p, ctxt);
            case FIELD_NAME:
            case END_OBJECT:
                if (this._vanillaProcessing) {
                    return vanillaDeserialize(p, ctxt, t);
                }
                if (this._objectIdReader != null) {
                    return deserializeWithObjectId(p, ctxt);
                }
                return deserializeFromObject(p, ctxt);
            default:
                return ctxt.handleUnexpectedToken(handledType(), p);
        }
    }

    @Deprecated
    protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
        throw ctxt.endOfInputException(handledType());
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        if (this._unwrappedPropertyHandler != null) {
            return deserializeWithUnwrapped(p, ctxt, bean);
        }
        if (this._externalTypeIdHandler != null) {
            return deserializeWithExternalTypeId(p, ctxt, bean);
        }
        String propName;
        if (p.isExpectedStartObjectToken()) {
            propName = p.nextFieldName();
            if (propName == null) {
                return bean;
            }
        } else if (!p.hasTokenId(5)) {
            return bean;
        } else {
            propName = p.getCurrentName();
        }
        if (this._needViewProcesing) {
            Class<?> view = ctxt.getActiveView();
            if (view != null) {
                return deserializeWithView(p, ctxt, bean, view);
            }
        }
        do {
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(p, ctxt, bean);
                } catch (Exception e) {
                    wrapAndThrow((Throwable) e, bean, propName, ctxt);
                }
            } else {
                handleUnknownVanilla(p, ctxt, bean, propName);
            }
            propName = p.nextFieldName();
        } while (propName != null);
        return bean;
    }

    private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (p.hasTokenId(5)) {
            String propName = p.getCurrentName();
            do {
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, bean, propName, ctxt);
                    }
                } else {
                    handleUnknownVanilla(p, ctxt, bean, propName);
                }
                propName = p.nextFieldName();
            } while (propName != null);
        }
        return bean;
    }

    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && p.hasTokenId(5) && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
            return deserializeFromObjectId(p, ctxt);
        }
        Object bean;
        if (!this._nonStandardCreation) {
            bean = this._valueInstantiator.createUsingDefault(ctxt);
            p.setCurrentValue(bean);
            if (p.canReadObjectId()) {
                Object id = p.getObjectId();
                if (id != null) {
                    _handleTypedObjectId(p, ctxt, bean, id);
                }
            }
            if (this._injectables != null) {
                injectValues(ctxt, bean);
            }
            if (this._needViewProcesing) {
                Class<?> view = ctxt.getActiveView();
                if (view != null) {
                    return deserializeWithView(p, ctxt, bean, view);
                }
            }
            if (!p.hasTokenId(5)) {
                return bean;
            }
            String propName = p.getCurrentName();
            do {
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, bean, propName, ctxt);
                    }
                } else {
                    handleUnknownVanilla(p, ctxt, bean, propName);
                }
                propName = p.nextFieldName();
            } while (propName != null);
            return bean;
        } else if (this._unwrappedPropertyHandler != null) {
            return deserializeWithUnwrapped(p, ctxt);
        } else {
            if (this._externalTypeIdHandler != null) {
                return deserializeWithExternalTypeId(p, ctxt);
            }
            bean = deserializeFromObjectUsingNonDefault(p, ctxt);
            if (this._injectables == null) {
                return bean;
            }
            injectValues(ctxt, bean);
            return bean;
        }
    }

    protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        TokenBuffer unknown = null;
        JsonToken t = p.getCurrentToken();
        List<BeanReferring> referrings = null;
        while (t == JsonToken.FIELD_NAME) {
            Object bean;
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
                    p.nextToken();
                    try {
                        bean = creator.build(ctxt, buffer);
                    } catch (Exception e) {
                        bean = wrapInstantiationProblem(e, ctxt);
                    }
                    if (bean == null) {
                        return ctxt.handleInstantiationProblem(handledType(), null, _creatorReturnedNullException());
                    }
                    p.setCurrentValue(bean);
                    if (bean.getClass() != this._beanType.getRawClass()) {
                        return handlePolymorphic(p, ctxt, bean, unknown);
                    }
                    if (unknown != null) {
                        bean = handleUnknownProperties(ctxt, bean, unknown);
                    }
                    return deserialize(p, ctxt, bean);
                }
            } else if (!buffer.readIdProperty(propName)) {
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
                    } catch (UnresolvedForwardReference reference) {
                        BeanReferring referring = handleUnresolvedReference(ctxt, prop, buffer, reference);
                        if (referrings == null) {
                            referrings = new ArrayList();
                        }
                        referrings.add(referring);
                    }
                } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    handleIgnoredProperty(p, ctxt, handledType(), propName);
                } else if (this._anySetter != null) {
                    try {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                    } catch (Exception e2) {
                        wrapAndThrow((Throwable) e2, (Object) this._beanType.getRawClass(), propName, ctxt);
                    }
                } else {
                    if (unknown == null) {
                        TokenBuffer tokenBuffer = new TokenBuffer(p, ctxt);
                    }
                    unknown.writeFieldName(propName);
                    unknown.copyCurrentStructure(p);
                }
            }
            t = p.nextToken();
        }
        try {
            bean = creator.build(ctxt, buffer);
        } catch (Exception e22) {
            wrapInstantiationProblem(e22, ctxt);
            bean = null;
        }
        if (referrings != null) {
            for (BeanReferring referring2 : referrings) {
                referring2.setBean(bean);
            }
        }
        if (unknown == null) {
            return bean;
        }
        if (bean.getClass() != this._beanType.getRawClass()) {
            return handlePolymorphic(null, ctxt, bean, unknown);
        }
        return handleUnknownProperties(ctxt, bean, unknown);
    }

    private BeanReferring handleUnresolvedReference(DeserializationContext ctxt, SettableBeanProperty prop, PropertyValueBuffer buffer, UnresolvedForwardReference reference) throws JsonMappingException {
        BeanReferring referring = new BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
        reference.getRoid().appendReferring(referring);
        return referring;
    }

    protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
        try {
            return prop.deserialize(p, ctxt);
        } catch (Exception e) {
            wrapAndThrow((Throwable) e, (Object) this._beanType.getRawClass(), prop.getName(), ctxt);
            return null;
        }
    }

    protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.requiresCustomCodec()) {
            return ctxt.handleUnexpectedToken(handledType(), p);
        }
        TokenBuffer tb = new TokenBuffer(p, ctxt);
        tb.writeEndObject();
        JsonParser p2 = tb.asParser(p);
        p2.nextToken();
        Object ob = this._vanillaProcessing ? vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : deserializeFromObject(p2, ctxt);
        p2.close();
        return ob;
    }

    protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
        if (p.hasTokenId(5)) {
            String propName = p.getCurrentName();
            do {
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop == null) {
                    handleUnknownVanilla(p, ctxt, bean, propName);
                } else if (prop.visibleInView(activeView)) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, bean, propName, ctxt);
                    }
                } else {
                    p.skipChildren();
                }
                propName = p.nextFieldName();
            } while (propName != null);
        }
        return bean;
    }

    protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
        }
        Class<?> activeView;
        String propName;
        TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        if (this._needViewProcesing) {
            activeView = ctxt.getActiveView();
        } else {
            activeView = null;
        }
        if (p.hasTokenId(5)) {
            propName = p.getCurrentName();
        } else {
            propName = null;
        }
        while (propName != null) {
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (activeView == null || prop.visibleInView(activeView)) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, bean, propName, ctxt);
                    }
                } else {
                    p.skipChildren();
                }
            } else if (this._ignorableProps == null || !this._ignorableProps.contains(propName)) {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
                    } catch (Exception e2) {
                        wrapAndThrow((Throwable) e2, bean, propName, ctxt);
                    }
                }
            } else {
                handleIgnoredProperty(p, ctxt, bean, propName);
            }
            propName = p.nextFieldName();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
        return bean;
    }

    protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            p.nextToken();
            if (prop != null) {
                if (activeView == null || prop.visibleInView(activeView)) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, bean, propName, ctxt);
                    }
                } else {
                    p.skipChildren();
                }
            } else if (this._ignorableProps == null || !this._ignorableProps.contains(propName)) {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
                if (this._anySetter != null) {
                    this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
                }
            } else {
                handleIgnoredProperty(p, ctxt, bean, propName);
            }
            t = p.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
        return bean;
    }

    protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        JsonToken t = p.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
                    Object bean;
                    t = p.nextToken();
                    try {
                        bean = creator.build(ctxt, buffer);
                    } catch (Exception e) {
                        bean = wrapInstantiationProblem(e, ctxt);
                    }
                    p.setCurrentValue(bean);
                    while (t == JsonToken.FIELD_NAME) {
                        p.nextToken();
                        tokens.copyCurrentStructure(p);
                        t = p.nextToken();
                    }
                    tokens.writeEndObject();
                    if (bean.getClass() == this._beanType.getRawClass()) {
                        return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
                    }
                    tokens.close();
                    ctxt.reportMappingException("Can not create polymorphic instances with unwrapped values", new Object[0]);
                    return null;
                }
            } else if (!buffer.readIdProperty(propName)) {
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
                } else if (this._ignorableProps == null || !this._ignorableProps.contains(propName)) {
                    tokens.writeFieldName(propName);
                    tokens.copyCurrentStructure(p);
                    if (this._anySetter != null) {
                        try {
                            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                        } catch (Exception e2) {
                            wrapAndThrow((Throwable) e2, (Object) this._beanType.getRawClass(), propName, ctxt);
                        }
                    }
                } else {
                    handleIgnoredProperty(p, ctxt, handledType(), propName);
                }
            }
            t = p.nextToken();
        }
        try {
            return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, creator.build(ctxt, buffer), tokens);
        } catch (Exception e22) {
            wrapInstantiationProblem(e22, ctxt);
            return null;
        }
    }

    protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
    }

    protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        JsonToken t = p.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            t = p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (t.isScalarValue()) {
                    ext.handleTypePropertyValue(p, ctxt, propName, bean);
                }
                if (activeView == null || prop.visibleInView(activeView)) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, bean, propName, ctxt);
                    }
                } else {
                    p.skipChildren();
                }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                handleIgnoredProperty(p, ctxt, bean, propName);
            } else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
                    } catch (Exception e2) {
                        wrapAndThrow((Throwable) e2, bean, propName, ctxt);
                    }
                } else {
                    handleUnknownProperty(p, ctxt, bean, propName);
                }
            }
            t = p.nextToken();
        }
        return ext.complete(p, ctxt, bean);
    }

    protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
        ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        JsonToken t = p.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (!ext.handlePropertyValue(p, ctxt, propName, null) && buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
                    t = p.nextToken();
                    try {
                        Object bean = creator.build(ctxt, buffer);
                        while (t == JsonToken.FIELD_NAME) {
                            p.nextToken();
                            tokens.copyCurrentStructure(p);
                            t = p.nextToken();
                        }
                        if (bean.getClass() == this._beanType.getRawClass()) {
                            return ext.complete(p, ctxt, bean);
                        }
                        ctxt.reportMappingException("Can not create polymorphic instances with external type ids", new Object[0]);
                        return null;
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, (Object) this._beanType.getRawClass(), propName, ctxt);
                    }
                }
            } else if (!buffer.readIdProperty(propName)) {
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                } else if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
                    if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                        handleIgnoredProperty(p, ctxt, handledType(), propName);
                    } else if (this._anySetter != null) {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                    }
                }
            }
            t = p.nextToken();
        }
        try {
            return ext.complete(p, ctxt, buffer, creator);
        } catch (Exception e2) {
            return wrapInstantiationProblem(e2, ctxt);
        }
    }

    protected Exception _creatorReturnedNullException() {
        if (this._nullFromCreator == null) {
            this._nullFromCreator = new NullPointerException("JSON Creator returned null");
        }
        return this._nullFromCreator;
    }
}
