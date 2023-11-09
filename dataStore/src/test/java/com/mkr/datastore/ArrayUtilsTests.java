package com.mkr.datastore;

import com.mkr.datastore.utils.ArrayUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilsTests {
    public static Stream<Arguments> indexOfTestArguments() {
        Object[] array1 = new Object[] { 0, 1, 3 };
        Object[] emptyArray = new Object[0];

        return Stream.of(
            Arguments.of(array1, 0, 0),
            Arguments.of(array1, 3, 2),
            Arguments.of(array1, 100, -1),
            Arguments.of(emptyArray, 1, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("indexOfTestArguments")
    public void indexOfTest(Object[] array, Object needle, int expectedIndex) {
        int actualIndex = ArrayUtils.indexOf(array, needle);

        assertEquals(expectedIndex, actualIndex);
    }
}
