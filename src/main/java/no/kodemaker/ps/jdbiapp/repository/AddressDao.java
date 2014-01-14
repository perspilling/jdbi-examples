package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;

/**
 * @author Per Spilling
 */
public interface AddressDao extends CrudDao<Address> {
    int count();
}
