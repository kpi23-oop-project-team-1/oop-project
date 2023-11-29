package com.mkr.server.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.mkr.server.utils.MathUtils;
import org.junit.jupiter.api.Test;

public class MathUtilsTests {
    @Test
    public void ceilDivTest() {
        assertEquals(2, MathUtils.ceilDiv(3, 2));
        assertEquals(1, MathUtils.ceilDiv(3, 3));
        assertEquals(1, MathUtils.ceilDiv(1, 1));
        assertEquals(4, MathUtils.ceilDiv(10, 3));
    }
 }
