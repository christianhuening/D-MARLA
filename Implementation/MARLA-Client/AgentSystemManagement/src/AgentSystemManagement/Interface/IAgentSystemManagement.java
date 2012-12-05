package AgentSystemManagement.Interface;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.Settings.SettingException;

import java.util.List;


/**
 *  This Interface provides methods to create a new AgentSystem of a specified type,
 *  load a bunch of saved AgentSystems or just one specific AgentSystem
 */
public interface IAgentSystemManagement {

    /**
     * Returns a list of all available agent system plugins.
     * @return empty if no plugins found.
     */
    public List<TAgentSystemDescription> getAvailableAgentSystems() throws TechnicalException, SettingException, PluginNotReadableException;

    /**
     * Gets an instance of the specified agent system.
     *
     * @param toLoad the agent system plugin to load. != null
     * @return the agent sytem instance != null
     * @throws TechnicalException if any severe technical problems occur (i.e. insufficient file access)
     * @throws PluginNotReadableException if the specified plugin does not fully abide the contract
     */
    public IAgentSystem getAgentSystem(TAgentSystemDescription toLoad) throws TechnicalException, PluginNotReadableException, SettingException;

}