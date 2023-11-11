package com.mkr.datastore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DataStoreConfigurationBuilder {
    private final List<DataStoreCollectionDescriptor<?>> documentDescriptorsList;

    public DataStoreConfigurationBuilder() {
        documentDescriptorsList = new ArrayList<>();
    }

    @NotNull
    public DataStoreConfigurationBuilder addCollection(DataStoreCollectionDescriptor<?> descriptor) {
        documentDescriptorsList.add(descriptor);

        return this;
    }

    public DataStoreConfiguration build() {
        var documentDescriptors = documentDescriptorsList.toArray(new DataStoreCollectionDescriptor<?>[0]);

        return new DataStoreConfiguration(documentDescriptors);
    }
}
