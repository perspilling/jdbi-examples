package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Email;
import no.kodemaker.ps.jdbiapp.domain.Person;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * @author Per Spilling
 */
public class PersonDaoJdbi3Test {

    private static PersonJdbiDao3 personDao;
    // cannot use the AddressDao interface as a reference in this case as this causes a CGLIB error
    private static AddressInnerClassJdbiDao addressDao;

    @BeforeClass
    public static void init() {
        new AddressTableCreator().resetTable();
        new PersonTableCreator().resetTable();
        new PersonAddressTableCreator().resetTable();

        personDao = new PersonJdbiDao3();
        addressDao = new AddressInnerClassJdbiDao();
        DbSeeder.initPersonTable(personDao);
    }

    @Test
    public void shouldNotFindNonExistingPerson() {
        Person p = personDao.get(100L);
        assertNull(p);
    }

    @Test
    public void saveShouldUpdatePrimaryKey() {
        createAlbertIfNotExist();
        Person albert = personDao.findByName("Albert Einstein").get(0);
        assertTrue(albert.getId() != null);
    }

    private void createAlbertIfNotExist() {
        if (personDao.findByName("Albert Einstein").size() == 0) {
            personDao.save(new Person("Albert Einstein", new Email("albert@nomail.com")));
        }
    }

    @Test
    public void deleteShouldDeleteDependentAddress() {
        createAlbertIfNotExist();
        Person albert = personDao.findByName("Albert Einstein").get(0);
        albert.setHomeAddress(new Address()
                .setStreetAddress("Princeton Avenue 1")
                .setPostalCode("42356")
                .setPostalPlace("Princeton"));

        albert = personDao.save(albert);
        Long addressId = albert.getHomeAddress().getId();
        assertTrue(addressId != null);
        assertTrue(addressDao.get(addressId) != null);

        personDao.delete(albert.getId());
        assertTrue(addressDao.get(addressId) == null);
    }

    @Test
    public void retrieveAll() {
        Iterator<Person> persons = personDao.getAll();
        int count = 0;
        while (persons.hasNext()) {
            count++;
            persons.next();
        }
        assertTrue(count >= 5);
    }

    @Test
    public void testDependentHomeAddress() {
        //Person person = personDao.findByName("Per Spilling").get(0);
        Person person = personDao.get(1L);
        assertThat(person.getHomeAddress(), notNullValue());
        person.getHomeAddress().setPostalPlace("Oslo");
        Person p = personDao.save(person);
        assertThat(p.getHomeAddress().getPostalPlace(), equalTo("Oslo"));
    }

    @Test
    public void testFinders() {
        List<Person> persons = personDao.findByName("Per%");
        assertThat(persons.size(), equalTo(2));
        assertThat(persons.get(0).getHomeAddress(), notNullValue());

        persons = personDao.findByName("Per Spilling");
        assertThat(persons.size(), equalTo(1));

        persons = personDao.findByEmail("per@kodemaker.no");
        assertThat(persons.size(), equalTo(1));
    }

    @Test
    public void testDelete() {
        int size = personDao.count();

        Person p = personDao.save(new Person("John Doe", new Email("john@mail.com")));
        assertThat(personDao.count(), equalTo(size + 1));

        personDao.delete(p.getId());
        assertThat(personDao.count(), equalTo(size));
    }

    @Test
    public void testUpdate() {
        Person person = personDao.get(1L);
        person.setPhone("12345678");
        personDao.save(person);
        Person updatedPerson = personDao.get(1L);
        assertThat(person, equalTo(updatedPerson));
    }
}
