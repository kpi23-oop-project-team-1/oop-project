package com.mkr.datastore;

public final class TestObject {
    private String string;
    private Integer integer;

    public TestObject() {
    }

    public TestObject(String string, Integer integer) {
        this.string = string;
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestObject other &&
            string.equals(other.string) &&
            integer.equals(other.integer);
    }

    @Override
    public int hashCode() {
        return string.hashCode() * 31 + integer;
    }

    public String toString() {
        return "{%s, %d}".formatted(string, integer);
    }
}
