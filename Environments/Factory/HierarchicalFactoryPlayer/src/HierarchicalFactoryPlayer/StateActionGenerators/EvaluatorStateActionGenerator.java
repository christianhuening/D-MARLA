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
    public Set<StateAction> getAllPossibleActions(StateAction state) {
        // does state even matter here?

        Set<StateAction> possibleActions = new HashSet<>();
        possibleActions.add(new StateAction())



        return possibleActions;
    }
}
