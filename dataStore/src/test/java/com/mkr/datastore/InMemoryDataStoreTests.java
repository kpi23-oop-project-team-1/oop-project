package com.mkr.datastore;

import com.mkr.datastore.inMemory.InMemoryDataStore;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InMemoryDataStoreTests {
    private static final DataStoreConfiguration dataStoreConfig = DataStoreConfiguration.builder()
        .addCollection(TestDataStoreCollections.testObject)
        .build();

    private static DataStore inMemoryDataStore(TestObject... objects) {
        var dataStore = new InMemoryDataStore(dataStoreConfig);
        dataStore.getCollection(TestDataStoreCollections.testObject)
            .insert(objects)
            .execute();

        return dataStore;
    }

    @Test
    public void selectAllTest() {
        var objects = new TestObject[] {
            new TestObject("1", 1),
            new TestObject("2", 2)
        };

        var dataStore = inMemoryDataStore(objects);

        TestObject[] actualResult = dataStore.getCollection(TestDataStoreCollections.testObject)
            .select()
            .execute();

        assertArrayEquals(objects, actualResult);
    }

    @Test
    public void selectFilterPlainPredicateTest() {
        var objects = new TestObject[] {
            new TestObject("1", 1),
            new TestObject("2", 2)
        };

        var dataStore = inMemoryDataStore(objects);
        TestObject[] actualResult = dataStore.getCollection(TestDataStoreCollections.testObject)
            .select()
            .filter(o -> o.getString().equals("1"))
            .execute();

        var expectedResult = new TestObject[] {
            new TestObject("1", 1)
        };

        assertArrayEquals(expectedResult, actualResult);
    }

    @Test
    public void selectFilterKeyPredicateTest() {
        var objects = new TestObject[] {
            new TestObject("1", 1),
            new TestObject("2", 2)
        };

        var dataStore = inMemoryDataStore(objects);
        TestObject[] actualResult = dataStore.getCollection(TestDataStoreCollections.testObject)
            .select()
            .filter(TestObjectKeys.string, s -> s.equals("2"))
            .execute();

        var expectedResult = new TestObject[] {
            new TestObject("2", 2)
        };

        assertArrayEquals(expectedResult, actualResult);
    }
}
