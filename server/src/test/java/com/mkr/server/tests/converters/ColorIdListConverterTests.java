package com.mkr.server.tests.converters;

import com.mkr.server.converters.ColorIdListConverter;
import com.mkr.server.domain.ColorId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ColorIdListConverterTests {
    @Test
    public void convertTest() {
        var converter = new ColorIdListConverter();

        Assertions.assertArrayEquals(new ColorId[] { ColorId.BLACK }, converter.convert("black"));
        Assertions.assertArrayEquals(
            new ColorId[] { ColorId.BLACK, ColorId.WHITE },
            converter.convert("black,white")
        );
    }
}
