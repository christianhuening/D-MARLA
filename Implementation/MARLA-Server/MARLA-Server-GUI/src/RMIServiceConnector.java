import EnvironmentPluginAPI.Contract.Exception.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import Exceptions.GameReplayNotContainedInDatabaseException;
import GameServerFacade.Interface.IServerFacade;
import RemoteInterface.ICycleStatistics;
import TransportTypes.TCycleReplayDescription;
import org.joda.time.DateTime;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to relay RMI Calls from clients to the application facade.
 */
public class RMIServiceConnector implements ICycleStatistics {

    private transient IServerFacade facade;
    private TEnvironmentDescription environmentDescription;

    public RMIServiceConnector(IServerFacade gameServerFacade) {
        this.facade = gameServerFacade;
    }

    @Override
    public ICycleReplay getCycleReplay(UUID gameID, TEnvironmentDescription environmentDescription) throws GameReplayNotContainedInDatabaseException, RemoteException, TechnicalException {
        return facade.getCycleReplay(gameID, environmentDescription);
    }

    @Override
    public float getWinLoseRatio(String player, String opponent, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getWinLoseRatio(player, opponent, environmentDescription);
    }

    @Override
    public List<TCycleReplayDescription> getCycleReplayDescriptionsByDeltaTime(DateTime startingTime, DateTime endingTime, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getCycleReplayDescriptionsByDeltaTime(startingTime, endingTime, environmentDescription);
    }

    @Override
    public float getCurrentGamesPerMinute(TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getCurrentGamesPerMinute(environmentDescription);
    }

    @Override
    public List<String> getClientNames(TEnvironmentDescription environmentDescription) throws RemoteException {
        return facade.getClientNames(environmentDescription);
    }

    @Override
    public int getTotalNumberOfCycles(String playerName, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getTotalNumberOfCycles(playerName, environmentDescription);
    }

    @Override
    public int getTotalNumberOfCyclesWon(String playerName, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getTotalNumberOfCyclesWon(playerName, environmentDescription);
    }

    @Override
    public int getTotalNumberOfCyclesLost(String playerName, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getTotalNumberOfCyclesLost(playerName, environmentDescription);
    }

    @Override
    public float getAverageTurnsPerCycle(String playerName, int numberOfLastGames, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getAverageTurnsPerCycle(playerName, numberOfLastGames, environmentDescription);
    }

    @Override
    public List<TCycleReplayDescription> getDescriptionsForNLastCycles(int numberOfGames, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException {
        return facade.getDescriptionsForNLastCycles(numberOfGames, environmentDescription);
    }
}
