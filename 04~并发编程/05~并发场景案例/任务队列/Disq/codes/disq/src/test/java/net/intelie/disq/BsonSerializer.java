package net.intelie.disq;

import org.bson.BsonBinaryWriter;

import java.io.IOException;

public class BsonSerializer implements Serializer<Object> {
    @Override
    public void serialize(Buffer buffer, Object obj) throws IOException {
    }

    @Override
    public Object deserialize(Buffer buffer) throws IOException {
        return null;
    }
}
