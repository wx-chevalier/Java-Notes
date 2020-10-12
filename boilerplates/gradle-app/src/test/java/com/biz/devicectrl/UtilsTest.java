package com.biz.devicectrl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * EPlatControlUtilTest.
 *
 * @author lotuc
 */
class UtilsTest {
    @Test
    void textSayHello() {
        assertEquals("Hello", new Utils().sayHello());
    }
}
