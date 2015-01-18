package com.seatgeek.sixpack;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
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
                .withAlternative(test)
                .withName(name)
                .build();

        assertEquals(mockSixpack, experiment.getSixpack());
    }

    @Test
    public void testGetName() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternative(test)
                .build();

        assertEquals(name, experiment.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNameThrows() {
        new ExperimentBuilder(mockSixpack)
                .withName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyNameThrows() {
        String name = "";

        new ExperimentBuilder(mockSixpack)
                .withName(name);
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
                .withAlternative(one)
                .withAlternative(two)
                .withAlternative(three)
                .build();

        Experiment experimentB = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(one, two, three)
                .build();

        Set<Alternative> setOfAlternatives = new HashSet<Alternative>();
        setOfAlternatives.add(one);
        setOfAlternatives.add(two);
        setOfAlternatives.add(three);

        Experiment experimentC = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(setOfAlternatives)
                .build();

        assertEquals(setOfAlternatives, experimentA.getAlternatives());
        assertEquals(setOfAlternatives, experimentB.getAlternatives());
        assertEquals(setOfAlternatives, experimentC.getAlternatives());
    }

    @Test(expected = NoAlternativesException.class)
    public void testNoAlternativesThrows() {
        String name = "test-experiment";
        new ExperimentBuilder(mockSixpack)
                .withName(name)
                .build();
    }

    @Test(expected = NoAlternativesException.class)
    public void testEmptyAlternativesThrows() {
        String name = "test-experiment";
        Set<Alternative> emptyAlternatives = new HashSet<Alternative>();

        new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternatives(emptyAlternatives)
                .build();
    }

    @Test
    public void testGetForcedChoice() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternative(test)
                .withForcedChoice(test)
                .build();

        assertTrue(experiment.hasForcedChoice());
        assertEquals(test, experiment.getForcedChoice());
    }

    @Test
    public void testGetTrafficFraction() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");
        Float fraction = .5f;

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternative(test)
                .withTrafficFraction(fraction)
                .build();

        assertEquals((Object)fraction, (Object)experiment.getTrafficFraction());
    }

    @Test(expected = BadTrafficFractionException.class)
    public void testLessThanZeroTrafficFractionThrows() {
        Float fraction = -.5f;

        new ExperimentBuilder(mockSixpack)
                .withTrafficFraction(fraction);
    }

    @Test(expected = BadTrafficFractionException.class)
    public void testGreaterThanOneTrafficFractionThrows() {
        Float fraction = 2f;

        new ExperimentBuilder(mockSixpack)
                .withTrafficFraction(fraction);
    }

    @Test
    public void testToStringEqualsExperimentName() {
        String name = "test-experiment";
        Alternative test = new Alternative("test");

        Experiment experiment = new ExperimentBuilder(mockSixpack)
                .withName(name)
                .withAlternative(test)
                .build();

        assertEquals(name, experiment.toString());
    }
}