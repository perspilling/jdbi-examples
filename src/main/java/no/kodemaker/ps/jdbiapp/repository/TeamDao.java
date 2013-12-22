package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Team;
import no.kodemaker.ps.jdbiapp.repository.jdbi.TableCreator;

/**
 * @author Per Spilling
 */
public interface TeamDao extends CrudDao<Team>, TableCreator {
    Team findByName(String name);
}
