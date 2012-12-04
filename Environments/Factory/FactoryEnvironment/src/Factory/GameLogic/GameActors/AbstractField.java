package Factory.GameLogic.GameActors;

import Factory.GameLogic.TransportTypes.TAbstractField;
import Factory.Interfaces.IHasTransportType;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 20:24
 * To change this template use File | Settings | File Templates.
 */

public abstract class AbstractField implements IHasTransportType<TAbstractField>, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private Unit occupant;

    public Unit getOccupant() {
        return occupant;
    }

    public void setOccupant(Unit unit) {
        occupant = unit;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        String unit = " ";
        if (this.isOccupied())
            unit = this.getOccupant().toString();

        return "[ N" + unit + "]";
    }

    public boolean isOccupied() {
        if (occupant == null)
            return false;
        return true;
    }

// -------------------------- OTHER METHODS --------------------------

    public Unit removeOccupant() {
        occupant = null;
        return occupant;
    }
}
