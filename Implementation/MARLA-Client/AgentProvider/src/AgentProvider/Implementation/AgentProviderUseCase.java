package AgentProvider.Implementation;

import AgentProvider.Implementation.Agents.*;
import AgentProvider.Implementation.Database.AgentSettingsAccessor;
import AgentProvider.Implementation.Database.PersistenceFactory;
import AgentProvider.Interface.IAgentProvider;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Settings.SettingException;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

class AgentProviderUseCase implements IAgentProvider, IAgentSettingUpdatedListener {

    private PersistenceFactory persistenceFactory;
    private Map<String, AgentSettingsAccessor> settings;
    private String agentSystemPath;

    public AgentProviderUseCase() {
        settings = new Hashtable<String, AgentSettingsAccessor>();
    }

    @Override
    public void loadAgentSystem(String pathToAgentSystem) throws TechnicalException, SettingException {
        this.agentSystemPath = pathToAgentSystem;
        this.persistenceFactory = new PersistenceFactory(pathToAgentSystem);
    }

    @Override
    public List<String> getAgents() throws TechnicalException {
        return persistenceFactory.getAgents();
    }

    @Override
    public List<String> getAgentParameters(String agentName) throws TechnicalException {
        if(!settings.containsKey(agentName)) {
            settings.put(agentName, persistenceFactory.getAgentSettingsAccessor(agentName));
        }

        return settings.get(agentName).getAgentParameterKeys();
    }

    @Override
    public IAgent getTableAgent(String agentName, LearningAlgorithm learningAlgorithm, IStateActionGenerator stateActionGenerator) throws TechnicalException {
        IAgent agent = null;

        switch(learningAlgorithm) {
            case QLearning:
                agent = new QLearningAgent(agentName, persistenceFactory.getDictionary(agentName, "QValues", PersistenceType.Table), stateActionGenerator, this);
                break;
            case SARSA:
                agent = new SarsaAgent(agentName, persistenceFactory.getDictionary(agentName, "QValues", PersistenceType.Table), stateActionGenerator, this);
            break;
            case SARSALambda:
                agent = new SarsaLambdaAgent(agentName, persistenceFactory.getDictionary(agentName, "QValues", PersistenceType.Table),
                                                        persistenceFactory.getDictionary(agentName, "EValues", PersistenceType.Table), stateActionGenerator, this);
            break;
        }

        return agent;
    }

    @Override
    public void setAgentParameter(String agentName, String key, float value) throws TechnicalException {
        if(!settings.containsKey(agentName)) {
            settings.put(agentName, persistenceFactory.getAgentSettingsAccessor(agentName));
        }

        settings.get(agentName).setValue(key, value);
    }

    @Override
    public void onAgentSettingUpdated(IAgent agent, AgentSettingName agentSettingName, float value) {

    }
}