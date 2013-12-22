package no.kodemaker.ps.jdbiapp.repository;

/**
 * This is a db-technical mapping class used for mapping the association from persons to address.
 * 
 * @author Per Spilling
 */
public class PersonAddressAssoc {
    private Long personId;
    private Long addressId;

    public PersonAddressAssoc(Long personId, Long addressId) {
        this.personId = personId;
        this.addressId = addressId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
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

        PersonAddressAssoc that = (PersonAddressAssoc) o;

        if (!addressId.equals(that.addressId)) return false;
        if (!personId.equals(that.personId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = personId.hashCode();
        result = 31 * result + addressId.hashCode();
        return result;
    }
}
