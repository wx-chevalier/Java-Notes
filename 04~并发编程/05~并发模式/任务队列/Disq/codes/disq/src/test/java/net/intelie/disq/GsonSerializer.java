package net.intelie.disq;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class GsonSerializer<T> implements SerializerFactory<T> {
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private final Class<T> clazz;

    public GsonSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Serializer<T> create() {
        return new Serializer<T>() {
            @Override
            public void serialize(Buffer buffer, T obj) throws IOException {
                try (OutputStreamWriter writer = new OutputStreamWriter(buffer.write(), StandardCharsets.UTF_8)) {
                    gson.toJson(obj, writer);
                }
            }

            @Override
            public T deserialize(Buffer buffer) throws IOException {
                try (InputStreamReader reader = new InputStreamReader(buffer.read(), StandardCharsets.UTF_8)) {
                    return gson.fromJson(reader, clazz);
                }
            }
        };
    }

    public static GsonSerializer<Object> make() {
        return new GsonSerializer<>(Object.class);
    }
}
