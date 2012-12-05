import Actions.ActionDescription;
import Actions.EnvironmentState;
import EnvironmentPluginAPI.Contract.IEnvironment;
import EnvironmentPluginAPI.Contract.IEnvironmentState;
import EnvironmentPluginAPI.Contract.TEnvironmentDescription;
import EnvironmentPluginAPI.Exceptions.CorruptConfigurationFileException;
import EnvironmentPluginAPI.Exceptions.IllegalNumberOfClientsException;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.ICycleStatisticsSaver;
import EnvironmentPluginAPI.TransportTypes.TMARLAClientInstance;
import Logic.GridWorldConfiguration;
import Logic.GridWorldStyle;
import Logic.Session;
import Statistics.CliffReplay;

import java.util.LinkedList;
import java.util.List;

/**
 *  This class implements the grid-world environment plugin.
 */
public class CliffEnvironmentPlugin implements IEnvironment<GridWorldConfiguration, EnvironmentState, ActionDescription> {

    private final ICycleStatisticsSaver cycleStatisticsSaver;
    private Session session;
    private TMARLAClientInstance activeInstance;
    private CliffReplay replay;


    public CliffEnvironmentPlugin(ICycleStatisticsSaver cycleStatisticsSaver) {
        this.cycleStatisticsSaver = cycleStatisticsSaver;
    }

    @Override
    public List<GridWorldConfiguration> getAvailableConfigurations() throws CorruptConfigurationFileException, TechnicalException {
        LinkedList<GridWorldConfiguration> configurations = new LinkedList<GridWorldConfiguration>();
        configurations.add(new GridWorldConfiguration(20, 8, GridWorldStyle.Cliff));
        configurations.add(new GridWorldConfiguration(20, 8, GridWorldStyle.Random));
        return configurations;
    }

    @Override
    public void saveConfiguration(GridWorldConfiguration configuration) throws TechnicalException {
        // not yet..
    }

    @Override
    public IEnvironmentState start(List<TMARLAClientInstance> marlaClientInstances, GridWorldConfiguration configuration) throws TechnicalException, IllegalNumberOfClientsException {
        if (marlaClientInstances.size() != 1) {
            throw new IllegalNumberOfClientsException("The cliff needs exactly one participant.");
        }

        session = new Session(configuration);
        activeInstance = marlaClientInstances.get(0);
        replay = new CliffReplay("QLearningAgent", 0, configuration);
        replay.addState(session.getCurrentState());

        return session.getCurrentState();
    }

    @Override
    public boolean isStillActive() {
        return session.isStillActive();
    }

    @Override
    public TMARLAClientInstance getActiveInstance() {
        return activeInstance;
    }

    @Override
    public EnvironmentState getCurrentEnvironmentState() throws TechnicalException {
        return session.getCurrentState();
    }

    @Override
    public EnvironmentState executeAction(ActionDescription actionDescription) throws TechnicalException {
        session.moveAgent(actionDescription.getDirection());

        replay.addState(session.getCurrentState());
        replay.countTurn();

        return session.getCurrentState();
    }

    @Override
    public void end() throws TechnicalException {
        cycleStatisticsSaver.SaveReplay(replay, new TEnvironmentDescription("The Cliff", "v0.01", "A simple environment, illustrating the cliff environment" +
                " from the book by Sutton. See http://webdocs.cs.ualberta.ca/~sutton/book/ebook/node1.html"));
    }
}
