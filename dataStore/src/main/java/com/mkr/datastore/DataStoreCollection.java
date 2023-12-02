package com.mkr.datastore;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface DataStoreCollection<E> {
    @NotNull
    DataStoreCollectionDescriptor<E> getDescriptor();

    @NotNull
    Stream<E> data();

    void insert(@NotNull E... values);

    void update(
        @NotNull Predicate<E> predicate,
        @NotNull Function<E, E> transformEntity
    );

    void delete(Predicate<E> predicate);

    int getLastID();

    void setLastID(int newLastID);
}
