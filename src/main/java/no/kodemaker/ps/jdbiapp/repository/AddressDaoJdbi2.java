package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * @author Per Spilling
 */
@RegisterMapper(AddressMapper.class)
public abstract class AddressDaoJdbi2 {

    public boolean exists(Long id) {
        return get(id) != null;
    }

    @Transaction
    public Address save(Address address) {
        if (address.getId() == null) {
            long id = insert(address);
            return get(id);
        } else {
            update(address);
            return get(address.getId());
        }
    }

    @SqlQuery("select * from ADDRESS")
    public abstract List<Address> getAll();

    @SqlQuery("select * from ADDRESS where addressId = :id")
    public abstract Address get(@Bind("id") long id);

    @SqlUpdate("insert into ADDRESS (streetAddress, postalCode, postalPlace) values (:a.streetAddress, :a.postalCode, :a.postalPlace)")
    @GetGeneratedKeys
    public abstract Long insert(@BindBean("a") Address address);

    @SqlUpdate("update ADDRESS set streetAddress = :a.streetAddress, postalCode = :a.postalCode, postalPlace = :a.postalPlace where addressId = :a.id")
    @GetGeneratedKeys
    public abstract Long update(@BindBean("a") Address address);

    @SqlUpdate("delete from ADDRESS where addressId = :id")
    public abstract void delete(@Bind("id") Long id);
}
