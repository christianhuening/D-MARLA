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

public class AgentSystemManagementUseCase implements IAgentSystemManagement {
    private IAgentProvider agentProvider;
    private PluginManager pluginManager;
    private IAgentSystemPluginLoader agentSystemPluginLoader;

    public AgentSystemManagementUseCase(PluginManager pluginManager, IAgentProvider agentProvider, IAgentSystemPluginLoader agentSystemPluginLoader) {
        this.agentProvider = agentProvider;
        this.pluginManager = pluginManager;
        this.agentSystemPluginLoader = agentSystemPluginLoader;
    }

    @Override
    public List<TAgentSystemDescription> getAvailableAgentSystems() throws TechnicalException, SettingException, PluginNotReadableException {
        return pluginManager.getAvailablePlugins();
    }

    @Override
    public IAgentSystem getAgentSystem(TAgentSystemDescription toLoad) throws TechnicalException, PluginNotReadableException, SettingException {
        return pluginManager.getAgentSystemInstance(toLoad, new AgentSystemServiceProvider(agentProvider, agentSystemPluginLoader.getAgentSystemPluginPath(toLoad), toLoad));
    }
}