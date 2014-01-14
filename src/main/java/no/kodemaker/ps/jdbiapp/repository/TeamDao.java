package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Team;

/**
 * @author Per Spilling
 */
public interface TeamDao extends CrudDao<Team> {
    Team findByName(String name);
}
