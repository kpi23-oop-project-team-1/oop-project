package com.mkr.datastore.inFile;

import com.mkr.datastore.DataStoreCollectionDescriptor;
import com.mkr.datastore.EntityScheme;
import com.mkr.datastore.utils.ByteUtils;
import com.mkr.datastore.utils.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CollectionFileController<E> {
    static final long VERSION_POS = 0;
    static final long SCHEME_POS = ByteUtils.INT32_SIZE;

    private final DataStoreCollectionDescriptor<E> descriptor;
    private final RandomAccessFile raf;

    private InFileEntityScheme inFileScheme;

    private int chunkSize = 32;
    private final long firstEntityPos;

    public CollectionFileController(File file, DataStoreCollectionDescriptor<E> descriptor) throws FileNotFoundException {
        this.descriptor = descriptor;

        boolean fileExists = file.exists();

        raf = new RandomAccessFile(file, "rw");

        if (fileExists) {  // Read existing
            readInFileScheme();
        } else {  // Create new
            inFileScheme = new InFileEntityScheme(descriptor.getScheme());
            writeVersion(0);
            writeInFileScheme();
        }

        firstEntityPos = findFirstEntityPos();
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int value) {
        chunkSize = value;
    }

    public long getFirstEntityPos() {
        return firstEntityPos;
    }

    public void writeVersion(int version) {
        FileUtils.writeInt32AtPos(raf, version, VERSION_POS);
    }

    public int readVersion() {
        return FileUtils.readInt32AtPos(raf, VERSION_POS);
    }

    public void writeEntityIsActiveAtPos(boolean isActive, long pos) {
        FileUtils.writeBoolAtPos(raf, isActive, pos);
    }

    public boolean readEntityIsActiveAtPos(long pos) {
        return FileUtils.readBoolAtPos(raf, pos);
    }

    public int readEntitySizeAtPos(long pos) {
        return FileUtils.readInt32AtPos(raf, pos + ByteUtils.BOOLEAN_SIZE);
    }

    public void writeEntityAtPos(E entity, long pos) {
        EntityScheme<E> scheme = descriptor.getScheme();

        // Build entity bytes
        ByteArrayBuilder entityData = new ByteArrayBuilder();

        for (InFileEntitySchemeKey key : inFileScheme.getKeys()) {
            Object value = scheme.getKeyValue(entity, scheme.findKeyByName(key.name()));

            ByteArrayBuilder elementsData = new ByteArrayBuilder();

            Object[] elements;
            if (key.isArray()) {
                elements = (Object[]) value;
                elementsData.add(ByteUtils.int32ToBytes(elements.length));
            } else {
                elements = new Object[] { value };
            }

            for (var element : elements) {
                if (Objects.equals(key.valueTypeCode(), InFileEntityScheme.INT_CODE)) {
                    elementsData.add(ByteUtils.int32ToBytes((Integer) element));
                } else if (Objects.equals(key.valueTypeCode(), InFileEntityScheme.STRING_CODE)) {
                    elementsData.add(ByteUtils.stringToBytes((String) element, StandardCharsets.UTF_8));
                } else {
                    throwUnexpectedValueTypeCodeException(key.valueTypeCode());
                }
            }

            entityData.add(elementsData.build());
        }

        byte[] entityBytes = entityData.build();

        // Write entity bytes
        boolean createNewRecord;
        long offset = pos;

        if (pos >= getFileLength()) {  // No old record at this pos
            createNewRecord = true;
        } else {  // There is an old record at this pos
            createNewRecord = entityBytes.length > readEntitySizeAtPos(pos);
            if (createNewRecord) {
                writeEntityIsActiveAtPos(false, pos);  // Deactivate old record
                offset = getFileLength();  // Go to the end of file
            }
        }

        if (createNewRecord) {
            FileUtils.writeBoolAtPos(raf, true, offset);
        }
        offset += ByteUtils.BOOLEAN_SIZE;

        if (createNewRecord) {
            int fullSize = (int) Math.ceil((double) entityBytes.length / chunkSize) * chunkSize;
            FileUtils.writeBytesAtPos(raf, ByteUtils.int32ToBytes(fullSize), offset);
        }
        offset += ByteUtils.INT32_SIZE;

        FileUtils.writeBytesAtPos(raf, entityBytes, offset);
    }

    public E readEntityAtPos(long pos) {
        EntityScheme<E> scheme = descriptor.getScheme();

        long offset = pos;

        // Read isActive
        boolean isActive = ByteUtils.bytesToBoolean(FileUtils.readNBytesAtPos(raf, ByteUtils.BOOLEAN_SIZE, pos));
        offset += ByteUtils.BOOLEAN_SIZE;

        if (!isActive) return null;

        // Skip size
        offset += ByteUtils.INT32_SIZE;

        // Read entity
        E entity = scheme.createInstance(scheme.getEntityClass());

        for (InFileEntitySchemeKey key : inFileScheme.getKeys()) {
            int elementCount = 1;
            if (key.isArray()) {
                elementCount = FileUtils.readInt32AtPos(raf, offset);
                offset += ByteUtils.INT32_SIZE;
            }

            Object[] elements = instantiateArrayByTypeCode(key.valueTypeCode(), elementCount);
            for (int i = 0; i < elementCount; i++) {
                if (Objects.equals(key.valueTypeCode(), InFileEntityScheme.INT_CODE)) {
                    elements[i] = FileUtils.readInt32AtPos(raf, offset);
                    offset += ByteUtils.INT32_SIZE;
                } else if (Objects.equals(key.valueTypeCode(), InFileEntityScheme.STRING_CODE)) {
                    int stringBytesSize = FileUtils.readInt32AtPos(raf, offset);
                    offset += ByteUtils.INT32_SIZE;
                    byte[] stringBytes = FileUtils.readNBytesAtPos(raf, stringBytesSize, offset);
                    offset += stringBytes.length;
                    elements[i] = new String(stringBytes, StandardCharsets.UTF_8);
                }
            }

            scheme.setKeyValue(entity, scheme.findKeyByName(key.name()), key.isArray() ? elements : elements[0]);
        }

        return entity;
    }

    public int countEntitiesFromPos(long pos) {
        long offset = pos;
        int count = 0;

        while (true) {
            long nextEntityPos = findNextEntityPos(offset);

            count++;

            if (nextEntityPos < 0) break;

            offset = nextEntityPos;
        }

        return count;
    }

    public long findNextEntityPos(long currentEntityPos) {
        long nextEntityPos = currentEntityPos +
                ByteUtils.BOOLEAN_SIZE +
                ByteUtils.INT32_SIZE +
                FileUtils.readInt32AtPos(raf, currentEntityPos + ByteUtils.BOOLEAN_SIZE);

        if (nextEntityPos > getFileLength()) return -1;

        return nextEntityPos;
    }

    public long getFileLength() {
        long fileLength;

        try {
            fileLength = raf.length();
        } catch (IOException e) {
            throw new RuntimeException(e);  // TODO: better exception handling
        }

        return fileLength;
    }

    private void writeInFileScheme() {
        InFileEntitySchemeKey[] keys = inFileScheme.getKeys();

        ByteArrayBuilder keysData = new ByteArrayBuilder();

        keysData.add(ByteUtils.int32ToBytes(keys.length));
        for (InFileEntitySchemeKey key : keys) {
            keysData.add(ByteUtils.stringToBytes(key.name(), StandardCharsets.US_ASCII));
            keysData.add(ByteUtils.stringToBytes(key.valueTypeCode(), StandardCharsets.US_ASCII));
            keysData.add(ByteUtils.booleanToBytes(key.isArray()));
        }

        byte[] keysBytes = keysData.build();

        FileUtils.writeBytesAtPos(raf, keysBytes, SCHEME_POS);
    }

    private void readInFileScheme() {
        long offset = SCHEME_POS;

        // Read key count
        int keyCount = FileUtils.readInt32AtPos(raf, offset);
        offset += ByteUtils.INT32_SIZE;

        // Read keys
        InFileEntitySchemeKey[] keys = new InFileEntitySchemeKey[keyCount];

        for (int i = 0; i < keyCount; i++) {
            // Read key name
            int keyNameBytesSize = FileUtils.readInt32AtPos(raf, offset);
            offset += ByteUtils.INT32_SIZE;
            byte[] keyNameBytes = FileUtils.readNBytesAtPos(raf, keyNameBytesSize, offset);
            offset += keyNameBytesSize;

            // Read value type code
            int valueTypeBytesSize = FileUtils.readInt32AtPos(raf, offset);
            offset += ByteUtils.INT32_SIZE;
            byte[] valueTypeBytes = FileUtils.readNBytesAtPos(raf, valueTypeBytesSize, offset);
            offset += valueTypeBytesSize;

            // Read is array
            boolean isArray = FileUtils.readBoolAtPos(raf, offset);
            offset += ByteUtils.BOOLEAN_SIZE;

            keys[i] = new InFileEntitySchemeKey(
                    new String(keyNameBytes, StandardCharsets.US_ASCII),
                    new String(valueTypeBytes, StandardCharsets.US_ASCII),
                    isArray
            );
        }

        inFileScheme = new InFileEntityScheme(keys);
    }

    private long findFirstEntityPos() {
        long offset = SCHEME_POS;

        // Read key count
        int keyCount = FileUtils.readInt32AtPos(raf, offset);
        offset += ByteUtils.INT32_SIZE;

        // Skip scheme
        for (int i = 0; i < keyCount; i++) {
            // Skip name
            offset += ByteUtils.INT32_SIZE + FileUtils.readInt32AtPos(raf, offset);

            // Skip valueTypeCode
            offset += ByteUtils.INT32_SIZE + FileUtils.readInt32AtPos(raf, offset);

            // Skip isArray
            offset += ByteUtils.BOOLEAN_SIZE;
        }

        return offset;
    }

    private Object[] instantiateArrayByTypeCode(String valueTypeCode, int elementCount) {
        if (Objects.equals(valueTypeCode, InFileEntityScheme.INT_CODE)) {
            return new Integer[elementCount];
        } else if (Objects.equals(valueTypeCode, InFileEntityScheme.STRING_CODE)) {
            return new String[elementCount];
        }

        throwUnexpectedValueTypeCodeException(valueTypeCode);
        return null;
    }

    private void throwUnexpectedValueTypeCodeException(String valueTypeCode) {
        throw new UnsupportedValueTypeCodeException(
                "This value type code is not supported: \"%s\"".formatted(valueTypeCode));
    }
}
