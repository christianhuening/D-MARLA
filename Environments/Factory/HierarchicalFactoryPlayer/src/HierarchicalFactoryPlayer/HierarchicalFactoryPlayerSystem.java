package HierarchicalFactoryPlayer;

import AgentSystemPluginAPI.Contract.IAgentSystem;
import AgentSystemPluginAPI.Contract.StateAction;
import AgentSystemPluginAPI.Services.IAgent;
import AgentSystemPluginAPI.Services.IPluginServiceProvider;
import AgentSystemPluginAPI.Services.LearningAlgorithm;
import EnvironmentPluginAPI.Exceptions.TechnicalException;
import EnvironmentPluginAPI.Service.IEnvironmentConfiguration;
import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.*;
import Factory.GameLogic.Utility.GameInfos;
import HierarchicalFactoryPlayer.Entities.EvaluatorGameState;
import HierarchicalFactoryPlayer.StateActionGenerators.EvaluatorStateActionGenerator;
import HierarchicalFactoryPlayer.StateActionGenerators.MoverStateActionGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 13.06.2014.
 */
public class HierarchicalFactoryPlayerSystem implements IAgentSystem<IEnvironmentConfiguration, TGameState, TActionsInTurn> {
    private final IAgent evaluator;
    private final IAgent mover;
    private final EvaluatorGameState evaluatorGameState;
    private List<IAgent> agentlist;
    private Faction myFaction;
    private Faction enemyFaction;
    private boolean firstturn;
    private float rewardForLastTurn;

    public HierarchicalFactoryPlayerSystem(IPluginServiceProvider provider) throws TechnicalException {
        agentlist = new ArrayList<>();

        evaluator = provider.getTableAgent("Evaluator", LearningAlgorithm.SARSALambda, new EvaluatorStateActionGenerator());
        agentlist.add(evaluator);
        mover = provider.getTableAgent("Mover", LearningAlgorithm.SARSALambda, new MoverStateActionGenerator());
        agentlist.add(mover);

        evaluatorGameState = new EvaluatorGameState(evaluator);

    }

    @Override
    public void start(IEnvironmentConfiguration metaData) throws TechnicalException {
        myFaction = Faction.valueOf(metaData.toString());
        if (myFaction == Faction.RED) {
            enemyFaction = Faction.BLUE;
        } else {
            enemyFaction = Faction.RED;
        }
        firstturn = true;
    }

    @Override
    public TActionsInTurn getActionsForEnvironmentStatus(TGameState currentGameState) throws TechnicalException {
        List<TUnit> myUnits = GameInfos.getUnitsForFaction(currentGameState, myFaction);

        // the action list holding all actions for the next step
        List<TAction> actionList = new ArrayList<TAction>();

        // First evaluate the current game state
        // tGameState.
        evaluatorGameState.evaluateNewGameState(currentGameState);

        // Now use this evaluated game state

        for (TUnit unit : myUnits) {


            TPosition unitPosition = GameInfos.getPositionForUnit(currentGameState, unit);
            TAbstractField field = GameInfos.getFieldForPosition(currentGameState, unitPosition);

            StateAction action = null;

            if (firstturn) {
                action = mover.startEpisode(new StateAction(""));
                firstturn = false;
            } else {
                action = mover.step(rewardForLastTurn, new StateAction(""));
            }

        }
        //rewardForLastTurn = calculateReward(currentGameState, moveOrder, unitPosition, field);


        // currentGameState = refreshGameState(currentGameState, moveOrder, unitPosition);

        return new TActionsInTurn(actionList);
    }

    @Override
    public void end() throws TechnicalException {

    }


    public List<IAgent> getInternalAgents() {
        return this.agentlist;
    }


    private float calculateReward(TGameState currentGameState, TAction moveOrder, TPosition unitPosition, TAbstractField field) {
        return 0;
    }

}
