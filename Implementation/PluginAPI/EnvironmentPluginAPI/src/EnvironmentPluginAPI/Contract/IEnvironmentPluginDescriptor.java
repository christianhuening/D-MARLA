package EnvironmentPluginAPI.Contract;


import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

/**
 *  Plugins must implement this interface in order to be recognized by MARLA.
 *  <br/><br/>
 *  MARLA uses this interface to identify and load environment plugins. It expects a correctly working, no-argument constructor to
 *  create an instance of the plugin descriptor. It uses it for those 2 purposes:<br/>
 *  - retrieving information about the plugin, i.e for displaying in the GUI<br/>
 *  - obtaining instances of the environment for use in cycles.
 */
public interface IEnvironmentPluginDescriptor {
    /**
     * The MARLA system uses this method to obtain the environment plugin's description. May be called repeatedly.
     * @return != null
     */
    public TEnvironmentDescription getDescription();

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
