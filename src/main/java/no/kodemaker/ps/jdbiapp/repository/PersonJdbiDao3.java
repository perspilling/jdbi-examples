package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import no.kodemaker.ps.jdbiapp.repository.mappers.ExistsMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonAddressMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonWithAddressMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.SQLException;
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
    private DBI dbi;

    public PersonJdbiDao3() {
        init(new JdbiHelper().getDBI());
    }

    private void init(DBI dbi) {
        this.dbi = dbi;
        personDao = dbi.onDemand(PersonDao.class);
        personAddressDao = dbi.onDemand(PersonAddressDao.class);
        addressDao = dbi.onDemand(AddressCrudDao.class);
    }

    @Override
    public List<Person> findByName(String name) {
        return personDao.findByName(name);
    }

    @Override
    public List<Person> findByEmail(String email) {
        return personDao.findByEmail(email);
    }

    public Iterator<Person> getAll() {
        return personDao.getAll();
    }

    @Override
    public int count() {
        return personDao.count();
    }

    @Override
    public Person get(Long id) {
        return personDao.getWithAddress(id);
    }

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
    public Person save(Person person) {
        try (Handle handle = dbi.open()) {
            handle.getConnection().setAutoCommit(false);   // NB!
            PersonDao personDao = handle.attach(PersonDao.class);
            if (person.getId() == null) {
                long personId = personDao.insert(person);
                return get(personId);
            } else {
                personDao.update(person);
                return get(person.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(final Long id) {
        try (Handle handle = dbi.open()) {
            handle.getConnection().setAutoCommit(false);   // NB!
            PersonDao dao = handle.attach(PersonDao.class);
            dao.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Internal Person JDBI dao.
     */
    static abstract class PersonDao {
        // Using outer join here as the address is optional. If no address is found, then inner join would return null
        static final String personWithAddressBaseQuery =
                "select p.personid, p.name, p.email, p.phone, a.addressid, a.streetaddress, a.postalcode, a.postalplace" +
                        "         FROM person as p " +
                        "         LEFT OUTER JOIN person_address as pa" +
                        "         ON p.personid = pa.personid " +
                        "         LEFT OUTER JOIN address as a" +
                        "         on pa.addressid = a.addressid ";

        /**
         * The @CreateSqlObject methods can be used to create DAO instances inside methods in the SqlObject
         * class, in order to have them participate in the same transaction
         */

        @CreateSqlObject
        public abstract AddressCrudDao createAddressCrudDao();

        @CreateSqlObject
        public abstract PersonAddressDao createPersonAddressDao();

        @Transaction
        long insert(final Person person) {
            long personId = insertPerson(person);
            if (person.getHomeAddress() != null) {
                Address a = createAddressCrudDao().save(person.getHomeAddress());
                createPersonAddressDao().insert(new PersonAddressAssoc(personId, a.getId()));
            }
            return personId;
        }

        @SqlUpdate("insert into PERSON (name, email, phone) values (:p.name, :p.emailVal, :p.phone)")
        @GetGeneratedKeys
        public abstract long insertPerson(@BindBean("p") Person person);

        @Transaction
        void update(final Person person) {
            updatePerson(person);
            Address homeAddress = person.getHomeAddress();
            if (homeAddress != null) {
                AddressCrudDao addressCrudDao = createAddressCrudDao();
                if (homeAddress.getId() == null) {
                    Address a = addressCrudDao.save(homeAddress);

                    PersonAddressDao personAddressDao = createPersonAddressDao();
                    personAddressDao.insert(new PersonAddressAssoc(person.getId(), a.getId()));
                    person.setHomeAddress(a);
                } else {
                    addressCrudDao.save(homeAddress);
                }
            }
        }

        @SqlUpdate("update PERSON set name = :p.name, email = :p.emailVal, phone = :p.phone where personId = :p.id")
        public abstract void updatePerson(@BindBean("p") Person person);


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
         *
         * @return
         */
        @SqlQuery(personWithAddressBaseQuery)
        @RegisterMapper(PersonWithAddressMapper.class)
        public abstract Iterator<Person> getAll();

        @SqlQuery("select count(*) from PERSON")
        public abstract int count();

        /**
         * The @Transaction annotation will create the TX boilerplate code needed to execute the method
         * body in a TX.
         */


        @Transaction
        public void delete(Long id) {
            // cascade delete homeAddress if exist
            Person p = getWithAddress(id);
            if (p.getHomeAddress() != null) {
                // first delete from the association table, then from the address table
                PersonAddressDao personAddressDao = createPersonAddressDao();
                personAddressDao.delete(new PersonAddressAssoc(p.getId(), p.getHomeAddress().getId()));

                AddressCrudDao addressCrudDao = createAddressCrudDao();
                addressCrudDao.delete(p.getHomeAddress().getId());
            }
            deletePerson(id);
        }

        @SqlUpdate("delete from PERSON where personId = :id")
        abstract void deletePerson(@Bind("id") Long id);
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

        @SqlUpdate("delete from PERSON_ADDRESS where personId = :pa.personId and addressId = :pa.addressId")
        @Transaction
        void delete(@BindBean("pa") PersonAddressAssoc personAddressAssoc);
    }
}
