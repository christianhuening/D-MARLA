package AgentSystemPluginAPI.Contract;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;

/**
 *  Plugins must implement this interface in order to be recognized by MARLA.
 *  <br/><br/>
 *  MARLA uses this interface to identify and load agent system plugins. It expects a correctly working, no-argument constructor to
 *  create an instance of the plugin descriptor. It uses it for those 2 purposes:<br/>
 *  - retrieving information about the plugin, i.e for displaying in the GUI<br/>
 *  - obtaining instances of the agent system for use in cycles.
 */
public interface IAgentSystemPluginDescriptor {

    /**
     * The MARLA system uses this method to obtain the environment plugin's description. May be called repeatedly.
     * @return != null
     */
    public TAgentSystemDescription getDescription();

    /**
     *  The MARLA system uses this method to obtain the plugin's agent system implementation.<br/>
     *  The returned instance will be used for an environment session. It passes an instance of a plugin service provider, which is never null. The agent system may use it to
     *  load/save agents and settings and to access information.
     * @see IPluginServiceProvider
     * @param serviceProvider != null
     * @return != null
     */
    public IAgentSystem getInstance(IPluginServiceProvider serviceProvider) throws TechnicalException;
}
