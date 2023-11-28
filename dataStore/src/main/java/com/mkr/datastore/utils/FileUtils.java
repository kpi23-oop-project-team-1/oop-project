package com.mkr.datastore.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileUtils {
    public static boolean readBoolAtPos(RandomAccessFile raf, long pos) {
        return ByteUtils.bytesToBoolean(readNBytesAtPos(raf, ByteUtils.BOOLEAN_SIZE, pos));
    }

    public static void writeBoolAtPos(RandomAccessFile raf, boolean value, long pos) {
        writeBytesAtPos(raf, ByteUtils.booleanToBytes(value), pos);
    }

    public static int readInt32AtPos(RandomAccessFile raf, long pos) {
        return ByteUtils.bytesToInt32(FileUtils.readNBytesAtPos(raf, ByteUtils.INT32_SIZE, pos));
    }

    public static void writeInt32AtPos(RandomAccessFile raf, int value, long pos) {
        writeBytesAtPos(raf, ByteUtils.int32ToBytes(value), pos);
    }

    public static byte[] readNBytesAtPos(RandomAccessFile raf, int n, long pos) {
        var bytes = new byte[n];

        try {
            raf.seek(pos);
            for (int i = 0; i < n; i++) {
                bytes[i] = raf.readByte();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);  // TODO: better exception handling
        }

        return bytes;
    }

    public static void writeBytesAtPos(RandomAccessFile raf, byte[] bytes, long pos) {
        try {
            raf.seek(pos);
            raf.write(bytes);
        } catch(IOException e) {
            throw new RuntimeException(e);  // TODO: better exception handling
        }
    }

    public static long getFileLength(RandomAccessFile raf) {
        long fileLength;

        try {
            fileLength = raf.length();
        } catch (IOException e) {
            throw new RuntimeException(e);  // TODO: better exception handling
        }

        return fileLength;
    }

    public static void tryDelete(File file) {
        if (file.delete()) return;
        throw new RuntimeException("Failed to delete file: \"%s\"".formatted(file.getName()));
    }

    public static void tryRename(File file, File newFile) {
        if (file.renameTo(newFile)) return;
        throw new RuntimeException("Failed to rename file: \"%s\"".formatted(file.getName()));
    }
}
