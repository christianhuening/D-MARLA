import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 28.11.12
 * Time: 21:17
 * To change this template use File | Settings | File Templates.
 */
public class CliffQLearningAgentDescriptor implements IAgentSystemPluginDescriptor {
    @Override
    public TAgentSystemDescription getDescription() {
        Set<TEnvironmentDescription> compatibleEnvironments = new HashSet<TEnvironmentDescription>();
        compatibleEnvironments.add(new TEnvironmentDescription("The Cliff", "v0.01", ""));
        return new TAgentSystemDescription("Cliff QLearning Agent", "v0.01", "The QLearning implementation of a simple agent that moves within a gridworld.", compatibleEnvironments);
    }

    @Override
    public List<IAgent> getInternalAgents() {
        List<IAgent> result = new ArrayList<IAgent>();
        return result;
    }

    @Override
    public IAgentSystem getInstance(IPluginServiceProvider pluginServiceProvider) throws TechnicalException {
        return new CliffQLearningAgentSystem(pluginServiceProvider);
    }
}
