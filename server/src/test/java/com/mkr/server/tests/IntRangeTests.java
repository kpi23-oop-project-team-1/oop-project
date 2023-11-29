package com.mkr.server.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.mkr.server.common.IntRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class IntRangeTests {
    @Test
    public void constructorThrowsWhenStartGreaterThanEndTest() {
        assertThrows(IllegalArgumentException.class, () -> new IntRange(7, 2));
    }

    @Test
    public void parseTest() {
        assertEquals(new IntRange(5, 7), IntRange.parse("5-7"));
        assertEquals(new IntRange(50, 700), IntRange.parse("50-700"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "123",
        "-34",
        "4-",
        "abc"
    })
    public void parseThrowsOnInvalidFormatTest(String text) {
        assertThrows(RuntimeException.class, () -> IntRange.parse(text));
    }

    @Test
    public void fixedStepTest() {
        assertEquals(new IntRange(0, 5), IntRange.fixedStep(0, 5));
        assertEquals(new IntRange(5, 10), IntRange.fixedStep(1, 5));
        assertEquals(new IntRange(10, 15), IntRange.fixedStep(2, 5));
    }

    @Test
    public void unionTest() {
        assertEquals(new IntRange(0, 5), new IntRange(0, 5).union(new IntRange(0, 1)));
        assertEquals(new IntRange(0, 5), new IntRange(0, 1).union(new IntRange(0, 5)));
        assertEquals(new IntRange(0, 5), new IntRange(1, 5).union(new IntRange(0, 2)));
    }

    @Test
    public void staticUnionTest() {
        assertNull(IntRange.union(null, null));
        assertEquals(new IntRange(0, 5), IntRange.union(new IntRange(0, 5), null));
        assertEquals(new IntRange(0, 5), IntRange.union(null, new IntRange(0, 5)));
        assertEquals(new IntRange(0, 5), IntRange.union(new IntRange(1, 5), new IntRange(0, 2)));
    }

    @Test
    public void widenTest() {
        assertEquals(new IntRange(0, 5), new IntRange(0, 5).widen(3));
        assertEquals(new IntRange(0, 6), new IntRange(0, 5).widen(6));
        assertEquals(new IntRange(0, 6), new IntRange(4, 6).widen(0));
    }

    @Test
    public void containsTest() {
        assertTrue(new IntRange(0, 5).contains(5));
        assertTrue(new IntRange(0, 5).contains(3));
        assertFalse(new IntRange(0, 5).contains(10));
    }

    @Test
    public void lengthTest() {
        assertEquals(7, new IntRange(0, 6).length());
        assertEquals(2, new IntRange(3, 4).length());
    }
}
