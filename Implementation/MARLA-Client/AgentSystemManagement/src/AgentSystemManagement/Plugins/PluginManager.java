package AgentSystemManagement.Plugins;

import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IPluginLoader;
import Settings.AppSettings;
import Settings.SettingException;

import java.util.List;
import java.util.regex.Pattern;

public class PluginManager {

    private Pattern agentPathPattern;
    private String agentSystemPluginDirectory;
    private final IPluginLoader pluginLoader;

    public PluginManager(IPluginLoader pluginLoader) throws SettingException {
        this.pluginLoader = pluginLoader;
        agentSystemPluginDirectory = AppSettings.getString("agentSystemPluginDirectory");
        if (agentSystemPluginDirectory.endsWith("/") || agentSystemPluginDirectory.endsWith("\\")) {
            agentSystemPluginDirectory = agentSystemPluginDirectory.substring(0, agentSystemPluginDirectory.length()-1);
        }

        //try to parse agent sub directory from path
        agentPathPattern = Pattern.compile(".*?[/\\\\]+" + agentSystemPluginDirectory + "[/\\\\]+(.*?[\\w ]+)[/\\\\]+[\\w ]+\\.jar");
    }

    public List<TAgentSystemDescription> getAvailablePlugins() throws TechnicalException, SettingException, PluginNotReadableException {

        return pluginLoader.listAvailableAgentSystemPlugins(AppSettings.getString("agentSystemPluginDirectory"));
    }

    public IAgentSystem getAgentSystemInstance(TAgentSystemDescription system, IPluginServiceProvider provider) throws TechnicalException, PluginNotReadableException, SettingException {
        return pluginLoader.loadAgentSystemPlugin(system).getInstance(provider);
    }
}