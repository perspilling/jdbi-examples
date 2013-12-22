package no.kodemaker.ps.jdbiapp.domain;

import java.io.Serializable;

/**
 * Value class representing an email address
 *
 * @author Per Spilling
 */
public class Email implements Serializable {
    private String val;

    public Email(String val) {
        if (!val.contains("@")) {
            throw new IllegalArgumentException("not a valid email address: " + val);
        }
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    @Override
    public String toString() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;

        Email email = (Email) o;

        if (!val.equals(email.val)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }
}
