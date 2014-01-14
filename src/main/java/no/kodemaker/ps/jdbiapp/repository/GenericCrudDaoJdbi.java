package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.EntityWithLongId;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import no.kodemaker.ps.jdbiapp.repository.jdbi.TableCreator;

import java.util.List;

/**
 * @author Per Spilling
 */
public abstract class GenericCrudDaoJdbi<T extends EntityWithLongId> implements CrudDao<T>, TableCreator {
    protected JdbiHelper jdbiHelper;

    private String tableName;
    private String tableCreateSql;
    private InternalCrudDao<T> dao;
    //private Class<InternalCrudDao<T>> internalDaoClass;

    protected GenericCrudDaoJdbi() {
    }

    protected GenericCrudDaoJdbi(String tableName, String tableCreateSql) {
        this.tableName = tableName;
        this.tableCreateSql = tableCreateSql;
        jdbiHelper = new JdbiHelper();
    }

    protected abstract InternalCrudDao<T> getInternalCrudDao();

    protected InternalCrudDao<T> getDao() {
        if (dao == null) {
            dao = getInternalCrudDao();
        }
        return dao;
    }

    @Override
    public void createTable() {
        jdbiHelper.createTableIfNotExist(tableName, tableCreateSql);
    }

    @Override
    public void dropTable() {
        jdbiHelper.dropTableIfExist(tableName);
    }

    public List<T> getAll() {
        return getDao().getAll();
    }

    @Override
    public T get(Long id) {
        return getDao().get(id);
    }

    @Override
    public boolean exists(Long id) {
        return (getDao().get(id) != null);
    }

    @Override
    public T save(T instance) {
        if (instance.getId() == null) {
            return get(getDao().insert(instance));
        } else {
            return get(getDao().update(instance));
        }
    }

    @Override
    public void delete(Long id) {
        getDao().delete(id);
    }
}
