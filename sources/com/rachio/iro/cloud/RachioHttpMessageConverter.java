package com.rachio.iro.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rachio.iro.model.mapping.JsonMapper;
import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public class RachioHttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private static final String TAG = RachioHttpMessageConverter.class.getName();
    private final ObjectMapper objectMapper = JsonMapper.createMapperForRestClient();
    private final Class view;

    public RachioHttpMessageConverter(Class view) {
        this.view = view;
        setObjectMapper(this.objectMapper);
    }

    protected final void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            this.objectMapper.writerWithView(this.view).writeValue(outputMessage.getBody(), object);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }
}
