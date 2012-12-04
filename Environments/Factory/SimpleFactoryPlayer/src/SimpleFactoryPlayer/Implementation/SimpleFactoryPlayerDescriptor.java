package SimpleFactoryPlayer.Implementation;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IAgentSystemPluginDescriptor;
import AgentSystemPluginAPI.Contract.TAgentSystemDescription;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 04.06.12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */
public class SimpleFactoryPlayerDescriptor implements IAgentSystemPluginDescriptor {
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IAIPluginDescriptor ---------------------

    @Override
    public TAgentSystemDescription getDescription() {
        Set<TEnvironmentDescription> enviros = new HashSet<TEnvironmentDescription>();
        enviros.add(new TEnvironmentDescription("Factory","v0.1",""));
        enviros.add(new TEnvironmentDescription("Factory","v0.2",""));
        enviros.add(new TEnvironmentDescription("Factory","v0.3",""));
        return new TAgentSystemDescription("SimpleFactoryPlayer","v001","A not so clever Agent",enviros);
    }

    @Override
    public IAgentSystem getInstance(IPluginServiceProvider iPluginServiceProvider) throws TechnicalException {
        return new SimpleFactoryPlayerSystem(iPluginServiceProvider);
    }
}
