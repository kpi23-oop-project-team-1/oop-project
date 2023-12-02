package com.mkr.datastore;

@BaseModel(id = "TestObject", inheritedClasses = { TestObjectWithArrays.class })
public class TestObject {
    private Boolean bool;
    private String string;
    private Integer integer;
    private Long testLong;
    private TestEnum testEnum;

    public TestObject() {
    }

    public TestObject(Boolean bool, String string, Integer integer, Long testLong, TestEnum testEnum) {
        this.bool = bool;
        this.string = string;
        this.integer = integer;
        this.testLong = testLong;
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

    public Long getTestLong() {
        return testLong;
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

    public void setTestLong(Long testLong) {
        this.testLong = testLong;
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
            testLong.equals(other.testLong) &&
            testEnum.equals(other.testEnum);
    }

    @Override
    public int hashCode() {
        return bool.hashCode() * 71 + string.hashCode() * 31 + integer + testLong.hashCode() * 17 + testEnum.hashCode() * 13;
    }
}
