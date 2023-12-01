package com.mkr.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ProductState {
    NEW("new"),
    IDEAL("ideal"),
    VERY_GOOD("very-good"),
    GOOD("good"),
    ACCEPTABLE("acceptable");

    private final String prettyString;

    ProductState(String prettyString) {
        this.prettyString = prettyString;
    }

    @JsonValue
    public String toPrettyString() {
        return prettyString;
    }

    @JsonCreator
    public static ProductState forValue(String value) {
        return Arrays.stream(ProductState.values())
            .filter(r -> r.prettyString.equals(value))
            .findFirst()
            .orElseThrow();
    }
}
