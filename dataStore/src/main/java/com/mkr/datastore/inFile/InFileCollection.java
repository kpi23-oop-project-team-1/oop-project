package com.mkr.datastore.inFile;

import com.mkr.datastore.DataStoreCollection;
import com.mkr.datastore.DataStoreCollectionDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class InFileCollection<E> implements DataStoreCollection<E> {
    private final CollectionFileController<E> fileController;

    private final Lock readLock;
    private final Lock writeLock;

    public InFileCollection(CollectionFileController<E> fileController) {
        this.fileController = fileController;

        var lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    @Override
    @NotNull
    public DataStoreCollectionDescriptor<E> getDescriptor() {
        return fileController.getDescriptor();
    }

    @Override
    @NotNull
    public Stream<E> data() {
        readLock.lock();

        fileController.openFile();

        var spliterator = new InFileCollectionSpliterator<>(fileController);

        return StreamSupport.stream(spliterator, false).onClose(this::onStreamClose);
    }

    @SafeVarargs
    @Override
    public final void insert(@NotNull E... values) {
        writeLock.lock();

        try {
            fileController.openFile();

            for (var entity : values) {
                fileController.writeEntityAtPos(entity, fileController.findFileEndPos());
            }
        } finally {
            fileController.closeFile();
            writeLock.unlock();
        }
    }

    @Override
    public void update(
            @NotNull Predicate<E> predicate,
            @NotNull Function<E, E> transformEntity
    ) {
        writeLock.lock();

        try {
            fileController.openFile();

            long offset = fileController.getFirstEntityPos();
            int entitiesCount = fileController.countEntitiesFromPos(offset);

            for (int i = 0; i < entitiesCount; i++) {
                E entity = fileController.readEntityAtPos(offset);

                if (entity != null && predicate.test(entity)) {
                    E newEntity = transformEntity.apply(entity);

                    fileController.writeEntityAtPos(newEntity, offset);
                }

                offset = fileController.findNextEntityPos(offset);
            }

            fileController.defragmentIfNeeded();
        } finally {
            fileController.closeFile();
            writeLock.unlock();
        }
    }

    @Override
    public void delete(Predicate<E> predicate) {
        writeLock.lock();

        try {
            fileController.openFile();

            long offset = fileController.getFirstEntityPos();

            while (offset >= 0) {
                E entity = fileController.readEntityAtPos(offset);

                if (entity != null && predicate.test(entity)) {
                    fileController.writeEntityIsActiveAtPos(false, offset);
                }

                offset = fileController.findNextEntityPos(offset);
            }

            fileController.defragmentIfNeeded();
        } finally {
            fileController.closeFile();
            writeLock.unlock();
        }
    }

    @Override
    public int getLastID() {
        int lastID;

        readLock.lock();

        try {
            fileController.openFile();

            lastID = fileController.readLastID();
        } finally {
            fileController.closeFile();
            readLock.unlock();
        }

        return lastID;
    }

    @Override
    public void setLastID(int newLastID) {
        writeLock.lock();

        try {
            fileController.openFile();

            fileController.writeLastID(newLastID);
        } finally {
            fileController.closeFile();
            writeLock.unlock();
        }
    }

    private void onStreamClose() {
        fileController.closeFile();
        readLock.unlock();
    }
}
