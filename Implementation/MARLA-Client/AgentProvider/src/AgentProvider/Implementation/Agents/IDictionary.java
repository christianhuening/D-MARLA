package AgentProvider.Implementation.Agents;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.StateAction;

import java.util.Map;

/**
 * This interface defines how reinforcement learning agents are allowed to access their learning data.
 */
public interface IDictionary {

    /**
     * Returns the memorized float value for the given Key. If it is not known so far, return 0.0f.
     * @param key != null
     * @return see description
     * @throws TechnicalException if learning data could not be accessed because of technical difficulties
     */
	public float getValue(StateAction key) throws TechnicalException;

    /**
     * Memorizes the float value for the given Key. If it is not known yet, return 0.0f.
     * @param key != null
     * @param newValue the value to save.
     * @throws TechnicalException if learning data could not be saved because of technical difficulties
     */
	public void setValue(StateAction key, float newValue) throws TechnicalException;

    /**
     * Deletes all memorized learning data from the dictionary. All values will start again with 0.0f.
     * @throws TechnicalException
     */
    public void resetValues() throws TechnicalException;
}