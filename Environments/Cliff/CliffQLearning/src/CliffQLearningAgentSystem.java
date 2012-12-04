import Actions.CliffActionDescription;
import Actions.CliffEnvironmentState;
import Actions.Direction;
import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Contract.Exception.TechnicalException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple agent system for the a grid world, consisting of one q-learning agent.
 */
public class CliffQLearningAgentSystem implements IAgentSystem<CliffEnvironmentState, CliffActionDescription> {

    IAgent sarsaLambdaAgent;

    public CliffQLearningAgentSystem(IPluginServiceProvider pluginServiceProvider) throws TechnicalException {

        sarsaLambdaAgent = pluginServiceProvider.getTableAgent("qlearning", LearningAlgorithm.SARSALambda, new IStateActionGenerator() {
            @Override
            public Set<StateAction> getAllPossibleActions(StateAction stateAction) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(stateAction.getStateDescription().getBytes()));
                try {
                    int width = in.read();
                    int height = in.read();
                    int x = in.read();
                    int y = in.read();

                    Set<StateAction> possibleActions = new HashSet<StateAction>();

                    if (x < width - 1) possibleActions.add(new StateAction(stateAction.getStateDescription(), "RIGHT"));
                    if (x > 0) possibleActions.add(new StateAction(stateAction.getStateDescription(), "LEFT"));
                    if (y > 0) possibleActions.add(new StateAction(stateAction.getStateDescription(), "DOWN"));
                    if (y < height - 1) possibleActions.add(new StateAction(stateAction.getStateDescription(), "UP"));

                    return possibleActions;
                } catch (IOException e) {
                    e.printStackTrace();
                    return new HashSet<StateAction>();
                }
            }
        });

        sarsaLambdaAgent.setAlpha(0.6f);
        sarsaLambdaAgent.setEpsilon(0.001f);
        sarsaLambdaAgent.setGamma(0.7f);
        sarsaLambdaAgent.setLambda(0.5f);
    }

    @Override
    public void start(Object o) throws TechnicalException {
        sarsaLambdaAgent.startEpisode(new StateAction(new CliffEnvironmentState(0, 0, false, 0.0f, 20, 8).getCompressedRepresentation()));
    }

    @Override
    public CliffActionDescription getActionsForEnvironmentStatus(CliffEnvironmentState environmentState) throws TechnicalException {
        System.err.println("environmentState erhalten: ");

        StateAction newStateAction = sarsaLambdaAgent.step(environmentState.getReward(), new StateAction(environmentState.getCompressedRepresentation()));
        return new CliffActionDescription(Direction.valueOf(newStateAction.getActionDescription()));
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
