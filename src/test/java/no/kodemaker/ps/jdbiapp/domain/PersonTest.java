package no.kodemaker.ps.jdbiapp.domain;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Per Spilling
 */
public class PersonTest {
    @Test
    public void testPersonConstraints() {
        try {
            new Person(null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("name"));
        }

        try {
            new Person("Per", null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("email"));
        }

        try {
            new Person("Per", new Email("abc"));
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("not a valid email address"));
        }

        try {
            new Person("Per Spilling", new Email("per@kodemaker.no"));
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }
    }
}
