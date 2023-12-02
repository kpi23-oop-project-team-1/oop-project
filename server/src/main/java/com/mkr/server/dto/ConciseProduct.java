package com.mkr.server.dto;

import com.mkr.server.domain.Product;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

public record ConciseProduct(int id, String title, String imageSource, int price, int totalAmount) {
    @NotNull
    public static ConciseProduct fromProduct(@NotNull Product product, @NotNull String imageSource) {
        return new ConciseProduct(
            product.getProductId(),
            product.getTitle(),
            imageSource,
            product.getPrice(),
            product.getAmount()
        );
    }

    @NotNull
    public static ConciseProduct[] fromProducts(
        @NotNull Product[] product,
        @NotNull Function<Product, String> imageSourceSupplier
    ) {
        return Arrays.stream(product)
            .map(p -> fromProduct(p, imageSourceSupplier.apply(p)))
            .toArray(ConciseProduct[]::new);
    }
}
