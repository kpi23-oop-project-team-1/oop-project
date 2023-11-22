package com.mkr.datastore.inFile;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreConfiguration;
import com.mkr.datastore.DataStoreCollection;
import com.mkr.datastore.DataStoreCollectionDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class InFileDataStore implements DataStore {
    private final Map<DataStoreCollectionDescriptor<?>, DataStoreCollection<?>> collectionMap;
    private final Function<DataStoreCollectionDescriptor<?>, String> filePathProvider;

    public InFileDataStore(
            @NotNull DataStoreConfiguration configuration,
            @NotNull Function<DataStoreCollectionDescriptor<?>, String> filePathProvider
    ) {
        this.filePathProvider = filePathProvider;

        DataStoreCollectionDescriptor<?>[] descriptors = configuration.getCollectionDescriptors();

        collectionMap = new HashMap<>();
        for (DataStoreCollectionDescriptor<?> descriptor : descriptors) {
            var file = new File(getFilePath(descriptor));

            var fileController = new CollectionFileController<>(file, descriptor);
            // TODO: set chunk size and fragmentation threshold

            var document = new InFileCollection<>(fileController);

            collectionMap.put(descriptor, document);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <T> DataStoreCollection<T> getCollection(@NotNull DataStoreCollectionDescriptor<T> descriptor) {
        var document = (DataStoreCollection<T>) collectionMap.get(descriptor);
        if (document == null) {
            throw new IllegalArgumentException("Invalid collection descriptor");
        }

        return document;
    }

    @NotNull
    public String getFilePath(@NotNull DataStoreCollectionDescriptor<?> descriptor) {
        return filePathProvider.apply(descriptor);
    }
}
