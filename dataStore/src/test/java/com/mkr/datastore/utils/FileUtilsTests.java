package com.mkr.datastore.utils;

import com.mkr.datastore.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUtilsTests {
    final String TMP_FILE_NAME = "tmp.bin";

    @Test
    public void test() throws FileNotFoundException {
        var bytes = new byte[] { Byte.MIN_VALUE, Byte.MAX_VALUE };
        var pos = 2;

        var file = new File(TMP_FILE_NAME);
        file.deleteOnExit();

        var raf = new RandomAccessFile(file, "rw");

        FileUtils.writeBytesAtPos(raf, bytes, pos);
        var actualBytes = FileUtils.readNBytesAtPos(raf, bytes.length, pos);

        for (int i = 0; i < bytes.length; i++) {
            assertEquals(bytes[i], actualBytes[i]);
        }
    }
}
