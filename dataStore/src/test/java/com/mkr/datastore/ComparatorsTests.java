package com.mkr.datastore;

import com.mkr.datastore.utils.Comparators;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;

public class ComparatorsTests {
    public record TestClass(int value1, int value2) {
    }

    private static Comparator<TestClass> value1Comparator() {
        return Comparator.comparingInt(a -> a.value1);
    }

    private static Comparator<TestClass> value2Comparator() {
        return Comparator.comparingInt(a -> a.value2);
    }

    @Test
    public void mergeCurrentNullTest() {
        Comparator<TestClass> comparator = value1Comparator();
        Comparator<TestClass> actualResult = Comparators.merge(null, comparator);

        assertSame(comparator, actualResult);
    }

    private static<T> void assertCompareResult(int expected, Comparator<T> comparator, T a, T b) {
        assertEquals(expected, comparator.compare(a, b));
    }

    private static<T> void assertCompareSame(Comparator<T> comparator, T a, T b) {
        assertCompareResult(0, comparator, a, b);
    }

    private static<T> void assertCompareGreater(Comparator<T> comparator, T a, T b) {
        assertCompareResult(1, comparator, a, b);
    }

    private static<T> void assertCompareLesser(Comparator<T> comparator, T a, T b) {
        assertCompareResult(-1, comparator, a, b);
    }

    @Test
    public void mergeOrderTest() {
        Comparator<TestClass> cmp = Comparators.merge(value1Comparator(), value2Comparator());

        assertCompareSame(cmp, new TestClass(0, 0), new TestClass(0, 0));
        assertCompareGreater(cmp, new TestClass(1, 0), new TestClass(0, 0));
        assertCompareGreater(cmp, new TestClass(1, 1), new TestClass(1, 0));
        assertCompareLesser(cmp, new TestClass(-1, 0), new TestClass(0, 0));
        assertCompareLesser(cmp, new TestClass(1, -1), new TestClass(1, 0));
    }
}
