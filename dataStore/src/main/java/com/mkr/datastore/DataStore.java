package com.mkr.datastore;

import org.jetbrains.annotations.NotNull;

public interface DataStore {
    @NotNull
    <T> DataStoreCollection<T> getCollection(@NotNull DataStoreCollectionDescriptor<T> descriptor);
}