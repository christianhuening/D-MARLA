package Actions;

import EnvironmentPluginAPI.Contract.IActionDescription;

import java.io.Serializable;

/**
 * This class describes an action by an agent. In this case as easy as possible.
 * It's just a move in a direction.
 */
public class CliffActionDescription implements IActionDescription, Serializable {
    private Direction direction;

    public CliffActionDescription(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
