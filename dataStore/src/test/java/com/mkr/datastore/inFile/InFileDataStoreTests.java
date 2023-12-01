package com.mkr.datastore.inFile;

import com.mkr.datastore.*;
import com.mkr.datastore.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class InFileDataStoreTests {
    private static final DataStoreConfiguration dataStoreConfig = DataStoreConfiguration.builder()
        .addCollection(TestDataStoreCollections.testObject)
        .build();

    private static final Function<DataStoreCollectionDescriptor<?>, String> filePathProvider = d -> d.getName() + ".bin";

    private static InFileDataStore dataStore;

    @BeforeEach
    public void setup() {
        dataStore = new InFileDataStore(dataStoreConfig, filePathProvider);

        // Make sure files will be deleted even if tests fail
        for (var descriptor: dataStoreConfig.getCollectionDescriptors()) {
            var file = new File(filePathProvider.apply(descriptor));
            file.deleteOnExit();
        }
    }

    @AfterEach
    public void deleteFiles() {
        // Make sure files will be deleted after each test
        for (var descriptor: dataStoreConfig.getCollectionDescriptors()) {
            var file = new File(filePathProvider.apply(descriptor));
            FileUtils.tryDelete(file);
        }
    }

    @Test
    public void insertTest() {
        var objects = new TestObject[]{
            new TestObject(true, "1", 1, TestEnum.VALUE1),
            new TestObject(false, "2", 2, TestEnum.VALUE2)
        };

        dataStore.getCollection(TestDataStoreCollections.testObject).insert(objects);

        try (var stream = dataStore.getCollection(TestDataStoreCollections.testObject).data()) {
            TestObject[] actualResult = stream.toArray(TestObject[]::new);

            assertArrayEquals(objects, actualResult);
        }
    }

    @Test
    public void deleteTest() {
        var objects = new TestObject[]{
            new TestObject(true, "1", 1, TestEnum.VALUE1),
            new TestObject(false, "2", 1, TestEnum.VALUE2),
            new TestObject(true, "3", 2, TestEnum.VALUE2)
        };

        dataStore.getCollection(TestDataStoreCollections.testObject).insert(objects);

        dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .delete(o -> o.getInteger() == 1);

        try (var stream = dataStore.getCollection(TestDataStoreCollections.testObject).data()) {
            TestObject[] actualResult = stream.toArray(TestObject[]::new);

            var expectedResult = new TestObject[]{
                    new TestObject(true, "3", 2, TestEnum.VALUE2)
            };

            assertArrayEquals(expectedResult, actualResult);
        }
    }

    @Test
    public void updateTest() {
        var objects = new TestObject[]{
            new TestObject(true, "1", 1, TestEnum.VALUE1),
            new TestObject(false, "2", 1, TestEnum.VALUE2),
            new TestObject(true, "3", 2, TestEnum.VALUE2)
        };

        dataStore.getCollection(TestDataStoreCollections.testObject).insert(objects);

        dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .update(
                o -> o.getInteger() == 1,
                o -> new TestObject(o.getBool(), o.getString() + "0", o.getInteger(), o.getTestEnum())
            );

        try (var stream = dataStore.getCollection(TestDataStoreCollections.testObject).data()) {
            TestObject[] actualResult = stream.toArray(TestObject[]::new);

            var expectedResult = new TestObject[]{
                    new TestObject(true, "10", 1, TestEnum.VALUE1),
                    new TestObject(false, "20", 1, TestEnum.VALUE2),
                    new TestObject(true, "3", 2, TestEnum.VALUE2)
            };

            assertArrayEquals(expectedResult, actualResult);
        }
    }
}
