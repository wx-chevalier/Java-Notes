package net.intelie.disq;

public interface SerializerFactory<T> {
    Serializer<T> create();
}
