package net.intelie.disq;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultSerializerTest {
    @Test
    public void willReadAndWrite() throws Exception {
        DefaultSerializer<String> serializer = new DefaultSerializer<>();

        Buffer buffer = new Buffer();
        serializer.serialize(buffer, "test");
        assertThat(buffer.count()).isEqualTo(11);

        assertThat(serializer.deserialize(buffer)).isEqualTo("test");
    }

    @Test
    public void willReadAndWriteBig() throws Exception {
        String s = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus ac magna accumsan, tempus lorem sit amet, consequat diam. Sed placerat sagittis neque. Suspendisse sit amet pulvinar nulla. Fusce in ligula in ante auctor gravida eget vitae libero. Suspendisse eu gravida justo. Nam imperdiet, lacus ac euismod aliquam, augue ligula consequat felis, et sagittis eros augue a orci. Nulla odio neque, dictum ornare euismod ut, faucibus ac augue. Nullam justo justo, aliquam in quam non, tincidunt tincidunt libero. In suscipit sapien eu tortor dapibus, in laoreet leo vestibulum. Sed malesuada ante metus, sed imperdiet nisl rutrum non. Pellentesque elementum facilisis quam, at imperdiet diam viverra eget. Donec pharetra lobortis elementum. Vivamus lobortis tortor nec posuere ornare.";

        DefaultSerializer<String> serializer = new DefaultSerializer<>();

        Buffer buffer = new Buffer();
        serializer.serialize(buffer, s);

        assertThat(serializer.deserialize(buffer)).isEqualTo(s);
    }

    @Test
    public void willFailIfClassNotFound() throws Exception {


        DefaultSerializer<Object> serializer = new DefaultSerializer<>();

        byte[] bytes = Base64.getDecoder().decode("rO0ABXNyAC9uZXQuaW50ZWxpZS5kaXNxLkRlZmF1bHRTZXJpYWxpemVyVGVzdCRXaGF0ZXZlcnB4QEf+xdUJAgAAeHA=");

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        serializer.serialize(baos, new Whatever());
//        System.out.println(Base64.getEncoder().encodeToString(baos.toByteArray()));

        Buffer buffer = new Buffer(bytes);
        assertThatThrownBy(() -> serializer.deserialize(buffer))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("DefaultSerializerTest$Whatever")
                .hasCauseInstanceOf(ClassNotFoundException.class);

    }
}
