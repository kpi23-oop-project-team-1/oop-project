package com.mkr.server.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.mkr.server.domain.ColorId;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.Comparator;

public enum SearchOrder {
    CHEAPEST("cheapest") {
        @Override
        public <T> Comparator<T> reverseIfNeeded(Comparator<T> ascending) {
            return ascending;
        }
    },
    MOST_EXPENSIVE("most-expensive") {
        @Override
        public <T> Comparator<T> reverseIfNeeded(Comparator<T> ascending) {
            return ascending.reversed();
        }
    };

    @NotNull
    public abstract<T> Comparator<T> reverseIfNeeded(@NotEmpty Comparator<T> ascending);

    private final String prettyString;

    SearchOrder(String prettyString) {
        this.prettyString = prettyString;
    }

    @JsonValue
    public String toPrettyString() {
        return prettyString;
    }

    @JsonCreator
    public static SearchOrder forValue(String value) {
        return Arrays.stream(SearchOrder.values())
            .filter(r -> r.prettyString.equals(value))
            .findFirst()
            .orElseThrow();
    }
}
