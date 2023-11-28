package com.mkr.datastore.inFile;

import java.util.ArrayList;

public class ByteArrayBuilder {
    private final ArrayList<Byte> bytes;

    public ByteArrayBuilder() {
        bytes = new ArrayList<>();
    }

    public void add(byte[] array) {
        for (byte a : array) {
            bytes.add(a);
        }
    }

    public byte[] build() {
        int bytesSize = bytes.size();

        byte[] bytesPrimitive = new byte[bytesSize];

        for (int i = 0; i < bytesSize; i++) {
            bytesPrimitive[i] = bytes.get(i);
        }

        return bytesPrimitive;
    }
}
