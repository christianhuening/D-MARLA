package ZeroTypes.RemoteInterface;


import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Service.ICycleReplay;
import ZeroTypes.Exceptions.GameReplayNotContainedInDatabaseException;
import ZeroTypes.TransportTypes.TCycleReplayDescription;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Provides statistical informations about the games that were played on this server.
 */
public interface ICycleStatistics extends Remote {


    /**
     * Gets a specified replay for a specified environment.
     *
     * @param gameID The id of the cycle the replay is of.
     * @param environment The environment for which the replay will be returned.
     * @return A replay for the specified cycle.
     */
    public ICycleReplay getCycleReplay(UUID gameID, TEnvironmentDescription environment) throws GameReplayNotContainedInDatabaseException, RemoteException, TechnicalException;

    /**
     * Gets the win/loose ratio for a given player/opponent pair.
     * The wins returned, will be the wins of the player against the opponent.
     *
     * @param player   The player involved in the games.
     * @param opponent The opponent involved in the games.
     * @param environmentDescription The environment for which the win-loose-ratio will be returned.
     * @return The win ratio of the player against the opponent over all games played on this server.
     *         Will be 0 if the player won no games, or never played against the given opponent.
     */
    public float getWinLoseRatio(String player, String opponent, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException;

    /**
     * Gets all cycle replays of games for a specific environment that were played in the given time interval on this server.
     *
     * @param startingTime The starting time of the interval.
     * @param endingTime   The ending time of the interval.
     * @param environment The environment for which the replays will be returned.
     * @return All replays that were played in the given time period.
     */
    public List<TCycleReplayDescription> getCycleReplayDescriptionsByDeltaTime(Date startingTime, Date endingTime, TEnvironmentDescription environment) throws RemoteException, TechnicalException;

    /**
     * Gets the number of games that were played in a given environment over the last minute on this server.
     *
     * @param environment The environment for which the games will be returned.
     * @return The number of games that were played over the last minute on this server.
     */
    public float getCurrentGamesPerMinute(TEnvironmentDescription environment) throws RemoteException, TechnicalException;

    /**
     * Gets a list with all players that took part in a cycle played on this server.
     * Note that this only take games into account that are finished!
     *
     * @param environmentDescription The environment for which the MarlaClient-names will be returned.
     * @return
     * @throws RemoteException
     */
    public List<String> getClientNames(TEnvironmentDescription environmentDescription) throws RemoteException;

    /**
     * gets the total number of games played by a specific MarlaClient on this server.
     *
     * @param clientName The name of the MarlaClient.
     * @param environmentDescription The environment for which the total number of games played will be returned.
     * @return The number of games played by the given MarlaClient on this server.
     */
    public int getTotalNumberOfCycles(String clientName, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException;

    /**
     * Gets the total number of games won by a MarlaClient on this server.
     *
     * @param clientName The name of the MarlaClient.
     * @param environmentDescription The environment for which the wanted informations will be returned.
     * @return The total number of games that were won by the given MarlaClient in this server.
     */
    public int getTotalNumberOfCyclesWon(String clientName, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException;

    /**
     * Gets the total number of games lost by a MarlaClient on this server.
     *
     * @param clientName The name of the MarlaClient.
     * @param environmentDescription The environment for which the wanted informations will be returned.
     * @return The total number of games that were lost by the given MarlaClient in this server.
     */
    public int getTotalNumberOfCyclesLost(String clientName, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException;

    /**
     * Gets the average number of turns the last n games lasted, that a given MarlaClient played.
     *
     * @param playerName        The name of the MarlaClient that played these games.
     * @param numberOfLastGames The number of games over which the average number of turns is calculated.
     * @param environmentDescription The environment for which the wanted informations will be returned.
     * @return The number of turns the last n games lasted, that a given MarlaClient played.
     */
    public float getAverageTurnsPerCycle(String playerName, int numberOfLastGames, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException;

    /**
     * Gets the last n played games.
     * @param numberOfGames The number (n) of games to get.
     * @param environmentDescription The environment for which the wanted informations will be returned.
     * @return A List containing the n last played games, if at least n games were played on this server.
     * Else the actual number of games in the list will be smaller than n. If no games have been played on this server, the list will be empty.
     */
    public List<TCycleReplayDescription> getDescriptionsForNLastCycles(int numberOfGames, TEnvironmentDescription environmentDescription) throws RemoteException, TechnicalException;
}