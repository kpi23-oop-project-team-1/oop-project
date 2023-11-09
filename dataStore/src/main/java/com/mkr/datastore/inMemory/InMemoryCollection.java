package com.mkr.datastore.inMemory;

import com.mkr.datastore.*;
import com.mkr.datastore.queries.DeleteQuery;
import com.mkr.datastore.queries.EntitySelectQuery;
import com.mkr.datastore.queries.NoResultQuery;
import com.mkr.datastore.queries.SelectQuery;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InMemoryCollection<TEntity> implements DataStoreCollection<TEntity> {
    private final DataStoreCollectionDescriptor<TEntity> descriptor;

    private final List<TEntity> entities;

    public InMemoryCollection(@NotNull DataStoreCollectionDescriptor<TEntity> descriptor) {
        this.descriptor = descriptor;

        entities = new ArrayList<>();
    }

    @Override
    @NotNull
    public DataStoreCollectionDescriptor<TEntity> getDescriptor() {
        return descriptor;
    }

    @Override
    @NotNull
    public EntitySelectQuery<TEntity> select() {
        return new InMemoryEntitySelectQuery<>(descriptor.getStructure(), entities);
    }

    @Override
    @NotNull
    public EntitySelectQuery<TEntity> select(EntityStructureKey<?>... keys) {
        return select();
    }

    @Override
    @NotNull
    public <T> SelectQuery<T> select(@NotNull EntityStructureKey<T> key) {
        EntityStructure<TEntity> structure = descriptor.getStructure();

        Stream<T> stream = entities.stream().map(structure.getKeyMapper(key));

        return new InMemorySelectQuery<>(key.valueType(), stream);
    }

    @SafeVarargs
    @Override
    @NotNull
    public final NoResultQuery insert(@NotNull TEntity... values) {
        return new InMemoryInsertQuery<>(entities, values);
    }

    @Override
    @NotNull
    public <K> NoResultQuery update(
        @NotNull EntityStructureKey<K> key,
        @NotNull Predicate<K> keyPredicate,
        @NotNull Function<TEntity, TEntity> transformEntity
    ) {
        return new InMemoryUpdateQuery<>(
            entities,
            descriptor.getStructure(),
            key,
            keyPredicate,
            transformEntity
        );
    }

    @Override
    @NotNull
    public DeleteQuery<TEntity> delete() {
        return new InMemoryDeleteQuery<>(descriptor.getStructure(), entities);
    }
}
