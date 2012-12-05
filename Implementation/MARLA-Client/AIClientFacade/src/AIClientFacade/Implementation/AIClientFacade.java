package AIClientFacade.Implementation;

import AIClientFacade.Interface.IAIClientFacade;
import AIRunner.Interface.IAIRunner;
import AIRunner.Interface.IAIRunnerEventHandler;
import AIRunner.Interface.SessionRunningException;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.Exceptions.NotConnectedException;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import ZeroTypes.Settings.SettingException;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * This class implements the encapsulation of the AI Client's application core.
 */
public class AIClientFacade implements IAIClientFacade {

    private final IClientNetworkAdapter clientNetworkAdapter;
    private final IAIRunner aiRunner;
    private final IAgentSystemManagement agentSystemManagement;

    public AIClientFacade(IClientNetworkAdapter clientNetworkAdapter,
                          IAIRunner aiRunner,
                          IAgentSystemManagement agentSystemManagement) {
        this.clientNetworkAdapter = clientNetworkAdapter;
        this.aiRunner = aiRunner;
        this.agentSystemManagement = agentSystemManagement;
    }

    //region ClientNetworkAdapter
    @Override
    public boolean isConnected() {
        return clientNetworkAdapter.isConnected();
    }

    @Override
    public int getClientId() throws NotConnectedException {
        return clientNetworkAdapter.getClientId();
    }
    //endregion

    //region Description
    @Override
    public void connectToServer(TAgentSystemDescription agentSystemDescription, String hostname, int port) throws
            HostUnreachableException,
            InvalidParameterException,
            PluginNotReadableException,
            TechnicalException {
        aiRunner.connectToServer(agentSystemDescription, hostname, port);
    }

    @Override
    public void addListener(IAIRunnerEventHandler networkEventHandler) {
        aiRunner.addListener(networkEventHandler);
    }

    @Override
    public void disconnect() throws SessionRunningException {
        aiRunner.disconnect();
    }

    @Override
    public List<TAgentSystemDescription> getAvailableAgentSystems() throws TechnicalException, SettingException, PluginNotReadableException {
        return agentSystemManagement.getAvailableAgentSystems();
    }
    //endregion
}
