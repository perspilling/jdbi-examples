package no.kodemaker.ps.jdbiapp.repository.mappers;

import no.kodemaker.ps.jdbiapp.repository.PersonAddressAssoc;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Per Spilling
 */

public class PersonAddressMapper implements ResultSetMapper<PersonAddressAssoc> {
    @Override
    public PersonAddressAssoc map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new PersonAddressAssoc(rs.getLong("personId"), rs.getLong("addressId"));
    }
}
