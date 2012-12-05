package Statistics;

import Actions.EnvironmentState;
import EnvironmentPluginAPI.Service.ICycleReplay;
import Logic.GridWorldConfiguration;

import java.util.*;


public class CliffReplay implements ICycleReplay<EnvironmentState, GridWorldConfiguration> {

    private UUID id;
    private Date replayDate;
    private List<String> agentNames;
    private int nrOfTurns;
    private List<EnvironmentState> states;
    private GridWorldConfiguration configuration;

    public CliffReplay(String agentName, int nrOfTurns, GridWorldConfiguration configuration) {
        this.nrOfTurns = nrOfTurns;
        this.configuration = configuration;
        id = UUID.randomUUID();
        replayDate = new Date();
        agentNames = new LinkedList<String>();
        agentNames.add(agentName);
        states = new LinkedList<EnvironmentState>();
    }

    @Override
    public UUID getReplayId() {
        return id;
    }

    @Override
    public GridWorldConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Date getReplayDate() {
        return replayDate;
    }

    @Override
    public List<String> getAgentSystems() {
        return agentNames;
    }

    @Override
    public String getAgentSystemsWithGoalReached() {
        return agentNames.get(0);
    }

    public void countTurn() {
        nrOfTurns++;
    }

    @Override
    public int getNumberOfTurns() {
        return nrOfTurns;
    }

    public void addState(EnvironmentState state) {
        states.add(state);
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator iterator() {
        return states.iterator();
    }
}
