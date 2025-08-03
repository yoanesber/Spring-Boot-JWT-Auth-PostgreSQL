package com.yoanesber.backend.jwt_auth_demo.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * InstantSerializer is a custom serializer for the Instant class.
 * It converts an Instant object to a string in ISO_INSTANT format.
 * This is useful for serializing Instant objects in JSON format.
 */

public class InstantSerializer extends JsonSerializer<Instant> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    // Serialize the Instant object to a string
    @Override
    public void serialize(Instant instant, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(FORMATTER.format(instant));
    }
}
