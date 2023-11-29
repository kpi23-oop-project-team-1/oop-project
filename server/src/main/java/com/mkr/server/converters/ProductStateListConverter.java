package com.mkr.server.converters;

import com.mkr.server.domain.ProductState;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ProductStateListConverter extends EnumListConverter<ProductState> {
    @Override
    @NotNull
    protected ProductState[] createArray(int size) {
        return new ProductState[size];
    }

    @Override
    @NotNull
    protected ProductState fromText(@NotNull String text) {
        return ProductState.forValue(text);
    }
}
