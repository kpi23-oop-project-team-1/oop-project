package com.mkr.server.repositories;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreCollection;
import com.mkr.server.config.DataStoreConfig;
import com.mkr.server.domain.Comment;
import com.mkr.server.domain.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public void addComment(@NotNull Comment comment) {
        productCollection().update(
            p -> p.getProductId() == comment.getTargetId(),
            p -> p.withComment(comment)
        );
    }
}
