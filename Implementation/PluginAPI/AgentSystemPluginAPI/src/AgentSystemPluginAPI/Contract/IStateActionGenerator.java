package AgentSystemPluginAPI.Contract;

import java.util.Set;

/**
 * AgentSystems must implement this interface, so that an agent is able to work.
 */
public interface IStateActionGenerator {

    public Set<StateAction> getAllPossibleActions(StateAction state);

}
