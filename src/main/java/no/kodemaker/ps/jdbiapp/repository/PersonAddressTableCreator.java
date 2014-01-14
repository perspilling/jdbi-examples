package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.repository.jdbi.TableCreatorBase;

/**
 * @author Per Spilling
 */
public class PersonAddressTableCreator extends TableCreatorBase {

    final static String tableName = "PERSON_ADDRESS";

    final static String createTableSql_postgres =
            "create table PERSON_ADDRESS (" +
                    "personId integer REFERENCES PERSON, " +
                    "addressId integer REFERENCES ADDRESS, " +
                    "PRIMARY KEY (personId, addressId) )";

    public PersonAddressTableCreator() {
        super(tableName, createTableSql_postgres);
    }
}
