package com.mkr.datastore;

public class TestDataStoreCollections {
    public static final DataStoreCollectionDescriptor<TestObject> testObject = new DataStoreCollectionDescriptorBuilder<TestObject>()
        .name("testObject")
        .entityClass(TestObject.class)
        .build();

    public static final DataStoreCollectionDescriptor<TestObjectWithArrays> testObjectWithArrays = new DataStoreCollectionDescriptorBuilder<TestObjectWithArrays>()
        .name("testObjectWithArrays")
        .entityClass(TestObjectWithArrays.class)
        .build();
}
