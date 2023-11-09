package com.mkr.datastore.inMemory;

import com.mkr.datastore.EntityStructure;
import com.mkr.datastore.EntityStructureKey;
import com.mkr.datastore.queries.NoResultQuery;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class InMemoryUpdateQuery<E, K> implements NoResultQuery {
    private final List<E> entities;
    private final EntityStructure<E> structure;
    private final EntityStructureKey<K> key;
    private final Predicate<K> keyPredicate;
    private final Function<E, E> transformEntity;

    public InMemoryUpdateQuery(
        @NotNull List<E> entities,
        @NotNull EntityStructure<E> structure,
        @NotNull EntityStructureKey<K> key,
        @NotNull Predicate<K> keyPredicate,
        @NotNull Function<E, E> transformEntity
    ) {
        this.entities = entities;
        this.structure = structure;
        this.key = key;
        this.keyPredicate = keyPredicate;
        this.transformEntity = transformEntity;
    }

    @Override
    public void execute() {
        for (int i = 0; i < entities.size(); i++) {
            E entity = entities.get(i);
            K keyValue = structure.getKeyValue(entity, key);

            if (keyPredicate.test(keyValue)) {
                E newEntity = transformEntity.apply(entity);

                entities.set(i, newEntity);
            }
        }
    }
}
