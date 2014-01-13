package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import no.kodemaker.ps.jdbiapp.repository.mappers.ExistsMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonAddressMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Implements a Person with a cascading association to an Address. The association is represented
 * by the PersonAddress table.
 *
 * @author Per Spilling
 */
public class PersonJdbiDao2 implements PersonDao {
    private PersonAbstractClassDao personAbstractClassDao;
    private PersonAddressDao personAddressDao;        // association table
    private AddressCrudDao addressDao;

    public PersonJdbiDao2() {
        init(new JdbiHelper().getDBI());
    }

    private void init(DBI dbi) {
        personAbstractClassDao = dbi.onDemand(PersonAbstractClassDao.class);
        personAddressDao = dbi.onDemand(PersonAddressDao.class);
        addressDao = dbi.onDemand(AddressCrudDao.class);
    }

    @Override
    public List<Person> findByName(String name) {
        return getPersonsWithAddress(personAbstractClassDao.findByName(name));
    }

    @Override
    public List<Person> findByEmail(String email) {
        return getPersonsWithAddress(personAbstractClassDao.findByEmail(email));
    }

    @Override
    public List<Person> getAll() {
        return getPersonsWithAddress(personAbstractClassDao.getAll());
    }

    @Override
    public Person get(Long id) {
        return getPersonWithAddress(personAbstractClassDao.get(id));
    }

    private Person getPersonWithAddress(Person person) {
        if (person == null) return null;
        List<PersonAddressAssoc> personAddressAssocList = personAddressDao.findByPersonId(person.getId());
        if (personAddressAssocList.size() == 1) {
            person.setHomeAddress(addressDao.get(personAddressAssocList.get(0).getAddressId()));
        }
        return person;
    }

    private List<Person> getPersonsWithAddress(List<Person> persons) {
        if (persons == null) return null;
        for (Person p : persons) {
            getPersonWithAddress(p);
        }
        return persons;
    }

    @Override
    public boolean exists(Long id) {
        return personAbstractClassDao.exists(id);
    }

    /**
     * Insert or update the person table and its associated address table if the person has a homeAddress.
     *
     * @return the saved person instance
     */
    @Override
    @Transaction
    public Person save(Person person) {
        if (person.getId() == null) {
            long personId = personAbstractClassDao.insert(person);
            insertHomeAddressIfExist(personId, person);
            return get(personId);
        } else {
            personAbstractClassDao.update(person);
            updateHomeAddressIfExist(person);
            return get(person.getId());
        }
    }

    private void insertHomeAddressIfExist(long personId, Person person) {
        if (person.getHomeAddress() != null) {
            Address a = addressDao.save(person.getHomeAddress());
            personAddressDao.insert(new PersonAddressAssoc(personId, a.getId()));
        }
    }

    private void updateHomeAddressIfExist(Person person) {
        Address homeAddress = person.getHomeAddress();
        if (homeAddress != null) {
            if (homeAddress.getId() == null) {
                Address a = addressDao.save(homeAddress);
                personAddressDao.insert(new PersonAddressAssoc(person.getId(), a.getId()));
                person.setHomeAddress(a);
            } else {
                addressDao.save(homeAddress);
            }
        }
    }

    @Override
    @Transaction
    public void delete(Long id) {
        // cascade delete homeAddress if exist
        Person p = get(id);
        if (p.getHomeAddress() != null) {
            // first delete from the association table, then from the address table
            personAddressDao.delete(new PersonAddressAssoc(p.getId(), p.getHomeAddress().getId()));
            addressDao.delete(p.getHomeAddress().getId());
        }
        personAbstractClassDao.delete(id);
    }

    /**
     * Internal Person JDBI dao.
     */
    @RegisterMapper(PersonMapper.class)
    interface PersonAbstractClassDao {

        @SqlUpdate("insert into PERSON (name, email, phone) values (:p.name, :p.emailVal, :p.phone)")
        @GetGeneratedKeys
        @Transaction
        public abstract long insert(@BindBean("p") Person person);

        @SqlUpdate("update PERSON set name = :p.name, email = :p.emailVal, phone = :p.phone where personId = :p.id")
        @Transaction
        public abstract void update(@BindBean("p") Person person);

        @SqlQuery("select * from PERSON where personId = :id")
        @RegisterMapper(ExistsMapper.class)
        public abstract boolean exists(Long id);

        @SqlQuery("select * from PERSON where personId = :id")
        public abstract Person get(@Bind("id") Long id);

        @SqlQuery("select * from PERSON where name like :name")
        public abstract List<Person> findByName(@Bind("name") String name);

        @SqlQuery("select * from PERSON where email like :email")
        public abstract List<Person> findByEmail(@Bind("email") String email);

        @SqlQuery("select * from PERSON")
        public abstract List<Person> getAll();

        @SqlUpdate("delete from PERSON where personId = :id")
        @Transaction
        public abstract void delete(@Bind("id") Long id);
    }


    /**
     * Internal Team -> Person association dao
     */
    @RegisterMapper(PersonAddressMapper.class)
    interface PersonAddressDao {

        @SqlUpdate("insert into PERSON_ADDRESS (personId, addressId) values (:pa.personId, :pa.addressId)")
        @GetGeneratedKeys
        @Transaction
        long insert(@BindBean("pa") PersonAddressAssoc personAddressAssoc);

        @SqlQuery("select * from PERSON_ADDRESS where personId = :personId")
        List<PersonAddressAssoc> findByPersonId(@Bind("personId") Long personId);

        @SqlUpdate("delete from PERSON_ADDRESS where personId = :pa.personId and addressId = :pa.addressId")
        @Transaction
        void delete(@BindBean("pa") PersonAddressAssoc personAddressAssoc);
    }
}
