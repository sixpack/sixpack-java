package com.seatgeek.sixpack;

import com.seatgeek.sixpack.log.LogLevel;
import org.junit.Test;

import static org.junit.Assert.*;

public class SixpackBuilderTest {

    @Test
    public void testSetSixpackUrl() throws Exception {
        String testUrl = "http://test/sixpack";
        SixpackBuilder sixpack = new SixpackBuilder()
                .setSixpackUrl(testUrl);
        sixpack.build();

        assertEquals(testUrl, sixpack.getSixpackUrl());
    }

    @Test
    public void testEmptySixpackUrlDefaults() throws Exception {
        SixpackBuilder sixpack = new SixpackBuilder()
                .setSixpackUrl("");
        sixpack.build();

        assertEquals(Sixpack.DEFAULT_URL, sixpack.getSixpackUrl());
    }

    @Test
    public void testNullSetSixpackUrlDefaults() throws Exception {
        SixpackBuilder sixpack = new SixpackBuilder()
                .setSixpackUrl(null);
        sixpack.build();

        assertEquals(Sixpack.DEFAULT_URL, sixpack.getSixpackUrl());
    }

    @Test
    public void testSetClientId() throws Exception {
        String testClientId = "test_id";
        Sixpack sixpack = new SixpackBuilder()
                .setClientId(testClientId)
                .build();

        assertEquals(testClientId, sixpack.getClientId());
    }

    @Test
    public void testNullClientIdDefaults() {
        Sixpack sixpack = new SixpackBuilder()
                .setClientId(null)
                .build();

        assertNotNull(sixpack.getClientId());
    }

    @Test
    public void testEmptyClientIdDefaults() {
        Sixpack sixpack = new SixpackBuilder()
                .setClientId("")
                .build();

        assertNotNull(sixpack.getClientId());
        assertTrue(sixpack.getClientId().length() > 0);
    }

    @Test
    public void testSetLogLevel() throws Exception {
        LogLevel logLevel = LogLevel.DEBUG;
        Sixpack sixpack = new SixpackBuilder()
                .setLogLevel(logLevel)
                .build();

        assertEquals(logLevel, sixpack.getLogLevel());
    }
}