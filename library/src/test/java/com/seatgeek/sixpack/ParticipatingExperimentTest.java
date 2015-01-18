package com.seatgeek.sixpack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class ParticipatingExperimentTest {
    @Mock Sixpack mockSixpack;
    @Mock Experiment mockExperiment;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetSixpack() throws Exception {
        ParticipatingExperiment participatingExperiment = new ParticipatingExperiment(mockSixpack, mockExperiment);

        Sixpack sixpack = participatingExperiment.getSixpack();

        assertEquals(sixpack, mockSixpack);
    }

    @Test
    public void testGetBaseExperiment() throws Exception {
        ParticipatingExperiment participatingExperiment = new ParticipatingExperiment(mockSixpack, mockExperiment);

        Experiment baseExperiment = participatingExperiment.getBaseExperiment();

        assertEquals(baseExperiment, mockExperiment);
    }
}