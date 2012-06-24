package PluginLoader.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This class represents the agent system plugin loader implementation.
 */
public class AgentSystemPluginLoaderComponent implements IAgentSystemPluginLoader {
    AgentSystemPluginLoaderUseCase agentSystemPluginLoaderUseCase;

    public AgentSystemPluginLoaderComponent() throws TechnicalException, SettingException, PluginNotReadableException {
        agentSystemPluginLoaderUseCase = new AgentSystemPluginLoaderUseCase();
    }

    @Override
    public List<TAgentSystemDescription> listAvailableAgentSystemPlugins() throws TechnicalException, PluginNotReadableException, SettingException {
        return agentSystemPluginLoaderUseCase.listAvailableAgentSystemPlugins();
    }

    @Override
    public IAgentSystemPluginDescriptor loadAgentSystemPlugin(TAgentSystemDescription agentSystem) throws TechnicalException, PluginNotReadableException {
        return agentSystemPluginLoaderUseCase.loadAgentSystemPlugin(agentSystem);
    }

    @Override
    public File getAgentSystemPluginPath(TAgentSystemDescription agentSystemDescription) throws TechnicalException, PluginNotReadableException {
        return agentSystemPluginLoaderUseCase.getAgentSystemPluginPath(agentSystemDescription);
    }

    @Override
    public NetworkMessage createActionDescriptionMessage(int clientId, IActionDescription actionDescription) {
        return agentSystemPluginLoaderUseCase.createActionDescriptionMessage(clientId, actionDescription);
    }
}
