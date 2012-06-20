package AgentProvider.Implementation.Database;

import AgentProvider.Implementation.Agents.IDictionary;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import Exceptions.ErrorMessages;
import AgentSystemPluginAPI.Contract.StateAction;

import java.sql.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * This class implements a database table based persistence for agent learning data.
 */
class DBDictionary extends DatabaseAccessor implements IDictionary {

    private PreparedStatement selectStatement;
    private PreparedStatement mergeStatement;
    private PreparedStatement similarStatement;
    private String tableName;

    //caching only for performance reasons
    private ResultSet resultSet;
    private Statement statement;
    private Map<StateAction, Float> bufferedStates = null;
    private StateAction activeState = null;

    /**
     * @param connection an existing database connection != null
     * @name the agent's name. != null
     */
    public DBDictionary(Connection connection, String tableName) throws TechnicalException {
        super(connection);

        this.tableName = tableName;

        createTableIfNotExists(tableName, "(key VARCHAR(255) NOT NULL PRIMARY KEY, value FLOAT)");

        try {
            selectStatement = connection.prepareStatement("select value from " + tableName + " where key = ?");
            mergeStatement = connection.prepareStatement("merge into " + tableName + " (key, value) key(key) values (?, ?)");
            similarStatement = connection.prepareStatement("select key, value from " + tableName + " where key like ?");
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }

    public float getValue(StateAction key) throws TechnicalException {
        try {
            selectStatement.setString(1, key.getCompressedRepresentation());
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getFloat("value");
            } else {
                return 0.0f;
            }

        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }

    public void setValue(StateAction key, float newValue) throws TechnicalException {
        if((activeState != null && activeState.equals(key) ) || bufferedStates != null && bufferedStates.containsKey(key)) {
            activeState = null;
            bufferedStates = null;
        }

        try {
            mergeStatement.setString(1, key.getCompressedRepresentation());
            mergeStatement.setFloat(2, newValue);
            mergeStatement.execute();
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }

    @Override
    public Map<StateAction, Float> getAllSimilarStatesFor(StateAction key) throws TechnicalException {
        if (activeState == null || !activeState.equals(key)) {
            try {
                similarStatement.setString(1, key.getCompressedRepresentation() + "%");
                resultSet = similarStatement.executeQuery();


                 bufferedStates = new Hashtable<StateAction, Float>();

                while (resultSet.next()) {
                    bufferedStates.put(new StateAction(resultSet.getString("key")), resultSet.getFloat("value"));
                }

                if (bufferedStates.size() < 1) {
                    bufferedStates.put(key, 0.0f);
                }

                return bufferedStates;

            } catch (SQLException e) {
                throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
            }
        } else {
           return bufferedStates;
        }
    }

    public void resetValues() throws TechnicalException {
        try {
            activeConnection().createStatement().execute("drop table " + tableName);
            createTableIfNotExists(tableName, "(key VARCHAR(10) NOT NULL PRIMARY KEY, value FLOAT)");
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") + "\nReason}\n" + e);
        }
    }
}