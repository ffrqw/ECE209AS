package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.ArrayIterator;
import java.io.Serializable;

public final class SerializerFactoryConfig implements Serializable {
    protected static final BeanSerializerModifier[] NO_MODIFIERS = new BeanSerializerModifier[0];
    protected static final Serializers[] NO_SERIALIZERS = new Serializers[0];
    private static final long serialVersionUID = 1;
    protected final Serializers[] _additionalKeySerializers;
    protected final Serializers[] _additionalSerializers;
    protected final BeanSerializerModifier[] _modifiers;

    public SerializerFactoryConfig() {
        this(null, null, null);
    }

    protected SerializerFactoryConfig(Serializers[] allAdditionalSerializers, Serializers[] allAdditionalKeySerializers, BeanSerializerModifier[] modifiers) {
        if (allAdditionalSerializers == null) {
            allAdditionalSerializers = NO_SERIALIZERS;
        }
        this._additionalSerializers = allAdditionalSerializers;
        if (allAdditionalKeySerializers == null) {
            allAdditionalKeySerializers = NO_SERIALIZERS;
        }
        this._additionalKeySerializers = allAdditionalKeySerializers;
        if (modifiers == null) {
            modifiers = NO_MODIFIERS;
        }
        this._modifiers = modifiers;
    }

    public final SerializerFactoryConfig withAdditionalSerializers(Serializers additional) {
        if (additional != null) {
            return new SerializerFactoryConfig((Serializers[]) ArrayBuilders.insertInListNoDup(this._additionalSerializers, additional), this._additionalKeySerializers, this._modifiers);
        }
        throw new IllegalArgumentException("Can not pass null Serializers");
    }

    public final SerializerFactoryConfig withAdditionalKeySerializers(Serializers additional) {
        if (additional == null) {
            throw new IllegalArgumentException("Can not pass null Serializers");
        }
        return new SerializerFactoryConfig(this._additionalSerializers, (Serializers[]) ArrayBuilders.insertInListNoDup(this._additionalKeySerializers, additional), this._modifiers);
    }

    public final SerializerFactoryConfig withSerializerModifier(BeanSerializerModifier modifier) {
        if (modifier == null) {
            throw new IllegalArgumentException("Can not pass null modifier");
        }
        return new SerializerFactoryConfig(this._additionalSerializers, this._additionalKeySerializers, (BeanSerializerModifier[]) ArrayBuilders.insertInListNoDup(this._modifiers, modifier));
    }

    public final boolean hasSerializers() {
        return this._additionalSerializers.length > 0;
    }

    public final boolean hasKeySerializers() {
        return this._additionalKeySerializers.length > 0;
    }

    public final boolean hasSerializerModifiers() {
        return this._modifiers.length > 0;
    }

    public final Iterable<Serializers> serializers() {
        return new ArrayIterator(this._additionalSerializers);
    }

    public final Iterable<Serializers> keySerializers() {
        return new ArrayIterator(this._additionalKeySerializers);
    }

    public final Iterable<BeanSerializerModifier> serializerModifiers() {
        return new ArrayIterator(this._modifiers);
    }
}
