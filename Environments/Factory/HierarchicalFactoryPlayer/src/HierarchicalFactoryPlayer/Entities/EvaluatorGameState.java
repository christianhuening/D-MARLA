package HierarchicalFactoryPlayer.Entities;

import AgentSystemPluginAPI.Services.IAgent;
import Factory.GameLogic.TransportTypes.TGameState;

/**
 * Created by chhuening on 15.06.14.
 */

/**
 * This class holds the evaluated gamestate form
 */
public class EvaluatorGameState {

    private IAgent evaluator;

    public EvaluatorGameState(IAgent evaluator){

        this.evaluator = evaluator;
    }

    public void evaluateNewGameState(TGameState gameState){

    }

}
