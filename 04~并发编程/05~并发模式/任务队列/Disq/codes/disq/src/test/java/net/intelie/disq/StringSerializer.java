package net.intelie.disq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringSerializer implements Serializer<String> {
    @Override
    public void serialize(Buffer buffer, String obj) throws IOException {
        buffer.write().write(obj.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String deserialize(Buffer buffer) throws IOException {
        return new String(buffer.buf(), 0, buffer.count(), StandardCharsets.UTF_8);
    }
}
