package AgentSystemPluginAPI.Contract;


import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import AgentSystemPluginAPI.Services.IAgent;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

import java.util.List;

/**
 *  This interface controls an AgentSystem. It provides methods to start and stop a game, and to get the next
 *  GameState from the AgentSystem.
 * @param <C> The custom implementation of IEnvironmentConfiguration that the environment uses.
 * @param <E> The custom implementation of IEnvironmentState that the environment uses.
 * @param <A> The custom implementation of IActionDescription that the environment uses.
 */
public interface IAgentSystem<C extends IEnvironmentConfiguration, E extends IEnvironmentState, A extends IActionDescription> {

    /**
     *  Tells the agent system, that a new game has been started, and what faction it controls in this game.
     * @param environmentConfiguration An arbitrary object, defined by the environment plugin that holding initialization info. nullable, if not needed     *
     */
	public void start(C environmentConfiguration) throws TechnicalException;

	/**
     *  The agent system will choose it's actions based on the provided current state of the environment.
     *  Its decisions will be returned as a new TGameState. This new game state represents the new situation, after the
     *  agent has carried out all his actions.
     * @Pre startGame must have been called previously
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

}