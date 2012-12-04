package Factory.GameLogic.GameActors;

import Factory.GameLogic.Enums.Faction;
import Factory.GameLogic.TransportTypes.TUnit;
import Factory.Interfaces.IHasTransportType;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: TwiG
 * Date: 13.05.12
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */

public class Unit implements IHasTransportType<TUnit>, java.io.Serializable {
// ------------------------------ FIELDS ------------------------------

    private Faction faction;

    public Faction getFaction() {
        return faction;
    }

    private UUID unitID;

    public UUID getUnitID() {
        return unitID;
    }

    private int exhaustedForTurn = -1;

    public int getExhaustedForTurn() {
        return exhaustedForTurn;
    }

    public void setExhaustedForTurn(int exhaustedForTurn) {
        this.exhaustedForTurn = exhaustedForTurn;
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public Unit(Faction faction) {
        this.faction = faction;
        this.unitID = UUID.randomUUID();
    }

    Unit(TUnit tUnit) {
        this.unitID = tUnit.getUnitId();
        this.faction = tUnit.getControllingFaction();
        this.exhaustedForTurn = tUnit.getExhaustedForTurn();
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return this.getFaction().toString();
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IHasTransportType ---------------------

    @Override
    public TUnit getTransportType() {
        return new TUnit(unitID, faction, exhaustedForTurn);
    }

// -------------------------- OTHER METHODS --------------------------

    public void calculateExhaustion(int turn) {
        exhaustedForTurn = turn;
    }
}
