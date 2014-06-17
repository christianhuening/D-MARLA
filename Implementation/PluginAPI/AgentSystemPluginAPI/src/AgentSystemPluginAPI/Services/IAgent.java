package AgentSystemPluginAPI.Services;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import AgentSystemPluginAPI.Contract.StateAction;

/**
 * Interface for available implementations of learning algorithms provided by the system (for example q-learning, sarsa, sarsa-lambda ...).
 */
public interface IAgent {
    /**
     * Tells the agent that a new episode has started.
     * How the agent handles this information is up to the specific implementation of the agent.
     * @param state The start state of the agent.
     */
	public StateAction startEpisode(StateAction state) throws TechnicalException;

    /**
     * Returns the state, the agent is currently in.
     * @return null, if not yet in a state.
     */
    public StateAction getCurrentState();

    /**
     * Advances a running episode by one step.
     * @pre An episode must be started by calling startEpisode(). There must be a currently running episode.
     *      This means startEpisode() must have been called without a corresponding endEpisode().
     * @param rewardForLastStep The reward the agent has earned for the last action he made.
     * @param newState The current newState of the agent.
     * @return  Returns an action that is selected out of the possible actions.
     *          The selection of the appropriate action is done on the basis of the strategy the agent pursues.
     */
    public StateAction step(float rewardForLastStep, StateAction newState) throws TechnicalException;

    /**
     * Tells the agent that an episode has ended.
     * How the agent handles this information is up to the specific implementation of the agent.
     * @pre startEpisode() must have been called before.
     * @param reward The last reward, the agent earns, when the episode ends.
     * @param stateAction the last state that the agent is in. != null
     */
	public void endEpisode(StateAction stateAction, float reward) throws TechnicalException;

    /**
     * Sets the discount factor of this agent.
     * Note: only has an effect when SarsaLambda implementation is used.
     * @param newLambda new lambda > 0
     */
    public void setLambda(float newLambda);

    /**
     * Gets the Discount Factor of this agent (sarsa-lambda).
     */
	public float getLambda();


    /**
     * Sets Gamma
     * @param newGamma
     */
    public void setGamma(float newGamma);
    /**
     * TODO: What is gamma good for?
     * Gets the Discount Factor of this agent.
     */
	public float getGamma();


    /**
     * Sets the epsilon value of this agent.
     * @param newEpsilon
     */
    public void setEpsilon(float newEpsilon);

    /**
     * Gets the Exploration Rate of this agent.
     */
	public float getEpsilon();

    /**
     * Gets the learning rate of the agent.
     */
	public float getAlpha();

    /**
     * Sets the alpha value of this agent.
     * @param newAlpha
     */
    public void setAlpha(float newAlpha);

    /**
     * Gets the name of the agent.
     */
	public String getName();
}