package no.kodemaker.ps.jdbiapp.repository.jdbi;

/**
 * @author Per Spilling
 */
public interface TableCreator {
    void createTable();
    void dropTable();
}
