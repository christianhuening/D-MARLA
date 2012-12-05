package AgentSystemManagement.Implementation;

import AgentProvider.Interface.IAgentProvider;
import AgentSystemManagement.Interface.ISettingChangeEventListener;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Exceptions.ErrorMessages;
import ZeroTypes.Settings.SettingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * This class implements the offering of services offered to an AI Plugin
 */
public class AgentSystemServiceProvider implements IPluginServiceProvider {

    private IAgentProvider provider;
    private final File agentDirectory;
    private final TAgentSystemDescription toLoad;
    private Properties agentSystemSettings;
    private List<ISettingChangeEventListener> settingsListeners;

    public AgentSystemServiceProvider(IAgentProvider provider, File agentDirectory, TAgentSystemDescription toLoad) throws TechnicalException, SettingException {
        this.provider = provider;
        this.agentDirectory = agentDirectory;
        this.toLoad = toLoad;

        provider.loadAgentSystem(agentDirectory.getPath() + "/" + toLoad.getName() + ".jar");

        //open agent system's settings. if no settings file is exists, create one. If even that fails, we can't do more :(
        String settingsPath = agentDirectory.toString() + "/settings.properties";
        agentSystemSettings = new Properties();
        try {
            agentSystemSettings.load(new FileInputStream(settingsPath));
        } catch (IOException e) {

            try {
                File newProperties = new File(settingsPath);
                newProperties.createNewFile();
            } catch (IOException ex) {
                throw new TechnicalException(ErrorMessages.get("unableToAccessAgentSettingFile") + settingsPath);
            }
        }
    }

    public void registerForSettingChangeEvent(ISettingChangeEventListener listenerSetting) {
		settingsListeners.add(listenerSetting);
	}

    /**
     * Returns the absolute path of the directory where the current agent system plugin is located in.
     *
     * @return != null
     */
    @Override
    public File agentDirectory() {
        return agentDirectory;
    }

    @Override
    public void saveAgentSystemSetting(String key, String newValue) {
        agentSystemSettings.put(key, newValue);
        informListenersAboutSettingChangeEvent(key, newValue);
    }

    @Override
    public String getAgentSystemSetting(String key) {
        return agentSystemSettings.getProperty(key);
    }

    private void informListenersAboutSettingChangeEvent(String key, String value) {
        for(ISettingChangeEventListener listener : settingsListeners) {
            listener.onSettingChanged(key, value);
        }
    }

    public IAgent getTableAgent(String agentName, LearningAlgorithm learningAlgorithm, IStateActionGenerator stateActionGenerator) throws TechnicalException {
         return provider.getTableAgent(agentName, learningAlgorithm, stateActionGenerator);
    }
}