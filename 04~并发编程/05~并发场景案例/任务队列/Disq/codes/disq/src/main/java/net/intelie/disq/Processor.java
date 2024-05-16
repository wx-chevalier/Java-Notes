package net.intelie.disq;

public interface Processor<T> {
    void process(T obj) throws Exception;
}
