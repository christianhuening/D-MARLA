package HierarchicalFactoryPlayer.Entities;

import AgentSystemPluginAPI.Services.IAgent;
import Factory.GameLogic.TransportTypes.TAbstractField;
import Factory.GameLogic.TransportTypes.TGameState;
import Factory.GameLogic.TransportTypes.TPosition;
import Factory.GameLogic.TransportTypes.TUnit;
import Factory.GameLogic.Utility.GameInfos;
import HierarchicalFactoryPlayer.Exceptions.MapIsNoSquareException;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

/**
 * This class evaluates the game state in a recursive manner. Reinforcement Learning is used on a 2 by 2 grid environment
 * which gets singled out by means of a recursive algorithm which breaks up the whole environment into a quad-tree like
 * structure
 */
public class EvaluatorGameState {

    private IAgent evaluator;
    private String[][] evaluatedArray;

    public EvaluatorGameState(IAgent evaluator){
        this.evaluator = evaluator;
    }

    /**
     * Evaluates the new game state
     * @param gameState The game state to be evaluated
     */
    public void evaluateNewGameState(TGameState gameState) throws MapIsNoSquareException {
        TAbstractField[][] mapFields = gameState.getMapFields();
        if(mapFields.length % 2 != 0 || mapFields[0].length % 2 != 0){
            throw new MapIsNoSquareException("Map needs to be a square for this to work. Sorry...");
        }
        evaluatedArray = new String[mapFields.length][mapFields.length];
        calculateIntervalEvaluations(evaluatedArray);
    }


    /**
     * Retrieves the evaluation for a specific field and unitPosition
     * @param unit The unit for which to look up the state
     * @return A RawState object holding a readable representation of the current State
     */
    public RawState getEvaluation(TUnit unit) {
        return null;
    }


    private void calculateIntervalEvaluations(String[][] mapFields) {
        int length = mapFields.length;
        ForkEvaluator fe = new ForkEvaluator(mapFields, 0, 0, length-1, length-1);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(fe);
    }
}
