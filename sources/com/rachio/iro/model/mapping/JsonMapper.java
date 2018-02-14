package com.rachio.iro.model.mapping;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rachio.iro.IroApplication;
import com.rachio.iro.model.TransmittableView;

public class JsonMapper {
    private static final ObjectMapper genericMapper = createObjectMapper(true);
    private static final ObjectMapper laxObjectMapper = createObjectMapper(false);
    public static final ObjectMapper transmitMapper;
    private static final ObjectMapper viewMapper;

    private static ObjectMapper createObjectMapper(boolean unknownsNG) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        if (unknownsNG) {
            boolean z = IroApplication.DANIELMODE;
        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true);
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        return objectMapper;
    }

    public static ObjectMapper createMapperForRestClient() {
        ObjectMapper objectMapper = createObjectMapper(true);
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        serializationConfig.withView(TransmittableView.class);
        objectMapper.setConfig(serializationConfig);
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        return objectMapper;
    }

    public static ObjectMapper createMapperForPubNub() {
        ObjectMapper objectMapper = createObjectMapper(true);
        objectMapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        return objectMapper;
    }

    static {
        ObjectMapper createObjectMapper = createObjectMapper(true);
        viewMapper = createObjectMapper;
        createObjectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        createObjectMapper = createObjectMapper(true);
        transmitMapper = createObjectMapper;
        createObjectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        transmitMapper.getSerializationConfig().withView(TransmittableView.class);
    }

    public static String toJson(Object object) {
        try {
            return genericMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object object, Class view) {
        try {
            return viewMapper.writerWithView(view).writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonPretty(Object object) {
        try {
            return genericMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> toClass) {
        return fromJson(json, toClass, false);
    }

    public static <T> T fromJson(String json, Class<T> toClass, boolean useLaxMapper) {
        if (!useLaxMapper) {
            return genericMapper.readValue(json, (Class) toClass);
        }
        try {
            return laxObjectMapper.readValue(json, (Class) toClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
