package no.kodemaker.ps.jdbiapp.repository.jdbi;

import javax.sql.DataSource;

/**
 * @author Per Spilling
 */
public interface DataSourceProvider {
    DataSource getDataSource();
}
