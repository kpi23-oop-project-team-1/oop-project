package com.mkr.datastore;

import com.mkr.datastore.queries.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public interface DataStoreCollection<TEntity> {
    @NotNull
    DataStoreCollectionDescriptor<TEntity> getDescriptor();

    @NotNull
    EntitySelectQuery<TEntity> select();

    @NotNull
    EntitySelectQuery<TEntity> select(EntityStructureKey<?>... keys);

    @NotNull
    <T> SelectQuery<T> select(@NotNull EntityStructureKey<T> key);

    @NotNull
    NoResultQuery insert(@NotNull TEntity... values);

    @NotNull
    <K> NoResultQuery update(
        @NotNull EntityStructureKey<K> key,
        @NotNull Predicate<K> keyPredicate,
        @NotNull Function<TEntity, TEntity> transformEntity);

    @NotNull
    DeleteQuery<TEntity> delete();
}
