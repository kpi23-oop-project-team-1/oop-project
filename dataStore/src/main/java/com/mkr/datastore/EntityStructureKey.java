package com.mkr.datastore;

import org.jetbrains.annotations.NotNull;

public record EntityStructureKey<TValue>(@NotNull Class<TValue> valueType, @NotNull String name) {
}
