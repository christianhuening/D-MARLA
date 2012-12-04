package Factory.GameLogic.TransportTypes;

import Factory.GameLogic.Enums.Direction;
import Factory.Interfaces.IHasConsistencyCheck;


/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 18.05.12
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public class TAction implements java.io.Serializable, IHasConsistencyCheck {
// ------------------------------ FIELDS ------------------------------

    private TUnit unit;

    public TUnit getUnit() {
        return unit;
    }

    private Direction direction;

    public Direction getDirection() {
        return direction;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TAction(TUnit unit, Direction direction) {
        this.unit = unit;
        this.direction = direction;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TAction)) return false;

        TAction tAction = (TAction) o;

        if (direction != tAction.direction) return false;
        if (unit != null ? !unit.equals(tAction.unit) : tAction.unit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = unit != null ? unit.hashCode() : 0;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        return result;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasConsistencyCheck ---------------------

    @Override
    public boolean isConsistent() {
        if(unit == null || direction == null) {
            return false;
        }

        if(!unit.isConsistent()) {
            return false;
        }

        if(direction == null) {
            return false;
        }

        return true;
    }
}
