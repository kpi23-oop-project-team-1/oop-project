package com.mkr.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum ProductCategory {
    DRESS("dress"),
    ELECTRONICS("electronics"),
    HOME("home"),
    SPORT("sport");

    private final String prettyString;

    ProductCategory(@NotNull String prettyString) {
        this.prettyString = prettyString;
    }

    @JsonValue
    public String toPrettyString() {
        return prettyString;
    }

    @JsonCreator
    public static ProductCategory forValue(String value) {
        return Arrays.stream(ProductCategory.values())
            .filter(r -> r.prettyString.equals(value))
            .findFirst()
            .orElseThrow();
    }
}
