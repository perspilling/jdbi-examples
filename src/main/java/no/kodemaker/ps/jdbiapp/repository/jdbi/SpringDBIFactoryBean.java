package no.kodemaker.ps.jdbiapp.repository.jdbi;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.spring.DBIFactoryBean;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * @author Per Spilling
 */
@Named
public class SpringDBIFactoryBean extends DBIFactoryBean {
    @Inject
    public SpringDBIFactoryBean(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public DBI getDBI() {
        try {
            return (DBI) getObject();
        } catch (Exception e) {
            throw new RuntimeException("Caught Exception when calling dbiFactoryBean.getObject()");
        }
    }
}
