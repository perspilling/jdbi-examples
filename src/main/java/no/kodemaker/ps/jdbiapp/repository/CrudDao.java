package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.EntityWithLongId;

import java.util.List;

/**
 * @author Per Spilling
 */
public interface CrudDao<T extends EntityWithLongId> {
    List<T> getAll();

    T get(Long id);

    boolean exists(Long id);

    T save(T instance);

    void delete(Long id);
}
