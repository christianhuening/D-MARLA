package AgentSystemManagement.Services;

import AgentProvider.Interface.IAgentProvider;
import AgentSystemManagement.Interface.ISettingChangeEventListener;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import Exceptions.ErrorMessages;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import Settings.SettingException;

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
    private Properties agentSystemSettings;
    private List<ISettingChangeEventListener> settingsListeners;

    public AgentSystemServiceProvider(IAgentProvider provider, String agentJarPath, String agentSettingsPath) throws TechnicalException, SettingException {
        this.provider = provider;

        provider.loadAgentSystem(agentJarPath);

        //open agent system's settings. if no settings file is exists, create one. If even that fails, we can't do more :(
        agentSystemSettings = new Properties();
        try {
            agentSystemSettings.load(new FileInputStream(agentSettingsPath));
        } catch (IOException e) {

            try {
                File newProperties = new File(agentSettingsPath);
                newProperties.createNewFile();
            } catch (IOException ex) {
                throw new TechnicalException(ErrorMessages.get("unableToAccessAgentSettingFile") + agentSettingsPath);
            }
        }
    }

    public void registerForSettingChangeEvent(ISettingChangeEventListener listenerSetting) {
		settingsListeners.add(listenerSetting);
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