package com.mkr.datastore.queries;

import com.mkr.datastore.EntityStructureKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface DeleteQuery<TEntity> {
    @NotNull
    DeleteQuery<TEntity> filter(@NotNull Predicate<TEntity> predicate);

    @NotNull
    <K> DeleteQuery<TEntity> filter(@NotNull EntityStructureKey<K> key, Predicate<K> predicate);

    void execute();
}
