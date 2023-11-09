package com.mkr.datastore.queries.normalized;

import com.mkr.datastore.queries.SelectQuery;
import com.mkr.datastore.utils.Comparators;
import com.mkr.datastore.utils.Functions;
import com.mkr.datastore.utils.PositiveIntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Predicate;

public abstract class NormalizedBaseSelectQuery<T, TSelf extends NormalizedBaseSelectQuery<T, TSelf>> implements SelectQuery<T> {
    @Nullable
    protected final Predicate<T> plainFilterPredicate;

    @Nullable
    protected final Comparator<T> orderComparator;
    protected final PositiveIntRange range;

    protected NormalizedBaseSelectQuery() {
        plainFilterPredicate = null;
        orderComparator = null;
        range = PositiveIntRange.full();
    }

    protected NormalizedBaseSelectQuery(
        @Nullable Predicate<T> plainFilterPredicate,
        @Nullable Comparator<T> orderComparator,
        @NotNull PositiveIntRange range
    ) {
        this.plainFilterPredicate = plainFilterPredicate;
        this.orderComparator = orderComparator;
        this.range = range;
    }

    @Override
    @NotNull
    public TSelf filter(@NotNull Predicate<T> predicate) {
        return createSelf(Functions.combine(plainFilterPredicate, predicate), orderComparator, range);
    }

    @Override
    @NotNull
    public TSelf order(@NotNull Comparator<T> comparator) {
        return createSelf(plainFilterPredicate, Comparators.merge(orderComparator, comparator), range);
    }

    @Override
    @NotNull
    public TSelf range(int offset, int limit) {
        PositiveIntRange newRange = range.intersectionRelative(new PositiveIntRange(offset, limit));

        return createSelf(plainFilterPredicate, orderComparator, newRange);
    }

    @NotNull
    protected abstract TSelf createSelf(
        Predicate<T> plainFilterPredicate,
        Comparator<T> orderComparator,
        PositiveIntRange range
    );
}
