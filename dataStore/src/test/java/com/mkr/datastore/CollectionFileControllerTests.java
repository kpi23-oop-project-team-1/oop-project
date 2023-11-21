package com.mkr.datastore;

import com.mkr.datastore.inFile.CollectionFileController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionFileControllerTests {
    private CollectionFileController<TestObjectWithArrays> controller;
    private File file;
    
    @BeforeEach
    public void setup() throws IOException {
        file = new File("tmp.bin");
        controller = new CollectionFileController<>(file, TestDataStoreCollections.testObjectWithArrays);
    }

    @AfterEach
    public void deleteFile() {
        file.delete();
    }

    @Test
    public void writeReadVersion() {
        int version = 10;

        controller.writeVersion(version);
        int actualVersion = controller.readVersion();

        assertEquals(version, actualVersion);
    }

    @Test
    public void changeEntityToShorter() {
        TestObjectWithArrays testObjectShorter = new TestObjectWithArrays(
                "",
                0,
                new String[] {},
                new Integer[] {});
        TestObjectWithArrays testObjectLonger = new TestObjectWithArrays(
                "test",
                128,
                new String[] {"array1", "array2"},
                new Integer[] {11, 22, 33});

        long pos = controller.getFirstEntityPos();
        controller.setChunkSize(16);
        controller.writeEntityAtPos(testObjectLonger, pos);
        controller.writeEntityAtPos(testObjectShorter, pos);  // Should make a new record in the same place

        TestObjectWithArrays actualObject = controller.readEntityAtPos(pos);

        assertEquals(testObjectShorter, actualObject);
    }

    @Test
    public void changeEntityToLonger() {
        TestObjectWithArrays testObjectShorter = new TestObjectWithArrays(
                "",
                0,
                new String[] {},
                new Integer[] {});
        TestObjectWithArrays testObjectLonger = new TestObjectWithArrays(
                "test",
                128,
                new String[] {"array1", "array2"},
                new Integer[] {11, 22, 33});

        long pos = controller.getFirstEntityPos();
        controller.setChunkSize(16);
        controller.writeEntityAtPos(testObjectShorter, pos);
        controller.writeEntityAtPos(testObjectLonger, pos);  // Should make a new record next to the old one

        long nextPos = controller.findNextEntityPos(pos);
        TestObjectWithArrays actualObject = controller.readEntityAtPos(nextPos);

        assertEquals(testObjectLonger, actualObject);
    }
}
