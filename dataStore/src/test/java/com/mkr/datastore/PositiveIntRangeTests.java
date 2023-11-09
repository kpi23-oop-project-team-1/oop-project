package com.mkr.datastore;

import static org.junit.jupiter.api.Assertions.*;

import com.mkr.datastore.utils.PositiveIntRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class PositiveIntRangeTests {
    public static Stream<Arguments> constructorThrowsTestArguments() {
        return Stream.of(
            Arguments.of(-1, 5),
            Arguments.of(5, -1),
            Arguments.of(-1, -1),
            Arguments.of(Integer.MAX_VALUE, Integer.MAX_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource("constructorThrowsTestArguments")
    public void constructorThrowsTest(int start, int length) {
        assertThrows(IllegalArgumentException.class, () -> new PositiveIntRange(start, length));
    }

    public static Stream<Arguments> intersectionRelativeTestArguments() {
        return Stream.of(
            Arguments.of(
                new PositiveIntRange(3, 5),
                new PositiveIntRange(1, 1),
                new PositiveIntRange(4, 1)
            ),
            Arguments.of(
                new PositiveIntRange(3, 5),
                new PositiveIntRange(5, 6),
                PositiveIntRange.empty()
            ),
            Arguments.of(
                new PositiveIntRange(3, 5),
                new PositiveIntRange(0, 2),
                new PositiveIntRange(3, 2)
            )
        );
    }

    @ParameterizedTest
    @MethodSource("intersectionRelativeTestArguments")
    public void intersectionRelativeTest(PositiveIntRange first, PositiveIntRange second, PositiveIntRange expected) {
        PositiveIntRange actual = first.intersectionRelative(second);

        if (expected.isEmpty()) {
            assertTrue(actual.isEmpty());
        } else {
            assertEquals(expected, actual);
        }
    }
}
