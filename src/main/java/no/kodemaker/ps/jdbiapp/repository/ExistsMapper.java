package no.kodemaker.ps.jdbiapp.repository;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Per Spilling
 */
public class ExistsMapper implements ResultSetMapper<Boolean> {

    public Boolean map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
        return rs.first();
    }
}
