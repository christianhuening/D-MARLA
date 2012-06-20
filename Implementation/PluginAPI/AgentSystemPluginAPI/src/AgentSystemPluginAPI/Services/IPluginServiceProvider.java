package AgentSystemPluginAPI.Services;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;

/**
 * This interface shows all services, that the container provides for an AgentSystemPlugin.
 */
public interface IPluginServiceProvider {

    /**
     * Saves the value given under the key in the settings.properties next to this plugin file.
     *
     * If the key already exists, it will be overwritten.
     * @param key the key for which the value is saved
     * @param newValue the value to save.
     */
    public void saveAgentSystemSetting(String key, String newValue);

    /**
     * Returns the value for the specified key, saved in the settings.properties next to the plugin file.
     * @param key the key to look for
     * @return the saved value. null, if not present in the file.
     */
    public String getAgentSystemSetting(String key);

    /**
     * Returns a concrete implementation of a reinforcement learning agent. It's learning data will be persistent over
     * different program starts.
     *
     * If no agent with this name was used so far, it will be created automatically. If an agent with this name was used
     * before, the returned agent will use the learned data of previous runs.
     *
     * NOTICE: This is even true, if you pass a different agent type.
     * @param agentName The name of the agent.
     * @param learningAlgorithm
     * @return @see description, != null
     * @throws TechnicalException
     */
    public IAgent getTableAgent(String agentName, LearningAlgorithm learningAlgorithm, IStateActionGenerator stateActionGenerator) throws TechnicalException;

}
