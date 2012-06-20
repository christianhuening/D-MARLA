package AIRunner.Implementation;


import AIRunner.Interface.IAIRunner;
import AIRunner.Interface.IAIRunnerEventHandler;
import AIRunner.Interface.SessionRunningException;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import NetworkAdapter.Interface.Exceptions.HostUnreachableException;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IPluginLoader;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

import java.security.InvalidParameterException;



public class AIRunnerComponent implements IAIRunner {

    private final IClientNetworkAdapter networkAdapter;
    private final IAgentSystemManagement agentSystemManagement;
    private final IPluginLoader pluginLoader;
    private AIRunnerUseCase aiRunnerUseCase;

    public AIRunnerComponent(IClientNetworkAdapter networkAdapter, IAgentSystemManagement agentSystemManagement, IPluginLoader pluginLoader){

        this.networkAdapter = networkAdapter;
        this.agentSystemManagement = agentSystemManagement;
        this.pluginLoader = pluginLoader;
        this.aiRunnerUseCase = new AIRunnerUseCase(networkAdapter, agentSystemManagement, pluginLoader);

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
