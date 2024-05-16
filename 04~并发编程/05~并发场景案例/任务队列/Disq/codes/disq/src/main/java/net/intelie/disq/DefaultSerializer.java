package net.intelie.disq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DefaultSerializer<T> implements Serializer<T>, SerializerFactory<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSerializer.class);

    @Override
    public void serialize(Buffer buffer, T obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(buffer.write())) {
            oos.writeObject(obj);
        }
    }

    @Override
    public T deserialize(Buffer buffer) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(buffer.read())) {
            try {
                return (T) ois.readObject();
            } catch (ClassNotFoundException e) {
                LOGGER.info("Exception on default deserializer", e);
                throw new IOException(e);
            }
        }
    }

    @Override
    public Serializer<T> create() {
        return this;
    }
}
