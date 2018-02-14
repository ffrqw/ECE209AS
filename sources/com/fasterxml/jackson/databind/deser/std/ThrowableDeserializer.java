package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;

public class ThrowableDeserializer extends BeanDeserializer {
    protected static final String PROP_NAME_MESSAGE = "message";
    private static final long serialVersionUID = 1;

    public ThrowableDeserializer(BeanDeserializer baseDeserializer) {
        super(baseDeserializer);
        this._vanillaProcessing = false;
    }

    protected ThrowableDeserializer(BeanDeserializer src, NameTransformer unwrapper) {
        super((BeanDeserializerBase) src, unwrapper);
    }

    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return getClass() != ThrowableDeserializer.class ? this : new ThrowableDeserializer(this, unwrapper);
    }

    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return _deserializeUsingPropertyBased(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._beanType.isAbstract()) {
            return ctxt.handleMissingInstantiator(handledType(), p, "abstract type (need to add/enable type information?)", new Object[0]);
        }
        boolean hasStringCreator = this._valueInstantiator.canCreateFromString();
        boolean hasDefaultCtor = this._valueInstantiator.canCreateUsingDefault();
        if (!hasStringCreator && !hasDefaultCtor) {
            return ctxt.handleMissingInstantiator(handledType(), p, "Throwable needs a default contructor, a single-String-arg constructor; or explicit @JsonCreator", new Object[0]);
        }
        Object throwable = null;
        Object[] pending = null;
        int pendingIx = 0;
        while (p.getCurrentToken() != JsonToken.END_OBJECT) {
            int len;
            int i;
            String propName = p.getCurrentName();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            p.nextToken();
            if (prop != null) {
                if (throwable != null) {
                    prop.deserializeAndSet(p, ctxt, throwable);
                } else {
                    if (pending == null) {
                        len = this._beanProperties.size();
                        pending = new Object[(len + len)];
                    }
                    int i2 = pendingIx + 1;
                    pending[pendingIx] = prop;
                    pendingIx = i2 + 1;
                    pending[i2] = prop.deserialize(p, ctxt);
                }
            } else if (PROP_NAME_MESSAGE.equals(propName) && hasStringCreator) {
                throwable = this._valueInstantiator.createFromString(ctxt, p.getText());
                if (pending != null) {
                    len = pendingIx;
                    for (i = 0; i < len; i += 2) {
                        pending[i].set(throwable, pending[i + 1]);
                    }
                    pending = null;
                }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                p.skipChildren();
            } else if (this._anySetter != null) {
                this._anySetter.deserializeAndSet(p, ctxt, throwable, propName);
            } else {
                handleUnknownProperty(p, ctxt, throwable, propName);
            }
            p.nextToken();
        }
        if (throwable != null) {
            return throwable;
        }
        if (hasStringCreator) {
            throwable = this._valueInstantiator.createFromString(ctxt, null);
        } else {
            throwable = this._valueInstantiator.createUsingDefault(ctxt);
        }
        if (pending == null) {
            return throwable;
        }
        len = pendingIx;
        for (i = 0; i < len; i += 2) {
            ((SettableBeanProperty) pending[i]).set(throwable, pending[i + 1]);
        }
        return throwable;
    }
}
