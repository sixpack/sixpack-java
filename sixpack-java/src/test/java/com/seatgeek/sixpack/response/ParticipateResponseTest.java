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
        experiment.name = "test_experiment";

        ParticipateResponse response = new ParticipateResponse();
        response.experiment = experiment;

        assertEquals("test_experiment", response.experiment.name);
    }

    @Test
    public void testGetAlternativeName() throws Exception {
        AlternativeName alternativeName = new AlternativeName();
        alternativeName.name = "green";

        ParticipateResponse response = new ParticipateResponse();
        response.alternative = alternativeName;

        assertEquals("green", response.alternative.name);
    }

    @Test
    public void testGetClientId() throws Exception {
        String clientId = "client_id";

        ParticipateResponse response = new ParticipateResponse();
        response.clientId = clientId;

        assertEquals(clientId, response.clientId);
    }

    @Test
    public void testGetSelectedAlternative() throws Exception {
        AlternativeName alternativeName = new AlternativeName();
        alternativeName.name = "green";

        ParticipateResponse response = new ParticipateResponse();
        response.alternative = alternativeName;

        assertEquals(new Alternative("green"), response.getSelectedAlternative());
    }

    @Test
    public void testGetSelectedAlternativeNull() throws Exception {
        ParticipateResponse response = new ParticipateResponse();

        assertNull(response.getSelectedAlternative());
    }
}
