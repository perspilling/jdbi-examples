package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.EntityWithLongId;

/**
 * A generic CRUD interface for DAO's.
 *
 * @author Per Spilling
 */
public interface CrudDao<T extends EntityWithLongId> {
    T get(Long id);

    int count();

    boolean exists(Long id);

    T save(T instance);

    void delete(Long id);
}
