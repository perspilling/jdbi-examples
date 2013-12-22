package no.kodemaker.ps.jdbiapp.repository;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Per Spilling
 */
@ContextConfiguration(locations = {"classpath:spring/spring-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractSpringDaoTest {
}
