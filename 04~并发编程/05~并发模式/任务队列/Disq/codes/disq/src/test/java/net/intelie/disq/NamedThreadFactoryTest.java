package net.intelie.disq;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NamedThreadFactoryTest {
    @Test
    public void testCreateNamed() throws Exception {
        NamedThreadFactory factory = new NamedThreadFactory("abc-%d");
        assertThat(factory.newThread(null).getName()).isEqualTo("abc-0");
        assertThat(factory.newThread(null).getName()).isEqualTo("abc-1");
        assertThat(factory.newThread(null).getName()).isEqualTo("abc-2");
    }
}