package com.seatgeek.sixpack;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ParticipatingExperimentTest {
    @Mock Sixpack mockSixpack;

    @Mock Experiment mockExperiment;

    @Mock Alternative mockAlternative;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testConvertCallsSixpackConvert() {
        ParticipatingExperiment participatingExperiment = new ParticipatingExperiment(mockSixpack, mockExperiment, mockAlternative);

        participatingExperiment.convert();

        verify(mockSixpack).convert(participatingExperiment, null);
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(ParticipatingExperiment.class).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify();
    }
}
