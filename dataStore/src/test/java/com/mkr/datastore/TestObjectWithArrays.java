package com.mkr.datastore;

import java.util.Arrays;

@InheritedModel(id = "TestObjectWithArrays")
public class TestObjectWithArrays extends TestObject {
    private String[] strings;
    private Integer[] integers;

    public TestObjectWithArrays() {
    }

    public TestObjectWithArrays(Boolean bool, String string, Integer integer, Long testLong, TestEnum testEnum, String[] strings, Integer[] integers) {
        super(bool, string, integer, testLong, testEnum);
        this.strings = strings;
        this.integers = integers;
    }

    public String[] getStrings() {
        return strings;
    }

    public Integer[] getIntegers() {
        return integers;
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
                super.equals(other) &&
                Arrays.equals(strings, other.strings) &&
                Arrays.equals(integers, other.integers);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Arrays.hashCode(strings) + Arrays.hashCode(integers);
    }
}
