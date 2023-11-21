package com.mkr.datastore.utils;

import com.mkr.datastore.utils.ByteUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteUtilsTests {
    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, 0, Integer.MAX_VALUE })
    public void int32Test(int value) {
        assertEquals(value, ByteUtils.bytesToInt32(ByteUtils.int32ToBytes(value)));
    }
}
