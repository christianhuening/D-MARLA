package Factory.GameLogic.TransportTypes;


import EnvironmentPluginAPI.Service.ICycleReplay;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 08.05.12
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class TGameReplay implements ICycleReplay<TGameState> {
// ------------------------------ FIELDS ------------------------------

    private UUID replayId;

    @Override
    public UUID getReplayId() {
        return replayId;
    }

    private DateTime replayDate;

    @Override
    public DateTime getReplayDate() {
        return replayDate;
    }

    private List<String> players;

    @Override
    public List<String> getPlayers() {
        return players;
    }

    private String winningPlayer;

    @Override
    public String getWinningPlayer() {
        return winningPlayer;
    }

    private int numberOfTurns;

    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    private List<TGameState> gameStates;

    @Override
    public List<TGameState> getEnvironmentStatesPerTurn() {
        return gameStates;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TGameReplay(UUID replayId, DateTime replayDate, List<String> players, String winningPlayer, int numberOfTurns) {
        this.replayId = replayId;
        this.replayDate = replayDate;
        this.players = players;
        this.winningPlayer = winningPlayer;
        this.numberOfTurns = numberOfTurns;
    }

    public TGameReplay(UUID replayId, DateTime replayDate, List<String> players, String winningPlayer, int numberOfTurns, List<TGameState> gameStates) {
        this.replayId = replayId;
        this.replayDate = replayDate;
        this.players = players;
        this.winningPlayer = winningPlayer;
        this.numberOfTurns = numberOfTurns;
        this.gameStates = gameStates;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ICycleReplay)) return false;

        TGameReplay replay = (TGameReplay) o;

        if (replayId != null ? !replayId.equals(replay.replayId) : replay.replayDate != null)
            return false;
        if (numberOfTurns != replay.numberOfTurns) return false;
        if (gameStates != null ? !gameStates.equals(replay.gameStates) : replay.gameStates != null)
            return false;
        if (players != null ? !players.equals(replay.players) : replay.players != null) return false;
        if (replayDate != null ? !replayDate.equals(replay.replayDate) : replay.replayDate != null) return false;
        if (winningPlayer != null ? !winningPlayer.equals(replay.winningPlayer) : replay.winningPlayer != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = replayDate != null ? replayDate.hashCode() : 0;
        result = 31 * result + (players != null ? players.hashCode() : 0);
        result = 31 * result + (winningPlayer != null ? winningPlayer.hashCode() : 0);
        result = 31 * result + numberOfTurns;
        result = 31 * result + (gameStates != null ? gameStates.hashCode() : 0);
        return result;
    }
}
