package AgentSystemManagement.Implementation;


import AgentProvider.Interface.IAgentProvider;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import AgentSystemManagement.Plugins.PluginManager;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import ZeroTypes.Settings.SettingException;

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