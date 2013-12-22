package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.domain.Person;
import no.kodemaker.ps.jdbiapp.domain.Team;
import no.kodemaker.ps.jdbiapp.repository.innerclass.PersonInnerClassJdbiDao;
import no.kodemaker.ps.jdbiapp.repository.jdbi.JdbiHelper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionCallback;
import org.skife.jdbi.v2.TransactionStatus;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import java.util.List;

/**
 * Repository for {@link no.kodemaker.ps.jdbiapp.domain.Team} objects. Internally it uses the {@link no.kodemaker.ps.jdbiapp.repository.TeamDaoJdbi.TeamDao} SQL
 * Object interface. The {@link no.kodemaker.ps.jdbiapp.domain.Person} collection association is mapped via
 * a mapping table (TEAM_PERSON).
 *
 * @author Per Spilling
 */
public class TeamDaoJdbi implements TeamDao {
    private JdbiHelper jdbiHelper = new JdbiHelper();
    private final DBI dbi = jdbiHelper.getDBI();

    // Let DBI manage the db connections
    private TeamDao teamDao = dbi.onDemand(TeamDao.class);
    private TeamPersonDao teamPersonDao = dbi.onDemand(TeamPersonDao.class); // team->person mapping table
    private PersonDao personDaoJdbi = new PersonInnerClassJdbiDao();

    public TeamDaoJdbi() {
    }

    @Override
    public void resetTable() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createTable() {
        jdbiHelper.createTableIfNotExist(TeamDao.TEAM_TABLE_NAME, TeamDao.createTeamTableSql_postgres);
        jdbiHelper.createTableIfNotExist(TeamPersonDao.TEAM_PERSON_TABLE_NAME, TeamPersonDao.createTeamPersonMappingTableSql);
    }

    @Override
    public void dropTable() {
        jdbiHelper.dropTableIfExist(TeamDao.TEAM_TABLE_NAME);
        jdbiHelper.dropTableIfExist(TeamPersonDao.TEAM_PERSON_TABLE_NAME);
    }

    @Override
    public Team save(final Team team) {
        if (team.getId() == null) {
            long id = insert(team);
            return get(id);
        }

        // Must update 2 tables here, so will do this in a transaction
        dbi.inTransaction(new TransactionCallback<Long>() {
            @Override
            public Long inTransaction(Handle conn, TransactionStatus status) throws Exception {
                // attach the dao's to the same handle
                TeamDao teamDao = conn.attach(TeamDao.class);
                TeamPersonDao teamPersonDao = conn.attach(TeamPersonDao.class);
                if (team.getPointOfContact() != null) {
                    teamDao.updateWithPoC(team);
                } else {
                    teamDao.updateWithoutPoC(team);
                }
                for (Person p : team.getMembers()) {
                    // update the team->person mapping table
                    TeamPerson tp = new TeamPerson(team.getId(), p.getId());
                    if (!teamPersonDao.findByTeamId(team.getId()).contains(tp)) {
                        teamPersonDao.insert(tp);
                    }
                }
                return team.getId();
            }
        });
        // the get must be done after the tx has been committed
        return get(team.getId());
    }

    private long insert(final Team team) {
        return dbi.inTransaction(new TransactionCallback<Long>() {
            @Override
            public Long inTransaction(Handle conn, TransactionStatus status) throws Exception {
                long teamId;
                TeamDao teamDao = conn.attach(TeamDao.class);
                TeamPersonDao teamPersonDao = conn.attach(TeamPersonDao.class);
                if (team.getPointOfContact() != null) {
                    teamId = teamDao.insertWithPoC(team);
                } else {
                    teamId = teamDao.insertWithoutPoC(team);
                }
                for (Person p : team.getMembers()) {
                    // update the team->person mapping table
                    teamPersonDao.insert(new TeamPerson(teamId, p.getId()));
                }
                return teamId;
            }
        });
    }

    public Team insertWithTxAnnotations(final Team team) {
        // in this case we use an explicit handle, and attach the dao's to the same handle (connection)
        try (Handle handle = jdbiHelper.getTxHandle()) {
            handle.begin();
            TeamDao teamDao = handle.attach(TeamDao.class);
            TeamPersonDao teamPersonDao = handle.attach(TeamPersonDao.class); // team->person mapping table

            long teamId;
            if (team.getPointOfContact() != null) {
                teamId = teamDao.insertWithPoC(team);
            } else {
                teamId = teamDao.insertWithoutPoC(team);
            }
            for (Person p : team.getMembers()) {
                // update the team->person mapping table
                teamPersonDao.insert(new TeamPerson(teamId, p.getId()));
            }
            handle.commit();
            return get(teamId);
        }
    }

    /**
     * This method is used to test that the transaction handling is working as expected, i.e.
     * the inserts should be rolled back when the exception occurs.
     */
    public Team insertWithTxAnnotationsFailing(final Team team) {
        // in this case we use an explicit handle, and attach the dao's to the same handle (connection)
        try (Handle handle = jdbiHelper.getTxHandle()) {
            handle.begin();
            TeamDao teamDao = handle.attach(TeamDao.class);
            TeamPersonDao teamPersonDao = handle.attach(TeamPersonDao.class); // team->person mapping table

            long teamId;
            if (team.getPointOfContact() != null) {
                teamId = teamDao.insertWithPoC(team);
            } else {
                teamId = teamDao.insertWithoutPoC(team);
            }
            for (Person p : team.getMembers()) {
                // update the team->person mapping table
                teamPersonDao.insert(new TeamPerson(teamId, p.getId()));
            }
            teamDao.failingMethod(); // should cause the TX to be rolled back
            handle.commit();
            return get(teamId);
        }
    }

