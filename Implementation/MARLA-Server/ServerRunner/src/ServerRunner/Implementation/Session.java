package ServerRunner.Implementation;

import Enumeration.ClientEventType;
import Enumeration.SessionStatus;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Interfaces.IHasTransportType;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import NetworkAdapter.Interface.MessageChannel;
import NetworkAdapter.Messages.CycleEndsMessage;
import NetworkAdapter.Messages.CycleStartsMessage;
import NetworkAdapter.Messages.SessionEndsMessage;
import NetworkAdapter.Messages.SessionStartsMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IEnvironmentPluginLoader;
import ServerRunner.Interface.IPlayerEventHandler;
import ServerRunner.Interface.SessionIsNotInReadyStateException;
import TransportTypes.TClientEvent;
import TransportTypes.TNetworkClient;
import TransportTypes.TSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class manages a session of n clients that are contained in an own environment.
 */
class Session extends Thread implements IHasTransportType<TSession> {
// ------------------------------ FIELDS ------------------------------

    private static List<IPlayerEventHandler> playerEventSubscribers = new ArrayList<IPlayerEventHandler>();

    private UUID sessionId;

    public UUID getSessionId() {
        return sessionId;
    }

    private int numberOfPlayedGames = 0;

    private String name;

    private SessionStatus status;

    public SessionStatus getStatus() {
        return status;
    }

    private int numberOfGames;

    private List<TNetworkClient> clientsInThisSession;

    public List<TNetworkClient> getClientsInThisSession() {
        return clientsInThisSession;
    }

    private HashMap<TMARLAClientInstance, TNetworkClient> clientsForPlayers;

    private TMapMetaData mapMetaData;

    private IEnvironment environment;

    private IEnvironmentPluginLoader environmentPluginLoader;

    private IServerNetworkAdapter serverNetworkAdapter;

    private IEnvironmentState currentEnvironmentState;

    private IActionDescription actionsInTurn;

    private TEnvironmentDescription environmentDescription;

    private ISaveGameStatistics gameStatistics;

// -------------------------- STATIC METHODS --------------------------

