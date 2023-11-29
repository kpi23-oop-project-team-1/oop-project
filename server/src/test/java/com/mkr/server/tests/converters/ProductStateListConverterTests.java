package com.mkr.server.tests.converters;

import com.mkr.server.converters.ProductStateListConverter;
import com.mkr.server.domain.ProductState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductStateListConverterTests {
    @Test
    public void convertTest() {
        var converter = new ProductStateListConverter();

        Assertions.assertArrayEquals(new ProductState[] { ProductState.NEW }, converter.convert("new"));
        Assertions.assertArrayEquals(
            new ProductState[] { ProductState.NEW, ProductState.GOOD },
            converter.convert("new,good")
        );
    }
}
