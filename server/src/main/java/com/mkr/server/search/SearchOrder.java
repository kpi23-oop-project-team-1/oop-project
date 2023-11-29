package com.mkr.server.search;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Comparator;

public enum SearchOrder {
    CHEAPEST {
        @Override
        public <T> Comparator<T> reverseIfNeeded(Comparator<T> ascending) {
            return ascending;
        }
    },
    MOST_EXPENSIVE {
        @Override
        public <T> Comparator<T> reverseIfNeeded(Comparator<T> ascending) {
            return ascending.reversed();
        }
    };

    @NotNull
    public abstract<T> Comparator<T> reverseIfNeeded(@NotEmpty Comparator<T> ascending);
}
