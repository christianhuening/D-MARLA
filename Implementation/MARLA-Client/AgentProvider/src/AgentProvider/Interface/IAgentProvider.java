package AgentProvider.Interface;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import Settings.SettingException;

import java.util.List;

/**
 *
 */
public interface IAgentProvider {

    /**
     * Sets the current agentSystem to the given one. Make sure this method is calls before any others of this interface.
     * All calls to other will be executed to the lastly loaded agentSystem.
     *
     * NOTICE: If no system was chosen, calling other methods of this interface will result in undefined behaviour!
     * @param pathToAgentSystem the file path to the agentsystem to load
     * @throws TechnicalException if a severe, technical error occurs
     * @throws SettingException if the application settings aren't accessible or corrupt
     */
    public void loadAgentSystem(String pathToAgentSystem) throws TechnicalException, SettingException;

    /**
     * Returns a list of all persisted agents for the loaded agent system.
     *
     * @pre loadAgentSystem() was called before!
     * @return empty, if none found or agentSystem unknown
     * @throws TechnicalException if a severe, technical error occurs
     */
    public List<String> getAgents() throws TechnicalException;

    /**
     * Gets an agent that stores its learning results in a table like structure (this may be a file, a database, or something else).
     * The name of the agent must be unique!
     *
     * @pre loadAgentSystem() was called before!
     * @pre If an agent with the given name already exists, the provided learningAlgorithm must match the type of this agent.
     * @param agentName the agent's name. != null. also unique within agent system
     * @param learningAlgorithm The learning algorithm of the agent.
     * @param stateActionGenerator an object that is able to generate all possible actions for a given state != null
     * @return An agent with the specified characteristics will be returned.
     */
	public IAgent getTableAgent(String agentName, LearningAlgorithm learningAlgorithm, IStateActionGenerator stateActionGenerator) throws TechnicalException;

    /**
     * Returns the names of all learning parameters used by the given agent.
     *
     * @pre loadAgentSystem() was called before!
     * @param agentName the name of the agent
     * @return empty, if none found or agentSystem unknown
     * @throws TechnicalException if a severe, technical error occurs
     */
    public List<String> getAgentParameters(String agentName) throws TechnicalException;

    /**
     * Configures a value for a specific agent.
     *
     * @pre loadAgentSystem() was called before!
     * @param agentName The name of the agent, the value will be set for.
     * @param key The key of the value that will be set.
     * @param value The value of the key that will be saved for the agent.
     */
	public void setAgentParameter(String agentName, String key, float value) throws TechnicalException;
}