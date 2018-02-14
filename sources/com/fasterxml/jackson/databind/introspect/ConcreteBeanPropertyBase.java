package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

public abstract class ConcreteBeanPropertyBase implements BeanProperty, Serializable {
    private static final long serialVersionUID = 1;
    protected final PropertyMetadata _metadata;
    protected transient Value _propertyFormat;

    protected ConcreteBeanPropertyBase(PropertyMetadata md) {
        if (md == null) {
            md = PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }
        this._metadata = md;
    }

    protected ConcreteBeanPropertyBase(ConcreteBeanPropertyBase src) {
        this._metadata = src._metadata;
        this._propertyFormat = src._propertyFormat;
    }

    public boolean isRequired() {
        return this._metadata.isRequired();
    }

    public PropertyMetadata getMetadata() {
        return this._metadata;
    }

    public boolean isVirtual() {
        return false;
    }

    @Deprecated
    public final Value findFormatOverrides(AnnotationIntrospector intr) {
        Value f = null;
        if (intr != null) {
            AnnotatedMember member = getMember();
            if (member != null) {
                f = intr.findFormat(member);
            }
        }
        if (f == null) {
            return EMPTY_FORMAT;
        }
        return f;
    }

    public Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType) {
        Value v = this._propertyFormat;
        if (v == null) {
            Value v1 = config.getDefaultPropertyFormat(baseType);
            Value v2 = null;
            AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr != null) {
                AnnotatedMember member = getMember();
                if (member != null) {
                    v2 = intr.findFormat(member);
                }
            }
            if (v1 != null) {
                v = v2 == null ? v1 : v1.withOverrides(v2);
            } else if (v2 == null) {
                v = EMPTY_FORMAT;
            } else {
                v = v2;
            }
            this._propertyFormat = v;
        }
        return v;
    }

    public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType) {
        JsonInclude.Value v0 = config.getDefaultPropertyInclusion(baseType);
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        AnnotatedMember member = getMember();
        if (intr == null || member == null) {
            return v0;
        }
        JsonInclude.Value v = intr.findPropertyInclusion(member);
        return v != null ? v0.withOverrides(v) : v0;
    }
}
