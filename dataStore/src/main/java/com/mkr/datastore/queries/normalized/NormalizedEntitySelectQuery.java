package com.mkr.datastore.queries.normalized;

import com.mkr.datastore.EntityStructure;
import com.mkr.datastore.EntityStructureKey;
import com.mkr.datastore.queries.EntitySelectQuery;
import com.mkr.datastore.utils.EntityStructureKeyPredicateMap;
import com.mkr.datastore.utils.Functions;
import com.mkr.datastore.utils.PositiveIntRange;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Predicate;

public abstract class NormalizedEntitySelectQuery<T> extends NormalizedBaseSelectQuery<T, NormalizedEntitySelectQuery<T>> implements EntitySelectQuery<T> {
    protected final EntityStructureKeyPredicateMap keyFilterPredicates;
    private final EntityStructure<T> structure;

    protected NormalizedEntitySelectQuery(EntityStructure<T> structure) {
        super();

        this.structure = structure;
        keyFilterPredicates = new EntityStructureKeyPredicateMap();
    }

    protected NormalizedEntitySelectQuery(
        EntityStructure<T> structure,
        Predicate<T> plainFilterPredicate,
        EntityStructureKeyPredicateMap keyFilterPredicates,
        Comparator<T> orderComparator,
        PositiveIntRange range
    ) {
        super(plainFilterPredicate, orderComparator, range);

        this.structure = structure;
        this.keyFilterPredicates = keyFilterPredicates;
    }

    @Override
    @NotNull
    public <K> EntitySelectQuery<T> filter(@NotNull EntityStructureKey<K> key, @NotNull Predicate<K> keyPredicate) {
        var newKeyFilterPredicates = keyFilterPredicates.withMerged(key, keyPredicate, Functions::combine);

        return createSelf(plainFilterPredicate, newKeyFilterPredicates, orderComparator, range);
    }

    @Override
    @NotNull
    public <R extends Comparable<R>> EntitySelectQuery<T> order(EntityStructureKey<R> key) {
        return order(structure.createKeyComparator(key));
    }

    @Override
    @NotNull
    protected final NormalizedEntitySelectQuery<T> createSelf(
        Predicate<T> plainFilterPredicate,
        Comparator<T> orderComparator,
        PositiveIntRange range
    ) {
        return createSelf(plainFilterPredicate, keyFilterPredicates, orderComparator, range);
    }

    protected abstract NormalizedEntitySelectQuery<T> createSelf(
        Predicate<T> plainFilterPredicate,
        EntityStructureKeyPredicateMap keyFilterPredicates,
        Comparator<T> orderComparator,
        PositiveIntRange range
    );
}
