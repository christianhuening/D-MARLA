package AIRunner.Implementation;

import AIRunner.Interface.AIRunnerEventType;
import AIRunner.Interface.IAIRunner;
import AIRunner.Interface.IAIRunnerEventHandler;
import AIRunner.Interface.SessionRunningException;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.CustomNetworkMessages.IEnvironmentStateMessage;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import NetworkAdapter.Interface.INetworkMessageReceivedEventHandler;
import NetworkAdapter.Interface.NetworkEventType;
import NetworkAdapter.Messages.CycleEndsMessage;
import NetworkAdapter.Messages.CycleStartsMessage;
import NetworkAdapter.Messages.SessionEndsMessage;
import NetworkAdapter.Messages.SessionStartsMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

/**
 * this class implements the logic for creating and managing sessions,
 * as well as informing listeners about network events.
 */
public class AIRunnerUseCase implements IAIRunner, IAIRunnerEventHandler, INetworkMessageReceivedEventHandler<NetworkMessage> {

    private final IClientNetworkAdapter networkAdapter;
    private final IAgentSystemPluginLoader agentSystemPluginLoader;
    private final IAgentSystemManagement agentSystemManagement;
    private PluginContainer pluginContainer = null;
    private List<IAIRunnerEventHandler> IAIRunnerEventHandlers = new LinkedList<IAIRunnerEventHandler>();
    private boolean sessionRunning;

    public AIRunnerUseCase(IClientNetworkAdapter networkAdapter,
                           IAgentSystemManagement agentSystemManagement,
                           IAgentSystemPluginLoader agentSystemPluginLoader) {

        this.networkAdapter = networkAdapter;
        this.agentSystemPluginLoader = agentSystemPluginLoader;
        networkAdapter.subscribeForNetworkMessageReceivedEvent(this, NetworkMessage.class);
        this.agentSystemManagement = agentSystemManagement;
    }

    @Override
    public void onAIRunnerEvent(AIRunnerEventType eventType) {
        for (IAIRunnerEventHandler eventHandler : IAIRunnerEventHandlers) {
            eventHandler.onAIRunnerEvent(eventType);
        }
    }

    @Override
    public void onSessionStart(int games) {
        for (IAIRunnerEventHandler eventHandler : IAIRunnerEventHandlers) {
            eventHandler.onSessionStart(games);
        }
    }

    @Override
    public void onException(Exception exception) {
        pluginContainer.interrupt();
        pluginContainer = null;
        System.err.println(exception);
    }

    @Override
    public void connectToServer(TAgentSystemDescription agentSystemDescription, String hostname, int port) throws HostUnreachableException, InvalidParameterException, PluginNotReadableException, TechnicalException {
        pluginContainer = new PluginContainer(agentSystemManagement, networkAdapter, this, agentSystemPluginLoader, hostname, port);
        pluginContainer.load(agentSystemDescription);
        onAIRunnerEvent(AIRunnerEventType.Connected);
    }

    @Override
    public void addListener(IAIRunnerEventHandler IAIRunnerEventHandler) {
        IAIRunnerEventHandlers.add(IAIRunnerEventHandler);
    }

    @Override
    public void disconnect() throws SessionRunningException {
        if (pluginContainer == null && !sessionRunning) {
            networkAdapter.disconnect();
            onAIRunnerEvent(AIRunnerEventType.Disconnected);
        }
    }

    @Override
    public void onMessageReceived(NetworkMessage message) {
        if (message instanceof SessionStartsMessage) {
            sessionRunning = true;
            onSessionStart(((SessionStartsMessage) message).getGamesToBePlayed());
        } else if (message instanceof SessionEndsMessage) {
            sessionRunning = false;
            onAIRunnerEvent(AIRunnerEventType.SessionEnded);
            System.err.println("informiere über session emde");
        } else if (message instanceof CycleStartsMessage) {
            pluginContainer.start(((CycleStartsMessage) message).getEnvironmentInitInfo());
        } else if (message instanceof IEnvironmentStateMessage) {
            pluginContainer.receiveGameState(((IEnvironmentStateMessage) message).getEnvironmentState());

        } else if (message instanceof CycleEndsMessage) {
            onAIRunnerEvent(AIRunnerEventType.CycleEnded);
            pluginContainer.end();
        }
    }

    @Override
    public void onNetworkEvent(NetworkEventType networkEventType, int clientID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}