    /**
     * This method is used to test that the transaction handling is working as expected, i.e.
     * the inserts should be rolled back when the exception occurs.
     */
    public Team insertWithTxFailing(final Team team) {
        // in this case we use an explicit handle, and attach the dao's to the same handle (connection)
        Long teamId = dbi.inTransaction(new TransactionCallback<Long>() {
            @Override
            public Long inTransaction(Handle handle, TransactionStatus status) throws Exception {
                long teamId;
                TeamDao teamDao = handle.attach(TeamDao.class);
                TeamPersonDao teamPersonDao = handle.attach(TeamPersonDao.class);
                if (team.getPointOfContact() != null) {
                    teamId = teamDao.insertWithPoC(team);
                } else {
                    teamId = teamDao.insertWithoutPoC(team);
                }
                for (Person p : team.getMembers()) {
                    // update the team->person mapping table
                    teamPersonDao.insert(new TeamPerson(teamId, p.getId()));
                }
                teamDao.failingMethod(); // should cause the TX to be rolled back
                return teamId;
            }
        });
        // the get must be done after the tx has been committed
        return get(teamId);
    }


    @Override
    public Team get(Long pk) {
        Team team = teamDao.get(pk);
        getTeamMembers(team, teamPersonDao, personDaoJdbi);
        return team;
    }

    private void getTeamMembers(Team team, TeamPersonDao teamPersonDao, PersonDao personDao) {
        List<TeamPerson> teamPersonList = teamPersonDao.findByTeamId(team.getId());
        for (TeamPerson tp : teamPersonList) {
            team.addMember(personDao.get(tp.getPersonId()));
        }
    }

    @Override
    public Team findByName(String name) {
        List<Team> teamList = teamDao.findByName(name);
        if (teamList.size() > 0) {
            return get(teamList.get(0).getId());
        } else {
            return null;
        }
    }

    @Override
    public List<Team> getAll() {
        List<Team> teams = teamDao.getAll();
        for (Team t : teams) {
            getTeamMembers(t, teamPersonDao, personDaoJdbi);
        }
        return teams;
    }

    @Override
    public boolean exists(Long id) {
        return get(id) != null;
    }

    @Override
    public void delete(Long id) {
        // delete associated entries from TEAM and TEAM_PERSON tables, but not from the PERSON table
        Team team = get(id);
        if (team == null) return;

        for (Person p : team.getMembers()) {
            teamPersonDao.delete(new TeamPerson(team.getId(), p.getId()));
        }
        teamDao.delete(team.getId());
    }

    /**
     * Internal Team -> Person association dao
     */
    @RegisterMapper(TeamPersonMapper.class)
    interface TeamPersonDao extends Transactional<TeamPersonDao> {

        final static String TEAM_PERSON_TABLE_NAME = "TEAM_PERSON";

        final static String createTeamPersonMappingTableSql =
                "create table TEAM_PERSON (" +
                        "teamId integer REFERENCES TEAM, " +
                        "personId integer REFERENCES PERSON, " +
                        "PRIMARY KEY (teamId, personId) );";


        @SqlUpdate("insert into TEAM_PERSON (teamId, personId) values (:tp.teamId, :tp.personId)")
        @GetGeneratedKeys
        @Transaction
        long insert(@BindBean("tp") TeamPerson teamPerson);

        @SqlQuery("select * from TEAM_PERSON where teamId = :teamId")
        List<TeamPerson> findByTeamId(@Bind("teamId") Long teamId);

        @SqlUpdate("delete from TEAM where teamId = :tp.teamId and personId = :tp.personId")
        @Transaction
        void delete(@BindBean("tp") TeamPerson teamPerson);
    }

    /**
     * Generated JDBI DAO for the {@link Team} class.
     */
    @RegisterMapper(TeamMapper.class)
    static abstract class TeamDao {
        static String TEAM_TABLE_NAME = "TEAM";

        static String createTeamTableSql_postgres =
                "create table TEAM (" +
                        "teamId serial PRIMARY KEY, " +
                        "name varchar(80) NOT NULL, " +
                        "pocPersonId integer REFERENCES PERSON (personId), " +
                        "unique(name))";

        @SqlUpdate("insert into TEAM (teamId, name, pocPersonId) values (default, :t.name, :t.pointOfContactId)")
        @GetGeneratedKeys
        @Transaction
        abstract long insertWithPoC(@BindBean("t") Team team);

        @SqlUpdate("insert into TEAM (teamId, name, pocPersonId) values (default, :t.name)")
        @GetGeneratedKeys
        @Transaction
        abstract long insertWithoutPoC(@BindBean("t") Team team);

        @SqlUpdate("update TEAM set name = :t.name,  pocPersonId = :t.pointOfContactId where teamId = :t.id")
        @Transaction
        abstract void updateWithPoC(@BindBean("t") Team team);

        @SqlUpdate("update TEAM set name = :t.name where teamId = :t.id")
        @Transaction
        abstract void updateWithoutPoC(@BindBean("t") Team team);

        @SqlQuery("select * from TEAM where teamId = :id")
        abstract Team get(@Bind("id") long id);

        @SqlQuery("select * from TEAM where name like :name")
        abstract List<Team> findByName(@Bind("name") String name);

        @SqlQuery("select * from TEAM")
        abstract List<Team> getAll();

        @SqlUpdate("delete from TEAM where teamId = :id")
        @Transaction
        abstract void delete(@Bind("id") long id);

        @Transaction
        void failingMethod() {
            throw new IllegalStateException("In failingMethod");
        }
    }
}
