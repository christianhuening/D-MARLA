package AIClientFacade.Interface;

import AIClientFacade.Implementation.AIClientFacade;
import AIRunner.Implementation.AIRunnerComponent;
import AIRunner.Interface.IAIRunner;
import AgentProvider.Implementation.AgentProviderComponent;
import AgentSystemManagement.Interface.IAgentSystemManagement;
import AgentSystemManagement.Services.AgentSystemManagementComponent;
import NetworkAdapter.Implementation.ClientNetworkAdapterComponent;
import NetworkAdapter.Interface.IClientNetworkAdapter;
import PluginLoader.Implementation.PluginLoaderComponent;
import PluginLoader.Interface.IPluginLoader;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import AgentProvider.Interface.IAgentProvider;

import static org.picocontainer.Characteristics.CACHE;

public class AIClientApplicationCoreFactory {

    public static IAIClientFacade getProductionApplicationCore(){
        MutablePicoContainer container = new DefaultPicoContainer();

        container.addComponent(container);

        container.as(CACHE).addComponent(IAgentProvider.class, AgentProviderComponent.class);
        container.as(CACHE).addComponent(IAgentSystemManagement.class, AgentSystemManagementComponent.class);
        container.as(CACHE).addComponent(IClientNetworkAdapter.class, ClientNetworkAdapterComponent.class);
        container.as(CACHE).addComponent(IAIRunner.class, AIRunnerComponent.class);
        container.as(CACHE).addComponent(IAIClientFacade.class, AIClientFacade.class);
        container.addComponent(IPluginLoader.class, PluginLoaderComponent.class);

        IAIClientFacade facade = container.getComponent(IAIClientFacade.class);
        return facade;
    }



}
