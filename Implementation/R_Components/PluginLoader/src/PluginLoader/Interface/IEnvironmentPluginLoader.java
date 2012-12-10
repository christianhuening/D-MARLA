package PluginLoader.Interface;

import EnvironmentPluginAPI.Contract.*;
import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Service.AbstractVisualizeReplayPanel;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import EnvironmentPluginAPI.Service.IVisualizeReplay;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This component implements methods for inspecting and loading environment plugins at runtime. It also helps
 * finding and creating the different object types used by MARLA that may reside in an environment plugin.
 */
public interface IEnvironmentPluginLoader {

    /**
     *  Searches recursively for environment plugins in the given directory.
     *
     * @return empty if no environment plugins were found
     * @throws TechnicalException if technical errors prevent the component from loading the plugin described
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     * @throws SettingException @throws SettingException if the environmentPluginsfolder is not correctly set int he app's settings.
     */
    public List<TEnvironmentDescription> listAvailableEnvironments() throws TechnicalException, PluginNotReadableException, SettingException;

    /**
     *  Loads the specified environment plugin an returns an instance of it.
     *
     * @pre listAvailableEnvironments must have been called successfully previously
     * @param environment the environment plugin to load
     * @throws TechnicalException if technical errors prevent the component from loading the plugin specified
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     */
    public void loadEnvironmentPlugin(TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException;

    /**
     *  Returns the class loader that was used to load the current environment plugin. Necessary to set this class
     *  loader as context classloader in every thread. This prevents the plugin's classes from being loaded again by
     *  another class loader, thus causing ClassCastExceptions.
     *
     * @return null, if no plugin currently loaded
     */
    public ClassLoader getUsedClassLoader();

    /**
     *  Returns a list of all saved maps.
     *
     * @pre loadEnvironmentPlugin must have been called successfully previously
     * @return empty, if no maps found
     * @throws EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException if a map file, that was being tried to read, was somehow corrupted
     * @throws TechnicalException if any technical error's occurred, that couldn't be handled
     */
    public List<IEnvironmentConfiguration> getAvailableConfigurations() throws CorruptConfigurationFileException, TechnicalException;

    /**
     *  Saves a configuration. If it already exists, it will be overwritten. Doesn't necessarily have to be implemented.
     *
     * @param environmentConfiguration the configuration to save != null
     */
    public void saveConfiguration(IEnvironmentConfiguration environmentConfiguration) throws TechnicalException;

    /**
     *  Returns the file handle for the directory, where the plugin jar is located at.
     *
     * @param environmentDescription a description of an existing environment != null
     * @return null, if environment was not found
     * @throws TechnicalException if technical errors prevent the component from loading the plugin specified
     * @throws PluginNotReadableException if the plugin is not readable, for example if no TEnvironmentDescription is provided
     */
    public File getEnvironmentPluginPath(TEnvironmentDescription environmentDescription) throws TechnicalException, PluginNotReadableException;

    /**
     *  Returns a new instance of the loaded environment.
     *
     * @throws UnsupportedOperationException if no environment plugin was loaded previously.
     * @return != null
     * @param cycleStatisticsSaver != null
     */
    public IEnvironment createEnvironmentInstance(ICycleStatisticsSaver cycleStatisticsSaver) throws TechnicalException;

    /**
     *  Creates an environment state message. If the environment provides a custom implementation, it will be used.
     *  Otherwise a default message is used.
     *
     * @pre Environment must be loaded!
     * @param clientId the client id of the client targeted to, != null
     * @param environmentState the environment state to send
     * @return not null
     */
    public NetworkMessage createEnvironmentStateMessage(int clientId, IEnvironmentState environmentState);

    /**
     *  Loads the implementation of IVisualizeReplay from the pre-loaded Environment.
     *
     * @pre Environment must be loaded!
     * @return not null
     */
    public IVisualizeReplay getReplayVisualization();

    /**
     *  Loads the Swing compatible implementation of IVisualizeReplay from the pre-loaded Environment.
     *
     * @pre Environment must be loaded!
     * @return not null
     */
    public AbstractVisualizeReplayPanel getReplayVisualizationForSwing();

}
