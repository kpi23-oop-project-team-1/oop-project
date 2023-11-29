package com.mkr.server.dto;

import com.mkr.server.domain.Product;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record ConciseProduct(int id, String title, String imageSource, int price, int totalAmount) {
    @NotNull
    public static ConciseProduct fromProduct(@NotNull Product product) {
        return new ConciseProduct(
            product.getProductId(),
            product.getTitle(),
            product.getImageSources()[0],
            product.getPrice(),
            product.getAmount()
        );
    }

    @NotNull
    public static ConciseProduct[] fromProducts(@NotNull Product[] product) {
        return Arrays.stream(product).map(ConciseProduct::fromProduct).toArray(ConciseProduct[]::new);
    }
}
