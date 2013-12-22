package no.kodemaker.ps.jdbiapp.domain;

/**
 * @author Per Spilling
 */
public class Address extends EntityWithLongId {
    // required fields
    private String streetAddress;
    private String postalCode;
    private String postalPlace;

    public Address() {
    }

    public Address(String streetAddress, String postalCode, String postalPlace) {
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.postalPlace = postalPlace;
    }

    public Address(Long id, String streetAddress, String postalCode, String postalPlace) {
        super(id);
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.postalPlace = postalPlace;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public Address setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Address setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getPostalPlace() {
        return postalPlace;
    }

    public Address setPostalPlace(String postalPlace) {
        this.postalPlace = postalPlace;
        return this;
    }
}
