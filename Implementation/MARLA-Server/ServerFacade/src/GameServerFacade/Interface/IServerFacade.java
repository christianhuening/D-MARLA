package GameServerFacade.Interface;

import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.RemoteInterface.ICycleStatistics;
import ServerRunner.Interface.IServerRunner;
import ZeroTypes.Settings.SettingException;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 13.05.12
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public interface IServerFacade extends
        ICycleStatistics,
        ICycleStatisticsSaver,
        IServerRunner {

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
     * Searches recursively for environment plugins in the given directory.
     *
     * @return empty if no environment plugins were found
     * @throws TechnicalException if technical errors prevent the component from loading the plugin described
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     * @throws ZeroTypes.Settings.SettingException @throws SettingException if the environmentPluginsfolder is not correctly set int he app's settings.
     */
    public List<TEnvironmentDescription> listAvailableEnvironments() throws TechnicalException, PluginNotReadableException, SettingException;

    /**
     * Saves the given map to a file in the maps directory.
     * if a map with that name already exists, it will be overwritten.
     * @param configuration the configuration to save
     * @throws EnvironmentPluginAPI.Exceptions.TechnicalException
     */
    public void saveConfiguration(IEnvironmentConfiguration configuration, TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException;

    /**
     * Gets all available maps from the maps directory.
     * @return empty, if no maps found.
     */
    public List<IEnvironmentConfiguration> getAvailableConfigurations(TEnvironmentDescription environment) throws CorruptConfigurationFileException, TechnicalException, PluginNotReadableException;
}
