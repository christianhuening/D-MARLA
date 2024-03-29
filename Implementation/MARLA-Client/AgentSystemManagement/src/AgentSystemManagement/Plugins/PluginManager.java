package AgentSystemManagement.Plugins;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import ZeroTypes.Settings.SettingException;

import java.util.List;

public class PluginManager {

    private final IAgentSystemPluginLoader agentSystemPluginLoader;

    public PluginManager(IAgentSystemPluginLoader agentSystemPluginLoader) throws SettingException {
        this.agentSystemPluginLoader = agentSystemPluginLoader;
    }

    public List<TAgentSystemDescription> getAvailablePlugins() throws TechnicalException, SettingException, PluginNotReadableException {
        return agentSystemPluginLoader.listAvailableAgentSystemPlugins();
    }

    public IAgentSystem getAgentSystemInstance(TAgentSystemDescription system, IPluginServiceProvider provider) throws TechnicalException, PluginNotReadableException, SettingException {
        agentSystemPluginLoader.loadAgentSystemPlugin(system);
        return agentSystemPluginLoader.createAgentSystemInstance(provider);
    }
}