    static synchronized void addPlayerEventSubscriber(IPlayerEventHandler eventHandler) {
        playerEventSubscribers.add(eventHandler);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public Session(TSession session, IServerNetworkAdapter serverNetworkAdapterInstance, IEnvironmentPluginLoader environmentPluginLoader, ISaveGameStatistics saveGameStatistics) throws TechnicalException, PluginNotReadableException {
        this(session.getName(), session.getNumberOfGames(), session.getClientsInThisSession(), session.getMapMetaData(), serverNetworkAdapterInstance, environmentPluginLoader, session.getEnvironmentDescription(), saveGameStatistics);
    }

    public Session(String name, int numberOfIterations, List<TNetworkClient> clientsInThisSession, TMapMetaData mapMetaData, IServerNetworkAdapter serverNetworkAdapter, IEnvironmentPluginLoader environmentPluginLoader, TEnvironmentDescription environmentDescription, ISaveGameStatistics gameStatistics) throws TechnicalException, PluginNotReadableException {
        super("Session");
        this.name = name;
        this.environmentPluginLoader = environmentPluginLoader;
        this.status = SessionStatus.READY;
        this.numberOfGames = numberOfIterations;
        this.clientsInThisSession = clientsInThisSession;
        this.serverNetworkAdapter = serverNetworkAdapter;
        this.mapMetaData = mapMetaData;
        this.environmentDescription = environmentDescription;
        this.gameStatistics = gameStatistics;

        clientsForPlayers = new HashMap<TMARLAClientInstance, TNetworkClient>();

        sessionId = UUID.randomUUID();
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setActionsInTurn(IActionDescription actionsInTurn) {
        this.actionsInTurn = actionsInTurn;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Runnable ---------------------

    public void run() {

        this.status = SessionStatus.RUNNING;

        try {
            environment = environmentPluginLoader.createEnvironmentInstance(gameStatistics);
        } catch (TechnicalException e) {
            //TODO: Better exception handling, session should fail
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        sendPlayerEventMessage(new TClientEvent(ClientEventType.SessionStarted, getTransportType()));

        for (TNetworkClient networkClient : clientsInThisSession) {
            try {
                serverNetworkAdapter.sendNetworkMessage(new SessionStartsMessage(networkClient.getId(), numberOfGames), MessageChannel.DATA);
            } catch (NotConnectedException e) {
                status = SessionStatus.FAILED;
                e.printStackTrace();
                return;
            } catch (ConnectionLostException e) {
                status = SessionStatus.FAILED;
                e.printStackTrace();
                return;
            }
        }

        while (numberOfPlayedGames < numberOfGames) {
            try {
                executeGame();
            } catch (NotConnectedException e) {
                e.printStackTrace();
                status = SessionStatus.FAILED;
                break;
            } catch (ConnectionLostException e) {
                e.printStackTrace();
                status = SessionStatus.FAILED;
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                status = SessionStatus.FAILED;
                break;
            } catch (TechnicalException e) {
                e.printStackTrace();
                status = SessionStatus.FAILED;
                break;
            } catch (Exception e) {
                e.printStackTrace();
                status = SessionStatus.FAILED;
                break;
            }
            numberOfPlayedGames += 1;
        }

        for (TNetworkClient networkClient : clientsInThisSession) {
            try {
                serverNetworkAdapter.sendNetworkMessage(new SessionEndsMessage(networkClient.getId()), MessageChannel.DATA);
            } catch (NotConnectedException e) {
                e.printStackTrace();
                status = SessionStatus.FAILED;
            } catch (ConnectionLostException e) {
                e.printStackTrace();
                status = SessionStatus.FAILED;
            }
        }

        if (!status.equals(SessionStatus.FAILED)) {
            this.status = SessionStatus.FINISHED;
            sendPlayerEventMessage(new TClientEvent(ClientEventType.SessionEnded, this.getTransportType()));
        } else {
            sendPlayerEventMessage(new TClientEvent(ClientEventType.SessionFailedWithError, this.getTransportType()));
        }
    }

// -------------------------- PUBLIC METHODS --------------------------

    public void UpdateSession(TSession session) throws SessionIsNotInReadyStateException {
        if (!status.equals(SessionStatus.READY)) {
            throw new SessionIsNotInReadyStateException();
        }

        this.numberOfGames = session.getNumberOfGames();
        this.clientsInThisSession = session.getClientsInThisSession();
        this.mapMetaData = session.getMapMetaData();
    }

    public List<TMARLAClientInstance> getPlayersInThisSession() {
        return new ArrayList<TMARLAClientInstance>(clientsForPlayers.keySet());
    }

// -------------------------- PRIVATE METHODS --------------------------

    private void executeGame() throws NotConnectedException, ConnectionLostException, InterruptedException, TechnicalException {
        for (TNetworkClient networkClient : clientsInThisSession) {
            clientsForPlayers.put(new TMARLAClientInstance(networkClient.getName(), networkClient.getId()), networkClient);

            serverNetworkAdapter.sendNetworkMessage(new CycleStartsMessage(networkClient.getId(), null), MessageChannel.DATA);
        }

        try {
            currentEnvironmentState = environment.start(new ArrayList<TMARLAClientInstance>(clientsForPlayers.keySet()), mapMetaData);
        } catch (EnvironmentPluginAPI.Contract.Exception.IllegalNumberOfClientsException e) {
            e.printStackTrace();
            //TODO: Better exception handling, session should fail
        }

        sendCurrentEnvironmentStateToClient(clientsForPlayers.get(environment.getActiveInstance()));
        synchronized (this) {
            // TODO: A timeout mechanic would be nice, if the clients do not respond.
            wait();
        }

        advanceTurns();

        sendPlayerEventMessage(new TClientEvent(ClientEventType.CycleStarted, this.getTransportType()));
    }

    private void sendCurrentEnvironmentStateToClient(TNetworkClient networkClient) throws NotConnectedException, ConnectionLostException {
        serverNetworkAdapter.sendNetworkMessage(environmentPluginLoader.createEnvironmentStateMessage(networkClient.getId(), currentEnvironmentState), MessageChannel.DATA);
    }

    private void advanceTurns() throws NotConnectedException, ConnectionLostException, InterruptedException, TechnicalException {

        synchronized (this) {
            while (status.equals(SessionStatus.RUNNING)) {
                System.err.println("Zugestellt in der Session: " + actionsInTurn.getClass().getClassLoader());
                currentEnvironmentState = environment.executeAction(actionsInTurn);

                if (environment.isStillActive()) {
                    sendCurrentEnvironmentStateToClient(clientsForPlayers.get(environment.getActiveInstance()));
                    this.wait();
                } else {
                    endGame();
                    break;
                }
            }
        }
    }

    private void endGame() throws NotConnectedException, ConnectionLostException, InterruptedException, TechnicalException {
        environment.end();

        for (TMARLAClientInstance player : clientsForPlayers.keySet()) {
            CycleEndsMessage message;

            if (environment.getActiveInstance().equals(player)) {
                message = new CycleEndsMessage(true, clientsForPlayers.get(player).getId());
            } else {
                message = new CycleEndsMessage(false, clientsForPlayers.get(player).getId());
            }

            serverNetworkAdapter.sendNetworkMessage(message, MessageChannel.DATA);
        }

        sendPlayerEventMessage(new TClientEvent(ClientEventType.CycleEnded, this.getTransportType()));
    }

    static synchronized void sendPlayerEventMessage(TClientEvent event) {
        for (IPlayerEventHandler eventHandler : playerEventSubscribers) {
            eventHandler.call(event);
        }
    }

    @Override
    public TSession getTransportType() {
        return new TSession(sessionId, name, status, clientsInThisSession.size(), numberOfGames, clientsInThisSession, mapMetaData, environmentDescription);
    }
}
