package AgentSystemManagement.Services;


import AgentProvider.Interface.IAgentProvider;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import AgentSystemManagement.Plugins.PluginManager;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import Settings.SettingException;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

import java.util.List;

public class AgentSystemManagementComponent implements IAgentSystemManagement {

    private PluginManager pluginManager;
    private AgentSystemManagementUseCase agentSystemManagementUseCase;
    private final IAgentSystemPluginLoader agentSystemPluginLoader;

    public AgentSystemManagementComponent(IAgentProvider agentProvider, IAgentSystemPluginLoader agentSystemPluginLoader) throws TechnicalException, SettingException {
        this.agentSystemPluginLoader = agentSystemPluginLoader;
        pluginManager = new PluginManager(agentSystemPluginLoader);
        this.agentSystemManagementUseCase = new AgentSystemManagementUseCase(pluginManager, agentProvider, agentSystemPluginLoader);
    }

    public List<TAgentSystemDescription> getAvailableAgentSystems() throws TechnicalException, SettingException, PluginNotReadableException {
		return agentSystemManagementUseCase.getAvailableAgentSystems();
	}

	public IAgentSystem getAgentSystem(TAgentSystemDescription toLoad) throws TechnicalException, PluginNotReadableException, SettingException {
        return agentSystemManagementUseCase.getAgentSystem(toLoad);
	}
}