package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Person;

import java.util.List;

/**
 * @author Per Spilling
 */
public interface PersonDao extends CrudDao<Person>  {
    List<Person> findByName(String name);
    List<Person> findByEmail(String email);
}
