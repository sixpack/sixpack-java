package com.seatgeek.sixpack.response;

import com.seatgeek.sixpack.Alternative;
import junit.framework.TestCase;
import org.junit.Test;

public class ParticipateResponseTest extends TestCase {

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