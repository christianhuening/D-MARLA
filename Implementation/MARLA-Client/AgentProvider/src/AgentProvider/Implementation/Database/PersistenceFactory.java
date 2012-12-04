package AgentProvider.Implementation.Database;

import AgentProvider.Implementation.Agents.IDictionary;
import AgentProvider.Implementation.PersistenceType;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import Exceptions.ErrorMessages;
import Settings.SettingException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class creates objects that handle persistent client data like learning data dictionaries or agent setting
 * accessors.
 */
public class PersistenceFactory {
    private String pathToAgentSystemPlugin;
    private static boolean driverLoaded;
    private static Connection connection;
    private ResultSet resultSet;

    public PersistenceFactory(String pathToAgentSystemPlugin) throws TechnicalException, SettingException {
        this.pathToAgentSystemPlugin = pathToAgentSystemPlugin;

        //Load JDBC Driver for the specified dialect if not already happened
        if (!driverLoaded) {
            try {
                Class.forName("org.h2.Driver").newInstance();
                driverLoaded = true;
            } catch (InstantiationException e) {
                throw new TechnicalException(ErrorMessages.get("unableToInstantiateJDBCDiver") + "\n" + e);
            } catch (IllegalAccessException e) {
                throw new TechnicalException(ErrorMessages.get("illegalAccess") + "\n" + e);
            } catch (ClassNotFoundException e) {
                throw new TechnicalException(ErrorMessages.get("unknownJDBCDriverClass") + "\n" + e);
            }
        }
    }

    private Connection getConnection() throws TechnicalException {


        //Create database connection for the given agentSystem
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:h2:" + pathToAgentSystemPlugin + "/LearningData;DB_CLOSE_ON_EXIT=FALSE");
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                throw new TechnicalException(ErrorMessages.get("databaseConnectionError") + "jdbc:h2:" + pathToAgentSystemPlugin + "/LearningData;DB_CLOSE_ON_EXIT=FALSE" +  "\n\nReason:\n" + ex.getMessage());
            }
        }

        return connection;
    }

    public IDictionary getDictionary(String agentName, String dictionaryName, PersistenceType type) throws TechnicalException {
        if (type == PersistenceType.Table) {
            return new DBDictionary(getConnection(), agentName + "_" + dictionaryName);
        }

        return null;
    }

    public AgentSettingsAccessor getAgentSettingsAccessor(String agentName) throws TechnicalException {
        return new AgentSettingsAccessor(getConnection(), agentName);
    }

    public List<String> getAgents() throws TechnicalException {
        try {
            resultSet = connection.createStatement().executeQuery("select name from Agents");
            List<String> result = new LinkedList<String>();

            while (resultSet.next()) {
                result.add(resultSet.getString("name"));
            }

            return result;

        } catch (SQLException e) {
            throw new TechnicalException(ErrorMessages.get("databaseError") +"\n\nReason:\n" + e);
        }
    }
}