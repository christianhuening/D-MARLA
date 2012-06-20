package Factory.GameLogic.TransportTypes;

import Factory.GameLogic.Enums.Faction;
import Factory.Interfaces.IHasConsistencyCheck;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 08.05.12
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class TUnit implements IHasConsistencyCheck, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private UUID unitId;

    public UUID getUnitId() {
        return unitId;
    }

    private int exhaustedForTurn;
    
    public int getExhaustedForTurn() {
        return exhaustedForTurn;
    }

    private Faction controllingFaction;

    public Faction getControllingFaction() {
        return controllingFaction;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public TUnit(UUID unitId, Faction controllingFaction) {
        this.unitId = unitId;
        this.controllingFaction = controllingFaction;
    }

    public TUnit(UUID unitId, Faction controllingFaction, int exhaustedForTurn) {
        this.unitId = unitId;
        this.controllingFaction = controllingFaction;
        this.exhaustedForTurn = exhaustedForTurn;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TUnit)) return false;

        TUnit tUnit = (TUnit) o;

        if (!unitId.equals(tUnit.unitId)) return false;
        if (controllingFaction != tUnit.controllingFaction) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = unitId.hashCode();
        result = 31 * result + (controllingFaction != null ? controllingFaction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.getControllingFaction().toString();
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasConsistencyCheck ---------------------

    @Override
    public boolean isConsistent() {
        if (unitId == null || exhaustedForTurn < -1 || controllingFaction == null) {
            return false;
        }

        return true;
    }
}
