package com.seatgeek.sixpack;

import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import nl.jqno.equalsverifier.EqualsVerifier;

public class AlternativeTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNoEmptyNames() {
        new Alternative("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoNullNames() {
        new Alternative(null);
    }

    @Test
    public void testGetName() {
        String name = "test-name";

        Alternative alternative = new Alternative(name);

        assertTrue(name.equals(alternative.name));
    }

    @Test
    public void testToStringEqualsName() {
        String name = "test-name";

        Alternative alternative = new Alternative(name);

        assertTrue(name.equals(alternative.toString()));
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(Alternative.class).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify();
    }
}
