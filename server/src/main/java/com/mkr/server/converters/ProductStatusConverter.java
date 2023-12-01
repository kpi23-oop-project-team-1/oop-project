package com.mkr.server.converters;

import com.mkr.server.domain.ProductStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

public class ProductStatusConverter implements Converter<String, ProductStatus> {
    @Override
    public ProductStatus convert(@NotNull String source) {
        return ProductStatus.forValue(source);
    }
}
