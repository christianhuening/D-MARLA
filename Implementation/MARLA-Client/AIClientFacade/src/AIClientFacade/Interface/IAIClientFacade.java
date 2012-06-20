package AIClientFacade.Interface;

import AIRunner.Interface.IAIRunner;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import Settings.SettingException;

import java.util.List;

/**
 *
 */
public interface IAIClientFacade extends
        IAIRunner
{

    //region Network
    /**
     * True, if an active connection to a GameServer is established.
     * @return
     */
    public boolean isConnected();

    /**
     * Returns the id that was assigned by the server.
     * @return >= 0
     * @throws NetworkAdapter.Interface.Exceptions.NotConnectedException if no server connection was established.
     */
    public int getClientId() throws NotConnectedException;
    //endregion


    //region Plugins
    /**
     * Returns a list of all available agent system plugins.
     * @return empty if no plugins found.
     */
    public List<TAgentSystemDescription> getAvailableAgentSystems() throws TechnicalException, SettingException, PluginNotReadableException;
    //endregion
}
