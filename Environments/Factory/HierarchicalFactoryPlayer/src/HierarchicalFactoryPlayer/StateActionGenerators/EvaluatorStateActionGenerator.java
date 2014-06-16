package HierarchicalFactoryPlayer.StateActionGenerators;

import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import java.util.HashSet;
import java.util.Set;

/**
 * Generates the possible actions for the evaluator from a given state
 */
public class EvaluatorStateActionGenerator implements IStateActionGenerator {


    @Override
    public Set<StateAction> getAllPossibleActions(StateAction stateAction) {
        Set<StateAction> possibleActions = new HashSet<>();

        String[] actions = new String[]{"+","-","="};

        // create all permutations
        for(int a = 0; a < 3; a++){
            for(int b = 0; b < 3; b++){
                for (int c = 0; c < 3; c++) {
                    for (int d = 0; d < 3; d++) {
                        StringBuilder stb = new StringBuilder(4);
                        stb.append(actions[a]);
                        stb.append(actions[b]);
                        stb.append(actions[c]);
                        stb.append(actions[d]);
                        possibleActions.add(new StateAction(stateAction.getStateDescription(), stb.toString()));
                    }
                }
            }
        }

        return possibleActions;
    }


}
