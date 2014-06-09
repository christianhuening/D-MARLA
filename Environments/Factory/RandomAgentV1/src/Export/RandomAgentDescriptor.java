package Export;


import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import Factory.GameLogic.TransportTypes.TGameState;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class
 */
public class RandomAgentDescriptor implements IAgentSystemPluginDescriptor {

    private RandomAgentImplementation randomAgentImplementation;

    @Override
    public TAgentSystemDescription getDescription() {
        Set<TEnvironmentDescription> compatibleEnvironments = new HashSet<TEnvironmentDescription>();
        compatibleEnvironments.add(new TEnvironmentDescription("Factory", "v0.3", ""));
        return new TAgentSystemDescription("Random Agent", "v0.1", "An absolutely simple agent system. It chooses randomly what to do. Mainly for testing purposes.", compatibleEnvironments);
    }

    @Override
    public List<IAgent> getInternalAgents() {
        if(randomAgentImplementation != null) {
            return randomAgentImplementation.getInternalAgents();
        }
        throw new NotImplementedException();
    }

    @Override
    public IAgentSystem getInstance(IPluginServiceProvider iAgentSystemServiceProvider) {
        randomAgentImplementation = new RandomAgentImplementation();
        return randomAgentImplementation;
    }
}
