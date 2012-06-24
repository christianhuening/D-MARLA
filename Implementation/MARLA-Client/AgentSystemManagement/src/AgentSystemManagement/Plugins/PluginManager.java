package AgentSystemManagement.Plugins;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IAgentSystemPluginLoader;
import Settings.AppSettings;
import Settings.SettingException;

import java.util.List;
import java.util.regex.Pattern;

public class PluginManager {

    private Pattern agentPathPattern;
    private String agentSystemPluginDirectory;
    private final IAgentSystemPluginLoader agentSystemPluginLoader;

    public PluginManager(IAgentSystemPluginLoader agentSystemPluginLoader) throws SettingException {
        this.agentSystemPluginLoader = agentSystemPluginLoader;
        agentSystemPluginDirectory = AppSettings.getString("agentSystemPluginDirectory");
        if (agentSystemPluginDirectory.endsWith("/") || agentSystemPluginDirectory.endsWith("\\")) {
            agentSystemPluginDirectory = agentSystemPluginDirectory.substring(0, agentSystemPluginDirectory.length()-1);
        }

        //try to parse agent sub directory from path
        agentPathPattern = Pattern.compile(".*?[/\\\\]+" + agentSystemPluginDirectory + "[/\\\\]+(.*?[\\w ]+)[/\\\\]+[\\w ]+\\.jar");
    }

    public List<TAgentSystemDescription> getAvailablePlugins() throws TechnicalException, SettingException, PluginNotReadableException {

        return agentSystemPluginLoader.listAvailableAgentSystemPlugins();
    }

    public IAgentSystem getAgentSystemInstance(TAgentSystemDescription system, IPluginServiceProvider provider) throws TechnicalException, PluginNotReadableException, SettingException {
        return agentSystemPluginLoader.loadAgentSystemPlugin(system).getInstance(provider);
    }
}