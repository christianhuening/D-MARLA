package Factory.GameLogic.GameActors;

import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.TAbstractField;
import Factory.GameLogic.TransportTypes.TInfluenceField;
import Factory.GameLogic.TransportTypes.TUnit;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 20:26
 * To change this template use File | Settings | File Templates.
 */

public class InfluenceField extends AbstractField implements java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private int factoryID;

    public int getFactoryID() {
        return factoryID;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public InfluenceField(int factoryID) {
        this.factoryID = factoryID;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public int getInfluence() {
        int influence = 0;
        if (this.isOccupied()) {
            if (this.getOccupant().getFaction() == Faction.BLUE) {
                influence = 1;
            } else
                influence = -1;
        } else {
            influence = 0;
        }
        return influence;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        String unit = " ";
        if (this.isOccupied())
            unit = this.getOccupant().toString();
        return "[ I" + unit + "]";
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasTransportType ---------------------

    @Override
    public TAbstractField getTransportType() {
        TUnit tUnit = null;
        if (this.isOccupied()) {
            tUnit = this.getOccupant().getTransportType();
        }
        return new TInfluenceField(tUnit, factoryID);
    }
}
