package HierarchicalFactoryPlayer.StateActionGenerators;

import AgentSystemPluginAPI.Contract.IStateActionGenerator;
import AgentSystemPluginAPI.Contract.StateAction;
import Factory.GameLogic.Enums.Direction;
import HierarchicalFactoryPlayer.Entities.RawState;

import java.util.Set;

/**
 * Created by Chris on 13.06.2014.
 */
public class MoverStateActionGenerator implements IStateActionGenerator {
    @Override
    public Set<StateAction> getAllPossibleActions(StateAction state) {
        return null;
    }

    public Direction decryptDirection(String actionDescription) {
        return null;
    }

    public String encryptState(RawState rawState) {
        return null;
    }
}
