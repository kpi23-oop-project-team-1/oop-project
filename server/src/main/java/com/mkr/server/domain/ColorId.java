package com.mkr.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ColorId {
    BLACK("black"),
    WHITE("white"),
    RED("red"),
    CYAN("cyan"),
    YELLOW("yellow"),
    MULTI("multi");

    private final String prettyString;

    ColorId(String prettyString) {
        this.prettyString = prettyString;
    }

    @JsonValue
    public String toPrettyString() {
        return prettyString;
    }

    @JsonCreator
    public static ColorId forValue(String value) {
        return Arrays.stream(ColorId.values())
            .filter(r -> r.prettyString.equals(value))
            .findFirst()
            .orElseThrow();
    }
}
