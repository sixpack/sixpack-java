package com.seatgeek.sixpack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PrefetchedExperimentTest {
    @Mock Sixpack mockSixpack;

    @Mock Experiment mockExperiment;

    @Mock Alternative mockAlternative;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testParticipateCallsSixpackParticipate() {
        PrefetchedExperiment prefetchedExperiment = new PrefetchedExperiment(mockSixpack, mockExperiment, mockAlternative);

        prefetchedExperiment.participate();

        verify(mockSixpack).participate(mockExperiment);
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(PrefetchedExperiment.class).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify();
    }
}
