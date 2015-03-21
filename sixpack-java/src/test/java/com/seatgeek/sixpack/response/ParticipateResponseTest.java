package com.seatgeek.sixpack.response;

import com.seatgeek.sixpack.Alternative;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ParticipateResponseTest extends TestCase {

    @Test
    public void testEmptyConstructor() {
        ParticipateResponse response = new ParticipateResponse();
        assertNotNull(response);
    }

    @Test
    public void testGetExperimentName() throws Exception {
        ExperimentName experiment = new ExperimentName();
        experiment.setName("test_experiment");

        ParticipateResponse response = new ParticipateResponse();
        response.setExperiment(experiment);

        assertEquals("test_experiment", response.getExperiment().getName());
    }

    @Test
    public void testGetAlternativeName() throws Exception {
        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setName("green");

        ParticipateResponse response = new ParticipateResponse();
        response.setAlternative(alternativeName);

        assertEquals("green", response.getAlternative().getName());
    }

    @Test
    public void testGetClientId() throws Exception {
        String clientId = "client_id";

        ParticipateResponse response = new ParticipateResponse();
        response.setClientId(clientId);

        assertEquals(clientId, response.getClientId());
    }

    @Test
    public void testGetSelectedAlternative() throws Exception {
        AlternativeName alternativeName = new AlternativeName();
        alternativeName.setName("green");

        ParticipateResponse response = new ParticipateResponse();
        response.setAlternative(alternativeName);

        assertEquals(new Alternative("green"), response.getSelectedAlternative());
    }

    @Test
    public void testGetSelectedAlternativeNull() throws Exception {
        ParticipateResponse response = new ParticipateResponse();

        assertNull(response.getSelectedAlternative());
    }
}