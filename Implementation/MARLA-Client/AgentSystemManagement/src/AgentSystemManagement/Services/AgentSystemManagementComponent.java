package AgentSystemManagement.Services;


import AgentProvider.Interface.IAgentProvider;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import AgentSystemManagement.Plugins.PluginManager;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IPluginLoader;
import Settings.SettingException;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;

import java.util.List;

public class AgentSystemManagementComponent implements IAgentSystemManagement {

    private PluginManager pluginManager;
    private AgentSystemManagementUseCase agentSystemManagementUseCase;
    private final IPluginLoader pluginLoader;

    public AgentSystemManagementComponent(IAgentProvider agentProvider, IPluginLoader pluginLoader) throws TechnicalException, SettingException {
        this.pluginLoader = pluginLoader;
        pluginManager = new PluginManager(pluginLoader);
        this.agentSystemManagementUseCase = new AgentSystemManagementUseCase(pluginManager, agentProvider, pluginLoader);
    }

    public List<TAgentSystemDescription> getAvailableAgentSystems() throws TechnicalException, SettingException, PluginNotReadableException {
		return agentSystemManagementUseCase.getAvailableAgentSystems();
	}

	public IAgentSystem getAgentSystem(TAgentSystemDescription toLoad) throws TechnicalException, PluginNotReadableException, SettingException {
        return agentSystemManagementUseCase.getAgentSystem(toLoad);
	}
}