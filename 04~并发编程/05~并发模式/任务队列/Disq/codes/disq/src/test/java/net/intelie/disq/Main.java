package net.intelie.disq;

import com.google.gson.Gson;
import net.intelie.disq.dson.DsonBinaryRead;
import net.intelie.disq.dson.DsonSerializer;
import net.intelie.introspective.ThreadResources;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Main {
    @SuppressForbidden
    public static void main(String[] args) throws IOException {
        try (DiskQueueReader queue = new DiskQueueReader(Paths.get("/home/juanplopes/Downloads/queue/hess"))) {
            System.out.println(queue.count());

            Buffer buffer = new Buffer();
            Serializer<Object> serializer = new StorageEventSerializer().create();

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("/home/juanplopes/Downloads/queue.txt"))) {
                while (queue.moveNext(buffer)) {
                    Map<?, ?> event = (Map<?, ?>) serializer.deserialize(buffer);
                    writer.write(event.toString());
                    writer.write('\n');
                    System.out.println(event.get("__type") + " " + new Date(((Number) event.get("timestamp")).longValue()));
                }
            }
        }


        //        Map<?, ?> map2 = new LinkedHashMap<>(new Gson().fromJson(
//                "{\"index_timestamp\":1.56294378011E12,\"wellbore_name\":\"1\",\"adjusted_index_timestamp\":1.562943817363E12,\"source\":\"WITS\",\"depth_value\":6717.527,\"uom\":\"unitless\",\"extra\":\"RBNvo1WzZ4o\",\"mnemonic\":\"STKNUM\",\"well_name\":\"MP72 – A11 ST\",\"depth_mnemonic\":\"DEPTMEAS\",\"value\":0.0,\"errors\":[\"missing_src_unit\",\"unknown_src_unit\"],\"timestamp\":1.562943818361E12,\"__type\":\"ensco75\",\"__src\":\"replay/rig11_b\"}",
//                Map.class));
//
//
//        Map<Object, Object> map = new LinkedHashMap<>();
//        map.put(111, "aaa");
//        map.put("âçãó", true);
//        map.put("ccc", null);
//        map.put(Arrays.asList("ddd", "eee"), Arrays.asList(
//                Collections.singletonMap(222.0, false),
//                Collections.singletonMap("fff", "(╯°□°)╯︵ ┻━┻")
//        ));
//
//
//        //StateFile file = new StateFile(Paths.get("/home/juanplopes/Downloads/test/core.storage.main/state"));
//
//        benchmark(map2, new DsonSerializer());
//        benchmark(map2, new FstSerializer());
//        benchmark(map2, new DefaultSerializer<>());
//        benchmark(map2, GsonSerializer.make());

    }

    @SuppressForbidden
    private static void benchmark(Map<?, ?> map2, SerializerFactory<Object> serializer) throws IOException {
        try (PersistentQueue<Object> q = Disq.builder()
                .setFlushOnPop(false)
                .setFlushOnPush(false)
                .setSerializer(serializer)
                .setDirectory("/home/juanplopes/Downloads/test/core.storage.main")
                .buildPersistentQueue()) {
            q.clear();
            long start = System.nanoTime();
            long memStart = ThreadResources.allocatedBytes(Thread.currentThread());

            for (int i = 0; i < 1000000; i++)
                q.push(map2);

            double writeTime = (System.nanoTime() - start) / 1e9;

            long bytes = q.bytes();
            int count = 0;
            while (q.count() > 0) {
                q.pop();
                count++;
            }
            double readTime = (System.nanoTime() - start) / 1e9 - writeTime;

            System.out.println(serializer.getClass().getSimpleName());
            System.out.printf((Locale) null, "total: %d objects, %.3fMB\n", count, bytes / (double) (1 << 20));
            System.out.printf((Locale) null, "write: %.3fs, %.3f obj/s, %.3fMB/s\n",
                    writeTime,
                    count / writeTime,
                    bytes / writeTime / (double) (1 << 20));
            System.out.printf((Locale) null, "read : %.3fs, %.3f obj/s, %.3fMB/s\n",
                    readTime,
                    count / readTime,
                    bytes / readTime / (double) (1 << 20));
            System.out.printf((Locale) null, "alloc: %.3f bytes/obj\n",
                    (ThreadResources.allocatedBytes(Thread.currentThread()) - memStart) / (double) count);
            System.out.println();
        }
    }

    public static class StorageEventSerializer implements SerializerFactory<Object> {
        private final DsonSerializer dson = new DsonSerializer();

        @Override
        public Serializer<Object> create() {
            return new Serializer<Object>() {
                private final DsonSerializer.Instance instance = dson.create();
                private final Gson gson = new Gson();

                @Override
                public void serialize(Buffer buffer, Object storageEvent) throws IOException {
                }

                @Override
                public Object deserialize(Buffer buffer) throws IOException {
                    if (buffer.buf().length >= 5 && buffer.buf()[4] == '{') {
                        //here, have some poetry:
                        //old queue format started with a JSON after retry
                        //new queue format writes a DsonType which is never JSON
                        //so we support old queue format by checking that byte
                        //this code should be removed when that queue is gone

                        return workaroundForOldQueueFormat(buffer);
                    }

                    try (Buffer.InStream stream = buffer.read()) {
                        int retries = DsonBinaryRead.readInt32(stream);

                        return instance.deserialize(stream);
                    }
                }

                private Object workaroundForOldQueueFormat(Buffer buffer) throws IOException {
                    Buffer.InStream read = buffer.read();
                    DataInputStream dataStream = new DataInputStream(read);
                    int retries = dataStream.readInt();

                    try (InputStreamReader reader = new InputStreamReader(read, StandardCharsets.UTF_8)) {
                        return gson.fromJson(reader, Map.class);
                    }
                }
            };
        }
    }

}
