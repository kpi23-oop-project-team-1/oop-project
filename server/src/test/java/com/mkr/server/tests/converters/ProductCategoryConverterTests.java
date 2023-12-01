package com.mkr.server.tests.converters;

import com.mkr.server.converters.ProductCategoryConverter;
import com.mkr.server.domain.ProductCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProductCategoryConverterTests {
    @Test
    public void convertTest() {
        var converter = new ProductCategoryConverter();
        Assertions.assertEquals(ProductCategory.HOME, converter.convert("home"));
        Assertions.assertEquals(ProductCategory.DRESS, converter.convert("dress"));
    }
}
