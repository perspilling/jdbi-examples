package no.kodemaker.ps.jdbiapp.repository.mappers;

import no.kodemaker.ps.jdbiapp.domain.Address;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A JDBI mapper class for {@link no.kodemaker.ps.jdbiapp.domain.Address} instances.
 *
 * @author Per Spilling
 */
public class AddressMapper implements ResultSetMapper<Address> {

    public Address map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Address(
                rs.getLong("addressId"),
                rs.getString("streetAddress"),
                rs.getString("postalCode"),
                rs.getString("postalPlace"));
    }
}
