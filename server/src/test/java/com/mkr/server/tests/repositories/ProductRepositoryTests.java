package com.mkr.server.tests.repositories;

import com.mkr.datastore.inMemory.InMemoryDataStore;
import com.mkr.server.common.IntRange;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.*;
import com.mkr.server.repositories.ProductRepository;
import com.mkr.server.search.SearchOrder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

public class ProductRepositoryTests {
    @NotNull
    private static Product product(int price) {
        return new Product(0, 0, "", new String[0], price, 0, ProductCategory.HOME, ProductState.NEW, ProductStatus.SOLD, ColorId.BLACK);
    }

    @NotNull
    private static ProductRepository repository(Product... products) {
        var dataStore = new InMemoryDataStore(DataStoreConfig.configuration);
        dataStore.getCollection(DataStoreConfig.products).insert(products);

        return new ProductRepository(dataStore);
    }

    @Test
    public void collectIfTest() {
        ProductRepository repo = repository(
            product(5),
            product(6),
            product(10),
            product(15)
        );

        int actual = repo.collectIf(p -> p.getPrice() >= 10, Collectors.summingInt(Product::getPrice));

        Assertions.assertEquals(25, actual);
    }

    @Test
    public void filterProductsFirstPageCheapestTest() {
        ProductRepository repo = repository(
            product(5),
            product(6),
            product(10),
            product(15),
            product(17),
            product(18)
        );

        Product[] products = repo.filterProducts(
            p -> p.getPrice() >= 10,
            SearchOrder.CHEAPEST,
            new IntRange(0, 20)
        );

        var expectedProducts = new Product[] {
            product(10),
            product(15),
            product(17),
            product(18)
        };

        Assertions.assertArrayEquals(expectedProducts, products);
    }

    @Test
    public void filterProductsFirstPageMostExpensiveTest() {
        ProductRepository repo = repository(
            product(5),
            product(6),
            product(10),
            product(15),
            product(17),
            product(18)
        );

        Product[] products = repo.filterProducts(
            p -> p.getPrice() >= 10,
            SearchOrder.MOST_EXPENSIVE,
            new IntRange(0, 20)
        );

        var expectedProducts = new Product[] {
            product(18),
            product(17),
            product(15),
            product(10),
        };

        Assertions.assertArrayEquals(expectedProducts, products);
    }

    @Test
    public void filterProductsSecondPageCheapestTest() {
        ProductRepository repo = repository(
            product(5),
            product(6),
            product(10),
            product(15),
            product(17),
            product(18)
        );

        Product[] products = repo.filterProducts(
            p -> p.getPrice() >= 10,
            SearchOrder.CHEAPEST,
            new IntRange(2, 20)
        );

        var expectedProducts = new Product[] {
            product(17),
            product(18)
        };

        Assertions.assertArrayEquals(expectedProducts, products);
    }

    @Test
    public void filterProductsSecondPageMostExpensiveTest() {
        ProductRepository repo = repository(
            product(5),
            product(6),
            product(10),
            product(15),
            product(17),
            product(18)
        );

        Product[] products = repo.filterProducts(
            p -> p.getPrice() >= 10,
            SearchOrder.MOST_EXPENSIVE,
            new IntRange(2, 20)
        );

        var expectedProducts = new Product[] {
            product(15),
            product(10),
        };

        Assertions.assertArrayEquals(expectedProducts, products);
    }
}
