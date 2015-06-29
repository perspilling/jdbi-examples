package no.kodemaker.ps.jdbiapp.service;

import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.repository.DbSeeder;
import no.kodemaker.ps.jdbiapp.repository.PersonDao;
import no.kodemaker.ps.jdbiapp.repository.PersonInnerClassJdbiDao;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;

/**
 * @author Per Spilling
 */
@ContextConfiguration(locations = {"classpath:spring/spring-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringServiceTest {

    @Inject
    private SpringService springService;

    @Inject
    private PersonDao personDao;

    @BeforeClass
    public static void init() {
        PersonInnerClassJdbiDao dao = new PersonInnerClassJdbiDao();
        dao.dropTable();
        dao.createTable();
        DbSeeder.initPersonTable(dao);
    }

    @Test
    @Transactional
    public void testService() {
        String name = "Per Spilling";
        springService.changePhoneNumber(name, "12345678");
        Person person = personDao.findByName(name).get(0);

        springService.changePhoneNumber(name, "21212121");
        Person person2 = personDao.findByName(name).get(0);

        assertTrue(!person2.getPhone().equals(person.getPhone()));
    }
}
