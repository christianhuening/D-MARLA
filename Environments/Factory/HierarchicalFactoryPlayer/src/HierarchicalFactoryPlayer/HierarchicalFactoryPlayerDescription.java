package HierarchicalFactoryPlayer;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HierarchicalFactoryPlayerDescription implements IAgentSystemPluginDescriptor {

   private HierarchicalFactoryPlayerSystem hierarchicalFactoryPlayerSystem;

    @Override
    public TAgentSystemDescription getDescription() {
        Set<TEnvironmentDescription> enviros = new HashSet<TEnvironmentDescription>();
        enviros.add(new TEnvironmentDescription("Factory","v0.1",""));
        enviros.add(new TEnvironmentDescription("Factory","v0.2",""));
        enviros.add(new TEnvironmentDescription("Factory","v0.3",""));
        return new TAgentSystemDescription("HierarchicalFactoryPlayer","v0.0.1","Hierarchical Evaluator/Mover agent", enviros);
    }

    @Override
    public List<IAgent> getInternalAgents() {
        if(hierarchicalFactoryPlayerSystem != null){
            return hierarchicalFactoryPlayerSystem.getInternalAgents();
        }
        return null;
    }

    @Override
    public IAgentSystem getInstance(IPluginServiceProvider serviceProvider) throws TechnicalException {
        hierarchicalFactoryPlayerSystem = new HierarchicalFactoryPlayerSystem(serviceProvider);
        return hierarchicalFactoryPlayerSystem;
    }
}
