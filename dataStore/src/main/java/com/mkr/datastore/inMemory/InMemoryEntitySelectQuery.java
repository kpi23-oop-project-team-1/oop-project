package com.mkr.datastore.inMemory;

import com.mkr.datastore.queries.EntitySelectQuery;
import com.mkr.datastore.EntityStructure;
import com.mkr.datastore.EntityStructureKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class InMemoryEntitySelectQuery<T> extends InMemoryBaseSelectQuery<InMemoryEntitySelectQuery<T>, T> implements EntitySelectQuery<T> {
    protected final EntityStructure<T> structure;

    public InMemoryEntitySelectQuery(@NotNull EntityStructure<T> structure, @NotNull List<T> entities) {
        this(structure, entities.stream());
    }

    public InMemoryEntitySelectQuery(@NotNull EntityStructure<T> structure, @NotNull Stream<T> entityStream) {
        super(structure.getEntityClass(), entityStream);

        this.structure = structure;
    }

    @Override
    @NotNull
    public <K> EntitySelectQuery<T> filter(@NotNull EntityStructureKey<K> key, @NotNull Predicate<K> keyPredicate) {
        return filter(obj -> keyPredicate.test(structure.getKeyValue(obj, key)));
    }

    @Override
    @NotNull
    public <R extends Comparable<R>> EntitySelectQuery<T> order(EntityStructureKey<R> key) {
        return order(structure.createKeyComparator(key));
    }

    @Override
    public <I, R> R aggregate(Collector<I, ?, R> collector, EntityStructureKey<I> key) {
        return entityStream.map(structure.getKeyMapper(key)).collect(collector);
    }

    @Override
    @NotNull
    protected InMemoryEntitySelectQuery<T> withMutatedStream(@NotNull Stream<T> newStream) {
        return new InMemoryEntitySelectQuery<>(structure, newStream);
    }
}
