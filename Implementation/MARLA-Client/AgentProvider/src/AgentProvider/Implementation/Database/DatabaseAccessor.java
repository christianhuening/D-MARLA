package AgentProvider.Implementation.Database;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class encapsulates low-level database actions.
 */
abstract class DatabaseAccessor {

    protected Connection connection;

    public DatabaseAccessor(Connection connection) throws TechnicalException {
        this.connection = connection;
    }

    /**
     * This method queries the database and looks for the given table name. If it is not already present, creates it.
     * @param name the table name. != null. Note: case sensitive
     * @param description JDBC-conform description of the table. != null
     * @throws TechnicalException if a technical error occured with the db connection
     */
    protected void createTableIfNotExists(String name, String description) throws TechnicalException {
        try {
            connection.createStatement().execute("create table if not exists " + name + description);
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "Reason:\n\n" + e);
        }
    }

    protected Connection activeConnection() {
        return connection;
    }

    @Override
    protected void finalize() throws Throwable {
        if(connection.isClosed()) {
            connection.close();
        }
        super.finalize();
    }
}
