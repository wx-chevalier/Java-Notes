package net.intelie.disq.dson;

import java.util.Arrays;

public enum DsonType {
    UNKNOWN(0x00),
    NULL(0x01),
    OBJECT(0x02),
    ARRAY(0x03),
    DOUBLE(0x04),
    BOOLEAN(0x05),
    STRING(0x06),
    STRING_LATIN1(0x07);

    private static final DsonType[] LOOKUP_TABLE = new DsonType[256];
    private final int value;

    static {
        Arrays.fill(LOOKUP_TABLE, UNKNOWN);
        for (final DsonType cur : DsonType.values()) {
            LOOKUP_TABLE[cur.getValue()] = cur;
        }
    }

    DsonType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DsonType findByValue(final int value) {
        return LOOKUP_TABLE[value & 0xFF];
    }
}
