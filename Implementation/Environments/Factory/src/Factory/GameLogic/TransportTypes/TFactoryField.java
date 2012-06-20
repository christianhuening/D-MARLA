package Factory.GameLogic.TransportTypes;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 21.05.12
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */
public class TFactoryField extends TAbstractField implements java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private int factoryID;

    public int getFactoryID() {
        return factoryID;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TFactoryField(TUnit occupant, int factoryID) {
        super(occupant);
        this.factoryID = factoryID;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        String unit = " ";
        if (this.isOccupied())
            unit = this.getOccupant().toString();
        return "[ F" + unit + "]";
    }
}
