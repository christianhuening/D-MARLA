package HierarchicalFactoryPlayer.Entities;

import AgentSystemPluginAPI.Services.IAgent;
import Factory.GameLogic.TransportTypes.TAbstractField;
import Factory.GameLogic.TransportTypes.TGameState;
import Factory.GameLogic.TransportTypes.TPosition;

/**
 * This class evaluates the game state in a recursive manner. Reinforcement Learning is used on a 2 by 2 grid environment
 * which gets singled out by means of a recursive algorithm which breaks up the whole environment into a quad-tree like
 * structure
 */
public class EvaluatorGameState {

    private IAgent evaluator;

    public EvaluatorGameState(IAgent evaluator){

        this.evaluator = evaluator;
    }

    /**
     * Evaluates the new game state
     * @param gameState The game state to be evaluated
     */
    public void evaluateNewGameState(TGameState gameState){

    }

    /**
     * Retrieves the evaluation for a specific field and unitPosition
     * @param field
     * @param unitPosition
     * @return A RawState object holding a readable representation of the current State
     */
    public RawState getEvaluation(TAbstractField field, TPosition unitPosition) {
        return null;
    }
}
