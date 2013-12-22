package no.kodemaker.ps.jdbiapp.repository.innerclass;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.repository.AddressDao;
import no.kodemaker.ps.jdbiapp.repository.AddressTableCreator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Per Spilling
 */
public class AddressInnerClassJdbiDaoTest {
    private AddressDao dao;

    @Before
    public void init() {
        new AddressTableCreator().resetTable();
        dao = new AddressInnerClassJdbiDao();
        dao.save(new Address("Storgata 22", "0123", "Oslo"));
        dao.save(new Address("Karl Johans gate 10", "0100", "Oslo"));
    }

    @Test
    public void testSave() {
        Address address = dao.save(new Address("Drammensveien 1", "0123", "Oslo"));
        assertTrue(address.getId() != null);
    }

    @Test
    public void testMisc() {
        assertTrue(dao.exists(1L));
        int size = dao.getAll().size();
        assertTrue(size >= 2);
        Address a = dao.save(new Address("Alexander Kiellands plass 1", "0123", "Oslo"));
        assertThat(dao.getAll().size(), equalTo(size + 1));
        dao.delete(a.getId());
        assertThat(dao.getAll().size(), equalTo(size));
    }

}
