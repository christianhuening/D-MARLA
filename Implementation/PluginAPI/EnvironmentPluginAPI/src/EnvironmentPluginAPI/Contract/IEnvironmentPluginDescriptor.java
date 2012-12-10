package EnvironmentPluginAPI.Contract;


import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

import java.util.List;

/**
 *  Plugins must implement this interface in order to be recognized by MARLA.
 *  <br/><br/>
 *  MARLA uses this interface to identify and load environment plugins. It expects a correctly working, no-argument constructor to
 *  create an instance of the plugin descriptor. It uses it for those 2 purposes:<br/>
 *  - retrieving information about the plugin, i.e for displaying in the GUI<br/>
 *  - obtaining instances of the environment for use in cycles.
 *  @param <C> The class that is used int the environment to describe an environment configuration.
 */
public interface IEnvironmentPluginDescriptor<C extends IEnvironmentConfiguration> {

    /**
     *  The MARLA system uses this method to obtain the environment plugin's description. May be called repeatedly.
     *
     * @return != null
     */
    public TEnvironmentDescription getDescription();

    /**
     *  Returns a list of all saved maps.
     *
     * @return empty, if no maps found
     * @throws EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException if a map file, that was being tried to read, was somehow corrupted
     * @throws TechnicalException if any technical error's occurred, that couldn't be handled
     */
    public List<C> getAvailableConfigurations() throws CorruptConfigurationFileException, TechnicalException;

    /**
     *  Saves a configuration. If it already exists, it will be overwritten. Doesn't necessarily have to be implemented.
     *
     * @param environmentConfiguration the configuration to save != null
     */
    public void saveConfiguration(C environmentConfiguration) throws TechnicalException;

    /**
     *  The MARLA system uses this method to obtain the plugin's environment logic implementation.<br/>
     *  It passes an instance of a cycleStatisticsSaver, which is never null. Environments may or may not use it to
     *  store replays of cycles.<br/>
     *  May be called repeatedly.
     *
     * @param cycleStatisticsSaver != null
     * @return != null
     */
    public IEnvironment getInstance(ICycleStatisticsSaver cycleStatisticsSaver) throws TechnicalException;
}
