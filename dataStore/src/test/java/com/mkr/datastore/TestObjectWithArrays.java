package com.mkr.datastore;

import java.util.Arrays;

public final class TestObjectWithArrays {
    private String string;
    private Integer integer;
    private String[] strings;
    private Integer[] integers;

    public TestObjectWithArrays() {
    }

    public TestObjectWithArrays(String string, Integer integer, String[] strings, Integer[] integers) {
        this.string = string;
        this.integer = integer;
        this.strings = strings;
        this.integers = integers;
    }

    public String getString() {
        return string;
    }

    public Integer getInteger() {
        return integer;
    }

    public String[] getStrings() {
        return strings;
    }

    public Integer[] getIntegers() {
        return integers;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    public void setIntegers(Integer[] integers) {
        this.integers = integers;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestObjectWithArrays other &&
                string.equals(other.string) &&
                integer.equals(other.integer) &&
                Arrays.equals(strings, other.strings) &&
                Arrays.equals(integers, other.integers);
    }

    @Override
    public int hashCode() {
        return string.hashCode() * 31 + integer + Arrays.hashCode(strings) * 71 + Arrays.hashCode(integers) * 11;
    }
}
