package net.intelie.disq.dson;

public interface StringView extends CharSequence {
    void clear();

    void set(byte[] buf, int start, int length);
}
