package SimpleFactoryPlayer.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 04.06.12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public class SimpleFactoryPlayerDescriptor implements IAgentSystemPluginDescriptor {


    private SimpleFactoryPlayerVolume2System simpleFactoryPlayerVolume2System;

    @Override
    public TAgentSystemDescription getDescription() {
        Set<TEnvironmentDescription> enviros = new HashSet<TEnvironmentDescription>();
        enviros.add(new TEnvironmentDescription("Factory","v0.1",""));
        enviros.add(new TEnvironmentDescription("Factory","v0.2",""));
        enviros.add(new TEnvironmentDescription("Factory","v0.3",""));
        return new TAgentSystemDescription("SimpleFactoryPlayerVolume2","v200","QLearning",enviros);
    }

    @Override
    public List<IAgent> getInternalAgents() {
        if(simpleFactoryPlayerVolume2System != null){
            return simpleFactoryPlayerVolume2System.getInternalAgents();
        }
        throw new NotImplementedException();
    }

    @Override
    public IAgentSystem getInstance(IPluginServiceProvider iPluginServiceProvider) throws TechnicalException {
        simpleFactoryPlayerVolume2System = new SimpleFactoryPlayerVolume2System(iPluginServiceProvider);
        return simpleFactoryPlayerVolume2System;
    }
}
