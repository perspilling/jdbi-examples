package no.kodemaker.ps.jdbiapp.repository.jdbi;

import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;

/**
 * @author Per Spilling
 */
public class H2DataSourceProvider implements DataSourceProvider {
    public DataSource getDataSource() {
        // using in-memory H2 database
        DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test", "username", "password");
        return ds;
    }
}
