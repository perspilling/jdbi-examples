package no.kodemaker.ps.jdbiapp.domain;

/**
 * An example POJO entity class to be used with JDBI.
 *
 * @author Per Spilling
 */
public class Person extends EntityWithLongId {
    // required fields
    private String name;
    private Email email;

    // optional fields
    private String phone;
    private Address homeAddress;

    public Person(String name, Email email) {
        validateName(name);
        validateEmail(email);
        this.name = name;
        this.email = email;
    }

    public Person(Long id, String name, Email email, String phone) {
        super(id);
        validateName(name);
        validateEmail(email);

        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("'name' may not be null");
        }
    }

    private void validateEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("'email' may not be null");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public Email getEmail() {
        return email;
    }

    public String getEmailVal() {
        return email.getVal();
    }


    public void setEmail(Email email) {
        validateEmail(email);
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (!name.equals(person.name)) return false;
        if (!email.equals(person.email)) return false;
        if (phone != null ? !phone.equals(person.phone) : person.phone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
