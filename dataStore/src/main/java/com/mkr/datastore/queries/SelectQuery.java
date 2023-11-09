package com.mkr.datastore.queries;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface SelectQuery<T> {
    @NotNull
    SelectQuery<T> filter(@NotNull Predicate<T> predicate);

    @NotNull
    default <R extends Comparable<R>> SelectQuery<T> order(@NotNull Function<T, R> getKey) {
        return order(Comparator.comparing(getKey));
    }

    @NotNull
    SelectQuery<T> order(@NotNull Comparator<T> comparator);

    @NotNull
    SelectQuery<T> range(int offset, int limit);

    T[] execute();

    default <R> R aggregate(Collector<T, ?, R> collector) {
        return Arrays.stream(execute()).collect(collector);
    }

    default long count() {
        return aggregate(Collectors.counting());
    }

    default double average(ToDoubleFunction<T> mapper) {
        return aggregate(Collectors.averagingDouble(mapper));
    }

    default double sum(ToDoubleFunction<T> mapper) {
        return aggregate(Collectors.summingDouble(mapper));
    }
}
