package com.mkr.datastore;

@BaseModel(id = "TestObject", inheritedClasses = { TestObjectWithArrays.class })
public class TestObject {
    private Boolean bool;
    private String string;
    private Integer integer;

    public TestObject() {
    }

    public TestObject(Boolean bool, String string, Integer integer) {
        this.bool = bool;
        this.string = string;
        this.integer = integer;
    }

    public Boolean getBool() {
        return bool;
    }

    public String getString() {
        return string;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
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
            bool.equals(other.bool) &&
            string.equals(other.string) &&
            integer.equals(other.integer);
    }

    @Override
    public int hashCode() {
        return bool.hashCode() * 71 + string.hashCode() * 31 + integer;
    }

    public String toString() {
        return "{%s, %s, %d}".formatted(bool, string, integer);
    }
}
