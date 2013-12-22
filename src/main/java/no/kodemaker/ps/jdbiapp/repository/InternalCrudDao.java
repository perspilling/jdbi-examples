package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.EntityWithLongId;
import org.skife.jdbi.v2.sqlobject.Transaction;

import java.util.List;

/**
 * @author Per Spilling
 */
public interface InternalCrudDao<T extends EntityWithLongId> {
    List<T> getAll();

    T get(Long id);

    @Transaction
    Long insert(T instance);

    @Transaction
    Long update(T instance);

    @Transaction
    void delete(Long id);
}
