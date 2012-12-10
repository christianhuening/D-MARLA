package PluginLoader.Implementation;

import EnvironmentPluginAPI.Contract.*;
import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Service.AbstractVisualizeReplayPanel;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import EnvironmentPluginAPI.Service.IVisualizeReplay;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IEnvironmentPluginLoader;
import ZeroTypes.Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This class represents the EnvironmentPluginLoaderComponent.
 */
public class EnvironmentPluginLoaderComponent implements IEnvironmentPluginLoader {

    private EnvironmentPluginLoaderUseCase environmentPluginLoaderUseCase;

    public EnvironmentPluginLoaderComponent(IServerNetworkAdapter serverNetworkAdapter) throws TechnicalException, SettingException, PluginNotReadableException {
        environmentPluginLoaderUseCase = new EnvironmentPluginLoaderUseCase(serverNetworkAdapter);
    }

    @Override
    public List<TEnvironmentDescription> listAvailableEnvironments() throws TechnicalException, PluginNotReadableException, SettingException {
        return environmentPluginLoaderUseCase.listAvailableEnvironments();
    }

    @Override
    public void loadEnvironmentPlugin(TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException {
         environmentPluginLoaderUseCase.loadEnvironmentPlugin(environment);
    }

    @Override
    public ClassLoader getUsedClassLoader() {
        return environmentPluginLoaderUseCase.getUsedClassLoader();
    }

    @Override
    public List<IEnvironmentConfiguration> getAvailableConfigurations() throws CorruptConfigurationFileException, TechnicalException {
        return environmentPluginLoaderUseCase.getAvailableConfigurations();
    }

    @Override
    public void saveConfiguration(IEnvironmentConfiguration environmentConfiguration) throws TechnicalException {
        environmentPluginLoaderUseCase.saveConfiguration(environmentConfiguration);
    }

    @Override
    public File getEnvironmentPluginPath(TEnvironmentDescription environmentDescription) throws TechnicalException, PluginNotReadableException {
        return environmentPluginLoaderUseCase.getEnvironmentPluginPath(environmentDescription);
    }

    @Override
    public IEnvironment createEnvironmentInstance(ICycleStatisticsSaver cycleStatisticsSaver) throws TechnicalException{
        return environmentPluginLoaderUseCase.createEnvironmentInstance(cycleStatisticsSaver);
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
