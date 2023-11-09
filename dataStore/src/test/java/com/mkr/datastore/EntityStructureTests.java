package com.mkr.datastore;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class EntityStructureTests {
    private static Method getMethodNoThrow(Class<?> c, String name, Class<?>... params) {
        try {
            return c.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static EntityStructure<TestObject> testObjectStructure() {
        return EntityStructure.create(TestObject.class, TestObjectKeys.class);
    }

    @Test
    public void createFromKeysClassTest() {
        EntityStructure<TestObject> structure = testObjectStructure();

        assertEquals(TestObject.class, structure.entityClass);
        assertSame(TestObjectKeys.string, structure.keys[0]);
        assertSame(TestObjectKeys.integer, structure.keys[1]);
        assertEquals(getMethodNoThrow(TestObject.class, "getString"), structure.getters[0]);
        assertEquals(getMethodNoThrow(TestObject.class, "getInteger"), structure.getters[1]);
    }

    @Test
    public void getKeyValueTest() {
        EntityStructure<TestObject> structure = testObjectStructure();
        var entity = new TestObject("123", 2);

        assertEquals("123", structure.getKeyValue(entity, TestObjectKeys.string));
        assertEquals(2, structure.getKeyValue(entity, TestObjectKeys.integer));
    }

    @Test
    public void getKeyMapperTest() {
        EntityStructure<TestObject> structure = testObjectStructure();
        var entity = new TestObject("123", 2);

        var stringMapper = structure.getKeyMapper(TestObjectKeys.string);
        var intMapper = structure.getKeyMapper(TestObjectKeys.integer);

        assertEquals("123", stringMapper.apply(entity));
        assertEquals(2, intMapper.apply(entity));
    }
}
