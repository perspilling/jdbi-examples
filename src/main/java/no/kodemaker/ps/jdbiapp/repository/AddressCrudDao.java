package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.repository.mappers.AddressMapper;
import no.kodemaker.ps.jdbiapp.repository.mappers.ExistsMapper;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * @author Per Spilling
 */
@RegisterMapper(AddressMapper.class)
public abstract class AddressCrudDao implements AddressDao {

    @Override
    @SqlQuery("select * from ADDRESS where addressId = :id")
    @RegisterMapper(ExistsMapper.class)
    public abstract boolean exists(@Bind("id") Long id);

    @Override
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

    @Override
    @SqlQuery("select * from ADDRESS where addressId = :id")
    public abstract Address get(@Bind("id") Long id);

    @SqlUpdate("insert into ADDRESS (streetAddress, postalCode, postalPlace) values (:a.streetAddress, :a.postalCode, :a.postalPlace)")
    @GetGeneratedKeys
    public abstract Long insert(@BindBean("a") Address address);

    @SqlUpdate("update ADDRESS set streetAddress = :a.streetAddress, postalCode = :a.postalCode, postalPlace = :a.postalPlace where addressId = :a.id")
    @GetGeneratedKeys
    public abstract Long update(@BindBean("a") Address address);

    @Override
    @SqlUpdate("delete from ADDRESS where addressId = :id")
    public abstract void delete(@Bind("id") Long id);
}
