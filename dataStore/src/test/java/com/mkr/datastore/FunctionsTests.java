package com.mkr.datastore;

import com.mkr.datastore.utils.Functions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

public class FunctionsTests {
    @Test
    public void combineTestBothNull() {
        Predicate<Object> predicate = Functions.combine(null, null);
        boolean actualResult = predicate.test(new Object());

        assertFalse(actualResult);
    }

    @Test
    public void combineTestFirstNull() {
        Predicate<Object> p2 = x -> true;
        Predicate<Object> combined = Functions.combine(null, p2);

        assertSame(p2, combined);
    }

    @Test
    public void combineTestSecondNull() {
        Predicate<Object> p1 = x -> true;
        Predicate<Object> combined = Functions.combine(p1, null);

        assertSame(p1, combined);
    }

    @Test
    public void combineTest() {
        Predicate<Integer> p1 = x -> x >= 5;
        Predicate<Integer> p2 = x -> x <= 10;
        Predicate<Integer> combined = Functions.combine(p1, p2);

        assertTrue(combined.test(6));
        assertTrue(combined.test(10));
        assertFalse(combined.test(12));
        assertFalse(combined.test(3));
    }

    @Test
    public void truePredicateTest() {
        Predicate<Object> pred = Functions.truePredicate();

        assertTrue(pred.test(new Object()));
    }

    @Test
    public void falsePredicateTest() {
        Predicate<Object> pred = Functions.falsePredicate();

        assertFalse(pred.test(new Object()));
    }
}
