package ServerRunner.Implementation;

import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import ZeroTypes.Enumerations.ClientEventType;
import ZeroTypes.Enumerations.SessionStatus;
import EnvironmentPluginAPI.Exceptions.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import ZeroTypes.Interfaces.IHasTransportType;
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
import ZeroTypes.TransportTypes.TClientEvent;
import ZeroTypes.TransportTypes.TNetworkClient;
import ZeroTypes.TransportTypes.TSession;

import java.util.*;

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

    private IEnvironment environment;

    private final IEnvironmentConfiguration configuration;
    private IEnvironmentPluginLoader environmentPluginLoader;

    private IServerNetworkAdapter serverNetworkAdapter;

    private IEnvironmentState currentEnvironmentState;

    private IActionDescription actionsInTurn;

    private TEnvironmentDescription environmentDescription;

    private ICycleStatisticsSaver gameStatistics;

// -------------------------- STATIC METHODS --------------------------

    static synchronized void addPlayerEventSubscriber(IPlayerEventHandler eventHandler) {
        playerEventSubscribers.add(eventHandler);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public Session(TSession session,
                   IServerNetworkAdapter serverNetworkAdapterInstance,
                   IEnvironmentPluginLoader environmentPluginLoader,
                   ICycleStatisticsSaver saveGameStatistics)
            throws TechnicalException, PluginNotReadableException {

        this(session.getName(),
                session.getNumberOfGames(),
                session.getEnvironmentDescription(),
                session.getConfiguration(),
                session.getClientsInThisSession(),
                serverNetworkAdapterInstance,
                environmentPluginLoader,
                saveGameStatistics);
    }

    public Session(String name,
                   int numberOfIterations,
                   TEnvironmentDescription environmentDescription,
                   IEnvironmentConfiguration configuration,
                   List<TNetworkClient> clientsInThisSession,
                   IServerNetworkAdapter serverNetworkAdapter,
                   IEnvironmentPluginLoader environmentPluginLoader,
                   ICycleStatisticsSaver gameStatistics)
            throws TechnicalException, PluginNotReadableException {

        super("Session");
        this.name = name;
        this.configuration = configuration;
        this.environmentPluginLoader = environmentPluginLoader;
        this.status = SessionStatus.READY;
        this.numberOfGames = numberOfIterations;
        this.clientsInThisSession = clientsInThisSession;
        this.serverNetworkAdapter = serverNetworkAdapter;
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

        for (TNetworkClient networkClient : clientsInThisSession) {
            clientsForPlayers.put(new TMARLAClientInstance(networkClient.getName(), networkClient.getId()), networkClient);
        }

        try {
            environment = environmentPluginLoader.createEnvironmentInstance(gameStatistics);
            environment.start(new ArrayList<TMARLAClientInstance>(clientsForPlayers.keySet()), configuration);
            Thread.currentThread().setContextClassLoader(environmentPluginLoader.getUsedClassLoader());

            currentEnvironmentState = environment.getCurrentEnvironmentState();


            for (TNetworkClient networkClient : clientsInThisSession) {
                serverNetworkAdapter.sendNetworkMessage(new SessionStartsMessage(networkClient.getId(), numberOfGames), MessageChannel.DATA);
            }

            while (numberOfPlayedGames < numberOfGames) {
                doOneCycle();
                numberOfPlayedGames += 1;
            }

            for (TNetworkClient networkClient : clientsInThisSession) {
                serverNetworkAdapter.sendNetworkMessage(new SessionEndsMessage(networkClient.getId()), MessageChannel.DATA);
            }

            if (!status.equals(SessionStatus.FAILED)) {
                this.status = SessionStatus.FINISHED;
                sendPlayerEventMessage(new TClientEvent(ClientEventType.SessionEnded, this.getTransportType()));
            } else {
                sendPlayerEventMessage(new TClientEvent(ClientEventType.SessionFailedWithError, this.getTransportType()));
            }

            sendPlayerEventMessage(new TClientEvent(ClientEventType.SessionStarted, getTransportType()));

        } catch (Exception e) {
            e.printStackTrace();
            status = SessionStatus.FAILED;
        }
    }

// -------------------------- PUBLIC METHODS --------------------------

    public void UpdateSession(TSession session) throws SessionIsNotInReadyStateException {
        if (!status.equals(SessionStatus.READY)) {
            throw new SessionIsNotInReadyStateException();
        }

        this.numberOfGames = session.getNumberOfGames();
        this.clientsInThisSession = session.getClientsInThisSession();
    }

    public List<TMARLAClientInstance> getPlayersInThisSession() {
        return new ArrayList<TMARLAClientInstance>(clientsForPlayers.keySet());
    }

// -------------------------- PRIVATE METHODS --------------------------

    private void doOneCycle() throws NotConnectedException, ConnectionLostException, InterruptedException, TechnicalException {
        try {
            currentEnvironmentState = environment.start(new ArrayList<TMARLAClientInstance>(clientsForPlayers.keySet()), configuration);
        } catch (IllegalNumberOfClientsException e) {
            e.printStackTrace();
            //TODO: Better exception handling, session should fail
        }

        for (TNetworkClient networkClient : clientsInThisSession) {
            clientsForPlayers.put(new TMARLAClientInstance(networkClient.getName(), networkClient.getId()), networkClient);

            serverNetworkAdapter.sendNetworkMessage(new CycleStartsMessage(networkClient.getId(), configuration), MessageChannel.DATA);
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
                currentEnvironmentState = environment.executeAction(actionsInTurn);

                if (environment.isStillActive()) {
                    sendCurrentEnvironmentStateToClient(clientsForPlayers.get(environment.getActiveInstance()));
                    this.wait();
                } else {
                    endCycle();
                    break;
                }
            }
        }
    }

    private void endCycle() throws NotConnectedException, ConnectionLostException, InterruptedException, TechnicalException {
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
        return new TSession(sessionId, name, status, configuration, clientsInThisSession.size(), numberOfGames, clientsInThisSession, environmentDescription);
    }
}
