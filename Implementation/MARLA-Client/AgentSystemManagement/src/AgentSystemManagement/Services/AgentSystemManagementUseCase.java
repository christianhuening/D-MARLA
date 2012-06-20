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

public class AgentSystemManagementUseCase implements IAgentSystemManagement {
    private IAgentProvider agentProvider;
    private PluginManager pluginManager;
    private IPluginLoader pluginLoader;

    public AgentSystemManagementUseCase(PluginManager pluginManager, IAgentProvider agentProvider, IPluginLoader loader) {
        this.agentProvider = agentProvider;
        this.pluginManager = pluginManager;
        this.pluginLoader = loader;
    }

    @Override
    public List<TAgentSystemDescription> getAvailableAgentSystems() throws TechnicalException, SettingException, PluginNotReadableException {
        return pluginManager.getAvailablePlugins();
    }

    @Override
    public IAgentSystem getAgentSystem(TAgentSystemDescription toLoad) throws TechnicalException, PluginNotReadableException, SettingException {
        return pluginManager.getAgentSystemInstance(toLoad, new AgentSystemServiceProvider(agentProvider, pluginLoader.getAgentSystemPluginPath(toLoad).toString(), pluginLoader.getAgentSystemPluginPath(toLoad).toString() + "/settings.properties"));
    }
}