package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Email;
import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Per Spilling
 */
public class PersonDaoFluentStyleTest {

    private static PersonDaoFluentStyle dao;

    @BeforeClass
    public static void initDb() {
        dao = new PersonDaoFluentStyle(new JdbiHelper().getDBI());
        new PersonTableCreator().resetTable();
        DbSeeder.initPersonTable(dao);
    }

    @Test
    public void shouldNotFindNonExistingPerson() {
        Person person2 = dao.get(100L);
        assertNull(person2);
    }

    @Test
    public void idShouldHaveBeenSetByDB() {
        dao.save(new Person("John Doe", new Email("john.doe@nomail.com")));
        Person p = dao.findByName("John Doe").get(0);
        assertTrue(p.getId() != null);
    }

    @Test
    public void retrieveAll() {
        List<Person> persons = dao.getAll();
        assertTrue(persons.size() > 4);
    }

    @Test
    public void findPer() {
        List<Person> persons = dao.findByName("Per%");
        assertThat(persons.size(), equalTo(2));

        persons = dao.findByName("Per Spilling");
        assertThat(persons.size(), equalTo(1));
    }
}
