package no.kodemaker.ps.jdbiapp.repository.mappers;

import no.kodemaker.ps.jdbiapp.domain.Email;
import no.kodemaker.ps.jdbiapp.domain.Person;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A JDBI mapper class for {@link no.kodemaker.ps.jdbiapp.domain.Person} instances.
 *
 * @author Per Spilling
 */
public class PersonMapper implements ResultSetMapper<Person> {
    public static PersonMapper INSTANCE = new PersonMapper();

    public Person map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new Person(rs.getLong("personId"), rs.getString("name"), new Email(rs.getString("email")), rs.getString("phone"));
    }
}
