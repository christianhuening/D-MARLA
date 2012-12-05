package AgentProvider.Implementation.Database;

import AgentProvider.Implementation.Agents.IDictionary;
import AgentSystemPluginAPI.Contract.StateAction;
import EnvironmentPluginAPI.Exceptions.TechnicalException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class implements a database table based persistence for agent learning data.
 */
class DBDictionary implements IDictionary {

    private ICache cache;

    /**
     * @param connection an existing database connection != null
     * @name the agent's name. != null
     */
    public DBDictionary(final Connection connection, String tableName) throws TechnicalException {

        cache = new MemoryCache(
                new MemoryCache(
                        new DatabaseCache(connection, tableName, 500),
                        20000),
                2000);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {

                try {
                    cache.flush();

                    if(!connection.isClosed()) {
                        connection.close();
                    }
                    System.err.println("test");
                } catch (TechnicalException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public float getValue(StateAction key) throws TechnicalException {
        try {
            return cache.get(key);
        } catch (ValueNotFoundException e) {
            cache.store(key, 0.0f);
        }

        return 0.0f;
    }

    @Override
    public void setValue(StateAction key, float newValue) throws TechnicalException {
        cache.store(key, newValue);
    }

    @Override
    public void resetValues() throws TechnicalException {
        cache.clear();
    }
}