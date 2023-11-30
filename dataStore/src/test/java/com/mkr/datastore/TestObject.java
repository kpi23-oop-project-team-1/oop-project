package com.mkr.datastore;

@BaseModel(id = "TestObject", inheritedClasses = { TestObjectWithArrays.class })
public class TestObject {
    private Boolean bool;
    private String string;
    private Integer integer;
    private TestEnum testEnum;

    public TestObject() {
    }

    public TestObject(Boolean bool, String string, Integer integer, TestEnum testEnum) {
        this.bool = bool;
        this.string = string;
        this.integer = integer;
        this.testEnum = testEnum;
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
    public TestEnum getTestEnum() {
        return testEnum;
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
    public void setTestEnum(TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestObject other &&
            bool.equals(other.bool) &&
            string.equals(other.string) &&
            integer.equals(other.integer) &&
            testEnum.equals(other.testEnum);
    }

    @Override
    public int hashCode() {
        return bool.hashCode() * 71 + string.hashCode() * 31 + integer + testEnum.hashCode() * 111;
    }

    public String toString() {
        return "{%s, %s, %d, %s}".formatted(bool, string, integer, testEnum);
    }
}
