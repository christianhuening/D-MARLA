package Factory.GameLogic.TransportTypes;

import Factory.GameLogic.Enums.Faction;
import Factory.Interfaces.IHasConsistencyCheck;

/**
 * Created with IntelliJ IDEA.
 * User: N3trunner
 * Date: 21.05.12
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public class TFactory implements IHasConsistencyCheck, java.io.Serializable {
    private int remainingRoundsForRespawn;

    private int currentInfluence;

    private Faction owningFaction;

    private int factoryID;

    public TFactory(int remainingRoundsForRespawn, int currentInfluence, Faction owningFaction, int factoryID) {
        this.remainingRoundsForRespawn = remainingRoundsForRespawn;
        this.currentInfluence = currentInfluence;
        this.owningFaction = owningFaction;
        this.factoryID = factoryID;
    }

    public int getRemainingRoundsForRespawn() {
        return remainingRoundsForRespawn;
    }

    public int getCurrentInfluence() {
        return currentInfluence;
    }

    public Faction getOwningFaction() {
        return owningFaction;
    }

    public int getFactoryID() {
        return factoryID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TFactory)) return false;

        TFactory factory = (TFactory) o;

        if (currentInfluence != factory.currentInfluence) return false;
        if (factoryID != factory.factoryID) return false;
        if (remainingRoundsForRespawn != factory.remainingRoundsForRespawn) return false;
        if (owningFaction != factory.owningFaction) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = remainingRoundsForRespawn;
        result = 31 * result + currentInfluence;
        result = 31 * result + (owningFaction != null ? owningFaction.hashCode() : 0);
        result = 31 * result + factoryID;
        return result;
    }

    @Override
    public boolean isConsistent() {
        if (remainingRoundsForRespawn < 0 || factoryID < 0 || owningFaction == null) {
            return false;
        }

        return true;
    }
}
