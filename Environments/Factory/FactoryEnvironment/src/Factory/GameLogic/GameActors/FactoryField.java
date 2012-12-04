package Factory.GameLogic.GameActors;

import Factory.GameLogic.TransportTypes.TFactoryField;
import Factory.GameLogic.TransportTypes.TUnit;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */
public class FactoryField extends AbstractField implements java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    public int factoryID;

    public int getFactoryID() {
        return factoryID;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Hibernate constructor!
     */
    public FactoryField() {

    }

    public FactoryField(int factoryID) {
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

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasTransportType ---------------------

    @Override
    public TFactoryField getTransportType() {
        TUnit tUnit = null;
        if (this.isOccupied()) {
            tUnit = this.getOccupant().getTransportType();
        }
        return new TFactoryField(tUnit, factoryID);
    }
}
