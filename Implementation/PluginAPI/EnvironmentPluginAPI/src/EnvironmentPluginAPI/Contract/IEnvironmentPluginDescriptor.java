package EnvironmentPluginAPI.Contract;


import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;

/**
 * Plugins must implement this interface to be used in MARLA.
 *
 * The interface provides general informations about the plugin. It also offers the ability to obtain instances of this
 * specific plugin.
 */
public interface IEnvironmentPluginDescriptor {
    /**
     * The MARLA system uses this method to obtain the environment plugin's description.
     * @return not null
     */
    public TEnvironmentDescription getDescription();

    /**
     * The MARLA system uses this method to obtain the environment plugin's implementation.<br/>
     * It will be used for one session of the environment.
     * @param gameStatisticSaver the
     * @return
     */
    public IEnvironment getInstance(ISaveGameStatistics gameStatisticSaver) throws TechnicalException;
}
