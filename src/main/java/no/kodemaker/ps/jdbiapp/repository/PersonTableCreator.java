package no.kodemaker.ps.jdbiapp.repository;

import no.kodemaker.ps.jdbiapp.repository.jdbi.TableCreatorBase;

/**
 * @author Per Spilling
 */
public class PersonTableCreator extends TableCreatorBase {

    final static String tableName = "PERSON";

    final static String createTableSql_postgres =
            "create table PERSON (" +
                    "personId serial PRIMARY KEY, " +
                    "name varchar(80) NOT NULL, " +
                    "email varchar(80), " +
                    "phone varchar(20))";

    public PersonTableCreator() {
        super(tableName, createTableSql_postgres);
    }
}
