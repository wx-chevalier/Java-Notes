package net.intelie.disq.dson;

import net.intelie.disq.Buffer;
import net.intelie.disq.Serializer;
import net.intelie.disq.SerializerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DsonSerializer implements SerializerFactory<Object> {
    private final StringCache keyCache;
    private final StringCache valueCache;

    public DsonSerializer() {
        this(new StringCache(), new StringCache());
    }

    public DsonSerializer(StringCache keyCache, StringCache valueCache) {
        this.keyCache = keyCache;
        this.valueCache = valueCache;

    }

    @Override
    public DsonSerializer.Instance create() {
        return new Instance();
    }

    public class Instance implements Serializer<Object> {
        private final UnicodeView unicodeView = new UnicodeView();
        private final Latin1View latin1View = new Latin1View();
        private final BiConsumer<Object, Object> SERIALIZE_DOUBLE = this::serializeDouble;
        private final Consumer<Object> SERIALIZE_SINGLE = this::serializeSingle;
        private Buffer.OutStream stream;

        @Override
        public void serialize(Buffer buffer, Object obj) {
            try (Buffer.OutStream stream = buffer.write()) {
                serialize(stream, obj);
            }
        }

        public void serialize(Buffer.OutStream stream, Object obj) {
            this.stream = stream;
            try {
                serializerObject(stream, obj);
            } finally {
                //doing that his way so I can use Map.forEach without allocations
                this.stream = null;
            }
        }

        private void serializerObject(Buffer.OutStream stream, Object obj) {
            if (obj instanceof Number) {
                DsonBinaryWrite.writeType(stream, DsonType.DOUBLE);
                DsonBinaryWrite.writeNumber(stream, ((Number) obj).doubleValue());
            } else if (obj instanceof CharSequence) {
                CharSequence str = (CharSequence) obj;
                boolean latin1 = true;
                int length = str.length();
                for (int i = 0; latin1 && i < length; i++)
                    latin1 = str.charAt(i) < 256;
                if (latin1) {
                    DsonBinaryWrite.writeType(stream, DsonType.STRING_LATIN1);
                    DsonBinaryWrite.writeLatin1(stream, str);
                } else {
                    DsonBinaryWrite.writeType(stream, DsonType.STRING);
                    DsonBinaryWrite.writeUnicode(stream, str);
                }
            } else if (obj instanceof Map<?, ?>) {
                DsonBinaryWrite.writeType(stream, DsonType.OBJECT);
                DsonBinaryWrite.writeCount(stream, ((Map<?, ?>) obj).size());
                ((Map<?, ?>) obj).forEach(SERIALIZE_DOUBLE);
            } else if (obj instanceof Collection<?>) {
                DsonBinaryWrite.writeType(stream, DsonType.ARRAY);
                DsonBinaryWrite.writeCount(stream, ((Collection<?>) obj).size());
                ((Collection<?>) obj).forEach(SERIALIZE_SINGLE);
            } else if (obj instanceof Boolean) {
                DsonBinaryWrite.writeType(stream, DsonType.BOOLEAN);
                DsonBinaryWrite.writeBoolean(stream, (Boolean) obj);
            } else if (obj == null) {
                DsonBinaryWrite.writeType(stream, DsonType.NULL);
            } else {
                serializerObject(stream, obj.toString());
            }
        }

        private void serializeSingle(Object v) {
            serializerObject(stream, v);
        }

        private void serializeDouble(Object k, Object v) {
            serializerObject(stream, k);
            serializerObject(stream, v);
        }

        @Override
        public Object deserialize(Buffer buffer) {
            try (Buffer.InStream stream = buffer.read()) {
                return deserialize(stream);
            }

        }

        public Object deserialize(Buffer.InStream stream) {
            return deserialize(stream, false);
        }

        public Object deserialize(Buffer.InStream stream, boolean forKey) {
            switch (DsonBinaryRead.readType(stream)) {
                case DOUBLE:
                    return DsonBinaryRead.readNumber(stream);
                case STRING:
                    DsonBinaryRead.readUnicode(stream, unicodeView);
                    String unicodeStr = getString(forKey, unicodeView);
                    unicodeView.clear();
                    return unicodeStr;
                case OBJECT:
                    int objectSize = DsonBinaryRead.readCount(stream);
                    Map<Object, Object> map = new LinkedHashMap<>(objectSize);
                    for (int i = 0; i < objectSize; i++) {
                        Object key = deserialize(stream, true);
                        Object value = deserialize(stream, false);
                        map.put(key, value);
                    }
                    return map;
                case ARRAY:
                    int arraySize = DsonBinaryRead.readCount(stream);
                    ArrayList<Object> list = new ArrayList<>(arraySize);
                    for (int i = 0; i < arraySize; i++)
                        list.add(deserialize(stream, false));
                    return list;
                case STRING_LATIN1:
                    DsonBinaryRead.readLatin1(stream, latin1View);
                    String latin1Str = getString(forKey, latin1View);
                    latin1View.clear();
                    return latin1Str;
                case BOOLEAN:
                    return DsonBinaryRead.readBoolean(stream);
                case NULL:
                    return null;
                default:
                    throw new IllegalStateException("unknown DSON type");
            }
        }

        private String getString(boolean forKey, CharSequence view) {
            return forKey ? keyCache.get(view) : valueCache.get(view);
        }

    }
}
