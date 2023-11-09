package com.mkr.datastore;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DataStoreCollectionDescriptorBuilder<T> {
    @Nullable
    private String name;

    @Nullable
    private Class<T> entityClass;

    @Nullable
    private Class<?> keysClass;

    public DataStoreCollectionDescriptorBuilder<T> name(String value) {
        name = value;
        return this;
    }

    public DataStoreCollectionDescriptorBuilder<T> entityClass(Class<T> value) {
        entityClass = value;
        return this;
    }

    public DataStoreCollectionDescriptorBuilder<T> keys(Class<?> value) {
        keysClass = value;
        return this;
    }

    public DataStoreCollectionDescriptor<T> build() {
        Objects.requireNonNull(name, "name is not set");
        Objects.requireNonNull(entityClass, "entityClass is not set");
        Objects.requireNonNull(keysClass, "keys is not set");

        return new DataStoreCollectionDescriptor<>(name, EntityStructure.create(entityClass, keysClass));
    }
}
