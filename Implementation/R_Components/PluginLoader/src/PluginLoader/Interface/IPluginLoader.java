package PluginLoader.Interface;

import EnvironmentPluginAPI.Contract.*;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This component implements methods for inspecting and loading plugins at runtime. It also helps
 * finding and creating the different object types used by MARLA that reside in a plugin.
 */
public interface IPluginLoader {

    /**
     * Searches recursively for environment plugins in the given directory.
     *
     * @return empty if no environment plugins were found
     * @throws TechnicalException if technical errors prevent the component from loading the plugin described
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     * @throws SettingException @throws SettingException if the environmentPluginsfolder is not correctly set int he app's settings.
     */
    public List<TEnvironmentDescription> listAvailableEnvironments() throws TechnicalException, PluginNotReadableException, SettingException;

    /**
     * Loads the specified environment plugin an returns an instance of it.
     * @pre listAvailableEnvironments must have been used before
     * @param environment the environment plugin to load
     * @return != null
     * @throws TechnicalException if technical errors prevent the component from loading the plugin specified
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     */
    public IEnvironmentPluginDescriptor loadEnvironmentPlugin(TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException;

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     * @param environmentDescription a description of an existing environment != null
     * @return null, if environment was not found
     * @throws TechnicalException if technical errors prevent the component from loading the plugin specified
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     */
    public File getEnvironmentPluginPath(TEnvironmentDescription environmentDescription) throws TechnicalException, PluginNotReadableException;

    /**
     * Searches recursively for agent system plugins in the given directory.
     *
     * @return empty if no agent system plugins found in that directory
     * @throws TechnicalException if technical errors prevent the component from loading the plugin described
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     * @throws SettingException if the agentSystemPluginsFolder is not correctly set int he app's settings.
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
     * Creates an environment state message. If the environment provides a custom implementation, it will be used.
     * Otherwise a default message is used.
     * @pre Environment must be loaded!
     * @param clientId the client id of the client targeted to, != null
     * @param environmentState the environment state to send
     * @return not null
     */
    public NetworkMessage createEnvironmentStateMessage(int clientId, IEnvironmentState environmentState);

    /**
     * Creates an action description message. If the environment provides a custom implementation, it will be used.
     * Otherwise a default message is used. The message will be targeted to the server automatically
     * @pre Environment must be loaded!
     * @param actionDescription the action description to send
     * @param clientId the client's network id
     * @return not null
     */
    public NetworkMessage createActionDescriptionMessage(int clientId, IActionDescription actionDescription);

    /**
     * Loads the implementation of IVisualizeReplay from the pre-loaded Environment.
     * @pre Environment must be loaded!
     * @return not null
     */
    public IVisualizeReplay getReplayVisualization();

    /**
     * Loads the Swing compatible implementation of IVisualizeReplay from the pre-loaded Environment.
     * @pre Environment must be loaded!
     * @return not null
     */
    public AbstractVisualizeReplayPanel getReplayVisualizationForSwing();

}
