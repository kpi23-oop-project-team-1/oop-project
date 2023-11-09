package com.mkr.datastore;

public final class TestObject {
    private final String string;
    private final int integer;

    public TestObject(String string, int integer) {
        this.string = string;
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public int getInteger() {
        return integer;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestObject other &&
            string.equals(other.string) &&
            integer == other.integer;
    }

    @Override
    public int hashCode() {
        return string.hashCode() * 31 + integer;
    }
}
