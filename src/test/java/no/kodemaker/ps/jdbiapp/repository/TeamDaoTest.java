package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Email;
import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.domain.Team;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skife.jdbi.v2.exceptions.CallbackFailedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Per Spilling
 */
public class TeamDaoTest {
    private static TeamDaoJdbi teamDao;
    private static PersonDao personDao;

    @BeforeClass
    public static void initDb() {
        new AddressTableCreator().resetTable();
        new PersonTableCreator().resetTable();
        new PersonAddressTableCreator().resetTable();
        new TeamDaoJdbi.TeamPersonTableCreator().resetTable();
        new TeamDaoJdbi.TeamTableCreator().resetTable();

        personDao = new PersonJdbiDao3();
        DbSeeder.initPersonTable(personDao);
        teamDao = new TeamDaoJdbi(personDao);
    }

    @Test
    public void testInsertAndGet() {
        Team team = createApollo11Team();
        Team savedTeam = teamDao.save(team);
        assertTrue(team.getMembers().size() == savedTeam.getMembers().size());
        assertTrue(savedTeam.getName().equals("Apollo11"));
        assertTrue(savedTeam.getMembers().size() == 3);
        assertTrue(savedTeam.getId() != null);
    }

    private Team createApollo11Team() {
        Person commander = personDao.findByName("Neil Armstrong").get(0);
        Team team = new Team("Apollo11", commander);
        team.addMember(commander);
        team.addMember(personDao.findByName("Edwin Aldrin").get(0));
        team.addMember(personDao.findByName("Michael Collins").get(0));
        return team;
    }


    @Test
    public void testInsertWithTxAnnotations() {
        Team team = createSuperHeroTeam();
        Team savedTeam = teamDao.insertWithTxHandle(team);
        assertTrue(team.getMembers().size() == savedTeam.getMembers().size());
        assertTrue(savedTeam.getName().equals("Superheros"));
        assertTrue(savedTeam.getMembers().size() == 3);
        assertTrue(savedTeam.getId() != null);
    }

    private Team createSuperHeroTeam() {
        Person commander = personDao.findByName("Superman").get(0);
        Team team = new Team("Superheros", commander);
        team.addMember(personDao.findByName("Spiderman").get(0));
        team.addMember(personDao.findByName("Batman").get(0));
        return team;
    }

    @Test
    public void testFailingTxWithHandle() {
        // check that the tx fails as expected and that nothing has been inserted
        Team fail = createFailingTeam();
        try {
            teamDao.insertWithTxHandle(fail);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
        Team t = teamDao.findByName(fail.getName());
        assertTrue(t == null);
    }

    @Test
    public void testFailingTxWithTxCallback() {
        // check that the tx fails as expected and that nothing has been inserted
        Team fail = createFailingTeam();
        try {
            teamDao.insertWithTxCallback(fail);
            fail();
        } catch (CallbackFailedException e) {
            /**
             * The IllegalStateException thrown in the dao is in this case wrapped in a CallbackFailedException
             */
            assertTrue(true);
        }
        Team t = teamDao.findByName(fail.getName());
        assertTrue(t == null);
    }

    private Team createSuperHeroTeam2() {
        Person commander = personDao.findByName("Superman").get(0);
        Team team = new Team("Superheros2", commander);
        team.addMember(personDao.findByName("Spiderman").get(0));
        team.addMember(personDao.findByName("Batman").get(0));
        return team;
    }

    private Team createFailingTeam() {
        return new Team("FAIL", personDao.findByName("Gollum").get(0));
    }

    @Test
    public void testUpdate() {
        Person edsger = new Person("Edsger Dijktstra", new Email("dijkstra@mail.com"));
        edsger = personDao.save(edsger);

        Team team = new Team("dining philosophers", edsger);
        team.addMember(edsger);
        team = teamDao.save(team);
        assertThat(team.getMembers().size(), equalTo(1));

        Person donald = personDao.save(new Person("Donald Knuth", new Email("knuth@nomail.com")));
        team.addMember(donald);
        team = teamDao.save(team);
        assertThat(team.getMembers().size(), equalTo(2));
    }

    @Test
    public void deletingTeamShouldNotDeletePersons() {
        ensureTeamApolloPresent();
    }

    private void ensureTeamApolloPresent() {
    }


}
