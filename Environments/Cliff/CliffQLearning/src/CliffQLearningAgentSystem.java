import Actions.ActionDescription;
import Actions.Direction;
import Actions.EnvironmentState;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import Logic.GridWorldConfiguration;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple agent system for the a grid world, consisting of one q-learning agent.
 */
public class CliffQLearningAgentSystem implements IAgentSystem<GridWorldConfiguration, EnvironmentState, ActionDescription> {

    private final IPluginServiceProvider pluginServiceProvider;
    IAgent sarsaLambdaAgent;
    private int gridWidth;
    private int gridHeight;

    public CliffQLearningAgentSystem(IPluginServiceProvider pluginServiceProvider) throws TechnicalException {

        this.pluginServiceProvider = pluginServiceProvider;
    }

    @Override
    public void start(GridWorldConfiguration configuration) throws TechnicalException {
        gridWidth = configuration.getWidth();
        gridHeight = configuration.getHeight();

        if (sarsaLambdaAgent != null) {
            sarsaLambdaAgent = pluginServiceProvider.getTableAgent("qlearning", LearningAlgorithm.SARSALambda, new IStateActionGenerator() {
                @Override
                public Set<StateAction> getAllPossibleActions(StateAction stateAction) {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(stateAction.getStateDescription().getBytes()));
                    try {
                        int x = in.read();
                        int y = in.read();

                        Set<StateAction> possibleActions = new HashSet<StateAction>();

                        if (x < gridWidth - 1) possibleActions.add(new StateAction(stateAction.getStateDescription(), "RIGHT"));
                        if (x > 0) possibleActions.add(new StateAction(stateAction.getStateDescription(), "LEFT"));
                        if (y > 0) possibleActions.add(new StateAction(stateAction.getStateDescription(), "DOWN"));
                        if (y < gridHeight - 1) possibleActions.add(new StateAction(stateAction.getStateDescription(), "UP"));

                        return possibleActions;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new HashSet<StateAction>();
                    }
                }

            });

            // set learning parameters
            sarsaLambdaAgent.setAlpha(0.6f);
            sarsaLambdaAgent.setEpsilon(0.001f);
            sarsaLambdaAgent.setGamma(0.7f);
            sarsaLambdaAgent.setLambda(0.5f);
        }

        sarsaLambdaAgent.startEpisode(new StateAction(new EnvironmentState(0, 0, false, 0.0f).getCompressedRepresentation()));
    }

    @Override
    public ActionDescription getActionsForEnvironmentStatus(EnvironmentState environmentState) throws TechnicalException {
        System.err.println("environmentState erhalten: ");

        StateAction newStateAction = sarsaLambdaAgent.step(environmentState.getReward(), new StateAction(environmentState.getCompressedRepresentation()));
        return new ActionDescription(Direction.valueOf(newStateAction.getActionDescription()));
    }

    @Override
    public void end() throws TechnicalException {
        System.err.println("beendet");
    }

    @Override
    public List<IAgent> getInternalAgents() {
        List<IAgent> result = new ArrayList<IAgent>();
        result.add(sarsaLambdaAgent);
        return result;
    }
}
