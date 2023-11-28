package com.mkr.server.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum UserRole {
    CUSTOMER_TRADER("customer-trader"),
    ADMIN("admin");

    private final String prettyString;

    UserRole(String prettyString) {
        this.prettyString = prettyString;
    }

    @JsonValue
    public String toPrettyString() {
        return prettyString;
    }

    @JsonCreator
    public static UserRole forValue(String value) {
        return Arrays.stream(UserRole.values())
            .filter(r -> r.prettyString.equals(value))
            .findFirst()
            .orElseThrow();
    }
}
