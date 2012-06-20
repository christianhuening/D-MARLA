package GameServerFacade.Implementation;

import EnvironmentPluginAPI.Contract.Exception.CorruptMapFileException;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.IEnvironmentPluginDescriptor;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.ISaveGameStatistics;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;
import Exceptions.GameReplayNotContainedInDatabaseException;
import GameServerFacade.Interface.ICycleServerFacade;
import NetworkAdapter.Interface.Exceptions.ConnectionLostException;
import NetworkAdapter.Interface.IServerNetworkAdapter;
import PluginLoader.Interface.Exceptions.PluginNotReadableException;
import PluginLoader.Interface.IPluginLoader;
import RemoteInterface.ICycleStatistics;
import ServerRunner.Interface.IPlayerEventHandler;
import ServerRunner.Interface.IServerRunner;
import ServerRunner.Interface.SessionIsNotInReadyStateException;
import Settings.AppSettings;
import Settings.SettingException;
import TransportTypes.TCycleReplayDescription;
import TransportTypes.TNetworkClient;
import TransportTypes.TSession;
import org.joda.time.DateTime;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class CycleServerFacade implements ICycleServerFacade {

    ICycleStatistics cycleStatistics;
    ISaveGameStatistics saveGameStatistics;
    IServerRunner serverRunner;
    IServerNetworkAdapter serverNetworkAdapter;
    IPluginLoader pluginLoader;

    public CycleServerFacade(ICycleStatistics cycleStatistics, ISaveGameStatistics saveGameStatistics,
                             IServerRunner serverRunner, IServerNetworkAdapter networkAdapter, IPluginLoader pluginLoader) {

        this.cycleStatistics = cycleStatistics;
        this.saveGameStatistics = saveGameStatistics;
        this.serverRunner = serverRunner;
        this.serverNetworkAdapter = networkAdapter;
        this.pluginLoader = pluginLoader;
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
    public List<TCycleReplayDescription> getCycleReplayDescriptionsByDeltaTime(DateTime startingTime, DateTime endingTime, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
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
    public void saveMap(TMapMetaData mapMetaData, TEnvironmentDescription environment) throws TechnicalException, PluginNotReadableException {
        IEnvironmentPluginDescriptor gameLogic = pluginLoader.loadEnvironmentPlugin(environment);

        gameLogic.getInstance(saveGameStatistics).saveMap(mapMetaData);
    }

    @Override
    public List<TMapMetaData> getAvailableMaps(TEnvironmentDescription environment) throws CorruptMapFileException, TechnicalException, PluginNotReadableException {
        try {
            pluginLoader.listAvailableEnvironments(AppSettings.getString("environmentPluginsFolder"));
        } catch (SettingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        IEnvironmentPluginDescriptor gameLogic = pluginLoader.loadEnvironmentPlugin(environment);

        return gameLogic.getInstance(saveGameStatistics).getAvailableMaps();
    }

    @Override
    public void SaveReplay(ICycleReplay replay, TEnvironmentDescription environment) throws TechnicalException {
        saveGameStatistics.SaveReplay(replay, environment);
    }
}
