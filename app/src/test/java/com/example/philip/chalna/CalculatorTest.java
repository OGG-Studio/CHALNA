package com.example.philip.chalna;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CalculatorTest {

    @Test
    public void testSum() {
        assertEquals(5, Calculator.sum(2, 3));
    }

}