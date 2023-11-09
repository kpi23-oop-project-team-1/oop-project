package com.mkr.datastore;

import org.jetbrains.annotations.NotNull;

public final class DataStoreCollectionDescriptor<T> {
    private final String name;
    private final EntityStructure<T> structure;

    public DataStoreCollectionDescriptor(@NotNull String name, @NotNull EntityStructure<T> structure) {
        this.name = name;
        this.structure = structure;
    }

    @NotNull
    public static <T> DataStoreCollectionDescriptorBuilder<T> builder() {
        return new DataStoreCollectionDescriptorBuilder<>();
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public EntityStructure<T> getStructure() {
        return structure;
    }
}
