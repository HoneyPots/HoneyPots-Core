package com.honeypot.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@JsonComponent
public class JsonConverter {

    public static class LocalDateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser,
                                         DeserializationContext deserializationContext) throws IOException {
            try {
                return LocalDateTime.parse(jsonParser.getValueAsString());
            } catch (Exception e) {
                Date in = new Date(jsonParser.getValueAsLong() * 1000);
                return LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
            }
        }
    }
}
