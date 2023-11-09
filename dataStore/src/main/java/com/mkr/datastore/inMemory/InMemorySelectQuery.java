package com.mkr.datastore.inMemory;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class InMemorySelectQuery<T> extends InMemoryBaseSelectQuery<InMemorySelectQuery<T>, T> {
    public InMemorySelectQuery(@NotNull Class<T> valueClass, @NotNull List<T> entities) {
        super(valueClass, entities);
    }

    public InMemorySelectQuery(@NotNull Class<T> valueClass, @NotNull Stream<T> entityStream) {
        super(valueClass, entityStream);
    }

    @Override
    @NotNull
    protected InMemorySelectQuery<T> withMutatedStream(@NotNull Stream<T> newStream) {
        return new InMemorySelectQuery<>(valueClass, newStream);
    }
}
