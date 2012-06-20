package AgentProvider.Implementation;

import AgentProvider.Interface.IAgentProvider;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import Settings.SettingException;

import java.util.List;

public class AgentProviderComponent implements IAgentProvider {

    private AgentProviderUseCase agentProviderUseCase;

    public AgentProviderComponent() throws TechnicalException {
        agentProviderUseCase = new AgentProviderUseCase();
    }

    @Override
    public void loadAgentSystem(String pathToAgentSystem) throws TechnicalException, SettingException {
        agentProviderUseCase.loadAgentSystem(pathToAgentSystem);
    }

    @Override
    public List<String> getAgents() throws TechnicalException {
        return agentProviderUseCase.getAgents();
    }

    @Override
    public IAgent getTableAgent(String agentName, LearningAlgorithm learningAlgorithm, IStateActionGenerator stateActionGenerator) throws TechnicalException {
        return agentProviderUseCase.getTableAgent(agentName, learningAlgorithm, stateActionGenerator);
    }

    @Override
    public List<String> getAgentParameters(String agentName) throws TechnicalException {
        return agentProviderUseCase.getAgentParameters(agentName);
    }

    @Override
    public void setAgentParameter(String agentName, String key, float value) throws TechnicalException {
        agentProviderUseCase.setAgentParameter(agentName, key, value);
    }
}