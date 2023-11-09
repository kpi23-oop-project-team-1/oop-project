package com.mkr.datastore.inMemory;

import com.mkr.datastore.queries.NoResultQuery;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InMemoryInsertQuery<T> implements NoResultQuery {
    private final List<T> entities;
    private final T[] values;

    public InMemoryInsertQuery(@NotNull List<T> entities, @NotNull T[] values) {
        this.entities = entities;
        this.values = values;
    }

    @Override
    public void execute() {
        entities.addAll(List.of(values));
    }
}
