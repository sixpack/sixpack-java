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

    @Mock OnConvertSuccess mockSuccess;

    @Mock OnConvertFailure mockFailure;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetSixpack() throws Exception {
        ParticipatingExperiment participatingExperiment = new ParticipatingExperiment(mockSixpack, mockExperiment, mockAlternative);

        Sixpack sixpack = participatingExperiment.getSixpack();

        assertEquals(sixpack, mockSixpack);
    }

    @Test
    public void testGetBaseExperiment() throws Exception {
        ParticipatingExperiment participatingExperiment = new ParticipatingExperiment(mockSixpack, mockExperiment, mockAlternative);

        Experiment baseExperiment = participatingExperiment.getBaseExperiment();

        assertEquals(baseExperiment, mockExperiment);
    }

    @Test
    public void testGetAlternative() {
        Alternative testAlternative = new Alternative("green");

        ParticipatingExperiment participatingExperiment = new ParticipatingExperiment(mockSixpack, mockExperiment, testAlternative);

        assertEquals(participatingExperiment.getSelectedAlternative(), testAlternative);
    }

    @Test
    public void testConvertCallsSixpackConvert() {
        ParticipatingExperiment participatingExperiment = new ParticipatingExperiment(mockSixpack, mockExperiment, mockAlternative);

        participatingExperiment.convert(mockSuccess, mockFailure);

        verify(mockSixpack).convert(participatingExperiment, mockSuccess, mockFailure);
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(ParticipatingExperiment.class).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify();
    }
}