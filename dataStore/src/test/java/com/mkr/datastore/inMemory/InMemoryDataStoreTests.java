package com.mkr.datastore.inMemory;

import com.mkr.datastore.DataStore;
import com.mkr.datastore.DataStoreConfiguration;
import com.mkr.datastore.TestDataStoreCollections;
import com.mkr.datastore.TestObject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InMemoryDataStoreTests {
    private static final DataStoreConfiguration dataStoreConfig = DataStoreConfiguration.builder()
        .addCollection(TestDataStoreCollections.testObject)
        .build();

    private static DataStore inMemoryDataStore(TestObject... objects) {
        var dataStore = new InMemoryDataStore(dataStoreConfig);

        if (objects.length > 0) {
            dataStore.getCollection(TestDataStoreCollections.testObject).insert(objects);
        }

        return dataStore;
    }

    @Test
    public void insertTest() {
        var objects = new TestObject[]{
            new TestObject(true, "1", 1),
            new TestObject(false, "2", 2)
        };

        var dataStore = inMemoryDataStore();
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
            new TestObject(true, "1", 1),
            new TestObject(false, "2", 1),
            new TestObject(true, "3", 2)
        };

        var dataStore = inMemoryDataStore(objects);

        dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .delete(o -> o.getInteger() == 1);

        TestObject[] actualResult = dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .data()
            .toArray(TestObject[]::new);

        var expectedResult = new TestObject[]{
            new TestObject(true, "3", 2)
        };

        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void updateTest() {
        var objects = new TestObject[]{
            new TestObject(true, "1", 1),
            new TestObject(false, "2", 1),
            new TestObject(true, "3", 2)
        };

        var dataStore = inMemoryDataStore(objects);

        dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .update(
                o -> o.getInteger() == 1,
                o -> new TestObject(o.getBool(), o.getString() + "0", o.getInteger())
            );

        TestObject[] actualResult = dataStore
            .getCollection(TestDataStoreCollections.testObject)
            .data()
            .toArray(TestObject[]::new);

        var expectedResult = new TestObject[]{
            new TestObject(true, "10", 1),
            new TestObject(false, "20", 1),
            new TestObject(true, "3", 2)
        };

        assertArrayEquals(expectedResult, actualResult);
    }
}