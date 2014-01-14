package no.kodemaker.ps.jdbiapp.domain;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Entity class for a team. The associated DAO class is {@link no.kodemaker.ps.jdbiapp.repository.TeamDaoJdbi}.
 *
 * @author Per Spilling
 */
public class Team extends EntityWithLongId {
    // required fields
    private String name;

    // optional fields
    private Person pointOfContact;
    private List<Person> members = Lists.newArrayList();

    public Team(String name) {
        this.name = name;
    }

    public Team(String name, Person pointOfContact) {
        this.name = name;
        this.pointOfContact = pointOfContact;
        addMember(pointOfContact);
    }

    public Team(Long id, String name) {
        super(id);
        this.name = name;
    }

    public Team(Long id, String name, Person pointOfContact) {
        super(id);
        this.name = name;
        this.pointOfContact = pointOfContact;
    }

    public String getName() {
        return name;
    }

    public Long getPointOfContactId() {
        return pointOfContact.getId();
    }

    public Person getPointOfContact() {
        return pointOfContact;
    }

    public void setPointOfContact(Person pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        if (members == null) throw new IllegalArgumentException("members may not be null");
        this.members = members;
    }

    public void addMember(Person p) {
        if (!members.contains(p)) members.add(p);
    }
}
