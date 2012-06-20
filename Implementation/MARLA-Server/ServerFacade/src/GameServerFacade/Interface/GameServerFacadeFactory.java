package GameServerFacade.Interface;

import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import GameServerFacade.Implementation.CycleServerFacade;
import GameStatistics.Implementation.CycleStatisticsComponent;
import NetworkAdapter.Implementation.ServerNetworkAdapterComponent;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import PluginLoader.Implementation.PluginLoaderComponent;
import PluginLoader.Interface.IPluginLoader;
import RemoteInterface.ICycleStatistics;
import ServerRunner.Implementation.ServerRunnerComponent;
import ServerRunner.Interface.IServerRunner;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import static org.picocontainer.Characteristics.CACHE;

/**
 * Allows the retrieval of application core instances.
 */
public class GameServerFacadeFactory {

    private static ICycleServerFacade serverFacade = null;

    public static ICycleServerFacade getIntegrationTestApplicationCore() {

        if(serverFacade != null) {
            return serverFacade;
        }

        MutablePicoContainer container = new DefaultPicoContainer();

        container.addComponent(container);
        container.as(CACHE).addComponent(IPluginLoader.class, PluginLoaderComponent.class);
        container.as(CACHE).addComponent(ICycleStatistics.class, CycleStatisticsComponent.class);
        container.as(CACHE).addComponent(ISaveGameStatistics.class, CycleStatisticsComponent.class);
        container.as(CACHE).addComponent(IServerRunner.class, ServerRunnerComponent.class);
        container.as(CACHE).addComponent(IServerNetworkAdapter.class, ServerNetworkAdapterComponent.class);
        container.as(CACHE).addComponent(ICycleServerFacade.class, CycleServerFacade.class);

        ICycleServerFacade facade = container.getComponent(ICycleServerFacade.class);
        serverFacade = facade;

        return facade;
    }
}
