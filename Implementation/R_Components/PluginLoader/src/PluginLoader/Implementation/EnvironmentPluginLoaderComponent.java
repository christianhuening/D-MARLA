package PluginLoader.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Contract.*;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IEnvironmentPluginLoader;
import Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This class represents the EnvironmentPluginLoaderComponent.
 */
public class EnvironmentPluginLoaderComponent implements IEnvironmentPluginLoader {

    private EnvironmentPluginLoaderUseCase environmentPluginLoaderUseCase;

    public EnvironmentPluginLoaderComponent() throws TechnicalException, SettingException, PluginNotReadableException {
        environmentPluginLoaderUseCase = new EnvironmentPluginLoaderUseCase();
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
    public IEnvironment createEnvironmentInstance(ISaveGameStatistics saveGameStatistics) throws TechnicalException{
        return environmentPluginLoaderUseCase.createEnvironmentInstance(saveGameStatistics);
    }

    @Override
    public NetworkMessage createEnvironmentStateMessage(int clientId, IEnvironmentState environmentState) {
        return environmentPluginLoaderUseCase.createEnvironmentStateMessage(environmentState,clientId);
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
