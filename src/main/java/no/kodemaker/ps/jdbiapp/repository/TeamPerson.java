package no.kodemaker.ps.jdbiapp.repository;

/**
 * This is a db-technical mapping class used for mapping the association from team to persons.
 * 
 * @author Per Spilling
 */
public class TeamPerson {
    private Long teamId;
    private Long personId;

    public TeamPerson(Long teamId, Long personId) {
        this.teamId = teamId;
        this.personId = personId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /**
     * Must implement equals and hashCode to make the mapping class work properly
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamPerson that = (TeamPerson) o;

        if (personId != null ? !personId.equals(that.personId) : that.personId != null) return false;
        if (teamId != null ? !teamId.equals(that.teamId) : that.teamId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = teamId != null ? teamId.hashCode() : 0;
        result = 31 * result + (personId != null ? personId.hashCode() : 0);
        return result;
    }
}
