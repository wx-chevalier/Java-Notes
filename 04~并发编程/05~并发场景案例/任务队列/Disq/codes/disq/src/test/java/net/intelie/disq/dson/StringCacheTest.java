package net.intelie.disq.dson;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringCacheTest {
    @Test
    public void testMustBePowerOfTwo() {
        assertThatThrownBy(() -> new StringCache(123, 1024))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bucketCount must be a power of two");
    }

    @Test
    public void testCacheHit() {
        StringCache cache = new StringCache();
        StringBuilder original = new StringBuilder("abcde");
        String cached1 = cache.get(original);
        String cached2 = cache.get(original);

        assertThat(original.toString()).isEqualTo(cached1).isNotSameAs(cached1);
        assertThat(original.toString()).isEqualTo(cached2).isNotSameAs(cached2);

        assertThat(cached1).isSameAs(cached2);
    }
}