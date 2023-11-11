package com.mkr.datastore;

import org.jetbrains.annotations.NotNull;

public final class DataStoreCollectionDescriptor<T> {
    private final String name;
    private final EntityScheme<T> scheme;

    public DataStoreCollectionDescriptor(@NotNull String name, @NotNull EntityScheme<T> scheme) {
        this.name = name;
        this.scheme = scheme;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public EntityScheme<T> getScheme() {
        return scheme;
    }
}
