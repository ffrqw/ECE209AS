package com.fasterxml.jackson.databind.ser.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;

public abstract class FilteredBeanPropertyWriter {

    private static final class MultiView extends BeanPropertyWriter implements Serializable {
        private static final long serialVersionUID = 1;
        protected final BeanPropertyWriter _delegate;
        protected final Class<?>[] _views;

        protected MultiView(BeanPropertyWriter delegate, Class<?>[] views) {
            super(delegate);
            this._delegate = delegate;
            this._views = views;
        }

        public final MultiView rename(NameTransformer transformer) {
            return new MultiView(this._delegate.rename(transformer), this._views);
        }

        public final void assignSerializer(JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }

        public final void assignNullSerializer(JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }

        public final void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception {
            Class<?> activeView = prov.getActiveView();
            if (activeView != null) {
                int i = 0;
                int len = this._views.length;
                while (i < len && !this._views[i].isAssignableFrom(activeView)) {
                    i++;
                }
                if (i == len) {
                    this._delegate.serializeAsOmittedField(bean, jgen, prov);
                    return;
                }
            }
            this._delegate.serializeAsField(bean, jgen, prov);
        }

        public final void serializeAsElement(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception {
            Class<?> activeView = prov.getActiveView();
            if (activeView != null) {
                int i = 0;
                int len = this._views.length;
                while (i < len && !this._views[i].isAssignableFrom(activeView)) {
                    i++;
                }
                if (i == len) {
                    this._delegate.serializeAsPlaceholder(bean, jgen, prov);
                    return;
                }
            }
            this._delegate.serializeAsElement(bean, jgen, prov);
        }

        public final void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
            Class<?> activeView = provider.getActiveView();
            if (activeView != null) {
                int i = 0;
                int len = this._views.length;
                while (i < len && !this._views[i].isAssignableFrom(activeView)) {
                    i++;
                }
                if (i == len) {
                    return;
                }
            }
            super.depositSchemaProperty(v, provider);
        }
    }

    private static final class SingleView extends BeanPropertyWriter implements Serializable {
        private static final long serialVersionUID = 1;
        protected final BeanPropertyWriter _delegate;
        protected final Class<?> _view;

        protected SingleView(BeanPropertyWriter delegate, Class<?> view) {
            super(delegate);
            this._delegate = delegate;
            this._view = view;
        }

        public final SingleView rename(NameTransformer transformer) {
            return new SingleView(this._delegate.rename(transformer), this._view);
        }

        public final void assignSerializer(JsonSerializer<Object> ser) {
            this._delegate.assignSerializer(ser);
        }

        public final void assignNullSerializer(JsonSerializer<Object> nullSer) {
            this._delegate.assignNullSerializer(nullSer);
        }

        public final void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception {
            Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsField(bean, jgen, prov);
            } else {
                this._delegate.serializeAsOmittedField(bean, jgen, prov);
            }
        }

        public final void serializeAsElement(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception {
            Class<?> activeView = prov.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                this._delegate.serializeAsElement(bean, jgen, prov);
            } else {
                this._delegate.serializeAsPlaceholder(bean, jgen, prov);
            }
        }

        public final void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
            Class<?> activeView = provider.getActiveView();
            if (activeView == null || this._view.isAssignableFrom(activeView)) {
                super.depositSchemaProperty(v, provider);
            }
        }
    }

    public static BeanPropertyWriter constructViewBased(BeanPropertyWriter base, Class<?>[] viewsToIncludeIn) {
        if (viewsToIncludeIn.length == 1) {
            return new SingleView(base, viewsToIncludeIn[0]);
        }
        return new MultiView(base, viewsToIncludeIn);
    }
}
