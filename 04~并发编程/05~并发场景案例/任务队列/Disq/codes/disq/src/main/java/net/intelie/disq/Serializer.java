package net.intelie.disq;

import java.io.IOException;

public interface Serializer<T> {
    void serialize(Buffer buffer, T obj) throws IOException;

    T deserialize(Buffer buffer) throws IOException;

    default void clear() {
    }
}
