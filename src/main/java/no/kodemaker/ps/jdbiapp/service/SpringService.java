package no.kodemaker.ps.jdbiapp.service;

import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.PersonDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Note: Regrettably Spring TX does not work with jDBI SQL Objects, only with the jDBI fluent API
 * using DBIUtil.getHandle(dbi) to get a handle that participates in the Spring TX. See the
 * following forum post for more on this:
 * https://groups.google.com/forum/#!searchin/jdbi/Spring/jdbi/B_jQ89Au970/VHBG-g9eqpUJ
 * <p>
 * So in this test we can only test that jDBI gets it's datasource from Spring and succeeds
 * in performing persistence operations.
 * </p>
 *
 * @author Per Spilling
 */
@Named
public class SpringService {

    @Inject
    private PersonDao personDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void changePhoneNumber(String name, String number) {
        Person person = personDao.findByName(name).get(0);
        person.setPhone(number);
        personDao.save(person);
    }

}
