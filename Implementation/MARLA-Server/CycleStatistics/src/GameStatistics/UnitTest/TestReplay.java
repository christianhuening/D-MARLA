import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Service.ICycleReplay;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 16.06.12
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class TestReplay implements ICycleReplay {
// ------------------------------ FIELDS ------------------------------

    UUID replayId;

    @Override
    public UUID getReplayId() {
        return replayId;
    }

    DateTime replayDate;

    @Override
    public DateTime getReplayDate() {
        return replayDate;
    }

    List<String> players;

    @Override
    public List<String> getPlayers() {
        return players;
    }

    String winningPlayer;

    @Override
    public String getWinningPlayer() {
        return winningPlayer;
    }

    int numberOfTurns;

    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    List<IEnvironmentState> environmentStates;

    @Override
    public List<? extends IEnvironmentState> getEnvironmentStatesPerTurn() {
        return environmentStates;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TestReplay(UUID replayId, DateTime replayDate, List<String> players, String winningPlayer, int numberOfTurns, List<IEnvironmentState> environmentStates) {
        this.replayId = replayId;
        this.replayDate = replayDate;
        this.players = players;
        this.winningPlayer = winningPlayer;
        this.numberOfTurns = numberOfTurns;
        this.environmentStates = environmentStates;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ICycleReplay ---------------------
}
