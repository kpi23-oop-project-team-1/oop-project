package com.mkr.datastore;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EntitySchemeTests {
    @BaseModel(
        id = "testClass1",
        inheritedClasses = { TestClass2.class }
    )
    public static class TestClass1 {
        protected int field1;

        public TestClass1() {
        }

        public TestClass1(int field1) {
            this.field1 = field1;
        }

        public int getField1() {
            return field1;
        }

        public void setField1(int value) {
            this.field1 = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            return field1 == ((TestClass1)o).field1;
        }

        @Override
        public int hashCode() {
            return field1;
        }

        @Override
        public String toString() {
            return "TestClass1 { field1=" + field1 + " }";
        }
    }

    @InheritedModel(id = "testClass2")
    public static class TestClass2 extends TestClass1 {
        private int field2;

        public TestClass2() {
        }

        public TestClass2(int field1, int field2) {
            super(field1);

            this.field2 = field2;
        }

        public int getField2() {
            return field2;
        }

        public void setField2(int value) {
            field2 = value;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            return field2 == ((TestClass2)o).field2;
        }

        @Override
        public int hashCode() {
            return super.hashCode() * 31 + field2;
        }

        @Override
        public String toString() {
            return "TestClass1 { field1=" + field1 + " ,field2=" + field2 + " }";
        }
    }

    private static EntityScheme<TestObject> testObjectScheme() {
        return EntityScheme.createFromClass(TestObject.class);
    }

    private static EntityScheme<TestClass1> testClass1Scheme() {
        return EntityScheme.createFromClass(TestClass1.class);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void getKeyValueTest() {
        EntityScheme<TestObject> scheme = testObjectScheme();
        var entity = new TestObject(true, "123", 2, TestEnum.VALUE1);

        assertEquals(true, scheme.getKeyValue(entity, scheme.findKeyByName("bool")));
        assertEquals("123", scheme.getKeyValue(entity, scheme.findKeyByName("string")));
        assertEquals(2, scheme.getKeyValue(entity, scheme.findKeyByName("integer")));
        assertEquals(TestEnum.VALUE1, scheme.getKeyValue(entity, scheme.findKeyByName("testEnum")));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void getKeyValueInheritanceTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();
        var entity = new TestClass2(1, 2);

        assertEquals(1, scheme.getKeyValue(entity, scheme.findKeyByName("field1")));
        assertEquals(2, scheme.getKeyValue(entity, scheme.findKeyByName("field2")));
    }

    @Test
    public void getKeysForSubclassTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();
        EntitySchemeKey[] keys = scheme.getKeysForSubclass(TestClass2.class);

        var expectedKeys = new EntitySchemeKey[] {
            new EntitySchemeKey(int.class, "field2"),
            new EntitySchemeKey(int.class, "field1")
        };

        assertArrayEquals(expectedKeys, keys);
    }

    @Test
    public void getKeysForSubclassBaseClassTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();
        EntitySchemeKey[] keys = scheme.getKeysForSubclass(TestClass1.class);

        var expectedKeys = new EntitySchemeKey[] {
            new EntitySchemeKey(int.class, "field1")
        };

        assertArrayEquals(expectedKeys, keys);
    }

    @Test
    public void getDirectKeysTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();
        EntitySchemeKey[] keys = scheme.getDirectKeys();

        var expectedKeys = new EntitySchemeKey[] {
            new EntitySchemeKey(int.class, "field1")
        };

        assertArrayEquals(expectedKeys, keys);
    }

    @Test
    public void resolveClassByIdTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();

        assertEquals(TestClass1.class, scheme.resolveClassById("testClass1"));
        assertEquals(TestClass2.class, scheme.resolveClassById("testClass2"));
    }

    @Test
    public void setKeyValueTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();
        TestClass1 instance = new TestClass1();
        scheme.setKeyValue(instance, scheme.findKeyByName("field1"), 1);

        assertEquals(1, instance.field1);
    }

    @Test
    public void setKeyValueOnInheritedTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();
        TestClass2 instance = new TestClass2();
        scheme.setKeyValue(instance, scheme.findKeyByName("field2"), 2);

        assertEquals(2, instance.field2);
    }

    @Test
    public void getIdForSubclassTest() {
        EntityScheme<TestClass1> scheme = testClass1Scheme();

        assertEquals("testClass1", scheme.getIdForSubclass(TestClass1.class));
        assertEquals("testClass2", scheme.getIdForSubclass(TestClass2.class));
    }
}
