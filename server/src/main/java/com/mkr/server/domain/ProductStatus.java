package com.mkr.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum ProductStatus {
    ACTIVE("active"),
    WAITING_FOR_MODERATION("waiting-for-moderation"),
    DECLINED("declined"),
    SOLD("sold");

    private final String prettyString;

    ProductStatus(String prettyString) {
        this.prettyString = prettyString;
    }

    @JsonValue
    public String toPrettyString() {
        return prettyString;
    }

    @JsonCreator
    public static ProductStatus forValue(String value) {
        return Arrays.stream(ProductStatus.values())
            .filter(r -> r.prettyString.equals(value))
            .findFirst()
            .orElseThrow();
    }
}
