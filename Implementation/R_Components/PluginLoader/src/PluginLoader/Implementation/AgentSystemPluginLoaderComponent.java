package PluginLoader.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IActionDescription;
import EnvironmentPluginAPI.CustomNetworkMessages.NetworkMessage;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import ZeroTypes.Settings.SettingException;

import java.io.File;
import java.util.List;

/**
 * This class represents the agent system plugin loader implementation.
 */
public class AgentSystemPluginLoaderComponent implements IAgentSystemPluginLoader {
    private AgentSystemPluginLoaderUseCase agentSystemPluginLoaderUseCase;

    public AgentSystemPluginLoaderComponent(IClientNetworkAdapter clientNetworkAdapter) throws TechnicalException, SettingException, PluginNotReadableException {
        agentSystemPluginLoaderUseCase = new AgentSystemPluginLoaderUseCase(clientNetworkAdapter);
    }

    @Override
    public List<TAgentSystemDescription> listAvailableAgentSystemPlugins() throws TechnicalException, PluginNotReadableException, SettingException {
        return agentSystemPluginLoaderUseCase.listAvailableAgentSystemPlugins();
    }

    @Override
    public void loadAgentSystemPlugin(TAgentSystemDescription agentSystem) throws TechnicalException, PluginNotReadableException {
        agentSystemPluginLoaderUseCase.loadAgentSystemPlugin(agentSystem);
    }

    @Override
    public ClassLoader getUsedClassLoader() {
        return agentSystemPluginLoaderUseCase.getUsedClassLoader();
    }

    @Override
    public IAgentSystem createAgentSystemInstance(IPluginServiceProvider serviceProvider) throws TechnicalException {
        return agentSystemPluginLoaderUseCase.createAgentSystemInstance(serviceProvider);
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
