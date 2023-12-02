package com.mkr.datastore.inFile;

import com.mkr.datastore.TestDataStoreCollections;
import com.mkr.datastore.TestEnum;
import com.mkr.datastore.TestObject;
import com.mkr.datastore.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InFileCollectionSpliteratorTests {
    private CollectionFileController<TestObject> fileController;
    private final String fileName = "tmp.bin";

    @BeforeEach
    public void setup() {
        File file = new File(fileName);
        file.deleteOnExit();

        fileController = new CollectionFileController<>(fileName, TestDataStoreCollections.testObject);

        fileController.openFile();
    }

    @AfterEach
    public void deleteFile() {
        fileController.closeFile();

        File file = new File(fileName);
        FileUtils.tryDelete(file);
    }

    @Test
    public void tryAdvanceTest() {
        int objectsCount = 5;

        var objects = new TestObject[objectsCount];
        Arrays.setAll(
                objects,
                i -> new TestObject(
                        i % 2 == 0,
                        String.valueOf(i),
                        i,
                        (long) i,
                        i % 2 == 0 ? TestEnum.VALUE1 : TestEnum.VALUE2
                )
        );

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
        Arrays.setAll(
                objects,
                i -> new TestObject(
                        i % 2 == 0,
                        String.valueOf(i),
                        i,
                        (long) i,
                        i % 2 == 0 ? TestEnum.VALUE1 : TestEnum.VALUE2
                )
        );

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
