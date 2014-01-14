package no.kodemaker.ps.jdbiapp.repository.jdbi;

/**
 * @author Per Spilling
 */
public class TableCreatorBase implements TableCreator {
    protected JdbiHelper jdbiHelper;
    private String tableName;
    private String createTableSql;

    public TableCreatorBase(String tableName, String createTableSql) {
        this.tableName = tableName;
        this.createTableSql = createTableSql;
        this.jdbiHelper = new JdbiHelper();
    }

    @Override
    public void createTable() {
        jdbiHelper.createTableIfNotExist(tableName, createTableSql);
    }

    @Override
    public void dropTable() {
        jdbiHelper.dropTableIfExist(tableName);
    }

    @Override
    public void resetTable() {
        jdbiHelper.resetTable(tableName, createTableSql);
    }
}
