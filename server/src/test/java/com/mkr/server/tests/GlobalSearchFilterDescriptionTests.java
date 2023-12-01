package com.mkr.server.tests;

import com.mkr.server.common.IntRange;
import com.mkr.server.domain.*;
import com.mkr.server.search.GlobalSearchFilterDescription;
import static org.junit.jupiter.api.Assertions.*;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Stream;

public class GlobalSearchFilterDescriptionTests {
    @NotNull
    private static Product product(int price, ColorId color, ProductState state) {
        return new Product(0, 0, "", new String[0], price, 1, ProductCategory.DRESS, state, ProductStatus.ACTIVE, color);
    }

    @Test
    public void productConsumerEmptyArrayTest() {
        Stream<Product> stream = Stream.empty();
        GlobalSearchFilterDescription actualDesc =
            stream.collect(GlobalSearchFilterDescription.productConsumer());

        var expectedDesc = new GlobalSearchFilterDescription(
            null,
            Set.of(),
            Set.of()
        );

        assertEquals(expectedDesc, actualDesc);
    }

    @Test
    public void productConsumerSingleElementTest() {
        Stream<Product> stream = Stream.of(
            product(100, ColorId.BLACK, ProductState.NEW)
        );
        GlobalSearchFilterDescription actualDesc =
            stream.collect(GlobalSearchFilterDescription.productConsumer());

        var expectedDesc = new GlobalSearchFilterDescription(
            new IntRange(100, 100),
            Set.of(ColorId.BLACK),
            Set.of(ProductState.NEW)
        );

        assertEquals(expectedDesc, actualDesc);
    }

    @Test
    public void productConsumerManyElementTest() {
        Stream<Product> stream = Stream.of(
            product(100, ColorId.BLACK, ProductState.NEW),
            product(50, ColorId.BLACK, ProductState.GOOD),
            product(150, ColorId.RED, ProductState.GOOD),
            product(200, ColorId.WHITE, ProductState.ACCEPTABLE)
        );
        GlobalSearchFilterDescription actualDesc =
            stream.collect(GlobalSearchFilterDescription.productConsumer());

        var expectedDesc = new GlobalSearchFilterDescription(
            new IntRange(50, 200),
            Set.of(ColorId.BLACK, ColorId.RED, ColorId.WHITE),
            Set.of(ProductState.NEW, ProductState.GOOD, ProductState.ACCEPTABLE)
        );

        assertEquals(expectedDesc, actualDesc);
    }
}
