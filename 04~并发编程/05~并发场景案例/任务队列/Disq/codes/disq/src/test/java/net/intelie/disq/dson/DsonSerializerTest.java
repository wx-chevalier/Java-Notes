package net.intelie.disq.dson;

import com.google.gson.Gson;
import net.intelie.disq.Buffer;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DsonSerializerTest {
    @Test
    public void testFromGson() {
        String str = "{\"index_timestamp\":1.56294378011E12,\"wellbore_name\":\"1\",\"adjusted_index_timestamp\":1.562943817363E12,\"source\":\"WITS\",\"depth_value\":6717.527,\"uom\":\"unitless\",\"extra\":\"RBNvo1WzZ4o\",\"mnemonic\":\"STKNUM\",\"well_name\":\"MP72 - A11 ST\",\"depth_mnemonic\":\"DEPTMEAS\",\"value\":0.0,\"errors\":[\"missing_src_unit\",\"unknown_src_unit\"],\"timestamp\":1.562943818361E12,\"__type\":\"ensco75\",\"__src\":\"replay/rig11_b\"}";
        Map<?, ?> map = new Gson().fromJson(
                str,
                Map.class);

        Buffer buffer = new Buffer();
        DsonSerializer.Instance serializer = new DsonSerializer().create();
        serializer.serialize(buffer, map);

        assertThat(buffer.count()).isLessThan(str.length());
    }

    @Test
    public void testDeserializeInvalidStream() throws IOException {
        Buffer buffer = new Buffer();
        buffer.write().write(254);

        DsonSerializer.Instance serializer = new DsonSerializer().create();
        assertThatThrownBy(() -> serializer.deserialize(buffer))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("unknown DSON type");

    }

    @Test
    public void testSerialize() throws IOException {
        Map<Object, Object> map = new LinkedHashMap<>();
        map.put(111, "aaa");
        map.put("âçãó", true);
        map.put("ccc", null);
        map.put(Arrays.asList("ddd", "eee"), Arrays.asList(
                Collections.singletonMap(222.0, false),
                Collections.singletonMap("fff", new Error("(╯°□°)╯︵ ┻━┻"))
        ));

        DsonSerializer.Instance serializer = new DsonSerializer().create();
        Buffer buffer = new Buffer();

        serializer.serialize(buffer, map);

        Map<Object, Object> expected = new LinkedHashMap<>();
        expected.put(111.0, "aaa");
        expected.put("âçãó", true);
        expected.put("ccc", null);
        expected.put(Arrays.asList("ddd", "eee"), Arrays.asList(
                Collections.singletonMap(222.0, false),
                Collections.singletonMap("fff", "java.lang.Error: (╯°□°)╯︵ ┻━┻")
        ));

        assertThat(serializer.deserialize(buffer)).isEqualTo(expected);
    }
}
