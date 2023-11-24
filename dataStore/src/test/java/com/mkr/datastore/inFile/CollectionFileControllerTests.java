package com.mkr.datastore.inFile;

import com.mkr.datastore.TestDataStoreCollections;
import com.mkr.datastore.TestObjectWithArrays;
import com.mkr.datastore.utils.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionFileControllerTests {
    private CollectionFileController<TestObjectWithArrays> fileController;
    private final String fileName = "tmp.bin";

    @BeforeEach
    public void setup() {
        File file = new File(fileName);
        file.deleteOnExit();

        fileController = new CollectionFileController<>(fileName, TestDataStoreCollections.testObjectWithArrays);
    }

    @AfterEach
    public void deleteFile() {
        File file = new File(fileName);
        FileUtils.tryDelete(file);
    }

    @Test
    public void writeReadVersionTest() {
        int version = 10;

        fileController.openFile();

        fileController.writeVersion(version);

        int actualVersion = fileController.readVersion();

        fileController.closeFile();

        assertEquals(version, actualVersion);
    }

    @Test
    public void changeEntityToShorterTest() {
        TestObjectWithArrays testObjectShorter = new TestObjectWithArrays(
                true,
                "",
                0,
                new String[] {},
                new Integer[] {});
        TestObjectWithArrays testObjectLonger = new TestObjectWithArrays(
                false,
                "test",
                128,
                new String[] {"array1", "array2"},
                new Integer[] {11, 22, 33});

        fileController.openFile();

        long pos = fileController.getFirstEntityPos();
        fileController.setChunkSize(16);
        fileController.writeEntityAtPos(testObjectLonger, pos);
        fileController.writeEntityAtPos(testObjectShorter, pos);  // Should make a new record in the same place

        TestObjectWithArrays actualObject = fileController.readEntityAtPos(pos);

        fileController.closeFile();

        assertEquals(testObjectShorter, actualObject);
    }

    @Test
    public void changeEntityToLongerTest() {
        TestObjectWithArrays testObjectShorter = new TestObjectWithArrays(
                true,
                "",
                0,
                new String[] {},
                new Integer[] {});
        TestObjectWithArrays testObjectLonger = new TestObjectWithArrays(
                false,
                "test",
                128,
                new String[] {"array1", "array2"},
                new Integer[] {11, 22, 33});

        fileController.openFile();

        long pos = fileController.getFirstEntityPos();
        fileController.setChunkSize(16);
        fileController.writeEntityAtPos(testObjectShorter, pos);
        fileController.writeEntityAtPos(testObjectLonger, pos);  // Should make a new record next to the old one

        long nextPos = fileController.findNextEntityPos(pos);
        TestObjectWithArrays actualObject = fileController.readEntityAtPos(nextPos);

        fileController.closeFile();

        assertEquals(testObjectLonger, actualObject);
    }

    @Test
    public void defragmentTest() {
        fileController.openFile();

        fileController.setChunkSize(16);
        fileController.setFragmentationThreshold(0.25f);

        int testObjectsCount = 10;
        var testObjects = new TestObjectWithArrays[testObjectsCount];
        for (int i = 0; i < testObjectsCount; i++) {
            testObjects[i] = new TestObjectWithArrays(
                    i % 2 == 0,
                    String.valueOf(i),
                    i,
                    new String[] {},
                    new Integer[] {});
        }

        // Write objects
        for (var testObject : testObjects) {
            fileController.writeEntityAtPos(testObject, fileController.findFileEndPos());
        }

        // Deactivate half of records
        long offset = fileController.getFirstEntityPos();
        while (offset >= 0) {
            fileController.writeEntityIsActiveAtPos(false, offset);
            offset = fileController.findNextEntityPos(offset);
            offset = fileController.findNextEntityPos(offset);
        }

        // Make sure that fragmentation coefficient has increased
        assertTrue(fileController.calculateFragmentationCoefficient() >= 0.25f);

        // Defragment
        fileController.defragmentIfNeeded();

        // Make sure that fragmentation coefficient is now zero
        assertEquals(0, fileController.calculateFragmentationCoefficient());

        fileController.closeFile();
    }
}
