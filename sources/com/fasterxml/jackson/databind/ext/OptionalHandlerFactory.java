package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import java.io.Serializable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class OptionalHandlerFactory implements Serializable {
    private static final Class<?> CLASS_DOM_DOCUMENT = Document.class;
    private static final Class<?> CLASS_DOM_NODE = Node.class;
    private static final String DESERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
    private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
    private static final String DESERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
    private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
    private static final String SERIALIZERS_FOR_JAVAX_XML = "com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
    private static final String SERIALIZER_FOR_DOM_NODE = "com.fasterxml.jackson.databind.ext.DOMSerializer";
    private static final Java7Support _jdk7Helper;
    public static final OptionalHandlerFactory instance = new OptionalHandlerFactory();
    private static final long serialVersionUID = 1;

    static {
        Java7Support x = null;
        try {
            x = Java7Support.instance();
        } catch (Throwable th) {
        }
        _jdk7Helper = x;
    }

    protected OptionalHandlerFactory() {
    }

    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        Class<?> rawType = type.getRawClass();
        if (_jdk7Helper != null) {
            JsonSerializer<?> ser = _jdk7Helper.getSerializerForJavaNioFilePath(rawType);
            if (ser != null) {
                return ser;
            }
        }
        if (CLASS_DOM_NODE != null && CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonSerializer) instantiate(SERIALIZER_FOR_DOM_NODE);
        }
        if (!rawType.getName().startsWith(PACKAGE_PREFIX_JAVAX_XML) && !hasSuperClassStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) {
            return null;
        }
        Object ob = instantiate(SERIALIZERS_FOR_JAVAX_XML);
        if (ob == null) {
            return null;
        }
        return ((Serializers) ob).findSerializer(config, type, beanDesc);
    }

    public JsonDeserializer<?> findDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        Class<?> rawType = type.getRawClass();
        if (_jdk7Helper != null) {
            JsonDeserializer<?> deser = _jdk7Helper.getDeserializerForJavaNioFilePath(rawType);
            if (deser != null) {
                return deser;
            }
        }
        if (CLASS_DOM_NODE != null && CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonDeserializer) instantiate(DESERIALIZER_FOR_DOM_NODE);
        }
        if (CLASS_DOM_DOCUMENT != null && CLASS_DOM_DOCUMENT.isAssignableFrom(rawType)) {
            return (JsonDeserializer) instantiate(DESERIALIZER_FOR_DOM_DOCUMENT);
        }
        if (!rawType.getName().startsWith(PACKAGE_PREFIX_JAVAX_XML) && !hasSuperClassStartingWith(rawType, PACKAGE_PREFIX_JAVAX_XML)) {
            return null;
        }
        Object ob = instantiate(DESERIALIZERS_FOR_JAVAX_XML);
        if (ob != null) {
            return ((Deserializers) ob).findBeanDeserializer(type, config, beanDesc);
        }
        return null;
    }

    private Object instantiate(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (LinkageError e) {
            return null;
        } catch (Exception e2) {
            return null;
        }
    }

    private boolean hasSuperClassStartingWith(Class<?> rawType, String prefix) {
        Class<?> supertype = rawType.getSuperclass();
        while (supertype != null && supertype != Object.class) {
            if (supertype.getName().startsWith(prefix)) {
                return true;
            }
            supertype = supertype.getSuperclass();
        }
        return false;
    }
}
