package com.mkr.datastore.inMemory;

import com.mkr.datastore.queries.DeleteQuery;
import com.mkr.datastore.EntityStructure;
import com.mkr.datastore.EntityStructureKey;
import com.mkr.datastore.utils.Functions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class InMemoryDeleteQuery<TEntity> implements DeleteQuery<TEntity> {
    private final EntityStructure<TEntity> structure;
    private final List<TEntity> entities;
    private final Predicate<TEntity> entityFilter;

    public InMemoryDeleteQuery(@NotNull EntityStructure<TEntity> structure, @NotNull List<TEntity> entities) {
        this(structure, entities, Functions.truePredicate());
    }

    public InMemoryDeleteQuery(@NotNull EntityStructure<TEntity> structure, @NotNull List<TEntity> entities, @NotNull Predicate<TEntity> predicate) {
        this.structure = structure;
        this.entities = entities;
        this.entityFilter = predicate;
    }

    @Override
    @NotNull
    public DeleteQuery<TEntity> filter(@NotNull Predicate<TEntity> predicate) {
        return new InMemoryDeleteQuery<>(structure, entities, Functions.combine(entityFilter, predicate));
    }

    @Override
    @NotNull
    public <T> DeleteQuery<TEntity> filter(@NotNull EntityStructureKey<T> key, Predicate<T> predicate) {
        return filter(x -> predicate.test(structure.getKeyValue(x, key)));
    }

    @Override
    public void execute() {
        entities.removeIf(entityFilter);
    }
}
