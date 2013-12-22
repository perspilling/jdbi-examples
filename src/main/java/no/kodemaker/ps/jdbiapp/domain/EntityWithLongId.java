package no.kodemaker.ps.jdbiapp.domain;

import java.io.Serializable;

/**
 * Abstract superclass for entities with an id of type long.
 *
 * @author Per Spilling
 */
public abstract class EntityWithLongId implements Serializable {
    protected Long id;

    protected EntityWithLongId() {
    }

    protected EntityWithLongId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityWithLongId)) return false;

        EntityWithLongId entity = (EntityWithLongId) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
