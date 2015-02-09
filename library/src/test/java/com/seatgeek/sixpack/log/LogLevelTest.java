package com.seatgeek.sixpack.log;

import org.junit.Test;

import static org.junit.Assert.*;

public class LogLevelTest {

    @Test
    public void testValuesArray() {
        assertArrayEquals(new LogLevel[] { LogLevel.VERBOSE, LogLevel.DEBUG, LogLevel.BASIC, LogLevel.NONE}, LogLevel.values());
    }

    @Test
    public void testValueOf() {
        assertEquals(LogLevel.valueOf("VERBOSE"), LogLevel.VERBOSE);
        assertEquals(LogLevel.valueOf("DEBUG"), LogLevel.DEBUG);
        assertEquals(LogLevel.valueOf("BASIC"), LogLevel.BASIC);
        assertEquals(LogLevel.valueOf("NONE"), LogLevel.NONE);
    }

    @Test(expected = NullPointerException.class)
    public void testValueOfNullThrows() {
        LogLevel.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfEmptyThrows() {
        LogLevel.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidThrows() {
        LogLevel.valueOf("NOTLOGLEVEL");
    }
}