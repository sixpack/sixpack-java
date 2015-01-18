package com.seatgeek.sixpack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class ConvertedExperimentTest {
    @Mock Sixpack mockSixpack;
    @Mock Experiment mockExperiment;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetSixpack() throws Exception {
        ConvertedExperiment convertedExperiment = new ConvertedExperiment(mockSixpack, mockExperiment);

        Sixpack sixpack = convertedExperiment.getSixpack();

        assertEquals(sixpack, mockSixpack);
    }

    @Test
    public void testGetBaseExperiment() throws Exception {
        ConvertedExperiment convertedExperiment = new ConvertedExperiment(mockSixpack, mockExperiment);

        Experiment baseExperiment = convertedExperiment.getBaseExperiment();

        assertEquals(baseExperiment, mockExperiment);
    }
}