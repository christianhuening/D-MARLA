package EnvironmentPluginAPI.Contract;

import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;

import java.io.Serializable;
import java.util.List;

/**
 *  Every environment hosted in MARLA must implement this interface. Environments provide the ability to react to actions of
 *  AgentSystems and change their states.
 *
 *  For every new cycle a new IEnvironment will be instantiated. The clients carry out their actions in a order determined
 *  by the IEnvironment.
 *  For every action a client carries out, the environment's state may change. The clients will carry out their actions, until
 *  the IEnvironment terminates the cycle. This will happen, if a predetermined goal or some other termination condition
 *  is met.
 *  <br/><br/>
 *  If the IEnvironment implements no termination-condition, the cycle will (theoretically) go on forever, unless terminated
 *  by the user.
 *  <br/><br/>
    The type parameters eliminate the need to cast in the plugin implementation, if used.
 * @param <A> The class that is used in the environment to describe an agent system's action.
 * @param <I> The class that is used in the environment to describe the current status of the environment.
 * @param <C> The class that is used in the environment to describe an environment configuration.
 */
public interface IEnvironment<C extends IEnvironmentConfiguration, I extends IEnvironmentState, A extends IActionDescription> {

    /**
     * Starts the environment with the given parameters.
     *
     * @param marlaClients a list of all marla networkClients that will take part in the environment session
     * @param environmentConfiguration (optional) data about the environments start configuration. May be null.
     * @return an object describing the environment's state when the game starts, != null
     * @throws TechnicalException if any technical errors occurred, that couldn't be handled
     */
    public IEnvironmentState start(List<TMARLAClientInstance> marlaClients, C environmentConfiguration) throws TechnicalException, IllegalNumberOfClientsException;

    /**
     * Denotes if the current environment is still active. If true, the MARLA system will use the value of
     * getActiveInstance() to ask that client for his next turn. If false, the MARLA system will stop letting the
     * clients make turns and call end().
     * @return see description
     */
    public boolean isStillActive();

    /**
     * Marks, which instance of a MARLA-Client was chosen to take the next turn.
     * <br/><br/>
     * The client, that is returned here, will receive this environment state. Its answer, in form of an
     * IActionDescription, will be the next input for the environment.
     *
     * @see EnvironmentPluginAPI.Contract.IActionDescription
     * @see EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance
     * @return must be part of this environment, != null
     */
    public TMARLAClientInstance getActiveInstance();

    /**
     * Gets the current state of the map for the running game.
     *
     * @return The current state of the game. != null
     * @see IEnvironmentState
     */
     I getCurrentEnvironmentState() throws TechnicalException;

    /**
     * Executes the action described by the actionDescription. The environment will change accordingly, and an updated
     * environment-state will be returned.
     * @param actionDescription an object describing the action(s) taken by the marla client
     * @return The state of the environment after the action has been executed, != null
     * @see IActionDescription
     */
    IEnvironmentState executeAction(A actionDescription) throws TechnicalException;

    /**
     * Called, when MARLA is about to end the current environment session. Can be used for cleanup and/or saving replays.
     */
    public void end() throws TechnicalException;
}
