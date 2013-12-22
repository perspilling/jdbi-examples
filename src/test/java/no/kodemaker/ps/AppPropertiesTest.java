package no.kodemaker.ps;

import no.kodemaker.ps.jdbiapp.DbProperties;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Per Spilling
 */
public class AppPropertiesTest {
    @Test
    public void testPropertyLoading() {
       assertNotNull(DbProperties.DB_DRIVER_CLASS.val());
       assertTrue(DbProperties.DB_DRIVER_CLASS.val().contains("Driver"));
    }
}
