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
        System.out.println("\n\n\ndata() in collection " + getDescriptor().getName());

        readLock.lock();

        fileController.openFile();

        var spliterator = new InFileCollectionSpliterator<>(fileController);

        return StreamSupport.stream(spliterator, false).onClose(this::onStreamClose);
    }

    @SafeVarargs
    @Override
    public final void insert(@NotNull E... values) {
        System.out.println("\n\n\ninsert() in collection " + getDescriptor().getName());

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
        System.out.println("\n\n\nupdate() in collection " + getDescriptor().getName());

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
        System.out.println("\n\n\ndelete() in collection " + getDescriptor().getName());

        writeLock.lock();

        try {
            fileController.openFile();

            long offset = fileController.getFirstEntityPos();
            if (offset == fileController.findFileEndPos()) {
                return;
            }

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
        System.out.println("\n\n\ngetLastID() in collection " + getDescriptor().getName());

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
        System.out.println("\n\n\nsetLastID() in collection " + getDescriptor().getName());

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
        System.out.println("\n\n\nonStreamClose() in collection " + getDescriptor().getName());

        fileController.closeFile();
        readLock.unlock();
    }
}
