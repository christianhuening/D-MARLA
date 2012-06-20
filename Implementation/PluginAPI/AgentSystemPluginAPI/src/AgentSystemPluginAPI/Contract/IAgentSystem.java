package AgentSystemPluginAPI.Contract;


import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import AgentSystemPluginAPI.Services.IAgent;

import java.util.List;

/**
 * This interface controls an AgentSystem. It provides methods to start and stop a game, and to get the next
 * GameState from the AgentSystem.
 * @param <E> Name of the custom implementation of IEnvironmentState that the environment uses. by providing this type information you can eliminate the need to cast
 * @param <A> Name of the custom implementation of IActionDescription that the environment uses. by providing this type information you can eliminate the need to cast
 */
public interface IAgentSystem<E extends IEnvironmentState, A extends IActionDescription> {

    /**
     * Tells the agent system, that a new game has been started, and what faction it controls in this game.
     * @param environmentInitInfo An arbitrary object, defined by the environment plugin that holding initialization info. nullable, if not needed
     *
     */
	public void start(Object environmentInitInfo) throws TechnicalException;

	/**
     * The agent system will choose it's actions based on the provided current state of the environment.
     * Its decisions will be returned as a new TGameState. This new game state represents the new situation, after the
     * agent has carried out all his actions.
     * @Pre startGame must have been called before
     * @Pre current != null
     * @param current The current state of the environment. The AgentSystem will base its decision on this information.
     * @return The new environment state, representing the situation of the environment, after the agent system carried out its action
	 */
	public A getActionsForEnvironmentStatus(E current) throws TechnicalException;

	/**
     * Called when session has ended. May be used for cleanup.
     * @pre start() must have been called earlier
     * */
	public void end() throws TechnicalException;

    /**
     * Gets a list of all internal agents of this agent system.
     * @return A list containing all agents that are used by this AgentSystem.
     */
	public List<IAgent> getInternalAgents();
}