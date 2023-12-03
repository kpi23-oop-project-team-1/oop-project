package com.mkr.server.repositories;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreCollection;
import com.mkr.server.common.IntRange;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.Product;
import com.mkr.server.search.SearchOrder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

@Repository
public class ProductRepository {
    private final DataStore dataStore;

    @Autowired
    public ProductRepository(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @NotNull
    private DataStoreCollection<Product> productCollection() {
        return dataStore.getCollection(DataStoreConfig.products);
    }

    public Optional<Product> getProductById(int id) {
        try (var data = productCollection().data()) {
            return data.filter(p -> p.getProductId() == id).findFirst();
        }
    }

    public boolean containsProductWithId(int id) {
        return getProductById(id).isPresent();
    }

    public Optional<Product> findFirstProductBy(@NotNull Predicate<Product> predicate) {
        try (var data = productCollection().data()) {
            return data.filter(predicate).findFirst();
        }
    }

    public void addProduct(@NotNull Product product) {
        productCollection().insert(product);
        productCollection().setLastID(product.getProductId());
    }

    public void updateProduct(int id, @NotNull Function<Product, Product> transformProduct) {
        productCollection().update(
                o -> o.getProductId() == id,
                transformProduct
        );
    }

    public void updateProductAmount(int id, @NotNull Function<Integer, Integer> transformAmount) {
        productCollection().update(
            p -> p.getProductId() == id,
            p -> p.withAmountAndStatus(transformAmount.apply(p.getAmount()))
        );
    }

    public void deleteProduct(int id) {
        productCollection().delete(o -> o.getProductId() == id);
    }

    public void addProductComment(int productId, int commentId) {
        updateProduct(productId, p -> p.withComment(commentId));
    }

    public Product[] getProductsBy(@NotNull Predicate<Product> predicate) {
        try (var data = productCollection().data()) {
            return data.filter(predicate).toArray(Product[]::new);
        }
    }

    @NotNull
    public<T> T collectIf(@NotNull Predicate<Product> predicate, @NotNull Collector<Product, ?, T> collector) {
        try (var data = productCollection().data()) {
            return data.filter(predicate).collect(collector);
        }
    }

    public Product[] filterProducts(
        @NotNull Predicate<Product> predicate,
        @NotNull SearchOrder order,
        @NotNull IntRange range
    ) {
        try (var data = productCollection().data()) {
            return data.filter(predicate)
                .sorted(order.reverseIfNeeded(Comparator.comparingInt(Product::getPrice)))
                .skip(range.start())
                .limit(range.length())
                .toArray(Product[]::new);
        }
    }

    public int countProducts(@NotNull Predicate<Product> predicate) {
        try (var data = productCollection().data()) {
            return (int)data.filter(predicate).count();
        }
    }

    @NotNull
    public Product[] getUserProducts(@NotNull Predicate<Product> predicate, @NotNull IntRange range, int userId) {
        try (var data = productCollection().data()) {
            return data
                .filter(userMatchPredicate(predicate, userId))
                .skip(range.start())
                .limit(range.length())
                .toArray(Product[]::new);
        }
    }

    public int countUserProducts(@NotNull Predicate<Product> predicate, int userId) {
        return countProducts(userMatchPredicate(predicate, userId));
    }

    public int getLastID() {
        return productCollection().getLastID();
    }

    public void setLastID(int newLastID) {
        productCollection().setLastID(newLastID);
    }

    @NotNull
    private static Predicate<Product> userMatchPredicate(@NotNull Predicate<Product> other, int userId) {
        return p -> p.getTraderId() == userId && other.test(p);
    }
}
