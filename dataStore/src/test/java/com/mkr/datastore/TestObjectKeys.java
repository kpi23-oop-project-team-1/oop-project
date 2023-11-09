package com.mkr.datastore;

public class TestObjectKeys {
    public static final EntityStructureKey<String> string =
        new EntityStructureKey<>(String.class, "string");

    public static final EntityStructureKey<Integer> integer =
        new EntityStructureKey<>(int.class, "integer");
}
