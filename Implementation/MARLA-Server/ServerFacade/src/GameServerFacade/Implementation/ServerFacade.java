package GameServerFacade.Implementation;

import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import ZeroTypes.Exceptions.GameReplayNotContainedInDatabaseException;
import GameServerFacade.Interface.IServerFacade;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IEnvironmentPluginLoader;
import ZeroTypes.RemoteInterface.ICycleStatistics;
import ServerRunner.Interface.IPlayerEventHandler;
import ServerRunner.Interface.IServerRunner;
import ServerRunner.Interface.SessionIsNotInReadyStateException;
import ZeroTypes.Settings.SettingException;
import ZeroTypes.TransportTypes.TCycleReplayDescription;
import ZeroTypes.TransportTypes.TNetworkClient;
import ZeroTypes.TransportTypes.TSession;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class ServerFacade implements IServerFacade {

    private ICycleStatistics cycleStatistics;
    private ICycleStatisticsSaver saveGameStatistics;
    private IServerRunner serverRunner;
    private IServerNetworkAdapter serverNetworkAdapter;
    private IEnvironmentPluginLoader environmentPluginLoader;

    public ServerFacade(ICycleStatistics cycleStatistics, ICycleStatisticsSaver saveGameStatistics,
                        IServerRunner serverRunner, IServerNetworkAdapter networkAdapter, IEnvironmentPluginLoader environmentPluginLoader) {

        this.cycleStatistics = cycleStatistics;
        this.saveGameStatistics = saveGameStatistics;
        this.serverRunner = serverRunner;
        this.serverNetworkAdapter = networkAdapter;
        this.environmentPluginLoader = environmentPluginLoader;
    }

    @Override
    public ICycleReplay getCycleReplay(UUID gameID, TEnvironmentDescription environmentDescription) throws GameReplayNotContainedInDatabaseException, RemoteException, TechnicalException {
        return cycleStatistics.getCycleReplay(gameID, environmentDescription);
    }

    @Override
    public float getWinLoseRatio(String player, String opponent, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getWinLoseRatio(player, opponent, environment);
    }

    @Override
    public List<TCycleReplayDescription> getCycleReplayDescriptionsByDeltaTime(Date startingTime, Date endingTime, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getCycleReplayDescriptionsByDeltaTime(startingTime, endingTime, environment);
    }

    @Override
    public float getCurrentGamesPerMinute(TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getCurrentGamesPerMinute(environment);
    }

    @Override
    public List<String> getClientNames(TEnvironmentDescription environment) throws RemoteException {
        return cycleStatistics.getClientNames(environment);
    }

    @Override
    public int getTotalNumberOfCycles(String playerName, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getTotalNumberOfCycles(playerName, environment);
    }

    @Override
    public int getTotalNumberOfCyclesWon(String playerName, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getTotalNumberOfCyclesLost(playerName, environment);
    }

    @Override
    public int getTotalNumberOfCyclesLost(String playerName, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getTotalNumberOfCyclesLost(playerName, environment);
    }

    @Override
    public float getAverageTurnsPerCycle(String playerName, int numberOfLastGames, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getAverageTurnsPerCycle(playerName, numberOfLastGames, environment);
    }

    @Override
    public List<TCycleReplayDescription> getDescriptionsForNLastCycles(int numberOfGames, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return cycleStatistics.getDescriptionsForNLastCycles(numberOfGames, environment);
    }

    @Override
    public UUID createSession(TSession session) throws TechnicalException, PluginNotReadableException {
        return serverRunner.createSession(session);
    }

    @Override
    public void updateSession(UUID id, TSession session) throws SessionIsNotInReadyStateException {
        serverRunner.updateSession(id, session);
    }

    @Override
    public TSession getSessionById(UUID id) {
        return serverRunner.getSessionById(id);
    }

    @Override
    public List<TSession> getAllSessions() {
        return serverRunner.getAllSessions();
    }

    @Override
    public void startAllReadySessions() {
        serverRunner.startAllReadySessions();
    }

    @Override
    public List<TNetworkClient> getFreeClients() {
        return serverRunner.getFreeClients();
    }

    @Override
    public void subscribeForPlayerEvent(IPlayerEventHandler playerEventHandler) {
        serverRunner.subscribeForPlayerEvent(playerEventHandler);
    }

    @Override
    public List<TMARLAClientInstance> getConnectedPlayers() {
        return serverRunner.getConnectedPlayers();
    }

    @Override
    public void startHosting() throws TechnicalException, ConnectionLostException {
        serverNetworkAdapter.startHosting();
    }

    @Override
    public void stopHosting() {
        serverNetworkAdapter.stopHosting();
    }

    @Override
    public IEnvironmentPluginDescriptor loadEnvironmentPlugin(TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException {
        return environmentPluginLoader.loadEnvironmentPlugin(environment);
    }

    @Override
    public List<TEnvironmentDescription> listAvailableEnvironments() throws TechnicalException, PluginNotReadableException, SettingException {
        return environmentPluginLoader.listAvailableEnvironments();
    }

    @Override
    public void saveConfiguration(IEnvironmentConfiguration configuration, TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException {
        environmentPluginLoader.loadEnvironmentPlugin(environment).getInstance(saveGameStatistics).saveConfiguration(configuration);
    }

    @Override
    public List<IEnvironmentConfiguration> getAvailableConfigurations(TEnvironmentDescription environment) throws CorruptConfigurationFileException, TechnicalException, PluginNotReadableException {
        return  environmentPluginLoader.loadEnvironmentPlugin(environment).getInstance(saveGameStatistics).getAvailableConfigurations();
    }

    @Override
    public void SaveReplay(ICycleReplay replay, TEnvironmentDescription environment) throws TechnicalException {
        saveGameStatistics.SaveReplay(replay, environment);
    }
}
