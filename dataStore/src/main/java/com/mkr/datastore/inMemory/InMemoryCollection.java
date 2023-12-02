package com.mkr.datastore.inMemory;

import com.mkr.datastore.DataStoreCollection;
import com.mkr.datastore.DataStoreCollectionDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InMemoryCollection<E> implements DataStoreCollection<E> {
    private final DataStoreCollectionDescriptor<E> descriptor;
    private final List<E> entities;
    private int lastID;

    private final Lock readLock;
    private final Lock writeLock;

    public InMemoryCollection(@NotNull DataStoreCollectionDescriptor<E> descriptor) {
        this.descriptor = descriptor;

        entities = new ArrayList<>();
        lastID = 0;

        var lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    @Override
    @NotNull
    public DataStoreCollectionDescriptor<E> getDescriptor() {
        return descriptor;
    }

    @Override
    @NotNull
    public Stream<E> data() {
        readLock.lock();

        return entities.stream().onClose(readLock::unlock);
    }

    @SafeVarargs
    @Override
    public final void insert(@NotNull E... values) {
        writeLock.lock();

        try {
            entities.addAll(List.of(values));
        } finally {
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
            for (int i = 0; i < entities.size(); i++) {
                E entity = entities.get(i);

                if (predicate.test(entity)) {
                    E newEntity = transformEntity.apply(entity);

                    entities.set(i, newEntity);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(Predicate<E> predicate) {
        writeLock.lock();

        try {
            entities.removeIf(predicate);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int getLastID() {
        return lastID;
    }

    @Override
    public void setLastID(int newLastID) {
        lastID = newLastID;
    }
}
