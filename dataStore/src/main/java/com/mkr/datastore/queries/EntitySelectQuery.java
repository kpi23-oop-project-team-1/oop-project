package com.mkr.datastore.queries;

import com.mkr.datastore.EntityStructureKey;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

public interface EntitySelectQuery<T> extends SelectQuery<T> {
    @NotNull
    EntitySelectQuery<T> filter(@NotNull Predicate<T> predicate);

    @NotNull
    <K> EntitySelectQuery<T> filter(@NotNull EntityStructureKey<K> key, @NotNull Predicate<K> keyPredicate);

    @NotNull
    default <R extends Comparable<R>> EntitySelectQuery<T> order(@NotNull Function<T, R> getKey) {
        return order(Comparator.comparing(getKey));
    }

    @NotNull
    EntitySelectQuery<T> order(@NotNull Comparator<T> comparator);

    @NotNull
    <R extends Comparable<R>> EntitySelectQuery<T> order(EntityStructureKey<R> key);

    @NotNull
    EntitySelectQuery<T> range(int offset, int limit);

    <I, R> R aggregate(Collector<I, ?, R> collector, EntityStructureKey<I> key);
}
