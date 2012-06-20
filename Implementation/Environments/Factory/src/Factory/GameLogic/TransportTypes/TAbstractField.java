package Factory.GameLogic.TransportTypes;

import Factory.Interfaces.IHasConsistencyCheck;


/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 21.05.12
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class TAbstractField implements IHasConsistencyCheck, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private TUnit occupant;

    public TUnit getOccupant() {
        return occupant;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TAbstractField(TUnit occupant) {
        this.occupant = occupant;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TAbstractField)) return false;

        TAbstractField that = (TAbstractField) o;

        if (occupant != null ? !occupant.equals(that.occupant) : that.occupant != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return occupant != null ? occupant.hashCode() : 0;
    }

    @Override
    public String toString() {
        String unit = " ";
        if (this.isOccupied())
            unit = this.getOccupant().toString();

        return "[ N" + unit + "]";
    }
    
    public boolean isOccupied() {
        if (occupant == null) {
            return false;
        } else {
            return true;
        }
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasConsistencyCheck ---------------------

    @Override
    public boolean isConsistent() {
        if (occupant != null) {
            return occupant.isConsistent();
        }

        return true;
    }
}
