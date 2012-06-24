package PluginLoader.Interface;

import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This component implements methods for inspecting and loading agent system plugins at runtime. It also helps
 * finding and creating the different object types used by MARLA that may reside in an agent system plugin.
 */
public interface IAgentSystemPluginLoader {

    /**
     * Searches recursively for agent system plugins in the given directory.
     *
     * @return empty if no agent system plugins found in that directory
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException if technical errors prevent the component from loading the plugin described
     * @throws PluginLoader.Interface.Exceptions.PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     * @throws Settings.SettingException if the agentSystemPluginsFolder is not correctly set int he app's settings.
     */
    public List<TAgentSystemDescription> listAvailableAgentSystemPlugins() throws TechnicalException, PluginNotReadableException, SettingException;

    /**
     * Loads the specified environment plugin and returns an instance of it.
     *
     * @pre listAvailableAgentSystemPlugins must have been used before
     * @param agentSystem the agent system plugin to load != null
     * @return != null
     * @throws TechnicalException if technical errors prevent the component from loading the plugin described
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     */
    public IAgentSystemPluginDescriptor loadAgentSystemPlugin(TAgentSystemDescription agentSystem) throws TechnicalException, PluginNotReadableException;

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     * @param agentSystemDescription a description of an existing agent system plugin != null
     * @return null, if agent system plugin was not found
     * @throws TechnicalException if technical errors prevent the component from loading the plugin specified
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TAgentSystemDescription is provided
     */
    public File getAgentSystemPluginPath(TAgentSystemDescription agentSystemDescription) throws TechnicalException, PluginNotReadableException;


    /**
     * Creates an action description message. If the environment provides a custom implementation, it will be used.
     * Otherwise a default message is used. The message will be targeted to the server automatically
     * @pre Environment must be loaded!
     * @param actionDescription the action description to send
     * @param clientId the client's network id
     * @return not null
     */
    public NetworkMessage createActionDescriptionMessage(int clientId, IActionDescription actionDescription);
}
