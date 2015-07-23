package com.seatgeek.sixpack;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
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

        Sixpack sixpack = convertedExperiment.sixpack;

        assertEquals(sixpack, mockSixpack);
    }

    @Test
    public void testGetBaseExperiment() throws Exception {
        ConvertedExperiment convertedExperiment = new ConvertedExperiment(mockSixpack, mockExperiment);

        Experiment baseExperiment = convertedExperiment.baseExperiment;

        assertEquals(baseExperiment, mockExperiment);
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(ConvertedExperiment.class).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify();
    }
}
