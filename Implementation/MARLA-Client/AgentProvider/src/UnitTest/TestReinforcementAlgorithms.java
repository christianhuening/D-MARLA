import AgentProvider.Implementation.AgentProviderComponent;
import AgentProvider.Interface.IAgentProvider;
import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import ZeroTypes.Settings.SettingException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This test is supposed to determine if the concrete implementations of the learning algorithm show expected behaviour.
 */
public class TestReinforcementAlgorithms implements IStateActionGenerator {

    private static IAgentProvider agentProvider;
    private static int[][] cliffWorld;
    private static int width = 10;
    private static int height = 5;

    private int agentX = 0;
    private int agentY = 0;

    @BeforeClass
    public static void setup() {

        try {
            agentProvider = new AgentProviderComponent();
        } catch (TechnicalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            agentProvider.loadAgentSystem("testAgentSystem");
        } catch (TechnicalException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SettingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        initializeCliff();

    }

    public static void initializeCliff() {
        cliffWorld = new int[height][width];

        //set way costs to -1 for all fields except the cliff
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (x < height - 1 || y == 0 || y == width - 1) { //the normal fields
                    cliffWorld[x][y] = -1;
                } else { //the cliff
                    cliffWorld[x][y] = -100;
                }
            }
        }

        cliffWorld[height - 1][width - 1] = 200;
    }

    public void printState() {

        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                if (x == agentX && y == agentY) {
                    System.out.print("A");
                } else {
                    System.out.print(cliffWorld[x][y]);
                }
            }
            System.out.print("\n");
        }

        System.out.println("=================================================================================================\n");
    }

    private void makeMove(StateAction action) {
        if (action.getActionDescription().equals("U")) {
            agentX--;
        } else if (action.getActionDescription().equals("D")) {
            agentX++;
        } else if (action.getActionDescription().equals("L")) {
            agentY--;
        } else if (action.getActionDescription().equals("R")) {
            agentY++;
        }
    }

    private int average(List<Integer> list) {
        int sum = 0;

        if (list.size() == 0) {
            return Integer.MAX_VALUE;
        }

        for (Integer i : list) {
            sum += i;
        }

        return sum / list.size();
    }

    @Override
    public Set<StateAction> getAllPossibleActions(StateAction state) {
        Set<StateAction> result = new HashSet<StateAction>();

        if (agentX > 1) {
            result.add(new StateAction(state.getStateDescription(), "U"));
        }

        if (agentX < height - 1) {
            result.add(new StateAction(state.getStateDescription(), "D"));
        }

        if (agentY > 1) {
            result.add(new StateAction(state.getStateDescription(), "L"));
        }

        if (agentY < width - 1) {
            result.add(new StateAction(state.getStateDescription(), "R"));
        }

        return result;
    }

    @Test
    public void testQLearning() throws TechnicalException, SettingException {

        IAgent agent = agentProvider.getTableAgent("QLearningGridWorld", LearningAlgorithm.QLearning, this);
        agent.setAlpha(0.6f);
        agent.setEpsilon(0.1f);
        agent.setGamma(0.7f);

        List<Integer> steps = new LinkedList<Integer>();
        StateAction action;
        int step;

        for (int i = 0; i < 200; i++) {

            agentX = height - 1;
            agentY = 0;
            step = 0;

            action = agent.startEpisode(new StateAction("" + agentX + agentY));
            while (agentX != height - 1 || agentY != width - 1) {
                //printState();
                makeMove(action);
                action = agent.step(cliffWorld[agentX][agentY], new StateAction("" + agentX + agentY));
                step++;
            }

            if (i >= 80) {
                steps.add(step);
            }
            //System.out.println("steps in this round: " + step + " average: " + average(steps));
            agent.endEpisode(new StateAction("" + agentX + agentY), -1);
        }

        Assert.assertTrue(average(steps) < 15);

    }

    @Test
    public void testSARSALambda() throws TechnicalException, SettingException {

        IAgent agent = agentProvider.getTableAgent("SARSALambdaGridworld", LearningAlgorithm.SARSALambda, this);
        agent.setAlpha(0.6f);
        agent.setEpsilon(0.001f);
        agent.setGamma(0.7f);
        agent.setLambda(0.5f);

        List<Integer> steps = new LinkedList<Integer>();
        StateAction action;
        int step;

        for (int i = 0; i < 100; i++) {

            agentX = height - 1;
            agentY = 0;
            step = 0;

            action = agent.startEpisode(new StateAction("" + agentX + agentY));
            while (agentX != height - 1 || agentY != width - 1) {
                //printState();
                makeMove(action);
                action = agent.step(cliffWorld[agentX][agentY], new StateAction("" + agentX + agentY));
                step++;
            }
            if (i >= 80) {
                steps.add(step);
                //System.out.println( i + "th round, steps in this round: " + step + " average: " + average(steps));
            }
            agent.endEpisode(new StateAction("" + agentX + agentY), -1);
        }

        Assert.assertTrue(average(steps) < 15);

    }

    @Test
    public void testSARSA() throws TechnicalException, SettingException {

        IAgent agent = agentProvider.getTableAgent("SARSAGridworld", LearningAlgorithm.SARSA, this);
        agent.setAlpha(0.6f);
        agent.setEpsilon(0.1f);
        agent.setGamma(0.7f);

        List<Integer> steps = new LinkedList<Integer>();
        StateAction action;
        int step;

        for (int i = 0; i < 250; i++) {

            agentX = height - 1;
            agentY = 0;
            step = 0;

            action = agent.startEpisode(new StateAction("" + agentX + agentY));
            while (agentX != height - 1 || agentY != width - 1) {
                //printState();
                makeMove(action);
                action = agent.step(cliffWorld[agentX][agentY], new StateAction("" + agentX + agentY));
                step++;
            }

            if (i >= 80) {
                steps.add(step);
            }
            //System.out.println("steps in this round: " + step + " average: " + average(steps));
            agent.endEpisode(new StateAction("" + agentX + agentY), -1);
        }

        Assert.assertTrue(average(steps) < 20);

    }
}
