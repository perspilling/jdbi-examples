package no.kodemaker.ps.jdbiapp.repository.mappers;

/**
 * Represents a joined row of the Person and Address tables.
 *
 * @author Per Spilling
 */
public class PersonAddressJoinRow {
    private Long personid;
    private String name;
    private String email;
    private String phone;
    private Long addressid;
    private String streetaddress;
    private String postalcode;
    private String postalplace;

    public Long getPersonid() {
        return personid;
    }

    public void setPersonid(Long personid) {
        this.personid = personid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getAddressid() {
        return addressid;
    }

    public void setAddressid(Long addressid) {
        this.addressid = addressid;
    }

    public String getStreetaddress() {
        return streetaddress;
    }

    public void setStreetaddress(String streetaddress) {
        this.streetaddress = streetaddress;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getPostalplace() {
        return postalplace;
    }

    public void setPostalplace(String postalplace) {
        this.postalplace = postalplace;
    }
}
