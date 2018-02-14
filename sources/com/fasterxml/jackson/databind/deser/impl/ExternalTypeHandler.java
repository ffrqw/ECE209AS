package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExternalTypeHandler {
    private final HashMap<String, Integer> _nameToPropertyIndex;
    private final ExtTypedProperty[] _properties;
    private final TokenBuffer[] _tokens;
    private final String[] _typeIds;

    public static class Builder {
        private final HashMap<String, Integer> _nameToPropertyIndex = new HashMap();
        private final ArrayList<ExtTypedProperty> _properties = new ArrayList();

        public void addExternal(SettableBeanProperty property, TypeDeserializer typeDeser) {
            Integer index = Integer.valueOf(this._properties.size());
            this._properties.add(new ExtTypedProperty(property, typeDeser));
            this._nameToPropertyIndex.put(property.getName(), index);
            this._nameToPropertyIndex.put(typeDeser.getPropertyName(), index);
        }

        public ExternalTypeHandler build(BeanPropertyMap otherProps) {
            int len = this._properties.size();
            ExtTypedProperty[] extProps = new ExtTypedProperty[len];
            for (int i = 0; i < len; i++) {
                ExtTypedProperty extProp = (ExtTypedProperty) this._properties.get(i);
                SettableBeanProperty typeProp = otherProps.find(extProp.getTypePropertyName());
                if (typeProp != null) {
                    extProp.linkTypeProperty(typeProp);
                }
                extProps[i] = extProp;
            }
            return new ExternalTypeHandler(extProps, this._nameToPropertyIndex, null, null);
        }

        @Deprecated
        public ExternalTypeHandler build() {
            return new ExternalTypeHandler((ExtTypedProperty[]) this._properties.toArray(new ExtTypedProperty[this._properties.size()]), this._nameToPropertyIndex, null, null);
        }
    }

    private static final class ExtTypedProperty {
        private final SettableBeanProperty _property;
        private final TypeDeserializer _typeDeserializer;
        private SettableBeanProperty _typeProperty;
        private final String _typePropertyName;

        public ExtTypedProperty(SettableBeanProperty property, TypeDeserializer typeDeser) {
            this._property = property;
            this._typeDeserializer = typeDeser;
            this._typePropertyName = typeDeser.getPropertyName();
        }

        public final void linkTypeProperty(SettableBeanProperty p) {
            this._typeProperty = p;
        }

        public final boolean hasTypePropertyName(String n) {
            return n.equals(this._typePropertyName);
        }

        public final boolean hasDefaultType() {
            return this._typeDeserializer.getDefaultImpl() != null;
        }

        public final String getDefaultTypeId() {
            Class<?> defaultType = this._typeDeserializer.getDefaultImpl();
            if (defaultType == null) {
                return null;
            }
            return this._typeDeserializer.getTypeIdResolver().idFromValueAndType(null, defaultType);
        }

        public final String getTypePropertyName() {
            return this._typePropertyName;
        }

        public final SettableBeanProperty getProperty() {
            return this._property;
        }

        public final SettableBeanProperty getTypeProperty() {
            return this._typeProperty;
        }
    }

    protected ExternalTypeHandler(ExtTypedProperty[] properties, HashMap<String, Integer> nameToPropertyIndex, String[] typeIds, TokenBuffer[] tokens) {
        this._properties = properties;
        this._nameToPropertyIndex = nameToPropertyIndex;
        this._typeIds = typeIds;
        this._tokens = tokens;
    }

    protected ExternalTypeHandler(ExternalTypeHandler h) {
        this._properties = h._properties;
        this._nameToPropertyIndex = h._nameToPropertyIndex;
        int len = this._properties.length;
        this._typeIds = new String[len];
        this._tokens = new TokenBuffer[len];
    }

    public ExternalTypeHandler start() {
        return new ExternalTypeHandler(this);
    }

    public boolean handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean) throws IOException {
        Integer I = (Integer) this._nameToPropertyIndex.get(propName);
        if (I == null) {
            return false;
        }
        int index = I.intValue();
        if (!this._properties[index].hasTypePropertyName(propName)) {
            return false;
        }
        boolean canDeserialize;
        String typeId = p.getText();
        if (bean == null || this._tokens[index] == null) {
            canDeserialize = false;
        } else {
            canDeserialize = true;
        }
        if (canDeserialize) {
            _deserializeAndSet(p, ctxt, bean, index, typeId);
            this._tokens[index] = null;
        } else {
            this._typeIds[index] = typeId;
        }
        return true;
    }

    public boolean handlePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean) throws IOException {
        Integer I = (Integer) this._nameToPropertyIndex.get(propName);
        if (I == null) {
            return false;
        }
        boolean canDeserialize;
        int index = I.intValue();
        if (this._properties[index].hasTypePropertyName(propName)) {
            this._typeIds[index] = p.getText();
            p.skipChildren();
            if (bean == null || this._tokens[index] == null) {
                canDeserialize = false;
            } else {
                canDeserialize = true;
            }
        } else {
            TokenBuffer tokens = new TokenBuffer(p, ctxt);
            tokens.copyCurrentStructure(p);
            this._tokens[index] = tokens;
            canDeserialize = (bean == null || this._typeIds[index] == null) ? false : true;
        }
        if (canDeserialize) {
            String typeId = this._typeIds[index];
            this._typeIds[index] = null;
            _deserializeAndSet(p, ctxt, bean, index, typeId);
            this._tokens[index] = null;
        }
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object complete(com.fasterxml.jackson.core.JsonParser r15, com.fasterxml.jackson.databind.DeserializationContext r16, java.lang.Object r17) throws java.io.IOException {
        /*
        r14 = this;
        r5 = 0;
        r1 = r14._properties;
        r9 = r1.length;
    L_0x0004:
        if (r5 >= r9) goto L_0x00ad;
    L_0x0006:
        r1 = r14._typeIds;
        r6 = r1[r5];
        if (r6 != 0) goto L_0x0073;
    L_0x000c:
        r1 = r14._tokens;
        r13 = r1[r5];
        if (r13 == 0) goto L_0x003e;
    L_0x0012:
        r12 = r13.firstToken();
        if (r12 == 0) goto L_0x0060;
    L_0x0018:
        r1 = r12.isScalarValue();
        if (r1 == 0) goto L_0x0060;
    L_0x001e:
        r7 = r13.asParser(r15);
        r7.nextToken();
        r1 = r14._properties;
        r1 = r1[r5];
        r8 = r1.getProperty();
        r1 = r8.getType();
        r0 = r16;
        r11 = com.fasterxml.jackson.databind.jsontype.TypeDeserializer.deserializeIfNatural(r7, r0, r1);
        if (r11 == 0) goto L_0x0041;
    L_0x0039:
        r0 = r17;
        r8.set(r0, r11);
    L_0x003e:
        r5 = r5 + 1;
        goto L_0x0004;
    L_0x0041:
        r1 = r14._properties;
        r1 = r1[r5];
        r1 = r1.hasDefaultType();
        if (r1 != 0) goto L_0x006a;
    L_0x004b:
        r1 = "Missing external type id property '%s'";
        r2 = 1;
        r2 = new java.lang.Object[r2];
        r3 = 0;
        r4 = r14._properties;
        r4 = r4[r5];
        r4 = r4.getTypePropertyName();
        r2[r3] = r4;
        r0 = r16;
        r0.reportMappingException(r1, r2);
    L_0x0060:
        r1 = r14;
        r2 = r15;
        r3 = r16;
        r4 = r17;
        r1._deserializeAndSet(r2, r3, r4, r5, r6);
        goto L_0x003e;
    L_0x006a:
        r1 = r14._properties;
        r1 = r1[r5];
        r6 = r1.getDefaultTypeId();
        goto L_0x0060;
    L_0x0073:
        r1 = r14._tokens;
        r1 = r1[r5];
        if (r1 != 0) goto L_0x0060;
    L_0x0079:
        r1 = r14._properties;
        r1 = r1[r5];
        r10 = r1.getProperty();
        r1 = r10.isRequired();
        if (r1 != 0) goto L_0x0091;
    L_0x0087:
        r1 = com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY;
        r0 = r16;
        r1 = r0.isEnabled(r1);
        if (r1 == 0) goto L_0x00ad;
    L_0x0091:
        r1 = "Missing property '%s' for external type id '%s'";
        r2 = 2;
        r2 = new java.lang.Object[r2];
        r3 = 0;
        r4 = r10.getName();
        r2[r3] = r4;
        r3 = 1;
        r4 = r14._properties;
        r4 = r4[r5];
        r4 = r4.getTypePropertyName();
        r2[r3] = r4;
        r0 = r16;
        r0.reportMappingException(r1, r2);
    L_0x00ad:
        return r17;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.complete(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, java.lang.Object):java.lang.Object");
    }

    public Object complete(JsonParser p, DeserializationContext ctxt, PropertyValueBuffer buffer, PropertyBasedCreator creator) throws IOException {
        int i;
        int len = this._properties.length;
        Object[] values = new Object[len];
        for (i = 0; i < len; i++) {
            String typeId = this._typeIds[i];
            ExtTypedProperty extProp = this._properties[i];
            DeserializationContext deserializationContext;
            if (typeId == null) {
                if (this._tokens[i] == null) {
                } else if (extProp.hasDefaultType()) {
                    typeId = extProp.getDefaultTypeId();
                } else {
                    deserializationContext = ctxt;
                    deserializationContext.reportMappingException("Missing external type id property '%s'", extProp.getTypePropertyName());
                }
            } else if (this._tokens[i] == null) {
                deserializationContext = ctxt;
                deserializationContext.reportMappingException("Missing property '%s' for external type id '%s'", extProp.getProperty().getName(), this._properties[i].getTypePropertyName());
            }
            values[i] = _deserialize(p, ctxt, i, typeId);
            SettableBeanProperty prop = extProp.getProperty();
            if (prop.getCreatorIndex() >= 0) {
                buffer.assignParameter(prop, values[i]);
                SettableBeanProperty typeProp = extProp.getTypeProperty();
                if (typeProp != null && typeProp.getCreatorIndex() >= 0) {
                    buffer.assignParameter(typeProp, typeId);
                }
            }
        }
        Object bean = creator.build(ctxt, buffer);
        for (i = 0; i < len; i++) {
            prop = this._properties[i].getProperty();
            if (prop.getCreatorIndex() < 0) {
                prop.set(bean, values[i]);
            }
        }
        return bean;
    }

    protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, int index, String typeId) throws IOException {
        JsonParser p2 = this._tokens[index].asParser(p);
        if (p2.nextToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        TokenBuffer merged = new TokenBuffer(p, ctxt);
        merged.writeStartArray();
        merged.writeString(typeId);
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        JsonParser mp = merged.asParser(p);
        mp.nextToken();
        return this._properties[index].getProperty().deserialize(mp, ctxt);
    }

    protected final void _deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, int index, String typeId) throws IOException {
        JsonParser p2 = this._tokens[index].asParser(p);
        if (p2.nextToken() == JsonToken.VALUE_NULL) {
            this._properties[index].getProperty().set(bean, null);
            return;
        }
        TokenBuffer merged = new TokenBuffer(p, ctxt);
        merged.writeStartArray();
        merged.writeString(typeId);
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        JsonParser mp = merged.asParser(p);
        mp.nextToken();
        this._properties[index].getProperty().deserializeAndSet(mp, ctxt, bean);
    }
}
