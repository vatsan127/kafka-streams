package com.github.kafka_streams.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Production-ready generic JSON Serde using Jackson.
 * Uses a shared ObjectMapper instance for better performance.
 * <p>
 * Thread-safety: ObjectMapper is thread-safe for read/write operations (readValue, writeValueAsBytes)
 * after configuration is complete. We configure it once at class loading time, so no mutability issues.
 *
 * @param <T> The type to serialize/deserialize
 */
@Slf4j
public class JsonSerde<T> implements Serde<T> {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private final Class<T> targetType;

    public JsonSerde(Class<T> targetType) {
        this.targetType = targetType;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Override
    public Serializer<T> serializer() {
        return (topic, data) -> {
            if (data == null) {
                return null;
            }
            try {
                return OBJECT_MAPPER.writeValueAsBytes(data);
            } catch (Exception e) {
                log.error("Error serializing {} to JSON for topic {}", targetType.getSimpleName(), topic, e);
                throw new SerializationException("Error serializing " + targetType.getSimpleName(), e);
            }
        };
    }

    @Override
    public Deserializer<T> deserializer() {
        return (topic, bytes) -> {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            try {
                return OBJECT_MAPPER.readValue(bytes, targetType);
            } catch (Exception e) {
                log.error("Error deserializing {} from JSON for topic {}", targetType.getSimpleName(), topic, e);
                throw new SerializationException("Error deserializing " + targetType.getSimpleName(), e);
            }
        };
    }
}
