package AgentProvider.Implementation.Database;

import AgentProvider.Implementation.KeyNotFoundException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class AgentSettingsAccessor extends DatabaseAccessor {

    private ResultSet resultSet;
    private PreparedStatement selectStatement;
    private PreparedStatement mergeStatement;
    private String agentName;


    public AgentSettingsAccessor(Connection connection, String agentName) throws TechnicalException {
        super(connection);

        this.agentName = agentName;

        // use db connection to ensure correct table structure
        createTableIfNotExists("Agents", "(name varchar(30) not null primary key, unique(name))");

        createTableIfNotExists("AgentSettings", "(agentName varchar(30) not null," +
                                                "key varchar(15) not null," +
                                                "value float not null," +
                                                "unique(agentName, key)," +
                                                "primary key(agentName, key)," +
                                                "foreign key(agentName) references Agents(name))");

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("merge into Agents(name) key(name) values ('" + agentName + "')");
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") +"\n\nReason:\n" + e);
        }

        //compile heavily used statements
        try {
            selectStatement = connection.prepareStatement("select value from AgentSettings where agentName = '" + agentName + "' and key = ?");
            mergeStatement = connection.prepareStatement("merge into AgentSettings(agentName, key, value) key(agentName, key) values ('" + agentName + "', ?, ?)");
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") +"\n\nReason:\n" + e);
        }
    }

    public List<String> getAgentParameterKeys() throws TechnicalException {
        try {
            resultSet = activeConnection().createStatement().executeQuery("select key from AgentSettings where agentName = '" + agentName + "'");
            List<String> result = new LinkedList<String>();

            while (resultSet.next()) {
                result.add(resultSet.getString("key"));
            }

            return result;

        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") +"\n\nReason:\n" + e);
        }
    }



    /**
     * @param settingsKey the name of the setting to persist != null
     * @param value the new value
     * @throws TechnicalException
     * @PostCondition("If there was no entry for the given agentName and key, this method will have generated one. Else it will be updated.")
     */
    public void setValue(String settingsKey, float value) throws TechnicalException {
        try {
            mergeStatement.setString(1, settingsKey);
            mergeStatement.setFloat(2, value);

            mergeStatement.executeUpdate();
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") +"\n\nReason:\n" + e);
        }
    }

    /**
     * Returns the float value of the named setting.
     *
     * @param settingsKey @param settingsKey the name of the setting to persist != null
     * @return see description
     * @throws KeyNotFoundException if key was not found.
     */
    public float getValue(String settingsKey) throws KeyNotFoundException, TechnicalException {
        try {
            selectStatement.setString(1, settingsKey);

            resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getFloat("value");
            } else {
                throw new KeyNotFoundException();
            }
        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") +"\n\nReason:\n" + e);
        }
    }
}