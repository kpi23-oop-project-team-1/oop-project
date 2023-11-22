package com.mkr.datastore.inFile;

import com.mkr.datastore.DataStoreCollectionDescriptor;
import com.mkr.datastore.DataStoreConfiguration;
import com.mkr.datastore.TestDataStoreCollections;
import com.mkr.datastore.TestObject;
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
            file.delete();
        }
    }

    @Test
    public void insertTest() {
        var objects = new TestObject[]{
            new TestObject("1", 1),
            new TestObject("2", 2)
        };

        dataStore.getCollection(TestDataStoreCollections.testObject).insert(objects);

        TestObject[] actualResult = dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .data()
            .toArray(TestObject[]::new);

        assertArrayEquals(objects, actualResult);
    }

    @Test
    public void deleteTest() {
        var objects = new TestObject[]{
            new TestObject("1", 1),
            new TestObject("2", 1),
            new TestObject("3", 2)
        };

        dataStore.getCollection(TestDataStoreCollections.testObject).insert(objects);

        dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .delete(o -> o.getInteger() == 1);

        TestObject[] actualResult = dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .data()
            .toArray(TestObject[]::new);

        var expectedResult = new TestObject[]{
            new TestObject("3", 2)
        };

        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void updateTest() {
        var objects = new TestObject[]{
            new TestObject("1", 1),
            new TestObject("2", 1),
            new TestObject("3", 2)
        };

        dataStore.getCollection(TestDataStoreCollections.testObject).insert(objects);

        dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .update(
                o -> o.getInteger() == 1,
                o -> new TestObject(o.getString() + "0", o.getInteger())
            );

        TestObject[] actualResult = dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .data()
            .toArray(TestObject[]::new);

        var expectedResult = new TestObject[]{
            new TestObject("10", 1),
            new TestObject("20", 1),
            new TestObject("3", 2)
        };

        assertArrayEquals(expectedResult, actualResult);
    }
}
