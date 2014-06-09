package Factory.GameLogic.TransportTypes;

import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.TransportTypes.TMapMetaData;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 08.05.12
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class TGameReplay implements ICycleReplay<TGameState, TMapMetaData> {

// ------------------------------ FIELDS ------------------------------

    private UUID replayId;

    @Override
    public UUID getReplayId() {
        return replayId;
    }

    @Override
    public TMapMetaData getConfiguration() {
        return null;
    }

    private Date replayDate;

    @Override
    public Date getReplayDate() {
        return replayDate;
    }

    private List<String> players;

    @Override
    public List<String> getAgentSystems() {
        return players;
    }

    private String winningPlayer;

    @Override
    public String getAgentSystemsWithGoalReached() {
        return winningPlayer;
    }

    private int numberOfTurns;

    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    private List<TGameState> gameStates;

    public List<TGameState> getEnvironmentStatesPerTurn() {
        return gameStates;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TGameReplay(UUID replayId, Date replayDate, List<String> players, String winningPlayer, int numberOfTurns) {
        this.replayId = replayId;
        this.replayDate = replayDate;
        this.players = players;
        this.winningPlayer = winningPlayer;
        this.numberOfTurns = numberOfTurns;
    }

    public TGameReplay(UUID replayId, Date replayDate, List<String> players, String winningPlayer, int numberOfTurns, List<TGameState> gameStates) {
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

    @Override
    public Iterator<TGameState> iterator() {
        return gameStates.iterator();
    }

    @Override
    public void forEach(Consumer<? super TGameState> action) {
        gameStates.forEach(tGameState -> {
            action.accept(tGameState);
        });
    }

    @Override
    public Spliterator<TGameState> spliterator() {
        return gameStates.spliterator();
    }
}
