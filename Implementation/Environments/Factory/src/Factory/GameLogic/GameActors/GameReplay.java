package Factory.GameLogic.GameActors;

import EnvironmentPluginAPI.Service.ICycleReplay;
import Factory.GameLogic.TransportTypes.TGameReplay;
import Factory.GameLogic.TransportTypes.TGameState;
import Factory.Interfaces.IHasTransportType;

import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 27.05.12
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */

public class GameReplay implements IHasTransportType<ICycleReplay>, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------
    private UUID replayID;

    public UUID getReplayID() {
        return replayID;
    }

    //public AbstractReplay(DateTime replayDate, List<String> players, String winningPlayer, int numberOfTurns) {
    private final DateTime replayDate;

    public DateTime getReplayDate() {
        return replayDate;
    }

    private final List<String> players;

    public List<String> getPlayers() {
        return players;
    }

    private final String winningPlayer;

    public String getWinningPlayer() {
        return winningPlayer;
    }

    private final int numberOfTurns;

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    private List<TGameState> gameStatesPerTurn;

    public List<TGameState> getGameStatesPerTurn() {
        return gameStatesPerTurn;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public GameReplay(DateTime replayDate, List<String> players, String winningPlayer, int numberOfTurns, List<TGameState> gameStates) {
        this.replayID = UUID.randomUUID();
        this.replayDate = replayDate;
        this.players = players;
        this.winningPlayer = winningPlayer;
        this.numberOfTurns = numberOfTurns;

        this.gameStatesPerTurn = gameStates;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasTransportType ---------------------

    @Override
    public ICycleReplay getTransportType() {
        return new TGameReplay(this.replayID, this.replayDate, this.players, this.winningPlayer, this.numberOfTurns, gameStatesPerTurn);
    }
}
