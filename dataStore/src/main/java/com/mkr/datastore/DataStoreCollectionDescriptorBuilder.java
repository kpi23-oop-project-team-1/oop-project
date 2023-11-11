package com.mkr.datastore;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DataStoreCollectionDescriptorBuilder<T> {
    @Nullable
    private String name;

    @Nullable
    private Class<T> entityClass;

    public DataStoreCollectionDescriptorBuilder<T> name(String value) {
        name = value;
        return this;
    }

    public DataStoreCollectionDescriptorBuilder<T> entityClass(Class<T> value) {
        entityClass = value;
        return this;
    }

    public DataStoreCollectionDescriptor<T> build() {
        Objects.requireNonNull(name, "name is not set");
        Objects.requireNonNull(entityClass, "entityClass is not set");

        return new DataStoreCollectionDescriptor<>(name, EntityScheme.createFromClass(entityClass));
    }
}
