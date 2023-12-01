package com.mkr.server.config;

import com.mkr.datastore.DataStoreCollectionDescriptor;
import com.mkr.datastore.DataStoreCollectionDescriptorBuilder;
import com.mkr.datastore.DataStoreConfiguration;
import com.mkr.server.domain.Product;
import com.mkr.server.domain.User;

public final class DataStoreConfig {
    private DataStoreConfig() {
    }

    public static final DataStoreCollectionDescriptor<User> users = new DataStoreCollectionDescriptorBuilder<User>()
        .name("users")
        .entityClass(User.class)
        .build();

    public static final DataStoreCollectionDescriptor<Product> products = new DataStoreCollectionDescriptorBuilder<Product>()
        .name("products")
        .entityClass(Product.class)
        .build();

    public static final DataStoreConfiguration configuration = DataStoreConfiguration.builder()
        .addCollection(users)
        .addCollection(products)
        .build();

}
