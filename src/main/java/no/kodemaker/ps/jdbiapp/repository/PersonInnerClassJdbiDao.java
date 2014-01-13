package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import no.kodemaker.ps.jdbiapp.repository.jdbi.SpringDBIFactoryBean;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonAddressMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.PersonMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import javax.inject.Named;
import java.util.List;

/**
 * Person DAO implemented with a cascading person -> address association for the homeAddress field.
 *
 * @author Per Spilling
 */
@Named
public class PersonInnerClassJdbiDao implements PersonDao {
    private PersonDao personDao;
    private PersonAddressDao personAddressDao;
    private AddressCrudDao addressDao;
    private JdbiHelper jdbiHelper;

    public PersonInnerClassJdbiDao() {
        jdbiHelper = new JdbiHelper();
        init(jdbiHelper.getDBI());
    }

    // used with Spring
    public PersonInnerClassJdbiDao(SpringDBIFactoryBean dbiFactoryBean) {
        DBI dbi = dbiFactoryBean.getDBI();
        jdbiHelper = new JdbiHelper(dbi);
        init(dbi);
    }

    private void init(DBI dbi) {
        personDao = dbi.onDemand(PersonDao.class);
        personAddressDao = dbi.onDemand(PersonAddressDao.class);
        addressDao = dbi.onDemand(AddressCrudDao.class);
    }

    public void createTable() {
        new PersonTableCreator().createTable();
        new AddressTableCreator().createTable();
        new PersonAddressTableCreator().createTable();
    }

    public void dropTable() {
        new PersonAddressTableCreator().dropTable();
        new AddressTableCreator().dropTable();
        new PersonTableCreator().dropTable();
    }

    /**
     * Insert or update the person table and its associated address table if the person has a homeAddress.
     * @return the saved person instance
     */
    @Override
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
    public Person get(Long id) {
        return getPersonWithAddress(personDao.get(id));
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
    public List<Person> findByName(String name) {
        return getPersonsWithAddress(personDao.findByName(name));
    }

    @Override
    public List<Person> findByEmail(String email) {
        return getPersonsWithAddress(personDao.findByEmail(email));
    }

    @Override
    public List<Person> getAll() {
        return getPersonsWithAddress(personDao.getAll());
    }

    @Override
    public boolean exists(Long id) {
        return personDao.get(id) != null;
    }

    @Override
    public void delete(Long id) {
        // cascade delete homeAddress if exist
        Person p = get(id);
        if (p.getHomeAddress() != null) {
            // first delete from the association table, then from the address table
            personAddressDao.delete(new PersonAddressAssoc(p.getId(), p.getHomeAddress().getId()));
            addressDao.delete(p.getHomeAddress().getId());
        }
        personDao.deleteById(id);
    }

    /**
     * Internal Person JDBI dao.
     */
    @RegisterMapper(PersonMapper.class)
    private interface PersonDao extends Transactional<PersonDao> {

        @SqlUpdate("insert into PERSON (name, email, phone) values (:p.name, :p.emailVal, :p.phone)")
        @GetGeneratedKeys
        long insert(@BindBean("p") Person person);

        @SqlUpdate("update PERSON set name = :p.name, email = :p.emailVal, phone = :p.phone where personId = :p.id")
        void update(@BindBean("p") Person person);

        @SqlQuery("select * from PERSON where personId = :id")
        Person get(@Bind("id") long id);

        @SqlQuery("select * from PERSON where name like :name")
        List<Person> findByName(@Bind("name") String name);

        @SqlQuery("select * from PERSON where email like :email")
        List<Person> findByEmail(@Bind("email") String email);

        @SqlQuery("select * from PERSON")
        List<Person> getAll();

        @SqlUpdate("delete from PERSON where personId = :id")
        void deleteById(@Bind("id") long id);
    }

    /**
     * Internal Team -> Person association dao
     */
    @RegisterMapper(PersonAddressMapper.class)
    interface PersonAddressDao extends Transactional<PersonAddressDao> {

        @SqlUpdate("insert into PERSON_ADDRESS (personId, addressId) values (:pa.personId, :pa.addressId)")
        @GetGeneratedKeys
        long insert(@BindBean("pa") PersonAddressAssoc personAddressAssoc);

        @SqlQuery("select * from PERSON_ADDRESS where personId = :personId")
        List<PersonAddressAssoc> findByPersonId(@Bind("personId") Long personId);

        @SqlUpdate("delete from PERSON_ADDRESS where personId = :pa.personId and addressId = :pa.addressId")
        void delete(@BindBean("pa") PersonAddressAssoc personAddressAssoc);
    }
}
