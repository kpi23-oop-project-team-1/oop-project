package com.mkr.datastore;

import org.jetbrains.annotations.NotNull;

public final class DataStoreConfiguration {
    private final DataStoreCollectionDescriptor<?>[] collectionDescriptors;

    public DataStoreConfiguration(@NotNull DataStoreCollectionDescriptor<?>[] collectionDescriptors) {
        this.collectionDescriptors = collectionDescriptors;
    }

    @NotNull
    public static DataStoreConfigurationBuilder builder() {
        return new DataStoreConfigurationBuilder();
    }

    @NotNull
    public DataStoreCollectionDescriptor<?>[] getCollectionDescriptors() {
        return collectionDescriptors;
    }
}
