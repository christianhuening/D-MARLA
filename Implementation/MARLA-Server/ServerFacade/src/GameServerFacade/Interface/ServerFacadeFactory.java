package GameServerFacade.Interface;

import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import GameServerFacade.Implementation.ServerFacade;
import GameStatistics.Implementation.CycleStatisticsComponent;
import NetworkAdapter.Implementation.ServerNetworkAdapterComponent;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import PluginLoader.Implementation.EnvironmentPluginLoaderComponent;
import PluginLoader.Interface.IEnvironmentPluginLoader;
import ZeroTypes.RemoteInterface.ICycleStatistics;
import ServerRunner.Implementation.ServerRunnerComponent;
import ServerRunner.Interface.IServerRunner;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import static org.picocontainer.Characteristics.CACHE;

/**
 * Allows the retrieval of application core instances.
 */
public class ServerFacadeFactory {

    private static IServerFacade serverFacade = null;

    public static IServerFacade getProductiveApplicationCore() {

        if(serverFacade != null) {
            return serverFacade;
        }

        MutablePicoContainer container = new DefaultPicoContainer();

        container.addComponent(container);
        container.as(CACHE).addComponent(IEnvironmentPluginLoader.class, EnvironmentPluginLoaderComponent.class);
        container.as(CACHE).addComponent(ICycleStatistics.class, CycleStatisticsComponent.class);
        container.as(CACHE).addComponent(ICycleStatisticsSaver.class, CycleStatisticsComponent.class);
        container.as(CACHE).addComponent(IServerRunner.class, ServerRunnerComponent.class);
        container.as(CACHE).addComponent(IServerNetworkAdapter.class, ServerNetworkAdapterComponent.class);
        container.as(CACHE).addComponent(IServerFacade.class, ServerFacade.class);

        IServerFacade facade = container.getComponent(IServerFacade.class);
        serverFacade = facade;

        return facade;
    }
}
