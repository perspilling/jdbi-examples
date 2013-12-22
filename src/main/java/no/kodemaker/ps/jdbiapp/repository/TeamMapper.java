package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.domain.Team;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A JDBI mapper class for {@link no.kodemaker.ps.jdbiapp.domain.Team} instances.
 *
 * @author Per Spilling
 */
public class TeamMapper implements ResultSetMapper<Team> {
    private PersonDao personDao = new PersonDaoJdbi();

    public Team map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        long pocId = rs.getLong("pocPersonId");
        if (pocId != 0L) {
            Person poc = personDao.get(pocId);
            return new Team(rs.getLong("teamId"), rs.getString("name"), poc);
        } else {
            return new Team(rs.getLong("teamId"), rs.getString("name"));
        }
    }
}
