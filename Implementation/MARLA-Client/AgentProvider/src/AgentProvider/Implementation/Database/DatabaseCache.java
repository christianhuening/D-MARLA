package AgentProvider.Implementation.Database;

import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the connection to the database as an unlimited cache.
 */
public class DatabaseCache extends DatabaseAccessor implements ICache {

    private String tableName;
    private final PreparedStatement selectStatement;
    private ResultSet resultSet;
    private int size;
    private Map<StateAction, Float> buffer;
    private int elemCount;


    public DatabaseCache(Connection connection, String tableName, int size) throws TechnicalException {
        super(connection);
        this.tableName = tableName;
        this.size = size;
        buffer = new HashMap<StateAction, Float>(size);
        elemCount = 0;

        createTableIfNotExists(tableName, "(key VARCHAR(255) NOT NULL PRIMARY KEY, value FLOAT)");

        try {
            selectStatement = connection.prepareStatement("select value from " + tableName + " where key = ?");
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }

    @Override
    public void store(StateAction stateAction, float value) throws TechnicalException {
        if(elemCount >= size) {
            flush();
        }

        if(!buffer.containsKey(stateAction)) {
            elemCount++;
        }

        buffer.put(stateAction, value);
    }

    @Override
    public float remove(StateAction stateAction) throws ValueNotFoundException, TechnicalException {
        Float result;
        result = buffer.get(stateAction);

        if (result != null) {
            buffer.remove(stateAction);
            return result;
        }

        result = readFromDb(stateAction);
        if (result != null) {
            return result;
        } else {
            throw new ValueNotFoundException();
        }
    }

    @Override
    public boolean hasStored(StateAction stateAction) throws TechnicalException {
        return
                buffer.containsKey(stateAction)
                        || (readFromDb(stateAction) != null);
    }

    @Override
    public float get(StateAction stateAction) throws ValueNotFoundException, TechnicalException {

        Float result;
        result = buffer.get(stateAction);

        if (result != null) {
            return result;
        }

        result = readFromDb(stateAction);
        if (result != null) {
            store(stateAction, result);
            return result;
        } else {
            throw new ValueNotFoundException();
        }
    }

    private Float readFromDb(StateAction stateAction) throws TechnicalException {
        // if that fails read data from db and update caches
        try {
            selectStatement.setString(1, stateAction.getCompressedRepresentation());
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getFloat("value");
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }

    @Override
    public void flush() throws TechnicalException {
        try {
            Statement statement = activeConnection().createStatement();

            for (Map.Entry<StateAction, Float> stateActionFloatEntry : buffer.entrySet()) {
                statement.addBatch(String.format("merge into %s (key, value) key(key) values ('%s', %s)", tableName, stateActionFloatEntry.getKey().getCompressedRepresentation(), stateActionFloatEntry.getValue()));
            }

            statement.executeBatch();
            elemCount = 0;
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }

    public void clear() throws TechnicalException {
        try {
            activeConnection().createStatement().execute("drop table " + tableName);
            createTableIfNotExists(tableName, "(key VARCHAR(255) NOT NULL PRIMARY KEY, value FLOAT)");
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }
}
