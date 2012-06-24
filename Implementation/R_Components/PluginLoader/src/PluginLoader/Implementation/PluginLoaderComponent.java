package PluginLoader.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Contract.*;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IPluginLoader;
import Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This class represents the PluginLoaderComponent.
 */
public class PluginLoaderComponent implements IPluginLoader {

    private EnvironmentPluginLoaderUseCase environmentPluginLoaderUseCase;
    private AgentSystemPluginLoaderUseCase agentSystemPluginLoaderUseCase;

    public PluginLoaderComponent() throws TechnicalException, SettingException, PluginNotReadableException {
        environmentPluginLoaderUseCase = new EnvironmentPluginLoaderUseCase();
        agentSystemPluginLoaderUseCase = new AgentSystemPluginLoaderUseCase();
    }

    @Override
    public List<TEnvironmentDescription> listAvailableEnvironments() throws TechnicalException, PluginNotReadableException, SettingException {
        return environmentPluginLoaderUseCase.listAvailableEnvironments();
    }

    @Override
    public IEnvironmentPluginDescriptor loadEnvironmentPlugin(TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException {
        return environmentPluginLoaderUseCase.loadEnvironmentPlugin(environment);
    }

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     *
     * @param environmentDescription a description of an existing environment != null
     * @return null, if environment was not found
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException
     *          if technical errors prevent the component from loading the plugin specified
     * @throws PluginLoader.Interface.Exceptions.PluginNotReadableException
     *          if the plugin is not readable, for example if no TEnvironmentDescription is provided
     */
    @Override
    public File getEnvironmentPluginPath(TEnvironmentDescription environmentDescription) throws TechnicalException, PluginNotReadableException {
        return environmentPluginLoaderUseCase.getEnvironmentPluginPath(environmentDescription);
    }

    @Override
    public List<TAgentSystemDescription> listAvailableAgentSystemPlugins() throws TechnicalException, PluginNotReadableException, SettingException {
        return agentSystemPluginLoaderUseCase.listAvailableAgentSystemPlugins();
    }

    @Override
    public IAgentSystemPluginDescriptor loadAgentSystemPlugin(TAgentSystemDescription agentSystem) throws TechnicalException, PluginNotReadableException {
        return agentSystemPluginLoaderUseCase.loadAgentSystemPlugin(agentSystem);
    }

    /**
     * Returns the file handle for the directory, where the plugin jar is located at.
     *
     * @param agentSystemDescription a description of an existing agent system plugin != null
     * @return null, if agent system plugin was not found
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException
     *          if technical errors prevent the component from loading the plugin specified
     * @throws PluginLoader.Interface.Exceptions.PluginNotReadableException
     *          if the plugin is not readable, for example if no TAgentSystemDescription is provided
     */
    @Override
    public File getAgentSystemPluginPath(TAgentSystemDescription agentSystemDescription) throws TechnicalException, PluginNotReadableException {
        return agentSystemPluginLoaderUseCase.getAgentSystemPluginPath(agentSystemDescription);
    }

    @Override
    public NetworkMessage createEnvironmentStateMessage(int clientId, IEnvironmentState environmentState) {
        return environmentPluginLoaderUseCase.createEnvironmentStateMessage(environmentState,clientId);
    }

    @Override
    public NetworkMessage createActionDescriptionMessage(int clientId, IActionDescription actionDescription) {
        return agentSystemPluginLoaderUseCase.createActionDescriptionMessage(clientId, actionDescription);
    }

    @Override
    public AbstractVisualizeReplayPanel getReplayVisualizationForSwing() {
        return environmentPluginLoaderUseCase.getReplayVisualizationForSwing();
    }

    @Override
    public IVisualizeReplay getReplayVisualization() {
        return environmentPluginLoaderUseCase.getReplayVisualization();
    }
}
