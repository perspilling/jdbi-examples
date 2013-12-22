package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.jdbi.TableCreator;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.LongMapper;

import java.util.List;

/**
 * Example of using the jDBI fluent style API.
 *
 * @author Per Spilling
 */
public class PersonDaoFluentStyle implements PersonDao, TableCreator {

    private DBI dbi;
    private PersonDaoJdbi personDaoJdbi;

    public PersonDaoFluentStyle(DBI dbi) {
        this.dbi = dbi;
        this.personDaoJdbi = new PersonDaoJdbi();

    }

    @Override
    public void createTable() {
        personDaoJdbi.createTable();
    }

    @Override
    public void dropTable() {
        personDaoJdbi.dropTable();
    }

    @Override
    public Person get(Long id) {
        Person person;
        try (Handle h = dbi.open()) {
            person = h.createQuery("select * from PERSON where personId" +
                    " = :id").bind("id", id)
                    .map(PersonMapper.INSTANCE)
                    .first();
        }
        return person;
    }

    @Override
    public boolean exists(Long id) {
        return get(id) != null;
    }

    @Override
    public void delete(Long id) {
        try (Handle h = dbi.open()) {
            h.createStatement("delete from PERSON where personId = :id").bind("id", id).execute();
        }
    }

    @Override
    public Person save(Person person) {
        long pk;
        try (Handle h = dbi.open()) {
            if (person.getId() == null)  {
                pk = h.createStatement("insert into PERSON (name, email, phone) values (:name, :email, :phone)")
                        .bind("name", person.getName())
                        .bind("email", person.getEmail().getVal())
                        .bind("phone", person.getPhone())
                        .executeAndReturnGeneratedKeys(LongMapper.FIRST).first();
            } else {
                pk = h.createStatement("update PERSON set name = :p.name, email = :p.emailVal, phone = :p.phone where personId = :p.id")
                        .bind("name", person.getName())
                        .bind("email", person.getEmail().getVal())
                        .bind("phone", person.getPhone())
                        .executeAndReturnGeneratedKeys(LongMapper.FIRST).first();
            }
            return new Person(pk, person.getName(), person.getEmail(), person.getPhone());
        }
    }

    @Override
    public List<Person> findByEmail(String email) {
        List<Person> persons;
        try (Handle h = dbi.open()) {
            persons = h.createQuery("select * from PERSON where email like :email")
                    .bind("email", email)
                    .map(PersonMapper.INSTANCE).list();
        }
        return persons;
    }

    public List<Person> findByName(String name) {
        List<Person> persons;
        try (Handle h = dbi.open()) {
            persons = h.createQuery("select * from PERSON where name like :name")
                    .bind("name", name)
                    .map(PersonMapper.INSTANCE).list();
        }
        return persons;
    }

    public List<Person> getAll() {
        List<Person> persons;
        try (Handle h = dbi.open()) {
            persons = h.createQuery("select * from PERSON").map(PersonMapper.INSTANCE).list();
        }
        return persons;
    }
}
