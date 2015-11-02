package com.seatgeek.sixpack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ExperimentTest {
    @Mock Sixpack mockSixpack;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetSixpack() {
        Alternative test = new Alternative("test");
        String name = "test-experiment";

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withAlternatives(test)
                .withName(name)
                .build();

        assertEquals(mockSixpack, experiment.sixpack);
    }

    @Test
    public void testGetName() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(test)
                .build();

        assertEquals(name, experiment.name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoEmptyNames() {
        new ExperimentBuilder(mockSixpack)
                .withName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoNullNames() {
        new ExperimentBuilder(mockSixpack)
                .withName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadName() {
        new ExperimentBuilder(mockSixpack)
                .withName("bad.name");
    }

    @Test(expected = NoExperimentNameException.class)
    public void testNoNameThrows() {
        new ExperimentBuilder(mockSixpack)
                .build();
    }

    @Test
    public void testGetAlternatives() {
        String name = "test-experiment";
        Alternative one = new Alternative("one");
        Alternative two = new Alternative("two");
        Alternative three = new Alternative("three");

        Experiment experimentA = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(one, two, three)
                .build();

        Experiment experimentB = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(one, two, three)
                .build();

        Set<Alternative> setOfAlternatives = new LinkedHashSet<>();
        setOfAlternatives.add(one);
        setOfAlternatives.add(two);
        setOfAlternatives.add(three);

        assertEquals(setOfAlternatives, experimentA.alternatives);
        assertEquals(setOfAlternatives, experimentB.alternatives);
    }

    @Test(expected = NoAlternativesException.class)
    public void testNoAlternativesThrows() {
        String name = "test-experiment";
        new ExperimentBuilder(mockSixpack)
                .withName(name)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAlternativesThrows() {
        String name = "test-experiment";

        new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(null)
                .build();
    }

    @Test
    public void testGetForcedChoice() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(test)
                .withForcedChoice(test)
                .build();

        assertTrue(experiment.hasForcedChoice());
        assertEquals(test, experiment.forcedChoice);
    }


    @Test
    public void testNoForcedChoice() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(test)
                .build();

        assertFalse(experiment.hasForcedChoice());
        assertNull(experiment.forcedChoice);
    }

    @Test
    public void testGetTrafficFraction() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");
        Double fraction = .5d;

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(test)
                .withTrafficFraction(fraction)
                .build();

        assertEquals(fraction, experiment.trafficFraction);
    }

    @Test(expected = BadTrafficFractionException.class)
    public void testLessThanZeroTrafficFractionThrows() {
        Double fraction = -.5d;

        new ExperimentBuilder(mockSixpack)
                .withTrafficFraction(fraction);
    }

    @Test(expected = BadTrafficFractionException.class)
    public void testGreaterThanOneTrafficFractionThrows() {
        Double fraction = 2d;

        new ExperimentBuilder(mockSixpack)
                .withTrafficFraction(fraction);
    }

    @Test
    public void testToStringEqualsExperimentName() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(test)
                .build();

        assertEquals(name, experiment.toString());
    }

    @Test
    public void testParticipateCallsSixpackParticipate() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(test)
                .build();

        experiment.participate();

        verify(mockSixpack).participate(experiment);
    }
}
