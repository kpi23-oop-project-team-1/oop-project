package com.mkr.datastore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

public final class Functions {
    private Functions() {}

    @NotNull
    public static <T> Predicate<T> combine(@Nullable Predicate<T> a, @Nullable Predicate<T> b) {
        if (a == null && b == null) {
            return falsePredicate();
        } else if (a == null || b == null) {
            return a == null ? b : a;
        }

        return x -> a.test(x) && b.test(x);
    }

    @NotNull
    public static <T> Predicate<T> truePredicate() {
        return x -> true;
    }

    @NotNull
    public static <T> Predicate<T> falsePredicate() {
        return x -> false;
    }
 }
