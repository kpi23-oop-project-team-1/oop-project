package com.mkr.server.converters;

import com.mkr.server.domain.ProductCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductCategoryConverter implements Converter<String, ProductCategory> {
    @Override
    @NotNull
    public ProductCategory convert(@NotNull String source) {
        return ProductCategory.forValue(source);
    }
}
