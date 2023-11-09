package com.mkr.datastore;

import com.mkr.datastore.utils.TypeUtils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public final class TypeUtilsTests {
    public static class TestClass {
        public void method1() {
        }
    }

    @Test
    public void createGetterNameTest() {
        assertEquals("get", TypeUtils.createGetterName(""));
        assertEquals("getValue", TypeUtils.createGetterName("value"));
        assertEquals("getValue", TypeUtils.createGetterName("Value"));
    }

    @Test
    public void getDeclaredMethodOrNullTest() throws NoSuchMethodException {
        assertNull(TypeUtils.getDeclaredMethodOrNull(TestClass.class, "method1", Integer.class));
        assertNull(TypeUtils.getDeclaredMethodOrNull(TestClass.class, "method2"));
        assertEquals(
            TestClass.class.getDeclaredMethod("method1"),
            TypeUtils.getDeclaredMethodOrNull(TestClass.class, "method1")
        );
    }

    public static Stream<Arguments> transformPrimitiveTypeToWrapperTestArguments() {
        return Stream.of(
            Arguments.of(boolean.class, Boolean.class),
            Arguments.of(byte.class, Byte.class),
            Arguments.of(short.class, Short.class),
            Arguments.of(int.class, Integer.class),
            Arguments.of(long.class, Long.class),
            Arguments.of(float.class, Float.class),
            Arguments.of(double.class, Double.class),
            Arguments.of(Object.class, Object.class)
        );
    }

    @ParameterizedTest
    @MethodSource("transformPrimitiveTypeToWrapperTestArguments")
    public void transformPrimitiveTypeToWrapperTest(Class<?> input, Class<?> expected) {
        Class<?> actual = TypeUtils.transformPrimitiveTypeToWrapper(input);

        assertEquals(expected, actual);
    }
}
