package no.kodemaker.ps.jdbiapp.repository;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Per Spilling
 */
public class TeamPersonMapper implements ResultSetMapper<TeamPerson> {
    @Override
    public TeamPerson map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return new TeamPerson(rs.getLong("teamId"), rs.getLong("personId"));
    }
}
