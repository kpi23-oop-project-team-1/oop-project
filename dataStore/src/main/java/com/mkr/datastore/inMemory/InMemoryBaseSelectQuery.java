package com.mkr.datastore.inMemory;

import com.mkr.datastore.queries.SelectQuery;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public abstract class InMemoryBaseSelectQuery<TSelf extends InMemoryBaseSelectQuery<TSelf, T>, T> implements SelectQuery<T> {
    protected final Class<T> valueClass;
    protected final Stream<T> entityStream;

    public InMemoryBaseSelectQuery(Class<T> valueClass, List<T> entities) {
        this(valueClass, entities.stream());
    }

    public InMemoryBaseSelectQuery(Class<T> valueClass, Stream<T> entityStream) {
        this.valueClass = valueClass;
        this.entityStream = entityStream;
    }

    @Override
    @NotNull
    public TSelf filter(@NotNull Predicate<T> predicate) {
        return withMutatedStream(entityStream.filter(predicate));
    }

    @Override
    @NotNull
    public TSelf order(@NotNull Comparator<T> comparator) {
        return withMutatedStream(entityStream.sorted(comparator));
    }

    @Override
    @NotNull
    public TSelf range(int offset, int limit) {
        return withMutatedStream(entityStream.skip(offset).limit(limit));
    }

    @Override
    public <R> R aggregate(Collector<T, ?, R> collector) {
        return entityStream.collect(collector);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] execute() {
        return entityStream.toArray(n -> (T[])Array.newInstance(valueClass, n));
    }

    @NotNull
    protected abstract TSelf withMutatedStream(@NotNull Stream<T> newStream);
}
