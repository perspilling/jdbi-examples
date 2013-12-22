package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Address;
import no.kodemaker.ps.jdbiapp.domain.Email;
import no.kodemaker.ps.jdbiapp.domain.Person;

/**
 * @author Per Spilling
 */
public class DbSeeder {
    public static void initPersonTable(PersonDao dao) {
        Person person = new Person("Per Spilling", new Email("per@kodemaker.no"));
        person.setHomeAddress(new Address("Main Street 2", "98767", "Disneyworld"));
        dao.save(person);

        person = new Person("Per Spelling", new Email("pspelling@nomail.com"));
        person.setHomeAddress(new Address("High Street 57", "6765", "London"));
        dao.save(person);

        dao.save(new Person("Neil Armstrong", new Email("armstrong@nomail.com")));
        dao.save(new Person("Edwin Aldrin", new Email("aldrin@nomail.com")));
        dao.save(new Person("Michael Collins", new Email("collins@nomail.com")));

        dao.save(new Person("Superman", new Email("superman@nomail.com")));
        dao.save(new Person("Spiderman", new Email("spiderman@nomail.com")));
        dao.save(new Person("Batman", new Email("batman@nomail.com")));
    }
}
