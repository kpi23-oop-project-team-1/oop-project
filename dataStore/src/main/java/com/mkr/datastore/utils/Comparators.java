package com.mkr.datastore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public final class Comparators {
    private Comparators() {
    }

    @NotNull
    public static<T> Comparator<T> merge(@Nullable Comparator<T> current, @NotNull Comparator<T> other) {
        if (current == null) {
            return other;
        }

        return (a, b) -> {
            int result = current.compare(a, b);
            if (result == 0) {
                result = other.compare(a, b);
            }

            return result;
        };
    }
}
