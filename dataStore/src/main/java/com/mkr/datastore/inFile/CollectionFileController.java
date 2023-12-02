package com.mkr.datastore.inFile;

import com.mkr.datastore.DataStoreCollectionDescriptor;
import com.mkr.datastore.EntityScheme;
import com.mkr.datastore.utils.ByteUtils;
import com.mkr.datastore.utils.FileUtils;
import com.mkr.datastore.utils.TypeUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CollectionFileController<T> {
    private static final long LAST_ID_POS = 0;
    private static final long ENTITIES_POS = ByteUtils.INT32_SIZE;

    private final String filePath;
    private final DataStoreCollectionDescriptor<T> descriptor;

    private RandomAccessFile raf;
    private boolean isOpen;

    private int chunkSize = 32;
    private float fragmentationThreshold = 0.5f;

    public CollectionFileController(String filePath, DataStoreCollectionDescriptor<T> descriptor) {
        this.filePath = filePath;
        this.descriptor = descriptor;
        isOpen = false;
    }

    public DataStoreCollectionDescriptor<T> getDescriptor() {
        return descriptor;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int value) {
        chunkSize = value;
    }

    public float getFragmentationThreshold() {
        return fragmentationThreshold;
    }

    public void setFragmentationThreshold(float value) {
        fragmentationThreshold = value;
    }

    public long getFirstEntityPos() {
        return ENTITIES_POS;
    }

    public void writeLastID(int id) {
        verifyIsOpen();
        FileUtils.writeInt32AtPos(raf, id, LAST_ID_POS);
    }

    public int readLastID() {
        verifyIsOpen();
        return FileUtils.readInt32AtPos(raf, LAST_ID_POS);
    }

    public void writeEntityIsActiveAtPos(boolean isActive, long pos) {
        verifyIsOpen();
        FileUtils.writeBoolAtPos(raf, isActive, pos);
    }

    public boolean readEntityIsActiveAtPos(long pos) {
        verifyIsOpen();
        return FileUtils.readBoolAtPos(raf, pos);
    }

    public int readEntitySizeAtPos(long pos) {
        verifyIsOpen();
        return FileUtils.readInt32AtPos(raf, pos + ByteUtils.BOOLEAN_SIZE);
    }

    public <E extends T> void writeEntityAtPos(E entity, long pos) {
        verifyIsOpen();

        EntityScheme<T> scheme = descriptor.getScheme();
        Class<? extends T> entityClass = (Class<? extends T>) entity.getClass();

        // Build entity bytes
        ByteArrayBuilder entityData = new ByteArrayBuilder();

        entityData.add(
                ByteUtils.stringToBytes(
                        scheme.getIdForSubclass(entityClass),
                        StandardCharsets.US_ASCII
                )
        );

        for (var key : scheme.getKeysForSubclass(entityClass)) {
            Object value = scheme.getKeyValue(entity, key);

            Class<?> valueType = key.valueType();
            Class<?> valueElementType = valueType.isArray() ? valueType.getComponentType() : valueType;

            ByteArrayBuilder elementsData = new ByteArrayBuilder();

            Object[] elements;
            if (valueType.isArray()) {
                elements = (Object[]) value;
                elementsData.add(ByteUtils.int32ToBytes(elements.length));
            } else {
                elements = new Object[] { value };
            }

            for (var element : elements) {
                if (valueElementType == Boolean.class) {
                    elementsData.add(ByteUtils.booleanToBytes((Boolean) element));
                } else if (valueElementType == Integer.class) {
                    elementsData.add(ByteUtils.int32ToBytes((Integer) element));
                } else if (valueElementType == Long.class) {
                    elementsData.add(ByteUtils.int64ToBytes((Long) element));
                } else if (valueElementType == String.class) {
                    elementsData.add(ByteUtils.stringToBytes((String) element, StandardCharsets.UTF_8));
                } else if (valueElementType.isEnum()) {
                    elementsData.add(ByteUtils.stringToBytes(element.toString(), StandardCharsets.US_ASCII));
                } else {
                    throw new UnsupportedValueTypeException(valueElementType);
                }
            }

            entityData.add(elementsData.build());
        }

        byte[] entityBytes = entityData.build();

        // Write entity bytes
        long offset = pos;
        long fileLength = findFileEndPos();
        boolean createNewRecord;

        if (pos >= fileLength) {  // No old record at this pos
            createNewRecord = true;
        } else {  // There is an old record at this pos
            createNewRecord = entityBytes.length > readEntitySizeAtPos(pos);
            if (createNewRecord) {
                writeEntityIsActiveAtPos(false, pos);  // Deactivate old record
                offset = fileLength;  // Go to the end of file
            }
        }

        if (createNewRecord) {  // Write isActive
            FileUtils.writeBoolAtPos(raf, true, offset);
        }
        offset += ByteUtils.BOOLEAN_SIZE;

        if (createNewRecord) {  // Write size
            int fullSize = calculateFullSize(entityBytes.length);
            FileUtils.writeBytesAtPos(raf, ByteUtils.int32ToBytes(fullSize), offset);
        }
        offset += ByteUtils.INT32_SIZE;

        FileUtils.writeBytesAtPos(raf, entityBytes, offset);  // Write entity bytes
        offset += entityBytes.length;

        if (createNewRecord) {  // Write additional bytes to fill the chunk
            int fullSize = calculateFullSize(entityBytes.length);
            byte[] additionalBytes = new byte[fullSize - entityBytes.length];
            Arrays.fill(additionalBytes, (byte) 0);
            FileUtils.writeBytesAtPos(raf, additionalBytes, offset);
        }
    }

    public T readEntityAtPos(long pos) {
        verifyIsOpen();

        EntityScheme<T> scheme = descriptor.getScheme();

        long offset = pos;

        // Read isActive
        boolean isActive = readEntityIsActiveAtPos(offset);
        offset += ByteUtils.BOOLEAN_SIZE;

        if (!isActive) return null;

        // Skip size
        offset += ByteUtils.INT32_SIZE;

        // Read entity
        int classIdBytesSize = FileUtils.readInt32AtPos(raf, offset);
        offset += ByteUtils.INT32_SIZE;
        byte[] classIdBytes = FileUtils.readNBytesAtPos(raf, classIdBytesSize, offset);
        offset += classIdBytes.length;
        String entityClassId = new String(classIdBytes, StandardCharsets.US_ASCII);
        Class<? extends T> entityClass = scheme.resolveClassById(entityClassId);

        T entity = scheme.createInstance(entityClass);

        for (var key : scheme.getKeysForSubclass(entityClass)) {
            Class<?> valueType = key.valueType();
            Class<?> valueElementType = valueType.isArray() ? valueType.getComponentType() : valueType;

            int elementCount = 1;
            if (valueType.isArray()) {
                elementCount = FileUtils.readInt32AtPos(raf, offset);
                offset += ByteUtils.INT32_SIZE;
            }

            Object[] elements = instantiateArrayByElementType(valueElementType, elementCount);
            for (int i = 0; i < elementCount; i++) {
                if (valueElementType == Boolean.class) {
                    elements[i] = FileUtils.readBoolAtPos(raf, offset);
                    offset += ByteUtils.BOOLEAN_SIZE;
                } else if (valueElementType == Integer.class) {
                    elements[i] = FileUtils.readInt32AtPos(raf, offset);
                    offset += ByteUtils.INT32_SIZE;
                } else if (valueElementType == Long.class) {
                    elements[i] = FileUtils.readInt64AtPos(raf, offset);
                    offset += ByteUtils.INT64_SIZE;
                } else if (valueElementType == String.class) {
                    int stringBytesSize = FileUtils.readInt32AtPos(raf, offset);
                    offset += ByteUtils.INT32_SIZE;
                    byte[] stringBytes = FileUtils.readNBytesAtPos(raf, stringBytesSize, offset);
                    offset += stringBytes.length;
                    elements[i] = new String(stringBytes, StandardCharsets.UTF_8);
                } else if (valueElementType.isEnum()) {
                    int stringBytesSize = FileUtils.readInt32AtPos(raf, offset);
                    offset += ByteUtils.INT32_SIZE;
                    byte[] stringBytes = FileUtils.readNBytesAtPos(raf, stringBytesSize, offset);
                    offset += stringBytes.length;

                    String stringValue = new String(stringBytes, StandardCharsets.US_ASCII);

                    elements[i] = TypeUtils.instantiateEnumByString(valueElementType, stringValue);
                }
            }

            scheme.setKeyValue(entity, key, valueType.isArray() ? elements : elements[0]);
        }

        return entity;
    }

    public void defragmentIfNeeded() {
        float fragmentationCoefficient = calculateFragmentationCoefficient();

        if (fragmentationCoefficient < fragmentationThreshold) return;

        defragment();
    }

    public void defragment() {
        verifyIsOpen();

        // Create new collection file
        var tmpFilePath = filePath + "1";
        var tmpFileController = new CollectionFileController<>(tmpFilePath, descriptor);

        // Write only active entities
        tmpFileController.openFile();

        long offset = getFirstEntityPos();
        while (offset >= 0) {
            boolean isActive = readEntityIsActiveAtPos(offset);

            if (isActive) {
                T entity = readEntityAtPos(offset);

                tmpFileController.writeEntityAtPos(entity, tmpFileController.findFileEndPos());
            }

            offset = findNextEntityPos(offset);
        }

        tmpFileController.closeFile();

        // Delete old file and rename a new one
        File currentFile = new File(filePath);
        File tmpFile = new File(tmpFilePath);

        closeFile();
        FileUtils.tryDelete(currentFile);
        FileUtils.tryRename(tmpFile, new File(filePath));
        openFile();
    }

    public float calculateFragmentationCoefficient() {
        verifyIsOpen();

        long unusedBytes = 0;

        long offset = getFirstEntityPos();
        while (offset >= 0) {
            boolean isActive = readEntityIsActiveAtPos(offset);

            if (!isActive) {
                unusedBytes += readEntitySizeAtPos(offset);
            }

            offset = findNextEntityPos(offset);
        }

        return (float) unusedBytes / findFileEndPos();
    }

    public int countEntitiesFromPos(long pos) {
        verifyIsOpen();

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
        verifyIsOpen();

        long nextEntityPos = currentEntityPos +
                ByteUtils.BOOLEAN_SIZE +
                ByteUtils.INT32_SIZE +
                FileUtils.readInt32AtPos(raf, currentEntityPos + ByteUtils.BOOLEAN_SIZE);

        if (nextEntityPos >= findFileEndPos()) return -1;

        return nextEntityPos;
    }

    public long findFileEndPos() {
        verifyIsOpen();
        return FileUtils.getFileLength(raf);
    }

    public void openFile() {
        if (isOpen) return;

        File file = new File(filePath);

        boolean fileExists = file.exists();

        try {
            raf = new RandomAccessFile(file, "rw");
            isOpen = true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (!fileExists) {
            writeLastID(0);
        }
    }

    public void closeFile() {
        if (!isOpen) return;

        try {
            raf.close();
            isOpen = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] instantiateArrayByElementType(Class<?> valueElementType, int elementCount) {
        if (valueElementType == Boolean.class) {
            return new Boolean[elementCount];
        } else if (valueElementType == Integer.class) {
            return new Integer[elementCount];
        } else if (valueElementType == Long.class) {
            return new Long[elementCount];
        } else if (valueElementType == String.class) {
            return new String[elementCount];
        } else if (valueElementType.isEnum()) {
            return new Enum[elementCount];
        }

        throw new UnsupportedValueTypeException(valueElementType);
    }

    private int calculateFullSize(int entitySize) {
        return (int) Math.ceil((double) entitySize / chunkSize) * chunkSize;
    }

    private void verifyIsOpen() {
        if (isOpen) return;
        throw new CollectionFileIsClosedException("Can't read/write while the file is closed");
    }
}
