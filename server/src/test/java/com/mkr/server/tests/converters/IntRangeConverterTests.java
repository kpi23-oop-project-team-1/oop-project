package com.mkr.server.tests.converters;

import com.mkr.server.common.IntRange;
import com.mkr.server.converters.IntRangeConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntRangeConverterTests {
    @Test
    public void convertTest() {
        Assertions.assertEquals(new IntRange(5, 7), new IntRangeConverter().convert("5-7"));
    }
}
