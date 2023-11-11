package com.mkr.datastore;

import com.mkr.datastore.utils.TypeUtils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public final class TypeUtilsTests {
    @Test
    public void createGetterNameTest() {
        assertEquals("get", TypeUtils.createGetterName(""));
        assertEquals("getValue", TypeUtils.createGetterName("value"));
        assertEquals("getValue", TypeUtils.createGetterName("Value"));
    }
}
