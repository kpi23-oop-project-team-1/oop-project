package com.mkr.datastore.utils;

import com.mkr.datastore.EntityStructureKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class EntityStructureKeyPredicateMap {
    private final HashMap<EntityStructureKey<?>, Predicate<?>> map;

    public EntityStructureKeyPredicateMap() {
        map = new HashMap<>();
    }

    public EntityStructureKeyPredicateMap(EntityStructureKeyPredicateMap other) {
        map = new HashMap<>(other.map);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(EntityStructureKey<?> key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Predicate<?> value) {
        return map.containsValue(value);
    }

    public <T> Predicate<T> get(EntityStructureKey<T> key) {
        return (Predicate<T>) map.get(key);
    }

    @Nullable
    public <T> Predicate<T> put(EntityStructureKey<T> key, Predicate<T> value) {
        return (Predicate<T>) map.put(key, value);
    }

    public<T> Predicate<T> remove(EntityStructureKey<T> key) {
        return (Predicate<T>)map.remove(key);
    }

    public <T> Predicate<T> merge(
        EntityStructureKey<T> key,
        Predicate<T> value,
        BiFunction<? super Predicate<T>, ? super Predicate<T>, ? extends Predicate<T>> remappingFunction
    ) {
        return (Predicate<T>) map.merge(
            key,
            value,
            (BiFunction<? super Predicate<?>, ? super Predicate<?>, ? extends Predicate<?>>) remappingFunction
        );
    }

    public <T> EntityStructureKeyPredicateMap withMerged(
        EntityStructureKey<T> key,
        Predicate<T> value,
        BiFunction<? super Predicate<T>, ? super Predicate<T>, ? extends Predicate<T>> remappingFunction
    ) {
        var newMap = new EntityStructureKeyPredicateMap(this);
        newMap.merge(key, value, remappingFunction);

        return newMap;
    }

    public void clear() {
        map.clear();
    }

    public void forEach(BiConsumer<EntityStructureKey<?>, Predicate<?>> consumer) {
        map.forEach(consumer);
    }

    @NotNull
    public Set<EntityStructureKey<?>> keySet() {
        return map.keySet();
    }

    @NotNull
    public Collection<Predicate<?>> values() {
        return map.values();
    }

    @NotNull
    public Set<Map.Entry<EntityStructureKey<?>, Predicate<?>>> entrySet() {
        return map.entrySet();
    }
}
