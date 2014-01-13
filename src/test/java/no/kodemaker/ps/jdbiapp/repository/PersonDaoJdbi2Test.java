package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Email;
import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * @author Per Spilling
 */
public class PersonDaoJdbi2Test {

    private static PersonJdbiDao2 personDao;
    // cannot use the AddressDao interface in this case as this causes a CGLIB error
    private static AddressAbstractClassJdbiDao addressDao;

    @BeforeClass
    public static void init() {
        new AddressTableCreator().resetTable();
        new PersonTableCreator().resetTable();
        new PersonAddressTableCreator().resetTable();
        personDao = new PersonJdbiDao2();
        addressDao = new JdbiHelper().getDBI().onDemand(AddressAbstractClassJdbiDao.class);
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
        List<Person> persons = personDao.getAll();
        assertTrue(persons.size() >= 5);
    }

    @Test
    public void testDependentHomeAddress() {
        Person person = personDao.findByName("Per Spilling").get(0);
        assertThat(person.getHomeAddress(), notNullValue());
        person.getHomeAddress().setPostalPlace("Oslo");
        Person p = personDao.save(person);
        assertThat(p.getHomeAddress().getPostalPlace(), equalTo("Oslo"));
    }

    @Test
    public void testFinders() {
        List<Person> persons = personDao.findByName("Per%");
        assertThat(persons.size(), equalTo(2));

        persons = personDao.findByName("Per Spilling");
        assertThat(persons.size(), equalTo(1));

        persons = personDao.findByEmail("per@kodemaker.no");
        assertThat(persons.size(), equalTo(1));
    }

    @Test
    public void testDelete() {
        List<Person> persons = personDao.getAll();
        int size = persons.size();

        Person p = personDao.save(new Person("John Doe", new Email("john@mail.com")));
        persons = personDao.getAll();
        assertThat(persons.size(), equalTo(size + 1));

        personDao.delete(p.getId());
        persons = personDao.getAll();
        assertThat(persons.size(), equalTo(size));
    }

    @Test
    public void testUpdate() {
        List<Person> persons = personDao.getAll();
        Person person = persons.get(0);
        person.setPhone("12345678");
        personDao.save(person);
        Person updatedPerson = personDao.get(person.getId());
        assertThat(person, equalTo(updatedPerson));
    }
}
