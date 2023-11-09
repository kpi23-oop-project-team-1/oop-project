package com.mkr.datastore.queries.normalized;

import com.mkr.datastore.EntityStructure;
import com.mkr.datastore.EntityStructureKey;
import com.mkr.datastore.queries.DeleteQuery;
import com.mkr.datastore.utils.EntityStructureKeyPredicateMap;
import com.mkr.datastore.utils.Functions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class NormalizedDeleteQuery<TEntity> implements DeleteQuery<TEntity> {
    protected final EntityStructure<TEntity> structure;

    @Nullable
    protected final Predicate<TEntity> plainPredicate;
    protected final EntityStructureKeyPredicateMap keyPredicates;

    protected NormalizedDeleteQuery(@NotNull EntityStructure<TEntity> structure) {
        this.structure = structure;

        plainPredicate = null;
        keyPredicates = new EntityStructureKeyPredicateMap();
    }

    protected NormalizedDeleteQuery(
        @NotNull EntityStructure<TEntity> structure,
        @Nullable Predicate<TEntity> plainPredicate,
        @NotNull EntityStructureKeyPredicateMap keyPredicates
    ) {
        this.structure = structure;
        this.plainPredicate = plainPredicate;
        this.keyPredicates = keyPredicates;
    }

    @Override
    @NotNull
    public DeleteQuery<TEntity> filter(@NotNull Predicate<TEntity> predicate) {
        return createSelf(Functions.combine(plainPredicate, predicate), keyPredicates);
    }

    @Override
    @NotNull
    public <K> DeleteQuery<TEntity> filter(@NotNull EntityStructureKey<K> key, Predicate<K> predicate) {
        var newKeyPredicates = keyPredicates.withMerged(key, predicate, Functions::combine);

        return createSelf(plainPredicate, newKeyPredicates);
    }

    protected abstract NormalizedDeleteQuery<TEntity> createSelf(
        @Nullable Predicate<TEntity> plainPredicate,
        @NotNull EntityStructureKeyPredicateMap keyPredicates);
}
