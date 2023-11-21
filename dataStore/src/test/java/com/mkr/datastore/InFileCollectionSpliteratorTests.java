package com.mkr.datastore;

import com.mkr.datastore.inFile.CollectionFileController;
import com.mkr.datastore.inFile.InFileCollectionSpliterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InFileCollectionSpliteratorTests {
    private CollectionFileController<TestObject> fileController;
    private File file;

    @BeforeEach
    public void setup() {
        file = new File("tmp.bin");
        file.deleteOnExit();

        fileController = new CollectionFileController<>(file, TestDataStoreCollections.testObject);
    }

    @AfterEach
    public void deleteFile() {
        file.delete();
    }

    @Test
    public void tryAdvanceTest() {
        int objectsCount = 5;

        var objects = new TestObject[objectsCount];
        Arrays.setAll(objects, i -> new TestObject(String.valueOf(i), i));

        // Write objects and deactivate (delete) the first one
        writeEntities(objects);
        fileController.writeEntityIsActiveAtPos(false, fileController.getFirstEntityPos());

        // Create spliterator
        var spliterator = new InFileCollectionSpliterator<>(fileController);

        // Get elements from spliterator
        var actualObjects = new ArrayList<TestObject>();
        while (true) {
            if (!spliterator.tryAdvance(actualObjects::add)) break;
        }

        // Expect to get the same objects (except for the deleted one)
        assertEquals(objectsCount - 1, actualObjects.size());
        for (int i = 1; i < objectsCount; i++) {
            assertEquals(objects[i], actualObjects.get(i - 1));
        }
    }

    @Test
    public void trySplitTest() {
        int objectsCount = 5;

        var objects = new TestObject[objectsCount];
        Arrays.setAll(objects, i -> new TestObject(String.valueOf(i), i));

        // Write objects
        writeEntities(objects);

        // Create spliterators
        var spliterator1 = new InFileCollectionSpliterator<>(fileController);
        var spliterator2 = spliterator1.trySplit();

        // Get elements from spliterators
        var actualObjects1 = new ArrayList<TestObject>();
        while (true) {
            if (!spliterator1.tryAdvance(actualObjects1::add)) break;
        }

        var actualObjects2 = new ArrayList<TestObject>();
        while (true) {
            if (!spliterator2.tryAdvance(actualObjects2::add)) break;
        }

        // Expect spliterator 2 to contain the first half of elements
        assertEquals(objectsCount / 2, actualObjects2.size());
        for (int i = 0; i < actualObjects2.size(); i++) {
            assertEquals(objects[i], actualObjects2.get(i));
        }

        // Expect spliterator 1 to contain the second half of elements
        assertEquals(objectsCount - objectsCount / 2, actualObjects1.size());
        for (int i = 0; i < actualObjects1.size(); i++) {
            assertEquals(objects[actualObjects2.size() + i], actualObjects1.get(i));
        }
    }

    private void writeEntities(TestObject... entities) {
        for (var entity : entities) {
            fileController.writeEntityAtPos(entity, fileController.findFileEndPos());
        }
    }
}
