package AgentSystemPluginAPI.Services;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;

import java.io.File;

/**
 *  This interface exposes services, that the MARLA system provides for agent system plugins.
 *  <br/><br/>
 *  (Currently) there are means for<br/>
 *  - reading and saving settings from the automatically generated agent settings file, i.e learning parameters<br/>
 *  TODO: Maybe do the parsing in the service provider?
 *  - getting the path were the plugin is located.
 *  - retrieving temporal difference learning implementations<br/>
 */
public interface IPluginServiceProvider {

    /**
     *  Returns the file that represents the directory where the current agent system plugin is located in.
     * @return != null
     */
    public File agentDirectory();

    /**
     *  Saves the value given under the key in the settings.properties next to this plugin file.
     *  <br/><br/>
     *  If the key already exists, it's value will be overwritten.
     * @param key the key for which the value is saved != null
     * @param newValue the value to save.
     */
    public void saveAgentSystemSetting(String key, String newValue);

    /**
     *  Returns the value for the specified key, saved in the settings.properties next to the plugin file.
     * @param key the key to look for != null
     * @return the saved value. null, if not present in the file.
     */
    public String getAgentSystemSetting(String key);

    /**
     *  Returns an implementation of a reinforcement learning agent. It's learning data will be persistent over
     *  different program starts.
     *  <br/><br/>
     *  If no agent with this name was used so far, it will be created automatically. If an agent with this name was used
     *  before, the returned agent will use the learned data of previous runs.
     *
     *  NOTICE: This is even true, if you pass a different agent type.
     * @param agentName The name of the agent.
     * @param learningAlgorithm The implementation of the learning algorithm
     * @return @see description, != null
     * @throws TechnicalException
     */
    public IAgent getTableAgent(String agentName, LearningAlgorithm learningAlgorithm, IStateActionGenerator stateActionGenerator) throws TechnicalException;

}
