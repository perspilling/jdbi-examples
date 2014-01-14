package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import no.kodemaker.ps.jdbiapp.repository.mappers.ExistsMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonAddressMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonWithAddressMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;
import java.util.List;

/**
 * Similar to {@link no.kodemaker.ps.jdbiapp.repository.PersonJdbiDao2}, but in this case I'm using sql joins instead
 * of using multiple dao's when doing queries.
 *
 * @author Per Spilling
 */
public class PersonJdbiDao3 implements PersonDao {
    private PersonDao personDao;
    private PersonAddressDao personAddressDao;     // association table
    private AddressCrudDao addressDao;

    public PersonJdbiDao3() {
        init(new JdbiHelper().getDBI());
    }

    private void init(DBI dbi) {
        personDao = dbi.onDemand(PersonDao.class);
        personAddressDao = dbi.onDemand(PersonAddressDao.class);
        addressDao = dbi.onDemand(AddressCrudDao.class);
    }

    @Override
    public List<Person> findByName(String name) { return personDao.findByName(name); }

    @Override
    public List<Person> findByEmail(String email) { return personDao.findByEmail(email); }

    public Iterator<Person> getAll() { return personDao.getAll(); }

    @Override
    public int count() { return personDao.count(); }

    @Override
    public Person get(Long id) { return personDao.getWithAddress(id); }

    @Override
    public boolean exists(Long id) {
        return personDao.exists(id);
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
            long personId = personDao.insert(person);
            insertHomeAddressIfExist(personId, person);
            return get(personId);
        } else {
            personDao.update(person);
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
        personDao.delete(id);
    }

    /**
     * Internal Person JDBI dao.
     */
    interface PersonDao {
        // Using outer join here as the address is optional. If no address is found, then inner join would return null
        String personWithAddressBaseQuery =
                "select p.personid, p.name, p.email, p.phone, a.addressid, a.streetaddress, a.postalcode, a.postalplace"+
                "         FROM person as p "+
                "         LEFT OUTER JOIN person_address as pa"+
                "         ON p.personid = pa.personid "+
                "         LEFT OUTER JOIN address as a"+
                "         on pa.addressid = a.addressid ";

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
        @RegisterMapper(PersonWithAddressMapper.class)
        public abstract Person get(@Bind("id") Long id);

        @SqlQuery(personWithAddressBaseQuery + "where p.personid = :id")
        @RegisterMapper(PersonWithAddressMapper.class)
        public abstract Person getWithAddress(@Bind("id") Long id);

        @SqlQuery(personWithAddressBaseQuery + "where p.name like :name")
        @RegisterMapper(PersonWithAddressMapper.class)
        public abstract List<Person> findByName(@Bind("name") String name);

        @SqlQuery(personWithAddressBaseQuery + "where email like :email")
        @RegisterMapper(PersonWithAddressMapper.class)
        public abstract List<Person> findByEmail(@Bind("email") String email);

        /**
         * Using an Iterator here means that the results will be loaded lazily, one row at a time
         * when Iterator#next or Iterator#hasNext is called. See
         * <a href=""http://jdbi.org/sql_object_api_queries/>SQL Object Queries</a> for more details.
         * @return
         */
        @SqlQuery(personWithAddressBaseQuery)
        @RegisterMapper(PersonWithAddressMapper.class)
        public abstract Iterator<Person> getAll();

        @SqlQuery("select count(*) from PERSON")
        public abstract int count();

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
