package GameStatistics.Implementation;

import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import ZeroTypes.Exceptions.GameReplayNotContainedInDatabaseException;
import ZeroTypes.RemoteInterface.ICycleStatistics;
import ZeroTypes.TransportTypes.TCycleReplayDescription;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 08.05.12
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public class CycleStatisticsComponent implements ICycleStatistics, ICycleStatisticsSaver {
// ------------------------------ FIELDS ------------------------------

    private CycleStatisticsUseCase gameStatisticsUseCase;
    private GameReplayDescriptionSaverHelper gameReplayDescriptionSaverHelper;

// --------------------------- CONSTRUCTORS ---------------------------

    public CycleStatisticsComponent() throws TechnicalException {
        gameReplayDescriptionSaverHelper = new GameReplayDescriptionSaverHelper();
        gameStatisticsUseCase = new CycleStatisticsUseCase(gameReplayDescriptionSaverHelper);
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ICycleStatistics ---------------------

    @Override
    public ICycleReplay getCycleReplay(UUID gameID, TEnvironmentDescription environment) throws GameReplayNotContainedInDatabaseException, RemoteException, TechnicalException {
        return gameStatisticsUseCase.getCycleReplay(gameID, environment);
    }

    @Override
    public float getWinLoseRatio(String player, String opponent, TEnvironmentDescription environment) throws TechnicalException, RemoteException {
        return gameStatisticsUseCase.getWinLoseRatio(player, opponent, environment);
    }

    @Override
    public List<TCycleReplayDescription> getCycleReplayDescriptionsByDeltaTime(Date startingTime, Date endingTime, TEnvironmentDescription environment) throws RemoteException, TechnicalException {
        return gameStatisticsUseCase.getCycleReplayDescriptionsByDeltaTime(startingTime, endingTime, environment);
    }

    @Override
    public float getCurrentGamesPerMinute(TEnvironmentDescription environment) throws TechnicalException {
        return gameStatisticsUseCase.getCurrentGamesPerMinute(environment);
    }

    @Override
    public List<String> getClientNames(TEnvironmentDescription environment) throws RemoteException {
        return gameStatisticsUseCase.getClientNames(environment);
    }

    @Override
    public int getTotalNumberOfCycles(String playerName, TEnvironmentDescription environment) throws TechnicalException, RemoteException {
        return gameStatisticsUseCase.getTotalNumberOfCycles(playerName, environment);
    }

    @Override
    public int getTotalNumberOfCyclesWon(String playerName, TEnvironmentDescription environment) throws TechnicalException, RemoteException {
        return gameStatisticsUseCase.getTotalNumberOfCyclesWon(playerName, environment);
    }

    @Override
    public int getTotalNumberOfCyclesLost(String playerName, TEnvironmentDescription environment) throws TechnicalException, RemoteException {
        return gameStatisticsUseCase.getTotalNumberOfCyclesLost(playerName, environment);
    }

    @Override
    public float getAverageTurnsPerCycle(String playerName, int numberOfLastGames, TEnvironmentDescription environment) throws TechnicalException, RemoteException {
        return gameStatisticsUseCase.getAverageTurnsPerCycle(playerName, numberOfLastGames, environment);
    }

    @Override
    public List<TCycleReplayDescription> getDescriptionsForNLastCycles(int numberOfGames, TEnvironmentDescription environment) throws TechnicalException, RemoteException {
        return gameStatisticsUseCase.getDescriptionsForNLastCycles(numberOfGames, environment);
    }

// --------------------- Interface ICycleStatisticsSaver ---------------------


    @Override
    public void SaveReplay(ICycleReplay replay, TEnvironmentDescription environment) throws TechnicalException {
        gameStatisticsUseCase.SaveReplay(replay,environment);
    }
}
