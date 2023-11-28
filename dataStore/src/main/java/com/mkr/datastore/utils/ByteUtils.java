package com.mkr.datastore.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public final class ByteUtils {
    public static final int INT32_SIZE = 4;
    public static final int BOOLEAN_SIZE = 1;

    public static byte[] booleanToBytes(boolean value) {
        return new byte[] { value ? Byte.MAX_VALUE : 0 };
    }

    public static boolean bytesToBoolean(byte[] bytes) {
        return bytes[0] != 0;
    }

    public static byte[] int32ToBytes(int value) {
        return ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(value)
                .array();
    }

    public static int bytesToInt32(byte[] bytes) {
        return ByteBuffer.allocate(4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(bytes)
                .rewind()
                .getInt();
    }

    public static byte[] stringToBytes(String string, Charset charset) {
        byte[] stringBytes = string.getBytes(charset);
        byte[] lengthBytes = int32ToBytes(stringBytes.length);

        byte[] bytes = new byte[lengthBytes.length + stringBytes.length];

        System.arraycopy(lengthBytes, 0, bytes, 0, lengthBytes.length);
        System.arraycopy(stringBytes, 0, bytes, lengthBytes.length, stringBytes.length);

        return bytes;
    }
}
