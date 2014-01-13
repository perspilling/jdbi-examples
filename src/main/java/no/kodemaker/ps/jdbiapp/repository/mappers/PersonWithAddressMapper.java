package no.kodemaker.ps.jdbiapp.repository.mappers;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Email;
import no.kodemaker.ps.jdbiapp.domain.Person;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A JDBI mapper class for {@link no.kodemaker.ps.jdbiapp.domain.Person} instances where the
 * result set is expected to also contain the address fields from the address table, i.e. the
 * following fields:
 * personid, name, email, phone, addressid, streetaddress, postalcode, postalplace
 *
 * @author Per Spilling
 */
public class PersonWithAddressMapper implements ResultSetMapper<Person> {

    public Person map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        Person p = new Person(rs.getLong("personid"), rs.getString("name"),new Email(rs.getString("email")), rs.getString("phone"));
        Address a = new Address(rs.getLong("addressid"), rs.getString("streetaddress"), rs.getString("postalcode"), rs.getString("postalplace") );
        p.setHomeAddress(a);
        return p;
    }
}
