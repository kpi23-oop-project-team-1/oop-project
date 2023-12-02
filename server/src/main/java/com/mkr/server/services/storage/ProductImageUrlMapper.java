package com.mkr.server.services.storage;

import com.mkr.server.domain.Product;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface ProductImageUrlMapper {
    @NotNull
    String getEndpointUrl(int productId, int imageIndex);

    @NotNull
    default Function<Product, String> asSingleImageFunction() {
        return p -> getEndpointUrl(p.getProductId(), 0);
    }
}
