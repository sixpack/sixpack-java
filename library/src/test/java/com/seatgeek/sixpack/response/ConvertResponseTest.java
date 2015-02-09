package com.seatgeek.sixpack.response;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConvertResponseTest {

    @Test
    public void testEmptyConstructor() {
        ConvertResponse response = new ConvertResponse();
        assertNotNull(response);
    }
}