package ServerRunner.Interface;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import TransportTypes.TNetworkClient;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import TransportTypes.TSession;

import java.util.List;
import java.util.UUID;

public interface IServerRunner {

    /**
     * Starts hosting the GameServer services over the network.
     *
     * @throws EnvironmentPluginAPI.Contract.Exception.TechnicalException
     * @throws ConnectionLostException
     */
    public void startHosting() throws TechnicalException, ConnectionLostException;

    /**
     * Stops hosting the GameServer services over the network.
     */
    public void stopHosting();

    /**
     * Creates a new Session with the given MapConfiguration
     *
     * @return The id of the internal session.
     */
    public UUID createSession(TSession session) throws TechnicalException, PluginNotReadableException;

    /**
     * Updates the session with the given identifier. Please note that you can only update sessions that are in the ready
     * state. Trying to update a session that is not in the ready state will throw an exception.
     *
     * @param id      The unique identifier of the session that should be updated.
     * @param session The session with the provided id will be updated with the data from this session.
     * @throws SessionIsNotInReadyStateException
     *          - Will be thrown, when trying to update a session that is in any other
     *          state than Ready.
     */
    public void updateSession(UUID id, TSession session) throws SessionIsNotInReadyStateException;

    /**
     * Returns a session with a specific id.
     *
     * @param id The id of the session that should be retrieved.
     * @return The session with the given id. Null if there is no session with this id.
     */
    public TSession getSessionById(UUID id);

    /**
     * Returns a list of all sessions.
     *
     * @return All sessions registered in this ServerRunner.
     */
    public List<TSession> getAllSessions();

    /**
     * Starts all available sessions that are not played at the moment.
     */
    public void startAllReadySessions();

    public List<TNetworkClient> getFreeClients();

    // TODO: This method is not implemented yet, because the network adapter doesn't provide matching methods to subscribe for these events.
    /**
     * Subscribes for player related network events.
     *
     * @param playerEventHandler A reference to the object, on which the appropriate method will be called on an event.
     */
    public void subscribeForPlayerEvent(IPlayerEventHandler playerEventHandler);

    /**
     * Gets a list of players containing all players that are currently connected to the server.
     *
     * @return All currently connected players.
     */
    public List<TMARLAClientInstance> getConnectedPlayers();
}