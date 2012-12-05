package ServerRunner.Implementation;

import ZeroTypes.Enumerations.ClientEventType;
import ZeroTypes.Enumerations.SessionStatus;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.IActionDescriptionMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import NetworkAdapter.Interface.NetworkEventType;
import NetworkAdapter.Messages.ClientJoinMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IEnvironmentPluginLoader;
import ServerRunner.Interface.IPlayerEventHandler;
import ServerRunner.Interface.IServerRunner;
import ServerRunner.Interface.SessionIsNotInReadyStateException;
import ZeroTypes.TransportTypes.TClientEvent;
import ZeroTypes.TransportTypes.TNetworkClient;
import ZeroTypes.TransportTypes.TSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 21.05.12
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public class ServerRunnerUseCase implements IServerRunner, INetworkMessageReceivedEventHandler {
    private final IServerNetworkAdapter networkAdapter;
    private final ICycleStatisticsSaver saveGameStatistics;
    private final IEnvironmentPluginLoader environmentPluginLoader;

    private List<TNetworkClient> networkClients;

    private List<Session> sessions;

    public ServerRunnerUseCase(ICycleStatisticsSaver gameStatistics, IServerNetworkAdapter networkAdapter, IEnvironmentPluginLoader environmentPluginLoader) {
        this.networkAdapter = networkAdapter;
        this.saveGameStatistics = gameStatistics;
        this.environmentPluginLoader = environmentPluginLoader;

        networkClients = new ArrayList<TNetworkClient>();
        sessions = new ArrayList<Session>();

        getServerNetworkAdapterInstance().subscribeForNetworkMessageReceivedEvent(this, IActionDescriptionMessage.class);
        getServerNetworkAdapterInstance().subscribeForNetworkMessageReceivedEvent(this, ClientJoinMessage.class);
    }

    @Override
    public void onMessageReceived(NetworkMessage message) {


        if (message instanceof IActionDescriptionMessage) {
            for (Session s : sessions) {
                for (TNetworkClient networkClient : s.getClientsInThisSession()) {
                    if (networkClient.getId() == message.getClientId()) {
                        s.setActionsInTurn(((IActionDescriptionMessage) message).getAction());
                        synchronized (s) {
                            s.notify();
                        }
                        break;
                    }
                }
            }
        } else if (message instanceof ClientJoinMessage) {

            ClientJoinMessage clientJoinMessage = (ClientJoinMessage) message;

            networkClients.add(new TNetworkClient(clientJoinMessage.getClientId(), clientJoinMessage.getAgentName(), clientJoinMessage.getAddress(), Calendar.getInstance().getTime()));

            Session.sendPlayerEventMessage(new TClientEvent(ClientEventType.ClientJoined, null));
        }
    }

    @Override
    public void onNetworkEvent(NetworkEventType networkEventType, int clientID) {
        if (networkEventType == NetworkEventType.Disconnected || networkEventType == NetworkEventType.ConnectionLost) {
            Session.sendPlayerEventMessage(new TClientEvent(ClientEventType.ClientDisconnected, null));
        }
    }

    @Override
    public void startHosting() throws TechnicalException, ConnectionLostException {
        getServerNetworkAdapterInstance().startHosting();
    }

    @Override
    public void stopHosting() {
        getServerNetworkAdapterInstance().stopHosting();
    }

    @Override
    public UUID createSession(TSession session) throws TechnicalException, PluginNotReadableException {

        Session newSession = new Session(session, getServerNetworkAdapterInstance(), environmentPluginLoader, saveGameStatistics);

        sessions.add(newSession);

        return newSession.getSessionId();
    }

    @Override
    public void updateSession(UUID id, TSession session) throws SessionIsNotInReadyStateException {
        for (Session s : sessions) {
            if (s.getSessionId() == id) {
                s.UpdateSession(session);
                break;
            }
        }
    }

    @Override
    public TSession getSessionById(UUID id) {
        for (Session s : sessions) {
            if (s.getSessionId() == id) {
                return s.getTransportType();
            }
        }

        return null;
    }

    @Override
    public List<TSession> getAllSessions() {
        List<TSession> returnedSessions = new ArrayList<TSession>();

        for (Session s : sessions) {
            returnedSessions.add(s.getTransportType());
        }

        return returnedSessions;
    }

    @Override
    public void startAllReadySessions() {
        for (Session s : sessions) {
            if (s.getStatus().equals(SessionStatus.READY)) {
                s.start();
            }
        }
    }

    @Override
    public List<TNetworkClient> getFreeClients() {
        List<TNetworkClient> freeNetworkClients = new ArrayList<TNetworkClient>(getServerNetworkAdapterInstance().getConnectedClients());

        for (Session s : sessions) {
            for (TNetworkClient networkClient : s.getClientsInThisSession()) {
                if (freeNetworkClients.contains(networkClient)) {
                    freeNetworkClients.remove(networkClient);
                }
            }
        }

        return freeNetworkClients;
    }

    @Override
    public void subscribeForPlayerEvent(IPlayerEventHandler playerEventHandler) {
        Session.addPlayerEventSubscriber(playerEventHandler);
    }

    @Override
    public List<TMARLAClientInstance> getConnectedPlayers() {
        List<TMARLAClientInstance> players = new ArrayList<TMARLAClientInstance>();

        for (Session s : sessions) {
            players.addAll(s.getPlayersInThisSession());
        }

        return players;
    }

    private IServerNetworkAdapter getServerNetworkAdapterInstance() {
        return networkAdapter;
    }
}
