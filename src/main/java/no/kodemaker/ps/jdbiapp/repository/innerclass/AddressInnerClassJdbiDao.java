package no.kodemaker.ps.jdbiapp.repository.innerclass;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.repository.AddressDao;
import no.kodemaker.ps.jdbiapp.repository.AddressMapper;
import no.kodemaker.ps.jdbiapp.repository.ExistsMapper;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import java.util.List;

/**
 * @author Per Spilling
 */
public class AddressInnerClassJdbiDao implements AddressDao {

    private AddressDao addressDao;

    public AddressInnerClassJdbiDao() {
        addressDao = new JdbiHelper().getDBI().onDemand(AddressDao.class);
    }

    @Override
    public List<Address> getAll() {
        return addressDao.getAll();
    }

    @Override
    public Address get(Long id) {
        return addressDao.get(id);
    }

    @Override
    public boolean exists(Long id) {
        return addressDao.exists(id);
    }

    @Override
    public Address save(Address address) {
        if (address.getId() == null) {
            long id = addressDao.insert(address);
            return addressDao.get(id);
        } else {
            addressDao.update(address);
            return addressDao.get(address.getId());
        }
    }

    @Override
    public void delete(Long id) {
        addressDao.deleteById(id);
    }

    @RegisterMapper(AddressMapper.class)
    private interface AddressDao extends Transactional<AddressDao> {

        @SqlQuery("select * from ADDRESS where addressId = :id")
        @RegisterMapper(ExistsMapper.class)
        public abstract boolean exists(@Bind("id") Long id);

        @SqlUpdate("insert into ADDRESS (streetAddress, postalCode, postalPlace) values (:a.streetAddress, :a.postalCode, :a.postalPlace)")
        @GetGeneratedKeys
        long insert(@BindBean("a") Address address);

        @SqlUpdate("update ADDRESS set streetAddress = :a.streetAddress, postalCode = :a.postalCode, postalPlace = :a.postalPlace where addressId = :a.id")
        void update(@BindBean("a") Address address);

        @SqlQuery("select * from ADDRESS where addressId = :id")
        Address get(@Bind("id") long id);

        @SqlQuery("select * from ADDRESS")
        List<Address> getAll();

        @SqlUpdate("delete from ADDRESS where addressId = :id")
        void deleteById(@Bind("id") long id);
    }
}
