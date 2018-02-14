package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class BuilderBasedDeserializer extends BeanDeserializerBase {
    private static final long serialVersionUID = 1;
    protected final AnnotatedMethod _buildMethod;

    public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
        this._buildMethod = builder.getBuildMethod();
        if (this._objectIdReader != null) {
            throw new IllegalArgumentException("Can not use Object Id with Builder-based deserialization (type " + beanDesc.getType() + ")");
        }
    }

    protected BuilderBasedDeserializer(BuilderBasedDeserializer src) {
        this(src, src._ignoreAllUnknown);
    }

    protected BuilderBasedDeserializer(BuilderBasedDeserializer src, boolean ignoreAllUnknown) {
        super((BeanDeserializerBase) src, ignoreAllUnknown);
        this._buildMethod = src._buildMethod;
    }

    protected BuilderBasedDeserializer(BuilderBasedDeserializer src, NameTransformer unwrapper) {
        super((BeanDeserializerBase) src, unwrapper);
        this._buildMethod = src._buildMethod;
    }

    public BuilderBasedDeserializer(BuilderBasedDeserializer src, ObjectIdReader oir) {
        super((BeanDeserializerBase) src, oir);
        this._buildMethod = src._buildMethod;
    }

    public BuilderBasedDeserializer(BuilderBasedDeserializer src, Set<String> ignorableProps) {
        super((BeanDeserializerBase) src, (Set) ignorableProps);
        this._buildMethod = src._buildMethod;
    }

    public BuilderBasedDeserializer(BuilderBasedDeserializer src, BeanPropertyMap props) {
        super((BeanDeserializerBase) src, props);
        this._buildMethod = src._buildMethod;
    }

    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return new BuilderBasedDeserializer(this, unwrapper);
    }

    public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
        return new BuilderBasedDeserializer(this, oir);
    }

    public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
        return new BuilderBasedDeserializer(this, (Set) ignorableProps);
    }

    public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
        return new BuilderBasedDeserializer(this, props);
    }

    protected BeanDeserializerBase asArrayDeserializer() {
        return new BeanAsArrayBuilderDeserializer(this, this._beanProperties.getPropertiesInInsertionOrder(), this._buildMethod);
    }

    protected final Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
        if (this._buildMethod == null) {
            return builder;
        }
        try {
            return this._buildMethod.getMember().invoke(builder, new Object[0]);
        } catch (Exception e) {
            return wrapInstantiationProblem(e, ctxt);
        }
    }

    public final Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
            if (this._vanillaProcessing) {
                return finishBuild(ctxt, vanillaDeserialize(p, ctxt, t));
            }
            return finishBuild(ctxt, deserializeFromObject(p, ctxt));
        }
        if (t != null) {
            switch (t) {
                case VALUE_STRING:
                    return finishBuild(ctxt, deserializeFromString(p, ctxt));
                case VALUE_NUMBER_INT:
                    return finishBuild(ctxt, deserializeFromNumber(p, ctxt));
                case VALUE_NUMBER_FLOAT:
                    return finishBuild(ctxt, deserializeFromDouble(p, ctxt));
                case VALUE_EMBEDDED_OBJECT:
                    return p.getEmbeddedObject();
                case VALUE_TRUE:
                case VALUE_FALSE:
                    return finishBuild(ctxt, deserializeFromBoolean(p, ctxt));
                case START_ARRAY:
                    return finishBuild(ctxt, deserializeFromArray(p, ctxt));
                case FIELD_NAME:
                case END_OBJECT:
                    return finishBuild(ctxt, deserializeFromObject(p, ctxt));
            }
        }
        return ctxt.handleUnexpectedToken(handledType(), p);
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object builder) throws IOException {
        return finishBuild(ctxt, _deserialize(p, ctxt, builder));
    }

    protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, Object builder) throws IOException, JsonProcessingException {
        if (this._injectables != null) {
            injectValues(ctxt, builder);
        }
        if (this._unwrappedPropertyHandler != null) {
            return deserializeWithUnwrapped(p, ctxt, builder);
        }
        if (this._externalTypeIdHandler != null) {
            return deserializeWithExternalTypeId(p, ctxt, builder);
        }
        if (this._needViewProcesing) {
            Class<?> view = ctxt.getActiveView();
            if (view != null) {
                return deserializeWithView(p, ctxt, builder, view);
            }
        }
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                } catch (Exception e) {
                    wrapAndThrow((Throwable) e, builder, propName, ctxt);
                }
            } else {
                handleUnknownVanilla(p, ctxt, handledType(), propName);
            }
            t = p.nextToken();
        }
        return builder;
    }

    private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException, JsonProcessingException {
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        while (p.getCurrentToken() != JsonToken.END_OBJECT) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                } catch (Exception e) {
                    wrapAndThrow((Throwable) e, bean, propName, ctxt);
                }
            } else {
                handleUnknownVanilla(p, ctxt, bean, propName);
            }
            p.nextToken();
        }
        return bean;
    }

    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (!this._nonStandardCreation) {
            Object bean = this._valueInstantiator.createUsingDefault(ctxt);
            if (this._injectables != null) {
                injectValues(ctxt, bean);
            }
            if (this._needViewProcesing) {
                Class<?> view = ctxt.getActiveView();
                if (view != null) {
                    return deserializeWithView(p, ctxt, bean, view);
                }
            }
            while (p.getCurrentToken() != JsonToken.END_OBJECT) {
                String propName = p.getCurrentName();
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, bean, propName, ctxt);
                    }
                } else {
                    handleUnknownVanilla(p, ctxt, bean, propName);
                }
                p.nextToken();
            }
            return bean;
        } else if (this._unwrappedPropertyHandler != null) {
            return deserializeWithUnwrapped(p, ctxt);
        } else {
            if (this._externalTypeIdHandler != null) {
                return deserializeWithExternalTypeId(p, ctxt);
            }
            return deserializeFromObjectUsingNonDefault(p, ctxt);
        }
    }

    protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Object bean;
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        TokenBuffer unknown = null;
        JsonToken t = p.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (creatorProp != null) {
                if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                    p.nextToken();
                    try {
                        bean = creator.build(ctxt, buffer);
                        if (bean.getClass() != this._beanType.getRawClass()) {
                            return handlePolymorphic(p, ctxt, bean, unknown);
                        }
                        if (unknown != null) {
                            bean = handleUnknownProperties(ctxt, bean, unknown);
                        }
                        return _deserialize(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, (Object) this._beanType.getRawClass(), propName, ctxt);
                    }
                } else {
                    continue;
                }
            } else if (!buffer.readIdProperty(propName)) {
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                    handleIgnoredProperty(p, ctxt, handledType(), propName);
                } else if (this._anySetter != null) {
                    buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                } else {
                    if (unknown == null) {
                        unknown = new TokenBuffer(p, ctxt);
                    }
                    unknown.writeFieldName(propName);
                    unknown.copyCurrentStructure(p);
                }
            }
            t = p.nextToken();
        }
        try {
            bean = creator.build(ctxt, buffer);
        } catch (Exception e2) {
            bean = wrapInstantiationProblem(e2, ctxt);
        }
        if (unknown == null) {
            return bean;
        }
        if (bean.getClass() != this._beanType.getRawClass()) {
            return handlePolymorphic(null, ctxt, bean, unknown);
        }
        return handleUnknownProperties(ctxt, bean, unknown);
    }

    protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException, JsonProcessingException {
        JsonToken t = p.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop == null) {
                handleUnknownVanilla(p, ctxt, bean, propName);
            } else if (prop.visibleInView(activeView)) {
                try {
                    bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                } catch (Exception e) {
                    wrapAndThrow((Throwable) e, bean, propName, ctxt);
                }
            } else {
                p.skipChildren();
            }
            t = p.nextToken();
        }
        return bean;
    }

    protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
        }
        TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (p.getCurrentToken() != JsonToken.END_OBJECT) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (activeView == null || prop.visibleInView(activeView)) {
                    try {
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
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
            p.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
        return bean;
    }

    protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
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
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
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

    protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
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
                if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                    t = p.nextToken();
                    try {
                        Object bean = creator.build(ctxt, buffer);
                        while (t == JsonToken.FIELD_NAME) {
                            p.nextToken();
                            tokens.copyCurrentStructure(p);
                            t = p.nextToken();
                        }
                        tokens.writeEndObject();
                        if (bean.getClass() == this._beanType.getRawClass()) {
                            return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
                        }
                        ctxt.reportMappingException("Can not create polymorphic instances with unwrapped values", new Object[0]);
                        return null;
                    } catch (Exception e) {
                        wrapAndThrow((Throwable) e, (Object) this._beanType.getRawClass(), propName, ctxt);
                    }
                } else {
                    continue;
                }
            } else if (!buffer.readIdProperty(propName)) {
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                } else if (this._ignorableProps == null || !this._ignorableProps.contains(propName)) {
                    tokens.writeFieldName(propName);
                    tokens.copyCurrentStructure(p);
                    if (this._anySetter != null) {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                    }
                } else {
                    handleIgnoredProperty(p, ctxt, handledType(), propName);
                }
            }
            t = p.nextToken();
        }
        try {
            return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, creator.build(ctxt, buffer), tokens);
        } catch (Exception e2) {
            return wrapInstantiationProblem(e2, ctxt);
        }
    }

    protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
        }
        return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
    }

    protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
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
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
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

    protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        throw new IllegalStateException("Deserialization with Builder, External type id, @JsonCreator not yet implemented");
    }
}
