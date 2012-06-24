package AIRunner.Implementation;


import AIRunner.Interface.IAIRunner;
import AIRunner.Interface.IAIRunnerEventHandler;
import AIRunner.Interface.SessionRunningException;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

import java.security.InvalidParameterException;



public class AIRunnerComponent implements IAIRunner {

    private final IClientNetworkAdapter networkAdapter;
    private final IAgentSystemManagement agentSystemManagement;
    private final IAgentSystemPluginLoader agentSystemPluginLoader;
    private AIRunnerUseCase aiRunnerUseCase;

    public AIRunnerComponent(IClientNetworkAdapter networkAdapter, IAgentSystemManagement agentSystemManagement, IAgentSystemPluginLoader agentSystemPluginLoader){

        this.networkAdapter = networkAdapter;
        this.agentSystemManagement = agentSystemManagement;
        this.agentSystemPluginLoader = agentSystemPluginLoader;
        this.aiRunnerUseCase = new AIRunnerUseCase(networkAdapter, agentSystemManagement, agentSystemPluginLoader);

    }

    @Override
    public void connectToServer(TAgentSystemDescription agentSystemDescription, String hostname, int port) throws HostUnreachableException, InvalidParameterException, PluginNotReadableException, TechnicalException {
        aiRunnerUseCase.connectToServer(agentSystemDescription, hostname, port);
    }

    @Override
    public void addListener(IAIRunnerEventHandler networkEventHandler) {
        aiRunnerUseCase.addListener(networkEventHandler);
    }

    @Override
    public void disconnect() throws SessionRunningException {
        aiRunnerUseCase.disconnect();
    }
}
