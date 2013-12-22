package no.kodemaker.ps.jdbiapp.repository;

/**
 * @author Per Spilling
 */
public class AddressTableCreator extends TableCreatorBase {

    public static String tableName = "ADDRESS";

    public static String createAddressTableSql_postgres =
            "create table ADDRESS (" +
                    "addressId serial PRIMARY KEY, " +
                    "streetAddress varchar(50) NOT NULL, " +
                    "postalCode varchar(8) NOT NULL, " +
                    "postalPlace varchar(50) NOT NULL)";

    public AddressTableCreator() {
        super(tableName, createAddressTableSql_postgres);
    }

}
