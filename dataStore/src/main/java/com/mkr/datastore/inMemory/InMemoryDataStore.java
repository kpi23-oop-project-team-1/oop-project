package com.mkr.datastore.inMemory;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreConfiguration;
import com.mkr.datastore.DataStoreCollection;
import com.mkr.datastore.DataStoreCollectionDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class InMemoryDataStore implements DataStore {
    private final Map<DataStoreCollectionDescriptor<?>, DataStoreCollection<?>> documentMap;

    public InMemoryDataStore(@NotNull DataStoreConfiguration configuration) {
        DataStoreCollectionDescriptor<?>[] descriptors = configuration.getCollectionDescriptors();
        documentMap = new HashMap<>();
        for (DataStoreCollectionDescriptor<?> descriptor : descriptors) {
            var document = new InMemoryCollection<>(descriptor);

            documentMap.put(descriptor, document);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <T> DataStoreCollection<T> getCollection(@NotNull DataStoreCollectionDescriptor<T> descriptor) {
        var document = (DataStoreCollection<T>)documentMap.get(descriptor);
        if (document == null) {
            throw new IllegalArgumentException("Invalid document descriptor");
        }

        return document;
    }
}
