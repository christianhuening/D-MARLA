import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Service.ICycleReplay;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;

import java.util.Date;
import java.util.Iterator;
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

    /**
     * The initial configuration, that the environment had when it started.
     *
     * @return
     */
    @Override
    public IEnvironmentConfiguration getConfiguration() {
        return null;
    }

    Date replayDate;

    @Override
    public Date getReplayDate() {
        return replayDate;
    }

    List<String> players;

    @Override
    public List<String> getAgentSystems() {
        return players;
    }

    String winningPlayer;

    @Override
    public String getAgentSystemsWithGoalReached() {
        return winningPlayer;
    }

    int numberOfTurns;

    @Override
    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    List<IEnvironmentState> environmentStates;

// --------------------------- CONSTRUCTORS ---------------------------

    public TestReplay(UUID replayId, Date replayDate, List<String> players, String winningPlayer, int numberOfTurns, List<IEnvironmentState> environmentStates) {
        this.replayId = replayId;
        this.replayDate = replayDate;
        this.players = players;
        this.winningPlayer = winningPlayer;
        this.numberOfTurns = numberOfTurns;
        this.environmentStates = environmentStates;
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator iterator() {
        return null;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface ICycleReplay ---------------------
}
