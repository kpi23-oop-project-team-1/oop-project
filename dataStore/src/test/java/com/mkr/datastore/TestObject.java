package com.mkr.datastore;

public final class TestObject {
    private String string;
    private int integer;

    public TestObject() {
    }

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

    public void setString(String string) {
        this.string = string;
    }

    public void setInteger(int integer) {
        this.integer = integer;
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
